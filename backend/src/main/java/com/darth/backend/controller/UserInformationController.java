package com.darth.backend.controller;

import com.darth.backend.service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserInformationController {

    @Autowired
    private UserInformationService userInformationService;

    // Display all listed users
    @GetMapping("users")
    public String viewHomePage(Model model){
        model.addAttribute("listUsers", userInformationService.getAllUsers());
        return "users";
    }
}
