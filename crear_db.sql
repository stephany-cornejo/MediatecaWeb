-- Crea la tabla de usuarios y un usuario admin por defecto
CREATE TABLE Usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    rol TEXT NOT NULL
);

-- Usuario admin: usuario = admin, contraseña = admin, rol = ADMIN
INSERT INTO Usuarios (nombre, password, rol) VALUES ('admin', 'admin', 'ADMIN');
