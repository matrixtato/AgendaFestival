package com.agendafestivais.dao;

import com.agendafestivais.config.ConnectionFactory;
import com.agendafestivais.model.Festival;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FestivalDAO {

    public List<Festival> listarTodos() {
        List<Festival> lista = new ArrayList<>();
        String sql = "SELECT id, nome, data, preco, local_id FROM festivais";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Festival(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDate("data"),
                        rs.getDouble("preco"),
                        rs.getInt("local_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Exigência: listarPorPai utilizando a FK no WHERE da cláusula SQL
    public List<Festival> listarPorPai(int localId) {
        List<Festival> lista = new ArrayList<>();
        String sql = "SELECT id, nome, data, preco, local_id FROM festivais WHERE local_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, localId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Festival(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getDate("data"),
                            rs.getDouble("preco"),
                            rs.getInt("local_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Festival buscarPorId(int id) {
        String sql = "SELECT id, nome, data, preco, local_id FROM festivais WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Festival(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getDate("data"),
                            rs.getDouble("preco"),
                            rs.getInt("local_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Festival inserir(Festival obj) {
        String sql = "INSERT INTO festivais (nome, data, preco, local_id) VALUES (?, ?, ?, ?)";

        // Exigência: Uso de multi-catch caso ocorra erro de conversão de dados ou banco
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, obj.getNome());

            // Simulação de validação de formato de dados para disparar exceções controladas
            if (obj.getData() == null) {
                throw new NumberFormatException("Data do festival inválida!");
            }

            stmt.setDate(2, new java.sql.Date(obj.getData().getTime()));
            stmt.setDouble(3, obj.getPreco());
            stmt.setInt(4, obj.getLocalId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    obj.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException | NumberFormatException e) { // Exigência: Multi-catch implementado
            e.printStackTrace();
        }
        return obj;
    }

    public boolean atualizar(Festival obj) {
        String sql = "UPDATE festivais SET nome = ?, data = ?, preco = ?, local_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, obj.getNome());
            stmt.setDate(2, new java.sql.Date(obj.getData().getTime()));
            stmt.setDouble(3, obj.getPreco());
            stmt.setInt(4, obj.getLocalId());
            stmt.setInt(5, obj.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM festivais WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
