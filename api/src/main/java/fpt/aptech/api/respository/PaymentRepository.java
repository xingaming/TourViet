package fpt.aptech.api.respository;

import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.Users;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT p FROM Payment p WHERE p.userId = :user")
    List<Payment> getPaymentByUser(Users user);

    @Query("SELECT p FROM Payment p WHERE p.bookingId.id = :bookingId")
    Payment findByBookingId(Integer bookingId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Payment p WHERE p.bookingId.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Integer bookingId);
    
    @Query("SELECT p FROM Payment p WHERE p.bookingId.scheduleId.tourId.companyId.id = :companyId AND p.status = 1")
    List<Payment> getPaymentPAIDByCompanyId(Integer companyId);
}
