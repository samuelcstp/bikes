package ifc.edu.br.bikes.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta com os dados do cliente")
public record ClienteResponseDto(
        @Schema(description = "Identificador único do cliente", example = "1")
        Long id,

        @Schema(description = "Nome do cliente", example = "Maria Silva")
        String nome,

        @Schema(description = "CPF do cliente", example = "12345678901")
        String cpf
) {}
