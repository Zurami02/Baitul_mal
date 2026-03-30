package mbtec.baitulmal02.dao;



import mbtec.baitulmal02.DB.ConexaoSQLite;
import mbtec.baitulmal02.model.Contribuicao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContribuicaoDAO {
    public void inserirContribuicao(Contribuicao contribuicao) {
        String sql = "INSERT INTO contribuicao (contribuinte, valor_contribuicao, data, observacao) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, contribuicao.getContribuinte());
            stmt.setBigDecimal(2, contribuicao.getValorContribuicao());
            stmt.setString(3, contribuicao.getData());
            stmt.setString(4, contribuicao.getObservacao());
            stmt.executeUpdate();

            //Recuperar o ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()){
                    contribuicao.setIdcontribuicao(rs.getInt(1));//atualizar o objeto
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Contribuicao> listarTodos() {
        List<Contribuicao> contribuicoes = new ArrayList<>();
        String sql = "SELECT * FROM contribuicao";
        try (Connection connection = ConexaoSQLite.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contribuicoes.add(new Contribuicao(
                        rs.getInt("idcontribuicao"),
                        rs.getString("contribuinte"),
                        rs.getBigDecimal("valor_contribuicao"),
                        rs.getString("data"),
                        rs.getString("observacao")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contribuicoes;
    }

    public boolean excluirContribuicao(int idContribuicao) {
        String sql = "DELETE FROM contribuicao WHERE idcontribuicao = ?";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idContribuicao);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editar(Contribuicao contribuicao) {

        String sql = "UPDATE contribuicao SET contribuinte=?, valor_contribuicao=?, data=?, observacao=? " +
                "WHERE idcontribuicao=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, contribuicao.getContribuinte());
            stmt.setBigDecimal(2, contribuicao.getValorContribuicao());
            stmt.setString(3, contribuicao.getData());
            stmt.setString(4, contribuicao.getObservacao());
            stmt.setInt(5, contribuicao.getIdcontribuicao());

            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(MovimentoDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }
}
