package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoBanco;
import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.mapper.ServicoMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.OrdemServico;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.Servico;
import br.com.mecaniQA.api.model.StatusOrdemServico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OrdemServicoRepository
        implements Repository<OrdemServico, Long> {

    private static OrdemServicoRepository instance;

    private OrdemServicoRepository() {
    }

    public static synchronized OrdemServicoRepository getInstance() {
        if (instance == null) {
            instance = new OrdemServicoRepository();
        }
        return instance;
    }

    @Override
    public OrdemServico save(OrdemServico os) {
        validar(os, false);

        String sql = "INSERT INTO ordens_servico "
                + "(cliente, veiculo, status) VALUES (?, ?, ?)";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conexao.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setString(1, os.getCliente());
                    stmt.setString(2, os.getVeiculo());
                    stmt.setString(3, os.getStatus().name());

                    stmt.executeUpdate();

                    try (ResultSet chaves = stmt.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            throw new SQLException(
                                    "MySQL não retornou o ID da ordem de serviço.");
                        }

                        os.setId(chaves.getLong(1));
                    }
                }

                salvarRelacionamentos(conexao, os);
                conexao.commit();

                return os;

            } catch (SQLException e) {
                rollback(conexao);
                throw e;
            } catch (RuntimeException e) {
                rollback(conexao);
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao cadastrar ordem de serviço no MySQL.", e);
        }
    }

    @Override
    public Optional<OrdemServico> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM ordens_servico WHERE id = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            OrdemServico os;

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                os = mapearOrdem(rs);
            }

            carregarRelacionamentos(conexao, os);

            return Optional.of(os);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao buscar ordem de serviço no MySQL.", e);
        }
    }

    @Override
    public List<OrdemServico> findAll() {
        List<OrdemServico> ordens = new ArrayList<>();

        String sql = "SELECT * FROM ordens_servico ORDER BY id";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ordens.add(mapearOrdem(rs));
            }

            for (OrdemServico os : ordens) {
                carregarRelacionamentos(conexao, os);
            }

            return ordens;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao listar ordens de serviço no MySQL.", e);
        }
    }

    @Override
    public OrdemServico update(OrdemServico os) {
        validar(os, true);

        String sql = "UPDATE ordens_servico "
                + "SET cliente = ?, veiculo = ?, status = ? "
                + "WHERE id = ?";

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                    stmt.setString(1, os.getCliente());
                    stmt.setString(2, os.getVeiculo());
                    stmt.setString(3, os.getStatus().name());
                    stmt.setLong(4, os.getId());

                    int alterados = stmt.executeUpdate();

                    if (alterados == 0 && !existePorId(conexao, os.getId())) {
                        throw new NoSuchElementException(
                                "Ordem de serviço não encontrada: " + os.getId());
                    }
                }

                limparRelacionamentos(conexao, os.getId());
                salvarRelacionamentos(conexao, os);

                conexao.commit();

                return os;

            } catch (SQLException e) {
                rollback(conexao);
                throw e;
            } catch (RuntimeException e) {
                rollback(conexao);
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao atualizar ordem de serviço no MySQL.", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        excluir(id);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }

        try (Connection conexao = ConexaoBanco.conectar()) {
            return existePorId(conexao, id);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao verificar ordem de serviço no MySQL.", e);
        }
    }

    private boolean existePorId(Connection conexao, Long id)
            throws SQLException {

        String sql = "SELECT 1 FROM ordens_servico WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private OrdemServico mapearOrdem(ResultSet rs) throws SQLException {
        OrdemServico os = new OrdemServico();

        os.setId(rs.getLong("id"));
        os.setCliente(rs.getString("cliente"));
        os.setVeiculo(rs.getString("veiculo"));
        os.setStatus(
                StatusOrdemServico.valueOf(rs.getString("status")));

        os.setServicos(new ArrayList<>());
        os.setItensPeca(new ArrayList<>());

        return os;
    }

    private void carregarRelacionamentos(
            Connection conexao, OrdemServico os) throws SQLException {

        String sqlServicos =
                "SELECT s.* FROM ordem_servico_servicos oss "
                        + "INNER JOIN servicos s ON s.id = oss.servico_id "
                        + "WHERE oss.ordem_servico_id = ? "
                        + "ORDER BY oss.id";

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sqlServicos)) {

            stmt.setLong(1, os.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    os.getServicos().add(
                            ServicoMapper.fromResultSet(rs));
                }
            }
        }

        String sqlPecas =
                "SELECT p.*, osi.quantidade, osi.preco_unitario "
                        + "FROM ordem_servico_itens osi "
                        + "INNER JOIN pecas p ON p.id = osi.peca_id "
                        + "WHERE osi.ordem_servico_id = ? "
                        + "ORDER BY osi.id";

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sqlPecas)) {

            stmt.setLong(1, os.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Peca peca = PecaMapper.fromResultSet(rs);

                    ItemPedido item = new ItemPedido(
                            peca,
                            rs.getInt("quantidade"),
                            rs.getDouble("preco_unitario")
                    );

                    os.getItensPeca().add(item);
                }
            }
        }
    }

    private void salvarRelacionamentos(
            Connection conexao, OrdemServico os) throws SQLException {

        String sqlServico =
                "INSERT INTO ordem_servico_servicos "
                        + "(ordem_servico_id, servico_id) VALUES (?, ?)";

        if (os.getServicos() != null
                && !os.getServicos().isEmpty()) {

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sqlServico)) {

                for (Servico servico : os.getServicos()) {
                    if (servico == null || servico.getId() == null) {
                        throw new IllegalArgumentException(
                                "Cada serviço da ordem deve possuir um ID.");
                    }

                    stmt.setLong(1, os.getId());
                    stmt.setLong(2, servico.getId());
                    stmt.addBatch();
                }

                stmt.executeBatch();
            }
        }

        String sqlItem =
                "INSERT INTO ordem_servico_itens "
                        + "(ordem_servico_id, peca_id, quantidade, preco_unitario) "
                        + "VALUES (?, ?, ?, ?)";

        if (os.getItensPeca() != null
                && !os.getItensPeca().isEmpty()) {

            try (PreparedStatement stmt =
                         conexao.prepareStatement(sqlItem)) {

                for (ItemPedido item : os.getItensPeca()) {
                    if (item == null
                            || item.getPeca() == null
                            || item.getPeca().getId() == null
                            || item.getQuantidade() == null
                            || item.getQuantidade() <= 0
                            || item.getPrecoUnitario() == null) {

                        throw new IllegalArgumentException(
                                "Dados inválidos nos itens da ordem de serviço.");
                    }

                    stmt.setLong(1, os.getId());
                    stmt.setLong(2, item.getPeca().getId());
                    stmt.setInt(3, item.getQuantidade());
                    stmt.setDouble(4, item.getPrecoUnitario());
                    stmt.addBatch();
                }

                stmt.executeBatch();
            }
        }
    }

    private void limparRelacionamentos(
            Connection conexao, Long id) throws SQLException {

        String sqlServicos =
                "DELETE FROM ordem_servico_servicos "
                        + "WHERE ordem_servico_id = ?";

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sqlServicos)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }

        String sqlItens =
                "DELETE FROM ordem_servico_itens "
                        + "WHERE ordem_servico_id = ?";

        try (PreparedStatement stmt =
                     conexao.prepareStatement(sqlItens)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private int excluir(Long id) {
        if (id == null) {
            return 0;
        }

        try (Connection conexao = ConexaoBanco.conectar()) {
            conexao.setAutoCommit(false);

            try {
                limparRelacionamentos(conexao, id);

                String sql = "DELETE FROM ordens_servico WHERE id = ?";
                int removidos;

                try (PreparedStatement stmt =
                             conexao.prepareStatement(sql)) {

                    stmt.setLong(1, id);
                    removidos = stmt.executeUpdate();
                }

                conexao.commit();
                return removidos;

            } catch (SQLException e) {
                rollback(conexao);
                throw e;
            } catch (RuntimeException e) {
                rollback(conexao);
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Erro ao excluir ordem de serviço no MySQL. "
                            + "Verifique se existem pedidos de peças vinculados.", e);
        }
    }

    private void validar(OrdemServico os, boolean exigirId) {
        if (os == null
                || os.getCliente() == null
                || os.getCliente().trim().isEmpty()
                || os.getVeiculo() == null
                || os.getVeiculo().trim().isEmpty()
                || os.getStatus() == null) {

            throw new IllegalArgumentException(
                    "Cliente, veículo e status são obrigatórios.");
        }

        if (exigirId && (os.getId() == null || os.getId() <= 0)) {
            throw new IllegalArgumentException(
                    "O ID é obrigatório para atualizar a ordem de serviço.");
        }

        if (!exigirId && os.getId() != null && os.getId() != 0) {
            throw new IllegalArgumentException(
                    "Uma nova ordem de serviço não deve possuir ID.");
        }
    }

    private void rollback(Connection conexao) {
        try {
            conexao.rollback();
        } catch (SQLException ignored) {
            // Preserva a exceção original.
        }
    }

    // Métodos mantidos para compatibilidade com o código atual.

    public OrdemServico salvar(OrdemServico os) {
        if (os != null && os.getId() != null && os.getId() != 0) {
            return update(os);
        }

        return save(os);
    }

    public List<OrdemServico> listarTodas() {
        return findAll();
    }

    public Optional<OrdemServico> buscarPorId(Long id) {
        return findById(id);
    }

    public boolean deletar(Long id) {
        return excluir(id) > 0;
    }
}