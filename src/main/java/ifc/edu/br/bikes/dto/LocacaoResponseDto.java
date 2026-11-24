package ifc.edu.br.bikes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocacaoResponseDto {
    private Long id;
    private Long clienteId;
    private Long bicicletaId;
    private LocalDateTime dataRetirada;
    private LocalDateTime dataPrevistaDevolucao;
    private LocalDateTime dataDevolucao;
    private String status;
}
