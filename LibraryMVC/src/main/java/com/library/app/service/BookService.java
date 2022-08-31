package com.library.app.service;

import com.library.app.model.Book;

import java.util.List;

public interface BookService {

    List<Book> getAllBooks();

    void saveBook(Book book);

    Book getBookById(long id);

    List<Book> getByHumanId(long humanId);

    void deleteById(long id);
}
