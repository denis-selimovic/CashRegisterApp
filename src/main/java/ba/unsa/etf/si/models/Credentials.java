package ba.unsa.etf.si.models;

import javax.persistence.*;

@Entity
@Table(name = "login_credentials")
public class Credentials {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    private User.UserRole userRole;

    public Credentials() {}

    public Credentials(Long id, String username, String password, String name, User.UserRole userRole) {
        this(username, password, name, userRole);
        this.id = id;
    }

    public Credentials(String username, String password, String name, User.UserRole userRole) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User.UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(User.UserRole userRole) {
        this.userRole = userRole;
    }
}
