package com.theironyard.invoicify.controllers;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.theironyard.invoicify.models.Company;
import com.theironyard.invoicify.repositories.CompanyRepository;

@Controller
@RequestMapping("/admin/companies")
public class CompanyController {

    private CompanyRepository companyRepo;
    
    public CompanyController(CompanyRepository companyRepo) {
        this.companyRepo = companyRepo;
    }
    
    @GetMapping("")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("companies/list");
        mv.addObject("companies", companyRepo.findAll(new Sort("name")));
        return mv;
    }
    
    @PostMapping("")
    public ModelAndView create(Company company) {
        ModelAndView mv = new ModelAndView("redirect:/admin/companies");
        companyRepo.save(company);
        return mv;
    }
    
    
}
