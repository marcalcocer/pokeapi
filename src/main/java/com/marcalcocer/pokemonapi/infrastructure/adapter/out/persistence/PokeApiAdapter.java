package com.marcalcocer.pokemonapi.infrastructure.adapter.out.persistence;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.out.PokemonRepository;
import com.marcalcocer.pokemonapi.infrastructure.client.PokeApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PokeApiAdapter implements PokemonRepository {
  private final PokeApiClient apiClient;

  @Override
  public Flux<Pokemon> findAll() {
    log.debug("Fetching all Pokémon from PokeAPI");
    return apiClient
        .getAllPokemonNames()
        .transform(apiClient::getPokemonDetailsBatched)
        .filter(p -> p.weight() > 0);
  }
}
