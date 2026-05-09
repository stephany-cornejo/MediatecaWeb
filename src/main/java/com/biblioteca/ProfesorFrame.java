package com.biblioteca;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProfesorFrame extends JFrame {

    private final int idUsuario;

    public ProfesorFrame(int idUsuario, String nombreUsuario) {
        this.idUsuario = idUsuario;
        setTitle("Panel Alumno — " + nombreUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 560);
        setLocationRelativeTo(null);

        JPanel panel = AppStyle.buildRootPanel();
        setContentPane(panel);

        JPanel topPanel = AppStyle.buildSurfacePanel(new BorderLayout());
        JLabel bienvenida = new JLabel("Bienvenido, " + nombreUsuario + " [ALUMNO]", SwingConstants.CENTER);
        AppStyle.styleTitle(bienvenida);
        topPanel.add(bienvenida, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // ── Tabla de documentos disponibles ─────────────────────────────
        String[] columnas = {"ID", "Título", "Tipo", "Disponibles"};
        Object[][] datos = cargarDocumentos();
        JTable tabla = new JTable(new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tabla.setEnabled(false);
        AppStyle.styleTable(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        AppStyle.styleTitledBorder(scroll, "Catalogo de documentos");
        panel.add(scroll, BorderLayout.CENTER);

        // ── Acciones ─────────────────────────────────────────────────────
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setOpaque(true);
        acciones.setBackground(AppStyle.SURFACE);
        acciones.setBorder(AppStyle.cardBorder());

        JButton btnMisPrestamos = new JButton("Mis préstamos");
        JButton btnCerrar       = new JButton("Cerrar sesión");
        AppStyle.styleSecondaryButton(btnMisPrestamos);
        AppStyle.styleDangerButton(btnCerrar);

        btnMisPrestamos.addActionListener(e -> mostrarMisPrestamos());
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        acciones.add(btnMisPrestamos);
        acciones.add(btnCerrar);
        panel.add(acciones, BorderLayout.SOUTH);
    }

    private Object[][] cargarDocumentos() {
        String colDisp = columnaDisponibleDocumentos();
        String sql = "SELECT id, titulo, tipo, " + colDisp + " AS disponibles FROM Documentos ORDER BY tipo, titulo";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                java.util.List<Object[]> filas = new java.util.ArrayList<>();
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getInt("id"), rs.getString("titulo"),
                        rs.getString("tipo"), rs.getInt("disponibles")
                    });
                }
                return filas.toArray(new Object[0][]);
            }
        } catch (SQLException e) {
            return new Object[][]{{"Error", e.getMessage(), "", ""}};
        }
    }

    private void mostrarMisPrestamos() {
        String sql = """
                SELECT d.titulo, p.fecha_salida,
                       COALESCE(p.fecha_devolucion, 'Pendiente') AS devolucion,
                       p.mora_acumulada
                FROM Prestamos p
                JOIN Documentos d ON d.id = p.id_documento
                WHERE p.id_usuario = ?
                ORDER BY p.fecha_salida DESC
                """;
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    String[] cols = {"Documento", "Salida", "Devolución", "Mora ($)"};
                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getString("titulo"), rs.getString("fecha_salida"),
                            rs.getString("devolucion"), rs.getDouble("mora_acumulada")
                        });
                    }
                    if (filas.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No tienes préstamos registrados.");
                        return;
                    }
                    JTable t = new JTable(filas.toArray(new Object[0][]), cols);
                    t.setEnabled(false);
                    AppStyle.styleTable(t);
                    JOptionPane.showMessageDialog(this, new JScrollPane(t), "Mis préstamos", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String columnaDisponibleDocumentos() {
        String sql = "PRAGMA table_info(Documentos)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
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
            }
        } catch (SQLException ignored) {
        }
        return "stock_disponible";
    }
}
