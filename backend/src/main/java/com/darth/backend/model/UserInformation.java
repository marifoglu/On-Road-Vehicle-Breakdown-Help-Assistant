package com.darth.backend.model;


import javax.persistence.*;

@Entity
@Table(name = "userinformation")
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Column(name = "email")
    private String userEmail;

    @Column(name = "nameAndSurname")
    private String userNameAndSurname;

    @Column(name = "homeAddress")
    private String userHomeAddress;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNameAndSurname() {
        return userNameAndSurname;
    }

    public void setUserNameAndSurname(String userNameAndSurname) {
        this.userNameAndSurname = userNameAndSurname;
    }

    public String getUserHomeAddress() {
        return userHomeAddress;
    }

    public void setUserHomeAddress(String userHomeAddress) {
        this.userHomeAddress = userHomeAddress;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    @Column(name = "phoneNumber")
    private String userPhoneNumber;



}
