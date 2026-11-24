## Atividade 01 - Alteração de Senha

O método `PATCH /api/v1/usuarios/{id}` troca a senha de um usuário. O corpo da requisição deixou de usar o DTO de cadastro e passou a utilizar o `UsuarioSenhaDTO`, contendo os campos:

- `senhaAtual`
- `novaSenha`
- `confirmaSenha`

**Comportamento:**
- Em caso de sucesso, devolve status `204 No Content` sem corpo (anteriormente retornava `200` com os dados do usuário).
- A validação de formato dos campos (não nulos/vazios e com tamanho exato de 6 caracteres) é feita no `UsuarioSenhaDTO` e retorna `422 Unprocessable Content` se inválida.
- A comparação entre a senha atual informada e a senha salva no banco, bem como a conferência entre a nova senha e a confirmação, é realizada no `UsuarioService`. Caso não confiram, é lançada a exceção `PasswordInvalidException`, tratada pelo `ApiExceptionHandler` retornando status `400 Bad Request`.

---

## Atividade 02 - Testes de Alteração de Senha

Foram implementados testes de integração em `UsuarioIT` para validar os 4 cenários da funcionalidade:

1. **Sucesso:** alteração com dados válidos retorna `204 No Content`.
2. **Senha atual inválida:** quando a senha atual não confere com a cadastrada, retorna `400 Bad Request`.
3. **Senhas divergentes:** quando a nova senha e a confirmação não coincidem, retorna `400 Bad Request`.
4. **Campos inválidos:** requisições com senhas vazias ou com tamanho menor/maior que 6 caracteres retornam status `422 Unprocessable Content`.

---

## Atividade 03 - Recurso Cliente

Um cliente só pode ser cadastrado se tiver um `usuário` associado (ou seja, deve ter um usuário cadastrado e autenticado).

**Recurso `Cliente`:**
- `id` (Long)
- `nome` (String)
- `cpf` (String) - deve ser único e válido
- Campos de auditoria (data de criação, data de modificação, criado por, modificado por)

**Regras e Funcionalidades:**
- O cadastro só pode ser realizado se o usuário estiver autenticado.
- O cliente pode consultar apenas os seus próprios dados.
- O Admin pode consultar os dados de todos os clientes.

**Classes criadas:**
- Classes de DTO (`ClienteCreateDto`, `ClienteResponseDto`, `ClienteMapper`)
- Controller (`ClienteController`)
- Service (`ClienteService`)
- Repository (`ClienteRepository`)

**Requisitos:**
- Proteção de rotas com autenticação e autorização.
- Validação dos campos de entrada.
- Recurso documentado com Swagger e OpenAPI.

---

## Atividade 04 - Recurso Bicicleta

Gerenciamento de bicicletas do sistema, acessível apenas por administradores.

**Recurso `Bicicleta`:**
- `id` (Long)
- `codigo` (String) — único
- `status` (Enum: `LIVRE`, `OCUPADA`, `MANUTENCAO`)
- Campos de auditoria (data de criação, data de modificação, criado por, modificado por)

**Endpoints (`/api/v1/bicicletas`):**
- `POST /` — cadastra uma bicicleta
- `GET /{codigo}` — busca pelo código
- `GET /` — lista todas
- `PUT /{codigo}` — atualiza dados
- `DELETE /{codigo}` — remove

**Classes criadas:**
- `Bicicleta` (entity), `BicicletaStatus` (enum)
- `BicicletaRequestDTO`, `BicicletaResponseDTO`, `BicicletaMapper`
- `BicicletaRepository`, `BicicletaService`, `BicicletaController`
- `CodigoUniqueViolationException`

**Requisitos:**
- Acesso restrito ao perfil `ADMIN`.
- Validação dos campos de entrada.
- Recurso documentado com Swagger e OpenAPI.
- Testes de integração (`BicicletaIT`) cobrindo os cenários de CRUD, conflito de código, campos inválidos e acesso não autorizado.

---

## Atividade 08 - Recurso Locação

Gerenciamento do ciclo de vida completo de uma locação de bicicleta, com controle de status e dados de pagamento simplificado.

