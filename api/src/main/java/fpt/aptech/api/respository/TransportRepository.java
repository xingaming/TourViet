package fpt.aptech.api.respository;

import fpt.aptech.api.models.Transport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransportRepository extends JpaRepository<Transport, Integer> {
    
}
