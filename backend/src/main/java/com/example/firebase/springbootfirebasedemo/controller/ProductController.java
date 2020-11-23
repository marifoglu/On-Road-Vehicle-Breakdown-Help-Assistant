package com.example.firebase.springbootfirebasedemo.controller;

import com.example.firebase.springbootfirebasedemo.entity.UserInformation;
import com.example.firebase.springbootfirebasedemo.service.UserInformationService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("userList")
    public String userList(Model model) throws ExecutionException, InterruptedException {
        model.addAttribute("listOfUser", userInformationService.getAllUsers());
        return "userList";
    }

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        // create model attribute to bind form data
        UserInformation userInfo = new UserInformation();
        model.addAttribute("userInformation", userInfo); // Update the attribute name
        return "newUserForm";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("userInformation") UserInformation userInformation) throws ExecutionException, InterruptedException {
        // save employee to database
        userInformationService.saveUser(userInformation);
        return "redirect:/api/userList";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        userInformationService.deleteUserById(id);
        return "redirect:/api/userList";
    }
    
    @GetMapping("/updateUserForm/{id}")
    public String updateUserForm(@PathVariable("id") String documentID, Model model) {
        try {
            UserInformation userInformation = userInformationService.getUserById(documentID);
            model.addAttribute("userInformation", userInformation);
            return "updateUser";
        } catch (ExecutionException | InterruptedException e) {
            return "dashboard"; // Return an appropriate error page, will be created later.
        }
    }


}