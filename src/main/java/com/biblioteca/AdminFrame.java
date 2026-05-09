package com.biblioteca;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AdminFrame extends JFrame {

    private JTable tablaUsuarios;
    private final GestorPrestamos gestor;

    public AdminFrame(int idUsuario, String nombreUsuario) {
        this.gestor = new GestorPrestamos();

        setTitle("Panel Administrador - " + nombreUsuario);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);

        JPanel panel = AppStyle.buildRootPanel();
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(panel);

        JPanel topPanel = AppStyle.buildSurfacePanel(new BorderLayout(8, 8));
        JLabel bienvenida = new JLabel("Bienvenido, " + nombreUsuario + " [ADMIN]", SwingConstants.CENTER);
        AppStyle.styleTitle(bienvenida);
        topPanel.add(bienvenida, BorderLayout.NORTH);
        topPanel.add(crearBarraHerramientas(), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Rol", "Mora"};
        tablaUsuarios = new JTable(cargarUsuarios(), columnas);
        tablaUsuarios.setEnabled(false);
        AppStyle.styleTable(tablaUsuarios);

        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        AppStyle.styleTitledBorder(scroll, "Usuarios registrados");
        panel.add(scroll, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setOpaque(true);
        acciones.setBackground(AppStyle.SURFACE);
        acciones.setBorder(AppStyle.cardBorder());

        JButton btnPrestamos = new JButton("Ver todos los prestamos");
        JButton btnCerrar = new JButton("Cerrar sesion");
        AppStyle.styleSecondaryButton(btnPrestamos);
        AppStyle.styleDangerButton(btnCerrar);

        btnPrestamos.addActionListener(e -> mostrarPrestamos());
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        acciones.add(btnPrestamos);
        acciones.add(btnCerrar);
        panel.add(acciones, BorderLayout.SOUTH);
    }

    private JToolBar crearBarraHerramientas() {
        JToolBar barra = new JToolBar();
        barra.setLayout(new WrapLayout(FlowLayout.LEFT, 8, 6));
        AppStyle.styleToolbar(barra);

        JButton btnCrearUsuario = AppStyle.buildToolbarButton("Usuarios");
        JButton btnResetPassword = AppStyle.buildToolbarButton("Restablecer clave");
        JButton btnIngresarEjemplar = AppStyle.buildToolbarButton("Nuevo ejemplar");
        JButton btnConsultaEjemplares = AppStyle.buildToolbarButton("Consulta ejemplares");
        JButton btnBusquedaRapida = AppStyle.buildToolbarButton("Busqueda rapida");
        JButton btnPrestamo = AppStyle.buildToolbarButton("Registrar prestamo");
        JButton btnDevolucion = AppStyle.buildToolbarButton("Registrar devolucion");
        JButton btnConfig = AppStyle.buildToolbarButton("Configuracion");

        btnCrearUsuario.addActionListener(e -> crearUsuario());
        btnResetPassword.addActionListener(e -> restablecerPassword());
        btnIngresarEjemplar.addActionListener(e -> ingresarEjemplar());
        btnConsultaEjemplares.addActionListener(e -> consultaEjemplares());
        btnBusquedaRapida.addActionListener(e -> busquedaRapidaEjemplares());
        btnPrestamo.addActionListener(e -> registrarPrestamo());
        btnDevolucion.addActionListener(e -> registrarDevolucion());
        btnConfig.addActionListener(e -> configurarSistema());

        barra.add(btnCrearUsuario);
        barra.add(btnResetPassword);
        barra.addSeparator(new Dimension(10, 10));
        barra.add(btnIngresarEjemplar);
        barra.add(btnConsultaEjemplares);
        barra.add(btnBusquedaRapida);
        barra.addSeparator(new Dimension(10, 10));
        barra.add(btnPrestamo);
        barra.add(btnDevolucion);
        barra.addSeparator(new Dimension(10, 10));
        barra.add(btnConfig);

        // Recalcular alto de la barra cuando cambia el ancho disponible.
        barra.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                barra.revalidate();
            }
        });

        return barra;
    }

    private void crearUsuario() {
        JTextField campoNombre = new JTextField();
        JPasswordField campoPass = new JPasswordField();
        JComboBox<String> comboRol = new JComboBox<>(new String[]{"ADMIN", "PROFESOR", "ALUMNO"});
        AppStyle.styleTextField(campoNombre);
        AppStyle.styleTextField(campoPass);
        comboRol.setFont(AppStyle.FONT_BODY);

        Object[] mensaje = {
            "Nombre de usuario:", campoNombre,
            "Contrasena:", campoPass,
            "Rol:", comboRol
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Nuevo usuario", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nombre = campoNombre.getText().trim();
        String password = new String(campoPass.getPassword()).trim();
        String rol = (String) comboRol.getSelectedItem();

        if (nombre.isEmpty() || password.isEmpty() || rol == null) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Usuarios (nombre, password, rol, mora_acumulada) VALUES (?, ?, ?, 0)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, hashSha256(password));
                ps.setString(3, rol);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Usuario creado correctamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaUsuarios();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo crear el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void restablecerPassword() {
        String usuario = JOptionPane.showInputDialog(this, "Usuario a restablecer:", "Restablecer contrasena", JOptionPane.QUESTION_MESSAGE);
        if (usuario == null) {
            return;
        }
        usuario = usuario.trim();
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes indicar un usuario.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPasswordField campoNueva = new JPasswordField();
        AppStyle.styleTextField(campoNueva);
        int opcion = JOptionPane.showConfirmDialog(this, new Object[]{"Nueva contrasena:", campoNueva}, "Restablecer contrasena", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String nuevaPassword = new String(campoNueva.getPassword()).trim();
        if (nuevaPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La contrasena no puede estar vacia.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Usuarios SET password = ? WHERE nombre = ?";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, hashSha256(nuevaPassword));
                ps.setString(2, usuario);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    JOptionPane.showMessageDialog(this, "Usuario no encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Contrasena restablecida.", "Exito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo restablecer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ingresarEjemplar() {
        JComboBox<String> tipo = new JComboBox<>(new String[]{"LIBRO", "REVISTA", "CD", "TESIS"});
        JTextField titulo = new JTextField();
        JTextField ubicacion = new JTextField();
        JTextField stockTotal = new JTextField("1");
        JTextField campo1 = new JTextField();
        JTextField campo2 = new JTextField();
        JTextField campo3 = new JTextField();
        tipo.setFont(AppStyle.FONT_BODY);
        AppStyle.styleTextField(titulo);
        AppStyle.styleTextField(ubicacion);
        AppStyle.styleTextField(stockTotal);
        AppStyle.styleTextField(campo1);
        AppStyle.styleTextField(campo2);
        AppStyle.styleTextField(campo3);

        JLabel etiquetaCampo1 = new JLabel();
        JLabel etiquetaCampo2 = new JLabel();
        JLabel etiquetaCampo3 = new JLabel();

        Runnable actualizarEtiquetas = () -> {
            String tipoDoc = (String) tipo.getSelectedItem();
            String[] etiquetas = obtenerEtiquetasCampos(tipoDoc);
            etiquetaCampo1.setText(etiquetas[0] + ":");
            etiquetaCampo2.setText(etiquetas[1] + ":");
            etiquetaCampo3.setText(etiquetas[2] + ":");
        };

        tipo.addActionListener(e -> actualizarEtiquetas.run());
        actualizarEtiquetas.run();

        Object[] mensaje = {
            "Tipo:", tipo,
            "Titulo:", titulo,
            "Ubicacion fisica:", ubicacion,
            "Cantidad total:", stockTotal,
            etiquetaCampo1, campo1,
            etiquetaCampo2, campo2,
            etiquetaCampo3, campo3
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Nuevo ejemplar", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        String tipoDoc = (String) tipo.getSelectedItem();
        String tituloDoc = titulo.getText().trim();
        String ub = ubicacion.getText().trim();
        String valorCampo1 = campo1.getText().trim();
        String valorCampo2 = campo2.getText().trim();
        String valorCampo3 = campo3.getText().trim();
        int total;
        try {
            total = Integer.parseInt(stockTotal.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad total invalida.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tipoDoc == null || tituloDoc.isEmpty() || ub.isEmpty() || valorCampo1.isEmpty() || valorCampo2.isEmpty() || valorCampo3.isEmpty() || total < 1) {
            JOptionPane.showMessageDialog(this, "No se pueden dejar campos vacios. Completa todos los datos.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String extras = construirJsonExtras(tipoDoc, valorCampo1, valorCampo2, valorCampo3);

        boolean tieneStockTotal = existeColumnaDocumentos("stock_total");
        boolean tieneStockDisp = existeColumnaDocumentos("stock_disponible");
        boolean tieneCantidadTotal = existeColumnaDocumentos("cantidad_total");
        boolean tieneCantidadDisp = existeColumnaDocumentos("cantidad_disponible");

        StringBuilder columnas = new StringBuilder("titulo, ubicacion, tipo");
        StringBuilder valores = new StringBuilder("?, ?, ?");

        if (tieneStockTotal) {
            columnas.append(", stock_total");
            valores.append(", ?");
        }
        if (tieneStockDisp) {
            columnas.append(", stock_disponible");
            valores.append(", ?");
        }
        if (tieneCantidadTotal) {
            columnas.append(", cantidad_total");
            valores.append(", ?");
        }
        if (tieneCantidadDisp) {
            columnas.append(", cantidad_disponible");
            valores.append(", ?");
        }
        if (existeColumnaDocumentos("campos_especificos_json")) {
            columnas.append(", campos_especificos_json");
            valores.append(", ?");
        }

        String sql = "INSERT INTO Documentos (" + columnas + ") VALUES (" + valores + ")";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int idx = 1;
                ps.setString(idx++, tituloDoc);
                ps.setString(idx++, ub);
                ps.setString(idx++, tipoDoc);
                if (tieneStockTotal) {
                    ps.setInt(idx++, total);
                }
                if (tieneStockDisp) {
                    ps.setInt(idx++, total);
                }
                if (tieneCantidadTotal) {
                    ps.setInt(idx++, total);
                }
                if (tieneCantidadDisp) {
                    ps.setInt(idx++, total);
                }
                if (existeColumnaDocumentos("campos_especificos_json")) {
                    ps.setString(idx++, extras);
                }
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Ejemplar registrado.", "Exito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar ejemplar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] obtenerEtiquetasCampos(String tipoDoc) {
        if (tipoDoc == null) {
            return new String[]{"Campo 1", "Campo 2", "Campo 3"};
        }

        return switch (tipoDoc) {
            case "LIBRO" -> new String[]{"Autor", "ISBN", "Editorial"};
            case "REVISTA" -> new String[]{"Periodicidad", "Fecha publicacion", "Detalle"};
            case "CD" -> new String[]{"Genero", "Duracion", "Detalle"};
            case "TESIS" -> new String[]{"Autor", "Carrera", "Detalle"};
            default -> new String[]{"Campo 1", "Campo 2", "Campo 3"};
        };
    }

    private String construirJsonExtras(String tipo, String a, String b, String c) {
        String v1 = escaparJson(a);
        String v2 = escaparJson(b);
        String v3 = escaparJson(c);
        return switch (tipo) {
            case "LIBRO" -> "{\"autor\":\"" + v1 + "\",\"isbn\":\"" + v2 + "\",\"editorial\":\"" + v3 + "\"}";
            case "REVISTA" -> "{\"periodicidad\":\"" + v1 + "\",\"fechaPublicacion\":\"" + v2 + "\",\"detalle\":\"" + v3 + "\"}";
            case "CD" -> "{\"genero\":\"" + v1 + "\",\"duracion\":\"" + v2 + "\",\"detalle\":\"" + v3 + "\"}";
            case "TESIS" -> "{\"autor\":\"" + v1 + "\",\"carrera\":\"" + v2 + "\",\"detalle\":\"" + v3 + "\"}";
            default -> "{}";
        };
    }

    private String escaparJson(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void consultaEjemplares() {
        String colTotal = columnaTotalDocumentos();
        String colDisp = columnaDisponibleDocumentos();
        String sql = """
            SELECT id, titulo, tipo, ubicacion,
                   %s AS total,
                   %s AS disponibles,
                   (%s - %s) AS prestados
            FROM Documentos
            ORDER BY tipo, titulo
            """.formatted(colTotal, colDisp, colTotal, colDisp);
        mostrarTablaConsulta(sql, "Consulta de ejemplares", new String[]{"ID", "Titulo", "Tipo", "Ubicacion", "Total", "Disponibles", "Prestados"});
    }

    private void busquedaRapidaEjemplares() {
        String criterio = JOptionPane.showInputDialog(this, "Buscar por titulo, tipo o ubicacion:", "Busqueda rapida", JOptionPane.QUESTION_MESSAGE);
        if (criterio == null || criterio.trim().isEmpty()) {
            return;
        }

        String colTotal = columnaTotalDocumentos();
        String colDisp = columnaDisponibleDocumentos();

        String sql = """
            SELECT id, titulo, tipo, ubicacion,
                   %s AS total,
                   %s AS disponibles,
                   (%s - %s) AS prestados
            FROM Documentos
            WHERE titulo LIKE ? OR tipo LIKE ? OR ubicacion LIKE ?
            ORDER BY titulo
            """.formatted(colTotal, colDisp, colTotal, colDisp);

        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                String filtro = "%" + criterio.trim() + "%";
                ps.setString(1, filtro);
                ps.setString(2, filtro);
                ps.setString(3, filtro);
                try (ResultSet rs = ps.executeQuery()) {
                    java.util.List<Object[]> filas = new java.util.ArrayList<>();
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getInt("id"), rs.getString("titulo"), rs.getString("tipo"),
                            rs.getString("ubicacion"), rs.getInt("total"),
                            rs.getInt("disponibles"), rs.getInt("prestados")
                        });
                    }
                    if (filas.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No se encontraron ejemplares.");
                        return;
                    }
                    JTable tabla = new JTable(filas.toArray(new Object[0][]),
                        new String[]{"ID", "Titulo", "Tipo", "Ubicacion", "Total", "Disponibles", "Prestados"});
                    tabla.setEnabled(false);
                    AppStyle.styleTable(tabla);
                    JOptionPane.showMessageDialog(this, new JScrollPane(tabla), "Resultados", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en busqueda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarPrestamo() {
        String usuarioSeleccion = seleccionarUsuario();
        if (usuarioSeleccion == null) {
            return;
        }
        String documentoSeleccion = seleccionarDocumentoDisponible();
        if (documentoSeleccion == null) {
            return;
        }

        int usuarioId = Integer.parseInt(usuarioSeleccion.split(" - ")[0]);
        int documentoId = Integer.parseInt(documentoSeleccion.split(" - ")[0]);

        boolean ok = gestor.realizarPrestamo(usuarioId, documentoId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Prestamo registrado correctamente.", "Exito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo registrar el prestamo. Verifica mora, limite de prestamos y disponibilidad.",
                "Prestamo rechazado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void registrarDevolucion() {
        String prestamo = seleccionarPrestamoActivo();
        if (prestamo == null) {
            return;
        }

        int prestamoId = Integer.parseInt(prestamo.split(" - ")[0]);
        boolean ok = gestor.registrarDevolucion(prestamoId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Devolucion registrada y mora calculada.", "Exito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la devolucion.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarSistema() {
        JTextField campoMax = new JTextField(String.valueOf(gestor.getConfigInt("max_prestamos", 3)));
        JTextField campoDias = new JTextField(String.valueOf(gestor.getConfigInt("dias_prestamo", 7)));
        JTextField campoMoraBase = new JTextField(String.valueOf(gestor.getConfigDouble("mora_diaria", 5.0)));
        JTextField campoAnio = new JTextField(String.valueOf(java.time.LocalDate.now().getYear()));
        JTextField campoMoraAnio = new JTextField();

        Object[] mensaje = {
            "Maximo de ejemplares por usuario:", campoMax,
            "Dias permitidos antes de mora:", campoDias,
            "Mora diaria por defecto:", campoMoraBase,
            "Anio para tarifa especifica (opcional):", campoAnio,
            "Mora diaria para ese anio (opcional):", campoMoraAnio
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Configuracion del sistema", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int max = Integer.parseInt(campoMax.getText().trim());
            int dias = Integer.parseInt(campoDias.getText().trim());
            double moraBase = Double.parseDouble(campoMoraBase.getText().trim());

            if (max < 1 || dias < 1 || moraBase < 0) {
                throw new NumberFormatException();
            }

            gestor.setConfig("max_prestamos", String.valueOf(max));
            gestor.setConfig("dias_prestamo", String.valueOf(dias));
            gestor.setConfig("mora_diaria", String.valueOf(moraBase));

            String anioTxt = campoAnio.getText().trim();
            String moraAnioTxt = campoMoraAnio.getText().trim();
            if (!anioTxt.isEmpty() && !moraAnioTxt.isEmpty()) {
                int anio = Integer.parseInt(anioTxt);
                double moraAnio = Double.parseDouble(moraAnioTxt);
                if (anio > 0 && moraAnio >= 0) {
                    gestor.setConfig("mora_diaria_" + anio, String.valueOf(moraAnio));
                }
            }

            JOptionPane.showMessageDialog(this, "Configuracion actualizada.", "Exito", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valores invalidos. Revisa numeros y rangos.", "Validacion", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String seleccionarUsuario() {
        String sql = "SELECT id, nombre, rol FROM Usuarios ORDER BY nombre";
        java.util.List<String> opciones = new java.util.ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    opciones.add(rs.getInt("id") + " - " + rs.getString("nombre") + " (" + rs.getString("rol") + ")");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando usuarios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (opciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios registrados.");
            return null;
        }

        return (String) JOptionPane.showInputDialog(this, "Selecciona usuario:", "Prestamo",
            JOptionPane.QUESTION_MESSAGE, null, opciones.toArray(), opciones.get(0));
    }

    private String seleccionarDocumentoDisponible() {
        String colDisp = columnaDisponibleDocumentos();
        String sql = "SELECT id, titulo, " + colDisp + " AS disponibles FROM Documentos WHERE " + colDisp + " > 0 ORDER BY titulo";
        java.util.List<String> opciones = new java.util.ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    opciones.add(rs.getInt("id") + " - " + rs.getString("titulo") + " (disp: " + rs.getInt("disponibles") + ")");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando documentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (opciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ejemplares disponibles.");
            return null;
        }

        return (String) JOptionPane.showInputDialog(this, "Selecciona documento:", "Prestamo",
            JOptionPane.QUESTION_MESSAGE, null, opciones.toArray(), opciones.get(0));
    }

    private String seleccionarPrestamoActivo() {
        String sql = """
            SELECT p.id, u.nombre, d.titulo, p.fecha_salida
            FROM Prestamos p
            JOIN Usuarios u ON u.id = p.id_usuario
            JOIN Documentos d ON d.id = p.id_documento
            WHERE p.fecha_devolucion IS NULL
            ORDER BY p.fecha_salida
            """;
        java.util.List<String> opciones = new java.util.ArrayList<>();
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    opciones.add(rs.getInt("id") + " - " + rs.getString("nombre") + " | " + rs.getString("titulo") + " | salida: " + rs.getString("fecha_salida"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando prestamos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (opciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay prestamos activos.");
            return null;
        }

        return (String) JOptionPane.showInputDialog(this, "Selecciona prestamo a devolver:", "Devolucion",
            JOptionPane.QUESTION_MESSAGE, null, opciones.toArray(), opciones.get(0));
    }

    private Object[][] cargarUsuarios() {
        String sql = "SELECT id, nombre, rol, COALESCE(mora_acumulada, 0) AS mora FROM Usuarios ORDER BY rol, nombre";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                java.util.List<Object[]> filas = new java.util.ArrayList<>();
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getDouble("mora")
                    });
                }
                return filas.toArray(new Object[0][]);
            }
        } catch (SQLException e) {
            return new Object[][]{{"Error", e.getMessage(), "", ""}};
        }
    }

    private void refrescarTablaUsuarios() {
        String[] columnas = {"ID", "Nombre", "Rol", "Mora"};
        Object[][] datos = cargarUsuarios();
        tablaUsuarios.setModel(new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    private void mostrarPrestamos() {
        String sql = """
                SELECT p.id, u.nombre AS usuario, d.titulo, p.fecha_salida,
                       COALESCE(p.fecha_devolucion, 'Pendiente') AS devolucion,
                       p.mora_acumulada
                FROM Prestamos p
                JOIN Usuarios u ON u.id = p.id_usuario
                JOIN Documentos d ON d.id = p.id_documento
                ORDER BY p.fecha_salida DESC
                """;
        mostrarTablaConsulta(sql, "Todos los prestamos", new String[]{"ID", "Usuario", "Documento", "Salida", "Devolucion", "Mora ($)"});
    }

    private void mostrarTablaConsulta(String sql, String titulo, String[] columnas) {
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                java.util.List<Object[]> filas = new java.util.ArrayList<>();
                while (rs.next()) {
                    Object[] fila = new Object[columnas.length];
                    for (int i = 0; i < columnas.length; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    filas.add(fila);
                }
                JTable t = new JTable(filas.toArray(new Object[0][]), columnas);
                t.setEnabled(false);
                AppStyle.styleTable(t);
                JOptionPane.showMessageDialog(this, new JScrollPane(t), titulo, JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashSha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return texto;
        }
    }

    private String columnaTotalDocumentos() {
        return existeColumnaDocumentos("stock_total") ? "stock_total" : "cantidad_total";
    }

    private String columnaDisponibleDocumentos() {
        return existeColumnaDocumentos("stock_disponible") ? "stock_disponible" : "cantidad_disponible";
    }

    private boolean existeColumnaDocumentos(String nombreColumna) {
        String sql = "PRAGMA table_info(Documentos)";
        try {
            Connection con = ConexionBD.getInstancia().getConexion();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    if (nombreColumna.equalsIgnoreCase(rs.getString("name"))) {
                        return true;
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return false;
    }
}
