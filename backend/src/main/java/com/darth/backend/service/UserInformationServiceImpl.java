package com.darth.backend.service;

import com.darth.backend.model.UserInformation;
import com.darth.backend.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInformationServiceImpl implements UserInformationService{

    @Autowired
    private UserInformationRepository userInformationRepository;

    @Override
    public List<UserInformation> getAllUsers() {
        return userInformationRepository.findAll();
    }
}
