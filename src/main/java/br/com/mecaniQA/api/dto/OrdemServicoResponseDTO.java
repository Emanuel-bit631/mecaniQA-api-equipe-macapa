package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusOrdemServico;
import java.util.List;

public class OrdemServicoResponseDTO {
    private Long id;
    private String cliente;
    private String veiculo;
    private StatusOrdemServico status;
    private List<ServicoDTO> servicos;
    private List<ItemPedidoDTO> itensPeca;
    private Double valorTotal;

    public OrdemServicoResponseDTO(){
    }

    public OrdemServicoResponseDTO(Long id, String cliente, String veiculo, StatusOrdemServico status, List<ServicoDTO> servicos, List<ItemPedidoDTO> itensPeca, Double valorTotal){
        this.id = id;
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.status = status;
        this.servicos = servicos;
        this.itensPeca = itensPeca;
        this.valorTotal = valorTotal;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getCliente(){
        return cliente;
    }

    public void setCliente(String cliente){
        this.cliente = cliente;
    }

    public String getVeiculo(){
        return veiculo;
    }

    public void setVeiculo(String veiculo){
        this.veiculo = veiculo;
    }

    public StatusOrdemServico getStatus(){
        return status;
    }

    public void setStatus(StatusOrdemServico status){
        this.status = status;
    }

    public List<ServicoDTO> getServicos(){
        return servicos;
    }

    public void setServicos(List<ServicoDTO> servicos){
        this.servicos = servicos;
    }

    public List<ItemPedidoDTO> getItensPeca(){
        return itensPeca;
    }

    public void setItensPeca(List<ItemPedidoDTO> itensPeca){
        this.itensPeca = itensPeca;
    }

    public Double getValorTotal(){
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal){
        this.valorTotal = valorTotal;
    }
}