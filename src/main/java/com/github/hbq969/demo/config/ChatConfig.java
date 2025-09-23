package com.github.hbq969.demo.config;

import com.github.hbq969.code.common.datasource.DynamicDataSource;
import com.github.hbq969.demo.service.BookService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.mariadb.MariaDBVectorStore;
import org.springframework.ai.vectorstore.mariadb.autoconfigure.MariaDbStoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ChatConfig {

    @Autowired
    private MariaDbStoreProperties mariaDbStoreProperties;
    @Autowired
    private OllamaChatModel ollamaChatModel;


    @Bean
    @Qualifier("mcp-chatClient")
    ChatClient mcpChatClient(BookService bookService) {
        ChatClient client = ChatClient.builder(ollamaChatModel).defaultTools(bookService).build();
        return client;
    }

    @Bean
    @Primary
    MariaDBVectorStore vectorStore(DataSource dataSource, EmbeddingModel embeddingModel) {

        DynamicDataSource dds;
        if (dataSource instanceof DynamicDataSource) dds = (DynamicDataSource) dataSource;
        else throw new UnsupportedOperationException("请开启动态数据源spring.datasource.dynamic.enabled.");
        JdbcTemplate jt = new JdbcTemplate(dds.getResolvedDataSources().get("mariadb"));

        boolean initializeSchema = mariaDbStoreProperties.isInitializeSchema();
        MariaDBVectorStore.MariaDBDistanceType distanceType = mariaDbStoreProperties.getDistanceType();
        int dimensions = mariaDbStoreProperties.getDimensions();

        MariaDBVectorStore mariaDBVectorStore = MariaDBVectorStore.builder(jt, embeddingModel).initializeSchema(initializeSchema).distanceType(distanceType).dimensions(dimensions).build();

        mariaDBVectorStore.afterPropertiesSet();

        return mariaDBVectorStore;
    }

    public static void main(String[] args) throws Exception {
        OllamaApi api = OllamaApi.builder().baseUrl("http://localhost:11434").build();
        OllamaOptions options = OllamaOptions.builder().model("nomic-embed-text").build();
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .ollamaApi(api)
                .defaultOptions(options)
                .build();
        Document doc = Document.builder()
                .text("产品说明书:产品名称：智能机器人\n" +
                        "产品描述：智能机器人是一个智能设备，能够自动完成各种任务。\n" +
                        "功能：\n" +
                        "1. 自动导航：机器人能够自动导航到指定位置。\n" +
                        "2. 自动抓取：机器人能够自动抓取物品。\n" +
                        "3. 自动放置：机器人能够自动放置物品。\n")
                .build();
        float[] embedding = embeddingModel.embed(doc.getText());
        System.out.println(embedding.length);
    }
}
