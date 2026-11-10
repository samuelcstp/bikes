package ifc.edu.br.bikes.repository;

import ifc.edu.br.bikes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    @Query("SELECT u.role from Usuario u WHERE u.username like :username")
    Usuario.Role findRoleByUsername(String username);
}
