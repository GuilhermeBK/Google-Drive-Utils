package com.driveutils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Component
public class Path {

    private static String path;

    @Bean
    public static void setPath(String path) {
        Path.path = path;
    }

    @Bean
    public static String getPath() {
        return path;
    }
}
