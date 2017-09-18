package com.theironyard.invoicify.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.theironyard.invoicify.models.BillingRecord;
import com.theironyard.invoicify.models.Invoice;
import com.theironyard.invoicify.models.InvoiceLineItem;
import com.theironyard.invoicify.models.User;
import com.theironyard.invoicify.repositories.BillingRecordRepository;
import com.theironyard.invoicify.repositories.CompanyRepository;
import com.theironyard.invoicify.repositories.InvoiceRepository;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private CompanyRepository companyRepo;
    private BillingRecordRepository recordRepo;
    private InvoiceRepository invoiceRepo;

    public InvoiceController(CompanyRepository companyRepo, BillingRecordRepository recordRepo, InvoiceRepository invoiceRepo) {
        this.companyRepo = companyRepo;
        this.recordRepo = recordRepo;
        this.invoiceRepo = invoiceRepo;
    }

    @GetMapping("")
    public ModelAndView list(Authentication auth) {
        User user = (User) auth.getPrincipal();
        ModelAndView mv = new ModelAndView("invoices/list");
        mv.addObject("user", user);
        mv.addObject("invoices", invoiceRepo.findAll());
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView goToStepOne() {
        ModelAndView mv = new ModelAndView("invoices/step-1");
        mv.addObject("companies", companyRepo.findAll());
        return mv;
    }

    @PostMapping("/new")
    public ModelAndView goToStepTwo(long clientId) {
        ModelAndView mv = new ModelAndView("invoices/step-2");
        mv.addObject("errorMessage", "");
        mv.addObject("clientId", clientId);
        mv.addObject("records", recordRepo.findByClientIdAndLineItemIsNull(clientId));
        return mv;
    }

    @PostMapping("create")
    public ModelAndView create(Authentication auth, Invoice invoice, String invoiceNumber, long clientId, long[] recordIds) {
        ModelAndView mv = new ModelAndView();
        if (recordIds != null) {
            User creator = (User) auth.getPrincipal();
            List<BillingRecord> records = recordRepo.findByIdIn(recordIds);
            long nowish = Calendar.getInstance().getTimeInMillis();
            Date now = new Date(nowish);

            List<InvoiceLineItem> items = new ArrayList<InvoiceLineItem>();
            for (BillingRecord record : records) {
                InvoiceLineItem lineItem = new InvoiceLineItem();
                lineItem.setBillingRecord(record);
                lineItem.setCreatedBy(creator);
                lineItem.setCreatedOn(now);
                lineItem.setInvoice(invoice);
                items.add(lineItem);
            }
            invoice.setCreatedBy(creator);
            invoice.setCompany(companyRepo.findOne(clientId));
            invoice.setCreatedOn(now);
            invoice.setLineItems(items);
            invoiceRepo.save(invoice);
            mv.setViewName("redirect:/invoices");
        } else {
            mv.addObject("errorMessage", "Please select at least one billing record.");
            mv.addObject("clientId", clientId);
            mv.addObject("records", recordRepo.findByClientIdAndLineItemIsNull(clientId));
            mv.setViewName("invoices/step-2");
        }
        return mv;
    }
    
}
