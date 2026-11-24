package ifc.edu.br.bikes.jwt;

import ifc.edu.br.bikes.entity.Usuario;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {
    private Usuario usuario;

    public JwtUserDetails(Usuario usuario){
        super(usuario.getUsername(), usuario.getPassword(),
                AuthorityUtils.createAuthorityList(
                        "ROLE_" + (usuario.getRole().name().startsWith("ROLE_") ? usuario.getRole().name().substring(5) : usuario.getRole().name()),
                        usuario.getRole().name().startsWith("ROLE_") ? usuario.getRole().name().substring(5) : usuario.getRole().name()
                )
        );
        this.usuario = usuario;
    }

    public Long getId(){
        return this.usuario.getId();
    }

    public String getRole(){
        String role = this.usuario.getRole().name();
        return role.startsWith("ROLE_") ? role.substring(5) : role;
    }
}
