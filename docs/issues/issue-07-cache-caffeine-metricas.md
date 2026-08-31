# [Issue 07] Camada de Cache (Caffeine/Redis) e Exposição de Métricas

## Contexto
Fórmulas e tabelas-verdade idênticas consultadas repetidamente não devem sobrecarregar o motor de computação ou a base de dados. Implementar cache no Quarkus e expor métricas de performance e taxa de acerto.

## Critérios de Aceite
- [ ] Configuração do Quarkus Cache (Caffeine para ambiente local / Redis para distribuído).
- [ ] Anotações `@CacheResult(cacheName = "tabelas-verdade")` e `@CacheResult(cacheName = "exercicios")` nos serviços correspondentes.
- [ ] Invalidação seletiva de cache (`@CacheInvalidate`) na criação/atualização de exercícios.
- [ ] Exposição de métricas de cache (hits, misses, evictions) via Micrometer em `/q/metrics` para coleta via Prometheus.

## Tasks Técnicas
- [ ] Adicionar extensão `quarkus-cache` e configurar TTL e tamanho máximo no `application.properties`.
- [ ] Adicionar anotações de cache nos métodos de busca de exercícios e cálculo de tabela-verdade.
- [ ] Configurar métricas do Micrometer para monitorar performance de chamadas.

## Testes Exigidos
- [ ] Teste de integração verificando que a segunda chamada com a mesma fórmula retorna o resultado cacheado sem reprocessamento.
- [ ] Teste de invalidação de cache após operação de `update`/`delete`.

## Labels / Estimativa
`~performance` `~cache` `~observability` · Estimativa: 5 pts · Prioridade: **P3** · Sprint: **Sprint 3**
