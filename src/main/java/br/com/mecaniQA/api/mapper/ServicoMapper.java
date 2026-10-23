
package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.model.Servico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ServicoMapper {

    public static Servico fromResultSet(ResultSet rs) throws SQLException {
        Servico servico = new Servico();

        servico.setId(rs.getLong("id"));
        servico.setNome(rs.getString("nome"));
        servico.setTempoEstimadoMinutos(
                rs.getInt("tempo_estimado_minutos")
        );
        servico.setCustoTabelado(
                rs.getDouble("custo_tabelado")
        );

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
