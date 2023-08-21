package fpt.aptech.api.respository;

import fpt.aptech.api.models.Coupon;
import fpt.aptech.api.models.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    @Query("SELECT c FROM Coupon c WHERE c.userId = :user")
    List<Coupon> getCouponByUser(Users user);
}
