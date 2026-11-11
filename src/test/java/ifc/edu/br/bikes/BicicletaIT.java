package ifc.edu.br.bikes;

import ifc.edu.br.bikes.dto.BicicletaRequestDTO;
import ifc.edu.br.bikes.dto.BicicletaResponseDTO;
import ifc.edu.br.bikes.entity.BicicletaStatus;
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
@Sql(scripts = "/sql/bicicletas/bicicletas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/bicicletas/bicicletas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BicicletaIT {

    @LocalServerPort
    int port;

    WebTestClient testClient;
    WebTestClient adminClient;
    String clienteToken;

    @BeforeEach
    void setUp() {
        String adminToken = JwtUtils.createToken("jose@gmail.com", "ADMIN").getToken();
        this.clienteToken = JwtUtils.createToken("maria@gmail.com", "CLIENTE").getToken();

        String baseUrl = "http://localhost:" + port;
        this.testClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
        this.adminClient = WebTestClient.bindToServer().baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .build();
    }

    @Test
    public void cadastrarBicicleta_ComDadosValidos_RetornaBicicletaCriada() {
        BicicletaResponseDTO responseBody = adminClient
                .post().uri("/api/v1/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("BIKE-99", BicicletaStatus.LIVRE))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BicicletaResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.codigo()).isEqualTo("BIKE-99");
        assertThat(responseBody.status()).isEqualTo(BicicletaStatus.LIVRE);
    }

    @Test
    public void cadastrarBicicleta_ComCodigoExistente_RetornaStatus409() {
        ErrorMessage responseBody = adminClient
                .post().uri("/api/v1/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("BIKE-10", BicicletaStatus.LIVRE))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void cadastrarBicicleta_ComDadosInvalidos_RetornaStatus422() {
        ErrorMessage responseBody = adminClient
                .post().uri("/api/v1/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("", BicicletaStatus.LIVRE))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = adminClient
                .post().uri("/api/v1/bicicletas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("BIKE-TEST", null))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void buscarBicicleta_PorCodigoExistente_RetornaBicicleta() {
        BicicletaResponseDTO responseBody = adminClient
                .get().uri("/api/v1/bicicletas/BIKE-10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BicicletaResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(10L);
        assertThat(responseBody.codigo()).isEqualTo("BIKE-10");
        assertThat(responseBody.status()).isEqualTo(BicicletaStatus.LIVRE);
    }

    @Test
    public void buscarBicicleta_PorCodigoInexistente_RetornaStatus404() {
        ErrorMessage responseBody = adminClient
                .get().uri("/api/v1/bicicletas/CODIGO-INEXISTENTE")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void listarBicicletas_RetornaTodasAsBicicletasCadastradas() {
        adminClient.get().uri("/api/v1/bicicletas")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BicicletaResponseDTO.class)
                .value(lista -> assertThat(lista).hasSize(3));
    }

    @Test
    public void atualizarBicicleta_ComDadosValidos_RetornaBicicletaAtualizada() {
        BicicletaResponseDTO responseBody = adminClient
                .put().uri("/api/v1/bicicletas/BIKE-10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("BIKE-10-ATUALIZADA", BicicletaStatus.MANUTENCAO))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BicicletaResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.codigo()).isEqualTo("BIKE-10-ATUALIZADA");
        assertThat(responseBody.status()).isEqualTo(BicicletaStatus.MANUTENCAO);
    }

    @Test
    public void atualizarBicicleta_ComCodigoInexistente_RetornaStatus404() {
        ErrorMessage responseBody = adminClient
                .put().uri("/api/v1/bicicletas/CODIGO-NAO-EXISTE")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("NOVO-CODIGO", BicicletaStatus.LIVRE))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void atualizarBicicleta_ComCodigoJaUtilizadoPorOutra_RetornaStatus409() {
        ErrorMessage responseBody = adminClient
                .put().uri("/api/v1/bicicletas/BIKE-10")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("BIKE-20", BicicletaStatus.LIVRE))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void excluirBicicleta_ComCodigoExistente_RetornaNoContent() {
        adminClient.delete().uri("/api/v1/bicicletas/BIKE-30")
                .exchange()
                .expectStatus().isNoContent();

        // Confirma que a bicicleta foi realmente excluída
        adminClient.get().uri("/api/v1/bicicletas/BIKE-30")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void excluirBicicleta_ComCodigoInexistente_RetornaStatus404() {
        ErrorMessage responseBody = adminClient
                .delete().uri("/api/v1/bicicletas/CODIGO-INEXISTENTE")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void operacoesBicicleta_SemTokenOuUsuarioCliente_RetornaForbidden() {
        testClient.get().uri("/api/v1/bicicletas")
                .exchange()
                .expectStatus().isForbidden();

        testClient.get().uri("/api/v1/bicicletas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + clienteToken)
                .exchange()
                .expectStatus().isForbidden();
    }
}
