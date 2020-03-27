package ba.unsa.etf.si.models;

public class User {

    private String firstName;
    private String username;
    private String password;
    private String token;

    public User(String firstName, String username, String password, String token) {
        this.firstName = firstName;
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public User(String firstName, String username, String password) {
        this.firstName = firstName;
        this.username = username;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
