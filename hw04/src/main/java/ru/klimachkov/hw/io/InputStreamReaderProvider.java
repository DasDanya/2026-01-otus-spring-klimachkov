package ru.klimachkov.hw.io;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class InputStreamReaderProvider implements ReaderProvider {

    private static final ClassLoader CLASS_LOADER = InputStreamReaderProvider.class.getClassLoader();

    @Override
    public Reader getReader(String resourceName)  {
        InputStream inputStream = CLASS_LOADER.getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceName);
        }

        return new InputStreamReader(inputStream);
    }
}
