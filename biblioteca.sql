-- ============================================================
--  biblioteca.sql  |  SQLite
--  Esquema completo + datos de prueba
-- ============================================================

PRAGMA foreign_keys = ON;

-- ------------------------------------------------------------
-- Tabla: Usuarios
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Usuarios (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre   TEXT    NOT NULL,
    password TEXT    NOT NULL,          -- almacenar siempre hash (bcrypt / SHA-256)
    rol      TEXT    NOT NULL
                     CHECK (rol IN ('ADMIN', 'PROFESOR', 'ALUMNO'))
);

-- ------------------------------------------------------------
-- Tabla: Documentos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Documentos (
    id                     INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo                 TEXT    NOT NULL,
    ubicacion              TEXT    NOT NULL,
    tipo                   TEXT    NOT NULL
                                   CHECK (tipo IN ('LIBRO', 'REVISTA', 'CD', 'TESIS')),
    stock_total            INTEGER NOT NULL CHECK (stock_total >= 0),
    stock_disponible       INTEGER NOT NULL CHECK (stock_disponible >= 0),
    campos_especificos_json TEXT   NOT NULL DEFAULT '{}'   -- JSON con atributos propios del tipo
);

-- ------------------------------------------------------------
-- Tabla: Prestamos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Prestamos (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario        INTEGER NOT NULL REFERENCES Usuarios(id)   ON DELETE RESTRICT,
    id_documento      INTEGER NOT NULL REFERENCES Documentos(id) ON DELETE RESTRICT,
    fecha_salida      TEXT    NOT NULL,   -- ISO-8601: YYYY-MM-DD
    fecha_devolucion  TEXT,               -- NULL mientras el préstamo esté activo
    mora_acumulada    REAL    NOT NULL DEFAULT 0.0 CHECK (mora_acumulada >= 0)
);

-- ============================================================
--  Datos de prueba
-- ============================================================

-- Usuarios (1 por cada rol)
-- NOTA: las contraseñas aquí son texto plano sólo para desarrollo.
--       En producción almacena únicamente el hash.
INSERT INTO Usuarios (nombre, password, rol) VALUES
    ('Ana García',     'hash_admin_001',   'ADMIN'),
    ('Luis Herrera',   'hash_prof_001',    'PROFESOR'),
    ('María López',    'hash_prof_002',    'PROFESOR'),
    ('Carlos Ruiz',    'hash_alumno_001',  'ALUMNO'),
    ('Sofia Mendoza',  'hash_alumno_002',  'ALUMNO');

-- Documentos (un ejemplo de cada tipo)
INSERT INTO Documentos (titulo, ubicacion, tipo, stock_total, stock_disponible, campos_especificos_json) VALUES
    (
        'Clean Code',
        'Estante A-12',
        'LIBRO',
        5, 3,
        '{"autor":"Robert C. Martin","isbn":"978-0132350884","editorial":"Prentice Hall"}'
    ),
    (
        'National Geographic - Abril 2026',
        'Estante R-03',
        'REVISTA',
        2, 2,
        '{"periodicidad":"Mensual","fechaPublicacion":"2026-04-01"}'
    ),
    (
        'The Dark Side of the Moon',
        'Estante CD-07',
        'CD',
        4, 4,
        '{"genero":"Rock progresivo","duracion":2532}'
    ),
    (
        'Impacto de la IA en la Educación Superior',
        'Estante T-15',
        'TESIS',
        1, 1,
        '{"autor":"Pedro Núñez","carrera":"Ingeniería en Sistemas"}'
    );

-- Préstamos de prueba
INSERT INTO Prestamos (id_usuario, id_documento, fecha_salida, fecha_devolucion, mora_acumulada) VALUES
    (4, 1, '2026-04-20', '2026-04-27', 0.0),   -- Carlos devolvió "Clean Code" a tiempo
    (5, 2, '2026-04-25', NULL,          0.0),   -- Sofia tiene "National Geographic" en curso
    (4, 3, '2026-03-10', '2026-03-20', 15.50),  -- Carlos devolvió el CD con mora
    (3, 4, '2026-05-01', NULL,          0.0);   -- María (profesora) tiene la Tesis en curso
