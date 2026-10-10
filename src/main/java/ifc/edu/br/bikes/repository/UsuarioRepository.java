package ifc.edu.br.bikes.repository;

import ifc.edu.br.bikes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
