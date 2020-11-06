package com.darth.backend.service;

import com.darth.backend.model.UserInformation;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserInformationService {
    List<UserInformation> getAllUsers();
    void saveUser(UserInformation userInformation);
    UserInformation getUserById(long id);
    void deleteUserInformationById(long id);
    Page<UserInformation> findPaginated(int pageNo, int pageSize);

}
