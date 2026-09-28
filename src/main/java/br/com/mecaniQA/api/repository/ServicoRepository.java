package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Servico;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicoRepository {
    private static ServicoRepository instance;
    private final List<Servico> servicos = new ArrayList<>();
    private long proximoId = 1;

    private ServicoRepository() {}

    public static synchronized ServicoRepository getInstance() {
        if (instance == null) {
            instance = new ServicoRepository();
        }
        return instance;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public synchronized boolean salvar(Servico servico) {
        if (servico == null || !servico.isValido()) {
            return false;
        }

        if (servico.getId() != null && servico.getId() != 0) {
            return false;
        }

        servico.setId(proximoId++);
        servico.setDataCriacao(LocalDateTime.now());
        servico.setDataUltimaAtualizacao(LocalDateTime.now());

        this.servicos.add(servico);
        return true;
    }

    public Optional<Servico> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return servicos.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst();
    }

    public synchronized boolean atualizar(Long id, Servico servicoAtualizado) {
        if (id == null || servicoAtualizado == null) {
            return false;
        }

        Optional<Servico> servicoExistenteOpt = buscarPorId(id);
        if (servicoExistenteOpt.isEmpty()) {
            return false;
        }

        Servico servicoExistente = servicoExistenteOpt.get();

        if (servicoAtualizado.getNome() != null && !servicoAtualizado.getNome().trim().isEmpty()) {
            servicoExistente.setNome(servicoAtualizado.getNome());
        }
        if (servicoAtualizado.getTempoEstimadoMinutos() != null && servicoAtualizado.getTempoEstimadoMinutos() > 0) {
            servicoExistente.setTempoEstimadoMinutos(servicoAtualizado.getTempoEstimadoMinutos());
        }
        if (servicoAtualizado.getCustoTabelado() > 0) {
            servicoExistente.setCustoTabelado(servicoAtualizado.getCustoTabelado());
        }

        servicoExistente.setDataUltimaAtualizacao(LocalDateTime.now());
        return true;
    }

    public boolean deletar(Long id) {
        if (id == null) return false;
        return servicos.removeIf(s -> id.equals(s.getId()));
    }
}