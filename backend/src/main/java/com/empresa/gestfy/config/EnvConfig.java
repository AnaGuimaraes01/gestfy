package com.empresa.gestfy.config;

// PARA O JAVA LER O ARQUIVO .env
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        Map<String, Object> envMap = new HashMap<>();

        // Carregar todas as variáveis do .env
        dotenv.entries().forEach(entry -> {
            envMap.put(entry.getKey(), entry.getValue());
        });

        // VERIFICAÇÃO DE INTEGRIDADE
        if (!envMap.containsKey("DB_URL") || !envMap.containsKey("DB_USERNAME") || !envMap.containsKey("DB_PASSWORD")) {
            System.out.println("⚠️ AVISO: Arquivo .env não encontrado ou incompleto!");
            System.out.println("   As variáveis DB_URL, DB_USERNAME e DB_PASSWORD são necessárias.");
            System.out.println("   Verifique se o arquivo .env existe no diretório raiz do projeto.");
        }

        // Adicionar como property source de alta prioridade
        environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv", envMap));
    }
}
