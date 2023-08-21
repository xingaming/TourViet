package fpt.aptech.api.resource.user;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.MoMoPaymentResponse;
import fpt.aptech.api.service.IBooking;
import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/payment")
public class BookingResource {

    private final IBooking bookingService;

    @Autowired
    public BookingResource(IBooking bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create-booking-v1/{userId}")
    public ResponseEntity<MoMoPaymentResponse> createBookingTestv1(@RequestBody Map<String, Object> requestBody, @PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<MoMoPaymentResponse>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            MoMoPaymentResponse responsePayment = bookingService.createBooking(requestBody, authenticatedUserId);
            if (responsePayment != null) {
                return ResponseEntity.ok(responsePayment);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        });
    }

    @PostMapping("/create-booking-v2/{userId}")
    public ResponseEntity<MoMoPaymentResponse> createBookingTestv2(@RequestBody Map<String, Object> requestBody, @PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<MoMoPaymentResponse>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            MoMoPaymentResponse responsePayment = bookingService.createBookingV2(requestBody, authenticatedUserId);
            if (responsePayment != null) {
                return ResponseEntity.ok(responsePayment);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        });
    }

    @PostMapping("/cash-payment/{userId}")
    public ResponseEntity<Booking> createBookingCashPayment(@PathVariable int userId, @RequestBody Map<String, Object> requestBody, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Booking>) getUserData(token, (authenticatedUserId, authenticatedRoleId) -> {
            try {
                Booking createdBooking = bookingService.createBookingCashPayment(requestBody, authenticatedUserId);
                return ResponseEntity.ok(createdBooking);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    @PostMapping("/ipn")
    public ResponseEntity<String> handleMoMoIPN(@RequestBody MoMoPaymentResponse paymentResponse) {
        bookingService.handleMoMoIPN(paymentResponse);
        return ResponseEntity.ok("SUCCESS");
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

}
