package fpt.aptech.api.service;

import fpt.aptech.api.models.Favorite;
import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.respository.FavorateRepository;
import fpt.aptech.api.respository.PostRepository;
import fpt.aptech.api.respository.ReviewRepository;
import fpt.aptech.api.respository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserPostPutDataPersonalService implements IUserPostPutDataPersonal {

    private final UsersRepository usersRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final FavorateRepository favoriteRepository;

    @Autowired
    public UserPostPutDataPersonalService(UsersRepository usersRepository, PostRepository postRepository, ReviewRepository reviewRepository, FavorateRepository favoriteRepository) {
        this.usersRepository = usersRepository;
        this.postRepository = postRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Users updateUserById(Users user) {
        // Kiểm tra xem data truyền vô có null không
        validateUserNotNull(user);

        //Kiểm tra xem thông tin người dùng có tồn tại trong csdl(database) không
        Users existingUser = usersRepository.findById(user.getId()).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + user.getId()));

        if (existingUser != null) {
            return usersRepository.save(user);
        }
        return null;
    }

    @Override
    public Users deleteUserById(int id) {
        //Kiểm tra xem thông tin người dùng có tồn tại trong csdl(database) không
        Users user = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        // Kiểm tra và xử lý các điều kiện liên quan đến việc xóa người dùng, nếu cần
        usersRepository.deleteById(id);

        return user;
    }

    @Override
    public Post createPost(Post post) {

        validatePostNotNull(post);

        // Kiểm tra và xử lý các điều kiện liên quan đến việc tạo bài viết, nếu cần
        return postRepository.save(post);
    }

    @Override
    public Post updatePostById(Post post) {

        validatePostNotNull(post);

        //Kiểm tra xem thông tin bài viết có tồn tại trong csdl(database) không
        Post existingPost = postRepository.findById(post.getId()).orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + post.getId()));

        // Kiểm tra và xử lý các điều kiện liên quan đến việc cập nhật bài viết, nếu cần
        if (existingPost != null) {
            return postRepository.save(post);
        }
        return null;
    }

    @Override
    public Post deletePostById(int id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + id));

        // Kiểm tra và xử lý các điều kiện liên quan đến việc xóa bài viết, nếu cần
        postRepository.deleteById(id);

        return post;
    }

    @Override
    public Review createReview(Review review) {
        validateReviewNotNull(review);

        // Kiểm tra và xử lý các điều kiện liên quan đến việc tạo đánh giá, nếu cần
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReviewById(Review review) {
        validateReviewNotNull(review);

        //Kiểm tra xem thông tin đánh giá có tồn tại trong csdl(database) không
        Review existingReview = reviewRepository.findById(review.getId()).orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + review.getId()));

        // Kiểm tra và xử lý các điều kiện liên quan đến việc cập nhật đánh giá, nếu cần
        if (existingReview != null) {
            return reviewRepository.save(review);
        }
        return null;
    }

    @Override
    public Review deleteReviewById(int id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

        // Kiểm tra và xử lý các điều kiện liên quan đến việc xóa đánh giá, nếu cần
        reviewRepository.deleteById(id);

        return review;
    }

    @Override
    public Favorite addFavorite(Favorite favorite) {
        validateFavoriteNotNull(favorite);

        // Kiểm tra và xử lý các điều kiện liên quan đến việc thêm vào danh sách yêu thích, nếu cần
        // Kiểm tra xem người dùng đã nhấn vào yêu thích chưa
        boolean isFavoriteExists = checkIfFavoriteExists(favorite.getUserId().getId(), favorite.getTourId().getId());

        if (isFavoriteExists) {
            return null; // Tour đã tồn tại trong danh sách yêu thích
        }

        return favoriteRepository.save(favorite);
    }

    private boolean checkIfFavoriteExists(int userId, int tourId) {
        List<Favorite> favorites = favoriteRepository.findAll();

        for (Favorite favorite : favorites) {
            if (favorite.getTourId().getId().equals(tourId) && favorite.getUserId().getId().equals(userId)) {
                return true; // Tour đã tồn tại trong danh sách yêu thích
            }
        }

        return false; // Tour chưa tồn tại trong danh sách yêu thích
    }

    @Override
    public Favorite deleteFavoriteById(int id) {
        Favorite favorite = favoriteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Favorite not found with ID: " + id));

        // Kiểm tra và xử lý các điều kiện liên quan đến việc xóa khỏi danh sách yêu thích, nếu cần
        favoriteRepository.deleteById(id);

        return favorite;
    }

    private void validateUserNotNull(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
    }

    private void validatePostNotNull(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
    }

    private void validateReviewNotNull(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
    }

    private void validateFavoriteNotNull(Favorite favorite) {
        if (favorite == null) {
            throw new IllegalArgumentException("Favorite cannot be null");
        }
    }

    @Override
    public boolean changePassword(int id, String oldPassword, String newPassword) {
        Users existingUser = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        if (checkPassword(oldPassword, existingUser.getPassword())) {
            String hashedPassword = hashPassword(newPassword);
            existingUser.setPassword(hashedPassword);
            usersRepository.save(existingUser);
            return true;
        }
        return false;
    }

    @Override
    public Users updateProfile(Users user) {
        return usersRepository.save(user);
    }

    private boolean checkPassword(String password, String hashedPassword) {
        // Kiểm tra mật khẩu đã được mã hóa khớp với mật khẩu gốc
        boolean passwordMatched = BCrypt.checkpw(password, hashedPassword);
        return passwordMatched;
    }

    private String hashPassword(String password) {
        // Thực hiện mã hóa mật khẩu ở đây (ví dụ: sử dụng BCrypt)
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }

    @Override
    public List<Post> findAllPost() {
        return postRepository.findAll();
    }

}
