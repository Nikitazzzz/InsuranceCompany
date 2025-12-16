-- Создание базы данных
CREATE DATABASE IF NOT EXISTS InsuranceCompany;
USE InsuranceCompany;

-- =============================================
-- 1. Очистка старых данных (если нужно пересоздать)
-- Удаляем сначала представления, затем таблицы (от дочерних к родительским)
-- =============================================
DROP VIEW IF EXISTS ActivePolicies;
DROP VIEW IF EXISTS ActiveInsurances;
DROP TABLE IF EXISTS Payouts;
DROP TABLE IF EXISTS Insurances;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Policies;
DROP TABLE IF EXISTS Vehicle;
DROP TABLE IF EXISTS Tariffs;
DROP TABLE IF EXISTS Administrator;
DROP TABLE IF EXISTS Owners;
DROP TABLE IF EXISTS Statuses;

-- =============================================
-- 2. Создание таблиц
-- =============================================
-- 1. Таблица статусов
CREATE TABLE Statuses (
    ID INT PRIMARY KEY,
    StName VARCHAR(20) NOT NULL UNIQUE
);

-- 2. Таблица владельцев
CREATE TABLE Owners (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Email VARCHAR(255) NOT NULL,
    OName VARCHAR(255) NOT NULL,
    Surname VARCHAR(255) NOT NULL,
    MiddleName VARCHAR(255),
    Phone VARCHAR(50) NOT NULL,
    Birthday DATE NOT NULL,
    DriverEXP INT NOT NULL CHECK (DriverEXP >= 0),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsActive TINYINT(1) DEFAULT 1
);

