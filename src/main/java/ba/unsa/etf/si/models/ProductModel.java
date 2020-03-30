package ba.unsa.etf.si.models;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class ProductModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty  name , unit;
    private final SimpleDoubleProperty price, discount, quantity;
    private Image image;

    public ProductModel(int id, String name, double price, String base64Image, String unit, double discount, double quantity) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        if (base64Image == null) {
            try {
                setDefaultImage();
            }
            catch (Exception e) {
                image=null;
            }
        }
        else {
            try {
                this.image = base64ToImageDecoder(base64Image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.unit = new SimpleStringProperty(unit);
        this.discount = new SimpleDoubleProperty(discount);
        this.quantity = new SimpleDoubleProperty(quantity);
    }


    public ProductModel(int id, String name, double price, Image image, String unit, double discount, double quantity) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.image = image;
        this.unit = new SimpleStringProperty(unit);
        this.discount = new SimpleDoubleProperty(discount);
        this.quantity = new SimpleDoubleProperty(quantity);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage (String base64Image) {
        if (base64Image == null) {
            try {
                setDefaultImage();
            }
            catch (Exception e) {
                e.printStackTrace();
                this.image=null;
            }
        }
        else {

            try {
                this.image = base64ToImageDecoder(base64Image);
            } catch (Exception e) {
                e.printStackTrace();
                this.image=null;
            }
        }
    }


    public int getId() {
        return id.get();
    }

    //obracunaj popust
    public double getDiscountedPrice () {
        return this.getPrice() - ((this.getDiscount()/100) * this.getPrice());
   }

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
        return  " { \n" +
                " \"id\" :" +  this.getId() +",\n"+
                " \"name\" :\"" + this.getName() + "\",\n" +
                " \"quantity\" :" + this.getQuantity() + ",\n" +
                " \"price\" :" + this.getPrice() + ",\n" +
                " \"discount\" :" + this.getDiscount() + ",\n" +
                " \"measurementUnit\" : \"" + this.getUnit() + "\",\n" +
                " \"imageBase64\" : \"" + imageToBase64Encoder(this.getImage()) + "\"\n }";
    }



    public static Image base64ToImageDecoder(String base64input) throws Exception {
        byte[] decodedBytes = null;

        try {
            if (base64input.contains("data:image/jpeg;")) throw new Exception("JPEG file");
            if (base64input.contains(","))
                decodedBytes = Base64.getMimeDecoder().decode(base64input.split(",")[1]);
            else
                decodedBytes = Base64.getMimeDecoder().decode(base64input);
        }
        catch (Exception e) {
            try {
               return getDefaultImage();
            }
            catch (Exception ec) {
                throw new Exception("Fatal error due to incorrect image type or missing default image.");
            }
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


    public static ProductModel JSONProductStringToProduct (String jsonProduct) {
        JSONObject jsonObj = new JSONObject(jsonProduct);
        return getProduct(jsonObj);
    }

    public static ProductModel JSONProductObjectToProduct (JSONObject jsonObj) {
        return getProduct(jsonObj);
    }

    private static ProductModel getProduct(JSONObject jsonObj) {
        String imageString = null;
        if (!(jsonObj.get("imageBase64").equals(null))) imageString= jsonObj.getString("imageBase64");
        return new ProductModel(jsonObj.getInt("id"),jsonObj.getString("name"), jsonObj.getDouble("price"), imageString,
                jsonObj.getString("measurementUnit"), jsonObj.getDouble("discount"), jsonObj.getDouble("quantity"));
    }

    public static ArrayList<ProductModel> JSONProductListToProductArray (String jsonListOfProducts) {
        JSONArray ja = new JSONArray(jsonListOfProducts);
        ArrayList<ProductModel> productList = new ArrayList<>();
        for (int i= 0 ; i<ja.length(); i++) {
            productList.add(JSONProductObjectToProduct(ja.getJSONObject(i)));
        }
        return  productList;
    }

    public static ObservableList<ProductModel> JSONProductListToObservableList (String jsonListOfProducts) {

        JSONArray ja = new JSONArray(jsonListOfProducts);
        ObservableList<ProductModel> observableList =  FXCollections.observableArrayList();
        for (int i=0 ; i<ja.length() ; i++) {
            observableList.add(JSONProductObjectToProduct(ja.getJSONObject(i)));
        }
        return observableList;
    }

    public static String ProductArrayToJSONString (ArrayList<ProductModel> productArrayList) {
        String jsonProductString = "[ ";
        for (int i=0 ; i<productArrayList.size(); i++) {
            if (i<productArrayList.size()-1) {
                jsonProductString += productArrayList.get(i).toString() + ",\n";
            }
            else {
                jsonProductString += productArrayList.get(i).toString() + " ]";
            }
        }
        return jsonProductString;
    }

    void setDefaultImage () throws IOException
    {
        FileInputStream inputstream = new FileInputStream("src/main/resources/ba/unsa/etf/si/img/no_icon.png");
        image = new Image (inputstream);
    }

    public static Image getDefaultImage() throws IOException
    {
        FileInputStream inputstream = new FileInputStream("src/main/resources/ba/unsa/etf/si/img/no_icon.png");
        return new Image (inputstream);
    }


}
