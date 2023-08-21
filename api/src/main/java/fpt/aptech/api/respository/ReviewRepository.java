package fpt.aptech.api.respository;

import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Users;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.userId = :user")
    List<Review> getReviewByUser(Users user);
    
    

    @Query("SELECT r FROM Review r WHERE r.tourId.id = :tourId")
    List<Review> getReviewsByTour(@Param("tourId") int tourId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Review r WHERE r.tourId.id = :tourId")
    void deleteReviewsBySchedule(@Param("tourId") int tourId);

}
