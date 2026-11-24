package ifc.edu.br.bikes.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "Número do cartão é obrigatório")
    private String numeroCartao;

    @Positive(message = "CVV deve ser positivo")
    private Integer cvv;

    @NotNull(message = "Mês de expiração é obrigatório")
    private Integer mesExpiracao;

    @NotNull(message = "Ano de expiração é obrigatório")
    private Integer anoExpiracao;
}