**Recurso `Locacao`:**
- `id` (Long)
- `cliente` (FK → Cliente)
- `bicicleta` (FK → Bicicleta)
- `dataRetirada` (LocalDateTime) — preenchida ao iniciar a locação
- `dataPrevistaDevolucao` (LocalDateTime) — obrigatória na criação
- `dataDevolucao` (LocalDateTime) — preenchida ao finalizar
- `status` (Enum: `SOLICITADA`, `EM_PROGRESSO`, `CONCLUIDA`, `CANCELADA`)
- `valor` (BigDecimal)
- Campos de auditoria (data de criação, data de modificação, criado por, modificado por)

**Endpoints (`/api/v1/locacoes`):**
- `POST /` — solicita uma nova locação _(perfil: CLIENTE)_
- `PUT /{id}/iniciar` — inicia a locação, registra `dataRetirada` e status `EM_PROGRESSO` _(perfil: ADMIN)_
- `PUT /{id}/finalizar` — finaliza a locação, registra `dataDevolucao` e status `CONCLUIDA` _(perfil: CLIENTE)_
- `PUT /{id}/cancelar` — cancela a locação _(perfil: ADMIN ou CLIENTE)_
- `GET /{id}` — busca locação por ID _(perfil: ADMIN ou CLIENTE)_
- `GET /` — lista todas as locações _(perfil: ADMIN)_

**Regras de negócio:**
- Uma locação só pode ser solicitada se a bicicleta estiver disponível no período informado (sem conflito com locações `SOLICITADA` ou `EM_PROGRESSO`).
- O status segue o fluxo: `SOLICITADA` → `EM_PROGRESSO` → `CONCLUIDA` (ou `CANCELADA` a qualquer momento antes de concluída).
- Locações já `CONCLUIDA` não podem ser canceladas.

**Classes criadas/modificadas:**
- `Locacao` (entity), `LocacaoStatus` (enum)
- `LocacaoCreateDto`, `LocacaoResponseDto`, `LocacaoMapper`
- `LocacaoRepository`, `LocacaoService`, `LocacaoController`
- `BicicletaIndisponivelException`, `LocacaoNotFoundException`
- Adicionada anotação `@EntityListeners(AuditingEntityListener.class)` na entidade `Locacao` para auditoria automática.

**Fluxo completo via curl:**

```bash
# 1. Criar usuário admin
curl -s -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"admin1"}'

# 2. Promover para ADMIN no banco
mysql -u samuel -psamuel118 -D bikes \
  -e "UPDATE usuarios SET role='ADMIN' WHERE username='admin@example.com';"

# 3. Login admin → guardar TOKEN_ADMIN
curl -s -X POST http://localhost:8080/api/v1/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@example.com","password":"admin1"}'

# 4. Criar usuário cliente
curl -s -X POST http://localhost:8080/api/v1/usuarios \
  -H "Content-Type: application/json" \
  -d '{"username":"cliente@example.com","password":"clien1"}'

# 5. Login cliente → guardar TOKEN_CLIENTE
curl -s -X POST http://localhost:8080/api/v1/auth \
  -H "Content-Type: application/json" \
  -d '{"username":"cliente@example.com","password":"clien1"}'

# 6. Criar entidade cliente (usar TOKEN_CLIENTE)
curl -s -X POST http://localhost:8080/api/v1/clientes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CLIENTE" \
  -d '{"nome":"João da Silva","cpf":"52998224725"}'

# 7. Criar bicicleta (usar TOKEN_ADMIN)
curl -s -X POST http://localhost:8080/api/v1/bicicletas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ADMIN" \
  -d '{"codigo":"BIKE-001","status":"LIVRE"}'

# 8. Solicitar locação (usar TOKEN_CLIENTE)
curl -s -X POST http://localhost:8080/api/v1/locacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_CLIENTE" \
  -d '{"clienteId":1,"bicicletaId":1,"dataPrevistaDevolucao":"2026-11-25T10:00:00","numeroCartao":"1234567890123456","cvv":123,"mesExpiracao":12,"anoExpiracao":2028}'

# 9. Iniciar locação (usar TOKEN_ADMIN)
curl -s -X PUT http://localhost:8080/api/v1/locacoes/1/iniciar \
  -H "Authorization: Bearer TOKEN_ADMIN"

# 10. Finalizar locação (usar TOKEN_CLIENTE)
curl -s -X PUT http://localhost:8080/api/v1/locacoes/1/finalizar \
  -H "Authorization: Bearer TOKEN_CLIENTE"
```

**Documentação da API:** `http://localhost:8080/docs-bikes.html`
