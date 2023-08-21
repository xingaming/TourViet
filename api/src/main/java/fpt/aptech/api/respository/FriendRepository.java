package fpt.aptech.api.respository;

import fpt.aptech.api.models.Friend;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FriendRepository extends JpaRepository<Friend, Integer> {
    
}
