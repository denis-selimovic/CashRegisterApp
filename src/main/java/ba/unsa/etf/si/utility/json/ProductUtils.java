package ba.unsa.etf.si.utility.json;

import ba.unsa.etf.si.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProductUtils {

    public static Product getProductFromJSON(JSONObject jsonObj) {
        String imageString = null;
        if (jsonObj.get("imageBase64") != null) imageString = jsonObj.getString("imageBase64");
        return new Product(jsonObj.getLong("id"), jsonObj.getString("name"), jsonObj.getDouble("price"), imageString,
                jsonObj.getString("measurementUnit"), jsonObj.getDouble("discount"), jsonObj.getDouble("quantity"));
    }

    public static ObservableList<Product> getObservableProductListFromJSON(String response) {
        return FXCollections.observableArrayList(getProductsFromJSON(response));
    }

    public static List<Product> getProductsFromJSON(String jsonListOfProducts) {
        List<Product> productList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonListOfProducts);
        for (int i = 0; i < jsonArray.length(); i++) productList.add(getProductFromJSON((jsonArray.getJSONObject(i))));
        return productList;
    }
}
