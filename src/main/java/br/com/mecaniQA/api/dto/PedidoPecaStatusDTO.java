package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusPedido;

public class PedidoPecaStatusDTO {
    private StatusPedido status;

    public PedidoPecaStatusDTO() {
    }

    public PedidoPecaStatusDTO(StatusPedido status) {
        this.status = status;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
}
