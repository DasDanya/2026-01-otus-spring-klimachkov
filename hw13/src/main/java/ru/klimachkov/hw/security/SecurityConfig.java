package ru.klimachkov.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.klimachkov.hw.handlers.GlobalAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GlobalAccessDeniedHandler globalAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(
                                        "/books/add",
                                        "/books/{id}/edit",
                                        "/books/{id}/delete").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(globalAccessDeniedHandler))
                .formLogin(formLogin -> formLogin.defaultSuccessUrl("/", true))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
