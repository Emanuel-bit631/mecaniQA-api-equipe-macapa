package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.PecaRequestDTO;
import br.com.mecaniQA.api.dto.PecaResponseDTO;
import br.com.mecaniQA.api.model.CategoriaPeca;
import br.com.mecaniQA.api.model.Peca;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PecaMapper {

    public static Peca toEntity(PecaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Peca peca = new Peca();
        peca.setNome(dto.getNome());
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

        PecaResponseDTO dto = new PecaResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setCodigoBarras(entity.getCodigoBarras());
        dto.setFornecedorMarca(entity.getFornecedorMarca());
        dto.setQuantidadeEstoque(entity.getQuantidadeEstoque());
        dto.setPrecoCusto(entity.getPrecoCusto());
        dto.setPrecoVenda(entity.getPrecoVenda());
        dto.setDataCadastro(entity.getDataCadastro());
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());
        dto.setCategoria(entity.getCategoria());
        dto.setTamanho(entity.getTamanho());
        dto.setCor(entity.getCor());
        dto.setValida(entity.isValida());

        return dto;
    }

    public static Peca fromResultSet(ResultSet rs) throws SQLException {
        Peca peca = new Peca();

        peca.setId(rs.getLong("id"));
        peca.setNome(rs.getString("nome"));
        peca.setCodigoBarras(rs.getString("codigo_barras"));
        peca.setFornecedorMarca(rs.getString("fornecedor_marca"));
        peca.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        peca.setPrecoCusto(rs.getDouble("preco_custo"));
        peca.setPrecoVenda(rs.getDouble("preco_venda"));

        Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
        peca.setDataCadastro(
                dataCadastro == null ? null : dataCadastro.toLocalDateTime()
        );

        Timestamp dataAtualizacao =
                rs.getTimestamp("data_ultima_atualizacao");
        peca.setDataUltimaAtualizacao(
                dataAtualizacao == null
                        ? null
                        : dataAtualizacao.toLocalDateTime()
        );

        String categoria = rs.getString("categoria");
        peca.setCategoria(
                categoria == null ? null : CategoriaPeca.valueOf(categoria)
        );

        peca.setTamanho(rs.getString("tamanho"));
        peca.setCor(rs.getString("cor"));

        return peca;
    }
}
