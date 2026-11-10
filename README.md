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
