package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.mapper.PecaMapper;
import br.com.mecaniQA.api.model.ItemPedido;
import br.com.mecaniQA.api.model.Peca;
import br.com.mecaniQA.api.model.PedidoPecas;
import br.com.mecaniQA.api.model.StatusPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PedidoPecasRepository
        extends AbstractJdbcRepository<PedidoPecas, Long> {

    private static PedidoPecasRepository instance;

    private PedidoPecasRepository() {
    }

    public static synchronized PedidoPecasRepository getInstance() {
        if (instance == null) {
            instance = new PedidoPecasRepository();
        }
        return instance;
    }

    @Override
    protected RuntimeException criarExcecaoBanco(
            String mensagem, SQLException causa) {
        return new IllegalStateException(mensagem, causa);
    }

    @Override
    public PedidoPecas save(PedidoPecas pedido) {
        prepararDados(pedido);
        validar(pedido, false);

        String sql = "INSERT INTO pedidos_pecas "
                + "(ordem_servico_id, data_pedido, status) "
                + "VALUES (?, ?, ?)";

        return executarComConexao(
                "Erro ao cadastrar pedido de peças no MySQL.",
                conexao -> {
                    conexao.setAutoCommit(false);

                    try {
                        try (PreparedStatement stmt =
                                     prepararStatement(conexao, sql, true)) {
                            stmt.setLong(1, pedido.getOrdemServicoId());
                            stmt.setTimestamp(
                                    2, Timestamp.valueOf(pedido.getDataPedido()));
                            stmt.setString(3, pedido.getStatus().name());
                            stmt.executeUpdate();

                            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                                if (!chaves.next()) {
                                    throw new SQLException(
                                            "MySQL não retornou o ID do pedido de peças.");
                                }
                                pedido.setId(chaves.getLong(1));
                            }
                        }

                        salvarItens(conexao, pedido);
                        conexao.commit();
                        return pedido;

                    } catch (SQLException e) {
                        rollback(conexao);
                        throw e;
                    } catch (RuntimeException e) {
                        rollback(conexao);
                        throw e;
                    }
                }
        );
    }

    @Override
    public Optional<PedidoPecas> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM pedidos_pecas WHERE id = ?";

        return executarComConexao(
                "Erro ao buscar pedido de peças no MySQL.",
                conexao -> {
                    PedidoPecas pedido;

                    try (PreparedStatement stmt =
                                 prepararStatement(conexao, sql, false)) {
                        stmt.setLong(1, id);

                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next()) {
                                return Optional.empty();
                            }
                            pedido = mapearPedido(rs);
                        }
                    }

                    carregarItens(conexao, pedido);
                    return Optional.of(pedido);
                }
        );
    }

    @Override
    public List<PedidoPecas> findAll() {
        String sql = "SELECT * FROM pedidos_pecas ORDER BY id";

        return executarComConexao(
                "Erro ao listar pedidos de peças no MySQL.",
                conexao -> {
                    List<PedidoPecas> pedidos = new ArrayList<>();

                    try (PreparedStatement stmt =
                                 prepararStatement(conexao, sql, false);
                         ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            pedidos.add(mapearPedido(rs));
                        }
                    }

                    for (PedidoPecas pedido : pedidos) {
                        carregarItens(conexao, pedido);
                    }

                    return pedidos;
                }
        );
    }

    @Override
    public PedidoPecas update(PedidoPecas pedido) {
        prepararDados(pedido);
        validar(pedido, true);

        String sql = "UPDATE pedidos_pecas "
                + "SET ordem_servico_id = ?, data_pedido = ?, status = ? "
                + "WHERE id = ?";

        return executarComConexao(
                "Erro ao atualizar pedido de peças no MySQL.",
                conexao -> {
                    conexao.setAutoCommit(false);

                    try {
                        try (PreparedStatement stmt =
                                     prepararStatement(conexao, sql, false)) {
                            stmt.setLong(1, pedido.getOrdemServicoId());
                            stmt.setTimestamp(
                                    2, Timestamp.valueOf(pedido.getDataPedido()));
                            stmt.setString(3, pedido.getStatus().name());
                            stmt.setLong(4, pedido.getId());

                            int alterados = stmt.executeUpdate();

                            if (alterados == 0
                                    && !existePorId(conexao, pedido.getId())) {
                                throw new NoSuchElementException(
                                        "Pedido de peças não encontrado: "
                                                + pedido.getId());
                            }
                        }

                        limparItens(conexao, pedido.getId());
                        salvarItens(conexao, pedido);
                        conexao.commit();
                        return pedido;

                    } catch (SQLException e) {
                        rollback(conexao);
                        throw e;
                    } catch (RuntimeException e) {
                        rollback(conexao);
                        throw e;
                    }
                }
        );
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

        return executarComConexao(
                "Erro ao verificar pedido de peças no MySQL.",
                conexao -> existePorId(conexao, id)
        );
    }

    private PedidoPecas mapearPedido(ResultSet rs) throws SQLException {
        PedidoPecas pedido = new PedidoPecas();

        pedido.setId(rs.getLong("id"));
        pedido.setOrdemServicoId(rs.getLong("ordem_servico_id"));

        Timestamp dataPedido = rs.getTimestamp("data_pedido");
        pedido.setDataPedido(
                dataPedido == null ? null : dataPedido.toLocalDateTime());

        pedido.setStatus(
                StatusPedido.valueOf(rs.getString("status")));

        pedido.setItens(new ArrayList<>());

        return pedido;
    }

    private void carregarItens(
            Connection conexao, PedidoPecas pedido) throws SQLException {

        String sql = "SELECT p.*, "
                + "ppi.quantidade AS quantidade_item, "
                + "ppi.preco_unitario AS preco_unitario_item "
                + "FROM pedido_pecas_itens ppi "
                + "INNER JOIN pecas p ON p.id = ppi.peca_id "
                + "WHERE ppi.pedido_id = ? "
                + "ORDER BY ppi.id";

        try (PreparedStatement stmt = prepararStatement(conexao, sql, false)) {
            stmt.setLong(1, pedido.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Peca peca = PecaMapper.fromResultSet(rs);

                    ItemPedido item = new ItemPedido(
                            peca,
                            rs.getInt("quantidade_item"),
                            rs.getDouble("preco_unitario_item")
                    );

                    pedido.adicionarItem(item);
                }
            }
        }
    }

    private void salvarItens(
            Connection conexao, PedidoPecas pedido) throws SQLException {

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO pedido_pecas_itens "
                + "(pedido_id, peca_id, quantidade, preco_unitario) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = prepararStatement(conexao, sql, false)) {
            for (ItemPedido item : pedido.getItens()) {
                if (item == null
                        || item.getPeca() == null
                        || item.getPeca().getId() == null
                        || item.getQuantidade() == null
                        || item.getQuantidade() <= 0
                        || item.getPrecoUnitario() == null
                        || item.getPrecoUnitario() <= 0) {

                    throw new IllegalArgumentException(
                            "Dados inválidos nos itens do pedido de peças.");
                }

                stmt.setLong(1, pedido.getId());
                stmt.setLong(2, item.getPeca().getId());
                stmt.setInt(3, item.getQuantidade());
                stmt.setDouble(4, item.getPrecoUnitario());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void limparItens(
            Connection conexao, Long pedidoId) throws SQLException {

        String sql = "DELETE FROM pedido_pecas_itens WHERE pedido_id = ?";

        try (PreparedStatement stmt = prepararStatement(conexao, sql, false)) {
            stmt.setLong(1, pedidoId);
            stmt.executeUpdate();
        }
    }

    private boolean existePorId(Connection conexao, Long id)
            throws SQLException {

        String sql = "SELECT 1 FROM pedidos_pecas WHERE id = ?";

        try (PreparedStatement stmt = prepararStatement(conexao, sql, false)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int excluir(Long id) {
        if (id == null) {
            return 0;
        }

        return executarComConexao(
                "Erro ao excluir pedido de peças no MySQL.",
                conexao -> {
                    conexao.setAutoCommit(false);

                    try {
                        limparItens(conexao, id);

                        String sql = "DELETE FROM pedidos_pecas WHERE id = ?";
                        int removidos;

                        try (PreparedStatement stmt =
                                     prepararStatement(conexao, sql, false)) {
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
                }
        );
    }

    private void prepararDados(PedidoPecas pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException(
                    "O pedido de peças não pode ser nulo.");
        }

        if (pedido.getDataPedido() == null) {
            pedido.setDataPedido(LocalDateTime.now());
        }

        if (pedido.getStatus() == null) {
            pedido.setStatus(StatusPedido.ORCANDO);
        }
    }

    private void validar(PedidoPecas pedido, boolean exigirId) {
        if (pedido.getOrdemServicoId() == null) {
            throw new IllegalArgumentException(
                    "O ID da ordem de serviço é obrigatório.");
        }

        if (exigirId && (pedido.getId() == null || pedido.getId() <= 0)) {
            throw new IllegalArgumentException(
                    "O ID é obrigatório para atualizar o pedido de peças.");
        }

        if (!exigirId && pedido.getId() != null && pedido.getId() != 0) {
            throw new IllegalArgumentException(
                    "Um novo pedido de peças não deve possuir ID.");
        }
    }

    private void rollback(Connection conexao) {
        try {
            conexao.rollback();
        } catch (SQLException ignored) {
            // Preserva a exceção original.
        }
    }

    // Métodos mantidos para compatibilidade com o Controller atual.

    public PedidoPecas salvar(PedidoPecas pedido) {
        if (pedido != null && pedido.getId() != null && pedido.getId() != 0) {
            return update(pedido);
        }

        return save(pedido);
    }

    public List<PedidoPecas> listarTodos() {
        return findAll();
    }

    public Optional<PedidoPecas> buscarPorId(Long id) {
        return findById(id);
    }

    public boolean deletar(Long id) {
        return excluir(id) > 0;
    }
}