package com.wileyedge.flooring.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Tax {
    private String state;
    private String stateAbbr;
    private BigDecimal taxRate;

    public Tax(BigDecimal taxRate, String stateAbbr, String state) {
        this.taxRate = taxRate;
        this.stateAbbr = stateAbbr;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateAbbr() {
        return stateAbbr;
    }

    public void setStateAbbr(String stateAbbr) {
        this.stateAbbr = stateAbbr;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tax tax = (Tax) o;
        return Objects.equals(state, tax.state) && Objects.equals(stateAbbr, tax.stateAbbr) && Objects.equals(taxRate, tax.taxRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, stateAbbr, taxRate);
    }

    @Override
    public String toString() {
        return "Tax{" +
                "state='" + state + '\'' +
                ", stateAbbr='" + stateAbbr + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}
