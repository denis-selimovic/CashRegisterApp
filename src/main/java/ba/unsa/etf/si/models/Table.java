package ba.unsa.etf.si.models;

public class Table {

    private Long serverID;
    private Long tableNumber;

    public Table () {}

    public Table(Long serverID, Long tableNumber) {
        this.serverID = serverID;
        this.tableNumber = tableNumber;
    }

    public Long getServerID() {
        return serverID;
    }

    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }

    public Long getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Long tableNumber) {
        this.tableNumber = tableNumber;
    }
}