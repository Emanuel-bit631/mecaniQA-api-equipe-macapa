package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.model.Servico;

import java.time.LocalDateTime;

public class ServicoMapper {

    public static Servico toEntity(ServicoDTO dto) {
        if (dto == null) {
            return null;
        }
        Servico servico = new Servico();
        servico.setId(dto.getId());
        servico.setNome(dto.getNome());
        servico.setTempoEstimadoMinutos(dto.getTempoEstimadoMinutos());
        servico.setCustoTabelado(dto.getCustoTabelado());
        servico.setDataCriacao(LocalDateTime.now());
        return servico;
    }

    public static ServicoDTO toDTO(Servico entity) {
        if (entity == null) {
            return null;
        }
        return new ServicoDTO(
                entity.getId(),
                entity.getNome(),
                entity.getTempoEstimadoMinutos(),
                entity.getCustoTabelado()
        );
    }
}