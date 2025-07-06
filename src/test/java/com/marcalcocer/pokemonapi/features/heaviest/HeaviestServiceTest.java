package com.marcalcocer.pokemonapi.features.heaviest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.domain.port.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
public class HeaviestServiceTest {
  @Mock private PokemonRepository mockRepository;

  @InjectMocks private HeaviestService service;

  private Object[] allMocks() {
    return new Object[] {mockRepository};
  }

  @Test
  void testGetTop5Heaviest_ShouldReturnTop5HeaviestPokemons() {
    when(mockRepository.findAll())
        .thenReturn(
            Flux.just(
                new Pokemon(1, "Bulbasaur", 69, 7, 64),
                new Pokemon(2, "Ivysaur", 130, 10, 142),
                new Pokemon(3, "Venusaur", 1000, 20, 236),
                new Pokemon(4, "Charmander", 85, 6, 62),
                new Pokemon(5, "Charmeleon", 190, 11, 142),
                new Pokemon(6, "Charizard", 905, 17, 240)));

    var result = service.getTop5Heaviest();
    var pokemons = result.block();

    assertNotNull(pokemons);
    assertEquals(5, pokemons.size());

    assertEquals(1000, pokemons.get(0).weight());
    assertEquals(905, pokemons.get(1).weight());
    assertEquals(190, pokemons.get(2).weight());
    assertEquals(130, pokemons.get(3).weight());
    assertEquals(85, pokemons.get(4).weight());

    verify(mockRepository).findAll();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  public void testGetTop5Heaviest_ShouldReturnNull_WhenRepositoryThrows() {
    when(mockRepository.findAll()).thenThrow(new RuntimeException("test"));

    var result = service.getTop5Heaviest();

    assertNull(result);

    verify(mockRepository).findAll();

    verifyNoMoreInteractions(allMocks());
  }
}
