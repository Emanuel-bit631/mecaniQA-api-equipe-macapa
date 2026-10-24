package br.com.mecaniQA.api.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime dataCriacao;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime dataUltimaAtualizacao;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean valido;

    public void setCustoTabelado(Double custoTabelado){
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

    public void setDataUltimaAtualizacao(
            LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public Boolean getValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }
}