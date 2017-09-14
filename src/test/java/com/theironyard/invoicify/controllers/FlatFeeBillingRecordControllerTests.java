package com.theironyard.invoicify.controllers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;
import org.junit.Test;

import com.theironyard.invoicify.models.Company;
import com.theironyard.invoicify.models.FlatFeeBillingRecord;
import com.theironyard.invoicify.models.User;
import com.theironyard.invoicify.repositories.BillingRecordRepository;
import com.theironyard.invoicify.repositories.CompanyRepository;

public class FlatFeeBillingRecordControllerTests {
    
    @Test
    public void test_that_create_returns_new_record() {
        BillingRecordRepository recordRepo = mock(BillingRecordRepository.class);
        CompanyRepository companyRepo = mock(CompanyRepository.class);
        FlatFeeBillingRecordController controller = new FlatFeeBillingRecordController(recordRepo, companyRepo);
        FlatFeeBillingRecord record = new FlatFeeBillingRecord();
        Authentication auth = mock(Authentication.class);
        Company company = new Company();
        User user = new User();
        when(companyRepo.findOne(1l)).thenReturn(company);
        when(auth.getPrincipal()).thenReturn(user);
        
        ModelAndView actual = controller.create(record, 1l, auth);
        
        assertThat(record.getCreatedBy()).isSameAs(user);
        assertThat(record.getClient()).isSameAs(company);
        assertThat(actual.getViewName()).isEqualTo("redirect:/billing-records");
        verify(companyRepo).findOne(1l);
        verify(recordRepo).save(record);
        
        
    }

}
