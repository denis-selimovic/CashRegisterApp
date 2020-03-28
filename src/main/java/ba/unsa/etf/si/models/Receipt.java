package ba.unsa.etf.si.models;

import java.util.Date;

public class Receipt {

    private Long id;
    private Date date;
    private String cashier;
    private Double amount;

    public Receipt(Long id, Date date, String cashier, Double amount) {
        this.id = id;
        this.date = date;
        this.cashier = cashier;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
