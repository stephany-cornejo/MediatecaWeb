package com.mediateca;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.sqlite.SQLiteDataSource;

/**
 * Gestiona la conexión JDBC con la base de datos SQLite 'mediateca'
 * ubicada en la raíz del proyecto.
 */
public class ConexionBD {

    private static final String JNDI_NAME = "java:comp/env/jdbc/MediatecaDS";
    private static final String DB_PATH = resolverRutaBaseDatos();
    private static final String URL = "jdbc:sqlite:" + DB_PATH.replace("\\", "/");

    private static ConexionBD instancia;
    private final DataSource dataSource;
    private volatile boolean esquemaInicializado;

    private static String resolverRutaBaseDatos() {
        String rutaConfigurada = System.getProperty("mediateca.db.path");
        if (rutaConfigurada != null && !rutaConfigurada.trim().isEmpty()) {
            return new File(rutaConfigurada.trim()).getAbsolutePath();
        }

        File proyectoDesdeUserDir = buscarRaizProyecto(new File(System.getProperty("user.dir", ".")));
        if (proyectoDesdeUserDir != null) {
            return new File(proyectoDesdeUserDir, "mediateca").getAbsolutePath();
        }

        try {
            File codeSource = new File(ConexionBD.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
            File proyectoDesdeCodigo = buscarRaizProyecto(codeSource);
            if (proyectoDesdeCodigo != null) {
                return new File(proyectoDesdeCodigo, "mediateca").getAbsolutePath();
            }
        } catch (Exception ignored) {
            // Si no puede resolverse desde el classpath, se usan los fallbacks inferiores.
        }

        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && !catalinaBase.trim().isEmpty()) {
            return new File(catalinaBase, "mediateca").getAbsolutePath();
        }

        return new File(System.getProperty("user.dir", "."), "mediateca").getAbsolutePath();
    }

    private static File buscarRaizProyecto(File inicio) {
        File actual = inicio;
        if (actual != null && actual.isFile()) {
            actual = actual.getParentFile();
        }

        while (actual != null) {
            File pom = new File(actual, "pom.xml");
            File bd = new File(actual, "mediateca");
            if (pom.isFile() && bd.exists()) {
                return actual;
            }
            actual = actual.getParentFile();
        }
        return null;
    }

    // Constructor privado: impide instanciación externa
    private ConexionBD() {
        try {
            // Registrar el driver explícitamente (necesario en algunas versiones de JDBC)
            Class.forName("org.sqlite.JDBC");
            this.dataSource = resolverDataSource();
            inicializarEsquemaSiEsNecesario();
            System.out.println("DataSource inicializado para mediateca en: " + DB_PATH);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver SQLite no encontrado. Verifica que sqlite-jdbc esté en el classpath.", e);
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(
                "Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    private DataSource resolverDataSource() throws NamingException {
        try {
            return (DataSource) new InitialContext().lookup(JNDI_NAME);
        } catch (NamingException e) {
            SQLiteDataSource fallback = new SQLiteDataSource();
            fallback.setUrl(URL);
            return fallback;
        }
    }

    private void inicializarEsquemaSiEsNecesario() throws SQLException {
        if (esquemaInicializado) {
            return;
        }
        synchronized (this) {
            if (esquemaInicializado) {
                return;
            }
            try (Connection conexion = dataSource.getConnection();
                 Statement pragma = conexion.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON;");
                inicializarEsquema(conexion);
            }
            esquemaInicializado = true;
        }
    }

    private void inicializarEsquema(Connection conexion) {
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

            if (!existeColumna(conexion, "Usuarios", "mora_acumulada")) {
                st.executeUpdate("ALTER TABLE Usuarios ADD COLUMN mora_acumulada REAL DEFAULT 0");
            }

            if (!existeColumna(conexion, "Documentos", "stock_total")
                && existeColumna(conexion, "Documentos", "cantidad_total")) {
                st.executeUpdate("ALTER TABLE Documentos ADD COLUMN stock_total INTEGER DEFAULT 0");
                st.executeUpdate("UPDATE Documentos SET stock_total = cantidad_total");
            }
            if (!existeColumna(conexion, "Documentos", "stock_disponible")
                && existeColumna(conexion, "Documentos", "cantidad_disponible")) {
                st.executeUpdate("ALTER TABLE Documentos ADD COLUMN stock_disponible INTEGER DEFAULT 0");
                st.executeUpdate("UPDATE Documentos SET stock_disponible = cantidad_disponible");
            }
            if (!existeColumna(conexion, "Documentos", "campos_especificos_json")) {
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

    private boolean existeColumna(Connection conexion, String tabla, String columna) throws SQLException {
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
        if (instancia == null) {
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
        try {
            Connection conexion = dataSource.getConnection();
            try (Statement pragma = conexion.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON;");
            }
            inicializarEsquemaSiEsNecesario();
            return conexion;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo obtener una conexión desde el pool: " + e.getMessage(), e);
        }
    }

    /**
     * Comprueba si la conexión sigue abierta y válida.
     *
     * @return {@code true} si la conexión está activa
     */
    public boolean isConectado() {
        try (Connection conexion = dataSource.getConnection()) {
            return conexion.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Cierra la conexión con la base de datos y libera el Singleton.
     * Debe llamarse al finalizar la aplicación.
     */
    public void cerrarConexion() {
        // El ciclo de vida del pool/JNDI lo administra Tomcat.
        instancia = null;
    }

    // ------------------------------------------------------------------
    //  Ejemplo de uso (puede eliminarse en producción)
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        ConexionBD bd = ConexionBD.getInstancia();

        try (Connection conexion = bd.getConexion()) {
            if (conexion.isValid(2)) {
                System.out.println("Base de datos lista.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        bd.cerrarConexion();
    }
}
