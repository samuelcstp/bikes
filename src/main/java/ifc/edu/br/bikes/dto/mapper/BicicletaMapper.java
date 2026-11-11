package ifc.edu.br.bikes.dto.mapper;

import ifc.edu.br.bikes.dto.BicicletaRequestDTO;
import ifc.edu.br.bikes.dto.BicicletaResponseDTO;
import ifc.edu.br.bikes.entity.Bicicleta;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BicicletaMapper {
    Bicicleta toBicicleta(BicicletaRequestDTO dto);
    BicicletaResponseDTO toResponse(Bicicleta bicicleta);
    List<BicicletaResponseDTO> toResponse(List<Bicicleta> bicicletas);
}
