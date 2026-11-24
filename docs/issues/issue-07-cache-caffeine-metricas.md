# [Issue 07] Camada de Cache (Caffeine/Redis) e Exposição de Métricas

## Contexto
Fórmulas e tabelas-verdade idênticas consultadas repetidamente não devem sobrecarregar o motor de computação ou a base de dados. Implementar cache no Quarkus e expor métricas de performance e taxa de acerto.

## Critérios de Aceite
- [x] Configuração do Quarkus Cache (Caffeine para ambiente local / Redis para distribuído).
- [x] Anotações `@CacheResult(cacheName = "silogismos")` e `@CacheResult(cacheName = "turmas")` nos serviços correspondentes (`SilogismoService` e `TurmaService`).
- [x] Invalidação seletiva de cache (`@CacheInvalidate` com `@CacheKey` e `@CacheInvalidateAll`) na atualização e remoção de turmas e silogismos.
- [x] Exposição de métricas de cache (hits, misses, evictions, puts) via Micrometer em `/q/metrics` para coleta via Prometheus.

## Tasks Técnicas
- [x] Adicionar extensão `quarkus-cache` e configurar TTL (`expire-after-write`) e tamanho máximo (`maximum-size`) no `application.properties`.
- [x] Adicionar anotações de cache nos métodos de busca e escrita de turmas e silogismos.
- [x] Configurar métricas do Micrometer (`quarkus-micrometer-registry-prometheus`) para monitorar performance de chamadas.

## Testes Exigidos
- [x] Teste de integração verificando que a segunda chamada com o mesmo ID retorna o resultado cacheado sem reprocessamento (`CacheMetricasTest`).
- [x] Teste de invalidação seletiva de cache após operação de `update`/`delete` e exposição em `/q/metrics` (`CacheMetricasTest`).

## Labels / Estimativa
`~performance` `~cache` `~observability` · Estimativa: 5 pts · Prioridade: **P3** · Sprint: **Sprint 3**
