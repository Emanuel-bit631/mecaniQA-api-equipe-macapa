package br.com.mecaniQA.api.mapper;

import br.com.mecaniQA.api.dto.ItemPedidoDTO;
import br.com.mecaniQA.api.dto.OrdemServicoResponseDTO;
import br.com.mecaniQA.api.dto.ServicoDTO;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Servico;

import java.util.ArrayList;
import java.util.List;

public class OrdemServicoMapper {

    public static OrdemServicoResponseDTO toDTO(OrdemServico entity) {
        if (entity == null) {
            return null;
        }

        List<ServicoDTO> servicosDTO = new ArrayList<>();

        if (entity.getServicos() != null) {
            for (Servico servico : entity.getServicos()) {
                servicosDTO.add(
                        new ServicoDTO(
                                servico.getId(),
                                servico.getNome(),
                                servico.getTempoEstimadoMinutos(),
                                servico.getCustoTabelado()
                        )
                );
            }
        }

        List<ItemPedidoDTO> itensDTO = new ArrayList<>();

        if (entity.getItensPeca() != null) {
            for (ItemPedido item : entity.getItensPeca()) {
                Long pecaId = item.getPeca() != null
                        ? item.getPeca().getId()
                        : null;

                itensDTO.add(
                        new ItemPedidoDTO(
                                pecaId,
                                item.getQuantidade(),
                                item.getPrecoUnitario(),
                                item.getSubtotal()
                        )
                );
            }
        }

        return new OrdemServicoResponseDTO(
                entity.getId(),
                entity.getCliente(),
                entity.getVeiculo(),
                entity.getStatus(),
                servicosDTO,
                itensDTO,
                entity.getValorTotal()
        );
    }
}