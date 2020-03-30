package ba.unsa.etf.si.models;

import ba.unsa.etf.si.controllers.MyCashRegisterController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class Receipt {
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty price;
    private final SimpleDoubleProperty discount;
    private final SimpleDoubleProperty totalPrice;
    private TextField quantity;

    public TextField getQuantity() {
        return quantity;
    }

    public void setQuantity(TextField quantity) {
        this.quantity = quantity;
    }



    public Receipt(String name, Double price, Double discount) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.discount = new SimpleDoubleProperty(discount);
        this.quantity = new TextField("1");
        this.totalPrice = new SimpleDoubleProperty(price);
        this.quantity.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    quantity.setText(newValue.replaceAll("[^\\d]", ""));

                }
                for (Receipt recept:MyCashRegisterController.data) {
                    String temp1 = recept.getQuantity().getText();
                    if(temp1.isEmpty())temp1="1";

                    double temp=Double.parseDouble(String.valueOf(Double.parseDouble(temp1)*recept.getPrice()));
                    recept.setTotalPrice(temp);

                }
                MyCashRegisterController.data.add(new Receipt("",0.0,0.0));
                int a=MyCashRegisterController.data.size();
                MyCashRegisterController.data.remove(a-1);
            }
        });

    }


    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Double getPrice() {
        return price.get();
    }

    public void setPrice(Double price) {
        this.price.set(price);
    }

    public Double getDiscount() {
        return discount.get();
    }


    public void setDiscount(Double discount) {
        this.discount.set(discount);
    }

    public Double getTotalPrice() {
        return totalPrice.get();
    }


    public void setTotalPrice(Double totalPrice) {
        this.totalPrice.set(totalPrice);
    }
}
