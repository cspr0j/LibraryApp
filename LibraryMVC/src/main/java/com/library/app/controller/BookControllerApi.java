package com.library.app.controller;

import com.library.app.model.Book;
import com.library.app.model.Human;
import com.library.app.service.BookService;
import com.library.app.service.HumanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
public class BookControllerApi {
    @Autowired
    private BookService bookService;

    @Autowired
    private HumanService humanService;


    @GetMapping("")
    @Operation(description = "List of all Books")
    public List<Book> booksList() {
        return bookService.getAllBooks();
    }

    @PostMapping("/add")
    @Operation(description = "Note: the book ID is generated automatically," +
            "so you don't need to write this in the Request Body." +
            "Also the person ID will be set to 0 if there is no such person in the database")
    public Book addBook(@RequestBody Book book) {

        List<Long> list = humanService.getAllHumans()
                .stream()
                .map(Human::getHumanId)
                .filter(num -> Objects.equals(book.getHumanId(), num))
                .collect(Collectors.toList());

        if (list.size() == 0) {
            book.setHumanId(0L);
        }
        bookService.saveBook(book);
        return book;
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") final Long id) {
        Book book = bookService.getBookById(id);
        bookService.deleteById(id);

        return book.toString() + " - Successfully deleted!";
    }

    @PutMapping("/update/{id}")
    @Operation(description = "Note: the bookId automatically takes the value that you entered in the field ID," +
            "so there is no need to specify it in the Request Body." +
            "Also the person ID will be set to 0 if there is no such person in the database")
    public Book updateBook(
            @PathVariable("id") final Long id, @RequestBody Book book) {

        book.setBookId(id);

        List<Long> list = humanService.getAllHumans()
                .stream()
                .map(Human::getHumanId)
                .filter(num -> Objects.equals(book.getHumanId(), num))
                .collect(Collectors.toList());

        if (list.size() == 0) {
            book.setHumanId(0L);
        }
        bookService.saveBook(book);
        return book;
    }
}
