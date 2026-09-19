package br.com.mecaniQA.api.model;

public class ItemPedido {
    private Peca peca;
    private Integer quantidade;
    private Double precoUnitario;

    public ItemPedido(){

    }

    public ItemPedido(Peca peca, Integer quantidade, Double precoUnitario){
        this.peca = peca;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Peca getPeca(){
        return peca;
    }

    public void setPeca(Peca peca){
        this.peca = peca;
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

    public Double getSubtotal() {
        if(precoUnitario != null && quantidade != null){
            return precoUnitario * quantidade;
        }
        return 0.0;
    }
}