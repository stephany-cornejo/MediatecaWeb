package com.biblioteca.web;

import com.biblioteca.ConexionBD;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class LoginService {

    public record Usuario(int id, String nombre, String rol) {}

    public Usuario autenticar(String nombre, String password) {
        String sql = "SELECT id, nombre, rol, password FROM Usuarios WHERE nombre = ?";

        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashAlmacenado = rs.getString("password");
                    if (hashAlmacenado != null && (PasswordUtils.sha256(password).equals(hashAlmacenado) || password.equals(hashAlmacenado))) {
                        return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("rol")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al autenticar usuario: " + e.getMessage(), e);
        }
        return null;
    }
}
