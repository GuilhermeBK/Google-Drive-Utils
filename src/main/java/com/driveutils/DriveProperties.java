package com.driveutils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties("drive")
public class DriveProperties {

    private String caminho;
    private String caminhoToken;

}
