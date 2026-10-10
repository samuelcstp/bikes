package ifc.edu.br.bikes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// NotBlank testa o envio de ao menos um caractere
public record UsuarioRequestDTO(
        @NotBlank
        @Email(message = "O email deve ser válido")
        String username,
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 6, message = "A senha deve ter exatamente 6 caracteres")
        String password
) { }
