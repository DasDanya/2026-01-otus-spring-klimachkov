package ru.klimachkov.hw.batch.exceptions;

public class MigratedIdNotFoundException extends RuntimeException {

    public MigratedIdNotFoundException(String message) {
        super(message);
    }
}
