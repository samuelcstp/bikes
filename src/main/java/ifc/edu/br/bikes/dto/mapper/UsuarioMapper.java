package ifc.edu.br.bikes.dto.mapper;

import ifc.edu.br.bikes.dto.UsuarioRequestDTO;
import ifc.edu.br.bikes.dto.UsuarioResponseDTO;
import ifc.edu.br.bikes.entity.Usuario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toUsuario(UsuarioRequestDTO usuarioRequestDTO);
    UsuarioResponseDTO toResponse(Usuario usuario);
    List<UsuarioResponseDTO> toResponse(List<Usuario> usuarios);
}
