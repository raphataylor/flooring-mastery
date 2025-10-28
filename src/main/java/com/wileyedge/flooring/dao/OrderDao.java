package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    /**
     * Gets the next available order number
     * @return the next order number
     */
    int getNextOrderNumber();

    /**
     * Adds an order to the system
     * @param order the order to add
     * @return the added order
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
     * Edits an existing order
     * @param order the updated order
     * @return the edited order
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
     * Gets all orders in the system
     * @return map of dates to orders
     * @throws PersistenceException if unable to read from persistence
     */
    Map<LocalDate, Map<Integer, Order>> getAllOrders() throws PersistenceException;

    /**
     * Removes an order from the system
     * @param date the order date
     * @param orderNumber the order number
     * @return the removed order
     * @throws NoSuchOrderException if the order doesn't exist
     * @throws PersistenceException if unable to persist changes
     */
    Order removeOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException;
}