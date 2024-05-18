package carport.entities;

public class User {
    private int id;
    private String name;
    private String surname;
    private String password;
    private String role;
    private int addressId;

    public User(int id, String name, String surname, String password, String role, int addressId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.role = role;
        this.addressId = addressId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getAddressId() {
        return addressId;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }
}
