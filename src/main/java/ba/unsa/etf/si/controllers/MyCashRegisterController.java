package ba.unsa.etf.si.controllers;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class MyCashRegisterController {

    public TableColumn productID;
    public TableColumn productName;
    public TableColumn productPrice;
    public TableColumn productQuantity;
    public TableColumn productDiscount;
    public TableColumn total;
    public TableView receiptTable;

    public static ObservableList<Receipt> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        productID.setCellValueFactory(new PropertyValueFactory<Receipt, Integer>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<Receipt, String>("name"));
        productPrice.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("price"));
        productDiscount.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("discount"));
        total.setCellValueFactory(new PropertyValueFactory<Receipt, Double>("totalPrice"));



        data.add(new Receipt(1,"nescafe", 2.30, 0.0));
        data.add(new Receipt(2,"7Days", 1.30,0.0));
        addSpinner();
        addButtonToTable();
        receiptTable.setItems(data);
    }


    private void addButtonToTable() {
        TableColumn<Receipt, Void> colBtn = new TableColumn("Remove");

        Callback<TableColumn<Receipt, Void>, TableCell<Receipt, Void>> cellFactory = new Callback<TableColumn<Receipt, Void>, TableCell<Receipt, Void>>() {
            @Override
            public TableCell<Receipt, Void> call(final TableColumn<Receipt, Void> param) {
                final TableCell<Receipt, Void> cell = new TableCell<Receipt, Void>() {

                    private final Button btn = new Button("Remove");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Receipt data1 = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data1);
                            receiptTable.getItems().removeAll(data1);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        receiptTable.getColumns().add(colBtn);

    }

    private void addSpinner(){
        TableColumn<Receipt, Spinner> SpinnerCol = new TableColumn<Receipt, Spinner>("Quantity");

        SpinnerCol.setMaxWidth(100);
        SpinnerCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Receipt, Spinner>, ObservableValue<Spinner>>() {

            @Override
            public ObservableValue<Spinner> call(
                    TableColumn.CellDataFeatures<Receipt, Spinner> arg0) {
                Receipt strumentoObject = arg0.getValue();

                Spinner<Integer> quantita = new Spinner<Integer>(1, 100, 1);
                quantita.valueProperty().addListener(
                        (obs, oldValue, newValue) -> {

                            System.out.println("New value: " + newValue);


                        }
                );

                return new SimpleObjectProperty<Spinner>(quantita);

            }

        });
        receiptTable.getColumns().add(SpinnerCol);
    }

    //privremena klasa, potrebno ju je definirati u modelu
    public class Receipt {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty name;
        private final SimpleDoubleProperty price;
        private final SimpleDoubleProperty discount;
        private final SimpleDoubleProperty totalPrice;



        private Receipt(Integer id, String name, Double price, Double discount) {
            this.id = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.price = new SimpleDoubleProperty(price);
            this.discount = new SimpleDoubleProperty(discount);
            this.totalPrice = new SimpleDoubleProperty(price);
        }

        public Integer getId() {
            return id.get();
        }


        public void setId(Integer id) {
            this.id.set(id);
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

}
