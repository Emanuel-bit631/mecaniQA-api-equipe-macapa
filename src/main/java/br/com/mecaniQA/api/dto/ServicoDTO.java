package br.com.mecaniQA.api.dto;

public class ServicoDTO {
    private Long id;
    private String nome;
    private Integer tempoEstimadoMinutos;
    private Double custoTabelado;

    public ServicoDTO(){
    }

    public ServicoDTO(Long id, String nome, Integer tempoEstimadoMinutos, Double custoTabelado){
        this.id = id;
        this.nome = nome;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.custoTabelado = custoTabelado;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public Integer getTempoEstimadoMinutos(){
        return tempoEstimadoMinutos;
    }

    public void setTempoEstimadoMinutos(Integer tempoEstimadoMinutos){
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
    }

    public Double getCustoTabelado(){
        return custoTabelado;
    }

    public void setCustoTabelado(Double custoTabelado){
        this.custoTabelado = custoTabelado;
    }
}