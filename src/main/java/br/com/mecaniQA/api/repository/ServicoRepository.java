
package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.mapper.ServicoMapper;
import br.com.mecaniQA.api.model.Servico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicoRepository extends AbstractJdbcRepository<Servico, Long> {

    private static ServicoRepository instance;

    private ServicoRepository() {
    }

    public static synchronized ServicoRepository getInstance() {
        if (instance == null) {
            instance = new ServicoRepository();
        }
        return instance;
    }

    @Override
    protected RuntimeException criarExcecaoBanco(
            String mensagem, SQLException causa) {
        return new IllegalStateException(mensagem, causa);
    }

    @Override
    public Servico save(Servico servico) {
        if (servico == null || !servico.isValido()
                || (servico.getId() != null && servico.getId() != 0)) {
            throw new IllegalArgumentException(
                    "Dados inválidos para cadastrar serviço.");
        }

        String sql = "INSERT INTO servicos "
                + "(nome, tempo_estimado_minutos, custo_tabelado, "
                + "data_criacao, data_ultima_atualizacao) VALUES (?, ?, ?, ?, ?)";

        LocalDateTime agora = LocalDateTime.now();

        return executarSQL(
                sql,
                true,
                "Erro ao cadastrar serviço no MySQL.",
                stmt -> {
                    stmt.setString(1, servico.getNome());
                    stmt.setInt(2, servico.getTempoEstimadoMinutos());
                    stmt.setDouble(3, servico.getCustoTabelado());
                    stmt.setTimestamp(4, Timestamp.valueOf(agora));
                    stmt.setTimestamp(5, Timestamp.valueOf(agora));
                },
                stmt -> {
                    stmt.executeUpdate();

                    try (ResultSet chaves = stmt.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            throw new SQLException(
                                    "MySQL não retornou o ID do serviço.");
                        }
                        servico.setId(chaves.getLong(1));
                    }

                    servico.setDataCriacao(agora);
                    servico.setDataUltimaAtualizacao(agora);
                    return servico;
                }
        );
    }

    @Override
    public Optional<Servico> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM servicos WHERE id = ?";

        return executarSQL(
                sql,
                false,
                "Erro ao buscar serviço no MySQL.",
                stmt -> stmt.setLong(1, id),
                stmt -> {
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return Optional.of(ServicoMapper.fromResultSet(rs));
                        }
                        return Optional.empty();
                    }
                }
        );
    }

    @Override
    public List<Servico> findAll() {
        String sql = "SELECT * FROM servicos ORDER BY id";

        return executarSQL(
                sql,
                false,
                "Erro ao listar serviços no MySQL.",
                stmt -> { },
                stmt -> {
                    List<Servico> servicos = new ArrayList<>();

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            servicos.add(ServicoMapper.fromResultSet(rs));
                        }
                    }

                    return servicos;
                }
        );
    }

    @Override
    public Servico update(Servico servico) {
        if (servico == null || servico.getId() == null
                || !servico.isValido()) {
            throw new IllegalArgumentException(
                    "Dados inválidos para atualizar serviço.");
        }

        String sql = "UPDATE servicos SET nome = ?, "
                + "tempo_estimado_minutos = ?, custo_tabelado = ?, "
                + "data_ultima_atualizacao = ? WHERE id = ?";

        LocalDateTime agora = LocalDateTime.now();

        return executarSQL(
                sql,
                false,
                "Erro ao atualizar serviço no MySQL.",
                stmt -> {
                    stmt.setString(1, servico.getNome());
                    stmt.setInt(2, servico.getTempoEstimadoMinutos());
                    stmt.setDouble(3, servico.getCustoTabelado());
                    stmt.setTimestamp(4, Timestamp.valueOf(agora));
                    stmt.setLong(5, servico.getId());
                },
                stmt -> {
                    if (stmt.executeUpdate() == 0
                            && !existsById(servico.getId())) {
                        throw new IllegalArgumentException(
                                "Serviço não encontrado: " + servico.getId());
                    }

                    servico.setDataUltimaAtualizacao(agora);
                    return servico;
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

        String sql = "SELECT 1 FROM servicos WHERE id = ?";

        return executarSQL(
                sql,
                false,
                "Erro ao verificar serviço no MySQL.",
                stmt -> stmt.setLong(1, id),
                stmt -> {
                    try (ResultSet rs = stmt.executeQuery()) {
                        return rs.next();
                    }
                }
        );
    }

    private int excluir(Long id) {
        if (id == null) {
            return 0;
        }

        String sql = "DELETE FROM servicos WHERE id = ?";

        return executarSQL(
                sql,
                false,
                "Erro ao excluir serviço no MySQL.",
                stmt -> stmt.setLong(1, id),
                stmt -> stmt.executeUpdate()
        );
    }

    // Métodos mantidos para compatibilidade com o código existente.

    public List<Servico> getServicos() {
        return findAll();
    }

    public boolean salvar(Servico servico) {
        if (servico == null || !servico.isValido()
                || (servico.getId() != null && servico.getId() != 0)) {
            return false;
        }

        save(servico);
        return true;
    }

    public Optional<Servico> buscarPorId(Long id) {
        return findById(id);
    }

    public boolean atualizar(Long id, Servico atualizado) {
        if (id == null || atualizado == null) {
            return false;
        }

        Optional<Servico> existenteOpt = findById(id);

        if (existenteOpt.isEmpty()) {
            return false;
        }

        Servico existente = existenteOpt.get();

        if (atualizado.getNome() != null
                && !atualizado.getNome().trim().isEmpty()) {
            existente.setNome(atualizado.getNome());
        }

        if (atualizado.getTempoEstimadoMinutos() != null
                && atualizado.getTempoEstimadoMinutos() > 0) {
            existente.setTempoEstimadoMinutos(
                    atualizado.getTempoEstimadoMinutos());
        }

        if (atualizado.getCustoTabelado() != null
                && atualizado.getCustoTabelado() > 0) {
            existente.setCustoTabelado(atualizado.getCustoTabelado());
        }

        update(existente);
        return true;
    }

    public boolean deletar(Long id) {
        return excluir(id) > 0;
    }
}
