package com.github.hbq969.demo.ctrl;

import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.demo.dao.entity.Book;
import com.github.hbq969.demo.model.ChatRequest;
import com.github.hbq969.demo.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Tag(name = "AI接口")
@RequestMapping(path = {"/ai", "/mcp-ui"})
@RestController
@Slf4j
public class ChatCtrl {

    private final ChatClient chatClient;
    private final BookService bookService;

    public ChatCtrl(ChatClient.Builder builder, BookService bookService) {
        logToolMethods(bookService.getClass());
        this.chatClient = builder.defaultTools(bookService).build();
        this.bookService = bookService;
    }

    @RequestMapping(path = "/chat", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<?> mcpChat(
            @RequestBody ChatRequest request) {
        try {
            String userMessage = request.getMessage();
            String content = chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();
            return ReturnMessage.success(content);
        } catch (Exception e) {
            log.error("", e);
            return ReturnMessage.fail("处理请求时出错: " + e.getMessage());
        }
    }

    @GetMapping(path = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> mcpChatSSE(@RequestParam(name = "message") String message) {
        String userMessage = message;

        return Flux.create(sink -> {
            try {
                chatClient.prompt()
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

    @RequestMapping(path = "/books", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<List<Book>> queryBooks() {
        return ReturnMessage.success(bookService.findBooksByAuthor(""));
    }

    private void logToolMethods(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Tool.class))
                .forEach(m -> log.info("Tool 方法注册成功: {}", m.getName()));
    }
}
