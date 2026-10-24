package br.com.mecaniQA.api.repository;

import br.com.mecaniQA.api.config.ConexaoBanco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractJdbcRepository<T, ID>
        implements Repository<T, ID> {

    @FunctionalInterface
    protected interface ConfigurarParametros {
        void configurar(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    protected interface ExecutarStatement<R> {
        R executar(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    protected interface ExecutarConexao<R> {
        R executar(Connection conexao) throws SQLException;
    }

    protected final <R> R executarSQL(
            String sql,
            boolean retornarChavesGeradas,
            String mensagemErro,
            ConfigurarParametros parametros,
            ExecutarStatement<R> operacao) {

        try (Connection conexao = abrirConexao();
             PreparedStatement stmt = prepararStatement(
                     conexao, sql, retornarChavesGeradas)) {

            parametros.configurar(stmt);
            return operacao.executar(stmt);

        } catch (SQLException e) {
            throw criarExcecaoBanco(mensagemErro, e);
        }
    }

    protected final <R> R executarComConexao(
            String mensagemErro,
            ExecutarConexao<R> operacao) {

        try (Connection conexao = abrirConexao()) {
            return operacao.executar(conexao);

        } catch (SQLException e) {
            throw criarExcecaoBanco(mensagemErro, e);
        }
    }

    protected Connection abrirConexao() throws SQLException {
        return ConexaoBanco.conectar();
    }

    protected PreparedStatement prepararStatement(
            Connection conexao,
            String sql,
            boolean retornarChavesGeradas) throws SQLException {

        if (retornarChavesGeradas) {
            return conexao.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS);
        }

        return conexao.prepareStatement(sql);
    }

    protected abstract RuntimeException criarExcecaoBanco(
            String mensagem, SQLException causa);
}
