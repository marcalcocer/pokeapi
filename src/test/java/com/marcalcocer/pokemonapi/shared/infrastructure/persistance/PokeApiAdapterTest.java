package com.marcalcocer.pokemonapi.shared.infrastructure.persistance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.infrastructure.client.PokeApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PokeApiAdapterTest {
  @Mock private PokeApiClient apiClient;

  @InjectMocks private PokeApiAdapter pokeApiAdapter;

  @Test
  void testFindAll_ShouldReturnFilteredPokemons() {
    var pokemon1 = new Pokemon(1, "Bulbasaur", 69, 7, 64);
    var pokemon2 = new Pokemon(2, "Ivysaur", 130, 10, 142);
    var invalidPokemon = new Pokemon(3, "Ghost", 0, 0, 0);

    when(apiClient.getAllPokemonNames()).thenReturn(Flux.just("Bulbasaur", "Ivysaur", "Ghost"));
    when(apiClient.getPokemonDetailsBatched(any()))
        .thenReturn(Flux.just(pokemon1, pokemon2, invalidPokemon));

    var result = pokeApiAdapter.findAll();

    StepVerifier.create(result).expectNext(pokemon1).expectNext(pokemon2).verifyComplete();

    verify(apiClient).getAllPokemonNames();
    verify(apiClient).getPokemonDetailsBatched(any());

    verifyNoMoreInteractions(apiClient);
  }

  @Test
  void testFindAll_ShouldReturnEmpty_WhenNoValidPokemons() {
    var invalidPokemon = new Pokemon(3, "Ghost", 0, 0, 0);

    when(apiClient.getAllPokemonNames()).thenReturn(Flux.just("Ghost"));
    when(apiClient.getPokemonDetailsBatched(any())).thenReturn(Flux.just(invalidPokemon));

    var result = pokeApiAdapter.findAll();

    StepVerifier.create(result).verifyComplete();

    verify(apiClient).getAllPokemonNames();
    verify(apiClient).getPokemonDetailsBatched(any());

    verifyNoMoreInteractions(apiClient);
  }
}
