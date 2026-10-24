package br.com.mecaniQA.api.mapper;

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