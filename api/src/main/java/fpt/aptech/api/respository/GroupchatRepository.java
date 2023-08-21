package fpt.aptech.api.respository;

import fpt.aptech.api.models.Groupchat;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupchatRepository extends JpaRepository<Groupchat, Integer> {
//    @Query("SELECT g FROM Groupchat g WHERE g.user = :user")
//    List<Groupchat> getGroupChatByUser(Users user);
}
