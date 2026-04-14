package ru.klimachkov.hw;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
public abstract class IgnoreShellTest {
}
