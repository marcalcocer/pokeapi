package com.marcalcocer.pokemonapi.infrastructure.pokeapi;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto.PokeApiListResponse;
import com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto.PokeApiPokemonDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class PokeApiClient {

  private final WebClient webClient;

  public Flux<String> getAllPokemonNames() {
    try {
      return webClient
          .get()
          .uri("/pokemon?limit=50")
          .retrieve()
          .bodyToMono(PokeApiListResponse.class)
          .flatMapIterable(
              resp ->
                  resp.results().stream().map(PokeApiListResponse.PokemonSummary::name).toList())
          .onErrorResume(
              e -> {
                log.error("Failed to fetch Pokémon names", e);
                return Flux.error(new RuntimeException("PokéAPI unavailable"));
              });

    } catch (Exception e) {
      log.error("Failed to get all pokemon names", e);
      throw e;
    }
  }

  public Mono<Pokemon> getPokemonDetails(String name) {
    try {
      return webClient
          .get()
          .uri("/pokemon/{name}", name)
          .retrieve()
          .bodyToMono(PokeApiPokemonDetailResponse.class)
          .map(dto -> new Pokemon(dto.name(), dto.weight(), dto.height()))
          .onErrorResume(
              e -> {
                log.warn("Failed to fetch details for {}", name, e);
                return Mono.empty();
              });
    } catch (Exception e) {
      log.warn("Failed to fetch details for pokemon {}", name, e);
      return Mono.empty();
    }
  }
}
