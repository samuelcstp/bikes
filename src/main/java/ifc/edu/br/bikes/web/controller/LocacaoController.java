package ifc.edu.br.bikes.web.controller;

import ifc.edu.br.bikes.dto.LocacaoCreateDto;
import ifc.edu.br.bikes.dto.LocacaoResponseDto;
import ifc.edu.br.bikes.exception.BicicletaIndisponivelException;
import ifc.edu.br.bikes.exception.LocacaoNotFoundException;
import ifc.edu.br.bikes.service.LocacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locacoes")
@Tag(name = "Locações", description = "Operações de locação de bicicletas")
public class LocacaoController {

    private final LocacaoService locacaoService;

    public LocacaoController(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    @Operation(summary = "Solicitar nova locação", security = @SecurityRequirement(name = "security"))
    @ApiResponse(responseCode = "201", description = "Locação criada")
    @ApiResponse(responseCode = "409", description = "Bicicleta indisponível")
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> solicitar(@Valid @RequestBody LocacaoCreateDto dto) {
        LocacaoResponseDto response = locacaoService.solicitarLocacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Iniciar locação (admin)", security = @SecurityRequirement(name = "security"))
    @PutMapping("/{id}/iniciar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocacaoResponseDto> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.iniciarLocacao(id));
    }

    @Operation(summary = "Finalizar locação (cliente)", security = @SecurityRequirement(name = "security"))
    @PutMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.finalizarLocacao(id));
    }

    @Operation(summary = "Cancelar locação (admin ou cliente)", security = @SecurityRequirement(name = "security"))
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.cancelarLocacao(id));
    }

    @Operation(summary = "Buscar locação por ID", security = @SecurityRequirement(name = "security"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    public ResponseEntity<LocacaoResponseDto> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(locacaoService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas as locações (admin)", security = @SecurityRequirement(name = "security"))
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LocacaoResponseDto>> listar() {
        return ResponseEntity.ok(locacaoService.listarTodas());
    }
}
