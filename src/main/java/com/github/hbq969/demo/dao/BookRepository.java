package com.github.hbq969.demo.dao;

import com.github.hbq969.demo.dao.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookRepository {

    void createBooks();

    // 根据书名包含指定字符串查询
    List<Book> findByTitleContaining(@Param("title") String title);

    // 根据作者查询
    List<Book> findByAuthor(@Param("author") String author);

    // 根据分类查询
    List<Book> findByCategory(@Param("category") String category);
}