package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class OrderDaoFileImplTest {

    private OrderDao dao;
    private final String TEST_ORDER_FOLDER = "TestOrders";

    @Before
    public void setUp() throws Exception {
        // Create test directory
        new File(TEST_ORDER_FOLDER).mkdirs();

        // Create a custom DAO for testing
        dao = new OrderDaoFileImpl() {
            @Override
            public Order addOrder(Order order) throws PersistenceException {
                // Override to use test folder
                return super.addOrder(order);
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        // Clean up test files
        File folder = new File(TEST_ORDER_FOLDER);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        folder.delete();
    }

    @Test
    public void testAddGetOrder() throws Exception {
        // Arrange
        Order order = new Order();
        order.setOrderNumber(1);
        order.setCustomerName("Test Customer");
        order.setState("TX");
        order.setTaxRate(new BigDecimal("4.45"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("100.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        order.setMaterialCost(new BigDecimal("350.00"));
        order.setLaborCost(new BigDecimal("415.00"));
        order.setTax(new BigDecimal("34.04"));
        order.setTotal(new BigDecimal("799.04"));
        order.setOrderDate(LocalDate.of(2025, 12, 1));

        // Act
        dao.addOrder(order);
        Order retrieved = dao.getOrder(LocalDate.of(2025, 12, 1), 1);

        // Assert
        assertNotNull(retrieved);
        assertEquals(order.getOrderNumber(), retrieved.getOrderNumber());
        assertEquals(order.getCustomerName(), retrieved.getCustomerName());
        assertEquals(order.getState(), retrieved.getState());
    }

    @Test(expected = NoSuchOrderException.class)
    public void testGetNonExistentOrder() throws Exception {
        dao.getOrder(LocalDate.of(2025, 12, 1), 999);
    }

    @Test
    public void testGetOrdersForDate() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 1);

        Order order1 = createTestOrder(1, testDate);
        Order order2 = createTestOrder(2, testDate);

        // Act
        dao.addOrder(order1);
        dao.addOrder(order2);
        List<Order> orders = dao.getOrdersForDate(testDate);

        // Assert
        assertEquals(2, orders.size());
    }

    @Test
    public void testRemoveOrder() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 1);
        Order order = createTestOrder(1, testDate);
        dao.addOrder(order);

        // Act
        Order removed = dao.removeOrder(testDate, 1);

        // Assert
        assertNotNull(removed);
        assertEquals(1, removed.getOrderNumber());

        // Verify it's actually removed
        List<Order> orders = dao.getOrdersForDate(testDate);
        assertEquals(0, orders.size());
    }

    @Test
    public void testEditOrder() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 12, 1);
        Order order = createTestOrder(1, testDate);
        dao.addOrder(order);

        // Modify the order
        order.setCustomerName("Updated Name");
        order.setArea(new BigDecimal("200.00"));

        // Act
        dao.editOrder(order);
        Order retrieved = dao.getOrder(testDate, 1);

        // Assert
        assertEquals("Updated Name", retrieved.getCustomerName());
        assertEquals(new BigDecimal("200.00"), retrieved.getArea());
    }

    @Test
    public void testGetNextOrderNumber() {
        // Act
        int firstOrderNumber = dao.getNextOrderNumber();

        // Assert
        assertTrue(firstOrderNumber >= 1);
    }

    private Order createTestOrder(int orderNumber, LocalDate date) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomerName("Test Customer " + orderNumber);
        order.setState("TX");
        order.setTaxRate(new BigDecimal("4.45"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("100.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        order.setMaterialCost(new BigDecimal("350.00"));
        order.setLaborCost(new BigDecimal("415.00"));
        order.setTax(new BigDecimal("34.04"));
        order.setTotal(new BigDecimal("799.04"));
        order.setOrderDate(date);
        return order;
    }
}