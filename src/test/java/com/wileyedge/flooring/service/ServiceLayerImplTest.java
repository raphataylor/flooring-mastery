package com.wileyedge.flooring.service;

import com.wileyedge.flooring.dao.*;
import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.dto.Tax;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class ServiceLayerImplTest {

    private ServiceLayer service;

    // Mock DAOs for testing
    private OrderDao orderDao;
    private ProductDao productDao;
    private TaxDao taxDao;
    private ExportDao exportDao;
    private AuditDao auditDao;

    @Before
    public void setUp() {
        // Create stub implementations
        orderDao = new OrderDaoStubImpl();
        productDao = new ProductDaoStubImpl();
        taxDao = new TaxDaoStubImpl();
        exportDao = new ExportDaoStubImpl();
        auditDao = new AuditDaoStubImpl();

        service = new ServiceLayerImpl(orderDao, productDao, taxDao, exportDao, auditDao);
    }

    @Test
    public void testAddOrder() throws Exception {
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
        order.setOrderDate(LocalDate.of(2025, 12, 1));

        // Act
        Order result = service.addOrder(order);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getMaterialCost());
        assertNotNull(result.getLaborCost());
        assertNotNull(result.getTax());
        assertNotNull(result.getTotal());

        // Verify calculations
        BigDecimal expectedMaterialCost = new BigDecimal("350.00");
        BigDecimal expectedLaborCost = new BigDecimal("415.00");

        assertEquals(expectedMaterialCost, result.getMaterialCost());
        assertEquals(expectedLaborCost, result.getLaborCost());
    }

    @Test
    public void testGetOrder() throws Exception {
        // Arrange - First add an order
        Order order = new Order();
        order.setOrderNumber(1);
        order.setCustomerName("Test Customer");
        order.setState("TX");
        order.setTaxRate(new BigDecimal("4.45"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("100.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        order.setOrderDate(LocalDate.of(2025, 12, 1));

        service.addOrder(order);

        // Act - Now retrieve it
        Order retrieved = service.getOrder(LocalDate.of(2025, 12, 1), 1);

        // Assert
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getOrderNumber());
    }

    @Test(expected = NoSuchOrderException.class)
    public void testGetNonExistentOrder() throws Exception {
        service.getOrder(LocalDate.of(2025, 12, 1), 999);
    }

    @Test
    public void testGetAllTaxes() throws Exception {
        // Act
        List<Tax> taxes = service.getAllTaxes();

        // Assert
        assertNotNull(taxes);
        assertTrue(taxes.size() > 0);
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Act
        List<Product> products = service.getAllProducts();

        // Assert
        assertNotNull(products);
        assertTrue(products.size() > 0);
    }

    @Test
    public void testCalculations() throws Exception {
        // Arrange - Test the calculation logic
        Order order = new Order();
        order.setArea(new BigDecimal("100.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        order.setTaxRate(new BigDecimal("4.45"));
        order.setOrderDate(LocalDate.of(2025, 12, 1));

        // Act
        Order result = service.addOrder(order);

        // Assert
        // MaterialCost = 100 * 3.50 = 350.00
        assertEquals(new BigDecimal("350.00"), result.getMaterialCost());

        // LaborCost = 100 * 4.15 = 415.00
        assertEquals(new BigDecimal("415.00"), result.getLaborCost());

        // Tax = (350 + 415) * (4.45 / 100) = 34.04
        assertEquals(new BigDecimal("34.04"), result.getTax());

        // Total = 350 + 415 + 34.04 = 799.04
        assertEquals(new BigDecimal("799.04"), result.getTotal());
    }

    // Stub implementations for testing
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
            return dateOrders == null ? new ArrayList<>() : new ArrayList<>(dateOrders.values());
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

    private static class ProductDaoStubImpl implements ProductDao {
        @Override
        public List<Product> getAllProducts() {
            List<Product> products = new ArrayList<>();
            products.add(new Product("Tile", new BigDecimal("3.50"), new BigDecimal("4.15")));
            products.add(new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10")));
            return products;
        }

        @Override
        public Product getProduct(String productType) {
            return new Product(productType, new BigDecimal("3.50"), new BigDecimal("4.15"));
        }
    }

    private static class TaxDaoStubImpl implements TaxDao {
        @Override
        public List<Tax> getAllTaxes() {
            List<Tax> taxes = new ArrayList<>();
            taxes.add(new Tax("TX", "Texas", new BigDecimal("4.45")));
            taxes.add(new Tax("CA", "California", new BigDecimal("25.00")));
            return taxes;
        }

        @Override
        public Tax getTax(String stateAbr) {
            return new Tax(stateAbr, "Texas", new BigDecimal("4.45"));
        }
    }

    private static class ExportDaoStubImpl implements ExportDao {
        @Override
        public void exportAllData(Map<LocalDate, Map<Integer, Order>> allOrders) {
            // Stub - does nothing
        }
    }

    private static class AuditDaoStubImpl implements AuditDao {
        @Override
        public void writeAuditEntry(String entry) {
            // Stub - does nothing
        }
    }
}