package br.com.mecaniQA.api.dto;

import java.util.List;

public class PedidoPecaRequestDTO {
    private Long ordemServicoId;
    private List<ItemPedidoDTO> itens;

    public PedidoPecaRequestDTO() {
    }

    public PedidoPecaRequestDTO(Long ordemServicoId, List<ItemPedidoDTO> itens) {
        this.ordemServicoId = ordemServicoId;
        this.itens = itens;
    }

    public Long getOrdemServicoId() {
        return ordemServicoId;
    }

    public void setOrdemServicoId(Long ordemServicoId) {
        this.ordemServicoId = ordemServicoId;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }
}