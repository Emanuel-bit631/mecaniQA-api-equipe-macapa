package br.com.mecaniQA.api.model;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PedidoPecas {
    private Long id;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private List<ItemPedido> itens;

    public PedidoPecas(){
        this.dataPedido = LocalDateTime.now();
        this.status = StatusPedido.ORCANDO;
        this.itens = new ArrayList<>();
    }

    public PedidoPecas(Long id, LocalDateTime dataPedido, StatusPedido status, List<ItemPedido> itens){
        this.id = id;
        this.dataPedido = dataPedido != null? dataPedido: LocalDateTime.now();
        this.status = status != null? status: StatusPedido.ORCANDO;
        this.itens = itens != null? itens: new ArrayList<>();
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public LocalDateTime getDataPedido(){
        return dataPedido;
    }

    public void setDataPedido( LocalDateTime dataPedido){
        this.dataPedido = dataPedido;
    }

    public StatusPedido getStatus(){
        return status;
    }

    public void setStatus(StatusPedido status){
        this.status = status;
    }

    public List<ItemPedido> getItens(){
        return itens;
    }

    public void setItens(java.util.List<ItemPedido> itens){
        this.itens = itens;
    }

    public Double getValorTotal(){
        double total = 0.0;
        if(itens != null){
            for (ItemPedido item : itens){
                total += item.getSubtotal();
            }
        }
        return total;
    }

    public void adicionarItem(ItemPedido item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        if (item != null) {
            this.itens.add(item);
        }
    }

    public void adicionarItens(List<ItemPedido> novosItens) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
            this.itens.addAll(novosItens);
    }
}