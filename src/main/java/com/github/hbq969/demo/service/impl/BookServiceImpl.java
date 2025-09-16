package com.github.hbq969.demo.service.impl;

import com.github.hbq969.code.common.initial.AbstractScriptInitialAware;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.spring.i18n.LanguageEvent;
import com.github.hbq969.code.common.utils.InitScriptUtils;
import com.github.hbq969.code.common.utils.ThrowUtils;
import com.github.hbq969.demo.dao.BookRepository;
import com.github.hbq969.demo.dao.entity.Book;
import com.github.hbq969.demo.service.BookService;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BookServiceImpl extends AbstractScriptInitialAware implements BookService {
    @Resource
    private BookRepository bookRepository;
    @Autowired
    private SpringContext context;

    @Override
    protected void tableCreate0() {
        ThrowUtils.call("books表创建成功", "books表已存在", () -> bookRepository.createBooks());
    }

    @Override
    protected void scriptInitial0() {
        InitScriptUtils.initial(context, "mcp.sql", StandardCharsets.UTF_8, null, null);
    }

    @Override
    public String nameOfScriptInitialAware() {
        return "mcp";
    }

    @Override
    public void onApplicationEvent(LanguageEvent event) {

    }

    @Override
    @Tool(name = "findBooksByTitle", description = "根据书名模糊查询图书，支持部分标题匹配")
    public List<Book> findBooksByTitle(@ToolParam(description = "书名关键词") String title) {
        return bookRepository.findByTitleContaining(title);
    }

    @Override
    @Tool(name = "findBooksByAuthor", description = "根据作者精确查询图书")
    public List<Book> findBooksByAuthor(@ToolParam(description = "作者姓名") String author) {
        return bookRepository.findByAuthor(author);
    }

    @Override
    @Tool(name = "findBooksByCategory", description = "根据图书分类精确查询图书")
    public List<Book> findBooksByCategory(@ToolParam(description = "图书分类") String category) {
        return bookRepository.findByCategory(category);
    }
}