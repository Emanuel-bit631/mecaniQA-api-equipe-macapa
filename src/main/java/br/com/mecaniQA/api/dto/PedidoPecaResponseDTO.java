package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusPedido;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoPecaResponseDTO {
    private Long id;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private List<ItemPedidoDTO> itens;
    private Double valorTotal;

    public PedidoPecaResponseDTO() {
    }

    public PedidoPecaResponseDTO(Long id, LocalDateTime dataPedido, StatusPedido status, List<ItemPedidoDTO> itens, Double valorTotal) {
        this.id = id;
        this.dataPedido = dataPedido;
        this.status = status;
        this.itens = itens;
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
