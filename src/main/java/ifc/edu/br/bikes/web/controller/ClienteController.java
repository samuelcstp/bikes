package ifc.edu.br.bikes.web.controller;

import ifc.edu.br.bikes.dto.ClienteCreateDto;
import ifc.edu.br.bikes.dto.ClienteResponseDto;
import ifc.edu.br.bikes.jwt.JwtUserDetails;
import ifc.edu.br.bikes.service.ClienteService;
import ifc.edu.br.bikes.web.excpetion.ErrorMessage;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Clientes", description = "Contém todas as operações relativas aos recursos de cliente.")
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Criar um novo cliente",
            description = "Recurso para cadastrar um novo cliente vinculado ao usuário autenticado. Requer perfil CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "CPF já cadastrado no sistema",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a acessar este recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE') || hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> create(
            @Valid @RequestBody ClienteCreateDto dto,
            @AuthenticationPrincipal JwtUserDetails userDetails) {

        ClienteResponseDto clienteResponse = clienteService.create(dto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponse);
    }

    @Operation(summary = "Recuperar dados do próprio cliente",
            description = "Permite que o cliente autenticado consulte seus próprios dados. Requer perfil CLIENTE.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dados recuperados com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a acessar este recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/detalhes")
    @PreAuthorize("hasAuthority('CLIENTE') || hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> getDadosProprios(@AuthenticationPrincipal JwtUserDetails userDetails) {
        ClienteResponseDto clienteResponse = clienteService.getByUsuarioId(userDetails.getId());
        return ResponseEntity.ok(clienteResponse);
    }

    @Operation(summary = "Recuperar cliente por ID",
            description = "Recurso restrito a administradores para buscar um cliente pelo ID. Requer perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente recuperado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a acessar este recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') || hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        ClienteResponseDto clienteResponse = clienteService.getById(id);
        return ResponseEntity.ok(clienteResponse);
    }

    @Operation(summary = "Listar todos os clientes",
            description = "Recurso restrito a administradores para listar todos os clientes cadastrados. Requer perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista com todos os clientes cadastrados",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ClienteResponseDto.class)))),
                    @ApiResponse(responseCode = "403", description = "Usuário não autorizado a acessar este recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') || hasRole('ADMIN')")
    public ResponseEntity<List<ClienteResponseDto>> getAll() {
        List<ClienteResponseDto> clientes = clienteService.getAll();
        return ResponseEntity.ok(clientes);
    }
}
