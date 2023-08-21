package fpt.aptech.api.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.Status;
import fpt.aptech.api.models.Roles;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.respository.UsersRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;


@Service
public class AuthService implements IAuth {

    private static final String GOOGLE_CLIENT_ID = "750323481995-hjpdf5uaji32ig9gpmhqleufegfbok2p.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8888/api/auth/google/callback/authorizationCode";

    //private final ClientRegistrationRepository clientRegistrationRepository;
    private final UsersRepository userRepository;

    @Autowired
    public AuthService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Users register(Users user) {
        // Bị trùng email thì trả về null
        if (existsByEmail(user.getEmail())) {
            return null;
        }

        //mã hóa mật khẩu
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
//        user.setVerifyCode(generateVerifyCode());

        // Lưu người dùng vào cơ sở dữ liệu
        return userRepository.save(user);
    }
    
    @Override
    public boolean checkVerifyCode(int userId, String verifyCode) {
        Users user = getUserById(userId);
        if(user.getVerifyCode().equals(verifyCode)){
            return true;
        }
        return false;
    }

    // Login thường
    @Override
    public Users login(String email, String password) {
        Users user = userRepository.findByEmail(email);
        // Login failed thì trả dữ liệu về null
        if (user == null || !checkPassword(password, user.getPassword())) {
            return null;
        }

        // Tạo token
        String token = TokenUtil.generateToken(user.getId(), user.getRoleId().getId());
        // Lưu token vào bảng người dùng
        saveToken(user.getId(), token);
        return user;
    }

    // Login with google
    @Override
    public Users loginWithGoogle(String authorizationCode) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            GsonFactory jsonFactory = new GsonFactory();

            // Xác minh authorizationCode và lấy token từ Google
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    transport, jsonFactory, GOOGLE_CLIENT_ID, CLIENT_SECRET, authorizationCode, REDIRECT_URI)
                    .execute();

            // Lấy thông tin người dùng từ tokenResponse
            String email = tokenResponse.parseIdToken().getPayload().getEmail();
            String name = (String) tokenResponse.parseIdToken().getPayload().get("name");
            String phone = (String) tokenResponse.parseIdToken().getPayload().get("phone");
            String address = (String) tokenResponse.parseIdToken().getPayload().get("address");

            // Kiểm tra xem người dùng có tồn tại trong hệ thống không
            Users user = userRepository.findByEmail(email);
            if (user == null) {
                // Người dùng không tồn tại, thực hiện đăng ký mới
                user = new Users();
                user.setEmail(email);
                user.setFirstName(name);
                user.setPhone(phone);
                user.setAddress(address);

                Roles role = new Roles();
                role.setId(5);
                user.setRoleId(role);
                
                user.setStatus(Status.ACTIVE.getValue());

                // Lưu người dùng vào cơ sở dữ liệu
                user = userRepository.save(user);
            }

            // Tạo token và lưu vào bảng người dùng
            // Tạo token
            String token = TokenUtil.generateToken(user.getId(), user.getRoleId().getId());
            saveToken(user.getId(), token);

            return user;
        } catch (IOException e) {
            return null;
        }
    }

    public Users getUserById(int userId) {
        Optional<Users> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }

    //Kiểm tra trùng email
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // phương thức để mã hóa mật khẩu
    private String hashPassword(String password) {
        // Thực hiện mã hóa mật khẩu ở đây (ví dụ: sử dụng BCrypt)
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }

    //phương thức để kiểm tra mật khẩu
    private boolean checkPassword(String password, String hashedPassword) {
        // Kiểm tra mật khẩu đã được mã hóa khớp với mật khẩu gốc
        boolean passwordMatched = BCrypt.checkpw(password, hashedPassword);
        return passwordMatched;
    }

    //để lưu lại token
    public void saveToken(int userId, String token) {
        Users user = getUserById(userId);
        if (user != null) {
            user.setToken(token);
            userRepository.save(user);
        }
    }
    
    // Hàm tạo Verify Code ngẫu nhiên gồm 4 ký tự [a-z0-9]
    public static String generateVerifyCode() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder verifyCode = new StringBuilder();

        // Sử dụng lớp Random để tạo ngẫu nhiên các vị trí trong chuỗi characters
        Random random = new Random();

        // Lặp 4 lần để tạo Verify Code có 4 ký tự
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(characters.length());
            verifyCode.append(characters.charAt(index));
        }

        return verifyCode.toString();
    }

    


}
