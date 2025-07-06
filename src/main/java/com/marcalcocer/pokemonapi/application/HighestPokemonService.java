package com.marcalcocer.pokemonapi.application;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.in.HighestPokemonFetcher;
import com.marcalcocer.pokemonapi.domain.port.out.PokemonRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HighestPokemonService implements HighestPokemonFetcher {

  private final PokemonRepository repository;

  @Override
  public Mono<List<Pokemon>> getTop5Highest() {
    try {
      log.debug("Fetching top 5 highest Pokémon");
      return repository
          .findAll()
          .sort((a, b) -> Integer.compare(b.height(), a.height()))
          .take(5)
          .collectList()
          .doOnError(e -> log.error("Error fetching top 5 highest Pokémon", e));
    } catch (Exception e) {
      log.error("Error fetching top 5 highest Pokémon", e);
      return null;
    }
  }
}
