package ifc.edu.br.bikes.service;

import ifc.edu.br.bikes.dto.UsuarioRequestDTO;
import ifc.edu.br.bikes.dto.UsuarioResponseDTO;
import ifc.edu.br.bikes.dto.UsuarioSenhaDTO;
import ifc.edu.br.bikes.dto.mapper.UsuarioMapper;
import ifc.edu.br.bikes.entity.Usuario;
import ifc.edu.br.bikes.exception.EntityNotFoundExcpetion;
import ifc.edu.br.bikes.exception.PasswordInvalidException;
import ifc.edu.br.bikes.exception.UsernameUniqueViolationExcpetion;
import ifc.edu.br.bikes.jwt.JwtAuthorizationFilter;
import ifc.edu.br.bikes.jwt.JwtUserDetailsService;
import ifc.edu.br.bikes.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO create(UsuarioRequestDTO usuarioRequestDTO){
        try{
            Usuario usuario = usuarioMapper.toUsuario(usuarioRequestDTO);
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
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

        // validação da senha atual com o passwordEncoder
        if (!passwordEncoder.matches(usuarioSenhaDTO.senhaAtual(), usuario.getPassword())) {
            throw new PasswordInvalidException("A senha atual não confere.");
        }

        // validação da nova senha com a confirmação de senha
        if (!usuarioSenhaDTO.novaSenha().equals(usuarioSenhaDTO.confirmaSenha())) {
            throw new PasswordInvalidException("A nova senha não confere com a confirmação de senha.");
        }

        usuario.setPassword(passwordEncoder.encode(usuarioSenhaDTO.novaSenha()));
        usuarioRepository.save(usuario);
    }

    public List<UsuarioResponseDTO> getAll(){
        return usuarioMapper.toResponse(usuarioRepository.findAll());
    }

    @Transactional
    public Usuario buscarPorNome(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Usuário '%s' não encontrado", username)));
    }

    @Transactional
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Usuário id=%d não encontrado", id)));
    }

    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(JwtUserDetailsService detailsService) {
        return new JwtAuthorizationFilter(detailsService);
    }
}
