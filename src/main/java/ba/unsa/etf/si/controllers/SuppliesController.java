package ba.unsa.etf.si.controllers;

import ba.unsa.etf.si.model.ProductModel;
import ba.unsa.etf.si.utility.HttpUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;


public class SuppliesController {

    public TableColumn productID;
    public TableColumn productImage;
    public TableColumn productName;
    public TableColumn quantityInStock;
    public TableView articleTable;
    public TextField searchBar;
    public static int x = 0;
    public TableColumn productUnit;

    private ObservableList<ProductModel> data = FXCollections.observableArrayList();
    private Image defaultImage= null;
    private final String RUTA = "cash-register-server-si.herokuapp.com/api/products" , TOKEN= null;


    //CALLBACK koji se poziva nakon requesta
    Consumer<String>  callback = (String str) -> {
        try {
            data= ProductModel.JSONProductListToObservableList(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        FilteredList<ProductModel> filterList = new FilteredList<>(data, b ->true);
        productID.setCellValueFactory(  new PropertyValueFactory<ProductModel, String>("id"));
        productImage.setCellFactory(param -> {
            //postavi imageview
            final ImageView imageview = new ImageView();
            //imageview.setPreserveRatio(true);
            imageview.setFitHeight(115);
            imageview.setFitWidth(115);

            //uspostavi tabelu
            TableCell<ProductModel, Image> cell = new TableCell<ProductModel, Image>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(item);
                    }
                   else {
                        imageview.setImage(null);
                    }
                }
            };
            //zakaci sliku na cell
            cell.setGraphic(imageview);
            return cell;
        });
        //postavka propertija za kolone
        productImage.setCellValueFactory(new PropertyValueFactory<ProductModel, Image>("image"));
        productName.setCellValueFactory(  new PropertyValueFactory<ProductModel, String>("name"));
        quantityInStock.setCellValueFactory(new PropertyValueFactory<ProductModel, String>("quantity"));
        productUnit.setCellValueFactory(new PropertyValueFactory<ProductModel, String>("unit"));
        //pretraga
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList.setPredicate(entry -> {
               //ako je textfield prazan ili "null" vrati sve proizvode
                if (newValue == null || newValue.isEmpty()) return true;

                // uporedi naziv proizvoda
                String lowerCaseFilter = newValue.toLowerCase();
                return entry.getName().toLowerCase().indexOf(lowerCaseFilter) != -1;
            });
        });
        SortedList<ProductModel> sortedList = new SortedList<>(filterList);
        sortedList.comparatorProperty().bind(articleTable.comparatorProperty());
        articleTable.setItems(sortedList);
    };


    @FXML
    public void initialize() {
        //slanje requesta
        HttpRequest getSuppliesData = HttpUtils.GET("https://raw.github.com/Lino2007/FakeAPI/master/db.json", "Content-Type", "application/json");
        HttpUtils.send(getSuppliesData, HttpResponse.BodyHandlers.ofString(), callback,    () -> {
            System.out.println("error");
        });
    }

}
