package fpt.aptech.api.respository;

import fpt.aptech.api.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<Item, Integer> {
    
}
