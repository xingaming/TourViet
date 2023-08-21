package fpt.aptech.api.respository;

import fpt.aptech.api.models.News;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NewsRepository extends JpaRepository<News, Integer> {
    
}
