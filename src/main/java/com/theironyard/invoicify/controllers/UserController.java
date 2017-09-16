package com.theironyard.invoicify.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
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
    public ModelAndView create(User user, String role) {
        ModelAndView mv = new ModelAndView("redirect:/admin/users");
        User newUser = new User();
        UserRole newRole = new UserRole(role, newUser);
        List<UserRole> roles = new ArrayList<UserRole>();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encoder.encode(user.getPassword()));
        roles.add(newRole);
        newUser.setRoles(roles);
        userRepo.save(newUser);
        return mv;
    }
}
