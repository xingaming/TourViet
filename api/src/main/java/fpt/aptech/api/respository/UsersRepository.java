package fpt.aptech.api.respository;

import fpt.aptech.api.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByEmail(String email);
    Users findByToken(String token);
}
