package br.com.mecaniQA.api.model;

import java.util.ArrayList;
import java.util.List;

public class OrdemServico {
    private Long id;
    private String cliente;
    private String veiculo;
    private StatusOrdemServico status;
    private List<Servico> servicos;
    private List<ItemPedido> itensPeca;

    public OrdemServico(){
        this.status = StatusOrdemServico.ABERTO;
        this.servicos = new ArrayList<>();
        this.itensPeca = new ArrayList<>();
    }

    public OrdemServico(Long id, String cliente, String veiculo, StatusOrdemServico status, List<Servico> servicos , List<ItemPedido> itensPeca){
        this.id = id;
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.status = status != null? status : StatusOrdemServico.ABERTO;
        this.servicos = servicos != null? servicos: new ArrayList<>();
        this.itensPeca = itensPeca != null? itensPeca: new ArrayList<>();
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

    public List<Servico> getServicos(){
        return servicos;
    }

    public void setServicos(List<Servico> servicos){
        this.servicos = servicos;
    }

    public List<ItemPedido> getItensPeca(){
        return itensPeca;
    }

    public void setItensPeca(List<ItemPedido> itensPeca){
        this.itensPeca = itensPeca;
    }

    public Double getValorTotal(){
        double total = 0.0;
        if(servicos != null){
            for (Servico s : servicos){
                if(s.getCustoTabelado() != null){
                    total += s.getCustoTabelado();
                }
            }
        }
        if(itensPeca != null){
            for(ItemPedido item : itensPeca){
                total += item.getSubtotal();
              }
            }
           return total;
        }
      }