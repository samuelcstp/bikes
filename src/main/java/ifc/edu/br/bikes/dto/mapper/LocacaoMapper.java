package ifc.edu.br.bikes.dto.mapper;

import ifc.edu.br.bikes.dto.LocacaoCreateDto;
import ifc.edu.br.bikes.dto.LocacaoResponseDto;
import ifc.edu.br.bikes.model.Locacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocacaoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "bicicleta.id", target = "bicicletaId")
    @Mapping(source = "status", target = "status")
    LocacaoResponseDto toResponseDto(Locacao locacao);

    // MapStruct can map simple fields; for nested objects we set later in service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "bicicleta", ignore = true)
    @Mapping(target = "dataRetirada", ignore = true)
    @Mapping(target = "dataDevolucao", ignore = true)
    @Mapping(target = "status", expression = "java(ifc.edu.br.bikes.model.LocacaoStatus.SOLICITADA)")
    Locacao fromCreateDto(LocacaoCreateDto dto);
}
