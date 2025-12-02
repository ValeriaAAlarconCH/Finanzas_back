-- =======================================================================
-- DATOS DE INSERCIÓN INICIAL PARA EL SISTEMA DE CRÉDITOS HIPOTECARIOS
-- SCRIPT CORREGIDO PARA POSTGRESQL: Incluye TRUNCATE con RESTART IDENTITY
-- y desactivación temporal de FKs para asegurar la inserción de datos.
-- =======================================================================

-- 1. DESACTIVAR CLAVES FORÁNEAS TEMPORALMENTE (Para evitar errores de dependencia)
SET session_replication_role = 'replica';

-- 2. LIMPIEZA Y REINICIO DE SECUENCIAS (CRÍTICO PARA POSTGRESQL)
TRUNCATE TABLE user_roles RESTART IDENTITY CASCADE;
TRUNCATE TABLE pago_cuota RESTART IDENTITY CASCADE;
TRUNCATE TABLE indicador_financiero RESTART IDENTITY CASCADE;
TRUNCATE TABLE cuota RESTART IDENTITY CASCADE;
TRUNCATE TABLE pago RESTART IDENTITY CASCADE;
TRUNCATE TABLE credito RESTART IDENTITY CASCADE;
TRUNCATE TABLE configuracion RESTART IDENTITY CASCADE;
TRUNCATE TABLE cliente RESTART IDENTITY CASCADE;
TRUNCATE TABLE unidad_inmobiliaria RESTART IDENTITY CASCADE;
TRUNCATE TABLE proyecto_inmobiliario RESTART IDENTITY CASCADE;
TRUNCATE TABLE entidad_financiera RESTART IDENTITY CASCADE;
TRUNCATE TABLE periodo_gracia RESTART IDENTITY CASCADE;
TRUNCATE TABLE capitalizacion RESTART IDENTITY CASCADE;
TRUNCATE TABLE tipo_tasa_interes RESTART IDENTITY CASCADE;
TRUNCATE TABLE moneda RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;
TRUNCATE TABLE roles RESTART IDENTITY CASCADE;


-- -----------------------------------------------------------------------
-- 3. INSERCIÓN DE DATOS (Las claves primarias se generarán automáticamente)
-- -----------------------------------------------------------------------

-- ROLES (IDs generados: 1, 2, 3)
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_MICROEMPRESARIO');

-- USERS (IDs generados: 1, 2, 3, 4)
INSERT INTO users(username, password) VALUES ('user1','$2a$12$tlMaQAFeJ82Bxx3dIsNZfuSoTy/86E3KDuv8osmM7Wmd/skEQRw0.');
INSERT INTO users(username, password) VALUES ('admin','$2a$12$cmgc7jkDr1PntzM2xNiXA.0GQNCHIgxgWo8vBWr5FIJJtwZUES/Ly');
INSERT INTO users(username, password) VALUES ('microempresario','$2a$12$.qcDOn80r9rG9.hI17WO7.S1KHpXwzuo6lMM5zckopfoh0LWaYoM2');
INSERT INTO users(username, password) VALUES ('cliente2','$2a$12$tlMaQAFeJ82Bxx3dIsNZfuSoTy/86E3KDuv8osmM7Wmd/skEQRw0.');

-- USER_ROLES (Usando los IDs generados: user_id | role_id)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 1);

-- MONEDA (IDs: 1, 2)
INSERT INTO moneda (codigo, nombre, simbolo) VALUES
                                                 ('PEN', 'Sol Peruano', 'S/'),
                                                 ('USD', 'Dólar Americano', '$');

-- TIPO DE TASA (IDs: 1, 2)
INSERT INTO tipo_tasa_interes (id_tasa, tipo) VALUES (1, 'TEA');
INSERT INTO tipo_tasa_interes (id_tasa, tipo) VALUES (2, 'TNA');

-- CAPITALIZACIÓN (IDs: 1, 2, 3)
INSERT INTO capitalizacion (nombre, periodos_por_ano) VALUES
                                                          ('Mensual', 12.0),
                                                          ('Trimestral', 4.0),
                                                          ('Anual', 1.0);

-- PERIODO DE GRACIA (IDs: 1, 2, 3)
INSERT INTO periodo_gracia (tipo) VALUES
                                      ('total'),
                                      ('parcial'),
                                      ('Ninguno');

-- ENTIDADES FINANCIERAS (IDs: 1 a 5)
INSERT INTO entidad_financiera (nombre, codigo_autorizacion, ruc, direccion, telefono, email)
VALUES
    ('Banco de Crédito del Perú', 'BCP-001', 20100047218, 'Av. La Marina 1234', 987654321, 'contacto@bcp.com'),
    ('Interbank', 'IBK-002', 20100053478, 'Av. Javier Prado 5678', 987111222, 'contacto@interbank.pe'),
    ('BBVA', 'BBVA-003', 20100099887, 'Av. Arequipa 4301', 987222333, 'contacto@bbva.pe'),
    ('Banco Hipotecario del Pacífico', 'BHIP-5678', 20541234567, 'Av. Paseo de la República 100', 901234567, 'contacto@bhip.com'),
    ('Financiera Futuro Seguro', 'FFUT-1011', 20119876543, 'Jr. Miraflores 202', 923456789, 'info@finfuturo.pe');

INSERT INTO proyecto_inmobiliario (
    id_proyecto,
    nombre_proyecto,
    direccion,
    descripcion,
    desarrollador,
    fecha_inicio,
    fecha_entrega_estimada
) VALUES (
             1,
             'Residencial Altos de Surco',
             'Av. El Sol 300, Surco, Lima',
             'Moderno complejo de departamentos con áreas comunes y seguridad 24/7.',
             'Inmobiliaria El Cóndor',
             '2023-01-10',
             '2024-12-30'
         );

