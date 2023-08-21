/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.portal.Controller.user;

import fpt.aptech.portal.entities.Booking;
import fpt.aptech.portal.entities.Payment;
import fpt.aptech.portal.entities.Region;
import fpt.aptech.portal.entities.Review;
import fpt.aptech.portal.entities.Roles;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author admin
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final String urlTour = "http://localhost:8888/api/tours/";

    @RequestMapping("/account/{userId}")
    public String profile(Model model, @PathVariable int userId, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        // Declare restTemplate
        if (user == null) {
            return "redirect:/user/login";
        }
        RestTemplate restTemplate = new RestTemplate();

        List<Review> resultR = getReviewForProfile(user, userId);
        model.addAttribute("listReview", resultR);
//        List<Booking> resultB = getBookingForProfile(user, userId);
//        model.addAttribute("listBooking", resultB);
//        Users u = getProfileUser(user, userId);
//        model.addAttribute("u", u);

        List<Payment> resultB = getPaymentForProfile(user, userId);
        model.addAttribute("listPayment", resultB);
        Users u = getProfileUser(user, userId);
        model.addAttribute("u", u);

        if (model.containsAttribute("error")) {
            String message = (String) model.asMap().get("error");
            model.addAttribute("error", message);
            return "user/profile";
        }

        List<Region> result = restTemplate.getForObject(urlTour + "region", List.class);
        model.addAttribute("listRegion", result);
        return "user/profile";

    }

    @PostMapping("/account/edit")
    public String updateProfileUser(RedirectAttributes redirectAttributes, HttpSession session, @RequestParam("images") MultipartFile imageFile, @RequestParam String proId, @RequestParam String fname, @RequestParam String lname, @RequestParam String phone, @RequestParam String address, Model model) {
        // Create a new RestTemplate instance
        Users u = (Users) session.getAttribute("user");

        if (fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "All fields are required. ");
            return "redirect:/profile/account/" + proId;
        }
        int userId = Integer.parseInt(proId);
        Users user = new Users();
        user.setEmail(u.getEmail());
        user.setFirstName(fname);
        user.setLastName(lname);
        user.setPassword(u.getPassword());
        user.setPhone(phone);
        user.setAddress(address);
        user.setRoleId(u.getRoleId());
        user.setCoin(u.getCoin());
        user.setToken(u.getToken());
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                user.setAvatar(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while processing the image.");
                return "redirect:/user/profile";

            }
        }
        if (imageFile.isEmpty()) {
            user.setAvatar(u.getAvatar());
        }
//        user.setAvatar(u.getAvatar());
        user.setStatus(u.getStatus());
        user.setTotalInvite(u.getTotalInvite());
        user.setVerifyCode(u.getVerifyCode());

        if (!user.isFname()) {
            redirectAttributes.addFlashAttribute("error", "First name cannot be left blank");
            return "redirect:/user/profile";

        }

        if (!user.isLname()) {
            redirectAttributes.addFlashAttribute("error", "Last name cannot be left blank");
            return "redirect:/user/profile";

        }

        if (!user.isPhoneValid()) {
            redirectAttributes.addFlashAttribute("error", "Assuming phone numbers are 10-12 digits");
            return "redirect:/user/profile";

        }

        Users profileUser = updateProfileForUser(user, userId);
        redirectAttributes.addFlashAttribute("message", "Registration Successful: ");
        session.removeAttribute("user");
        session.setAttribute("user", profileUser);
        return "redirect:/user/home";

    }

    private String convertImageToBase64(byte[] imageBytes) {
        // Chuyển đổi mảng byte của hình ảnh thành mã Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private List<Review> getReviewForProfile(Users user, @RequestParam("userId") int userId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUser = "http://localhost:8888/api/users/{userId}/reviews";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUser)
                .buildAndExpand(user.getId(), userId)
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Review>>() {
        }
        );
        // Get the list of tours from the response body
        List<Review> reviews = (List<Review>) response.getBody();
        return reviews;
    }

    private List<Payment> getPaymentForProfile(Users user, @RequestParam("userId") int userId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUser = "http://localhost:8888/api/users/{userId}/payments";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUser)
                .buildAndExpand(user.getId(), userId)
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Payment>>() {
        }
        );
        // Get the list of tours from the response body
        List<Payment> payments = (List<Payment>) response.getBody();
        return payments;
    }

    private List<Booking> getBookingForProfile(Users user, @RequestParam("userId") int userId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUser = "http://localhost:8888/api/users/{userId}/bookings";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUser)
                .buildAndExpand(user.getId(), userId)
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Booking>>() {
        }
        );
        // Get the list of tours from the response body
        List<Booking> booking = (List<Booking>) response.getBody();
        return booking;
    }

    private Users getProfileUser(Users user, @RequestParam("userId") int userId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/users/{userId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), userId)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Users> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Users>() {
        }// Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        Users users = (Users) response.getBody();
        return users;
    }

    private Users updateProfileForUser(Users user, @RequestParam("userId") int userId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/users/{userId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(userId) // Chỉ cần truyền userId vào buildAndExpand
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
//        headers.setContentType(MediaType.APPLICATION_JSON);
        // Make the API call using the complete URL, the Authorization header, and the user object as the request body
        ResponseEntity<Users> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(user, headers), // Đưa user vào trong HttpEntity
                Users.class // Loại dữ liệu trả về của API
        );
        // Get the Users object from the response body
        Users result = response.getBody();
        return result;
    }

}
