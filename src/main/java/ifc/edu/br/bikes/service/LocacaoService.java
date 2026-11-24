package ifc.edu.br.bikes.service;

import ifc.edu.br.bikes.dto.LocacaoCreateDto;
import ifc.edu.br.bikes.dto.LocacaoResponseDto;
import ifc.edu.br.bikes.exception.BicicletaIndisponivelException;
import ifc.edu.br.bikes.exception.LocacaoNotFoundException;
import ifc.edu.br.bikes.entity.Bicicleta;
import ifc.edu.br.bikes.entity.Cliente;
import ifc.edu.br.bikes.model.Locacao;
import ifc.edu.br.bikes.model.LocacaoStatus;
import ifc.edu.br.bikes.repository.BicicletaRepository;
import ifc.edu.br.bikes.repository.ClienteRepository;
import ifc.edu.br.bikes.repository.LocacaoRepository;
import ifc.edu.br.bikes.dto.mapper.LocacaoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class LocacaoService {

    private final LocacaoRepository locacaoRepository;
    private final ClienteRepository clienteRepository;
    private final BicicletaRepository bicicletaRepository;
    private final LocacaoMapper locacaoMapper;

    public LocacaoService(LocacaoRepository locacaoRepository,
                          ClienteRepository clienteRepository,
                          BicicletaRepository bicicletaRepository,
                          LocacaoMapper locacaoMapper) {
        this.locacaoRepository = locacaoRepository;
        this.clienteRepository = clienteRepository;
        this.bicicletaRepository = bicicletaRepository;
        this.locacaoMapper = locacaoMapper;
    }

    @Transactional
    public LocacaoResponseDto solicitarLocacao(LocacaoCreateDto dto) {
        // valida cliente e bicicleta existirem
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Bicicleta bicicleta = bicicletaRepository.findById(dto.getBicicletaId())
                .orElseThrow(() -> new IllegalArgumentException("Bicicleta não encontrada"));
        // verifica disponibilidade da bicicleta no período solicitado
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dataPrevista = dto.getDataPrevistaDevolucao();
        // período de uso esperado: now até dataPrevista
        List<LocacaoStatus> active = Arrays.asList(LocacaoStatus.SOLICITADA, LocacaoStatus.EM_PROGRESSO);
        boolean conflito = !locacaoRepository.findConflictingLocacoes(bicicleta.getId(), now, dataPrevista, active).isEmpty();
        if (conflito) {
            throw new BicicletaIndisponivelException("Bicicleta não está disponível no período solicitado");
        }
        // cria locação
        Locacao locacao = locacaoMapper.fromCreateDto(dto);
        locacao.setCliente(cliente);
        locacao.setBicicleta(bicicleta);
        locacao.setDataPrevistaDevolucao(dataPrevista);
        // status já definido como SOLICITADA pelo mapper
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional
    public LocacaoResponseDto iniciarLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        if (locacao.getStatus() != LocacaoStatus.SOLICITADA) {
            throw new IllegalStateException("Locação não está no status SOLICITADA");
        }
        locacao.setStatus(LocacaoStatus.EM_PROGRESSO);
        locacao.setDataRetirada(LocalDateTime.now());
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional
    public LocacaoResponseDto finalizarLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        if (locacao.getStatus() != LocacaoStatus.EM_PROGRESSO) {
            throw new IllegalStateException("Locação não está em progresso");
        }
        locacao.setStatus(LocacaoStatus.CONCLUIDA);
        locacao.setDataDevolucao(LocalDateTime.now());
        locacao = locacaoRepository.save(locacao);
        return locacaoMapper.toResponseDto(locacao);
    }

    @Transactional
    public LocacaoResponseDto cancelarLocacao(Long id) {
        Locacao locacao = locacaoRepository.findById(id)
                .orElseThrow(() -> new LocacaoNotFoundException("Locação não encontrada"));
        if (locacao.getStatus() == LocacaoStatus.CONCLUIDA) {
            throw new IllegalStateException("Locação já concluída, não pode ser cancelada");
        }
        locacao.setStatus(LocacaoStatus.CANCELADA);
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
}
