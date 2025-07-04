package com.marcalcocer.pokemonapi.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pokemon {
  private String name;
  private int weight;
  private int height;
  private int baseExperience;
}
