package fpt.aptech.api.respository;

import fpt.aptech.api.models.Tour;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    @Query("SELECT t FROM Tour t WHERE t.regionId.id = :regionId")
    List<Tour> getTourByRegion(@Param("regionId") int regionId);

    // Truy vấn danh sách các tour dựa trên startDate và endDate của thuộc tídulenh schedule
    @Query("SELECT t FROM Tour t JOIN t.scheduleList s WHERE s.startDate BETWEEN :startDate AND :endDate")
    List<Tour> findByScheduleStartDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
