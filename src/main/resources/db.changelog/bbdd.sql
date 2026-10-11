-- PostgreSQL: creación de una base de datos nueva; no ejecutar sobre tablas existentes.
-- Importes y saldos: 16 dígitos enteros y hasta 2 decimales.
BEGIN;

-- Tabla para Status
CREATE TABLE Status (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Tabla para Role
CREATE TABLE Role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    comment TEXT,
    status_id INTEGER REFERENCES Status(id)
);

-- Tabla para User
CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status_id INTEGER REFERENCES Status(id),
    comment TEXT,
    record_date TIMESTAMP,
    last_access TIMESTAMP,
    role_id INTEGER REFERENCES Role(id)
);

-- Tabla para Wallet
CREATE TABLE Wallet (
    id SERIAL PRIMARY KEY,
    budget NUMERIC(18,2) NOT NULL DEFAULT 0.00,
    comment TEXT,
    status_id INTEGER REFERENCES Status(id),
    name VARCHAR(255) NOT NULL,
    user_id INTEGER REFERENCES "user"(id),
    last_modification TIMESTAMP,
    creation_date TIMESTAMP
);

-- Tabla para Category
CREATE TABLE Category (
    id SERIAL PRIMARY KEY,
    comment TEXT,
    status_id INTEGER REFERENCES Status(id),
    name VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    last_modification TIMESTAMP
);

-- Tabla para Store
CREATE TABLE Store (
    id SERIAL PRIMARY KEY,
    comment TEXT,
    status_id INTEGER REFERENCES Status(id),
    name VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    last_modification TIMESTAMP
);

-- Tabla para Brand
CREATE TABLE Brand (
    id SERIAL PRIMARY KEY,
    comment TEXT,
    status_id INTEGER REFERENCES Status(id),
    name VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    last_modification TIMESTAMP
);



-- Tabla para operation_type
CREATE TABLE operation_type (
    id SERIAL PRIMARY KEY,
    comment TEXT,
    status_id INTEGER REFERENCES Status(id),
    name VARCHAR(255) NOT NULL,
    last_modification TIMESTAMP
);

-- Tabla para account_entry
CREATE TABLE account_entry (
    id SERIAL PRIMARY KEY,
    amount NUMERIC(18,2) NOT NULL,
    wallet_id INTEGER NOT NULL REFERENCES Wallet(id),
    comment TEXT,
    category_id INTEGER REFERENCES Category(id),
    status_id INTEGER REFERENCES Status(id),
    date DATE NOT NULL,
    brand_id INTEGER REFERENCES Brand(id),
    store_id INTEGER REFERENCES Store(id),
    type_operation_id INTEGER REFERENCES operation_type(id)
);

-- Identificadores reservados utilizados por StatusUtils y RoleUtils.
INSERT INTO Status (id, name) VALUES (1, 'Activo'), (2, 'Inactivo');
INSERT INTO Role (id, name, comment, status_id) VALUES
    (1, 'ADMIN', 'Role for admin user', 1),
    (2, 'USER', 'Role for common user', 1);
INSERT INTO operation_type (id, name, comment, status_id, last_modification) VALUES
    (1, 'Ingreso', 'Tipo para los ingresos', 1, CURRENT_TIMESTAMP),
    (2, 'Gasto', 'Tipo para los gastos', 1, CURRENT_TIMESTAMP),
    (3, 'Inversión', 'Tipo para las inversiones', 1, CURRENT_TIMESTAMP);

-- Las inserciones explícitas de IDs no avanzan las secuencias SERIAL.
SELECT setval(pg_get_serial_sequence('status', 'id'), (SELECT MAX(id) FROM Status));
SELECT setval(pg_get_serial_sequence('role', 'id'), (SELECT MAX(id) FROM Role));
SELECT setval(pg_get_serial_sequence('operation_type', 'id'), (SELECT MAX(id) FROM operation_type));

COMMIT;
