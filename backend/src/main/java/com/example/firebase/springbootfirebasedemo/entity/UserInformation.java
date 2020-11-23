package com.example.firebase.springbootfirebasedemo.entity;


import java.util.UUID;

public class UserInformation {

    private String documentId;
    private String id;
    private String email;
    private String homeAddress;
    private String nameAndSurname;
    private String phoneNumber;


    public UserInformation() {
        this.id = UUID.randomUUID().toString();
    }
    public String getDocumentId() { return documentId; }

    public void setDocumentId(String documentId) {
        if (documentId == null) {
            throw new NullPointerException("Document ID cannot be null");
        }
        this.documentId = documentId;
    }
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getHomeAddress() { return homeAddress; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress;  }

    public String getNameAndSurname() { return nameAndSurname; }

    public void setNameAndSurname(String nameAndSurname) { this.nameAndSurname = nameAndSurname; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

}
