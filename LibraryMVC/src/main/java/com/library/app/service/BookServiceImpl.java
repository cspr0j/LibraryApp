package com.library.app.service;

import com.library.app.model.Book;
import com.library.app.repository.BookRepository;
import com.library.app.repository.HumanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository bookRepository;

    @Autowired
    HumanRepository humanRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public void saveBook(Book book) {
        this.bookRepository.save(book);
    }

    @Override
    public Book getBookById(long id) {

        return bookRepository.findById(id)
                .orElseThrow(() -> {
                    throw new RuntimeException("Sorry you can't do this");
                });
    }

    @Override
    public List<Book> getByHumanId(long humanId) {
        List<Book> bookList = bookRepository.findAll();
        bookList.removeIf(book -> book.getHumanId() != humanId || book.getHumanId() == 0);

        return bookList;
    }

    @Override
    public void deleteById(long id) {
        this.bookRepository.deleteById(id);
    }
}
