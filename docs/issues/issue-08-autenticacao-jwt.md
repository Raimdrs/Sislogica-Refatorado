# [Issue 08] Autenticação e Perfis com JWT

## Contexto
Permitir a autenticação segura de usuários e administradores via JSON Web Tokens (JWT), protegendo endpoints administrativos de cadastro e associando o histórico de submissões ao perfil do estudante autenticado.

## Critérios de Aceite
- [ ] Endpoints de autenticação em `/api/v1/auth`:
  - `POST /api/v1/auth/register`: Registro de novos estudantes.
  - `POST /api/v1/auth/login`: Autenticação e emissão de token JWT assinado.
- [ ] Controle de acesso baseado em papéis (RBAC): `@RolesAllowed({"ADMIN"})` para gerenciamento de exercícios e `@RolesAllowed({"STUDENT", "ADMIN"})` para histórico individual.
- [ ] Persistência de senha com hash seguro (Bcrypt / Argon2).
- [ ] Integração com SmallRye JWT no Quarkus.

## Tasks Técnicas
- [ ] Adicionar extensão `quarkus-smallrye-jwt`.
- [ ] Modelar entidade `Usuario` (`id`, `nome`, `email`, `senhaHash`, `perfil`).
- [ ] Implementar gerador e validador de tokens JWT (chaves pública/privada).
- [ ] Proteger recursos no `ExercicioResource` e criar `UsuarioResource`.

## Testes Exigidos
- [ ] Teste de login com credenciais válidas e inválidas.
- [ ] Teste de acesso negado (401 Unauthorized / 403 Forbidden) para usuários sem o papel necessário.

## Labels / Estimativa
`~security` `~auth` `~jwt` · Estimativa: 8 pts · Prioridade: **P2** · Sprint: **Bloco Final**
