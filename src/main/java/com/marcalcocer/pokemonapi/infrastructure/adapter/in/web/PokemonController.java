package com.marcalcocer.pokemonapi.infrastructure.adapter.in.web;

import static java.util.Collections.emptyList;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.in.HeaviestPokemonFetcher;
import com.marcalcocer.pokemonapi.domain.port.in.HighestPokemonFetcher;
import com.marcalcocer.pokemonapi.domain.port.in.MostExperiencedPokemonFetcher;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pokemons")
@RequiredArgsConstructor
@Slf4j
public class PokemonController {

  private final HeaviestPokemonFetcher heaviestPokemonFetcher;
  private final HighestPokemonFetcher highestPokemonFetcher;
  private final MostExperiencedPokemonFetcher mostExperiencedPokemonFetcher;

  @GetMapping("/heaviest")
  public Mono<ResponseEntity<List<Pokemon>>> getTop5Heaviest() {
    log.info("Heaviest Pokémon request received");
    Mono<List<Pokemon>> result = heaviestPokemonFetcher.getTop5Heaviest();
    return createResponse(result);
  }

  @GetMapping("/highest")
  public Mono<ResponseEntity<List<Pokemon>>> getTop5Highest() {
    log.info("Highest Pokémon request received");
    return createResponse(highestPokemonFetcher.getTop5Highest());
  }

  @GetMapping("/most-experienced")
  public Mono<ResponseEntity<List<Pokemon>>> getTop5MostExperienced() {
    log.info("Most experienced Pokémon request received");
    return createResponse(mostExperiencedPokemonFetcher.getTop5MostExperienced());
  }

  private Mono<ResponseEntity<List<Pokemon>>> createResponse(Mono<List<Pokemon>> result) {
    return result
        .map(
            pokemons -> {
              if (pokemons == null || pokemons.isEmpty()) {
                log.warn("No Pokémon data available");
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Collections.<Pokemon>emptyList());
              }
              log.info("Returning {} Pokémon response", pokemons.size());
              return ResponseEntity.ok(pokemons);
            })
        .onErrorResume(
            e -> {
              log.error("Error processing request: {}", e.getMessage());
              return Mono.just(
                  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList()));
            });
  }
}
