package com.mediateca.web;

import com.mediateca.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentoService {

    public record Documento(int id, String titulo, String ubicacion, String tipo,
                            int stockDisponible, int stockTotal, String camposEspecificosJson) {
        public String getCamposEspecificosFormateados() {
            if (camposEspecificosJson == null) {
                return "";
            }
            String valor = camposEspecificosJson.trim();
            if (valor.isEmpty() || "{}".equals(valor) || "null".equalsIgnoreCase(valor)) {
                return "Sin datos adicionales";
            }
            return valor
                .replaceAll("[{}]", "")
                .replaceAll("\\\"", "")
                .replaceAll(",", ", ")
                .trim();
        }
    }

    public List<Documento> listarDocumentos() {
        String sql = "SELECT id, titulo, ubicacion, tipo, stock_disponible, stock_total, COALESCE(campos_especificos_json, '{}') AS campos_especificos_json FROM Documentos ORDER BY tipo, titulo";
        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Documento> documentos = new ArrayList<>();
            while (rs.next()) {
                documentos.add(new Documento(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("ubicacion"),
                    rs.getString("tipo"),
                    rs.getInt("stock_disponible"),
                    rs.getInt("stock_total"),
                    rs.getString("campos_especificos_json")
                ));
            }
            return documentos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar documentos: " + e.getMessage(), e);
        }
    }

    public boolean crearDocumento(String tipo, String titulo, String ubicacion,
                                  int stockTotal, String camposEspecificosJson) {
        String sql = "INSERT INTO Documentos (titulo, ubicacion, tipo, stock_total, stock_disponible, campos_especificos_json) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, ubicacion);
            ps.setString(3, tipo);
            ps.setInt(4, stockTotal);
            ps.setInt(5, stockTotal);
            ps.setString(6, camposEspecificosJson == null || camposEspecificosJson.isBlank() ? "{}" : camposEspecificosJson);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean actualizarDocumento(int id, String tipo, String titulo, String ubicacion,
                                       int stockDisponible, int stockTotal, String camposEspecificosJson) {
        String sql = "UPDATE Documentos SET titulo = ?, ubicacion = ?, tipo = ?, stock_disponible = ?, stock_total = ?, campos_especificos_json = ? WHERE id = ?";
        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, ubicacion);
            ps.setString(3, tipo);
            ps.setInt(4, stockDisponible);
            ps.setInt(5, stockTotal);
            ps.setString(6, camposEspecificosJson == null || camposEspecificosJson.isBlank() ? "{}" : camposEspecificosJson);
            ps.setInt(7, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminarDocumento(int id) {
        String sql = "DELETE FROM Documentos WHERE id = ?";
        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public int contarPorTipo(String tipo) {
        String sql = "SELECT COUNT(*) AS total FROM Documentos WHERE tipo = ?";
        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar documentos por tipo: " + e.getMessage(), e);
        }
        return 0;
    }

    public int sumarStockDisponible() {
        return sumarStock("stock_disponible", "Error al sumar stock disponible: ");
    }

    public int sumarStockTotal() {
        return sumarStock("stock_total", "Error al sumar stock total: ");
    }

    public List<Documento> buscarDocumentos(String palabra, String tipo) {
        StringBuilder sql = new StringBuilder("SELECT id, titulo, ubicacion, tipo, stock_disponible, stock_total, COALESCE(campos_especificos_json, '{}') AS campos_especificos_json FROM Documentos WHERE 1=1");
        boolean filtrarTipo = tipo != null && !tipo.isBlank();
        boolean filtrarPalabra = palabra != null && !palabra.isBlank();

        if (filtrarTipo) {
            sql.append(" AND tipo = ?");
        }
        if (filtrarPalabra) {
            sql.append(" AND (titulo LIKE ? OR ubicacion LIKE ? OR campos_especificos_json LIKE ?)");
        }
        sql.append(" ORDER BY tipo, titulo");

        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (filtrarTipo) {
                ps.setString(index++, tipo);
            }
            if (filtrarPalabra) {
                String valor = "%" + palabra.trim() + "%";
                ps.setString(index++, valor);
                ps.setString(index++, valor);
                ps.setString(index, valor);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Documento> documentos = new ArrayList<>();
                while (rs.next()) {
                    documentos.add(new Documento(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("ubicacion"),
                        rs.getString("tipo"),
                        rs.getInt("stock_disponible"),
                        rs.getInt("stock_total"),
                        rs.getString("campos_especificos_json")
                    ));
                }
                return documentos;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar documentos: " + e.getMessage(), e);
        }
    }

    private int sumarStock(String columna, String prefijoError) {
        String sql = "SELECT COALESCE(SUM(" + columna + "), 0) AS total FROM Documentos";
        try (Connection connection = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(prefijoError + e.getMessage(), e);
        }
        return 0;
    }
}
