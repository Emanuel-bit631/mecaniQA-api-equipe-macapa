package br.com.mecaniQA.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Servico {
    private Long id;
    private String nome;
    private Integer tempoEstimadoMinutos;
    private Double custoTabelado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataUltimaAtualizacao;

    public Servico() {
    }

    public Servico(Long id, String nome, Integer tempoEstimadoMinutos, Double custoTabelado, LocalDateTime dataCriacao, LocalDateTime dataUltimaAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.custoTabelado = custoTabelado;
        this.dataCriacao = dataCriacao;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }
    public void setTempoEstimadoMinutos(Integer tempoEstimadoMinutos) {
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
    }

    public Double getCustoTabelado() {
        return custoTabelado;
    }
    public void setCustoTabelado(Double custoTabelado) {
        this.custoTabelado = custoTabelado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public boolean isValido() {
        return nome != null && !nome.trim().isEmpty() &&
                tempoEstimadoMinutos != null && tempoEstimadoMinutos > 0 &&
                custoTabelado > 0;
    }
}