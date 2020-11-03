package com.darth.backend.service;

import com.darth.backend.model.UserInformation;

import java.util.List;

public interface UserInformationService {
    List<UserInformation> getAllUsers();
    void saveUser(UserInformation userInformation);
}