INSERT INTO proyecto_inmobiliario (
    id_proyecto,
    nombre_proyecto,
    direccion,
    descripcion,
    desarrollador,
    fecha_inicio,
    fecha_entrega_estimada
) VALUES (
             2,
             'Condominio Las Brisas',
             'Carretera Central Km 5, Chosica',
             'Proyecto de casas de campo, ideal para fines de semana.',
             'Constructora Verde',
             '2022-06-01',
             '2023-11-15'
         );
-- UNIDAD INMOBILIARIA (FK: id_proyecto=1, id_moneda=2 (USD))
INSERT INTO unidad_inmobiliaria (codigo_unidad, tipo, area_m2, num_dormitorios, num_banos, piso, precio_lista, precio_venta, estado, descripcion, id_proyecto, id_moneda)
VALUES
    ('RAS-D502', 'Departamento', 85.50, 3, 2, '5to Piso', 150000.00, 145000.00, 'Vendido', 'Departamento con vista a la piscina, 3 dormitorios.', 1, 2), -- ID 1
    ('RAS-D101', 'Departamento', 60.00, 2, 1, '1er Piso', 100000.00, 98000.00, 'Reservado', 'Departamento de 2 dormitorios con patio.', 1, 2), -- ID 2
    ('CLB-C05', 'Casa', 180.00, 4, 3, '1er y 2do', 220000.00, 220000.00, 'Disponible', 'Casa modelo Ébano de dos plantas.', 2, 2); -- ID 3

-- CLIENTE (FK a users: 1 y 4)
INSERT INTO cliente (dni, nombres, apellidos, fecha_nacimiento, sexo, direccion, telefono, email, ocupacion, empleador, ingreso_mensual, estado_civil, num_dependientes, observaciones, id_user)
VALUES
    (74581234, 'Juan Carlos', 'Pérez López', '1985-05-15', 'Masculino', 'Av. La Molina 123, Lima', 987654321, 'juan.perez@example.com', 'Ingeniero de Software', 'TechSolutions S.A.C.', 8500.00, 'Casado', 2, 'Primer cliente con crédito hipotecario aprobado.', 1), -- ID 1
    (40129876, 'María Elena', 'Gómez Ruiz', '1992-11-20', 'Femenino', 'Calle Los Rosales 456, Surco', 998877665, 'maria.gomez@example.com', 'Analista Financiera', 'Banco de Créditos', 5200.00, 'Soltero', 0, 'Cliente pre-aprobada.', 4); -- ID 2

-- CONFIGURACION (FKs: id_cliente=1, id_moneda=2, id_tasa=2, id_capitalizacion=1)
INSERT INTO configuracion (convencion_dias, periodicidad, id_cliente, id_moneda, id_tasa, id_capitalizacion)
VALUES (360, 'Mensual', 1, 2, 2, 1); -- ID 1

-- CRÉDITO (FKs: id_cliente=1, id_unidad=1, id_entidad=1, id_moneda=2, id_tasa=2, id_capitalizacion=1, id_gracia=3)
INSERT INTO credito (meses_gracia, monto_principal, tasa_anual, plazo_meses, fecha_desembolso, numero_contrato, estado, id_cliente, id_unidad, id_entidad, id_moneda, id_tasa, id_capitalizacion, id_gracia)
VALUES (0, 100000.00, 0.08, 120, '2025-01-01', 'CH-2025-0001', 'Activo', 1, 1, 1, 2, 2, 1, 3); -- ID 1

-- CUOTA (FK a id_credito=1)
INSERT INTO cuota (numero_cuota, fecha_vencimiento, dias_periodo, saldo_inicial, capital_programado, interes_programado, otros_cargos, total_cuota, saldo_final, estado, id_credito)
VALUES
    (1, '2025-02-01', 31, 100000.00, 546.61, 666.67, 20.00, 1233.28, 99453.39, 'Pagada', 1), -- ID 1
    (2, '2025-03-01', 28, 99453.39, 550.25, 663.03, 20.00, 1233.28, 98903.14, 'Pagada', 1), -- ID 2
    (3, '2025-04-01', 31, 98903.14, 553.92, 659.36, 20.00, 1233.28, 98349.22, 'Pendiente', 1), -- ID 3
    (4, '2025-05-01', 30, 98349.22, 557.62, 655.66, 20.00, 1233.28, 97791.60, 'Pendiente', 1); -- ID 4


-- PAGO (FKs: id_credito=1, id_moneda=2, id_cliente=1)
INSERT INTO pago (fecha_pago, monto, metodo_pago, referencia, id_credito, id_moneda, id_cliente)
VALUES
    ('2025-01-28', 1233.28, 'Transferencia', 'TRF-12345', 1, 2, 1), -- ID 1
    ('2025-02-25', 1233.28, 'Transferencia', 'TRF-12346', 1, 2, 1); -- ID 2

-- PAGO CUOTA (Aplicación de pagos a cuotas: Pago 1 -> Cuota 1, Pago 2 -> Cuota 2)
INSERT INTO pago_cuota (id_pago, id_cuota, monto_aplicado, fecha_pago)
VALUES
    (1, 1, 1233.28, '2025-01-28'),
    (2, 2, 1233.28, '2025-02-25');

-- INDICADOR FINANCIERO (FK a id_credito=1)
INSERT INTO indicador_financiero (fecha_calculo, VAN, TIR, TCEA, TREA, duracion, duracion_modificada, convexidad, id_credito)
VALUES ('2025-01-05', 1000.00, 0.0803, 0.0855, 0.00, 7.5, 7.0, 105.0, 1);

-- 4. REACTIVAR CLAVES FORÁNEAS
SET session_replication_role = 'origin';