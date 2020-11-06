package com.darth.backend.service;

import com.darth.backend.model.UserInformation;
import com.darth.backend.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public void deleteUserInformationById(long id) {
        this.userInformationRepository.deleteById(id);
    }

    @Override
    public Page<UserInformation> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.userInformationRepository.findAll(pageable);
    }
}
