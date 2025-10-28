package com.wileyedge.flooring.controller;

import com.wileyedge.flooring.dto.Order;
import com.wileyedge.flooring.dto.Product;
import com.wileyedge.flooring.dto.Tax;
import com.wileyedge.flooring.exceptions.NoSuchOrderException;
import com.wileyedge.flooring.exceptions.PersistenceException;
import com.wileyedge.flooring.service.ServiceLayer;
import com.wileyedge.flooring.ui.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class Controller {

    private final View view;
    private final ServiceLayer service;

    @Autowired
    public Controller(View view, ServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() {
        boolean keepGoing = true;
        int menuSelection;

        try {
            while (keepGoing) {
                menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportData();
                        break;
                    case 6:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }
            }
            exitMessage();
        } catch (PersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private int getMenuSelection() {
        return view.displayMainMenuAndGetSelection();
    }

    private void displayOrders() throws PersistenceException {
        LocalDate date = view.getDateInput();
        List<Order> orders = service.getOrdersForDate(date);
        view.displayOrders(orders);
        view.pressEnterToContinue();
    }

    private void addOrder() throws PersistenceException {
        view.displayAddOrderBanner();

        List<Tax> taxes = service.getAllTaxes();
        List<Product> products = service.getAllProducts();

        Order order = view.getAddOrderInput(taxes, products);

        // Set order number
        order.setOrderNumber(service.getNextOrderNumber());

        // Show order summary and confirm
        view.displayOrderInfo(order);

        boolean confirm = view.getConfirmation("\nWould you like to place this order?");

        if (confirm) {
            service.addOrder(order);
            view.displayAddOrderSuccess();
        } else {
            view.displayErrorMessage("Order was not placed.");
        }

        view.pressEnterToContinue();
    }

    private void editOrder() throws PersistenceException {
        view.displayEditOrderBanner();

        LocalDate date = view.getDateInput();
        int orderNumber = view.getOrderNumberInput();

        try {
            Order existingOrder = service.getOrder(date, orderNumber);
            view.displayOrderInfo(existingOrder);

            List<Tax> taxes = service.getAllTaxes();
            List<Product> products = service.getAllProducts();

            Order editedOrder = view.getEditOrderInput(existingOrder, taxes, products);

            // Show updated order summary
            view.displayOrderInfo(editedOrder);

            boolean confirm = view.getConfirmation("\nWould you like to save these changes?");

            if (confirm) {
                service.editOrder(editedOrder);
                view.displayEditOrderSuccess();
            } else {
                view.displayErrorMessage("Changes were not saved.");
            }

        } catch (NoSuchOrderException e) {
            view.displayErrorMessage(e.getMessage());
        }

        view.pressEnterToContinue();
    }

    private void removeOrder() throws PersistenceException {
        view.displayRemoveOrderBanner();

        LocalDate date = view.getDateInput();
        int orderNumber = view.getOrderNumberInput();

        try {
            Order order = service.getOrder(date, orderNumber);
            view.displayOrderInfo(order);

            boolean confirm = view.getConfirmation("\nAre you sure you want to remove this order?");

            if (confirm) {
                service.removeOrder(date, orderNumber);
                view.displayRemoveOrderSuccess();
            } else {
                view.displayErrorMessage("Order was not removed.");
            }

        } catch (NoSuchOrderException e) {
            view.displayErrorMessage(e.getMessage());
        }

        view.pressEnterToContinue();
    }

    private void exportData() throws PersistenceException {
        service.exportAllData();
        view.displayExportDataSuccess();
        view.pressEnterToContinue();
    }

    private void unknownCommand() {
        view.displayUnknownCommandMessage();
    }

    private void exitMessage() {
        view.displayExitMessage();
    }
}