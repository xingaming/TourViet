package fpt.aptech.api.respository;

import fpt.aptech.api.models.Usergroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UsergroupRepository extends JpaRepository<Usergroup, Integer> {
//    @Query("SELECT u.groupchatId FROM Usergroup u WHERE u.groupchatId.id = :groupchatId")
//    Groupchat findGroupchatByGroupchatId(int groupchatId);
    
    @Query("SELECT u FROM Usergroup u WHERE u.userId.id = :userId")
    List<Usergroup> findByUserId(@Param("userId") int userId);
}
