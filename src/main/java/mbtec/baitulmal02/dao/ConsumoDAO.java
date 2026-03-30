package mbtec.baitulmal02.dao;



import mbtec.baitulmal02.DB.ConexaoSQLite;
import mbtec.baitulmal02.model.Consumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumoDAO {

    public void inserirConsumo(Consumo consumo) {
        System.out.println("DAO Consumo invocada");
        String sqlConsumo = "INSERT INTO consumo(descricao, valor_consumo, data, observacao) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlConsumo, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, consumo.getDescricao());
            stmt.setBigDecimal(2, consumo.getValorConsumo());
            stmt.setString(3, consumo.getData());
            stmt.setString(4, consumo.getObservacao());
            stmt.executeUpdate();

            // pega o id gerado do consumo
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idConsumo = rs.getInt(1);
                consumo.setIdconsumo(idConsumo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Consumo> listarTodos() {
        List<Consumo> consumos = new ArrayList<>();
        String sql = "SELECT * FROM consumo";
        try (Connection connection = ConexaoSQLite.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                consumos.add(new Consumo(
                        rs.getInt("idconsumo"),
                        rs.getString("descricao"),
                        rs.getBigDecimal("valor_consumo"),
                        rs.getString("data"),
                        rs.getString("observacao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consumos;
    }

    public boolean excluirConsumo(int idConsumo) {
        String sql = "DELETE FROM consumo WHERE idconsumo = ?";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConsumo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editar(Consumo consumo) {
        System.out.println("EditarConsumoDAO invocada");
        String sql = "UPDATE consumo SET descricao=?, valor_consumo=?, data=?, observacao=? WHERE idconsumo=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, consumo.getDescricao());
            stmt.setBigDecimal(2, consumo.getValorConsumo());
            stmt.setString(3, consumo.getData());
            stmt.setString(3, consumo.getObservacao());
            stmt.setInt(3, consumo.getIdconsumo());

            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(MovimentoDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }
}
