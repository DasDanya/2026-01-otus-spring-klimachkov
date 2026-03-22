package ru.klimachkov.hw.service;

public interface IOService {

    void printLine(String line);

    void printFormattedLine(String line, Object... args);

    String readString();

    String readStringWithPrompt(String prompt);

    int readIntForRange(int min, int max, String errorMessage);

    int readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage);
}
