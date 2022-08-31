package com.library.app.controller;

import com.library.app.model.Book;
import com.library.app.model.Human;
import com.library.app.service.BookService;
import com.library.app.service.HumanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/human")
public class HumanControllerApi {

    @Autowired
    private HumanService humanService;

    @Autowired
    private BookService bookService;


    @GetMapping("/")
    @Operation(description = "List of all Humans")
    public List<Human> humanList() {
        return humanService.getAllHumans();
    }

    @GetMapping("/info/{id}")
    public Human humanInfo(@PathVariable("id") long id) {
        return humanService.getHumanById(id);
    }

    @PostMapping("/add")
    public Human addHuman(@RequestBody Human human) {
        humanService.saveHuman(human);
        return human;
    }

    @DeleteMapping("/delete/{id}")
    @Operation(description = "Note: Deletion of person, who owns a library book isn't allowed.")
    public String deleteHuman(@PathVariable("id") long id) {

        Human human = humanService.getHumanById(id);
        StringBuilder result = new StringBuilder();
        List<Book> books = bookService.getByHumanId(human.getHumanId());

        // ONLY when a person does not have any books
        // (isn't the owner of any books)
        if (books.size() == 0) {
            humanService.deleteById(id);
            result.append(" - Successfully deleted!");
        } else {
            result.append(" - You cannot delete this person" +
                    ", because this person owns a library book. Try again after he returns the book!");
        }
        return human.toString() + result;
    }

    @PutMapping("/update/{id}")
    public Human updateHuman(@PathVariable("id") Long id, @RequestBody Human human) {
        Human humanById = humanService.getHumanById(id);
        human.setHumanId(humanById.getHumanId());
        humanService.saveHuman(human);
        return human;
    }
}
