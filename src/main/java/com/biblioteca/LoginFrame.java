package com.biblioteca;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginFrame extends JFrame {

    private final JTextField campoUsuario;
    private final JPasswordField campoPassword;
    private final JButton botonLogin;
    private final JLabel etiquetaError;

    public LoginFrame() {
        setTitle("Biblioteca - Iniciar sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(470, 330);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = AppStyle.buildRootPanel();
        setContentPane(root);

        JPanel panel = AppStyle.buildSurfacePanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 26, 20, 26));
        root.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ── Título ───────────────────────────────────────────────────────
        JLabel titulo = new JLabel("Sistema de Biblioteca", SwingConstants.CENTER);
        AppStyle.styleTitle(titulo);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        JLabel subtitulo = new JLabel("Colegio Amigos De Don Bosco", SwingConstants.CENTER);
        AppStyle.styleSubtitle(subtitulo);
        gbc.gridy = 1;
        panel.add(subtitulo, gbc);

        // ── Usuario ──────────────────────────────────────────────────────
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblUsuario = new JLabel("Usuario:");
        AppStyle.styleBodyLabel(lblUsuario);
        panel.add(lblUsuario, gbc);

        campoUsuario = new JTextField(18);
        AppStyle.styleTextField(campoUsuario);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(campoUsuario, gbc);

        // ── Contraseña ───────────────────────────────────────────────────
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblPassword = new JLabel("Contrasena:");
        AppStyle.styleBodyLabel(lblPassword);
        panel.add(lblPassword, gbc);

        campoPassword = new JPasswordField(18);
        AppStyle.styleTextField(campoPassword);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(campoPassword, gbc);

        // ── Botón ────────────────────────────────────────────────────────
        botonLogin = new JButton("Ingresar");
        AppStyle.stylePrimaryButton(botonLogin);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(botonLogin, gbc);

        // ── Mensaje de error ─────────────────────────────────────────────
        etiquetaError = new JLabel("", SwingConstants.CENTER);
        etiquetaError.setForeground(AppStyle.WARNING);
        etiquetaError.setFont(AppStyle.FONT_BODY);
        gbc.gridy = 5;
        panel.add(etiquetaError, gbc);

        // ── Listeners ────────────────────────────────────────────────────
        ActionListener accionLogin = e -> intentarLogin();
        botonLogin.addActionListener(accionLogin);
        // Permitir login con Enter desde cualquier campo
        campoUsuario.addActionListener(accionLogin);
        campoPassword.addActionListener(accionLogin);
    }

    private void intentarLogin() {
        String nombre = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();

        if (nombre.isEmpty() || password.isEmpty()) {
            etiquetaError.setText("Completa todos los campos.");
            return;
        }

        String[] resultado = autenticar(nombre, password);
        if (resultado == null) {
            etiquetaError.setText("Usuario o contraseña incorrectos.");
            campoPassword.setText("");
            return;
        }

        int idUsuario = Integer.parseInt(resultado[0]);
        String nombreUsuario = resultado[1];
        String rol = resultado[2];

        dispose(); // cierra la ventana de login

        SwingUtilities.invokeLater(() -> {
            JFrame ventana = switch (rol) {
                case "ADMIN"    -> new AdminFrame(idUsuario, nombreUsuario);
                case "PROFESOR" -> new AlumnoFrame(idUsuario, nombreUsuario);
                default          -> new ProfesorFrame(idUsuario, nombreUsuario);
            };
            ventana.setVisible(true);
        });
    }

    /**
     * Consulta la BD y retorna {id, nombre, rol} si las credenciales son válidas, o null.
     *
     * NOTA DE SEGURIDAD: el SQL usa PreparedStatement para prevenir inyección SQL.
     * La comparación de contraseña se hace en Java sobre el hash SHA-256.
     * En producción usa bcrypt o Argon2 en lugar de SHA-256.
     */
    private String[] autenticar(String nombre, String password) {
        // Para los datos de prueba del SQL (prefijo "hash_"), comparación directa.
        // En producción: reemplaza por verificación bcrypt.
        String sql = "SELECT id, nombre, rol, password FROM Usuarios WHERE nombre = ?";

        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hashAlmacenado = rs.getString("password");
                        // Verificar contraseña: acepta hash SHA-256 o texto de prueba directo
                        if (verificarPassword(password, hashAlmacenado)) {
                            return new String[]{
                                String.valueOf(rs.getInt("id")),
                                rs.getString("nombre"),
                                rs.getString("rol")
                            };
                        }
                    }
                }
            }
        } catch (SQLException e) {
            etiquetaError.setText("Error de conexión con la base de datos.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Compara la contraseña ingresada con el hash almacenado.
     * Soporta:
     *   • Hash SHA-256 hexadecimal (producción mínima)
     *   • Texto plano / marcadores de prueba (desarrollo)
     */
    private boolean verificarPassword(String ingresada, String almacenada) {
        // Intento 1: comparar SHA-256
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(ingresada.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            if (sb.toString().equals(almacenada)) return true;
        } catch (NoSuchAlgorithmException ignored) { }

        // Intento 2: comparación directa (datos de prueba del SQL)
        return ingresada.equals(almacenada);
    }
}
