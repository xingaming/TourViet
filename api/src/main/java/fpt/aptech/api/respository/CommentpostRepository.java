package fpt.aptech.api.respository;

import fpt.aptech.api.models.Commentpost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentpostRepository extends JpaRepository<Commentpost, Integer> {
    
}
