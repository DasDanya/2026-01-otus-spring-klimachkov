package ru.klimachkov.hw.service;

public interface LocalizedMessagesService {
    String getMessage(String code, Object... args);
}
