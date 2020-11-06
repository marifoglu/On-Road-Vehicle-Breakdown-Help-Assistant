package com.darth.backend.controller;

import com.darth.backend.model.UserInformation;
import com.darth.backend.service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
    @GetMapping("/userList")
    public String viewHomePage(Model model) {
        // model.addAttribute("listUsers", userInformationService.getAllUsers());
        // return "userList";
        return findPaginated(1,model);
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

    @GetMapping("/showFromUserForm/{id}")
    public String showFromUserForm(@PathVariable( value = "id") long id, Model model) {

        // get user
        UserInformation userInformation = userInformationService.getUserById(id);
        // set user as a model
        model.addAttribute("userInformation", userInformation);
        return "updateUser";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable (value = "id") long id) {

        this.userInformationService.deleteUserInformationById(id);
        return "redirect:/userList";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model){
        int pageSize = 5;

        Page<UserInformation> page = userInformationService.findPaginated(pageNo, pageSize);
        List<UserInformation> listOfUsers = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listOfUsers);
        return "userList";



    }
}
