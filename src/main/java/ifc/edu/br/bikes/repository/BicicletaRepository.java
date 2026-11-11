package ifc.edu.br.bikes.repository;

import ifc.edu.br.bikes.entity.Bicicleta;
import ifc.edu.br.bikes.entity.BicicletaStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Long> {
    Optional<Bicicleta> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Bicicleta> findByStatus(BicicletaStatus status);
}
