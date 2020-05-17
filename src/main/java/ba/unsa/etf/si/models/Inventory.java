package ba.unsa.etf.si.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    private String productName;
    private double productQuantity;
}
