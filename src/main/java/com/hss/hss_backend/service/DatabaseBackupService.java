package com.hss.hss_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseBackupService {

    @Value("${backup.database.path:/tmp/backups}")
    private String backupPath;

    @Value("${spring.datasource.url:}")
    private String databaseUrl;

    @Value("${spring.datasource.username:}")
    private String databaseUsername;

    @Value("${spring.datasource.password:}")
    private String databasePassword;

    public String createDatabaseBackup() throws IOException {
        log.info("Creating database backup");
        
        // Backup dizinini oluştur
        Path backupDir = Paths.get(backupPath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        // Backup dosya adı
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = String.format("hss_backup_%s.sql", timestamp);
        String backupFilePath = backupPath + "/" + backupFileName;

        // PostgreSQL dump komutu
        String dbName = extractDatabaseName(databaseUrl);
        String pgDumpCommand = String.format(
            "PGPASSWORD=%s pg_dump -h %s -U %s -d %s -F c -f %s",
            databasePassword,
            extractHost(databaseUrl),
            databaseUsername,
            dbName,
            backupFilePath
        );

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", pgDumpCommand});
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                File backupFile = new File(backupFilePath);
                long fileSize = backupFile.length();
                log.info("Database backup created successfully: {} ({} bytes)", backupFilePath, fileSize);
                return backupFilePath;
            } else {
                throw new IOException("Database backup failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Database backup interrupted", e);
        }
    }

    public void restoreDatabaseBackup(String backupFilePath) throws IOException {
        log.info("Restoring database from backup: {}", backupFilePath);
        
        File backupFile = new File(backupFilePath);
        if (!backupFile.exists()) {
            throw new IOException("Backup file not found: " + backupFilePath);
        }

        String dbName = extractDatabaseName(databaseUrl);
        String pgRestoreCommand = String.format(
            "PGPASSWORD=%s pg_restore -h %s -U %s -d %s -c %s",
            databasePassword,
            extractHost(databaseUrl),
            databaseUsername,
            dbName,
            backupFilePath
        );

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", pgRestoreCommand});
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                log.info("Database restored successfully from: {}", backupFilePath);
            } else {
                throw new IOException("Database restore failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Database restore interrupted", e);
        }
    }

    private String extractDatabaseName(String url) {
        // jdbc:postgresql://host:port/dbname formatından dbname'i çıkar
        if (url.contains("/")) {
            String[] parts = url.split("/");
            if (parts.length > 0) {
                String dbPart = parts[parts.length - 1];
                if (dbPart.contains("?")) {
                    return dbPart.substring(0, dbPart.indexOf("?"));
                }
                return dbPart;
            }
        }
        return "hss_dev";
    }

    private String extractHost(String url) {
        // jdbc:postgresql://host:port/dbname formatından host'u çıkar
        if (url.contains("//")) {
            String hostPart = url.substring(url.indexOf("//") + 2);
            if (hostPart.contains("/")) {
                hostPart = hostPart.substring(0, hostPart.indexOf("/"));
            }
            if (hostPart.contains(":")) {
                return hostPart.substring(0, hostPart.indexOf(":"));
            }
            return hostPart;
        }
        return "localhost";
    }
}

