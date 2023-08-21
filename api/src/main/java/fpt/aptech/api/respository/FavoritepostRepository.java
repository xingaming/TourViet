package fpt.aptech.api.respository;

import fpt.aptech.api.models.Favoritepost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FavoritepostRepository extends JpaRepository<Favoritepost, Integer> {
    
}
