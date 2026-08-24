package br.com.mecaniQA.api.model;

import java.time.LocalDateTime;

public class Peca {
    private long id;
    private String codigoBarras;
    private String fornecedorMarca;
    private Integer quantidadeEstoque;
    private double precoCusto;
    private double precoVenda;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataUltimaAtualizacao;
    private CategoriaPeca categoria;

    private String tamanho;
    private String cor;

    public Peca(){
    }

    public Peca(long id, String codigoBarras, String fornecedorMarca, Integer quantidadeEstoque, double precoCusto, double precoVenda, LocalDateTime dataCadastro, LocalDateTime dataUltimaAtualizacao, CategoriaPeca categoria){
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.fornecedorMarca = fornecedorMarca;
        this.quantidadeEstoque = quantidadeEstoque;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.categoria = categoria;
        this.dataCadastro = LocalDateTime.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getCodigoBarras(){
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras){
        this.codigoBarras = codigoBarras;
    }

    public String getFornecedorMarca(){
        return fornecedorMarca;
    }

    public void setFornecedorMarca(String fornecedorMarca){
        this.fornecedorMarca = fornecedorMarca;
    }

    public Integer getQuantidadeEstoque(){
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque){
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public double getPrecoCusto(){
        return precoCusto;
    }

    public void setPrecoCusto(double precoCusto){
        this.precoCusto = precoCusto;
    }

    public double getPrecoVenda(){
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda){
        this.precoVenda = precoVenda;
    }

    public LocalDateTime getDataCadastro(){
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro){
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataUltimaAtualizacao(){
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao){
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public CategoriaPeca getCategoria(){
        return categoria;
    }

    public void setCategoria(CategoriaPeca categoria){
        this.categoria = categoria;
    }

    public String getTamanho(){
        return tamanho;
    }

    public void setTamanho(String tamanho){
        this.tamanho = tamanho;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor){
        this.cor = cor;
    }
}
