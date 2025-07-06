package com.marcalcocer.pokemonapi.domain.port;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import reactor.core.publisher.Flux;

public interface PokemonRepository {
  Flux<Pokemon> findAll();
}
