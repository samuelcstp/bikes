package ifc.edu.br.bikes.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocacaoCreateDto {

    @NotNull(message = "Cliente ID não pode ser nulo")
    private Long clienteId;

    @NotNull(message = "Bicicleta ID não pode ser nulo")
    private Long bicicletaId;

    @NotNull(message = "Data prevista de devolução é obrigatória")
    @Future(message = "Data prevista deve ser futura")
    private LocalDateTime dataPrevistaDevolucao;

    // Dados simplificados do cartão, apenas placeholder
    @NotBlank(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "\\d{13,19}", message = "Número do cartão deve conter entre 13 e 19 dígitos")
    private String numeroCartao;

    @NotNull(message = "CVV é obrigatório")
    @Min(value = 100, message = "CVV deve conter 3 dígitos")
    @Max(value = 999, message = "CVV deve conter 3 dígitos")
    private Integer cvv;

    @NotNull(message = "Mês de expiração é obrigatório")
    @Min(value = 1, message = "Mês de expiração deve estar entre 1 e 12")
    @Max(value = 12, message = "Mês de expiração deve estar entre 1 e 12")
    private Integer mesExpiracao;

    @NotNull(message = "Ano de expiração é obrigatório")
    @Min(value = 2000, message = "Ano de expiração inválido")
    private Integer anoExpiracao;
}
