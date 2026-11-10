package ifc.edu.br.bikes.service;

import ifc.edu.br.bikes.dto.ClienteCreateDto;
import ifc.edu.br.bikes.dto.ClienteResponseDto;
import ifc.edu.br.bikes.dto.mapper.ClienteMapper;
import ifc.edu.br.bikes.entity.Cliente;
import ifc.edu.br.bikes.entity.Usuario;
import ifc.edu.br.bikes.exception.CpfUniqueViolationException;
import ifc.edu.br.bikes.exception.EntityNotFoundExcpetion;
import ifc.edu.br.bikes.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioService usuarioService;

    @Transactional
    public ClienteResponseDto create(ClienteCreateDto dto, Long usuarioId) {
        String cpfLimpo = dto.cpf().replaceAll("\\D", "");

        if (clienteRepository.existsByCpf(cpfLimpo)) {
            throw new CpfUniqueViolationException(String.format("CPF '%s' já cadastrado no sistema", dto.cpf()));
        }

        if (clienteRepository.existsByUsuarioId(usuarioId)) {
            throw new CpfUniqueViolationException(String.format("Usuário id=%d já possui cliente cadastrado", usuarioId));
        }

        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioId);
            Cliente cliente = clienteMapper.toCliente(dto);
            cliente.setCpf(cpfLimpo);
            cliente.setUsuario(usuario);
            return clienteMapper.toResponse(clienteRepository.save(cliente));
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("CPF '%s' já cadastrado no sistema", dto.cpf()));
        }
    }

    @Transactional
    public ClienteResponseDto getById(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Cliente id=%d não encontrado", id)));
    }

    @Transactional
    public ClienteResponseDto getByUsuarioId(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .map(clienteMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Cliente não encontrado para o usuário id=%d", usuarioId)));
    }

    @Transactional
    public List<ClienteResponseDto> getAll() {
        return clienteMapper.toResponse(clienteRepository.findAll());
    }
}
