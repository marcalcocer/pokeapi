package com.marcalcocer.pokemonapi.features.heaviest;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.web.ResponseHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pokemons")
@RequiredArgsConstructor
@Slf4j
public class HeaviestController {
  private final HeaviestFetcher fetcher;
  private final ResponseHandler responseHandler;

  @GetMapping("/heaviest")
  public Mono<ResponseEntity<List<Pokemon>>> getTop5Heaviest() {
    log.info("Heaviest Pokémon request received");
    return responseHandler.createPokemonResponse(fetcher.getTop5Heaviest());
  }
}
