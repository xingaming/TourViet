package fpt.aptech.api.resource.user;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.service.AuthService;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        Users newUser = authService.register(user);
        if(newUser == null ){
           String errorMessage = "Email already exists";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        Users user = authService.login(email, password);
        if (user != null) {
            // Lấy token
            String token = user.getToken();
            // Gắn token vào header của phản hồi
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            // Trả về phản hồi với token gắn trong header
            return ResponseEntity.ok().headers(headers).body(user);
        } else {
            // Authentication failed
            String errorMessage = "Invalid email or password";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestParam String authorizationCode) {

        Users user = authService.loginWithGoogle(authorizationCode);
        if (user != null) {
            // Lấy token
            String token = user.getToken();
            // Gắn token vào header của phản hồi
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);

            // Trả về phản hồi với token gắn trong header
            return ResponseEntity.ok().headers(headers).body(user);
        }
        return null;
    }

    @GetMapping("/google/callback/authorizationCode")
    public ResponseEntity<?> getAuthorizationCode(@RequestParam("code") String authorizationCode) {
        String json = "{\"authorizationCode\": \"" + authorizationCode + "\"}";
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(json);
    }
    
    // example: http://localhost:8888/api/auth/user/5?verifyCode=QRST
    @GetMapping("/user/{id}")
    public ResponseEntity<?> checkVerifyCode(@PathVariable int id, @RequestParam("verifyCode") String verifyCode, @RequestHeader("Authorization") String token) {
        // Gọi phương thức chung getUserData và ép kiểu kết quả về ResponseEntity<Users>
        return (ResponseEntity<Users>) getUserData(id, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean checkVerifyCode = authService.checkVerifyCode(id, verifyCode);
            if (checkVerifyCode == true) {
                return ResponseEntity.ok(checkVerifyCode);
            } else {
                return ResponseEntity.ok(checkVerifyCode);
            }
        });
    }
    
    // Phương thức chung để xác thực và truy cập dữ liệu của người dùng
    private ResponseEntity<?> getUserData(int userId, String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Cắt bỏ tiền tố "Bearer " từ chuỗi token, gồm 7 ký tự
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId và authenticatedRoleId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dữ liệu
        if (authenticatedRoleId == RoleId.CUSTOMER.getValue() && authenticatedUserId == userId) {
            // Gọi hành động cụ thể được truyền vào dưới dạng BiFunction
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            // Trả về mã trạng thái UNAUTHORIZED nếu không có quyền truy cập
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
