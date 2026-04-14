package ru.klimachkov.hw.shell;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.command.CommandRegistration;
import ru.klimachkov.hw.domain.Student;
import ru.klimachkov.hw.service.LocalizedIOService;
import ru.klimachkov.hw.service.ResultService;
import ru.klimachkov.hw.service.TestRunnerService;

@Configuration
public class TestCommandsConfig {

    private static final String COMMANDS_GROUP = "Test commands";

    @Bean
    CommandRegistration runCommand(TestRunnerService testRunnerService, LocalizedIOService localizedIOService) {
        return CommandRegistration.builder()
                .command("run")
                .group(COMMANDS_GROUP)
                .description(localizedIOService.getMessage("TestCommands.run.description"))
                .withTarget()
                    .consumer(context -> testRunnerService.run())
                .and()
                .build();
    }

    @Bean
    CommandRegistration showResultsCommand(ResultService resultService, LocalizedIOService localizedIOService) {
        return CommandRegistration.builder()
                .command("results")
                .group(COMMANDS_GROUP)
                .description(localizedIOService.getMessage("TestCommands.results.description"))
                .withOption()
                    .shortNames('f')
                    .type(String.class)
                    .required()
                    .description(localizedIOService.getMessage("TestCommands.results.firstName.description"))
                .and()
                .withOption()
                    .shortNames('l')
                    .type(String.class)
                    .required()
                    .description(localizedIOService.getMessage("TestCommands.results.lastName.description"))
                .and()
                .withTarget()
                    .consumer(context -> resultService.showResults(
                            new Student(context.getOptionValue("f"),
                                    context.getOptionValue("l")))
                    )
                .and()
                .build();
    }
}
