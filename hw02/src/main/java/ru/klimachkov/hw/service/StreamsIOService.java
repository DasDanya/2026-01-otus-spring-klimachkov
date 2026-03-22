package ru.klimachkov.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class StreamsIOService implements IOService {

    private static final int MAX_ATTEMPTS = 10;

    private final PrintStream printStream;

    private final Scanner scanner;

    @Autowired
    public StreamsIOService(@Value("#{T(System).out}") PrintStream printStream,
                            @Value("#{T(System).in}") InputStream inputStream) {

        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public void printLine(String line) {
        printStream.println(line);
    }

    @Override
    public void printFormattedLine(String line, Object... args) {
        printStream.printf(line + "%n", args);
    }

    @Override
    public String readString() {
        return scanner.nextLine();
    }

    @Override
    public String readStringWithPrompt(String prompt) {
        printLine(prompt);
        return readString();
    }

    @Override
    public int readIntForRange(int min, int max, String errorMessage) {
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            try {
                int intValue = Integer.parseInt(readString());
                if (intValue < min || intValue > max) {
                    throw new IllegalArgumentException();
                }
                return intValue;
            } catch (IllegalArgumentException e) {
                printLine(errorMessage);
            }
        }
        throw new IllegalArgumentException("Error during reading int value");
    }

    @Override
    public int readIntForRangeWithPrompt(int min, int max, String prompt, String errorMessage) {
        printLine(prompt);
        return readIntForRange(min, max, errorMessage);
    }
}
