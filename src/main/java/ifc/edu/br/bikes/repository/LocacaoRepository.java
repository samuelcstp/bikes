package ifc.edu.br.bikes.repository;

import ifc.edu.br.bikes.entity.Locacao;
import ifc.edu.br.bikes.model.LocacaoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {

    // Find overlapping locacoes for a given bicicleta that are not cancelled/completed
    @Query("SELECT l FROM Locacao l WHERE l.bicicleta.id = :bicicletaId AND l.status IN :activeStatuses AND " +
            "( (l.dataRetirada <= :end AND (l.dataDevolucao IS NULL OR l.dataDevolucao >= :start)) )")
    List<Locacao> findConflictingLocacoes(@Param("bicicletaId") Long bicicletaId,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("activeStatuses") List<LocacaoStatus> activeStatuses);

    boolean existsByBicicletaIdAndStatusIn(Long bicicletaId, List<LocacaoStatus> statuses);
}
