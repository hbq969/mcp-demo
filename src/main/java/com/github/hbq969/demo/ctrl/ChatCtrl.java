package com.github.hbq969.demo.ctrl;

import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.demo.model.ImportModel;
import com.github.hbq969.demo.service.KnowledgeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.mariadb.MariaDBVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Tag(name = "AI接口")
@RequestMapping(path = {"/ai", "/mcp-ui"})
@RestController
@Slf4j
public class ChatCtrl {

    @Autowired
    private MariaDBVectorStore vectorStore;

    @Autowired
    @Qualifier("mcp-chatClient")
    private ChatClient mcpChatClient;

    @Autowired
    private KnowledgeService knowledgeService;

    @GetMapping(path = "/mcp/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> mcpChat(@RequestParam(name = "message") String message) {
        String userMessage = message;

        return Flux.create(sink -> {
            try {
                mcpChatClient.prompt()
                        .user(userMessage)
                        .stream()
                        .chatResponse().doOnNext(response -> {
                            String chunk = response.getResult().getOutput().getText();
                            if (chunk != null && !chunk.isEmpty()) {
                                sink.next(chunk);
                            }
                        })
                        .doOnComplete(() -> {
                            sink.complete();
                        })
                        .doOnError(error -> {
                            sink.error(error);
                        })
                        .subscribe();
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

    @GetMapping(path = "/rag/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> ragChat(@RequestParam(name = "message") String message) {
        String userMessage = message;

        return Flux.create(sink -> {
            try {
                knowledgeService.getChatClient().prompt()
                        .user(userMessage)
                        .advisors(new QuestionAnswerAdvisor(vectorStore))
                        .stream()
                        .chatResponse().doOnNext(response -> {
                            String chunk = response.getResult().getOutput().getText();
                            if (chunk != null && !chunk.isEmpty()) {
                                sink.next(chunk);
                            }
                        })
                        .doOnComplete(() -> {
                            sink.complete();
                        })
                        .doOnError(error -> {
                            sink.error(error);
                        })
                        .subscribe();
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

    @RequestMapping(path = "/knowledge/import", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ReturnMessage<?> importKnowledge(@RequestParam("defaultSystem") String defaultSystem,
                                            @RequestParam("files") MultipartFile[] files) {
        ImportModel model = ImportModel.builder()
                .files(files).defaultSystem(defaultSystem)
                .build();
        knowledgeService.importKnowledge(model);
        return ReturnMessage.success("导入成功");
    }

    private void logToolMethods(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Tool.class))
                .forEach(m -> log.info("Tool 方法注册成功: {}", m.getName()));
    }
}
