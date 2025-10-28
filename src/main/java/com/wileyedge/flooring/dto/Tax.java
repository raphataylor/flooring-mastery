package com.wileyedge.flooring.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class Tax {
    private String stateAbr;
    private String state;
    private BigDecimal taxRate;

    // Constructors
    public Tax() {
    }

    public Tax(String stateAbr, String state, BigDecimal taxRate) {
        this.stateAbr = stateAbr;
        this.state = state;
        this.taxRate = taxRate;
    }

    // Getters
    public String getStateAbr() {
        return stateAbr;
    }

    public void setStateAbr(String stateAbr) {
        this.stateAbr = stateAbr;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tax tax = (Tax) o;
        return Objects.equals(stateAbr, tax.stateAbr) &&
                Objects.equals(state, tax.state) &&
                Objects.equals(taxRate, tax.taxRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateAbr, state, taxRate);
    }

    @Override
    public String toString() {
        return "Tax{" +
                "stateAbr='" + stateAbr + '\'' +
                ", state='" + state + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}