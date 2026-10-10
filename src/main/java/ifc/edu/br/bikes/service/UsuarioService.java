package ifc.edu.br.bikes.service;

import ifc.edu.br.bikes.dto.UsuarioRequestDTO;
import ifc.edu.br.bikes.dto.UsuarioResponseDTO;
import ifc.edu.br.bikes.dto.UsuarioSenhaDTO;
import ifc.edu.br.bikes.dto.mapper.UsuarioMapper;
import ifc.edu.br.bikes.entity.Usuario;
import ifc.edu.br.bikes.exception.EntityNotFoundExcpetion;
import ifc.edu.br.bikes.exception.PasswordInvalidException;
import ifc.edu.br.bikes.exception.UsernameUniqueViolationExcpetion;
import ifc.edu.br.bikes.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponseDTO create(UsuarioRequestDTO usuarioRequestDTO){
        try{
            Usuario usuario = usuarioMapper.toUsuario(usuarioRequestDTO);
            return usuarioMapper.toResponse(usuarioRepository.save(usuario));
        } catch (RuntimeException ex) {
            throw new UsernameUniqueViolationExcpetion(String.format("O username '%s' já existe.", usuarioRequestDTO.username()));
        }
    }

    @Transactional()
    public UsuarioResponseDTO getById(Long id){
        return usuarioMapper.toResponse(usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundExcpetion(String.format("O usuário com id '%d' não encontrado.", id))
        ));
    }

    @Transactional
    public void updatePassword(Long id, UsuarioSenhaDTO usuarioSenhaDTO){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundExcpetion(String.format("O usuário com id '%d' não encontrado.", id))
        );

        // validação da senha atual e da senha salva com a classe de exceção para senhas invalidas
        if (!usuario.getPassword().equals(usuarioSenhaDTO.senhaAtual())) {
            throw new PasswordInvalidException("A senha atual não confere.");
        }

        // validação da nova senha com a confirmação de senha usando a mesma exceção
        if (!usuarioSenhaDTO.novaSenha().equals(usuarioSenhaDTO.confirmaSenha())) {
            throw new PasswordInvalidException("A nova senha não confere com a confirmação de senha.");
        }

        usuario.setPassword(usuarioSenhaDTO.novaSenha());
        usuarioRepository.save(usuario);
    }

    public List<UsuarioResponseDTO> getAll(){
        return usuarioMapper.toResponse(usuarioRepository.findAll());
    }
}
