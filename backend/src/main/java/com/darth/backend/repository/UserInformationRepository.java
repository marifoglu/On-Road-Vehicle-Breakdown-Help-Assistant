package com.darth.backend.repository;

import com.darth.backend.model.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
}
