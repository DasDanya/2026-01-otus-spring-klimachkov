package ru.klimachkov.hw.service;

public interface IOService {

    void printLine(String line);

    void printFormattedLine(String line, Object... args);
}
