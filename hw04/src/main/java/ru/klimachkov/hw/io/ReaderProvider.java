package ru.klimachkov.hw.io;

import java.io.Reader;

public interface ReaderProvider {

    Reader getReader(String resourceName);
}
