
package br.com.mecaniQA.api.service;

import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.exception.DadosInvalidosException;
import br.com.mecaniQA.api.exception.RecursoNaoEncontradoException;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ServicoDTO buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .map(this::paraDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Serviço não encontrado para o ID informado: " + id
                ));
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

    public ServicoDTO atualizar(Long id, ServicoDTO dto) {
        if (dto == null) {
            throw new DadosInvalidosException(
                    "Os dados do serviço não foram informados."
            );
        }

        Servico existente = servicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Serviço não encontrado para o ID informado: " + id
                ));

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

        return paraDTO(servicoRepository.update(existente));
    }

    public void deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException(
                    "Serviço não encontrado para o ID informado: " + id
            );
        }

        servicoRepository.deleteById(id);
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
