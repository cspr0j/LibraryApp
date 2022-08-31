package com.library.app.service;

import com.library.app.model.Human;
import com.library.app.repository.HumanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class HumanServiceImpl implements HumanService {

    @Autowired
    HumanRepository humanRepository;

    @Override
    public List<Human> getAllHumans() {
        return humanRepository.findAll();
    }

    @Override
    public void saveHuman(Human human) {
        this.humanRepository.save(human);
    }

    @Override
    public Human getHumanById(long id) {
        return humanRepository.findById(id)
                .orElseThrow(() -> {
                    throw new RuntimeException("Sorry you can't do this");
                });
    }

    @Override
    public void deleteById(long id) {
        this.humanRepository.deleteById(id);
    }
}
