package com.driveutils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@Slf4j
public class UtilMessageError {

    private UtilMessageError() {
    }

    @Bean
    protected static void printMessageError(Throwable e) {
        String message = Optional.of(e)
                .map(Throwable::getCause)
                .map(Throwable::getCause)
                .map(cause -> cause.getCause().getMessage())
                .orElse(e.getMessage());
        log.error("Mensagem de Erro: {}", message, e);
    }
}