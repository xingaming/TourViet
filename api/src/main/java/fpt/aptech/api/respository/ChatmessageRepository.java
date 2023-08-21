package fpt.aptech.api.respository;

import fpt.aptech.api.models.Chatmessage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatmessageRepository extends JpaRepository<Chatmessage, Integer> {
    
}
