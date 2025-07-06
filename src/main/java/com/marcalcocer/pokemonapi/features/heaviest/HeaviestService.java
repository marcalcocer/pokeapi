package com.marcalcocer.pokemonapi.features.heaviest;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.domain.port.PokemonRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeaviestService implements HeaviestFetcher {
  private final PokemonRepository repository;

  @Override
  public Mono<List<Pokemon>> getTop5Heaviest() {
    try {
      log.debug("Fetching top 5 heaviest Pokémon");
      return repository
          .findAll()
          .sort((a, b) -> Integer.compare(b.weight(), a.weight()))
          .take(5)
          .collectList();
    } catch (Exception e) {
      log.error("Error fetching top 5 heaviest Pokémon", e);
      return null;
    }
  }
}
