package com.empresa.gestfy.config;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DataHoraBrasil {

    private static final ZoneId BRASIL = ZoneId.of("America/Sao_Paulo");

    public static LocalDateTime agora() {
        return LocalDateTime.now(BRASIL);
    }
}
