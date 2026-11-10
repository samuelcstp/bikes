package ifc.edu.br.bikes.dto.mapper;

import ifc.edu.br.bikes.dto.ClienteCreateDto;
import ifc.edu.br.bikes.dto.ClienteResponseDto;
import ifc.edu.br.bikes.entity.Cliente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {
    Cliente toCliente(ClienteCreateDto dto);
    ClienteResponseDto toResponse(Cliente cliente);
    List<ClienteResponseDto> toResponse(List<Cliente> clientes);
}
