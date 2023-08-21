package fpt.aptech.api.respository;

import fpt.aptech.api.models.Hotels;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HotelsRepository extends JpaRepository<Hotels, Integer> {
    
}
