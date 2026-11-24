package ifc.edu.br.bikes.web.controller;

import ifc.edu.br.bikes.dto.BicicletaRequestDTO;
import ifc.edu.br.bikes.dto.BicicletaResponseDTO;
import ifc.edu.br.bikes.service.BicicletaService;
import ifc.edu.br.bikes.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bicicletas", description = "Consulta de bicicletas para clientes autenticados e gerenciamento para administradores.")
@RestController
@RequestMapping("/api/v1/bicicletas")
@RequiredArgsConstructor
public class BicicletaController {

    private final BicicletaService bicicletaService;

    @Operation(summary = "Cadastrar bicicleta",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Bicicleta criada",
                            content = @Content(schema = @Schema(implementation = BicicletaResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Código já cadastrado",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BicicletaResponseDTO> create(@Valid @RequestBody BicicletaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bicicletaService.create(request));
    }

    @Operation(summary = "Localizar bicicleta pelo código",
            description = "Disponível para os perfis ADMIN e CLIENTE; o status informa se a bicicleta está LIVRE, OCUPADA ou em MANUTENCAO.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bicicleta localizada",
                            content = @Content(schema = @Schema(implementation = BicicletaResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Bicicleta não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{codigo}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<BicicletaResponseDTO> getByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(bicicletaService.getByCodigo(codigo));
    }

    @Operation(summary = "Listar bicicletas",
            description = "Disponível para os perfis ADMIN e CLIENTE; use o campo status para identificar bicicletas LIVREs.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bicicletas cadastradas",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BicicletaResponseDTO.class)))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<List<BicicletaResponseDTO>> getAll() {
        return ResponseEntity.ok(bicicletaService.getAll());
    }

    @Operation(summary = "Atualizar bicicleta",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Bicicleta atualizada",
                            content = @Content(schema = @Schema(implementation = BicicletaResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Bicicleta não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Código já cadastrado",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Dados inválidos",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BicicletaResponseDTO> update(
            @PathVariable String codigo,
            @Valid @RequestBody BicicletaRequestDTO request) {
        return ResponseEntity.ok(bicicletaService.update(codigo, request));
    }

    @Operation(summary = "Excluir bicicleta",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Bicicleta excluída"),
                    @ApiResponse(responseCode = "404", description = "Bicicleta não encontrada",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            })
    @DeleteMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String codigo) {
        bicicletaService.delete(codigo);
        return ResponseEntity.noContent().build();
    }
}
