package com.mediateca.web;

import com.mediateca.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigService {

    public Map<String, String> listarConfiguracion() {
        String sql = "SELECT clave, valor FROM Configuracion ORDER BY clave";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            Map<String, String> configuracion = new LinkedHashMap<>();
            while (rs.next()) {
                configuracion.put(rs.getString("clave"), rs.getString("valor"));
            }
            return configuracion;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar configuración: " + e.getMessage(), e);
        }
    }

    public int getInt(String clave, int defaultValue) {
        String sql = "SELECT valor FROM Configuracion WHERE clave = ?";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Integer.parseInt(rs.getString("valor"));
                }
            }
        } catch (SQLException | NumberFormatException ignored) {
        }
        return defaultValue;
    }

    public double getDouble(String clave, double defaultValue) {
        String sql = "SELECT valor FROM Configuracion WHERE clave = ?";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Double.parseDouble(rs.getString("valor"));
                }
            }
        } catch (SQLException | NumberFormatException ignored) {
        }
        return defaultValue;
    }

    public void setConfig(String clave, String valor) {
        String sql = "INSERT INTO Configuracion (clave, valor) VALUES (?, ?) ON CONFLICT(clave) DO UPDATE SET valor = excluded.valor";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setString(2, valor);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar configuración: " + e.getMessage(), e);
        }
    }
}
