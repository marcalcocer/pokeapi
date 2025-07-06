package com.marcalcocer.pokemonapi.shared.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.marcalcocer.pokemonapi.shared.infrastructure.client.dto.PokeApiListResponse;
import com.marcalcocer.pokemonapi.shared.infrastructure.client.dto.PokeApiListResponse.PokemonSummary;
import com.marcalcocer.pokemonapi.shared.infrastructure.client.dto.PokeApiPokemonDetailResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PokeApiClientTest {
  @Mock private WebClient webClient;
  @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock private WebClient.ResponseSpec responseSpec;

  @InjectMocks private PokeApiClient pokeApiClient;

  private Object[] allMocks() {
    return new Object[] {webClient, requestHeadersUriSpec, responseSpec};
  }

  @Test
  public void testGetAllPokemonNames_ShouldThrowExceptionOnError() {
    Exception exception = new RuntimeException("test");
    when(webClient.get()).thenThrow(exception);

    Exception result =
        assertThrows(RuntimeException.class, () -> pokeApiClient.getAllPokemonNames());

    assertEquals(exception, result);

    verify(webClient).get();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testGetAllPokemonNames_ShouldReturnNames() {
    var firstPage =
        new PokeApiListResponse(
            List.of(new PokemonSummary("bulbasaur", "url1")),
            "https://pokeapi.co/api/v2/pokemon?offset=100&limit=100");
    var secondPage = new PokeApiListResponse(List.of(new PokemonSummary("ivysaur", "url2")), null);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(eq("/pokemon?limit=100&offset={offset}"), eq(0)))
        .thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(eq("/pokemon?limit=100&offset={offset}"), eq(100)))
        .thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PokeApiListResponse.class))
        .thenReturn(Mono.just(firstPage))
        .thenReturn(Mono.just(secondPage));

    var result = pokeApiClient.getAllPokemonNames();

    StepVerifier.create(result).expectNext("bulbasaur").expectNext("ivysaur").verifyComplete();

    verify(webClient, times(2)).get();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testGetPokemonDetailsBatched_ShouldReturnPokemons() {
    var mockResponse = new PokeApiPokemonDetailResponse(1, "bulbasaur", 69, 7, 64);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(eq("/pokemon/{name}"), eq("bulbasaur")))
        .thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PokeApiPokemonDetailResponse.class))
        .thenReturn(Mono.just(mockResponse));

    var result = pokeApiClient.getPokemonDetailsBatched(Flux.just("bulbasaur"));

    StepVerifier.create(result)
        .expectNextMatches(p -> p.id() == 1 && p.name().equals("bulbasaur") && p.weight() == 69)
        .verifyComplete();

    verify(webClient).get();

    verifyNoMoreInteractions(allMocks());
  }

  @Test
  void testGetPokemonDetailsBatched_ShouldHandleErrors() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(eq("/pokemon/{name}"), eq("missing")))
        .thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PokeApiPokemonDetailResponse.class))
        .thenReturn(Mono.error(new RuntimeException("Not found")));

    var result = pokeApiClient.getPokemonDetailsBatched(Flux.just("missing"));

    StepVerifier.create(result).verifyComplete();

    verify(webClient).get();

    verifyNoMoreInteractions(allMocks());
  }
}
