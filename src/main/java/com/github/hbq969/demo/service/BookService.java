package com.github.hbq969.demo.service;

import com.github.hbq969.demo.dao.entity.Book;

import java.util.List;

public interface BookService {
    // 根据标题查询
    List<Book> findBooksByTitle(String title);

    // 根据作者查询
    List<Book> findBooksByAuthor(String author);

    // 根据分类查询
    List<Book> findBooksByCategory(String category);
}
