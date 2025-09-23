package com.github.hbq969.demo.service;


import com.github.hbq969.demo.model.ImportModel;
import org.springframework.ai.chat.client.ChatClient;

public interface KnowledgeService {

    void importKnowledge(
            ImportModel model);

    ChatClient getChatClient();
}
