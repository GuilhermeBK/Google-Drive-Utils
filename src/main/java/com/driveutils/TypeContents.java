package com.driveutils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;


/*
 *
 * Enum feito para representar os tipos de envio suportados pelo google cloud:
 * Jeito correto de chamar o conteudo do enum:
 * Type.PDF.getType
 *
 * */
@Getter
@AllArgsConstructor
@Component
public enum TypeContents {

    JPEG("image/jpeg"),
    PDF("application/pdf"),
    PNG("image/PNG"),
    GIF("image/gif"),
    BMP("image/BMP"),
    ZIP("application/zip"),
    RAR("application/zip"),
    HTML("text/html"),
    CSV("text/csv"),
    JSON("application/vnd.google-apps.script+json");

    private final String type;

}
