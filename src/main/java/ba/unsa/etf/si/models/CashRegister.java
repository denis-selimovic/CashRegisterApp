package ba.unsa.etf.si.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cash_register")
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cash_register_id")
    private Long cashRegisterID;

    @Column(name = "cash_register_name")
    private String cashRegisterName;

    @Column(name = "office_id")
    private Long officeID;

    @Column(name = "merchant_id")
    private Long merchantID;

    @Column(name = "merchant_name")
    private String merchantName;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "restaurant")
    private boolean restaurant;
}
