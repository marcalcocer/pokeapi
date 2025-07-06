package com.marcalcocer.pokemonapi.shared.domain.port;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import reactor.core.publisher.Flux;

public interface PokemonRepository {
  Flux<Pokemon> findAll();
}
