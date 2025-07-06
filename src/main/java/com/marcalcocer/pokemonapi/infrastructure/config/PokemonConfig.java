package com.marcalcocer.pokemonapi.infrastructure.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class PokemonConfig {
  private static final String BASE_URL = "https://pokeapi.co/api/v2";

  @Bean
  public WebClient webClient() {
    HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(10));

    return WebClient.builder()
        .baseUrl(BASE_URL)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(
            configurer ->
                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
        .build();
  }
}
