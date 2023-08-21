package fpt.aptech.api.respository;

import fpt.aptech.api.models.Invite;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InviteRepository extends JpaRepository<Invite, Integer> {
    
}
