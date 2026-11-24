package ifc.edu.br.bikes.service;

import ifc.edu.br.bikes.dto.LocacaoCreateDto;
import ifc.edu.br.bikes.dto.LocacaoResponseDto;
import ifc.edu.br.bikes.exception.BicicletaIndisponivelException;
import ifc.edu.br.bikes.exception.EntityNotFoundException;
import ifc.edu.br.bikes.exception.LocacaoInvalidaException;
import ifc.edu.br.bikes.exception.LocacaoNotFoundException;
import ifc.edu.br.bikes.entity.Bicicleta;
import ifc.edu.br.bikes.entity.BicicletaStatus;
import ifc.edu.br.bikes.entity.Cliente;
import ifc.edu.br.bikes.entity.Locacao;
import ifc.edu.br.bikes.model.LocacaoStatus;
import ifc.edu.br.bikes.repository.BicicletaRepository;
import ifc.edu.br.bikes.repository.ClienteRepository;
import ifc.edu.br.bikes.repository.LocacaoRepository;
import ifc.edu.br.bikes.dto.mapper.LocacaoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.time.YearMonth;

@Service
public class LocacaoService {

    private final LocacaoRepository locacaoRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;
    private final LocacaoMapper locacaoMapper;
    private final BigDecimal valorHora;

    public LocacaoService(LocacaoRepository locacaoRepository,
                          ClienteRepository clienteRepository,
                          BicicletaRepository bicicletaRepository,
                          LocacaoMapper locacaoMapper,
                          @Value("${locacao.valor-hora:10.00}") BigDecimal valorHora) {
        this.locacaoRepository = locacaoRepository;
        this.clienteRepository = clienteRepository;
        this.bicicletaRepository = bicicletaRepository;
        this.locacaoMapper = locacaoMapper;
        this.valorHora = valorHora;
    }

    @Transactional
    public LocacaoResponseDto solicitarLocacao(LocacaoCreateDto dto) {
        // valida cliente e bicicleta existirem
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        Bicicleta bicicleta = bicicletaRepository.findByIdForUpdate(dto.getBicicletaId())
                .orElseThrow(() -> new EntityNotFoundException("Bicicleta não encontrada"));
        validarCartaoNaoExpirado(dto);

        if (bicicleta.getStatus() != BicicletaStatus.LIVRE) {
            throw new BicicletaIndisponivelException("Bicicleta não está livre para locação");
        }
        List<LocacaoStatus> activeStatuses = List.of(LocacaoStatus.SOLICITADA, LocacaoStatus.EM_PROGRESSO);
        if (locacaoRepository.existsByBicicletaIdAndStatusIn(bicicleta.getId(), activeStatuses)) {
            throw new BicicletaIndisponivelException("Bicicleta já possui uma locação solicitada ou em andamento");
        }

        Locacao locacao = locacaoMapper.fromCreateDto(dto);
        locacao.setCliente(cliente);
        locacao.setBicicleta(bicicleta);
        locacao.setDataPrevistaDevolucao(dto.getDataPrevistaDevolucao());
        locacao.setStatus(LocacaoStatus.SOLICITADA);
        bicicleta.setStatus(BicicletaStatus.OCUPADA);
        bicicletaRepository.save(bicicleta);
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional
    public LocacaoResponseDto iniciarLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        if (locacao.getStatus() != LocacaoStatus.SOLICITADA) {
            throw new LocacaoInvalidaException("Locação não está no status SOLICITADA");
        }
        Bicicleta bicicleta = locacao.getBicicleta();
        if (bicicleta.getStatus() == BicicletaStatus.MANUTENCAO) {
            throw new BicicletaIndisponivelException("Bicicleta está em manutenção");
        }
        locacao.setStatus(LocacaoStatus.EM_PROGRESSO);
        locacao.setDataRetirada(LocalDateTime.now());
        bicicleta.setStatus(BicicletaStatus.OCUPADA);
        bicicletaRepository.save(bicicleta);
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional
    public LocacaoResponseDto finalizarLocacao(Long id, boolean comProblema) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        if (locacao.getStatus() != LocacaoStatus.EM_PROGRESSO) {
            throw new LocacaoInvalidaException("Locação não está em progresso");
        }
        LocalDateTime dataDevolucao = LocalDateTime.now();
        locacao.setStatus(LocacaoStatus.CONCLUIDA);
        locacao.setDataDevolucao(dataDevolucao);
        locacao.setValor(calcularValor(locacao, dataDevolucao));

        Bicicleta bicicleta = locacao.getBicicleta();
        bicicleta.setStatus(comProblema ? BicicletaStatus.MANUTENCAO : BicicletaStatus.LIVRE);
        bicicletaRepository.save(bicicleta);
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional
    public LocacaoResponseDto cancelarLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        if (locacao.getStatus() == LocacaoStatus.CONCLUIDA) {
            throw new LocacaoInvalidaException("Locação já concluída, não pode ser cancelada");
        }
        locacao.setStatus(LocacaoStatus.CANCELADA);
        Bicicleta bicicleta = locacao.getBicicleta();
        if (bicicleta.getStatus() == BicicletaStatus.OCUPADA) {
            bicicleta.setStatus(BicicletaStatus.LIVRE);
            bicicletaRepository.save(bicicleta);
        }
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional(readOnly = true)
    public LocacaoResponseDto buscarPorId(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional(readOnly = true)
    public List<LocacaoResponseDto> listarTodas() {
        return locacaoRepository.findAll().stream()
                .map(locacaoMapper::toResponseDto)
                .toList();
    }

    private void validarCartaoNaoExpirado(LocacaoCreateDto dto) {
        YearMonth expiracao = YearMonth.of(dto.getAnoExpiracao(), dto.getMesExpiracao());
        if (expiracao.isBefore(YearMonth.now())) {
            throw new LocacaoInvalidaException("Cartão de crédito expirado");
        }
    }

    private BigDecimal calcularValor(Locacao locacao, LocalDateTime dataDevolucao) {
        LocalDateTime inicio = locacao.getDataRetirada() != null
                ? locacao.getDataRetirada()
                : locacao.getDataCriacao();
        if (inicio == null || inicio.isAfter(dataDevolucao)) {
            throw new LocacaoInvalidaException("Não foi possível calcular a duração da locação");
        }

        long segundos = Duration.between(inicio, dataDevolucao).getSeconds();
        long horasCobradas = Math.max(1, (segundos + 3_599) / 3_600);
        return valorHora.multiply(BigDecimal.valueOf(horasCobradas)).setScale(2, RoundingMode.HALF_UP);
    }
}
