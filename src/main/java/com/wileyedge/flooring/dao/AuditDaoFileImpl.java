package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.exceptions.PersistenceException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class AuditDaoFileImpl implements AuditDao {

    private final String AUDIT_FILE;

    public AuditDaoFileImpl() {
        this.AUDIT_FILE = "audit.txt";
    }

    public AuditDaoFileImpl(String auditFile) {
        this.AUDIT_FILE = auditFile;
    }

    @Override
    public void writeAuditEntry(String entry) throws PersistenceException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(AUDIT_FILE, true));
        } catch (IOException e) {
            throw new PersistenceException("Could not write to audit file.", e);
        }

        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        out.println(timestamp.format(formatter) + " : " + entry);
        out.flush();
        out.close();
    }
}