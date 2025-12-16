-- Миграция: Добавление таблицы для фотографий страховых случаев
USE InsuranceCompany;

-- Создание таблицы для фотографий страховых случаев
CREATE TABLE IF NOT EXISTS InsurancePhotos (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    InsuranceID INT NOT NULL,
    FileName VARCHAR(255) NOT NULL,
    FilePath VARCHAR(500) NOT NULL,
    FileSize BIGINT,
    MimeType VARCHAR(100),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (InsuranceID) REFERENCES Insurances(ID) ON DELETE CASCADE,
    INDEX IX_InsurancePhotos_InsuranceID (InsuranceID)
);

