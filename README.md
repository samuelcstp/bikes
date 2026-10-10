# Alteração de senha

PATCH do controller troca a senha de um usuário. O corpo deixou de ser o DTO de cadastro e passou a usar o DTO específico para alteração de senha, que contém os campos necessários:

- senhaAtual
- novaSenha
- confirmaSenha

Sucesso devolve `204` sem corpo. Antes devolvia `200` com o usuário.

A validação de formato fica no `UsuarioSenhaDTO`. A comparação das senhas fica no `UsuarioService` e sobe como `PasswordInvalidException`, tratada no `ApiExceptionHandler`.
