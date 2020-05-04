package ba.unsa.etf.si.utility.modelutils;

import ba.unsa.etf.si.models.Table;
import ba.unsa.etf.si.utility.stream.StreamUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

public class TableUtils {

    public static ObservableList<Table> getTablesFromJSON(JSONArray jsonArray) {
        ObservableList<Table> tables = FXCollections.observableArrayList();
        for(int i = 0; i < jsonArray.length(); ++i) tables.add(getTableFromJSON(jsonArray.getJSONObject(i)));
        return StreamUtils.sort(tables, (t1, t2) -> {
            if(t1.getTableNumber() < t2.getTableNumber()) return -1;
            else if(t1.getTableNumber() > t2.getTableNumber()) return 1;
            return 0;
        });
    }

    public static Table getTableFromJSON(JSONObject jsonObject) {
        return new Table(jsonObject.getLong("id"), jsonObject.getLong("tableNumber"));
    }
}
