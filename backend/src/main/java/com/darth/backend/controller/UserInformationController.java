package com.darth.backend.controller;

import com.darth.backend.model.UserInformation;
import com.darth.backend.service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserInformationController {

    @Autowired
    private UserInformationService userInformationService;

    private String nameAndSurname;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // Display all listed users
    @GetMapping("userList")
    public String viewHomePage(Model model) {
        model.addAttribute("listUsers", userInformationService.getAllUsers());
        return "userList";
    }

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        // create model attribute to bind form data
        UserInformation userInformation = new UserInformation();
        model.addAttribute("userInformation", userInformation); // Update the attribute name
        return "newUserForm";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("userInformation") UserInformation userInformation) {
        // save employee to database
        userInformationService.saveUser(userInformation);
        return "redirect:/userList";
    }
}
