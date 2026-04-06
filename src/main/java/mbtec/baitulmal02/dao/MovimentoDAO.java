package mbtec.baitulmal02.dao;


import mbtec.baitulmal02.DB.ConexaoSQLite;
import mbtec.baitulmal02.model.Consumo;
import mbtec.baitulmal02.model.Contribuicao;
import mbtec.baitulmal02.model.Movimento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovimentoDAO {

    public int inserirMovimento(Movimento mov) {
        String sql = "INSERT INTO movimento (tipo, valor, data, saldo_resultante, idconsumo, idcontribuicao) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mov.getTipo());
            stmt.setBigDecimal(2, mov.getValor());
            stmt.setString(3, mov.getData());
            stmt.setBigDecimal(4, mov.getSaldoResultante());

            // pega o id do objeto consumo ou contribuição
            if (mov.getConsumo() != null) {
                stmt.setInt(5, mov.getConsumo().getIdconsumo());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            if (mov.getContribuicao() != null) {
                stmt.setInt(6, mov.getContribuicao().getIdcontribuicao());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean excluir(Movimento movimento) {
        String sqlDeleteMov = "DELETE FROM movimento WHERE idmovimento = ?";
        try (Connection conn = ConexaoSQLite.getConnection()) {
            conn.setAutoCommit(false); // inicia transação

            try {
                //Se for CONSUMO — primeiro exclui da tabela consumo
                if (movimento.getTipo().equalsIgnoreCase("Consumo") && movimento.getConsumo() != null) {
                    String sqlConsumo = "DELETE FROM consumo WHERE idconsumo = ?";
                    try (PreparedStatement stmtConsumo = conn.prepareStatement(sqlConsumo)) {
                        stmtConsumo.setInt(1, movimento.getConsumo().getIdconsumo());
                        stmtConsumo.executeUpdate();
                    }
                }

                //Se for CONTRIBUICAO — primeiro exclui da tabela contribuicao
                if (movimento.getTipo().equalsIgnoreCase("Contribuicao") && movimento.getContribuicao() != null) {
                    String sqlContrib = "DELETE FROM contribuicao WHERE idcontribuicao = ?";
                    try (PreparedStatement stmtContrib = conn.prepareStatement(sqlContrib)) {
                        stmtContrib.setInt(1, movimento.getContribuicao().getIdcontribuicao());
                        stmtContrib.executeUpdate();
                    }
                }

                //Depois exclui o movimento
                try (PreparedStatement stmtMov = conn.prepareStatement(sqlDeleteMov)) {
                    stmtMov.setInt(1, movimento.getIdmovimento());
                    stmtMov.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean editar(Movimento movimento) {
        System.out.println("EditarMovimentoDAO invocada");

        String sql = "UPDATE movimento SET tipo=?, valor=?, data=?, saldo_resultante=?, idconsumo=?, idcontribuicao=? WHERE idmovimento=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, movimento.getTipo());
            stmt.setBigDecimal(2, movimento.getValor());
            stmt.setString(3, movimento.getData());
            stmt.setBigDecimal(4, movimento.getSaldoResultante());
            stmt.setInt(5, movimento.getConsumo().getIdconsumo());
            stmt.setInt(6, movimento.getContribuicao().getIdcontribuicao());
            stmt.setInt(7, movimento.getIdmovimento());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(MovimentoDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<Movimento> buscarPorTipoData(String filtro) {

        String sql = """
        SELECT 
            m.idmovimento, m.tipo, m.valor, m.data, m.saldo_resultante,
            m.idconsumo, m.idcontribuicao,

            c.descricao AS consumo_descricao, 
            c.valor_consumo AS consumo_valor, 
            c.data AS consumo_data, 
            c.observacao AS consumo_obs,

            cb.contribuinte AS contrib_contribuinte, 
            cb.valor_contribuicao AS contrib_valor, 
            cb.data AS contrib_data, 
            cb.observacao AS contrib_obs 
        FROM movimento m
        LEFT JOIN consumo c ON m.idconsumo = c.idconsumo
        LEFT JOIN contribuicao cb ON m.idcontribuicao = cb.idcontribuicao
        WHERE m.tipo LIKE ? OR m.data LIKE ?
        ORDER BY m.data DESC
    """;

        List<Movimento> lista = new ArrayList<>();

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + filtro + "%");
            stmt.setString(2, "%" + filtro + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Movimento m = new Movimento();
                m.setIdmovimento(rs.getInt("idmovimento"));
                m.setTipo(rs.getString("tipo"));
                m.setValor(rs.getBigDecimal("valor"));
                m.setData(rs.getString("data"));
                m.setSaldoResultante(rs.getBigDecimal("saldo_resultante"));

                //Mapeia consumo, se existir
                int idConsumo = rs.getInt("idconsumo");
                if (!rs.wasNull()) {
                    Consumo consumo = new Consumo();
                    consumo.setIdconsumo(idConsumo);
                    consumo.setDescricao(rs.getString("consumo_descricao"));
                    consumo.setValorConsumo(rs.getBigDecimal("consumo_valor"));
                    consumo.setData(rs.getString("consumo_data"));
                    consumo.setObservacao(rs.getString("consumo_obs"));
                    m.setConsumo(consumo);
                }

                //Mapeia contribuição, se existir
                int idContribuicao = rs.getInt("idcontribuicao");
                if (!rs.wasNull()) {
                    Contribuicao contrib = new Contribuicao();
                    contrib.setIdcontribuicao(idContribuicao);
                    contrib.setContribuinte(rs.getString("contrib_contribuinte"));
                    contrib.setValorContribuicao(rs.getBigDecimal("contrib_valor"));
                    contrib.setData(rs.getString("contrib_data"));
                    contrib.setObservacao(rs.getString("contrib_obs"));
                    m.setContribuicao(contrib);
                }

                lista.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    public List<Movimento> listar() {

        String sql = "SELECT m.*, c.idconsumo, c.descricao AS descricao_consumo, c.valor_consumo, c.data As data_consumo, " +
                "c.observacao AS cons_observacao, co.idcontribuicao, co.contribuinte, co.valor_contribuicao, co.data AS data_contribuicao, co.observacao AS cont_observacao " +
                "FROM movimento m " +
                "LEFT JOIN consumo c ON m.idconsumo = c.idconsumo "+
                "LEFT JOIN contribuicao co ON m.idcontribuicao = co.idcontribuicao";

        List<Movimento> retorno = new ArrayList<>();
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {

                Movimento m = new Movimento();
                m.setIdmovimento(resultado.getInt("idmovimento"));
                m.setTipo(resultado.getString("tipo"));
                m.setValor(resultado.getBigDecimal("valor"));
                m.setData(resultado.getString("data"));
                m.setSaldoResultante(resultado.getBigDecimal("saldo_resultante"));

                // Mapeia consumo, se existir
                int idConsumo = resultado.getInt("idconsumo");
                if (!resultado.wasNull()) {
                    Consumo consumo = new Consumo();
                    consumo.setIdconsumo(idConsumo);
                    consumo.setDescricao(resultado.getString("descricao_consumo"));
                    consumo.setValorConsumo(resultado.getBigDecimal("valor_consumo"));
                    consumo.setData(resultado.getString("data_consumo"));
                    consumo.setObservacao(resultado.getString("cons_observacao"));
                    m.setConsumo(consumo);
                }

                // Mapeia contribuição, se existir
                int idContribuicao = resultado.getInt("idcontribuicao");
                if (!resultado.wasNull()) {
                    Contribuicao contribuicao = new Contribuicao();
                    contribuicao.setIdcontribuicao(idContribuicao);
                    contribuicao.setContribuinte(resultado.getString("contribuinte"));
                    contribuicao.setValorContribuicao(resultado.getBigDecimal("valor_contribuicao"));
                    contribuicao.setData(resultado.getString("data_contribuicao"));
                    contribuicao.setObservacao(resultado.getString("cont_observacao"));
                    m.setContribuicao(contribuicao);
                }
                retorno.add(m);
            }
        } catch (SQLException e) {
            Logger.getLogger(MovimentoDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public List<Movimento> listarPorPeriodo(LocalDate inicio, LocalDate datafinal){

        String sql = "SELECT m.idmovimento, m.tipo, m.valor, m.data, m.saldo_resultante, " +
                "c.idconsumo, c.descricao AS descricao_consumo, c.valor_consumo, c.data As data_consumo, " +
                "c.observacao AS cons_observacao, co.idcontribuicao, co.contribuinte, co.valor_contribuicao, co.data AS data_contribuicao, co.observacao AS cont_observacao " +
                "FROM movimento m " +
                "LEFT JOIN consumo c ON m.idconsumo = c.idconsumo "+
                "LEFT JOIN contribuicao co ON m.idcontribuicao = co.idcontribuicao " +
                "WHERE date(m.data) BETWEEN ? AND ?";

        List<Movimento> retorno = new ArrayList<>();
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, inicio.toString());
            stmt.setString(2, datafinal.toString());

            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {

                Movimento m = new Movimento();
                m.setIdmovimento(resultado.getInt("idmovimento"));
                m.setTipo(resultado.getString("tipo"));
                m.setValor(resultado.getBigDecimal("valor"));
                m.setData(resultado.getString("data"));
                m.setSaldoResultante(resultado.getBigDecimal("saldo_resultante"));

                int idConsumo = resultado.getInt("idconsumo");
                if (!resultado.wasNull()) {
                    Consumo consumo = new Consumo();
                    consumo.setIdconsumo(idConsumo);
                    consumo.setDescricao(resultado.getString("descricao_consumo"));
                    consumo.setValorConsumo(resultado.getBigDecimal("valor_consumo"));
                    consumo.setData(resultado.getString("data_consumo"));
                    consumo.setObservacao(resultado.getString("cons_observacao"));
                    m.setConsumo(consumo);
                }

                int idContribuicao = resultado.getInt("idcontribuicao");
                if (!resultado.wasNull()) {
                    Contribuicao contribuicao = new Contribuicao();
                    contribuicao.setIdcontribuicao(idContribuicao);
                    contribuicao.setContribuinte(resultado.getString("contribuinte"));
                    contribuicao.setValorContribuicao(resultado.getBigDecimal("valor_contribuicao"));
                    contribuicao.setData(resultado.getString("data_contribuicao"));
                    contribuicao.setObservacao(resultado.getString("cont_observacao"));
                    m.setContribuicao(contribuicao);
                }
                retorno.add(m);
            }
        } catch (SQLException e) {
            Logger.getLogger(MovimentoDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public Movimento buscarPorId(int idMovimento) {
        String sql = """
        SELECT 
            m.idmovimento, m.tipo, m.valor, m.data, m.saldo_resultante,
            m.idconsumo, m.idcontribuicao,

            c.descricao AS consumo_descricao, 
            c.valor_consumo AS consumo_valor, 
            c.data AS consumo_data, 
            c.observacao AS obser_Consumo,

            cb.contribuinte AS contrib_contribuinte, 
            cb.valor_contribuicao AS contrib_valor, 
            cb.data AS contrib_data,
            cb.observacao AS contrib_obs
            
        FROM movimento m
        LEFT JOIN consumo c ON m.idconsumo = c.idconsumo
        LEFT JOIN contribuicao cb ON m.idcontribuicao = cb.idcontribuicao
        WHERE m.idmovimento = ?
    """;

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMovimento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Movimento m = new Movimento();
                m.setIdmovimento(rs.getInt("idmovimento"));
                m.setTipo(rs.getString("tipo"));
                m.setValor(rs.getBigDecimal("valor"));
                m.setData(rs.getString("data"));
                m.setSaldoResultante(rs.getBigDecimal("saldo_resultante"));

                // Mapeia consumo, se existir
                int idConsumo = rs.getInt("idconsumo");
                if (!rs.wasNull()) {
                    Consumo consumo = new Consumo();
                    consumo.setIdconsumo(idConsumo);
                    consumo.setDescricao(rs.getString("consumo_descricao"));
                    consumo.setValorConsumo(rs.getBigDecimal("consumo_valor"));
                    consumo.setData(rs.getString("consumo_data"));
                    consumo.setObservacao(rs.getString("obser_Consumo"));
                    m.setConsumo(consumo);
                }

                // Mapeia contribuição, se existir
                int idContribuicao = rs.getInt("idcontribuicao");
                if (!rs.wasNull()) {
                    Contribuicao contrib = new Contribuicao();
                    contrib.setIdcontribuicao(idContribuicao);
                    contrib.setContribuinte(rs.getString("contrib_contribuinte"));
                    contrib.setValorContribuicao(rs.getBigDecimal("contrib_valor"));
                    contrib.setData(rs.getString("contrib_data"));
                    contrib.setObservacao(rs.getString("contrib_obs"));
                    m.setContribuicao(contrib);
                }
                return m;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // se não encontrou
    }

    public void limparConsumo(int idMovimento) {
        String sql = "UPDATE movimento SET idconsumo = NULL WHERE idmovimento = ?";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMovimento);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void limparContribuicao(int idMovimento) {
        String sql = "UPDATE movimento SET idcontribuicao = NULL WHERE idmovimento = ?";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMovimento);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
