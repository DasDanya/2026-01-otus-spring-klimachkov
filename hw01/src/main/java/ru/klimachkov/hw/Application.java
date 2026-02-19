package ru.klimachkov.hw;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.klimachkov.hw.service.TestRunnerService;

public class Application {

    private static final String APPLICATION_CONTEXT_CLASS_PATH = "classpath:ru/klimachkov/hw/application-context.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CLASS_PATH);
        TestRunnerService testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }
}
