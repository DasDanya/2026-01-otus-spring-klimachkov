package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;

import java.io.PrintStream;

@RequiredArgsConstructor
public class StreamsIOService implements IOService {

    private final PrintStream printStream;

    @Override
    public void printLine(String line) {
        printStream.println(line);
    }

    @Override
    public void printFormattedLine(String line, Object... args) {
        printStream.printf(line + "%n", args);
    }
}
