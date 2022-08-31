package com.library.app.service;

import com.library.app.model.Human;

import java.util.List;


public interface HumanService {
    List<Human> getAllHumans();

    void saveHuman(Human human);

    Human getHumanById(long id);

    void deleteById(long id);
}
