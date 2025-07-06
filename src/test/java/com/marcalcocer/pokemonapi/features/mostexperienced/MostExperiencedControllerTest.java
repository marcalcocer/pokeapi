package com.marcalcocer.pokemonapi.features.mostexperienced;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.shared.web.ResponseHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MostExperiencedControllerTest {
  @Mock private MostExperiencedFetcher mockFetcher;
  @Mock private ResponseHandler mockResponseHandler;

  @InjectMocks private MostExperiencedController controller;

  private Object[] allMocks() {
    return new Object[] {mockFetcher, mockResponseHandler};
  }

  @Test
  void testGetTop5Heaviest_ShouldReturnResponseEntity() {
    var pokemonList =
        List.of(
            new Pokemon(1, "Bulbasaur", 69, 7, 64),
            new Pokemon(2, "Ivysaur", 130, 10, 142),
            new Pokemon(3, "Venusaur", 1000, 20, 236),
            new Pokemon(4, "Charmander", 85, 6, 62),
            new Pokemon(5, "Charmeleon", 190, 11, 142));
    var monoPokemonList = Mono.just(pokemonList);

    when(mockFetcher.getTop5MostExperienced()).thenReturn(monoPokemonList);
    when(mockResponseHandler.createPokemonResponse(any()))
        .thenReturn(Mono.just(ResponseEntity.ok(pokemonList)));

    var result = controller.getTop5MostExperienced();

    assertEquals(ResponseEntity.ok(pokemonList), result.block());

    verify(mockFetcher).getTop5MostExperienced();
    verify(mockResponseHandler).createPokemonResponse(eq(monoPokemonList));

    verifyNoMoreInteractions(allMocks());
  }
}
