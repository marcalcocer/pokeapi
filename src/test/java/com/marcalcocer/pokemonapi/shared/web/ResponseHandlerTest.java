package com.marcalcocer.pokemonapi.shared.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ResponseHandlerTest {
  @Spy @InjectMocks private ResponseHandler responseHandler;

  @Test
  public void testCreatePokemonResponse_ShouldReturnInternalError_WhenNull() {
    var result = responseHandler.createPokemonResponse(null);

    StepVerifier.create(result)
        .assertNext(
            response -> {
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
              assertNotNull(response.getBody());
              assertEquals(0, response.getBody().size());
            })
        .verifyComplete();
  }

  @Test
  void testCreatePokemonResponse_ShouldReturnOk_WhenDataExists() {
    var pokemons =
        List.of(new Pokemon(1, "Bulbasaur", 69, 7, 64), new Pokemon(2, "Ivysaur", 130, 10, 142));

    var result = responseHandler.createPokemonResponse(Mono.just(pokemons));

    StepVerifier.create(result)
        .assertNext(
            response -> {
              assertEquals(HttpStatus.OK, response.getStatusCode());
              assertNotNull(response.getBody());
              assertEquals(2, response.getBody().size());
            })
        .verifyComplete();
  }

  @Test
  void testCreatePokemonResponse_ShouldReturnNoContent_WhenEmpty() {
    var result = responseHandler.createPokemonResponse(Mono.just(List.of()));

    StepVerifier.create(result)
        .assertNext(
            response -> {
              assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
              assertNotNull(response.getBody());
              assertEquals(0, response.getBody().size());
            })
        .verifyComplete();
  }

  @Test
  void testCreatePokemonResponse_ShouldReturnInternalError_OnFailure() {
    var result = responseHandler.createPokemonResponse(Mono.error(new RuntimeException("test")));

    StepVerifier.create(result)
        .assertNext(
            response -> {
              assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
              assertNotNull(response.getBody());
              assertEquals(0, response.getBody().size());
            })
        .verifyComplete();
  }
}
