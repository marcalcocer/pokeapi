package com.marcalcocer.pokemonapi.infrastructure.adapter.out.cache;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.out.PokemonRepository;
import com.marcalcocer.pokemonapi.infrastructure.adapter.out.persistence.PokeApiAdapter;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class RedisPokemonAdapter implements PokemonRepository {
  private static final String CACHE_KEY_PREFIX = "pokemon:";
  private static final Duration TTL = Duration.ofHours(12);

  private final ReactiveRedisTemplate<String, Pokemon> redisTemplate;
  private final PokeApiAdapter fallback;

  @Override
  public Flux<Pokemon> findAll() {
    log.debug("Attempting to fetch Pokémon from Redis cache");
    return redisTemplate
        .keys(CACHE_KEY_PREFIX + "*")
        .flatMap(key -> redisTemplate.opsForValue().get(key))
        .doOnNext(p -> log.trace("Found cached Pokémon: {}", p.name()))
        .switchIfEmpty(
            Flux.defer(
                () -> {
                  log.info("No Pokémon found in cache, falling back to PokeAPI");
                  return fetchAndCacheAll();
                }))
        .onErrorResume(
            e -> {
              log.warn("Redis error, falling back to PokeAPI: {}", e.getMessage());
              return fetchAndCacheAll();
            });
  }

  private Flux<Pokemon> fetchAndCacheAll() {
    return fallback
        .findAll()
        .flatMap(
            pokemon -> {
              String key = CACHE_KEY_PREFIX + pokemon.name().toLowerCase();
              return redisTemplate
                  .opsForValue()
                  .set(key, pokemon, TTL)
                  .thenReturn(pokemon)
                  .doOnNext(p -> log.trace("Cached new Pokémon: {}", p.name()));
            });
  }
}
