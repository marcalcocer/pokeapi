package com.marcalcocer.pokemonapi.features.mostexperienced;

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
public class MostExperiencedService implements MostExperiencedFetcher {
  private final PokemonRepository repository;

  @Override
  public Mono<List<Pokemon>> getTop5MostExperienced() {
    try {
      log.debug("Fetching top 5 most experienced Pokémon");
      return repository
          .findAll()
          .sort((a, b) -> Integer.compare(b.baseExperience(), a.baseExperience()))
          .take(5)
          .collectList()
          .doOnError(e -> log.error("Error fetching top 5 most experienced Pokémon", e));
    } catch (Exception e) {
      log.error("Error fetching top 5 most experienced Pokémon", e);
      return null;
    }
  }
}
