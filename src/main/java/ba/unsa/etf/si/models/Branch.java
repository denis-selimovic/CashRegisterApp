package ba.unsa.etf.si.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public class Branch {
    SimpleIntegerProperty id = new SimpleIntegerProperty();
    SimpleStringProperty companyName = new SimpleStringProperty();

    public Branch(int id, String companyName) {
        this.id.set(id);
        this.companyName.set(companyName);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getCompanyName() {
        return companyName.get();
    }

    public SimpleStringProperty companyNameProperty() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName.set(companyName);
    }
}
