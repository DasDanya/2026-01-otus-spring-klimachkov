package ru.klimachkov.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import ru.klimachkov.hw.config.AppProperties;
import ru.klimachkov.hw.service.TestRunnerService;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        TestRunnerService testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }
}
