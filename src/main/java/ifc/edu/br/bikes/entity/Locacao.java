package ifc.edu.br.bikes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import ifc.edu.br.bikes.entity.Cliente;
import ifc.edu.br.bikes.entity.Bicicleta;
import ifc.edu.br.bikes.model.LocacaoStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locacoes")
@EntityListeners(AuditingEntityListener.class)
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicicleta_id", nullable = false)
    private Bicicleta bicicleta;

    @Column(name = "data_retirada")
    private LocalDateTime dataRetirada;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDateTime dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LocacaoStatus status;

    @Column(name = "valor")
    private java.math.BigDecimal valor;

    @Transient
    private java.time.Duration duracao;

    // Auditoria
    @CreatedDate
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    @CreatedBy
    @Column(name = "criado_por", updatable = false)
    private String criadoPor;

    @LastModifiedBy
    @Column(name = "modificado_por")
    private String modificadoPor;
}
