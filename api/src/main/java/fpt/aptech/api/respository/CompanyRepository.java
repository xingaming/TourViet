package fpt.aptech.api.respository;

import fpt.aptech.api.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompanyRepository extends JpaRepository<Company, Integer> {
    
}
