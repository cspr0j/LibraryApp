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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HumanController {

    @Autowired
    private HumanService humanService;

    @Autowired
    private BookService bookService;

    @GetMapping("/")
    public String homePage() {
        return "redirect:/human";
    }

    @GetMapping("/human")
    public String humanListPage(Model model) {
        List<Human> listHuman = humanService.getAllHumans();
        model.addAttribute("listHuman", listHuman);
        model.addAttribute("totalItems", listHuman.size());

        List<Long> list = new ArrayList<>();
        bookService.getAllBooks()
                .forEach(book -> {
                    listHuman
                            .stream()
                            .map(Human::getHumanId)
                            .filter(humanId -> humanId.equals(book.getHumanId()))
                            .forEach(list::add);
                });

        List<Long> bookHumanId = list.stream().distinct()
                .sorted().collect(Collectors.toList());

        model.addAttribute("bookHumanId", bookHumanId);
        return "human_tmp/human";
    }

    @GetMapping("/human/info/{id}")
    public String humanInfoPage(@PathVariable("id") long id, Model model) {
        Human person = humanService.getHumanById(id);
        model.addAttribute("person", person);

        Book book = new Book();
        model.addAttribute("book", book);

        List<Book> listBook = bookService.getByHumanId(id);
        model.addAttribute("listBook", listBook);

        return "human_tmp/info";
    }

    @GetMapping("/human/add")
    public String addPage(Model model) {
        Human human = new Human();
        model.addAttribute("human", human);

        return "human_tmp/new_human";
    }

    @PostMapping("/human/save")
    public String savePage(@ModelAttribute("human") Human human) {
        humanService.saveHuman(human);
        return "redirect:/human";
    }

    @GetMapping("/human/delete/{id}")
    public String deletePage(@PathVariable("id") long id) {

        // deletion is available
        // ONLY when a person does not have any books
        // (isn't the owner of any books)
        humanService.deleteById(id);

        return "redirect:/human";
    }

    @GetMapping("/human/update/{id}")
    public String updatePage(@PathVariable(value = "id") long id, Model model) {
        Human human = humanService.getHumanById(id);
        model.addAttribute("human", human);
        return "human_tmp/update_human";
    }
}
