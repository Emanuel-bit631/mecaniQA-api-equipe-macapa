package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Servico;
import java.util.ArrayList;
import java.util.List;

public class ServicoRepository {
    private static ServicoRepository instance;
    private final List<Servico> servicos = new ArrayList<>();

    private ServicoRepository(){}

    public static synchronized ServicoRepository getInstance(){
        if(instance == null){
            instance = new ServicoRepository();
        }
        return instance;
    }

    public List<Servico> getServicos(){
        return servicos;
    }
}
