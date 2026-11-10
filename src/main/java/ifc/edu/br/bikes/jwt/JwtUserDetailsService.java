package ifc.edu.br.bikes.jwt;

import ifc.edu.br.bikes.entity.Usuario;
import ifc.edu.br.bikes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.buscarPorNome(username);
        return new JwtUserDetails(usuario);
    }

    public JwtToken getTokenAuthenticated(String username){
        Usuario.Role role = usuarioService.buscarRolePorUsername(username);
        return JwtUtils.createToken(username, role.name());
    }
}
