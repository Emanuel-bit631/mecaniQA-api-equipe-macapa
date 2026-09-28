package br.com.mecaniQA.api.dto;

public class ItemPedidoDTO {
    private Long pecaId;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;

    public ItemPedidoDTO(){
    }

    public ItemPedidoDTO(Long pecaId, Integer quantidade, Double precoUnitario, Double subtotal){
        this.pecaId = pecaId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    public Long getPecaId(){
        return pecaId;
    }

    public void setPecaId(Long pecaId){
        this.pecaId = pecaId;
    }

    public Integer getQuantidade(){
        return quantidade;
    }

    public void setQuantidade(Integer quantidade){
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario(){
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario){
        this.precoUnitario = precoUnitario;
    }

    public Double getSubtotal(){
        return subtotal;
    }

    public void setSubtotal(Double subtotal){
        this.subtotal = subtotal;
    }
}