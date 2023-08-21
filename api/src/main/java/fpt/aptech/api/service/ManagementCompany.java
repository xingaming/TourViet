package fpt.aptech.api.service;

import fpt.aptech.api.enums.PaymentStatus;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Company;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.respository.CompanyRepository;
import fpt.aptech.api.respository.PaymentRepository;
import fpt.aptech.api.respository.UsersRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementCompany implements IManagementCompany {

    private final CompanyRepository companyRepository;
    private final UsersRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Autowired
    public ManagementCompany(CompanyRepository companyRepository, UsersRepository userRepository, PaymentRepository paymentRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Company> getListCompany() {
        return companyRepository.findAll();
    }

    @Override
    public Company getCompanyById(int companyId) {
        return companyRepository.findById(companyId).get();
    }

    @Override
    public Company editCompany(int roleId, Company company) {
        if (!(roleId == RoleId.SUPER_ADMIN.getValue())) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }

        companyRepository.save(company);

        return company;
    }

    @Override
    public List<Payment> getPaidPayment(int companyId) {
        try {
            return paymentRepository.getPaymentPAIDByCompanyId(companyId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Payment> getPAIDPayment() {
        List<Payment> allPayments = paymentRepository.findAll();
        List<Payment> paidPayments = allPayments.stream()
                        .filter(payment -> payment.getStatus() == PaymentStatus.PAID.getValue())
                        .collect(Collectors.toList());

        return paidPayments;
    }

    @Override
    public Company createCompany(int roleId, Company company) {
        if (!(roleId == RoleId.SUPER_ADMIN.getValue())) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }

        companyRepository.save(company);
        return company;
    }

}
