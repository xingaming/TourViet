package fpt.aptech.api.resource.admin;

import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Company;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.service.IManagementCompany;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ManagementCompanyResource {

    @Autowired
    IManagementCompany service;

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> showCompanyList() {
        try {
            List<Company> companies = service.getListCompany();
            return new ResponseEntity<>(companies, HttpStatus.OK);
        } catch (Exception e) {
            // Handle the exception and provide an appropriate error response
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable int companyId) {
        try {
            Company company = service.getCompanyById(companyId);
            return ResponseEntity.ok(company);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/edit-company")
    public ResponseEntity<?> editCompany(@RequestBody Company company) {
        try {
            int roleId = RoleId.SUPER_ADMIN.getValue();
            Company updatedCompany = service.editCompany(roleId, company);
            return ResponseEntity.ok(updatedCompany);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/paymentPAID")
    public ResponseEntity<List<Payment>> getPaidPayment() {
        try {
            List<Payment> payment = service.getPAIDPayment();
            return ResponseEntity.ok(payment);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    
    @GetMapping("/company/{companyId}/paymentPAID")
    public ResponseEntity<List<Payment>> getPaidPaymentByCompany(@PathVariable int companyId) {
        try {
            List<Payment> payment = service.getPaidPayment(companyId);
            return ResponseEntity.ok(payment);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    
    @PostMapping("/create-company")
    public Company createCompany(@RequestBody Company company) {
        int roleId = RoleId.SUPER_ADMIN.getValue();
        return service.createCompany(roleId, company);
    }
}
