package com.example.firebase.springbootfirebasedemo.controller;

import com.example.firebase.springbootfirebasedemo.entity.UserInformation;
import com.example.firebase.springbootfirebasedemo.service.UserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

    /*
@Controller
//@RequestMapping("/userInformation")
public class UserInformationController {

    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    // Display all listed users
    @GetMapping("/userList")
    public String viewHomePage(Model model) throws ExecutionException, InterruptedException {
        model.addAttribute("listUsers", userInformationService.getUserDetails());
        return "userList";
//        return findPaginated(1,model);
    }

    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        // create model attribute to bind form data
        UserInformation userInformation = new UserInformation();
        model.addAttribute("userInformation", userInformation); // Update the attribute name
        return "newUserForm";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("userInformation") UserInformation userInformation) throws ExecutionException, InterruptedException {
        // save employee to database
        userInformationService.saveUser(userInformation);
        return "redirect:/userList";
    }

    @GetMapping("/showFromUserForm/{id}")
    public String showFromUserForm(@PathVariable( value = "id") long id, Model model) throws ExecutionException, InterruptedException {

        // get user
        UserInformation userInformation = userInformationService.getUserById(id);
        // set user as a model
        model.addAttribute("userInformation", userInformation);
        return "updateUser";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable (value = "id") long id) throws ExecutionException, InterruptedException {

        this.userInformationService.deleteUserById(id);
        return "redirect:/userList";
    }
}*/

@Controller
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private UserInformationService userInformationService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

//    @GetMapping("/products")
//    public String getAllProducts(Model model) throws ExecutionException, InterruptedException {
//        List<UserInformation> products = userInformationService.getUserDetails();
//        model.addAttribute("products", products);
//        return "products";
//    }

    @GetMapping("userList")
    public String viewHomePage(Model model) throws ExecutionException, InterruptedException {
        model.addAttribute("listOfUser", userInformationService.getUserDetails());
        return "products";
    }


    @GetMapping("/showNewUserForm")
    public String showNewUserForm(Model model) {
        // create model attribute to bind form data
        UserInformation userInformation = new UserInformation();
        model.addAttribute("userInformation", userInformation); // Update the attribute name
        return "newUserForm";
    }
//
//    @PostMapping("/saveUser")
//    public String saveUser(@RequestBody UserInformation product) throws ExecutionException, InterruptedException {
//
//        return userInformationService.saveUser(product);
//    }


    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("userInformation") UserInformation userInformation) throws ExecutionException, InterruptedException {
        // save employee to database
        userInformationService.saveUser(userInformation);
        return "redirect:/api/userList";
    }

    @GetMapping("/products/{name}")
    public UserInformation getProduct(@PathVariable String name) throws ExecutionException, InterruptedException {

        return userInformationService.getUserByName(name);
    }

//    @GetMapping("/products")
//    public List<UserInformation> getAllProducts() throws ExecutionException, InterruptedException {
//
//        return userInformationService.getUserDetails();
//    }


    @PutMapping("/products")
    public String update(@RequestBody UserInformation product) throws ExecutionException, InterruptedException {

        return userInformationService.updateUser(product);
    }


    @DeleteMapping("/products/{name}")
    public String deleteProduct(@PathVariable String name) throws ExecutionException, InterruptedException {

        return userInformationService.deleteUserById(2);
    }
}
