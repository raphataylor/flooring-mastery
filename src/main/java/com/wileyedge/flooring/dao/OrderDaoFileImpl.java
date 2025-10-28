package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OrderDaoFileImpl implements OrderDao {

    private static final String DELIMITER = ",";
    private static final String ORDER_FOLDER = "Orders";
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area," +
            "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";

    private Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();
    private int largestOrderNumber = 0;

    public OrderDaoFileImpl() {
        // Ensure Orders directory exists
        File folder = new File(ORDER_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    public int getNextOrderNumber() {
        try {
            loadAllOrders();
        } catch (PersistenceException e) {
            // If can't load, start from 1
        }
        return largestOrderNumber + 1;
    }

    @Override
    public Order addOrder(Order order) throws PersistenceException {
        loadOrdersForDate(order.getOrderDate());

        Map<Integer, Order> ordersForDate = orders.computeIfAbsent(
                order.getOrderDate(),
                k -> new HashMap<>()
        );

        // Set order number if not already set
        if (order.getOrderNumber() == 0) {
            order.setOrderNumber(getNextOrderNumber());
        }

        ordersForDate.put(order.getOrderNumber(), order);

        // Update largest order number
        if (order.getOrderNumber() > largestOrderNumber) {
            largestOrderNumber = order.getOrderNumber();
        }

        writeOrdersForDate(order.getOrderDate());

        return order;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException {
        loadOrdersForDate(date);

        Map<Integer, Order> ordersForDate = orders.get(date);

        if (ordersForDate == null || !ordersForDate.containsKey(orderNumber)) {
            throw new NoSuchOrderException("No order found with number " + orderNumber + " on date " + date);
        }

        return ordersForDate.get(orderNumber);
    }

    @Override
    public Order editOrder(Order order) throws NoSuchOrderException, PersistenceException {
        loadOrdersForDate(order.getOrderDate());

        Map<Integer, Order> ordersForDate = orders.get(order.getOrderDate());

        if (ordersForDate == null || !ordersForDate.containsKey(order.getOrderNumber())) {
            throw new NoSuchOrderException("No order found with number " + order.getOrderNumber());
        }

        ordersForDate.put(order.getOrderNumber(), order);
        writeOrdersForDate(order.getOrderDate());

        return order;
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws PersistenceException {
        loadOrdersForDate(date);

        Map<Integer, Order> ordersForDate = orders.get(date);

        if (ordersForDate == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(ordersForDate.values());
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() throws PersistenceException {
        loadAllOrders();
        return new HashMap<>(orders);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws NoSuchOrderException, PersistenceException {
        loadOrdersForDate(date);

        Map<Integer, Order> ordersForDate = orders.get(date);

        if (ordersForDate == null || !ordersForDate.containsKey(orderNumber)) {
            throw new NoSuchOrderException("No order found with number " + orderNumber);
        }

        Order removedOrder = ordersForDate.remove(orderNumber);
        writeOrdersForDate(date);

        return removedOrder;
    }

    private void loadOrdersForDate(LocalDate date) throws PersistenceException {
        String fileName = getFileNameForDate(date);
        File file = new File(fileName);

        // If file doesn't exist, there are no orders for this date
        if (!file.exists()) {
            return;
        }

        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(fileName)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load order data for date " + date, e);
        }

        Map<Integer, Order> ordersForDate = new HashMap<>();
        String currentLine;
        Order currentOrder;

        // Skip header line
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentOrder = unmarshallOrder(currentLine, date);
            ordersForDate.put(currentOrder.getOrderNumber(), currentOrder);

            // Track largest order number
            if (currentOrder.getOrderNumber() > largestOrderNumber) {
                largestOrderNumber = currentOrder.getOrderNumber();
            }
        }

        scanner.close();
        orders.put(date, ordersForDate);
    }

    private void loadAllOrders() throws PersistenceException {
        File folder = new File(ORDER_FOLDER);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return;
        }

        orders.clear();
        largestOrderNumber = 0;

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith("Orders_")) {
                LocalDate date = getDateFromFileName(file.getName());
                if (date != null) {
                    loadOrdersForDate(date);
                }
            }
        }
    }

    private void writeOrdersForDate(LocalDate date) throws PersistenceException {
        String fileName = getFileNameForDate(date);
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e) {
            throw new PersistenceException("Could not save order data for date " + date, e);
        }

        // Write header
        out.println(HEADER);

        // Write orders
        Map<Integer, Order> ordersForDate = orders.get(date);
        if (ordersForDate != null) {
            for (Order order : ordersForDate.values()) {
                out.println(marshallOrder(order));
            }
        }

        out.flush();
        out.close();
    }

    private String getFileNameForDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        return ORDER_FOLDER + "/Orders_" + date.format(formatter) + ".txt";
    }

    private LocalDate getDateFromFileName(String fileName) {
        try {
            // Extract date from "Orders_MMddyyyy.txt"
            String datePart = fileName.substring(7, 15); // Gets MMddyyyy
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
            return LocalDate.parse(datePart, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private Order unmarshallOrder(String orderAsText, LocalDate date) {
        String[] orderTokens = orderAsText.split(DELIMITER);

        Order order = new Order();
        order.setOrderNumber(Integer.parseInt(orderTokens[0]));
        order.setCustomerName(orderTokens[1]);
        order.setState(orderTokens[2]);
        order.setTaxRate(new BigDecimal(orderTokens[3]));
        order.setProductType(orderTokens[4]);
        order.setArea(new BigDecimal(orderTokens[5]));
        order.setCostPerSquareFoot(new BigDecimal(orderTokens[6]));
        order.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[7]));
        order.setMaterialCost(new BigDecimal(orderTokens[8]));
        order.setLaborCost(new BigDecimal(orderTokens[9]));
        order.setTax(new BigDecimal(orderTokens[10]));
        order.setTotal(new BigDecimal(orderTokens[11]));
        order.setOrderDate(date);

        return order;
    }

    private String marshallOrder(Order order) {
        return order.getOrderNumber() + DELIMITER +
                order.getCustomerName() + DELIMITER +
                order.getState() + DELIMITER +
                order.getTaxRate() + DELIMITER +
                order.getProductType() + DELIMITER +
                order.getArea() + DELIMITER +
                order.getCostPerSquareFoot() + DELIMITER +
                order.getLaborCostPerSquareFoot() + DELIMITER +
                order.getMaterialCost() + DELIMITER +
                order.getLaborCost() + DELIMITER +
                order.getTax() + DELIMITER +
                order.getTotal();
    }
}