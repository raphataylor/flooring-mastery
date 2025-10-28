package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.exceptions.PersistenceException;

public interface AuditDao {

    /**
     * Writes an audit entry to the audit log
     * @param entry the audit entry to write
     * @throws PersistenceException if unable to write to audit log
     */
    void writeAuditEntry(String entry) throws PersistenceException;
}