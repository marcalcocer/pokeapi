package com.marcalcocer.pokemonapi.infrastructure.config;

import static java.time.Duration.ofSeconds;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class PokemonConfig {
  private static final String BASE_URL = "https://pokeapi.co/api/v2";

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Bean
  public WebClient webClient() {
    HttpClient httpClient = HttpClient.create().responseTimeout(ofSeconds(10));

    return WebClient.builder()
        .baseUrl(BASE_URL)
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(
            configurer ->
                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
        .build();
  }

  @Bean
  @Primary
  public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    return new LettuceConnectionFactory(redisHost, redisPort);
  }

  @Bean
  public ReactiveRedisTemplate<String, Pokemon> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory factory) {
    RedisSerializationContext<String, Pokemon> context =
        RedisSerializationContext.<String, Pokemon>newSerializationContext(RedisSerializer.string())
            .value(new Jackson2JsonRedisSerializer<>(Pokemon.class))
            .build();

    return new ReactiveRedisTemplate<>(factory, context);
  }
}
