package ifc.edu.br.bikes;

import ifc.edu.br.bikes.dto.UsuarioRequestDTO;
import ifc.edu.br.bikes.dto.UsuarioResponseDTO;
import ifc.edu.br.bikes.dto.UsuarioSenhaDTO;
import ifc.edu.br.bikes.jwt.JwtUtils;
import ifc.edu.br.bikes.web.excpetion.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @LocalServerPort
    int port;

    WebTestClient testClient;
    WebTestClient adminClient;
    WebTestClient clienteClient;

    @BeforeEach
    void setUp() {
        String adminToken = JwtUtils.createToken("jose@gmail.com", "ADMIN").getToken();
        String clienteToken = JwtUtils.createToken("maria@gmail.com", "CLIENTE").getToken();

        String baseUrl = "http://localhost:" + port;
        this.testClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
        this.adminClient = WebTestClient.bindToServer().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .build();
        this.clienteClient = WebTestClient.bindToServer().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + clienteToken)
                .build();
    }

    @Test
    public void criarUsuario_UsernamePasswordInvalidos_RetornaUsuarioCriado() {
        UsuarioResponseDTO responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioRequestDTO("fabio@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.username()).isEqualTo("fabio@gmail.com");
        assertThat(responseBody.role()).isEqualTo("CLIENTE");
    }

    @Test
    public void criarUsuario_UsernameExistente_RetornaErrorMessageStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioRequestDTO("jose@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test // TESTA PARA O CASO DE USUÁRIO INVÁLIDO
    public void criarUsuario_UsernameInvalido_RetornaErrorMessageStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioRequestDTO("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioRequestDTO("jose@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioRequestDTO("jose@gmail.", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test // TESTA PARA O CASO DE SUCESSO - USUÁRIO EXISTENTE
    public void buscarUsuario_IdExistente_RetornaUsuario() {
        UsuarioResponseDTO responseBody = adminClient
                .get()
                .uri("/api/v1/usuarios/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(100);
        assertThat(responseBody.username()).isEqualTo("jose@gmail.com");
        assertThat(responseBody.role()).isEqualTo("ADMIN");
    }

    @Test // TESTA PARA O CASO DE ERRO - USUÁRIO INEXISTENTE (ID 110)
    public void buscarUsuario_IdInexistente_RetornaUsuario() {
        ErrorMessage responseBody = adminClient
                .get()
                .uri("/api/v1/usuarios/110")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test // TESTA ACESSO NÃO AUTORIZADO
    public void buscarUsuario_SemAutenticacao_RetornaStatus403() {
        testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test // CLIENTE NÃO PODE ACESSAR DADOS DE OUTRO USUÁRIO
    public void buscarUsuario_PorOutroCliente_RetornaStatus403() {
        clienteClient
                .get()
                .uri("/api/v1/usuarios/100")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test // TESTA O CASO DE SUCESSO - ALTERAÇÃO DE SENHA COM DADOS VÁLIDOS
    public void alterarSenha_DadosValidos_RetornaNoContent() {
        adminClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "654321", "654321"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test // TESTA O CASO DE ERRO - SENHA ATUAL NÃO CONFERE
    public void alterarSenha_SenhaAtualInvalida_RetornaBadRequest() {
        adminClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("654321", "111111", "111111"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test // TESTA O CASO DE ERRO - NOVA SENHA E CONFIRMAÇÃO SÃO DIFERENTES
    public void alterarSenha_NovaSenhaEConfirmacaoDiferentes_RetornaBadRequest() {
        adminClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "654321", "111111"))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test // TESTA CAMPOS VAZIOS E SENHAS COM MENOS OU MAIS DE 6 CARACTERES
    public void alterarSenha_CamposInvalidos_RetornaUnprocessableContent() {
        UsuarioSenhaDTO[] senhasInvalidas = {
                new UsuarioSenhaDTO("", "", ""),
                new UsuarioSenhaDTO("12345", "12345", "12345"),
                new UsuarioSenhaDTO("1234567", "1234567", "1234567")
        };

        for (UsuarioSenhaDTO senha : senhasInvalidas) {
            adminClient
                    .patch()
                    .uri("/api/v1/usuarios/100")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(senha)
                    .exchange()
                    .expectStatus().isEqualTo(422);
        }
    }

    @Test // USUÁRIO NÃO PODE ALTERAR SENHA DE OUTRO USUÁRIO
    public void alterarSenha_PorOutroUsuario_RetornaStatus403() {
        clienteClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "654321", "654321"))
                .exchange()
                .expectStatus().isForbidden();
    }
}
