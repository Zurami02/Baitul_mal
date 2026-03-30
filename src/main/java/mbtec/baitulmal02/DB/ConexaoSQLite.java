package mbtec.baitulmal02.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLite {

        private static final String URL = "jdbc:sqlite:E:/JavaFX/Baitulmal02/Baitul_mal.db"; // caminho do seu arquivo .db
        private static Connection connection = null;

        private ConexaoSQLite() {
            // Construtor privado: ninguém pode instanciar essa classe
        }

        public static Connection getConnection() {
            try {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(URL);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }

        public static void fecharConexao() {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    connection = null; // importante!
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
