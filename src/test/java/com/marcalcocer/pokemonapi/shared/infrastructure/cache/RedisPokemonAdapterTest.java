package com.marcalcocer.pokemonapi.shared.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.infrastructure.persistance.PokeApiAdapter;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RedisPokemonAdapterTest {
  @Mock private ReactiveRedisTemplate<String, Pokemon> redisTemplate;
  @Mock private ReactiveValueOperations<String, Pokemon> valueOperations;
  @Mock private PokeApiAdapter pokeApiAdapter;

  @InjectMocks private RedisPokemonAdapter redisPokemonAdapter;

  private Object[] allMocks() {
    return new Object[] {redisTemplate, pokeApiAdapter, valueOperations};
  }

  @Test
  void testFindAll_ShouldReturnFromCache() {
    when(redisTemplate.keys(anyString())).thenReturn(Flux.just("pokemon:pikachu"));
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get(anyString()))
        .thenReturn(Mono.just(new Pokemon(25, "Pikachu", 60, 4, 112)));

    var result = redisPokemonAdapter.findAll();

    StepVerifier.create(result).expectNextMatches(p -> p.name().equals("Pikachu")).verifyComplete();

    verify(redisTemplate).keys(eq("pokemon:*"));
    verify(valueOperations).get(eq("pokemon:pikachu"));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testFindAll_ShouldFallbackToPokeAPI_WhenCacheEmpty() {
    when(redisTemplate.keys(anyString())).thenReturn(Flux.empty());
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    var charizard = new Pokemon(6, "Charizard", 905, 17, 240);
    when(pokeApiAdapter.findAll()).thenReturn(Flux.just(charizard));
    when(valueOperations.set(anyString(), any(Pokemon.class), any(Duration.class)))
        .thenReturn(Mono.just(true));

    var result = redisPokemonAdapter.findAll();

    StepVerifier.create(result).expectNext(charizard).verifyComplete();

    verify(redisTemplate).keys(eq("pokemon:*"));
    verify(pokeApiAdapter).findAll();
    verify(valueOperations).set(eq("pokemon:charizard"), eq(charizard), eq(Duration.ofHours(12)));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testFindAll_ShouldFallbackToPokeAPI_WhenCacheFails() {
    when(redisTemplate.keys(anyString()))
        .thenReturn(Flux.error(new RuntimeException("Cache error")));
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    var bulbasaur = new Pokemon(1, "Bulbasaur", 69, 7, 64);
    when(pokeApiAdapter.findAll()).thenReturn(Flux.just(bulbasaur));
    when(valueOperations.set(anyString(), any(Pokemon.class), any(Duration.class)))
        .thenReturn(Mono.just(true));

    var result = redisPokemonAdapter.findAll();

    StepVerifier.create(result).expectNext(bulbasaur).verifyComplete();

    verify(redisTemplate).keys(eq("pokemon:*"));
    verify(pokeApiAdapter).findAll();
    verify(valueOperations).set(eq("pokemon:bulbasaur"), eq(bulbasaur), eq(Duration.ofHours(12)));

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testFindAll_ShouldCachePokemon_WhenFallingBackToPokeAPI() {
    when(redisTemplate.keys(anyString())).thenReturn(Flux.empty());
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    var squirtle = new Pokemon(7, "Squirtle", 90, 5, 63);
    when(pokeApiAdapter.findAll()).thenReturn(Flux.just(squirtle));
    when(valueOperations.set(anyString(), any(Pokemon.class), any(Duration.class)))
        .thenReturn(Mono.just(true));

    var result = redisPokemonAdapter.findAll();

    StepVerifier.create(result.collectList())
        .assertNext(
            pokemons -> {
              assertEquals(1, pokemons.size());
              assertEquals("Squirtle", pokemons.getFirst().name());
            })
        .verifyComplete();

    verify(valueOperations).set(eq("pokemon:squirtle"), eq(squirtle), eq(Duration.ofHours(12)));

    verifyNoMoreInteractions(allMocks());
  }
}
