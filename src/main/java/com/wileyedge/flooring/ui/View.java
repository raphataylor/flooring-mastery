package com.wileyedge.flooring.ui;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.dto.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class View {

    private final UserIO io;

    @Autowired
    public View(UserIO io) {
        this.io = io;
    }

    public int displayMainMenuAndGetSelection() {
        io.print("\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        io.print("*");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

        return io.readInt("Please select from the above choices.", 1, 6);
    }

    public LocalDate getDateInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        while (true) {
            String dateStr = io.readString("Enter order date (MM-dd-yyyy):");
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                io.print("Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }

    public void displayOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            io.print("\nNo orders found for this date.");
            return;
        }

        io.print("\n=== Orders ===");
        for (Order order : orders) {
            displayOrderSummary(order);
        }
    }

    public void displayOrderInfo(Order order) {
        io.print("\n=== Order Details ===");
        io.print("Order Number: " + order.getOrderNumber());
        io.print("Customer Name: " + order.getCustomerName());
        io.print("State: " + order.getState());
        io.print("Tax Rate: " + order.getTaxRate() + "%");
        io.print("Product Type: " + order.getProductType());
        io.print("Area: " + order.getArea() + " sq ft");
        io.print("Cost Per Square Foot: $" + order.getCostPerSquareFoot());
        io.print("Labor Cost Per Square Foot: $" + order.getLaborCostPerSquareFoot());
        io.print("Material Cost: $" + order.getMaterialCost());
        io.print("Labor Cost: $" + order.getLaborCost());
        io.print("Tax: $" + order.getTax());
        io.print("Total: $" + order.getTotal());
    }

    private void displayOrderSummary(Order order) {
        io.print(String.format("\nOrder #%d - %s - %s - $%.2f",
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getProductType(),
                order.getTotal()));
    }

    public void displayAddOrderBanner() {
        io.print("\n=== Add Order ===");
    }

    public Order getAddOrderInput(List<Tax> taxes, List<Product> products) {
        Order order = new Order();

        // Get order date
        LocalDate orderDate = getFutureDate();
        order.setOrderDate(orderDate);

        // Get customer name
        String customerName = getCustomerName();
        order.setCustomerName(customerName);

        // Get state
        Tax tax = getStateInput(taxes);
        order.setState(tax.getStateAbr());
        order.setTaxRate(tax.getTaxRate());

        // Display products and get selection
        Product product = getProductInput(products);
        order.setProductType(product.getProductType());
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());

        // Get area
        BigDecimal area = getAreaInput();
        order.setArea(area);

        return order;
    }

    public LocalDate getFutureDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate today = LocalDate.now();

        while (true) {
            String dateStr = io.readString("Enter order date (MM-dd-yyyy) [Must be in the future]:");
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                if (date.isAfter(today)) {
                    return date;
                } else {
                    io.print("Order date must be in the future.");
                }
            } catch (DateTimeParseException e) {
                io.print("Invalid date format. Please use MM-dd-yyyy.");
            }
        }
    }

    public String getCustomerName() {
        while (true) {
            String name = io.readString("Enter customer name:");
            if (validateCustomerName(name)) {
                return name;
            }
            io.print("Customer name may not be blank and can only contain [a-z][0-9], periods, and commas.");
        }
    }

    private boolean validateCustomerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Allow alphanumeric, periods, commas, and spaces
        return name.matches("[a-zA-Z0-9., ]+");
    }

    public Tax getStateInput(List<Tax> taxes) {
        io.print("\n=== Available States ===");
        for (Tax tax : taxes) {
            io.print(tax.getStateAbr() + " - " + tax.getState() + " (Tax Rate: " + tax.getTaxRate() + "%)");
        }

        while (true) {
            String stateAbr = io.readString("\nEnter state abbreviation:").toUpperCase();

            for (Tax tax : taxes) {
                if (tax.getStateAbr().equalsIgnoreCase(stateAbr)) {
                    return tax;
                }
            }

            io.print("Invalid state. We can only sell in the states listed above.");
        }
    }

    public Product getProductInput(List<Product> products) {
        io.print("\n=== Available Products ===");
        for (Product product : products) {
            io.print(String.format("%s - Cost: $%.2f/sq ft - Labor: $%.2f/sq ft",
                    product.getProductType(),
                    product.getCostPerSquareFoot(),
                    product.getLaborCostPerSquareFoot()));
        }

        while (true) {
            String productType = io.readString("\nEnter product type:");

            for (Product product : products) {
                if (product.getProductType().equalsIgnoreCase(productType)) {
                    return product;
                }
            }

            io.print("Invalid product type. Please choose from the list above.");
        }
    }

    public BigDecimal getAreaInput() {
        while (true) {
            double area = io.readDouble("Enter area in square feet (minimum 100):");
            if (area >= 100) {
                return new BigDecimal(String.valueOf(area));
            }
            io.print("Minimum order size is 100 square feet.");
        }
    }

    public boolean getConfirmation(String message) {
        String response = io.readString(message + " (Y/N):").toUpperCase();
        return response.equals("Y") || response.equals("YES");
    }

    public void displayAddOrderSuccess() {
        io.print("\n*** Order successfully added! ***");
    }

    public void displayEditOrderBanner() {
        io.print("\n=== Edit Order ===");
    }

    public int getOrderNumberInput() {
        return io.readInt("Enter order number:");
    }

    public Order getEditOrderInput(Order existingOrder, List<Tax> taxes, List<Product> products) {
        Order editedOrder = new Order();
        editedOrder.setOrderNumber(existingOrder.getOrderNumber());
        editedOrder.setOrderDate(existingOrder.getOrderDate());

        // Customer Name
        String currentName = existingOrder.getCustomerName();
        String newName = io.readString("Enter customer name (" + currentName + "):");
        if (newName.trim().isEmpty()) {
            editedOrder.setCustomerName(currentName);
        } else if (validateCustomerName(newName)) {
            editedOrder.setCustomerName(newName);
        } else {
            io.print("Invalid name format. Keeping existing name.");
            editedOrder.setCustomerName(currentName);
        }

        // State
        String currentState = existingOrder.getState();
        io.print("\nCurrent state: " + currentState);
        String changeState = io.readString("Change state? (Y/N):").toUpperCase();

        Tax tax;
        if (changeState.equals("Y") || changeState.equals("YES")) {
            tax = getStateInput(taxes);
        } else {
            tax = taxes.stream()
                    .filter(t -> t.getStateAbr().equals(currentState))
                    .findFirst()
                    .orElse(null);
            if (tax == null) {
                io.print("Error: Could not find tax info for current state. Please select a new state.");
                tax = getStateInput(taxes);
            }
        }
        editedOrder.setState(tax.getStateAbr());
        editedOrder.setTaxRate(tax.getTaxRate());

        // Product
        String currentProduct = existingOrder.getProductType();
        io.print("\nCurrent product: " + currentProduct);
        String changeProduct = io.readString("Change product? (Y/N):").toUpperCase();

        Product product;
        if (changeProduct.equals("Y") || changeProduct.equals("YES")) {
            product = getProductInput(products);
        } else {
            product = products.stream()
                    .filter(p -> p.getProductType().equals(currentProduct))
                    .findFirst()
                    .orElse(null);
            if (product == null) {
                io.print("Error: Could not find current product. Please select a new product.");
                product = getProductInput(products);
            }
        }
        editedOrder.setProductType(product.getProductType());
        editedOrder.setCostPerSquareFoot(product.getCostPerSquareFoot());
        editedOrder.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());

        // Area
        BigDecimal currentArea = existingOrder.getArea();
        String newAreaStr = io.readString("Enter area in square feet (" + currentArea + "):");
        if (newAreaStr.trim().isEmpty()) {
            editedOrder.setArea(currentArea);
        } else {
            try {
                double newArea = Double.parseDouble(newAreaStr);
                if (newArea >= 100) {
                    editedOrder.setArea(new BigDecimal(String.valueOf(newArea)));
                } else {
                    io.print("Minimum area is 100 sq ft. Keeping existing area.");
                    editedOrder.setArea(currentArea);
                }
            } catch (NumberFormatException e) {
                io.print("Invalid area. Keeping existing area.");
                editedOrder.setArea(currentArea);
            }
        }

        return editedOrder;
    }

    public void displayEditOrderSuccess() {
        io.print("\n*** Order successfully edited! ***");
    }

    public void displayRemoveOrderBanner() {
        io.print("\n=== Remove Order ===");
    }

    public void displayRemoveOrderSuccess() {
        io.print("\n*** Order successfully removed! ***");
    }

    public void displayExportDataSuccess() {
        io.print("\n*** All data successfully exported! ***");
    }

    public void displayExitMessage() {
        io.print("\n*** Thank you for using Flooring Program! ***");
    }

    public void displayErrorMessage(String errorMsg) {
        io.print("\n=== ERROR ===");
        io.print(errorMsg);
    }

    public void displayUnknownCommandMessage() {
        io.print("\n*** Unknown Command ***");
    }

    public void pressEnterToContinue() {
        io.readString("\nPress Enter to continue...");
    }
}