-- 3. Таблица администраторов
CREATE TABLE Administrator (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    AName VARCHAR(255) NOT NULL,
    Surname VARCHAR(255) NOT NULL,
    Position VARCHAR(255) NOT NULL,
    WorkEXP INT NOT NULL DEFAULT 0 CHECK (WorkEXP >= 0),
    Email VARCHAR(255) UNIQUE,
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. Таблица транспортных средств
CREATE TABLE Vehicle (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    OwnerID INT NOT NULL, 
    VIN VARCHAR(17) NOT NULL UNIQUE, 
    Reg VARCHAR(15) NOT NULL UNIQUE,
    Brand VARCHAR(50) NOT NULL,
    Model VARCHAR(50) NOT NULL,
    YearManufact INT NOT NULL, 
    HorsePower INT NOT NULL CHECK (HorsePower > 0),
    CategoryLic VARCHAR(50) NOT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (OwnerID) REFERENCES Owners(ID) ON DELETE NO ACTION 
);

-- 5. Таблица тарифов
CREATE TABLE Tariffs (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    StatID INT NOT NULL,
    TariffName VARCHAR(255) NOT NULL,
    PolicyType VARCHAR(50) NOT NULL CHECK (PolicyType IN ('ОСАГО', 'КАСКО', 'Зелёная карта', 'Страховка водителя и пассажиров')), 
    BasePrice DECIMAL(10,2) NOT NULL CHECK (BasePrice > 0), 
    RegionCoefficient DECIMAL(3,2) NOT NULL CHECK (RegionCoefficient > 0),
    DriverEXPCoefficient DECIMAL(3,2) NOT NULL CHECK (DriverEXPCoefficient > 0),
    PowerCoefficient DECIMAL(3,2) NOT NULL CHECK (PowerCoefficient > 0),
    Description TEXT,
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (StatID) REFERENCES Statuses(ID) ON DELETE NO ACTION
);

-- 6. Таблица полисов
CREATE TABLE Policies (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    OwnerID INT NOT NULL,
    VehicleID INT NOT NULL,
    TariffID INT NOT NULL,
    StatID INT NOT NULL,
    PolicyNumber VARCHAR(50) NOT NULL UNIQUE,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Price DECIMAL(10,2) NOT NULL CHECK (Price > 0),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (OwnerID) REFERENCES Owners(ID) ON DELETE NO ACTION, 
    FOREIGN KEY (VehicleID) REFERENCES Vehicle(ID) ON DELETE NO ACTION,
    FOREIGN KEY (TariffID) REFERENCES Tariffs(ID) ON DELETE NO ACTION,
    FOREIGN KEY (StatID) REFERENCES Statuses(ID) ON DELETE NO ACTION,
    CHECK (EndDate >= StartDate)
);

-- 7. Таблица страховых случаев
CREATE TABLE Insurances (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    PolicyID INT NOT NULL, 
    OwnerID INT NOT NULL, 
    StatID INT NOT NULL,
    IncidentDate DATETIME NOT NULL,
    IncidentDescription TEXT NOT NULL,
    DescriptionDamage TEXT NOT NULL,
    GradeDamage DECIMAL(10,2) NOT NULL CHECK (GradeDamage >= 0),
    AdminComment TEXT,
    CreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    DecisionDate DATETIME NULL,
    IsActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (StatID) REFERENCES Statuses(ID) ON DELETE NO ACTION,
    FOREIGN KEY (PolicyID) REFERENCES Policies(ID) ON DELETE NO ACTION,
    FOREIGN KEY (OwnerID) REFERENCES Owners(ID) ON DELETE NO ACTION,
    CHECK (DecisionDate IS NULL OR DecisionDate >= CreateDate)
);

-- 8. Таблица выплат
CREATE TABLE Payouts (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    InsuranceID INT NOT NULL UNIQUE,
    AdminID INT NOT NULL,
    SumPayout DECIMAL(10,2) NOT NULL CHECK (SumPayout > 0),
    PayoutDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    PaymentMethod VARCHAR(20) NOT NULL CHECK (PaymentMethod IN ('Наличные', 'На карту')),
    IsActive TINYINT(1) DEFAULT 1,
    FOREIGN KEY (InsuranceID) REFERENCES Insurances(ID) ON DELETE NO ACTION,
    FOREIGN KEY (AdminID) REFERENCES Administrator(ID) ON DELETE NO ACTION
);

-- 9. Таблица пользователей системы (для логина)
CREATE TABLE Users (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Login VARCHAR(100) NOT NULL UNIQUE,
    PasswordHash VARCHAR(255) NOT NULL,
    Role VARCHAR(20) NOT NULL CHECK (Role IN ('OWNER', 'ADMIN')),
    OwnerID INT NULL,
    AdminID INT NULL,
    IsActive TINYINT(1) DEFAULT 1,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_Users_Owner
        FOREIGN KEY (OwnerID) REFERENCES Owners(ID) ON DELETE NO ACTION,
    CONSTRAINT FK_Users_Admin
        FOREIGN KEY (AdminID) REFERENCES Administrator(ID) ON DELETE NO ACTION
);

-- =============================================
-- 3. Создание индексов
-- =============================================
CREATE INDEX IX_Users_Login ON Users(Login);
CREATE INDEX IX_Owners_Email ON Owners(Email);
CREATE INDEX IX_Owners_Phone ON Owners(Phone);
CREATE INDEX IX_Vehicle_OwnerID ON Vehicle(OwnerID);
CREATE INDEX IX_Vehicle_VIN ON Vehicle(VIN);
CREATE INDEX IX_Policies_OwnerID ON Policies(OwnerID);
CREATE INDEX IX_Policies_VehicleID ON Policies(VehicleID);
CREATE INDEX IX_Policies_PolicyNumber ON Policies(PolicyNumber);
CREATE INDEX IX_Insurances_PolicyID ON Insurances(PolicyID);
CREATE INDEX IX_Insurances_OwnerID ON Insurances(OwnerID);
CREATE INDEX IX_Insurances_StatID ON Insurances(StatID);
CREATE INDEX IX_Tariffs_StatID ON Tariffs(StatID);

-- =============================================
-- 4. Заполнение начальными данными
-- =============================================
INSERT INTO Statuses (ID, StName) VALUES
(1, 'Активный'),
(2, 'Неактивный'),
(3, 'В обработке'),
(4, 'Одобрен'),
(5, 'Отклонен'),
(6, 'Завершен'),
(7, 'Ожидает оплаты'),
(8, 'Оплачен');

-- =============================================
-- 5. Создание представлений (Views)
-- =============================================
-- Представление для активных полисов
CREATE VIEW ActivePolicies AS
SELECT p.*, o.OName, o.Surname, v.Brand, v.Model, v.Reg, s.StName as StatusName
FROM Policies p
    INNER JOIN Owners o ON p.OwnerID = o.ID
    INNER JOIN Vehicle v ON p.VehicleID = v.ID
    INNER JOIN Statuses s ON p.StatID = s.ID
WHERE p.IsActive = 1 AND o.IsActive = 1 AND v.IsActive = 1;

-- Представление для активных страховых случаев
CREATE VIEW ActiveInsurances AS
SELECT i.*, p.PolicyNumber, o.OName, o.Surname, s.StName as StatusName
FROM Insurances i
    INNER JOIN Policies p ON i.PolicyID = p.ID
    INNER JOIN Owners o ON i.OwnerID = o.ID
    INNER JOIN Statuses s ON i.StatID = s.ID
WHERE i.IsActive = 1 AND p.IsActive = 1 AND o.IsActive = 1;
