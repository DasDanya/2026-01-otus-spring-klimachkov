package ru.klimachkov.hw.exceptions;

public class MissingHeaderException extends RuntimeException {

    public MissingHeaderException(String headerName) {
        super("Required header is missing: " + headerName);
    }
}
