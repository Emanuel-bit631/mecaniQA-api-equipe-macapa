
package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository =
            ServicoRepository.getInstance();

    public List<ServicoDTO> listarTodos() {
        return servicoRepository.findAll()
                .stream()
                .map(this::paraDTO)
                .toList();
    }

    public Optional<ServicoDTO> buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .map(this::paraDTO);
    }

    public ServicoDTO cadastrar(ServicoDTO dto) {
        if (dto == null) {
            throw new DadosInvalidosException(
                    "Os dados do serviço não foram informados."
            );
        }

        Servico servico = paraEntidade(dto);

        if (!servico.isValido()) {
            throw new DadosInvalidosException(
                    "Dados inválidos para cadastrar serviço."
            );
        }

        return paraDTO(servicoRepository.save(servico));
    }

    public Optional<ServicoDTO> atualizar(Long id, ServicoDTO dto) {
        if (dto == null) {
            throw new DadosInvalidosException(
                    "Os dados do serviço não foram informados."
            );
        }

        Optional<Servico> existenteOpt =
                servicoRepository.findById(id);

        if (existenteOpt.isEmpty()) {
            return Optional.empty();
        }

        Servico existente = existenteOpt.get();

        if (dto.getNome() != null
                && !dto.getNome().trim().isEmpty()) {
            existente.setNome(dto.getNome());
        }

        if (dto.getTempoEstimadoMinutos() != null
                && dto.getTempoEstimadoMinutos() > 0) {
            existente.setTempoEstimadoMinutos(
                    dto.getTempoEstimadoMinutos()
            );
        }

        if (dto.getCustoTabelado() != null
                && dto.getCustoTabelado() > 0) {
            existente.setCustoTabelado(dto.getCustoTabelado());
        }

        if (!existente.isValido()) {
            throw new DadosInvalidosException(
                    "Dados inválidos para atualizar serviço."
            );
        }

        return Optional.of(
                paraDTO(servicoRepository.update(existente))
        );
    }

    public boolean deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            return false;
        }

        servicoRepository.deleteById(id);
        return true;
    }

    private Servico paraEntidade(ServicoDTO dto) {
        Servico servico = new Servico();

        servico.setNome(dto.getNome());
        servico.setTempoEstimadoMinutos(
                dto.getTempoEstimadoMinutos()
        );
        servico.setCustoTabelado(dto.getCustoTabelado());

        return servico;
    }

    private ServicoDTO paraDTO(Servico servico) {
        ServicoDTO dto = new ServicoDTO();

        dto.setId(servico.getId());
        dto.setNome(servico.getNome());
        dto.setTempoEstimadoMinutos(
                servico.getTempoEstimadoMinutos()
        );
        dto.setCustoTabelado(servico.getCustoTabelado());
        dto.setDataCriacao(servico.getDataCriacao());
        dto.setDataUltimaAtualizacao(
                servico.getDataUltimaAtualizacao()
        );
        dto.setValido(servico.isValido());

        return dto;
    }
}
