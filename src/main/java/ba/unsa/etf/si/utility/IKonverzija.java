package ba.unsa.etf.si.utility;


import ba.unsa.etf.si.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public interface IKonverzija {

    static Product getProductFromJSON(JSONObject jsonObj) {
    String imageString = null;
    if (jsonObj.get("imageBase64") != null) imageString = jsonObj.getString("imageBase64");
    return new Product(jsonObj.getLong("id"), jsonObj.getString("name"), jsonObj.getDouble("price"), imageString,
            jsonObj.getString("measurementUnit"), jsonObj.getDouble("discount"), jsonObj.getDouble("quantity"));
     }

    static Product getProduct(JSONObject obj) {
    return getProductFromJSON(obj);
    }

    static Product getProduct(String json) {
    return getProductFromJSON(new JSONObject(json));
    }

    static ObservableList<Product> getObservableProductListFromJSON(String response) {
    ObservableList<Product> list = FXCollections.observableArrayList();
    JSONArray array = new JSONArray(response);
    for (int i = 0; i < array.length(); ++i) list.add(getProductFromJSON(array.getJSONObject(i)));
    return list;
    }

    static ArrayList<Product> getProductArrayFromJSON(String jsonListOfProducts) {
        JSONArray ja = new JSONArray(jsonListOfProducts);
        ArrayList<Product> productList = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            productList.add(getProduct(ja.getJSONObject(i)));
        }
        return productList;
    }

    static String getJSONFromProductArray(ArrayList<Product> productArrayList) {
        String jsonProductString = "[ ";
        if (productArrayList == null || productArrayList.size() ==0) return "[ ]";
        for (int i = 0; i < productArrayList.size(); i++) {
            if (i < productArrayList.size() - 1) {
                jsonProductString += productArrayList.get(i).toString() + ",\n";
            } else {
                jsonProductString += productArrayList.get(i).toString() + " ]";
            }
        }
        return jsonProductString;
    }

}
