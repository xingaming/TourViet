/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Company;
import fpt.aptech.portal.entities.Roles;
import fpt.aptech.portal.entities.Tour;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import fpt.aptech.portal.enums.Status;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author admin
 */
@Controller
public class AccountController {

    private final String url = "http://localhost:8888/api/auth/";

    //Lấy User ra
    @RequestMapping("/admin/account/user")
    public String user(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            List<Users> result = getUsersForAdmin(user);
            model.addAttribute("list", result);
            return "admin/account/user";
        }
        return "/admin/error403";
    }

    //Lấy Staff ra
    @RequestMapping("/admin/account/staff")
    public String staff(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false && user.getRoleId().getId() != Role.SUPER_ADMIN.getValue() && user.getRoleId().getId() != Role.HQ.getValue() && user.getRoleId().getId() != Role.AREA.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            List<Users> result = getUsersForAdmin(user);
            model.addAttribute("list", result);
            model.addAttribute("user", user);
            if (model.containsAttribute("message")) {
                String message = (String) model.asMap().get("message");
                model.addAttribute("message", message);
                return "/admin/account/staff";
            }
            return "admin/account/staff";
        }
        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            List<Users> result = getStaffAreaForHQ(user);
            List<Users> r = getStaffGuideForHQ(user);
            model.addAttribute("list", result);
            model.addAttribute("user", user);
            if (model.containsAttribute("message")) {
                String message = (String) model.asMap().get("message");
                model.addAttribute("message", message);
                return "/admin/account/staff";
            }
            return "admin/account/staff";
        }
        if (user.getRoleId().getId() == Role.AREA.getValue()) {
            List<Users> result = getStaffGuideForArea(user);
            model.addAttribute("list", result);
            model.addAttribute("user", user);
            if (model.containsAttribute("message")) {
                String message = (String) model.asMap().get("message");
                model.addAttribute("message", message);
                return "/admin/account/staff";
            }
            return "admin/account/staff";
        }

        return "/admin/error403";
    }

    //Lấy chi tiết Staff
    @RequestMapping("/admin/account/detail/staff/{userId}")
    public String detailStaff(Model model, HttpSession session, @PathVariable("userId") String userId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false && user.getRoleId().getId() != Role.SUPER_ADMIN.getValue() && user.getRoleId().getId() != Role.AREA.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        int idUser = Integer.parseInt(userId);
        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            Users u = getUserForAdmin(user, idUser);
            model.addAttribute("user", u);
            return "/admin/account/profile-staff";
        }
        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            Users u = getStaffForHQ(user, idUser);
            model.addAttribute("user", u);
            return "/admin/account/profile-staff";
        }
        if (user.getRoleId().getId() == Role.AREA.getValue()) {
            Users u = getStaffForArea(user, idUser);
            model.addAttribute("user", u);
            return "/admin/account/profile-staff";
        }
        return "/admin/error403";
    }

    //Block User
    @RequestMapping("/admin/account/user/block/{userId}")
    public String blockUser(Model model, HttpSession session, @PathVariable("userId") String userId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        int idUser = Integer.parseInt(userId);
        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            blockUsersForAdmin(user, idUser);
            return "redirect:/admin/account/user";
        }
        return "/admin/error403";
    }

    //unBlock User
    @RequestMapping("/admin/account/user/unblock/{userId}")
    public String unBlockUser(Model model, HttpSession session, @PathVariable("userId") String userId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        int idUser = Integer.parseInt(userId);
        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            unBlockUsersForAdmin(user, idUser);
            return "redirect:/admin/account/user";
        }
        return "/admin/error403";
    }

    //Block Staff
    @RequestMapping("/admin/account/staff/block/{userId}")
    public String blockStaff(Model model, HttpSession session, @PathVariable("userId") String userId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false && user.getRoleId().getId() != Role.SUPER_ADMIN.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        int idUser = Integer.parseInt(userId);
        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            blockUsersForAdmin(user, idUser);
            return "redirect:/admin/account/staff";
        }
        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            blockStaffForHQ(user, idUser);
            return "redirect:/admin/account/staff";
        }
        if (user.getRoleId().getId() == Role.AREA.getValue()) {
            blockStaffForArea(user, idUser);
            return "redirect:/admin/account/staff";
        }
        return "/admin/error403";
    }

    //unBlock Staff
    @RequestMapping("/admin/account/staff/unblock/{userId}")
    public String unBlockStaff(Model model, HttpSession session, @PathVariable("userId") String userId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false && user.getRoleId().getId() != Role.SUPER_ADMIN.getValue() && user.getRoleId().getId() != Role.HQ.getValue() && user.getRoleId().getId() != Role.AREA.getValue()) {
            return "/admin/error403";
        }

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        int idUser = Integer.parseInt(userId);
        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            unBlockUsersForAdmin(user, idUser);
            return "redirect:/admin/account/staff";
        }
        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            unBlockStaffForHQ(user, idUser);
            return "redirect:/admin/account/staff";
        }
        if (user.getRoleId().getId() == Role.AREA.getValue()) {
            unBlockStaffForArea(user, idUser);
            return "redirect:/admin/account/staff";
        }
        return "/admin/error403";
    }

    //Create Account
    @RequestMapping("/admin/account/create-account")
    public String createAccount(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }

        Users user = (Users) session.getAttribute("user");
        RestTemplate restTemplate = new RestTemplate();
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            List<Users> result = getUsersForAdmin(user);
            model.addAttribute("list", result);
            List<Company> CResult = getCompanyForAdmin();
            model.addAttribute("clist", CResult);
            model.addAttribute("user", user);
            // Lấy thông báo lỗi nếu có
            if (model.containsAttribute("error")) {
                String message = (String) model.asMap().get("error");
                model.addAttribute("error", message);
                return "/admin/account/create-account";
            }
            return "/admin/account/create-account";
        }

        if (user.getRoleId().getId() != Role.SUPER_ADMIN.getValue() && user.getRoleId().getId() != Role.CUSTOMER.getValue()) {
            List<Company> CResult = getCompanyForAdmin();
            model.addAttribute("clist", CResult);
            model.addAttribute("user", user);
            return "/admin/account/create-account";
        }
        return "/admin/account/create-account";
    }

    @PostMapping("/admin/account/register")
    public String registerStaff(RedirectAttributes redirectAttributes, @RequestParam String email, @RequestParam String fname, @RequestParam String lname, @RequestParam String phone, @RequestParam String pass, @RequestParam String confirm, @RequestParam String company, @RequestParam String role, @RequestParam("images") MultipartFile imageFile, @RequestParam String image, @RequestParam String address, Model model) throws IOException {
        // Create a new RestTemplate instance

        // Xử lý ảnh
        String base64Image = null;
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                base64Image = convertImageToBase64(imageBytes);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                redirectAttributes.addFlashAttribute("error", "An error occurred while processing the image.");
                return "redirect:/admin/account/create-account";

            }
        }
        if (email.isEmpty() || fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty() || (imageFile.isEmpty() && image.isEmpty()) || address.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", " fields are required. ");

            // Chuyển từng thông tin qua RedirectAttributes
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("fname", fname);
            redirectAttributes.addFlashAttribute("lname", lname);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("pass", pass);
            redirectAttributes.addFlashAttribute("confirm", confirm);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("role", role);
            redirectAttributes.addFlashAttribute("image", base64Image);
            redirectAttributes.addFlashAttribute("address", address);
            return "redirect:/admin/account/create-account";
        }

        if (!pass.equals(confirm)) {
            redirectAttributes.addFlashAttribute("error", "Password and confirm password do not match. ");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("fname", fname);
            redirectAttributes.addFlashAttribute("lname", lname);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("pass", pass);
            redirectAttributes.addFlashAttribute("confirm", confirm);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("role", role);
            redirectAttributes.addFlashAttribute("image", base64Image);
            redirectAttributes.addFlashAttribute("address", address);
            return "redirect:/admin/account/create-account";
        }

        Users user = new Users();
        user.setEmail(email);
        user.setFirstName(fname);
        user.setLastName(lname);

        user.setPassword(pass);
        user.setPhone(phone);
        user.setAddress(address);

        Company comp = new Company();
        int compId = Integer.parseInt(company);
        comp.setId(compId);
        user.setCompanyId(comp);

        int roleStaff = Integer.parseInt(role);
        Roles roles = new Roles();

        if (roleStaff == 2) {
            roles.setId(Role.HQ.getValue());
        }
        if (roleStaff == 3) {
            roles.setId(Role.AREA.getValue());
        }
        if (roleStaff == 4) {
            roles.setId(Role.TOUR_GUIDE.getValue());
        }

        user.setRoleId(roles);

        user.setStatus(Status.ACTIVE.getValue());
        if (!user.isEmailValid()) {
            redirectAttributes.addFlashAttribute("emailError", "Invalid email format");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("fname", fname);
            redirectAttributes.addFlashAttribute("lname", lname);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("pass", pass);
            redirectAttributes.addFlashAttribute("confirm", confirm);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("role", role);
            redirectAttributes.addFlashAttribute("image", base64Image);
            redirectAttributes.addFlashAttribute("address", address);
            return "redirect:/admin/account/create-account";
        }

        if (!user.isPhoneValid()) {
            redirectAttributes.addFlashAttribute("phoneError", "Assuming phone numbers are 10-12 digits");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("fname", fname);
            redirectAttributes.addFlashAttribute("lname", lname);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("pass", pass);
            redirectAttributes.addFlashAttribute("confirm", confirm);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("role", role);
            redirectAttributes.addFlashAttribute("image", base64Image);
            redirectAttributes.addFlashAttribute("address", address);
            return "redirect:/admin/account/create-account";
        }

        if (!user.isPasswordValid()) {
            redirectAttributes.addFlashAttribute("passwordError", "Password must be at least 8 characters long.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("fname", fname);
            redirectAttributes.addFlashAttribute("lname", lname);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("pass", pass);
            redirectAttributes.addFlashAttribute("confirm", confirm);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("role", role);
            redirectAttributes.addFlashAttribute("image", base64Image);
            redirectAttributes.addFlashAttribute("address", address);
            return "redirect:/admin/account/create-account";
        }

        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                base64Image = convertImageToBase64(imageBytes);
                user.setAvatar(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                redirectAttributes.addFlashAttribute("error", "An error occurred while processing the image.");
                return "redirect:/admin/account/create-account";

            }
        }

        String registerApiUrl = url + "register";
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Users> response = restTemplate.postForEntity(registerApiUrl, user, Users.class);
            Users newUsers = response.getBody();

            if (newUsers != null) {
                // Registration successful, you can handle the response data here
                redirectAttributes.addFlashAttribute("message", "Registration Successful: ");
                return "redirect:/admin/account/staff"; // Redirect to home page or show success message
            } else {
                // Registration failed, handle the error here

                redirectAttributes.addFlashAttribute("emailError", "Email already exists");
                return "redirect:/admin/account/create-account"; // Redirect back to register page with error message
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            // Handle the unauthorized error here (if needed)
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("fname", fname);
            redirectAttributes.addFlashAttribute("lname", lname);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("pass", pass);
            redirectAttributes.addFlashAttribute("confirm", confirm);
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("role", role);
            redirectAttributes.addFlashAttribute("image", image);
            redirectAttributes.addFlashAttribute("address", address);
            redirectAttributes.addFlashAttribute("emailError", "Email already exists");
            return "redirect:/admin/account/create-account"; // Redirect back to register page with error message
        }
    }

    @RequestMapping("admin/account/profile/{userId}")
    public String profile(Model model, @PathVariable int userId, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        // Declare restTemplate
        if (user == null) {
            return "redirect:/user/login";
        }
        RestTemplate restTemplate = new RestTemplate();
        Users u = getProfileUser(user, userId);
        model.addAttribute("u", u);
        if (model.containsAttribute("error")) {
            String message = (String) model.asMap().get("error");
            model.addAttribute("error", message);
            return "admmin/my-profile";
        }
        return "admin/account/my-profile";

    }

    @PostMapping("admin/profile/account/edit")
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
                return "redirect:/admin/my-profile";

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
            return "redirect:/admin/my-profile";
        }

        if (!user.isLname()) {
            redirectAttributes.addFlashAttribute("error", "Last name cannot be left blank");
            return "redirect:/admin/my-profile";
        }

        if (!user.isPhoneValid()) {
            redirectAttributes.addFlashAttribute("error", "Assuming phone numbers are 10-12 digits");
            return "redirect:/admin/my-profile";
        }

        Users profileUser = updateProfileForUser(user, userId);
        redirectAttributes.addFlashAttribute("message", "Registration Successful: ");
        session.removeAttribute("user");
        session.setAttribute("user", profileUser);
        return "redirect:/admin/dashboard";

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

    private String convertImageToBase64(byte[] imageBytes) {
        // Chuyển đổi mảng byte của hình ảnh thành mã Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
    }

    private List<Users> getUsersForAdmin(Users user) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/{adminId}/accounts";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId())
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Users>>() {
        }
        );
        // Get the list of tours from the response body
        List<Users> users = (List<Users>) response.getBody();
        return users;
    }

    private List<Company> getCompanyForAdmin() {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/companies";
        List<Company> list = restTemplate.getForObject(apiUrl, List.class);
        return list;
    }

    private List<Users> getStaffAreaForHQ(Users user) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/hq/{userId}/areas";
        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId())
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Users>>() {
        }
        );
        // Get the list of tours from the response body
        List<Users> users = (List<Users>) response.getBody();
        return users;
    }

    private List<Users> getStaffGuideForHQ(Users user) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/hq/{userId}/tourguides";
        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId())
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Users>>() {
        }
        );
        // Get the list of tours from the response body
        List<Users> users = (List<Users>) response.getBody();
        return users;
    }

    private List<Users> getStaffGuideForArea(Users user) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/area/{adminId}/tourguides";
        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId())
                .toUriString();

        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Users>>() {
        }
        );
        // Get the list of tours from the response body
        List<Users> users = (List<Users>) response.getBody();
        return users;
    }

    private boolean blockUsersForAdmin(Users user, @RequestParam("idUser") int idUser) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/{adminId}/accounts/block/{idUser}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), idUser)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Boolean> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Boolean.class // Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        boolean result = response.getBody();
        return result;
    }

    private boolean unBlockUsersForAdmin(Users user, @RequestParam("idUser") int idUser) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/{adminId}/accounts/unblock/{idUser}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), idUser)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Boolean> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Boolean.class // Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        boolean result = response.getBody();
        return result;
    }

    private boolean blockStaffForHQ(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/hq/{userId}/block/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), staffId)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Boolean> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Boolean.class // Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        boolean result = response.getBody();
        return result;
    }

    private boolean unBlockStaffForHQ(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/hq/{userId}/unblock/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), staffId)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Boolean> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Boolean.class // Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        boolean result = response.getBody();
        return result;
    }

    private boolean blockStaffForArea(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/area/{adminId}/tourguides/block/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), staffId)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Boolean> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Boolean.class // Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        boolean result = response.getBody();
        return result;
    }

    private boolean unBlockStaffForArea(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/area/{adminId}/tourguides/unblock/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), staffId)
                .toUriString();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<Boolean> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Boolean.class // Sử dụng Boolean.class thay vì Users.class
        );
        // Get the boolean result from the response body
        boolean result = response.getBody();
        return result;
    }

    private Users getStaffForArea(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/area/{adminId}/tourguides/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), staffId)
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

    private Users getStaffForHQ(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/hq/{userId}/details/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId(), staffId)
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

    private Users getUserForAdmin(Users user, @RequestParam("staffId") int staffId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/users/admin/{staffId}";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(staffId)
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
}
