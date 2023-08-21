package fpt.aptech.api.respository;

import fpt.aptech.api.models.Scheduleimage;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ScheduleimageRepository extends JpaRepository<Scheduleimage, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Scheduleimage s WHERE s.scheduleItemId.id = :scheduleItemId")
    void deleteByScheduleItemId(@Param("scheduleItemId") int scheduleItemId);

    @Query("SELECT s FROM Scheduleimage s WHERE s.scheduleItemId.id = :scheduleItemId")
    List<Scheduleimage> findScheduleImagesByScheduleItemId(@Param("scheduleItemId") int scheduleItemId);

}
