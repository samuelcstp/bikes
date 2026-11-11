package ifc.edu.br.bikes.dto;

import ifc.edu.br.bikes.entity.BicicletaStatus;

public record BicicletaResponseDTO(
        Long id,
        String codigo,
        BicicletaStatus status
) {
}
