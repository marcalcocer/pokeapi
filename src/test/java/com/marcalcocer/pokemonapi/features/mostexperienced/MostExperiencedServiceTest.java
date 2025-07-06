package com.marcalcocer.pokemonapi.features.mostexperienced;

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
public class MostExperiencedServiceTest {
  @Mock private PokemonRepository mockRepository;

  @InjectMocks private MostExperiencedService service;

  private Object[] allMocks() {
    return new Object[] {mockRepository};
  }

  @Test
  void testGetTop5MostExperienced_ShouldReturnTop5Pokemons() {
    when(mockRepository.findAll())
        .thenReturn(
            Flux.just(
                new Pokemon(1, "Bulbasaur", 69, 7, 64),
                new Pokemon(2, "Ivysaur", 130, 10, 142),
                new Pokemon(3, "Venusaur", 1000, 20, 236),
                new Pokemon(4, "Charmander", 85, 6, 62),
                new Pokemon(5, "Charmeleon", 190, 11, 142),
                new Pokemon(6, "Charizard", 905, 17, 240)));

    var result = service.getTop5MostExperienced();
    var pokemons = result.block();

    assertNotNull(pokemons);
    assertEquals(5, pokemons.size());

    assertEquals(240, pokemons.get(0).baseExperience());
    assertEquals(236, pokemons.get(1).baseExperience());
    assertEquals(142, pokemons.get(2).baseExperience());
    assertEquals(142, pokemons.get(3).baseExperience());
    assertEquals(64, pokemons.get(4).baseExperience());

    verify(mockRepository).findAll();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testGetTop5MostExperienced_ShouldReturnNull_WhenRepositoryThrows() {
    when(mockRepository.findAll()).thenThrow(new RuntimeException("test"));

    var result = service.getTop5MostExperienced();

    assertNull(result);

    verify(mockRepository).findAll();

    verifyNoMoreInteractions(allMocks());
  }
}
