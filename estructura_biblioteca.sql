-- Estructura base de datos para Biblioteca

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    rol TEXT NOT NULL CHECK(rol IN ('ADMIN','PROFESOR','ALUMNO')),
    mora_acumulada REAL DEFAULT 0
);

-- Tabla de configuraciones generales
CREATE TABLE IF NOT EXISTS Configuracion (
    clave TEXT PRIMARY KEY,
    valor TEXT NOT NULL
);

-- Tabla de documentos (libros, revistas, CDs, tesis, etc.)
CREATE TABLE IF NOT EXISTS Documentos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo TEXT NOT NULL,
    titulo TEXT NOT NULL,
    autor TEXT,
    ubicacion TEXT NOT NULL,
    cantidad_total INTEGER NOT NULL,
    cantidad_disponible INTEGER NOT NULL,
    anio INTEGER,
    editorial TEXT,
    descripcion TEXT
);

-- Tabla de préstamos
CREATE TABLE IF NOT EXISTS Prestamos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    id_documento INTEGER NOT NULL,
    fecha_salida TEXT NOT NULL,
    fecha_devolucion TEXT,
    mora_acumulada REAL DEFAULT 0,
    FOREIGN KEY(id_usuario) REFERENCES Usuarios(id),
    FOREIGN KEY(id_documento) REFERENCES Documentos(id)
);

-- Configuración por defecto
INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('max_prestamos', '3');
INSERT OR IGNORE INTO Configuracion (clave, valor) VALUES ('mora_diaria', '5');
