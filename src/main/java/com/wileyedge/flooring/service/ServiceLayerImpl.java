package com.wileyedge.flooring.service;

import com.wileyedge.flooring.dao.*;
import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.dto.Tax;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class ServiceLayerImpl implements ServiceLayer {

    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final TaxDao taxDao;
    private final ExportDao exportDao;
    private final AuditDao auditDao;

    @Autowired
    public ServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao,
                            ExportDao exportDao, AuditDao auditDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
        this.exportDao = exportDao;
        this.auditDao = auditDao;
    }

    @Override
    public int getNextOrderNumber() {
        return orderDao.getNextOrderNumber();
    }

    @Override
    public Order addOrder(Order order) throws PersistenceException {
        // Calculate order totals
        calculateOrderTotals(order);

        // Add to persistence
        Order addedOrder = orderDao.addOrder(order);

        // Write audit entry
        auditDao.writeAuditEntry("Order " + addedOrder.getOrderNumber() + " ADDED.");

        return addedOrder;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException {
        return orderDao.getOrder(date, orderNumber);
    }

    @Override
    public Order editOrder(Order order) throws NoSuchOrderException, PersistenceException {
        // Recalculate order totals
        calculateOrderTotals(order);

        // Update in persistence
        Order editedOrder = orderDao.editOrder(order);

        // Write audit entry
        auditDao.writeAuditEntry("Order " + editedOrder.getOrderNumber() + " EDITED.");

        return editedOrder;
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws PersistenceException {
        return orderDao.getOrdersForDate(date);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException {
        Order removedOrder = orderDao.removeOrder(date, orderNumber);

        // Write audit entry
        auditDao.writeAuditEntry("Order " + orderNumber + " REMOVED.");

        return removedOrder;
    }

    @Override
    public void exportAllData() throws PersistenceException {
        Map<LocalDate, Map<Integer, Order>> allOrders = orderDao.getAllOrders();
        exportDao.exportAllData(allOrders);

        // Write audit entry
        auditDao.writeAuditEntry("All data EXPORTED.");
    }

    @Override
    public List<Tax> getAllTaxes() throws PersistenceException {
        return taxDao.getAllTaxes();
    }

    @Override
    public List<Product> getAllProducts() throws PersistenceException {
        return productDao.getAllProducts();
    }

    @Override
    public void writeToAudit(String entry) throws PersistenceException {
        auditDao.writeAuditEntry(entry);
    }

    /**
     * Calculates all derived fields for an order
     * MaterialCost = Area * CostPerSquareFoot
     * LaborCost = Area * LaborCostPerSquareFoot
     * Tax = (MaterialCost + LaborCost) * (TaxRate/100)
     * Total = MaterialCost + LaborCost + Tax
     */
    private void calculateOrderTotals(Order order) {
        BigDecimal area = order.getArea();
        BigDecimal costPerSqFt = order.getCostPerSquareFoot();
        BigDecimal laborCostPerSqFt = order.getLaborCostPerSquareFoot();
        BigDecimal taxRate = order.getTaxRate();

        // Calculate Material Cost
        BigDecimal materialCost = area.multiply(costPerSqFt)
                .setScale(2, RoundingMode.HALF_UP);
        order.setMaterialCost(materialCost);

        // Calculate Labor Cost
        BigDecimal laborCost = area.multiply(laborCostPerSqFt)
                .setScale(2, RoundingMode.HALF_UP);
        order.setLaborCost(laborCost);

        // Calculate Tax
        BigDecimal tax = materialCost.add(laborCost)
                .multiply(taxRate.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
        order.setTax(tax);

        // Calculate Total
        BigDecimal total = materialCost.add(laborCost).add(tax)
                .setScale(2, RoundingMode.HALF_UP);
        order.setTotal(total);
    }
}