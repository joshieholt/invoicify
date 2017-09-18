package com.theironyard.invoicify.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.theironyard.invoicify.models.User;
import com.theironyard.invoicify.models.UserRole;
import com.theironyard.invoicify.repositories.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    
    private UserRepository userRepo;
    private PasswordEncoder encoder;
    
    public UserController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }
    
    @GetMapping("")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("users/list");
        mv.addObject("users", userRepo.findAll());
        return mv;
    }

    @PostMapping("")
    public ModelAndView create(User user, boolean makeAdmin, boolean makeAccountant, boolean makeClerk, boolean makeTeacher) {
        ModelAndView mv = new ModelAndView("redirect:/admin/users");
        User newUser = new User();
        List<UserRole> roles = new ArrayList<UserRole>();
        if (makeAdmin) {
            UserRole newRole = new UserRole("ADMIN", newUser);
            roles.add(newRole);
        }
        if (makeAccountant) {
            UserRole newRole = new UserRole("ACCOUNTANT", newUser);
            roles.add(newRole);
        }
        if (makeClerk) {
            UserRole newRole = new UserRole("CLERK", newUser);
            roles.add(newRole);
        }
        if (makeTeacher) {
            UserRole newRole = new UserRole("TEACHER", newUser);
            roles.add(newRole);
        }
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encoder.encode(user.getPassword()));
        newUser.setRoles(roles);
        userRepo.save(newUser);
        return mv;
    }
    
    @PostMapping("/roles")
    public ModelAndView updateRoles(long userId, boolean makeAdmin, boolean makeAccountant, boolean makeClerk, boolean makeTeacher) {
        ModelAndView mv = new ModelAndView("redirect:/admin/users");
        User theUser = userRepo.findOne(userId);
        List<UserRole> roles = new ArrayList<UserRole>();

        if (makeAdmin && !theUser.isAdmin()) {
            UserRole newRole = new UserRole("ADMIN", theUser);
            roles.add(newRole);
        }
        if (makeAccountant && !theUser.isAccountant()) {
            UserRole newRole = new UserRole("ACCOUNTANT", theUser);
            roles.add(newRole);
        }
        if (makeClerk && !theUser.isClerk()) {
            UserRole newRole = new UserRole("CLERK", theUser);
            roles.add(newRole);
        }
        if (makeTeacher && !theUser.isTeacher()) {
            UserRole newRole = new UserRole("TEACHER", theUser);
            roles.add(newRole);
        }
        theUser.setRoles(roles);
        userRepo.save(theUser);
        return mv;
    }
}
