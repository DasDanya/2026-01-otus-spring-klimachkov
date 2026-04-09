package ru.klimachkov.hw.dao;

import ru.klimachkov.hw.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> findAll();
}
