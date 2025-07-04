package com.marcalcocer.pokemonapi.infrastructure.cache;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.PokemonCachePort;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryPokemonCache implements PokemonCachePort {

  private final Map<String, Pokemon> cache = new ConcurrentHashMap<>();

  @Override
  public Optional<Pokemon> get(String name) {
    return Optional.ofNullable(cache.get(name));
  }

  @Override
  public void save(Pokemon pokemon) {
    cache.put(pokemon.getName().toLowerCase(), pokemon);
  }

  @Override
  public void clear() {
    cache.clear();
  }
}
