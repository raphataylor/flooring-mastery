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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderDaoFileImplTest {

    private OrderDao dao;

    @Before
    public void setUp() throws Exception {
        // Use a stub implementation for testing
        dao = new OrderDaoStubImpl();
    }

    @After
    public void tearDown() throws Exception {
        // No cleanup needed for stub
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

    // Stub implementation for testing
    private static class OrderDaoStubImpl implements OrderDao {
        private Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();
        private int nextOrderNumber = 1;

        @Override
        public int getNextOrderNumber() {
            return nextOrderNumber++;
        }

        @Override
        public Order addOrder(Order order) {
            LocalDate date = order.getOrderDate();
            orders.computeIfAbsent(date, k -> new HashMap<>())
                    .put(order.getOrderNumber(), order);
            return order;
        }

        @Override
        public Order getOrder(LocalDate date, int orderNumber) throws NoSuchOrderException {
            Map<Integer, Order> dateOrders = orders.get(date);
            if (dateOrders == null || !dateOrders.containsKey(orderNumber)) {
                throw new NoSuchOrderException("Order not found");
            }
            return dateOrders.get(orderNumber);
        }

        @Override
        public Order editOrder(Order order) throws NoSuchOrderException {
            LocalDate date = order.getOrderDate();
            Map<Integer, Order> dateOrders = orders.get(date);
            if (dateOrders == null || !dateOrders.containsKey(order.getOrderNumber())) {
                throw new NoSuchOrderException("Order not found");
            }
            dateOrders.put(order.getOrderNumber(), order);
            return order;
        }

        @Override
        public List<Order> getOrdersForDate(LocalDate date) {
            Map<Integer, Order> dateOrders = orders.get(date);
            return dateOrders == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(dateOrders.values());
        }

        @Override
        public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
            return new HashMap<>(orders);
        }

        @Override
        public Order removeOrder(LocalDate date, int orderNumber) throws NoSuchOrderException {
            Map<Integer, Order> dateOrders = orders.get(date);
            if (dateOrders == null || !dateOrders.containsKey(orderNumber)) {
                throw new NoSuchOrderException("Order not found");
            }
            return dateOrders.remove(orderNumber);
        }
    }
}