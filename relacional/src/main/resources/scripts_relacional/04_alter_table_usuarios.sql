-- ==================================================
-- Script: Agregar columnas adicionales a USUARIOS
-- Descripción: Añade campos de apellido, teléfono único, 
--              dirección, ciudad, estado y código postal
-- ==================================================

-- Agregar columna apellido
ALTER TABLE usuarios ADD (
    apellido VARCHAR2(100)
);

-- Agregar columna telefono (teléfono individual)
ALTER TABLE usuarios ADD (
    telefono VARCHAR2(15)
);

-- Agregar columnas de dirección
ALTER TABLE usuarios ADD (
    direccion VARCHAR2(255),
    ciudad VARCHAR2(100),
    estado VARCHAR2(100),
    codigo_postal VARCHAR2(10)
);

-- Comentarios para documentar las columnas
COMMENT ON COLUMN usuarios.apellido IS 'Apellido(s) del usuario';
COMMENT ON COLUMN usuarios.telefono IS 'Número de teléfono principal del usuario';
COMMENT ON COLUMN usuarios.direccion IS 'Dirección física del usuario (calle y número)';
COMMENT ON COLUMN usuarios.ciudad IS 'Ciudad de residencia del usuario';
COMMENT ON COLUMN usuarios.estado IS 'Estado/Provincia del usuario';
COMMENT ON COLUMN usuarios.codigo_postal IS 'Código postal de la dirección';

-- Verificar cambios
SELECT column_name, data_type, data_length, nullable
FROM user_tab_columns
WHERE table_name = 'USUARIOS'
ORDER BY column_id;

COMMIT;
