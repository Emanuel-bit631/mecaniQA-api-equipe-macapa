package br.com.mecaniQA.api.builder;

import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.model.StatusOrdemServico;

import java.util.ArrayList;
import java.util.List;

public class OrdemServicoBuilder {
    private Long id;
    private String cliente;
    private String veiculo;
    private StatusOrdemServico status = StatusOrdemServico.ABERTO;
    private List<Servico> servicos = new ArrayList<>();
    private List<ItemPedido> itensPeca = new ArrayList<>();

    public OrdemServicoBuilder(){
    }

    public OrdemServicoBuilder comId(Long id){
        this.id = id;
        return this;
    }

    public OrdemServicoBuilder comCliente(String cliente){
        this.cliente = cliente;
        return this;
    }

    public OrdemServicoBuilder comVeiculo(String veiculo){
        this.veiculo = veiculo;
        return this;
    }

    public OrdemServicoBuilder comStatus(StatusOrdemServico status){
        if(status != null){
            this.status = status;
        }
        return this;
    }

    public OrdemServicoBuilder adicionarServico(Servico servico){
        if(servico != null){
            this.servicos.add(servico);
        }
        return this;
    }

    public OrdemServicoBuilder comServicos(List<Servico> servicos){
        if(servicos != null){
            this.servicos = servicos;
        }
        return this;
    }

    public OrdemServicoBuilder adicionarItemPeca(ItemPedido item){
        if(item != null){
            this.itensPeca.add(item);
        }
        return this;
    }

    public OrdemServicoBuilder comItensPeca(List<ItemPedido> itensPeca){
        if(itensPeca != null){
            this.itensPeca = itensPeca;
        }
        return this;
    }

    public OrdemServico build() {
        return new OrdemServico(
                this.id,
                this.cliente,
                this.veiculo,
                this.status,
                this.servicos,
                this.itensPeca
        );
    }
}