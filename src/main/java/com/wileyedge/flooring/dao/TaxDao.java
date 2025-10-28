package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Tax;
import com.wileyedge.flooring.exceptions.PersistenceException;

import java.util.List;

public interface TaxDao {

    /**
     * Gets all available tax information
     * @return list of all tax records
     * @throws PersistenceException if unable to read from persistence
     */
    List<Tax> getAllTaxes() throws PersistenceException;

    /**
     * Gets tax information by state abbreviation
     * @param stateAbr the state abbreviation
     * @return the tax record if found, null otherwise
     * @throws PersistenceException if unable to read from persistence
     */
    Tax getTax(String stateAbr) throws PersistenceException;
}