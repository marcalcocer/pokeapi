package com.marcalcocer.pokemonapi.application;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.in.HeaviestPokemonFetcher;
import com.marcalcocer.pokemonapi.domain.port.out.PokemonRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeaviestPokemonService implements HeaviestPokemonFetcher {

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
