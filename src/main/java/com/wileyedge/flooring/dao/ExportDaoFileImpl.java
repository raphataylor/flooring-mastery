package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class ExportDaoFileImpl implements ExportDao {

    private final String EXPORT_FILE;
    private static final String DELIMITER = ",";
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area," +
            "CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,OrderDate";

    public ExportDaoFileImpl() {
        this.EXPORT_FILE = "Backup/DataExport.txt";
    }

    public ExportDaoFileImpl(String exportFile) {
        this.EXPORT_FILE = exportFile;
    }

    @Override
    public void exportAllData(Map<LocalDate, Map<Integer, Order>> allOrders) throws PersistenceException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(EXPORT_FILE));
        } catch (IOException e) {
            throw new PersistenceException("Could not write to export file.", e);
        }

        // Write header
        out.println(HEADER);

        // Write all orders
        for (Map.Entry<LocalDate, Map<Integer, Order>> dateEntry : allOrders.entrySet()) {
            for (Order order : dateEntry.getValue().values()) {
                out.println(marshallOrder(order));
            }
        }

        out.flush();
        out.close();
    }

    private String marshallOrder(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

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
                order.getTotal() + DELIMITER +
                order.getOrderDate().format(formatter);
    }
}