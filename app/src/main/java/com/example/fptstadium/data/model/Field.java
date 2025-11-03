package com.example.fptstadium.data.model;

public class Field {
    private String name;
    private String address;
    private String distance;
    private String hours;
    private String phone;
    private String imageUrl;

    public Field(String name, String address, String distance, String hours, String phone, String imageUrl) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.hours = hours;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDistance() {
        return distance;
    }

    public String getHours() {
        return hours;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
