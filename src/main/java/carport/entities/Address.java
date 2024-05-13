package carport.entities;

public class Address {
    private String street;
    private int number;
    private int floor;
    private String info;
    private int zip;


    public Address(String street, int number, int floor, String info, int zip) {
        this.street = street;
        this.number = number;
        this.floor = floor;
        this.info = info;
        this.zip = zip;
    }


    public String getStreet() {
        return street;
    }

    public int getNumber() {
        return number;
    }

    public int getFloor() {
        return floor;
    }

    public String getInfo() {
        return info;
    }

    public int getZip() {
        return zip;
    }
}