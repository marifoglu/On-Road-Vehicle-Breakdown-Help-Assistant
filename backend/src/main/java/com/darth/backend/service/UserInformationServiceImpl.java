package com.darth.backend.service;

import com.darth.backend.model.UserInformation;
import com.darth.backend.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInformationServiceImpl implements UserInformationService{

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Override
    public List<UserInformation> getAllUsers() {
        return userInformationRepository.findAll();
    }

    @Override
    public void saveUser(UserInformation userInformation) {
        this.userInformationRepository.save(userInformation);
    }

    @Override
    public UserInformation getUserById(long id) {
        Optional<UserInformation> optional = userInformationRepository.findById(id);
        UserInformation userInformation = null;
        if (optional.isPresent()){
            userInformation = optional.get();
        }else {
            throw new RuntimeException("Could not found a user with this " + id + " id");
        }
        return userInformation;
    }
}
