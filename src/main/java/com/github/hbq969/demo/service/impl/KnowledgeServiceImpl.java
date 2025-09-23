package com.github.hbq969.demo.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.hbq969.demo.dao.RagRepository;
import com.github.hbq969.demo.model.ImportModel;
import com.github.hbq969.demo.model.ReaderModel;
import com.github.hbq969.demo.service.KnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.mariadb.MariaDBVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private MariaDBVectorStore vectorStore;

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private RagRepository ragRepository;

    @Autowired
    private ChatClient.Builder builder;

    private volatile ChatClient chatClient;

    @Override
    public void importKnowledge(ImportModel model) {
        ragRepository.clearVectorStore();
        this.chatClient = builder.defaultSystem(model.getDefaultSystem()).build();
        MultipartFile[] files = model.getFiles();
        log.debug("提交的文件数量= {}", ArrayUtil.length(files));
        if (ArrayUtil.isEmpty(files))
            return;
        for (MultipartFile file : files) {
            importFile(file);
        }
    }

    @Override
    public ChatClient getChatClient() {
        return this.chatClient;
    }

    private void importFile(MultipartFile file) {
        try {
            String suffix = FileUtil.getSuffix(file.getOriginalFilename());
            log.debug("导入文件信息, 文件名: {}, 后缀: {}", file.getOriginalFilename(), suffix);
            List<Document> documents = null;
            if (StrUtil.equals("pdf", suffix)) {
                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(file.getResource(),
                        PdfDocumentReaderConfig.builder()
                                .withPageTopMargin(0)
                                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build())
                                .withPagesPerDocument(1)
                                .build());
                documents = pdfReader.read();
            } else if (StrUtil.equals("txt", suffix)) {
                TextReader txtReader = new TextReader(file.getResource());
                txtReader.setCharset(StandardCharsets.UTF_8);
                documents = txtReader.read();
            }
            if (documents == null)
                return;
            // 2.转换，令牌文本拆分器
            TokenTextSplitter splitter = new TokenTextSplitter(512, 128, 128, 50, true);
            List<Document> splitDocuments = splitter.apply(documents);
            splitDocuments.forEach(doc -> {
                log.debug("Chunk: {}", doc.getFormattedContent());
                log.debug("Metadata: {}", doc.getMetadata());
            });
            // 2.转换，关键字元数据丰富器
//        KeywordMetadataEnricher enricher = KeywordMetadataEnricher.builder(chatModel)
//                .keywordCount(5)
//                .build();
//        List<Document> enricherDocuments = enricher.apply(splitDocuments);

            // 3. 存向量库
            vectorStore.add(splitDocuments);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
