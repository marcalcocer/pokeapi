package com.marcalcocer.pokemonapi.domain.port;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import java.util.Optional;

public interface PokemonCachePort {
  Optional<Pokemon> get(String name);

  void save(Pokemon pokemon);

  void clear();
}
