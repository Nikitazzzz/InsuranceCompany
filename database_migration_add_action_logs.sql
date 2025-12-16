-- Миграция: Добавление таблицы для логирования действий
USE InsuranceCompany;

-- Создание таблицы для логирования действий
CREATE TABLE IF NOT EXISTS ActionLogs (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NULL,
    UserRole VARCHAR(20) NOT NULL,
    ActionType VARCHAR(50) NOT NULL,
    EntityType VARCHAR(50) NOT NULL,
    EntityID INT NULL,
    Description TEXT NOT NULL,
    IPAddress VARCHAR(45),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX IX_ActionLogs_UserID (UserID),
    INDEX IX_ActionLogs_CreatedAt (CreatedAt),
    INDEX IX_ActionLogs_ActionType (ActionType),
    INDEX IX_ActionLogs_EntityType (EntityType)
);

