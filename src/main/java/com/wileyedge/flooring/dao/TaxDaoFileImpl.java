package com.wileyedge.flooring.dao;

import com.wileyedge.flooring.dto.Tax;
import com.wileyedge.flooring.exceptions.PersistenceException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

@Component
public class TaxDaoFileImpl implements TaxDao {

    private final String TAX_FILE;
    private static final String DELIMITER = ",";
    private Map<String, Tax> taxes = new HashMap<>();

    public TaxDaoFileImpl() {
        this.TAX_FILE = "Data/Taxes.txt";
    }

    public TaxDaoFileImpl(String taxFile) {
        this.TAX_FILE = taxFile;
    }

    @Override
    public List<Tax> getAllTaxes() throws PersistenceException {
        loadTaxes();
        return new ArrayList<>(taxes.values());
    }

    @Override
    public Tax getTax(String stateAbr) throws PersistenceException {
        loadTaxes();
        return taxes.get(stateAbr);
    }

    private void loadTaxes() throws PersistenceException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(TAX_FILE)));
        } catch (FileNotFoundException e) {
            throw new PersistenceException("Could not load tax data.", e);
        }

        taxes.clear();
        String currentLine;
        Tax currentTax;

        // Skip header line
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentTax = unmarshallTax(currentLine);
            taxes.put(currentTax.getStateAbr(), currentTax);
        }

        scanner.close();
    }

    private Tax unmarshallTax(String taxAsText) {
        String[] taxTokens = taxAsText.split(DELIMITER);

        String stateAbr = taxTokens[0];
        String stateName = taxTokens[1];
        BigDecimal taxRate = new BigDecimal(taxTokens[2]);

        Tax tax = new Tax(stateAbr, stateName, taxRate);

        return tax;
    }
}