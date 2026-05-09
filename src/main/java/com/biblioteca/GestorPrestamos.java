package com.biblioteca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GestorPrestamos {

    private final Connection conexion;
    private final String columnaDisponibles;

    public GestorPrestamos() {
        this.conexion = ConexionBD.getInstancia().getConexion();
        this.columnaDisponibles = detectarColumnaDisponibles();
    }

    /**
     * Verifica si el usuario tiene préstamos vencidos sin devolver o mora acumulada > 0.
     *
     * @param usuarioId ID del usuario a verificar
     * @return true si el usuario tiene deudas pendientes
     */
    public boolean verificarMora(int usuarioId) {
        String sql = """
                SELECT COUNT(*) AS pendientes
                FROM Prestamos p
                JOIN Usuarios u ON u.id = p.id_usuario
                WHERE p.id_usuario = ?
                  AND (
                      u.mora_acumulada > 0
                      OR (
                          p.fecha_devolucion IS NULL
                          AND julianday('now') - julianday(p.fecha_salida) > ?
                      )
                  )
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, getConfigInt("dias_prestamo", 7));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("pendientes") > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar mora del usuario " + usuarioId + ": " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Registra un préstamo si el usuario no tiene mora y hay ejemplares disponibles.
     *
     * @param usuarioId   ID del usuario que solicita el préstamo
     * @param documentoId ID del documento a prestar
     * @return true si el préstamo se registró correctamente; false en caso contrario
     */
    public boolean realizarPrestamo(int usuarioId, int documentoId) {
        // 1. Verificar mora del usuario
        if (verificarMora(usuarioId)) {
            System.out.println("Préstamo rechazado: el usuario " + usuarioId + " tiene mora pendiente.");
            return false;
        }

        // 1.1 Verificar cantidad maxima de prestamos activos por usuario
        int maxPrestamos = getConfigInt("max_prestamos", 3);
        String sqlActivos = "SELECT COUNT(*) AS activos FROM Prestamos WHERE id_usuario = ? AND fecha_devolucion IS NULL";
        try (PreparedStatement ps = conexion.prepareStatement(sqlActivos)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("activos") >= maxPrestamos) {
                    System.out.println("Préstamo rechazado: el usuario superó el límite de préstamos activos.");
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar préstamos activos: " + e.getMessage(), e);
        }

        // 2. Verificar stock disponible
        String consultaStock = "SELECT " + columnaDisponibles + " AS disponibles FROM Documentos WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(consultaStock)) {
            ps.setInt(1, documentoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Préstamo rechazado: el documento " + documentoId + " no existe.");
                    return false;
                }
                if (rs.getInt("disponibles") <= 0) {
                    System.out.println("Préstamo rechazado: no hay ejemplares disponibles del documento " + documentoId + ".");
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar stock del documento " + documentoId + ": " + e.getMessage(), e);
        }

        // 3. Registrar el préstamo y decrementar el stock en una transacción
        String insertPrestamo = """
                INSERT INTO Prestamos (id_usuario, id_documento, fecha_salida, mora_acumulada)
                VALUES (?, ?, ?, 0.0)
                """;
        String actualizarStock = "UPDATE Documentos SET " + columnaDisponibles + " = " + columnaDisponibles + " - 1 "
            + "WHERE id = ? AND " + columnaDisponibles + " > 0";

        try {
            conexion.setAutoCommit(false);

            try (PreparedStatement psInsert = conexion.prepareStatement(insertPrestamo);
                 PreparedStatement psStock  = conexion.prepareStatement(actualizarStock)) {

                psInsert.setInt(1, usuarioId);
                psInsert.setInt(2, documentoId);
                psInsert.setString(3, LocalDate.now().toString());
                psInsert.executeUpdate();

                psStock.setInt(1, documentoId);
                int filasActualizadas = psStock.executeUpdate();

                if (filasActualizadas == 0) {
                    // Otra transacción tomó el último ejemplar justo antes: revertir
                    conexion.rollback();
                    System.out.println("Préstamo rechazado: ya no quedan ejemplares disponibles (condición de carrera).");
                    return false;
                }

                conexion.commit();
                System.out.println("Préstamo registrado correctamente para el usuario " + usuarioId + ".");
                return true;
            }
        } catch (SQLException e) {
            try { conexion.rollback(); } catch (SQLException ex) { /* ignorar error de rollback */ }
            throw new RuntimeException("Error al registrar el préstamo: " + e.getMessage(), e);
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException e) { /* ignorar */ }
        }
    }

    public boolean registrarDevolucion(int prestamoId) {
        String sqlPrestamo = """
                SELECT id_usuario, id_documento, fecha_salida
                FROM Prestamos
                WHERE id = ? AND fecha_devolucion IS NULL
                """;
        String sqlUpdatePrestamo = "UPDATE Prestamos SET fecha_devolucion = ?, mora_acumulada = ? WHERE id = ?";
        String sqlUpdateDoc = "UPDATE Documentos SET " + columnaDisponibles + " = " + columnaDisponibles + " + 1 WHERE id = ?";
        String sqlUpdateUsuarioMora = "UPDATE Usuarios SET mora_acumulada = COALESCE(mora_acumulada, 0) + ? WHERE id = ?";

        try {
            conexion.setAutoCommit(false);

            int idUsuario;
            int idDocumento;
            LocalDate fechaSalida;

            try (PreparedStatement ps = conexion.prepareStatement(sqlPrestamo)) {
                ps.setInt(1, prestamoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conexion.rollback();
                        return false;
                    }
                    idUsuario = rs.getInt("id_usuario");
                    idDocumento = rs.getInt("id_documento");
                    fechaSalida = LocalDate.parse(rs.getString("fecha_salida"));
                }
            }

            LocalDate hoy = LocalDate.now();
            int diasPrestamo = getConfigInt("dias_prestamo", 7);
            long diasTranscurridos = ChronoUnit.DAYS.between(fechaSalida, hoy);
            int diasRetraso = (int) Math.max(0, diasTranscurridos - diasPrestamo);
            double tarifaDiaria = getMoraDiaria(hoy.getYear());
            double mora = calcularMora(diasRetraso, tarifaDiaria);

            try (PreparedStatement ps = conexion.prepareStatement(sqlUpdatePrestamo)) {
                ps.setString(1, hoy.toString());
                ps.setDouble(2, mora);
                ps.setInt(3, prestamoId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conexion.prepareStatement(sqlUpdateDoc)) {
                ps.setInt(1, idDocumento);
                ps.executeUpdate();
            }

            if (mora > 0) {
                try (PreparedStatement ps = conexion.prepareStatement(sqlUpdateUsuarioMora)) {
                    ps.setDouble(1, mora);
                    ps.setInt(2, idUsuario);
                    ps.executeUpdate();
                }
            }

            conexion.commit();
            return true;
        } catch (SQLException e) {
            try { conexion.rollback(); } catch (SQLException ignored) { }
            throw new RuntimeException("Error al registrar devolución: " + e.getMessage(), e);
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    public int getConfigInt(String clave, int defaultValue) {
        String sql = "SELECT valor FROM Configuracion WHERE clave = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
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

    public double getConfigDouble(String clave, double defaultValue) {
        String sql = "SELECT valor FROM Configuracion WHERE clave = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
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
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setString(2, valor);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar configuración: " + e.getMessage(), e);
        }
    }

    public double getMoraDiaria(int anio) {
        double porAnio = getConfigDouble("mora_diaria_" + anio, -1);
        if (porAnio >= 0) {
            return porAnio;
        }
        return getConfigDouble("mora_diaria", 5.0);
    }

    /**
     * Calcula la mora total acumulada por días de retraso.
     *
     * @param diasRetraso   número de días de retraso (debe ser >= 0)
     * @param tarifaDiaria  costo por día de retraso (debe ser > 0)
     * @return monto total a pagar por mora
     */
    public double calcularMora(int diasRetraso, double tarifaDiaria) {
        if (diasRetraso < 0) {
            throw new IllegalArgumentException("Los días de retraso no pueden ser negativos.");
        }
        if (tarifaDiaria < 0) {
            throw new IllegalArgumentException("La tarifa diaria no puede ser negativa.");
        }
        return diasRetraso * tarifaDiaria;
    }

    private String detectarColumnaDisponibles() {
        String sql = "PRAGMA table_info(Documentos)";
        try (PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            boolean tieneStock = false;
            boolean tieneCantidad = false;
            while (rs.next()) {
                String nombre = rs.getString("name");
                if ("stock_disponible".equalsIgnoreCase(nombre)) {
                    tieneStock = true;
                }
                if ("cantidad_disponible".equalsIgnoreCase(nombre)) {
                    tieneCantidad = true;
                }
            }
            if (tieneStock) {
                return "stock_disponible";
            }
            if (tieneCantidad) {
                return "cantidad_disponible";
            }
        } catch (SQLException ignored) {
        }
        return "stock_disponible";
    }
}
