package fpt.aptech.api.resource.user;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.models.Favorite;
import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.service.IUserPostPutDataPersonal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.function.BiFunction;

@RestController
@RequestMapping("/api/users")
public class UserPostPutDataPersonalResource {

    @Autowired
    private IUserPostPutDataPersonal service;

    @PutMapping("/{userId}")
    public ResponseEntity<Users> updateUserById(@PathVariable int userId, @RequestHeader("Authorization") String token, @RequestBody Users user) {
        return (ResponseEntity<Users>) getUserDataByUserAndAdmin(token, (authenticatedUserId, authenticatedRoleId) -> {
            user.setId(authenticatedUserId);
            Users updatedUser = service.updateUserById(user);
            if (updatedUser != null) {
                return ResponseEntity.ok(updatedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @PutMapping("/user/{userId}")
    public Users saveUser(@RequestBody Users user) {
        Users updatedUser = service.updateUserById(user);
        if (updatedUser != null) {
            return updatedUser;
        } else {
            return null;
        }
    }

    @PutMapping("/change-password/{id}")
    public boolean changePassword(@PathVariable int id, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return service.changePassword(id, oldPassword, newPassword);
    }

    @PutMapping("/update-profile")
    public Users updateProfile(@RequestBody Users user) {
        return service.updateProfile(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Users> deleteUserById(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Users>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users deletedUser = service.deleteUserById(authenticatedUserId);
            if (deletedUser != null) {
                return ResponseEntity.ok(deletedUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @GetMapping("/posts")
    public List<Post> getPost() {
        return service.findAllPost();
    }

    @PostMapping("/posts")
    public Post createPost(@RequestBody Post post) {
        return service.createPost(post);
    }

    @PutMapping("/posts")
    public Post updatePostById(@RequestBody Post post) {
        return service.updatePostById(post);
    }

    @DeleteMapping("/posts/{id}")
    public Post deletePostById(@PathVariable int id) {
        return service.deletePostById(id);
    }

    @PostMapping("/{userId}/reviews")
    public ResponseEntity<Review> createReview(@PathVariable int userId, @RequestHeader("Authorization") String token, @RequestBody Review review) {
        return (ResponseEntity<Review>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users u = new Users();
            u.setId(authenticatedUserId);
            review.setUserId(u);
            Review createdReview = service.createReview(review);
            if (createdReview != null) {
                return ResponseEntity.ok(createdReview);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        });
    }

    @PutMapping("/{userId}/reviews/{reviewId}")
    public ResponseEntity<Review> updateReviewById(@PathVariable int userId, @PathVariable int reviewId, @RequestHeader("Authorization") String token, @RequestBody Review review) {
        return (ResponseEntity<Review>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users u = new Users();
            u.setId(authenticatedUserId);
            review.setUserId(u);
            Review updatedReview = service.updateReviewById(review);
            if (updatedReview != null) {
                return ResponseEntity.ok(updatedReview);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @DeleteMapping("/{userId}/reviews/{reviewId}")
    public ResponseEntity<Review> deleteReviewById(@PathVariable int userId, @PathVariable int reviewId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Review>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Review deletedReview = service.deleteReviewById(reviewId);
            if (deletedReview != null) {
                return ResponseEntity.ok(deletedReview);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @PostMapping("/{userId}/favorites")
    public ResponseEntity<Favorite> addFavorite(@PathVariable int userId, @RequestHeader("Authorization") String token, @RequestBody Favorite favorite) {
        return (ResponseEntity<Favorite>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users u = new Users();
            u.setId(authenticatedUserId);
            favorite.setUserId(u);
            Favorite addedFavorite = service.addFavorite(favorite);
            if (addedFavorite != null) {
                return ResponseEntity.ok(addedFavorite);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        });
    }

    @DeleteMapping("/{userId}/favorites/{favoriteId}")
    public ResponseEntity<Favorite> deleteFavoriteById(@PathVariable int userId, @PathVariable int favoriteId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Favorite>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Favorite deletedFavorite = service.deleteFavoriteById(favoriteId);
            if (deletedFavorite != null) {
                return ResponseEntity.ok(deletedFavorite);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    private ResponseEntity<?> getUserData(String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Lấy phần JWT token bằng cách loại bỏ tiền tố "Bearer "
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        // Lấy authenticatedRoleId từ JWT
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dựa trên authenticatedRoleId và authenticatedUserId
        if (authenticatedRoleId == RoleId.CUSTOMER.getValue()) {
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseEntity<?> getUserDataByUserAndAdmin(String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Lấy phần JWT token bằng cách loại bỏ tiền tố "Bearer "
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        // Lấy authenticatedRoleId từ JWT
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dựa trên authenticatedRoleId và authenticatedUserId
        return action.apply(authenticatedUserId, authenticatedRoleId);
    }
}
