package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.models.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

public class TableUtils {

    public static ObservableList<Table> getTablesFromJSON(JSONArray jsonArray) {
        ObservableList<Table> tables = FXCollections.observableArrayList();
        for(int i = 0; i < jsonArray.length(); ++i) tables.add(getTableFromJSON(jsonArray.getJSONObject(i)));
        return tables;
    }

    public static Table getTableFromJSON(JSONObject jsonObject) {
        return new Table(jsonObject.getLong("id"), jsonObject.getString("tableName"));
    }
}
