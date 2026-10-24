
package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoBanco;
import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.model.Peca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PecaRepository implements Repository<Peca, Long> {
    private static PecaRepository instance;

    private PecaRepository() {
    }

    public static synchronized PecaRepository getInstance() {
        if (instance == null) {
            instance = new PecaRepository();
        }
        return instance;
    }

    @Override
    public Peca save(Peca peca) {
        if (peca == null || !peca.isValida() || peca.getCategoria() == null
                || (peca.getId() != null && peca.getId() != 0)) {
            throw new IllegalArgumentException("Dados inválidos para cadastrar peça.");
        }

        String sql = "INSERT INTO pecas "
                + "(nome, codigo_barras, fornecedor_marca, quantidade_estoque, "
                + "preco_custo, preco_venda, data_cadastro, data_ultima_atualizacao, "
                + "categoria, tamanho, cor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime agora = LocalDateTime.now();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, peca.getNome());
            stmt.setString(2, peca.getCodigoBarras());
            stmt.setString(3, peca.getFornecedorMarca());
            stmt.setInt(4, peca.getQuantidadeEstoque());
            stmt.setDouble(5, peca.getPrecoCusto());
            stmt.setDouble(6, peca.getPrecoVenda());
            stmt.setTimestamp(7, Timestamp.valueOf(agora));
            stmt.setTimestamp(8, Timestamp.valueOf(agora));
            stmt.setString(9, peca.getCategoria().name());
            stmt.setString(10, peca.getTamanho());
            stmt.setString(11, peca.getCor());

            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("MySQL não retornou o ID da peça.");
                }
                peca.setId(chaves.getLong(1));
            }

            peca.setDataCadastro(agora);
            peca.setDataUltimaAtualizacao(agora);
            return peca;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao cadastrar peça no MySQL.", e);
        }
    }

    @Override
    public Optional<Peca> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM pecas WHERE id = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(PecaMapper.fromResultSet(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar peça no MySQL.", e);
        }
    }

    @Override
    public List<Peca> findAll() {
        List<Peca> resultado = new ArrayList<>();
        String sql = "SELECT * FROM pecas ORDER BY id";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                resultado.add(PecaMapper.fromResultSet(rs));
            }

            return resultado;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao listar peças no MySQL.", e);
        }
    }

    @Override
    public Peca update(Peca peca) {
        if (peca == null || peca.getId() == null
                || !peca.isValida() || peca.getCategoria() == null) {
            throw new IllegalArgumentException("Dados inválidos para atualizar peça.");
        }

        String sql = "UPDATE pecas SET nome = ?, codigo_barras = ?, "
                + "fornecedor_marca = ?, quantidade_estoque = ?, preco_custo = ?, "
                + "preco_venda = ?, data_ultima_atualizacao = ?, categoria = ?, "
                + "tamanho = ?, cor = ? WHERE id = ?";

        LocalDateTime agora = LocalDateTime.now();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, peca.getNome());
            stmt.setString(2, peca.getCodigoBarras());
            stmt.setString(3, peca.getFornecedorMarca());
            stmt.setInt(4, peca.getQuantidadeEstoque());
            stmt.setDouble(5, peca.getPrecoCusto());
            stmt.setDouble(6, peca.getPrecoVenda());
            stmt.setTimestamp(7, Timestamp.valueOf(agora));
            stmt.setString(8, peca.getCategoria().name());
            stmt.setString(9, peca.getTamanho());
            stmt.setString(10, peca.getCor());
            stmt.setLong(11, peca.getId());

            if (stmt.executeUpdate() == 0 && !existsById(peca.getId())) {
                throw new NoSuchElementException("Peça não encontrada: " + peca.getId());
            }

            peca.setDataUltimaAtualizacao(agora);
            return peca;

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao atualizar peça no MySQL.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        excluir(id);
    }

    private int excluir(Long id) {
        if (id == null) {
            return 0;
        }

        String sql = "DELETE FROM pecas WHERE id = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao excluir peça no MySQL.", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }

        String sql = "SELECT 1 FROM pecas WHERE id = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao verificar peça no MySQL.", e);
        }
    }

    public List<Peca> getPecas() {
        return findAll();
    }

    public Optional<Peca> buscarPorId(Long id) {
        return findById(id);
    }

    public synchronized boolean salvar(Peca peca) {
        if (peca == null || !peca.isValida() || peca.getCategoria() == null
                || (peca.getId() != null && peca.getId() != 0)) {
            return false;
        }

        save(peca);
        return true;
    }

    public boolean deletar(Long id) {
        return excluir(id) > 0;
    }

    public Optional<Peca> buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM pecas WHERE LOWER(codigo_barras) = LOWER(?) LIMIT 1";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(PecaMapper.fromResultSet(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao buscar peça pelo código de barras.", e);
        }
    }

    public synchronized boolean atualizar(Long id, Peca pecaAtualizada) {
        if (id == null || pecaAtualizada == null) {
            return false;
        }

        Optional<Peca> encontrada = findById(id);
        if (encontrada.isEmpty()) {
            return false;
        }

        Peca atual = encontrada.get();

        if (pecaAtualizada.getNome() != null && !pecaAtualizada.getNome().trim().isEmpty()) {
            atual.setNome(pecaAtualizada.getNome());
        }

        if (pecaAtualizada.getPrecoCusto() != null && pecaAtualizada.getPrecoCusto() > 0) {
            atual.setPrecoCusto(pecaAtualizada.getPrecoCusto());
        }

        if (pecaAtualizada.getPrecoVenda() != null && pecaAtualizada.getPrecoVenda() > 0) {
            atual.setPrecoVenda(pecaAtualizada.getPrecoVenda());
        }

        if (pecaAtualizada.getQuantidadeEstoque() != null && pecaAtualizada.getQuantidadeEstoque() >= 0) {
            atual.setQuantidadeEstoque(pecaAtualizada.getQuantidadeEstoque());
        }

        if (pecaAtualizada.getCodigoBarras() != null && !pecaAtualizada.getCodigoBarras().trim().isEmpty()) {
            atual.setCodigoBarras(pecaAtualizada.getCodigoBarras());
        }

        if (pecaAtualizada.getFornecedorMarca() != null && !pecaAtualizada.getFornecedorMarca().trim().isEmpty()) {
            atual.setFornecedorMarca(pecaAtualizada.getFornecedorMarca());
        }

        if (pecaAtualizada.getCategoria() != null) {
            atual.setCategoria(pecaAtualizada.getCategoria());
        }

        if (pecaAtualizada.getTamanho() != null) {
            atual.setTamanho(pecaAtualizada.getTamanho());
        }

        if (pecaAtualizada.getCor() != null) {
            atual.setCor(pecaAtualizada.getCor());
        }

        update(atual);
        return true;
    }
}
