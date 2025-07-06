package com.marcalcocer.pokemonapi.shared.infrastructure.persistance;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.domain.port.PokemonRepository;
import com.marcalcocer.pokemonapi.shared.infrastructure.client.PokeApiClient;
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
