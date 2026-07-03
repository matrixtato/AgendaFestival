package com.agendafestivais.dao;

import com.agendafestivais.config.ConnectionFactory;
import com.agendafestivais.model.Local;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalDAO {

    public List<Local> listarTodos() {
        List<Local> lista = new ArrayList<>();
        String sql = "SELECT id, nome, cidade FROM locais";

        // Exigência: try-with-resources gerenciando Connection, PreparedStatement e ResultSet
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Local(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cidade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Local buscarPorId(int id) {
        String sql = "SELECT id, nome, cidade FROM locais WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id); // Exigência: PreparedStatement em todas as queries
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Local(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cidade")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Local inserir(Local obj) {
        String sql = "INSERT INTO locais (nome, cidade) VALUES (?, ?)";

        // Exigência: Retornar chaves geradas automaticamente pelo banco
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, obj.getNome());
            stmt.setString(2, obj.getCidade());
            stmt.executeUpdate();

            // Exigência: getGeneratedKeys() para preencher o ID do objeto inserido
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    obj.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public boolean atualizar(Local obj) {
        String sql = "UPDATE locais SET nome = ?, cidade = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, obj.getNome());
            stmt.setString(2, obj.getCidade());
            stmt.setInt(3, obj.getId());

            return stmt.executeUpdate() > 0; // Retorna true se alterou alguma linha
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM locais WHERE id = ?";

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
