package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.model.Servico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    public static Servico fromResultSet(ResultSet rs) throws SQLException {
        Servico servico = new Servico();

        servico.setId(rs.getLong("id"));
        servico.setNome(rs.getString("nome"));
        servico.setTempoEstimadoMinutos(
                rs.getInt("tempo_estimado_minutos")
        );
        servico.setCustoTabelado(rs.getDouble("custo_tabelado"));

        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        servico.setDataCriacao(
                dataCriacao == null ? null : dataCriacao.toLocalDateTime()
        );

        Timestamp dataAtualizacao =
                rs.getTimestamp("data_ultima_atualizacao");
        servico.setDataUltimaAtualizacao(
                dataAtualizacao == null
                        ? null
                        : dataAtualizacao.toLocalDateTime()
        );

        return servico;
    }
}
