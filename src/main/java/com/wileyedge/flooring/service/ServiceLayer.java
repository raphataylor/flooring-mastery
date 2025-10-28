package com.wileyedge.flooring.service;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.dto.Tax;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;

import java.time.LocalDate;
import java.util.List;

public interface ServiceLayer {

    /**
     * Gets the next available order number
     * @return the next order number
     */
    int getNextOrderNumber();

    /**
     * Adds an order to the system with validation and calculations
     * @param order the order to add
     * @return the added order with calculations
     * @throws PersistenceException if unable to persist the order
     */
    Order addOrder(Order order) throws PersistenceException;

    /**
     * Gets an order by date and order number
     * @param date the order date
     * @param orderNumber the order number
     * @return the order if found
     * @throws NoSuchOrderException if the order doesn't exist
     * @throws PersistenceException if unable to read from persistence
     */
    Order getOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException;

    /**
     * Edits an existing order with recalculations
     * @param order the updated order
     * @return the edited order with recalculations
     * @throws NoSuchOrderException if the order doesn't exist
     * @throws PersistenceException if unable to persist changes
     */
    Order editOrder(Order order) throws NoSuchOrderException, PersistenceException;

    /**
     * Gets all orders for a specific date
     * @param date the date to retrieve orders for
     * @return list of orders for that date
     * @throws PersistenceException if unable to read from persistence
     */
    List<Order> getOrdersForDate(LocalDate date) throws PersistenceException;

    /**
     * Removes an order from the system
     * @param date the order date
     * @param orderNumber the order number
     * @return the removed order
     * @throws NoSuchOrderException if the order doesn't exist
     * @throws PersistenceException if unable to persist changes
     */
    Order removeOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException;

    /**
     * Exports all data to a backup file
     * @throws PersistenceException if unable to export data
     */
    void exportAllData() throws PersistenceException;

    /**
     * Gets all available tax information
     * @return list of all tax records
     * @throws PersistenceException if unable to read from persistence
     */
    List<Tax> getAllTaxes() throws PersistenceException;

    /**
     * Gets all available products
     * @return list of all products
     * @throws PersistenceException if unable to read from persistence
     */
    List<Product> getAllProducts() throws PersistenceException;

    /**
     * Writes an entry to the audit log
     * @param entry the audit entry
     * @throws PersistenceException if unable to write to audit log
     */
    void writeToAudit(String entry) throws PersistenceException;
}