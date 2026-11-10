package ifc.edu.br.bikes.repository;

import ifc.edu.br.bikes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
    Optional<Cliente> findByUsuarioId(Long usuarioId);
    boolean existsByCpf(String cpf);
    boolean existsByUsuarioId(Long usuarioId);
}
