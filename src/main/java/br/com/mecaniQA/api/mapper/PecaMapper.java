
package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.CategoriaPeca;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PecaMapper {

    public static Peca fromResultSet(ResultSet rs) throws SQLException {
        Peca peca = new Peca();

        peca.setId(rs.getLong("id"));
        peca.setNome(rs.getString("nome"));
        peca.setCodigoBarras(rs.getString("codigo_barras"));
        peca.setFornecedorMarca(rs.getString("fornecedor_marca"));
        peca.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        peca.setPrecoCusto(rs.getDouble("preco_custo"));
        peca.setPrecoVenda(rs.getDouble("preco_venda"));

        peca.setDataCadastro(
                rs.getTimestamp("data_cadastro").toLocalDateTime()
        );

        peca.setDataUltimaAtualizacao(
                rs.getTimestamp("data_ultima_atualizacao").toLocalDateTime()
        );

        peca.setCategoria(
                CategoriaPeca.valueOf(rs.getString("categoria"))
        );

        peca.setTamanho(rs.getString("tamanho"));
        peca.setCor(rs.getString("cor"));

        return peca;
    }
}
