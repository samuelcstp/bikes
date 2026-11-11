package ifc.edu.br.bikes.service;

import ifc.edu.br.bikes.dto.BicicletaRequestDTO;
import ifc.edu.br.bikes.dto.BicicletaResponseDTO;
import ifc.edu.br.bikes.dto.mapper.BicicletaMapper;
import ifc.edu.br.bikes.entity.Bicicleta;
import ifc.edu.br.bikes.exception.CodigoUniqueViolationException;
import ifc.edu.br.bikes.exception.EntityNotFoundExcpetion;
import ifc.edu.br.bikes.repository.BicicletaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BicicletaService {

    private final BicicletaRepository bicicletaRepository;
    private final BicicletaMapper bicicletaMapper;

    @Transactional
    public BicicletaResponseDTO create(BicicletaRequestDTO request) {
        if (bicicletaRepository.existsByCodigo(request.codigo())) {
            throw new CodigoUniqueViolationException(String.format("Bicicleta com código '%s' já cadastrada no sistema", request.codigo()));
        }

        try {
            Bicicleta bicicleta = bicicletaMapper.toBicicleta(request);
            return bicicletaMapper.toResponse(bicicletaRepository.save(bicicleta));
        } catch (DataIntegrityViolationException ex) {
            throw new CodigoUniqueViolationException(String.format("Bicicleta com código '%s' já cadastrada no sistema", request.codigo()));
        }
    }

    @Transactional
    public BicicletaResponseDTO getByCodigo(String codigo) {
        return bicicletaRepository.findByCodigo(codigo)
                .map(bicicletaMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Bicicleta com código '%s' não encontrada", codigo)));
    }

    @Transactional
    public List<BicicletaResponseDTO> getAll() {
        return bicicletaMapper.toResponse(bicicletaRepository.findAll());
    }

    @Transactional
    public BicicletaResponseDTO update(String codigo, BicicletaRequestDTO request) {
        Bicicleta bicicleta = bicicletaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Bicicleta com código '%s' não encontrada", codigo)));

        if (!request.codigo().equals(codigo) && bicicletaRepository.existsByCodigo(request.codigo())) {
            throw new CodigoUniqueViolationException(String.format("Bicicleta com código '%s' já cadastrada no sistema", request.codigo()));
        }

        bicicleta.setCodigo(request.codigo());
        bicicleta.setStatus(request.status());

        return bicicletaMapper.toResponse(bicicletaRepository.save(bicicleta));
    }

    @Transactional
    public void delete(String codigo) {
        Bicicleta bicicleta = bicicletaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundExcpetion(String.format("Bicicleta com código '%s' não encontrada", codigo)));

        bicicletaRepository.delete(bicicleta);
    }
}
