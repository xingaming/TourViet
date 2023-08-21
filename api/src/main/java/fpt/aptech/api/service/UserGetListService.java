package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Coupon;
import fpt.aptech.api.models.Favorite;
import fpt.aptech.api.models.Groupchat;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Usergroup;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.respository.BookingRepository;
import fpt.aptech.api.respository.CouponRepository;
import fpt.aptech.api.respository.FavorateRepository;
import fpt.aptech.api.respository.PaymentRepository;
import fpt.aptech.api.respository.PostRepository;
import fpt.aptech.api.respository.ReviewRepository;
import fpt.aptech.api.respository.UsergroupRepository;
import fpt.aptech.api.respository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserGetListService implements IUserGetList {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final PaymentRepository paymentRepository;
    private final CouponRepository couponRepository;
    private final UsergroupRepository usergroupRepository;
    private final UsersRepository usersRepository;
    private final FavorateRepository favoriteRepository;

    @Autowired
    public UserGetListService(BookingRepository bookingRepository, ReviewRepository reviewRepository, PostRepository postRepository, PaymentRepository paymentRepository, CouponRepository couponRepository, UsergroupRepository usergroupRepository, UsersRepository usersRepository, fpt.aptech.api.respository.FavorateRepository favoriteRepository) {
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.postRepository = postRepository;
        this.paymentRepository = paymentRepository;
        this.couponRepository = couponRepository;
        this.usergroupRepository = usergroupRepository;
        this.usersRepository = usersRepository;
        this.favoriteRepository = favoriteRepository;
    }
    
    @Override
    public Users getProfileUser(String token) {
        return usersRepository.findByToken(token);
    }

    @Override
    public List<Booking> getBookings(Users user) {
        return bookingRepository.getBookingsByUser(user);
    }

    @Override
    public List<Review> getReviews(Users user) {
        return reviewRepository.getReviewByUser(user);
    }

    @Override
    public List<Post> getPosts(Users user) {
        return postRepository.getPostByUser(user);
    }

    @Override
    public List<Payment> getPayments(Users user) {
        return paymentRepository.getPaymentByUser(user);
    }
    
    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Coupon> getCoupons(Users user) {
        return couponRepository.getCouponByUser(user);
    }

    @Override
    public List<Groupchat> getGroupchatByUserId(int userId) {
        List<Usergroup> userGroups = usergroupRepository.findByUserId(userId);

        List<Groupchat> groupChats = new ArrayList<>();
        for (Usergroup userGroup : userGroups) {
            groupChats.add(userGroup.getGroupchatId());
        }

        return groupChats;
    }

    @Override
    public Users getUsers(int id) {
        return usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not Found Id: " + id));
    }

    @Override
    public List<Favorite> getFavorites(int userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    public List<Review> getAllReviews() {
        return  reviewRepository.findAll();
    }
}
