
package br.com.mecaniQA.api.mapper;

 oat3-jdbc
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

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
import br.com.mecaniQA.api.model.Peca;

import java.time.LocalDateTime;

public class PecaMapper {

    public static Peca toEntity(PecaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Peca peca = new Peca();
        peca.setCodigoBarras(dto.getCodigoBarras());
        peca.setFornecedorMarca(dto.getFornecedorMarca());
        peca.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        peca.setPrecoCusto(dto.getPrecoCusto());
        peca.setPrecoVenda(dto.getPrecoVenda());
        peca.setCategoria(dto.getCategoria());
        peca.setTamanho(dto.getTamanho());
        peca.setCor(dto.getCor());
        peca.setDataCadastro(LocalDateTime.now());
        return peca;
    }

    public static PecaResponseDTO toDTO(Peca entity) {
        if (entity == null) {
            return null;
        }

        return new PecaResponseDTO(
                entity.getId(),
                entity.getCodigoBarras(),
                entity.getFornecedorMarca(),
                entity.getQuantidadeEstoque(),
                entity.getPrecoCusto(),
                entity.getPrecoVenda(),
                entity.getDataCadastro(),
                entity.getDataUltimaAtualizacao(),
                entity.getCategoria(),
                entity.getTamanho(),
                entity.getCor()
        );
    }
}
 main
