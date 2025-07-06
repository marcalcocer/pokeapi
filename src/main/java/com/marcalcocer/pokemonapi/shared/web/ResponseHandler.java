package com.marcalcocer.pokemonapi.shared.web;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ResponseHandler {

  public Mono<ResponseEntity<List<Pokemon>>> createPokemonResponse(Mono<List<Pokemon>> result) {
    return result
        .<ResponseEntity<List<Pokemon>>>map(
            pokemons -> {
              if (pokemons == null || pokemons.isEmpty()) {
                log.warn("No Pokémon data available");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
              }
              log.info("Returning {} Pokémon response", pokemons.size());
              return ResponseEntity.ok(pokemons);
            })
        .onErrorResume(
            e -> {
              log.error("Error processing request: {}", e.getMessage());
              return Mono.just(
                  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                      .body(Collections.emptyList()));
            });
  }
}
