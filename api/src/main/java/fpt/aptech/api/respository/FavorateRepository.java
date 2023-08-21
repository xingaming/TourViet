package fpt.aptech.api.respository;

import fpt.aptech.api.models.Favorite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FavorateRepository extends JpaRepository<Favorite, Integer> {
    @Query("SELECT f FROM Favorite f WHERE f.userId.id = :userId")
    List<Favorite> findByUserId(@Param("userId") int userId);
}
