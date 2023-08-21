package fpt.aptech.api.respository;

import fpt.aptech.api.models.Serviceitem;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ServiceitemRepository extends JpaRepository<Serviceitem, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Serviceitem s WHERE s.scheduleId.id = :scheduleId")
    void deleteServiceItemByScheduleId(@Param("scheduleId") int scheduleId);
    
    @Query("SELECT s FROM Serviceitem s WHERE s.scheduleId.id = :scheduleId")
    List<Serviceitem> findServiceItemByScheduleId(@Param("scheduleId") int scheduleId);
}
