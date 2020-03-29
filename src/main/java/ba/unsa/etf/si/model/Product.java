package ba.unsa.etf.si.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class Product {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty  name , unit;
    private final SimpleDoubleProperty price, discount, quantity;
    private final Image image;

    public Product(int id, String name, double price, String base64Image, String unit, double discount, double quantity) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.image = base64ToImageDecoder(base64Image);
        this.unit = new SimpleStringProperty(unit);
        this.discount = new SimpleDoubleProperty(discount/100);
        this.quantity = new SimpleDoubleProperty(quantity);
    }


    public Product(int id, String name, double price, Image image, String unit, double discount, double quantity) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.image = image;
        this.unit = new SimpleStringProperty(unit);
        this.discount = new SimpleDoubleProperty(discount);
        this.quantity = new SimpleDoubleProperty(quantity);
    }

    public int getId() {
        return id.get();
    }

    //obracunaj popust
    //public double getDiscountedPrice () {
    //    return this.getPrice() - this.getDiscount() * this.getPrice();
   // }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getUnit() {
        return unit.get();
    }

    public SimpleStringProperty unitProperty() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public double getDiscount() {
        return discount.get();
    }

    public SimpleDoubleProperty discountProperty() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount.set(discount);
    }

    public double getQuantity() {
        return quantity.get();
    }

    public SimpleDoubleProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity.set(quantity);
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "{ \"product\":{ \"id\" : " + this.id.getValue() + ",\"name\" : \""  + this.name.getValue() +
                "\", \"price\" : " + this.price.getValue() + "\n,\"image\" : \"" + imageToBase64Encoder(this.image) + "\"\n,\"unit\" : \"" + this.unit.getValue() +
                "\" , \"discount\" : { \"percentage\" : " + this.discount.getValue() + " } }, \"quantity\":" + this.quantity.getValue()  +"}";

    }

    public static Image base64ToImageDecoder(String base64input) {
        byte[] decodedBytes = null;
        try {
            if (base64input.contains(","))
                decodedBytes = Base64.getMimeDecoder().decode(base64input.split(",")[1]);
            else
                decodedBytes = Base64.getMimeDecoder().decode(base64input);
        }
        catch (Exception e) {
            throw e;
        }
        ByteArrayInputStream imageArr =  new ByteArrayInputStream(decodedBytes);
        return new Image(imageArr);
    }


    public static String imageToBase64Encoder(Image image) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String base64String = null;
        try {
            ImageIO.write(bImage, "png" , b);
            byte[] imageBytes = b.toByteArray();

            Base64.Encoder encoder = Base64.getEncoder();
            base64String = encoder.encodeToString(imageBytes);
            b.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return base64String;
    }


    public static Product JSONProductStringToProduct (String jsonProduct) {
        System.out.println(jsonProduct);
        JSONObject jsonObj = new JSONObject(jsonProduct);
        JSONObject productObj = jsonObj.getJSONObject("product");
        JSONObject discount = productObj.getJSONObject("discount");

        return new Product(productObj.getInt("id"),productObj.getString("name"), productObj.getDouble("price"), productObj.getString("image"),
                productObj.getString("unit"), discount.getDouble("percentage"), jsonObj.getDouble("quantity"));
    }

    public static Product JSONProductObjectToProduct (JSONObject jsonObj) {
        JSONObject productObj = jsonObj.getJSONObject("product");
        JSONObject discount = productObj.getJSONObject("discount");

        return new Product(productObj.getInt("id"),productObj.getString("name"), productObj.getDouble("price"), productObj.getString("image"),
                productObj.getString("unit"), discount.getDouble("percentage"), jsonObj.getDouble("quantity"));
    }

    public static ArrayList<Product> JSONProductListToProductArray (String jsonListOfProducts) {
        JSONArray ja = new JSONArray(jsonListOfProducts);
        ArrayList<Product> productList = new ArrayList<>();
        for (int i= 0 ; i<ja.length(); i++) {
            productList.add(JSONProductObjectToProduct(ja.getJSONObject(i)));
        }
        return  productList;
    }

    public static ObservableList<Product> JSONProductListToObservableList (String jsonListOfProducts) {
        JSONArray ja = new JSONArray(jsonListOfProducts);
        ObservableList<Product> observableList =  FXCollections.observableArrayList();
        for (int i=0 ; i<ja.length() ; i++) {
            observableList.add(JSONProductObjectToProduct(ja.getJSONObject(i)));
        }
        return observableList;
    }

    public static String ProductArrayToJSON (ArrayList<Product> productArrayList) {
        String jsonProductString = "[ ";
        for (int i=0 ; i<productArrayList.size(); i++) {
            if (i<productArrayList.size()-1) {
                jsonProductString += productArrayList.get(i).toString() + ",\n";
            }
            else {
                jsonProductString += productArrayList.get(i).toString() + " ]";
            }
        }
        System.out.println(jsonProductString);
        return jsonProductString;
    }
}


/*{
   "product": {
       "id" : 111,
       "name" : "Prirodni sok",
       "price" : 2.50,
       "image" : "data:image/png;base64,",
       "unit" : "kom",
       "discount" : {
       "percentage" : 75
       }
   },
   "quantity" : 152.0
  }*/