package com.marcalcocer.pokemonapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final String BASE_URL = "https://pokeapi.co/api/v2";

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(BASE_URL).build();
    }
}
