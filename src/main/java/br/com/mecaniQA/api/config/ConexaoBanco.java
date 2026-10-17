
package br.com.mecaniQA.api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    public static Connection conectar() throws SQLException {

        String url = System.getenv("MECANIQA_DB_URL");
        String usuario = System.getenv("MECANIQA_DB_USER");
        String senha = System.getenv("MECANIQA_DB_PASSWORD");

        if (url == null || url.isBlank()
                || usuario == null || usuario.isBlank()
                || senha == null) {
            throw new SQLException("Configuração do MySQL incompleta.");
        }

        return DriverManager.getConnection(url, usuario, senha);
    }
}
