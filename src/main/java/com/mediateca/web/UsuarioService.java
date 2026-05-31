package com.mediateca.web;

import com.mediateca.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    public record Usuario(int id, String nombre, String rol, double mora) {}

    public List<Usuario> listarUsuarios() {
        String sql = "SELECT id, nombre, rol, COALESCE(mora_acumulada, 0) AS mora FROM Usuarios ORDER BY rol, nombre";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Usuario> usuarios = new ArrayList<>();
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("rol"),
                    rs.getDouble("mora")
                ));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        }
    }

    public boolean crearUsuario(String nombre, String password, String rol) {
        String sql = "INSERT INTO Usuarios (nombre, password, rol, mora_acumulada) VALUES (?, ?, ?, 0)";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, PasswordUtils.sha256(password));
            ps.setString(3, rol);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }
}
