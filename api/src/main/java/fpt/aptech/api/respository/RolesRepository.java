package fpt.aptech.api.respository;

import fpt.aptech.api.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RolesRepository extends JpaRepository<Roles, Integer> {
    
}
