package fpt.aptech.api.service;

import fpt.aptech.api.models.Company;
import fpt.aptech.api.models.Payment;
import java.util.List;


public interface IManagementCompany {
    List<Company> getListCompany();
    Company getCompanyById(int companyId);
    Company createCompany(int userId, Company company);
    Company editCompany(int userId, Company company);
    
    List<Payment> getPaidPayment(int companyId);
    List<Payment> getPAIDPayment();
}
