package ba.unsa.etf.si.models;

public class User {

    public static enum UserRole {
        ROLE_CASHIER("ROLE_CASHIER"), ROLE_OFFICEMAN("ROLE_OFFICEMAN"), ROLE_PRW("ROLE_PRW"), ROLE_MANAGER("ROLE_MANAGER"), ROLE_WAREMAN("ROLE_WAREMAN");

        private String role;

        UserRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }

    private String name;
    private String surname;
    private String address;
    private String city;
    private String country;
    private String phoneNumber;
    private String email;
    private String username;
    private UserRole userRole;
    private String token;
    boolean oneTimePassword;

    public User(String name, String surname, String address, String city, String country, String phoneNumber, String email, String username, UserRole userRole, boolean otp) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.city = city;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        this.userRole = userRole;
        this.oneTimePassword = otp;
    }

    public User(Credentials credentials) {
        this.name = credentials.getName();
        this.username = credentials.getUsername();
        this.userRole = credentials.getUserRole();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isUsingOtp(){
        return this.oneTimePassword;
    }

    public void clearOneTimePassword(){
        this.oneTimePassword = false;
    }

    @Override
    public String toString() {
        return "Name: " + name + "\nSurname: " + surname + "\nUsername: " + username + "\nRole: " + userRole.getRole() +
                "\nEmail: " + email + "\nCountry: " + country + "\nCity: " + city + "\nAddress: " + address
                + "\nPhone number: " + phoneNumber + "\nToken: " + token;
    }
}
