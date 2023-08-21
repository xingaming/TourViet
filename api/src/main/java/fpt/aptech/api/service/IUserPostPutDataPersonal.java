package fpt.aptech.api.service;

import fpt.aptech.api.models.Favorite;
import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Users;
import java.util.List;


public interface IUserPostPutDataPersonal {
    Users updateUserById(Users user);
    Users deleteUserById(int id);
    Post createPost(Post post);
    Post updatePostById(Post post);
    Post deletePostById(int id);
    List<Post> findAllPost();
    Review createReview(Review review);
    Review updateReviewById(Review review);
    Review deleteReviewById(int id);
    Favorite addFavorite(Favorite favorite);
    Favorite deleteFavoriteById(int id);
    boolean changePassword(int id, String oldPassword, String newPassword);
    Users updateProfile(Users user);
}
