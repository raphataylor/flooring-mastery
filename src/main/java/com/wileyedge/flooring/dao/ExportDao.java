package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.exceptions.PersistenceException;

import java.time.LocalDate;
import java.util.Map;

public interface ExportDao {

    /**
     * Exports all order data to a backup file
     * @param allOrders map of all orders by date
     * @throws PersistenceException if unable to export data
     */
    void exportAllData(Map<LocalDate, Map<Integer, Order>> allOrders) throws PersistenceException;
}