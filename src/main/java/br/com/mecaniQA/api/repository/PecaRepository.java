package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.model.Peca;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PecaRepository {
    private static PecaRepository instance;
    private final List<Peca> pecas = new ArrayList<>();
    private long proximoId = 1;

    private PecaRepository() {
    }

    public static synchronized PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    public List<Peca> getPecas() {
        return pecas;
    }

    public Optional<Peca> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return pecas.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst();
    }

    public synchronized boolean salvar(Peca peca) {
        if (peca == null || !peca.isValida()) {
            return false;
        }

        if (peca.getId() != null && peca.getId() != 0) {
            return false;
        }

        peca.setId(proximoId++);
        peca.setDataCadastro(LocalDateTime.now());
        peca.setDataUltimaAtualizacao(LocalDateTime.now());

        this.pecas.add(peca);
        return true;
    }

    public boolean deletar(Long id) {
        if (id == null) return false;
        return pecas.removeIf(p -> id.equals(p.getId()));
    }

    public Optional<Peca> buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return Optional.empty();
        }
        return pecas.stream()
                .filter(p -> p.getCodigoBarras() != null && p.getCodigoBarras().equalsIgnoreCase(codigoBarras.trim()))
                .findFirst();
    }

    public synchronized boolean atualizar(Long id, Peca pecaAtualizada) {
        if (id == null || pecaAtualizada == null) {
            return false;
        }
        Optional<Peca> pecaExistenteOpt = buscarPorId(id);
        if (pecaExistenteOpt.isEmpty()) {
            return false;
        }

        Peca pecaExistente = pecaExistenteOpt.get();

        if (pecaAtualizada.getPrecoCusto() > 0) {
            pecaExistente.setPrecoCusto(pecaAtualizada.getPrecoCusto());
        }
        if (pecaAtualizada.getPrecoVenda() > 0) {
            pecaExistente.setPrecoVenda(pecaAtualizada.getPrecoVenda());
        }
        if (pecaAtualizada.getQuantidadeEstoque() != null && pecaAtualizada.getQuantidadeEstoque() >= 0) {
            pecaExistente.setQuantidadeEstoque(pecaAtualizada.getQuantidadeEstoque());
        }
        if (pecaAtualizada.getCodigoBarras() != null && !pecaAtualizada.getCodigoBarras().trim().isEmpty()) {
            pecaExistente.setCodigoBarras(pecaAtualizada.getCodigoBarras());
        }
        if (pecaAtualizada.getFornecedorMarca() != null && !pecaAtualizada.getFornecedorMarca().trim().isEmpty()) {
            pecaExistente.setFornecedorMarca(pecaAtualizada.getFornecedorMarca());
        }
        if (pecaAtualizada.getCategoria() != null) {
            pecaExistente.setCategoria(pecaAtualizada.getCategoria());
        }
        if (pecaAtualizada.getTamanho() != null) {
            pecaExistente.setTamanho(pecaAtualizada.getTamanho());
        }
        if (pecaAtualizada.getCor() != null) {
            pecaExistente.setCor(pecaAtualizada.getCor());
        }

        pecaExistente.setDataUltimaAtualizacao(LocalDateTime.now());
        return true;
    }
}