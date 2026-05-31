package com.mediateca.web;

import com.mediateca.ConexionBD;
import com.mediateca.GestorPrestamos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoService {

    private final GestorPrestamos gestorPrestamos = new GestorPrestamos();

    public record Prestamo(int id, int usuarioId, String usuarioNombre,
                           int documentoId, String documentoTitulo,
                           String fechaSalida, String fechaDevolucion,
                           double mora) {}

    public boolean solicitarPrestamo(int usuarioId, String role, int documentoId) {
        return gestorPrestamos.realizarPrestamo(usuarioId, role, documentoId);
    }

    public boolean devolverPrestamo(int prestamoId) {
        return gestorPrestamos.registrarDevolucion(prestamoId);
    }

    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) AS total FROM Prestamos WHERE fecha_devolucion IS NULL";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar prestamos activos: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Prestamo> listarPrestamosUsuario(int usuarioId) {
        String sql = "SELECT p.id, p.id_usuario, u.nombre AS usuario, p.id_documento, d.titulo AS documento, p.fecha_salida, COALESCE(p.fecha_devolucion, 'Pendiente') AS fecha_devolucion, COALESCE(p.mora_acumulada, 0) AS mora FROM Prestamos p JOIN Usuarios u ON u.id = p.id_usuario JOIN Documentos d ON d.id = p.id_documento WHERE p.id_usuario = ? ORDER BY p.fecha_salida DESC";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Prestamo> prestamos = new ArrayList<>();
                while (rs.next()) {
                    prestamos.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getString("usuario"),
                        rs.getInt("id_documento"),
                        rs.getString("documento"),
                        rs.getString("fecha_salida"),
                        rs.getString("fecha_devolucion"),
                        rs.getDouble("mora")
                    ));
                }
                return prestamos;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar préstamos del usuario: " + e.getMessage(), e);
        }
    }

    public List<Prestamo> listarPrestamosTodos() {
        String sql = "SELECT p.id, p.id_usuario, u.nombre AS usuario, p.id_documento, d.titulo AS documento, p.fecha_salida, COALESCE(p.fecha_devolucion, 'Pendiente') AS fecha_devolucion, COALESCE(p.mora_acumulada, 0) AS mora FROM Prestamos p JOIN Usuarios u ON u.id = p.id_usuario JOIN Documentos d ON d.id = p.id_documento ORDER BY p.fecha_salida DESC";
        Connection connection = ConexionBD.getInstancia().getConexion();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Prestamo> prestamos = new ArrayList<>();
            while (rs.next()) {
                prestamos.add(new Prestamo(
                    rs.getInt("id"),
                    rs.getInt("id_usuario"),
                    rs.getString("usuario"),
                    rs.getInt("id_documento"),
                    rs.getString("documento"),
                    rs.getString("fecha_salida"),
                    rs.getString("fecha_devolucion"),
                    rs.getDouble("mora")
                ));
            }
            return prestamos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar préstamos: " + e.getMessage(), e);
        }
    }
}
