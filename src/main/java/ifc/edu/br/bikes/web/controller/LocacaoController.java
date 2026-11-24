package ifc.edu.br.bikes.web.controller;

import ifc.edu.br.bikes.dto.LocacaoCreateDto;
import ifc.edu.br.bikes.dto.LocacaoResponseDto;
import ifc.edu.br.bikes.exception.BicicletaIndisponivelException;
import ifc.edu.br.bikes.exception.LocacaoNotFoundException;
import ifc.edu.br.bikes.service.LocacaoService;
import ifc.edu.br.bikes.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locacoes")
@Tag(name = "Locações", description = "Operações do ciclo de vida de locação de bicicletas: solicitar, iniciar, finalizar e cancelar.")
public class LocacaoController {

    private final LocacaoService locacaoService;

    public LocacaoController(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    @Operation(summary = "Solicitar nova locação",
            description = "Recurso para o cliente autenticado solicitar uma locação de bicicleta. Requer perfil CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Locação solicitada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Bicicleta indisponível no período solicitado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Dados de entrada inválidos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> solicitar(@Valid @RequestBody LocacaoCreateDto dto) {
        LocacaoResponseDto response = locacaoService.solicitarLocacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Iniciar locação",
            description = "Recurso para o administrador iniciar uma locação previamente solicitada. Define a data de retirada e muda o status para EM_PROGRESSO. Requer perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locação iniciada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Locação não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/{id}/iniciar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocacaoResponseDto> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.iniciarLocacao(id));
    }

    @Operation(summary = "Finalizar locação",
            description = "Recurso para o cliente autenticado finalizar sua locação em andamento. Define a data de devolução e muda o status para CONCLUIDA. Requer perfil CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locação finalizada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Locação não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.finalizarLocacao(id));
    }

    @Operation(summary = "Cancelar locação",
            description = "Recurso para cancelar uma locação que ainda não foi concluída. Pode ser acionado por ADMIN ou CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locação cancelada com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Locação não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.cancelarLocacao(id));
    }

    @Operation(summary = "Buscar locação por ID",
            description = "Recupera os detalhes de uma locação pelo seu ID. Acessível por ADMIN e CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Locação encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LocacaoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Locação não encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas as locações",
            description = "Retorna a lista completa de locações registradas no sistema. Requer perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de locações retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LocacaoResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado — requer perfil ADMIN",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LocacaoResponseDto>> listar() {
        return ResponseEntity.ok(locacaoService.listarTodas());
    }
}

