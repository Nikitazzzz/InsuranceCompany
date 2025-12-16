-- Миграция для добавления новых типов тарифов и поля Description
-- Выполните этот скрипт для обновления существующей базы данных

USE InsuranceCompany;

-- 1. Обновление CHECK ограничения для PolicyType (добавление новых типов)
ALTER TABLE Tariffs 
DROP CHECK IF EXISTS Tariffs_chk_1;

ALTER TABLE Tariffs 
ADD CONSTRAINT Tariffs_chk_1 
CHECK (PolicyType IN ('ОСАГО', 'КАСКО', 'Зелёная карта', 'Страховка водителя и пассажиров'));

-- 2. Добавление поля Description, если его еще нет
-- Проверяем, существует ли колонка Description
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'InsuranceCompany' 
    AND TABLE_NAME = 'Tariffs' 
    AND COLUMN_NAME = 'Description'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE Tariffs ADD COLUMN Description TEXT AFTER PowerCoefficient',
    'SELECT "Column Description already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

