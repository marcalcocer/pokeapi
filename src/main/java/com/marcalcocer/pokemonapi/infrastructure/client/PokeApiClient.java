package com.marcalcocer.pokemonapi.infrastructure.client;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.infrastructure.client.dto.PokeApiListResponse;
import com.marcalcocer.pokemonapi.infrastructure.client.dto.PokeApiPokemonDetailResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokeApiClient {

  private final WebClient webClient;

  private static final Duration API_TIMEOUT = Duration.ofSeconds(10);
  private static final int MAX_RETRIES = 2;

  public Flux<String> getAllPokemonNames() {
    try {
      return getPokemonPage(0)
          .expand(
              response -> {
                if (response.next() == null) {
                  return Mono.empty();
                }
                int nextOffset = extractOffset(response.next());
                return getPokemonPage(nextOffset);
              })
          .flatMapIterable(PokeApiListResponse::results)
          .map(PokeApiListResponse.PokemonSummary::name)
          .timeout(API_TIMEOUT)
          .retryWhen(Retry.backoff(MAX_RETRIES, Duration.ofSeconds(1)));
    } catch (Exception e) {
      log.error("Failed to get all pokemon names", e);
      throw e;
    }
  }

  private Mono<PokeApiListResponse> getPokemonPage(int offset) {
    log.debug("Fetching Pokémon page with offset: {}", offset);
    return webClient
        .get()
        .uri("/pokemon?limit=100&offset={offset}", offset)
        .retrieve()
        .bodyToMono(PokeApiListResponse.class)
        .timeout(Duration.ofSeconds(5))
        .retry(3);
  }

  // Process Pokémon details in controlled batches
  public Flux<Pokemon> getPokemonDetailsBatched(Flux<String> names) {
    return names
        .parallel(10)
        .runOn(Schedulers.boundedElastic())
        .flatMap(this::getPokemonDetailsWithRetry)
        .sequential();
  }

  private Mono<Pokemon> getPokemonDetailsWithRetry(String name) {
    return getPokemonDetails(name)
        .retryWhen(Retry.backoff(MAX_RETRIES, Duration.ofMillis(500)))
        .onErrorResume(
            e -> {
              log.debug("Skipping Pokémon {} after retries: {}", name, e.getMessage());
              return Mono.empty();
            });
  }

  private Mono<Pokemon> getPokemonDetails(String name) {
    log.trace("Fetching details for Pokémon: {}", name);
    return webClient
        .get()
        .uri("/pokemon/{name}", name)
        .retrieve()
        .bodyToMono(PokeApiPokemonDetailResponse.class)
        .map(
            dto -> {
              log.trace("Received details for Pokémon: {}", dto.toString());
              return new Pokemon(
                  dto.id(), dto.name(), dto.weight(), dto.height(), dto.baseExperience());
            });
  }

  private int extractOffset(String url) {
    // Extract offset from URL like "https://pokeapi.co/api/v2/pokemon?offset=100&limit=100"
    return Integer.parseInt(url.split("offset=")[1].split("&")[0]);
  }
}
