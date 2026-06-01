package com.fooddelivery.audit;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static final String FILE_PATH = "audit.csv";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static AuditService instance;

    private AuditService() {}

    public static AuditService getInstance() {
        if (instance == null) instance = new AuditService();
        return instance;
    }

    public void log(String actionName) {
        String line = actionName + "," + LocalDateTime.now().format(FMT);
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("[AuditService] Failed to write audit log: " + e.getMessage());
        }
    }
}
