package com.library.app.controller;

import com.library.app.model.Book;
import com.library.app.model.Human;
import com.library.app.service.BookService;
import com.library.app.service.HumanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private HumanService humanService;


    @GetMapping("/book")
    public String booksListPage(Model model) {
        List<Book> listBook = bookService.getAllBooks();
        model.addAttribute("listBook", listBook);
        model.addAttribute("totalItems", listBook.size());

        return "book_tmp/book";
    }

    @GetMapping("/book/add")
    public String addPage(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);

        Human human = new Human();
        model.addAttribute("person", human);

        List<Human> listHuman = humanService.getAllHumans();
        model.addAttribute("listHuman", listHuman);

        return "book_tmp/new_book";
    }

    @PostMapping("/book/save")
    public String savePage(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/book";
    }

    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return "redirect:/book";
    }

    @GetMapping("/book/update/{id}")
    public String updatePage(@PathVariable(value = "id") long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);

        Human human;
        if (book.getHumanId() != 0) {
            human = humanService.getHumanById(book.getHumanId());
        } else {
            human = new Human();
        }
        model.addAttribute("person", human);

        List<Human> listHuman = humanService.getAllHumans();
        model.addAttribute("listHuman", listHuman);

        return "book_tmp/update_book";
    }
}
