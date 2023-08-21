package fpt.aptech.api.respository;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Users;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.userId = :user")
    List<Booking> getBookingsByUser(Users user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Booking b WHERE b.scheduleId.id = :scheduleId")
    void deleteBookingsByScheduleId(@Param("scheduleId") int scheduleId);

    @Query("SELECT b FROM Booking b WHERE b.scheduleId.id = :scheduleId")
    List<Booking> findBookingsByScheduleId(@Param("scheduleId") int scheduleId);

}
