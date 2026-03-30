package mbtec.baitulmal02.dao;



import mbtec.baitulmal02.DB.ConexaoSQLite;
import mbtec.baitulmal02.model.ContaPrincipal;

import java.math.BigDecimal;
import java.sql.*;

public class ContaPrincipalDAO {

    public ContaPrincipal buscarConta() {
        String sql = "SELECT * FROM conta_principal LIMIT 1";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new ContaPrincipal(
                        rs.getInt("idconta_principal"),
                        rs.getBigDecimal("saldo_atual")
                );
            } else {
                // Nenhum registro -> cria automaticamente
                String SQL = "INSERT INTO conta_principal (saldo_atual, idmovimento) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(SQL)) {
                    insertStmt.setBigDecimal(1, BigDecimal.ZERO);
                    insertStmt.setNull(2, java.sql.Types.INTEGER);
                    insertStmt.executeUpdate();
                }
                System.out.println("Conta principal criada automaticamente.");
                return new ContaPrincipal(1, BigDecimal.ZERO);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ContaPrincipal(1, BigDecimal.ZERO);
    }

    public void atualizarSaldo(BigDecimal novoSaldo, int idMovimento) {
        System.out.println("ContaPrincipal DAO invocada");

        String sql = "UPDATE conta_principal SET saldo_atual = ?, idmovimento = ? WHERE idconta_principal = 1";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, novoSaldo);
            stmt.setInt(2, idMovimento);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
