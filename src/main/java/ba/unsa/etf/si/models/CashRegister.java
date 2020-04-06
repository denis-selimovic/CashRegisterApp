package ba.unsa.etf.si.models;


import javax.persistence.*;

@Entity
@Table(name = "cash_register")
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long cashRegisterID;

    @Column
    private Long officeID;

    @Column
    private Long merchantID;

    @Column
    private String merchantNAme;

    public CashRegister() {}

    public CashRegister(Long cashRegisterID, Long officeID, Long merchantID, String merchantNAme) {
        this.cashRegisterID = cashRegisterID;
        this.officeID = officeID;
        this.merchantID = merchantID;
        this.merchantNAme = merchantNAme;
    }

    public Long getId() {
        return id;
    }

    public Long getCashRegisterID() {
        return cashRegisterID;
    }

    public void setCashRegisterID(Long cashRegisterID) {
        this.cashRegisterID = cashRegisterID;
    }

    public Long getOfficeID() {
        return officeID;
    }

    public void setOfficeID(Long officeID) {
        this.officeID = officeID;
    }

    public Long getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(Long merchantID) {
        this.merchantID = merchantID;
    }

    public String getMerchantNAme() {
        return merchantNAme;
    }

    public void setMerchantNAme(String merchantNAme) {
        this.merchantNAme = merchantNAme;
    }
}
