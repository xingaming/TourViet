package fpt.aptech.api.resource.user;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Coupon;
import fpt.aptech.api.models.Groupchat;
import fpt.aptech.api.models.Payment;
import fpt.aptech.api.models.Post;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.service.IUserGetList;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.models.Favorite;
import fpt.aptech.api.enums.RoleId;
import java.util.function.BiFunction;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/users")
public class UserGetListResource {

    @Autowired
    private IUserGetList service;

    @GetMapping("/{userId}/bookings")
    public ResponseEntity<List<Booking>> getListBookings(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Booking>>
        return (ResponseEntity<List<Booking>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = new Users();
            user.setId(authenticatedUserId);
            List<Booking> bookings = service.getBookings(user);
            return ResponseEntity.ok(bookings);
        });
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<List<Review>> getListReviews(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Review>>
        return (ResponseEntity<List<Review>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = new Users();
            user.setId(authenticatedUserId);
            List<Review> reviews = service.getReviews(user);
            return ResponseEntity.ok(reviews);
        });
    }

    @GetMapping("/allreviews")
    @ResponseStatus(HttpStatus.OK)
    public List<Review> list() {
        return service.getAllReviews();
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<Post>> getListPosts(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Post>>
        return (ResponseEntity<List<Post>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = new Users();
            user.setId(authenticatedUserId);
            List<Post> posts = service.getPosts(user);
            return ResponseEntity.ok(posts);
        });
    }

    @GetMapping("/{userId}/payments")
    public ResponseEntity<List<Payment>> getListPayments(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Payment>>
        return (ResponseEntity<List<Payment>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = new Users();
            user.setId(authenticatedUserId);
            List<Payment> payments = service.getPayments(user);
            return ResponseEntity.ok(payments);
        });
    }

    @GetMapping("/all/payments")
    @ResponseStatus(HttpStatus.OK)
    public List<Payment> getAllListPayments() {
        return service.getAllPayments();
        
    }

    @GetMapping("/{userId}/coupons")
    public ResponseEntity<List<Coupon>> getListCoupons(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Coupon>>
        return (ResponseEntity<List<Coupon>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = new Users();
            user.setId(authenticatedUserId);
            List<Coupon> coupons = service.getCoupons(user);
            return ResponseEntity.ok(coupons);
        });
    }

    @GetMapping("/{userId}/groupchats")
    public ResponseEntity<List<Groupchat>> getUserGroupChats(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Groupchat>>
        return (ResponseEntity<List<Groupchat>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Groupchat> groupChats = service.getGroupchatByUserId(authenticatedUserId);
            if (groupChats != null) {
                return ResponseEntity.ok(groupChats);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserss(@PathVariable int id, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<Users>
        return (ResponseEntity<Users>) getUserData2(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = service.getUsers(authenticatedUserId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @GetMapping("admin/{id}")
    public ResponseEntity<Users> getUsersforAdmin(@PathVariable int id, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<Users>
        return (ResponseEntity<Users>) getUserData2(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = service.getUsers(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }
    
    @GetMapping("/user/{id}")
    public Users getUsser(@PathVariable int id) {
        Users user = service.getUsers(id);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Favorite>> getUserFavorites(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<List<Favorite>>
        return (ResponseEntity<List<Favorite>>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Favorite> favorites = service.getFavorites(authenticatedUserId);
            if (favorites != null) {
                return ResponseEntity.ok(favorites);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @GetMapping("/profile")
    public ResponseEntity<Users> getProfile(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<Users>
        return (ResponseEntity<Users>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            Users user = service.getProfileUser(jwtToken);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    // Phương thức chung để xác thực và truy cập dữ liệu của người dùng
    private ResponseEntity<?> getUserData(String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Cắt bỏ tiền tố "Bearer " từ chuỗi token, gồm 7 ký tự
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId và authenticatedRoleId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dữ liệu
        if (authenticatedRoleId == RoleId.SUPER_ADMIN.getValue() || (authenticatedRoleId == RoleId.CUSTOMER.getValue())) {
            // Gọi hành động cụ thể được truyền vào dưới dạng BiFunction
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            // Trả về mã trạng thái UNAUTHORIZED nếu không có quyền truy cập
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Phương thức chung để xác thực và truy cập dữ liệu của người dùng
    private ResponseEntity<?> getUserData2(String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Cắt bỏ tiền tố "Bearer " từ chuỗi token, gồm 7 ký tự
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId và authenticatedRoleId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dữ liệu
        if (authenticatedRoleId == RoleId.SUPER_ADMIN.getValue() || authenticatedRoleId == RoleId.HQ.getValue() || authenticatedRoleId == RoleId.AREA.getValue() || authenticatedRoleId == RoleId.TOUR_GUIDE.getValue() || (authenticatedRoleId == RoleId.CUSTOMER.getValue())) {
            // Gọi hành động cụ thể được truyền vào dưới dạng BiFunction
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            // Trả về mã trạng thái UNAUTHORIZED nếu không có quyền truy cập
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
