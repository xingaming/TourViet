package fpt.aptech.api.respository;

import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Users;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE p.userId = :user")
    List<Post> getPostByUser(Users user);
}
