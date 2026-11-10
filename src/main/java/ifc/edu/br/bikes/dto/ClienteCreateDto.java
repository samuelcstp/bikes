package ifc.edu.br.bikes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "DTO de criação de um novo cliente")
public record ClienteCreateDto(
        @Schema(description = "Nome do cliente", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Schema(description = "CPF do cliente", example = "12345678901")
        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf
) {}
