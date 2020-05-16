package ba.unsa.etf.si.models;

import lombok.Data;
import javax.persistence.*;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "daily_reports")
public class DailyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "cash_transactions")
    private int cashTransactions;

    @Column(name = "card_transactions")
    private int cardTransactions;

    @Column(name = "payApp_transactions")
    private int payAppTransactions;

    @Column(name = "total_amount")
    private float totalAmount;

    public DailyReport() {
        date = LocalDate.now();
        cashTransactions = cardTransactions = payAppTransactions = 0;
        totalAmount = 0f;
    }

    public DailyReport(LocalDate date, int cashTransactions, int cardTransactions, int payAppTransactions, float totalAmount) {
        this.date = date;
        this.cashTransactions = cashTransactions;
        this.cardTransactions = cardTransactions;
        this.payAppTransactions = payAppTransactions;
        this.totalAmount = totalAmount;
    }
}