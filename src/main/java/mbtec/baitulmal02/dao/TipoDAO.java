package mbtec.baitulmal02.dao;


import mbtec.baitulmal02.DB.ConexaoSQLite;
import mbtec.baitulmal02.model.Tipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TipoDAO {
    public void inserir(Tipo tipo) {
        String sql = "INSERT INTO tipo (descricao_tipo) VALUES (?)";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipo.getDescricaoTipo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Tipo> listar() {
        List<Tipo> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tipo";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tipos.add(new Tipo(
                        rs.getInt("idtipo"),
                        rs.getString("descricao_tipo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
    }

    public boolean editar(Tipo tipo) {
        String sql = "UPDATE tipo SET descricao_tipo=? WHERE idtipo=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipo.getDescricaoTipo());
            stmt.setInt(2, tipo.getIdtipo());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(TipoDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean remover(Tipo tipo) {
        String sql = "DELETE FROM tipo WHERE idtipo=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tipo.getIdtipo());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(TipoDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }
}
