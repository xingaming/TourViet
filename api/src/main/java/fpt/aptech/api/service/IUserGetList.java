package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Coupon;
import fpt.aptech.api.models.Favorite;
import fpt.aptech.api.models.Groupchat;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Users;
import java.util.List;

public interface IUserGetList {

    public List<Booking> getBookings(Users user);

    public List<Review> getReviews(Users user);

    public List<Review> getAllReviews();

    public List<Post> getPosts(Users user);

    public List<Payment> getPayments(Users user);

    public List<Payment> getAllPayments();

    public List<Coupon> getCoupons(Users user);

    // Modify by Duc
    public List<Groupchat> getGroupchatByUserId(int userId);

    // Add by Duc
    public List<Favorite> getFavorites(int userId);

    // By Vinh
    public Users getUsers(int id);

    // By Thinh
    public Users getProfileUser(String token);
}
