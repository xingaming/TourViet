package fpt.aptech.api.respository;

import fpt.aptech.api.models.Region;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RegionRepository extends JpaRepository<Region, Integer> {
    
}
