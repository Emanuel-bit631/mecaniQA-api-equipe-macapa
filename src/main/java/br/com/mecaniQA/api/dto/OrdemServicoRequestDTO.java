package br.com.mecaniQA.api.dto;

import br.com.mecaniQA.api.model.StatusOrdemServico;
import java.util.List;

public class OrdemServicoRequestDTO {
    private String cliente;
    private String veiculo;
    private StatusOrdemServico status;
    private List<Long> servicosIds;
    private List<ItemPedidoDTO> itensPeca;

    public OrdemServicoRequestDTO(){
    }

    public OrdemServicoRequestDTO(String cliente, String veiculo, StatusOrdemServico status, List<Long> servicosIds, List<ItemPedidoDTO> itensPeca){
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.status = status;
        this.servicosIds = servicosIds;
        this.itensPeca = itensPeca;
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

    public List<Long> getServicosIds(){
        return servicosIds;
    }

    public void setServicosIds(List<Long> servicosIds){
        this.servicosIds = servicosIds;
    }

    public List<ItemPedidoDTO> getItensPeca(){
        return itensPeca;
    }

    public void setItensPeca(List<ItemPedidoDTO> itensPeca){
        this.itensPeca = itensPeca;
    }
}