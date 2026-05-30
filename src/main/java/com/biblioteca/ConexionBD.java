package com.biblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Gestiona la conexión JDBC con la base de datos SQLite 'biblioteca.db'.
 * Implementa el patrón Singleton para reutilizar una única conexión.
 *
 * Dependencia requerida: sqlite-jdbc (https://github.com/xerial/sqlite-jdbc)
 * Maven:
 *   <dependency>
 *       <groupId>org.xerial</groupId>
 *       <artifactId>sqlite-jdbc</artifactId>
 *       <version>3.45.3.0</version>
 *   </dependency>
 */
public class ConexionBD {

    private static final String URL = "jdbc:sqlite:" +
        System.getProperty("user.home").replace("\\", "/") + "/biblioteca/biblioteca.db";

    private static ConexionBD instancia;
    private Connection conexion;

    // Constructor privado: impide instanciación externa
    private ConexionBD() {
        try {
            // Asegurar que existe la carpeta donde se colocará biblioteca.db
            String dbDir = System.getProperty("user.home") + File.separator + "biblioteca";
            try {
                Files.createDirectories(Paths.get(dbDir));
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear la carpeta para la base de datos: " + e.getMessage(), e);
            }
            // Registrar el driver explícitamente (necesario en algunas versiones de JDBC)
            Class.forName("org.sqlite.JDBC");
            this.conexion = DriverManager.getConnection(URL);
            // Activar claves foráneas (desactivadas por defecto en SQLite)
            this.conexion.createStatement().execute("PRAGMA foreign_keys = ON;");
            inicializarEsquema();
            System.out.println("Conexión establecida con biblioteca.db");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver SQLite no encontrado. Verifica que sqlite-jdbc esté en el classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    private void inicializarEsquema() {
        try (Statement st = conexion.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    rol TEXT NOT NULL CHECK(rol IN ('ADMIN','PROFESOR','ALUMNO')),
                    mora_acumulada REAL DEFAULT 0
                )
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Documentos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    ubicacion TEXT NOT NULL,
                    tipo TEXT NOT NULL CHECK (tipo IN ('LIBRO', 'REVISTA', 'CD', 'TESIS')),
                    stock_total INTEGER NOT NULL CHECK(stock_total >= 0),
                    stock_disponible INTEGER NOT NULL CHECK(stock_disponible >= 0),
                    campos_especificos_json TEXT NOT NULL DEFAULT '{}'
                )
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Prestamos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_usuario INTEGER NOT NULL,
                    id_documento INTEGER NOT NULL,
                    fecha_salida TEXT NOT NULL,
                    fecha_devolucion TEXT,
                    mora_acumulada REAL DEFAULT 0,
                    FOREIGN KEY(id_usuario) REFERENCES Usuarios(id),
                    FOREIGN KEY(id_documento) REFERENCES Documentos(id)
                )
                """);

            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Configuracion (
                    clave TEXT PRIMARY KEY,
                    valor TEXT NOT NULL
                )
                """);

            if (!existeColumna("Usuarios", "mora_acumulada")) {
                st.executeUpdate("ALTER TABLE Usuarios ADD COLUMN mora_acumulada REAL DEFAULT 0");
            }

            if (!existeColumna("Documentos", "stock_total") && existeColumna("Documentos", "cantidad_total")) {
                st.executeUpdate("ALTER TABLE Documentos ADD COLUMN stock_total INTEGER DEFAULT 0");
                st.executeUpdate("UPDATE Documentos SET stock_total = cantidad_total");
            }
            if (!existeColumna("Documentos", "stock_disponible") && existeColumna("Documentos", "cantidad_disponible")) {
                st.executeUpdate("ALTER TABLE Documentos ADD COLUMN stock_disponible INTEGER DEFAULT 0");
                st.executeUpdate("UPDATE Documentos SET stock_disponible = cantidad_disponible");
            }
            if (!existeColumna("Documentos", "campos_especificos_json")) {
                st.executeUpdate("ALTER TABLE Documentos ADD COLUMN campos_especificos_json TEXT DEFAULT '{}'");
            }

            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('max_prestamos', '3')");
            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('max_prestamos_alumno', '3')");
            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('max_prestamos_profesor', '6')");
            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('dias_prestamo', '7')");
            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('dias_prestamo_alumno', '7')");
            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('dias_prestamo_profesor', '14')");
            st.executeUpdate("INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('mora_diaria', '5.0')");
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar esquema SQLite: " + e.getMessage(), e);
        }
    }

    private boolean existeColumna(String tabla, String columna) throws SQLException {
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(" + tabla + ")")) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Devuelve la instancia única de ConexionBD (Singleton).
     * Crea la conexión la primera vez que se invoca.
     *
     * @return instancia de ConexionBD
     */
    public static ConexionBD getInstancia() {
        if (instancia == null || !instancia.isConectado()) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Devuelve el objeto {@link Connection} listo para ejecutar sentencias SQL.
     *
     * @return conexión activa con la base de datos
     */
    public Connection getConexion() {
        return conexion;
    }

    /**
     * Comprueba si la conexión sigue abierta y válida.
     *
     * @return {@code true} si la conexión está activa
     */
    public boolean isConectado() {
        try {
            return conexion != null && !conexion.isClosed() && conexion.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Cierra la conexión con la base de datos y libera el Singleton.
     * Debe llamarse al finalizar la aplicación.
     */
    public void cerrarConexion() {
        if (isConectado()) {
            try {
                conexion.close();
                instancia = null;
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------
    //  Ejemplo de uso (puede eliminarse en producción)
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        ConexionBD bd = ConexionBD.getInstancia();

        if (bd.isConectado()) {
            System.out.println("Base de datos lista.");
            // Ejemplo: bd.getConexion().prepareStatement("SELECT * FROM Usuarios");
        }

        bd.cerrarConexion();
    }
}
