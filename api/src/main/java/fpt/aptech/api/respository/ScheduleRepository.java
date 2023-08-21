package fpt.aptech.api.respository;

import fpt.aptech.api.models.Schedule;
import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.tourId.id = :tourId")
    void deleteSchedulesByTour(@Param("tourId") int tourId);
    
    @Query("SELECT s FROM Schedule s WHERE s.tourId.id = :tourId")
    List<Schedule> findSchedulesByTourId(@Param("tourId") int tourId);
    
    @Query("SELECT s FROM Schedule s WHERE s.id = :id")
    Schedule findSchedulesById(@Param("id") int id);
    
    @Transactional
    @Modifying
    @Query("SELECT s FROM Schedule s WHERE s.startDate BETWEEN :startDate AND :endDate")
    List<Schedule> findSchedulesByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    
    @Query("SELECT s FROM Schedule s")
    List<Schedule> findAllSchedules();

}
