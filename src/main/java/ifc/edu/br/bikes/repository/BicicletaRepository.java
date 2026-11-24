package ifc.edu.br.bikes.repository;

import ifc.edu.br.bikes.entity.Bicicleta;
import ifc.edu.br.bikes.entity.BicicletaStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bicicleta b WHERE b.id = :id")
    Optional<Bicicleta> findByIdForUpdate(@Param("id") Long id);

    Optional<Bicicleta> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Bicicleta> findByStatus(BicicletaStatus status);
}
