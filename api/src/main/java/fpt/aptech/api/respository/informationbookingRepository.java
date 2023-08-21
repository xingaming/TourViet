package fpt.aptech.api.respository;

import fpt.aptech.api.models.Informationbooking;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface informationbookingRepository extends JpaRepository<Informationbooking, Integer> {
    
    @Query("SELECT i FROM Informationbooking i WHERE i.bookingId.id = :bookingId")
    List<Informationbooking> getInformationBookingByBookingId(@Param("bookingId") Integer bookingId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Informationbooking i WHERE i.bookingId.id = :bookingId")
    void deleteIn4BookingByBookingId(@Param("bookingId") Integer bookingId);

    

}
