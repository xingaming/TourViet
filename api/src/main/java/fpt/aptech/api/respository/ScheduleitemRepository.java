package fpt.aptech.api.respository;

import fpt.aptech.api.models.Scheduleitem;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ScheduleitemRepository extends JpaRepository<Scheduleitem, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Scheduleitem s WHERE s.scheduleId.id = :scheduleId")
    void deleteSchedulesItemByScheduleId(@Param("scheduleId") int scheduleId);

    @Query("SELECT s FROM Scheduleitem s WHERE s.scheduleId.id = :scheduleId")
    List<Scheduleitem> findScheduleItemsByScheduleId(@Param("scheduleId") int scheduleId);

}
