package com.example.fptstadium.data.model.request;

public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public UpdateProfileRequest(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
