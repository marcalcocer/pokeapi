# Proyecto PokéAPI

Este proyecto implementa una API RESTful en Java con Spring Boot para obtener información específica de Pokémon,
consumiendo datos de la PokéAPI (https://pokeapi.co/api/v2/).

## 🌟 Visión General

La aplicación expone tres endpoints principales para satisfacer los siguientes escenarios:

1.  Los 5 Pokémon más pesados.

2.  Los 5 Pokémon más altos.

3.  Los 5 Pokémon con más experiencia base.

Además de las funcionalidades principales, el proyecto está diseñado con una arquitectura robusta y considera aspectos
clave para su preparación y despliegue en entornos de producción.

## 🏗️ Arquitectura

La aplicación sigue una **Arquitectura Hexagonal (Ports and Adapters)** combinada con un enfoque de **Vertical Sliding**
para la organización del código.

### Principios Clave:

* **Dominio en el Centro:** El dominio del negocio (`Pokemon.java`, `PokemonRepository.java`) es el corazón de la
  aplicación, completamente independiente de las tecnologías externas.

* **Separación de Preocupaciones:** Cada "slice" vertical representa una característica de negocio (e.g., `heaviest`,
  `highest`, `mostexperienced`), conteniendo todos los componentes necesarios (controlador, servicio, fetcher) para esa
  funcionalidad.
* **Inversión de Dependencias:** El dominio define las interfaces (puertos) y la infraestructura implementa esos puertos
  (adaptadores), asegurando que el código de negocio no dependa de los detalles de implementación de la infraestructura.

### Estructura de Paquetes (Vertical Sliding):

* `com.marcalcocer.pokemonapi`: Paquete raíz.
    * `features`: Contiene los "slices" verticales para cada funcionalidad principal.
        * `highest`: Lógica para los Pokémon más altos.
            * `HighestController.java`: Expone el endpoint.
            * `HighestService.java`: Contiene la lógica de negocio.
            * `HighestFetcher.java`: Interfaz que define el contrato.
        * `heaviest`: Lógica para los Pokémon más pesados.
        * `mostexperienced`: Lógica para los Pokémon con más experiencia base.
    * `shared`: Contiene componentes comunes o compartidos entre las características.
        * `domain.model.Pokemon.java`: El modelo de dominio principal.
        * `domain.port.PokemonRepository.java`: Interfaz (puerto) para la interacción con datos.
        * `infrastructure.client.PokeApiClient.java`: Adaptador para interactuar con la API externa de Pokémon
          (PokeAPI).
        * `infrastructure.client.dto`: DTOs para mapear las respuestas de la PokeAPI.
        * `infrastructure.cache.RedisPokemonAdapter.java`: Adaptador (implementación del puerto `PokemonRepository`) que
          proporciona una capa de caché con Redis. Es la implementación *primaria* de `PokemonRepository`.
        * `infrastructure.persistance.PokeApiAdapter.java`: Adaptador (implementación del puerto `PokemonRepository`)
          que obtiene los datos directamente de la `PokeApiClient`. Actúa como *fallback* si Redis no está disponible o
          falla.
        * `shared.config.PokemonConfig.java`: Clases de configuración de Spring para `WebClient` y `Redis`.
        * `shared.web.ResponseHandler.java`: Componente compartido para estandarizar el manejo de respuestas HTTP y
          errores.

Este diseño permite una alta cohesión dentro de las características y un bajo acoplamiento entre ellas y con la
infraestructura, facilitando la mantenibilidad y la escalabilidad.

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java
* **Framework:** Spring Boot (versión 3.2.3)
* **Programación Reactiva:** Project Reactor (WebFlux)
* **Inyección de Dependencias:** Lombok (`@RequiredArgsConstructor`, `@Slf4j`)
* **Cliente HTTP:** Spring WebClient
* **Base de Datos NoSQL / Caché:** Redis (a través de Spring Data Reactive Redis)
* **Contenedorización:** Docker, Docker Compose
* **Testing:** JUnit 5, Mockito, Reactor Test (StepVerifier)

## 🚀 Funcionalidades Implementadas

### Endpoints API

Los endpoints están disponibles bajo el path `/api/pokemons`:

* **GET /api/pokemons/heaviest**: Devuelve una lista de los 5 Pokémon más pesados.
* **GET /api/pokemons/highest**: Devuelve una lista de los 5 Pokémon más altos.
* **GET /api/pokemons/most-experienced**: Devuelve una lista de los 5 Pokémon con más experiencia base.

### Caché con Redis

Aunque no era un requisito explícito, se ha implementado una capa de caché con Redis (`RedisPokemonAdapter`). Esta capa
actúa como el `PokemonRepository` primario (`@Primary`) para todas las operaciones de lectura, mejorando
significativamente el rendimiento al reducir las llamadas repetidas a la PokéAPI externa.

* **Estrategia:** Cache-aside. Se intenta obtener los Pokémon desde Redis; si no están presentes (cache miss) o si Redis
  falla, se recurre a la `PokeApiAdapter` (el `fallback`).

* **TTL (Time-To-Live):** Los datos en caché expiran después de 12 horas para asegurar la frescura de los datos sin
  sobrecargar la API externa.

### Manejo de Errores y Respuestas

Se utiliza un `ResponseHandler` compartido para estandarizar las respuestas HTTP. Esto incluye:

* `200 OK` para respuestas exitosas con datos.
* `204 No Content` cuando no hay datos disponibles (ej. la API externa devuelve una lista vacía).
* `500 Internal Server Error` para cualquier excepción no controlada durante el procesamiento.

### Resiliencia y Rendimiento con `PokeApiClient`

El `PokeApiClient` está diseñado para ser robusto:

* **Paginación interna:** Maneja la paginación de la PokéAPI utilizando `expand()` para obtener todos los nombres de
  Pokémon de manera eficiente.
* **Paralelización:** Las llamadas para obtener detalles de Pokémon se realizan en paralelo (10 concurrencias) para
  mejorar la velocidad.
* **Reintentos:** Implementa una estrategia de reintentos con *backoff* exponencial para manejar fallos temporales en la
  comunicación con la PokéAPI.
* **Filtrado de datos:** Los Pokémon con `weight` igual a 0 son filtrados, ya que se consideran datos inválidos para el
  contexto de "más pesados".

## 🚀 Uso Local
Para levantar la aplicación y sus servicios de soporte (Redis, Prometheus, Grafana) en tu máquina local, sigue estos pasos:

1. Asegúrate de tener [Docker y Docker Compose](https://docs.docker.com/compose/install/) instalados.

2. Navega a la raíz del proyecto en tu terminal.

3. Ejecuta el siguiente comando para construir y levantar los contenedores en segundo plano: 

  ```bash
  docker-compose up --build -d
  ```
Una vez que los contenedores estén levantados y el `pokemon-api` esté listo (puede tardar un poco en iniciarse Spring Boot), podrás acceder a los endpoints.

### Endpoints Locales

Puedes probar los siguientes endpoints utilizando `localhost` y el puerto 8080:

* **5 Pokémon más pesados:** `http://localhost:8080/api/pokemons/heaviest`

* **5 Pokémon más altos:** `http://localhost:8080/api/pokemons/highest`

* **5 Pokémon con más experiencia base:** `http://localhost:8080/api/pokemons/most-experienced` 

### Ejecución de Tests Unitarios

Para ejecutar solo los tests unitarios del proyecto:

```bash
./gradlew test
```


## 💭 Decisiones de Diseño / Reflexiones

* **Uso de Spring WebFlux y `WebClient`:** Se optó por Spring WebFlux y su cliente `WebClient` en lugar de un enfoque
  bloqueante como `RestTemplate`. La principal razón es aprovechar la **programación reactiva** para manejar un gran
  volumen de solicitudes concurrentes de manera eficiente, sin bloquear hilos. Esto es crucial cuando se interactúa con
  APIs externas (como PokeAPI), ya que permite que la aplicación siga siendo responsiva y escalable incluso si el
  servicio externo tarda en responder, mejorando el uso de recursos y la latencia.
* **Patrones de Diseño:** Se han aplicado patrones como el `Repository` para la abstracción de la capa de datos,
  `Service` para encapsular la lógica de negocio, y el patrón Adaptador para integrar servicios externos (PokeAPI,
  Redis).
* **Ventajas de la Arquitectura Hexagonal:** Este diseño promueve un código más limpio, modular y fácil de testear, ya
  que el dominio no tiene dependencias directas de implementaciones específicas de infraestructura o UI.

## 🧪 Tests

Se ha puesto un gran énfasis en la calidad del código a través de los tests.

* **Cobertura de Tests:** La aplicación mantiene una cobertura de tests superior al 90%. Esto indica que la mayor parte
  del código está cubierta por pruebas, aunque la priorización principal fue alcanzar este porcentaje. Esto significa
  que, si bien se asegura que la mayoría de las líneas de código se ejecutan, algunos casos de borde o flujos más
  específicos podrían requerir una granularidad de tests unitarios adicional.
* **Tests Unitarios:** Se han implementado tests unitarios exhaustivos para los servicios y adaptadores, utilizando
  Mockito para simular dependencias y `StepVerifier` de Reactor Test para probar flujos reactivos.

## ⚙️ Preparación para Producción

El proyecto está configurado con varios aspectos que lo hacen "production ready", aunque siempre hay margen de mejora y
consideraciones específicas para entornos de producción y prueba.

### Aspectos "Production Ready" Implementados:

* **Contenedorización con Docker:** La aplicación y sus dependencias (Redis) están definidas en `docker-compose.yml`, lo
  que facilita su despliegue consistente en cualquier entorno.
* **Salud del Servicio:** El `docker-compose.yml` incluye `healthcheck` para Redis, asegurando que la aplicación no
  intente conectarse a un servicio no disponible.
* **Configuración Externa:** El uso de `application.yml` y variables de entorno (`SPRING_REDIS_HOST` en
  `docker-compose.yml`) permite configurar la aplicación sin necesidad de recompilarla, adaptándose a diferentes
  entornos (desarrollo, QA, producción).

### Consideraciones de Despliegue en Producción (AWS con Docker)

Para un despliegue en producción utilizando AWS y Docker, se podría seguir un enfoque como este:

* **Amazon Elastic Container Service (ECS) o Amazon Elastic Kubernetes Service (EKS):** Estos servicios permitirían
  orquestar y escalar los contenedores Docker de la aplicación.
* **Amazon EC2 / Fargate:** Los contenedores se ejecutarían en instancias EC2 (control total) o Fargate (sin servidor,
  mayor abstracción de la infraestructura subyacente).
* **Amazon ElastiCache (para Redis):** Para Redis, en lugar del contenedor Docker local, se usaría un servicio
  gestionado como Amazon ElastiCache, que ofrece alta disponibilidad, escalabilidad y respaldo.
* **Amazon ECR (Elastic Container Registry):** Para almacenar las imágenes Docker de la aplicación de manera segura y
  eficiente.
* **Load Balancer (ALB/NLB):** Para distribuir el tráfico entre las instancias de la aplicación y asegurar la alta
  disponibilidad.
* **VPC (Virtual Private Cloud):** Para un entorno de red aislado y seguro.

## 🚦 Puntos a Mejorar / Trabajo Pendiente

* **Tests de Integración:** Aún falta implementar los tests de integración. La idea es usar `docker-compose` para
  levantar un entorno aislado que incluya la aplicación y Redis, y simular la PokeAPI con un `MockWebServer` ejecutado
  durante el test. Esto permitiría controlar las respuestas de la API externa y probar la interacción entre los
  componentes de la aplicación de manera aislada y reproducible en un entorno cercano a la producción.
* **Monitorización con Prometheus y Grafana:** Se ha incluido su configuración en el archivo `docker-compose.yml` para
  la recolección de métricas. Sin embargo, la instrumentación completa y el envío activo de métricas desde la aplicación
  hacia estas herramientas aún no se ha implementado por completo. Esto sería un siguiente paso crucial para tener una
  observabilidad real en un entorno de producción.
* **Manejo de Errores Más Granular:** Actualmente, `ResponseHandler` devuelve un `500 Internal Server Error` genérico.
  Podría mapearse a errores HTTP más específicos (ej. `404 Not Found` si un Pokémon no existe, `400 Bad Request` para
  entradas inválidas, etc.) para proporcionar una mejor información al cliente de la API.
* **Circuit Breaker:** Implementar un patrón Circuit Breaker (ej. con Resilience4j) para proteger la aplicación de
  fallos persistentes de la PokéAPI, evitando que las fallas en el servicio externo afecten toda la aplicación.
* **Seguridad:** Implementar autenticación y autorización (ej. Spring Security con JWT/OAuth2), validación de entrada de
  datos más robusta.
* **Logging Avanzado:** Configurar un sistema de logging centralizado (ej. ELK Stack o CloudWatch Logs) para una mejor
  depuración y auditoría en producción.
* **Gestión de Secretos:** Utilizar servicios de gestión de secretos (ej. AWS Secrets Manager) para credenciales
  sensibles, en lugar de pasarlos directamente como variables de entorno o en archivos de configuración.
* **Pruebas de Carga y Rendimiento:** Realizar pruebas exhaustivas de carga para asegurar que la aplicación escala
  correctamente y cumple con los SLA.
* **Alertas:** Configurar alertas en el sistema de monitorización (Prometheus/Grafana) para notificaciones proactivas
  sobre problemas en producción.
* **CI/CD:** Establecer un pipeline de Integración Continua y Despliegue Continuo para automatizar el build, test y
  despliegue.