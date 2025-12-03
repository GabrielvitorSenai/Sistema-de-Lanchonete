package br.com.lanchonete.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static ConnectionFactory instance;
    private static final Properties PROPERTIES = new Properties();

    // Bloco estático executado uma única vez ao carregar a classe
    static {
        try (InputStream input = ConnectionFactory.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("ERRO: Arquivo db.properties não foi encontrado na pasta resources.");
            }

            // Carrega as propriedades
            PROPERTIES.load(input);

            // Carrega o driver JDBC
            Class.forName(PROPERTIES.getProperty("db.driver"));

        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar db.properties: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado: " + e.getMessage(), e);
        }
    }

    // Singleton → mantém uma única instância
    private ConnectionFactory() {}

    public static synchronized ConnectionFactory getInstance() {
        if (instance == null) {
            instance = new ConnectionFactory();
        }
        return instance;
    }

    /**
     * Obtém uma conexão ativa com o banco de dados usando o db.properties
     */
    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }
}
