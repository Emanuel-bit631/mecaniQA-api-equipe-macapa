package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Peca;
import java.util.ArrayList;
import java.util.List;

public class PecaRepository {
    private static PecaRepository instance;
    private final List<Peca> pecas = new ArrayList<>();

    private PecaRepository() {}

    public static synchronized PecaRepository getInstance(){
        if(instance == null){
            instance = new PecaRepository();
        }
        return instance;
    }

    public List<Peca> getPecas(){
        return pecas;
    }
}
