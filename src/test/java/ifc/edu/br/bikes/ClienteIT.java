package ifc.edu.br.bikes;

import ifc.edu.br.bikes.dto.ClienteCreateDto;
import ifc.edu.br.bikes.dto.ClienteResponseDto;
import ifc.edu.br.bikes.jwt.JwtUtils;
import ifc.edu.br.bikes.web.exception.ErrorMessage;
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
@Sql(scripts = "/sql/clientes/clientes-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clientes/clientes-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClienteIT {

    @LocalServerPort
    int port;

    WebTestClient testClient;
    WebTestClient adminClient;
    WebTestClient mariaClient; // cliente com cadastro já existente (id 101)
    WebTestClient joaoClient;  // cliente sem cadastro ainda (id 102)

    @BeforeEach
    void setUp() {
        String adminToken = JwtUtils.createToken("jose@gmail.com", "ADMIN").getToken();
        String mariaToken = JwtUtils.createToken("maria@gmail.com", "CLIENTE").getToken();
        String joaoToken = JwtUtils.createToken("joao@gmail.com", "CLIENTE").getToken();

        String baseUrl = "http://localhost:" + port;
        this.testClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
        this.adminClient = WebTestClient.bindToServer().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .build();
        this.mariaClient = WebTestClient.bindToServer().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + mariaToken)
                .build();
        this.joaoClient = WebTestClient.bindToServer().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + joaoToken)
                .build();
    }

    @Test
    public void cadastrarCliente_ComDadosValidos_RetornaStatus201() {
        ClienteResponseDto responseBody = joaoClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("Joao Santos", "45561573071"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.nome()).isEqualTo("Joao Santos");
        assertThat(responseBody.cpf()).isEqualTo("45561573071");
    }

    @Test
    public void cadastrarCliente_ComCpfJaExistente_RetornaStatus409() {
        ErrorMessage responseBody = joaoClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("Joao Santos", "60727580531"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void cadastrarCliente_UsuarioJaPossuiCliente_RetornaStatus409() {
        ErrorMessage responseBody = mariaClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("Maria Silva Outra", "34542836479"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void cadastrarCliente_ComDadosInvalidos_RetornaStatus422() {
        // Nome vazio
        ErrorMessage responseBody = joaoClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("", "45561573071"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        // CPF inválido (formato incorreto / dígito inválido)
        responseBody = joaoClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("Joao Santos", "11111111111"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void cadastrarCliente_ComPerfilAdmin_RetornaStatus403() {
        adminClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("Admin Nome", "45561573071"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void cadastrarCliente_SemAutenticacao_RetornaStatus403() {
        testClient
                .post().uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClienteCreateDto("Teste Nome", "45561573071"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void buscarCliente_PorIdExistente_ComPerfilAdmin_RetornaCliente() {
        ClienteResponseDto responseBody = adminClient
                .get().uri("/api/v1/clientes/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(10L);
        assertThat(responseBody.nome()).isEqualTo("Maria Silva");
        assertThat(responseBody.cpf()).isEqualTo("60727580531");
    }

    @Test
    public void buscarCliente_PorIdInexistente_ComPerfilAdmin_RetornaStatus404() {
        ErrorMessage responseBody = adminClient
                .get().uri("/api/v1/clientes/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarCliente_PorId_ComPerfilCliente_RetornaStatus403() {
        mariaClient
                .get().uri("/api/v1/clientes/10")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void recuperarDadosProprios_ComPerfilCliente_RetornaCliente() {
        ClienteResponseDto responseBody = mariaClient
                .get().uri("/api/v1/clientes/detalhes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(10L);
        assertThat(responseBody.nome()).isEqualTo("Maria Silva");
        assertThat(responseBody.cpf()).isEqualTo("60727580531");
    }

    @Test
    public void recuperarDadosProprios_ClienteNaoCadastrado_RetornaStatus404() {
        ErrorMessage responseBody = joaoClient
                .get().uri("/api/v1/clientes/detalhes")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void listarClientes_ComPerfilAdmin_RetornaListaClientes() {
        adminClient
                .get().uri("/api/v1/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClienteResponseDto.class)
                .value(clientes -> assertThat(clientes).isNotEmpty());
    }

    @Test
    public void listarClientes_ComPerfilCliente_RetornaStatus403() {
        mariaClient
                .get().uri("/api/v1/clientes")
                .exchange()
                .expectStatus().isForbidden();
    }
}
