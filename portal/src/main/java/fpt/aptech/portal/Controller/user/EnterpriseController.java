package fpt.aptech.portal.Controller.user;

import fpt.aptech.portal.entities.Company;
import fpt.aptech.portal.entities.Region;
import fpt.aptech.portal.entities.Roles;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.entities.UsersAndCompanyDTO;
import fpt.aptech.portal.enums.Role;
import fpt.aptech.portal.enums.Status;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class EnterpriseController {

    @RequestMapping("/enterprise")
    public String enterprise(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UsersAndCompanyDTO userAndCompanyDTO = new UsersAndCompanyDTO();
        model.addAttribute("userAndCompanyDTO", userAndCompanyDTO);
        if (session.getAttribute("userAndCompanyDTO") != null) {
            model.addAttribute("userAndCompanyDTO", session.getAttribute("userAndCompanyDTO"));
            session.removeAttribute("userAndCompanyDTO");
        }
        return "user/enterprise";
    }

    @RequestMapping(value = "/registerBussiness", method = RequestMethod.POST)
    public String registerBussiness(Model model, HttpSession session, @ModelAttribute("userAndCompanyDTO") UsersAndCompanyDTO userAndCompanyDTO, @RequestParam("confirm") String confirm, RedirectAttributes redirectAttributes) {
        Users user = userAndCompanyDTO.getUser();
        Company company = userAndCompanyDTO.getCompany();
        // Check field blank
        if (user.getEmail().trim().isEmpty() || user.getPassword().trim().isEmpty() || user.getPhone().trim().isEmpty() || company.getName().trim().isEmpty() || company.getAddress().trim().isEmpty() || confirm.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank");
            session.setAttribute("userAndCompanyDTO", userAndCompanyDTO);
            return "redirect:/user/enterprise";
        }

        // Check email format
        String emailFormat = user.getEmail();
        if (!isValidEmail(emailFormat)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email format");
            session.setAttribute("userAndCompanyDTO", userAndCompanyDTO);
            return "redirect:/user/enterprise";
        }

        // Check phone number format
        String phoneNumber = user.getPhone();
        if (!isValidPhoneNumber(phoneNumber)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid phone number format");
            session.setAttribute("userAndCompanyDTO", userAndCompanyDTO);
            return "redirect:/user/enterprise";
        }

        // Check password and password confirm match
        String password = user.getPassword();
        String passwordConfirm = confirm;
        if (!password.equals(passwordConfirm)) {
            redirectAttributes.addFlashAttribute("messageError", "Passwords do not match");
            session.setAttribute("userAndCompanyDTO", userAndCompanyDTO);
            return "redirect:/user/enterprise";
        }

        // Tạo yêu cầu để xác thực
        Company createdCompany = createCompany(company);
        // Check
        if (createdCompany == null) {
            redirectAttributes.addFlashAttribute("messageError", "Email already exists");
            session.setAttribute("userAndCompanyDTO", userAndCompanyDTO);
            return "redirect:/user/enterprise";
        }
        user.setCompanyId(createdCompany);
        String email = user.getEmail();
        // Tách tên từ địa chỉ email
        String[] parts = email.split("@"); // Tách thành mảng 2 phần tử [tên, domain]
        String name = parts[0]; // Lấy phần tên (phần tử đầu tiên trong mảng)
        // Tách phần tên thành firstName và lastName
        String[] nameParts = name.split("\\."); // Tách thành mảng các phần tên [firstName, lastName]
        String firstName = nameParts[0]; // Lấy phần tên đầu tiên trong mảng
        String lastName = nameParts.length > 1 ? nameParts[1] : ""; // Lấy phần tên thứ hai nếu có
        // Gán firstName và lastName vào đối tượng user
        user.setFirstName(firstName);
        user.setLastName(lastName);
        createUser(user);

        redirectAttributes.addFlashAttribute("message", "Request has been submitted successfully, normally will be responded in 2 days.");
        return "redirect:/user/enterprise";
    }

    private Users createUser(Users user) {
        String apiUrl = "http://localhost:8888/api/auth/register";

        // Set giá trị cho user
        Roles role = new Roles();
        role.setId(Role.HQ.getValue());
        user.setRoleId(role);
        user.setStatus(Status.WAIT.getValue());

        try {
            // Tạo RestTemplate
            RestTemplate restTemplate = new RestTemplate();

            // Tạo header cho yêu cầu
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Tạo yêu cầu với đối tượng người dùng trong body và header
            HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);

            // Gửi yêu cầu POST và nhận phản hồi từ API
            ResponseEntity<Users> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Users.class);

            // Xử lý phản hồi từ API
            return responseEntity.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private Company createCompany(Company company) {

        //set giá trị cho company
        company.setTotalRevenue(BigDecimal.ZERO);

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Company> requestEntity = new HttpEntity<>(company, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Company> response = restTemplate.exchange(
                "http://localhost:8888/api/create-company",
                HttpMethod.POST,
                requestEntity,
                Company.class
        );

        return response.getBody();
    }

    private Users getUserById(int userId) {
        String apiUrl = "http://localhost:8888/api/users/user/" + userId;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Users> response = restTemplate.getForEntity(apiUrl, Users.class);

        // You can process the response here and return it or handle errors.
        return response.getBody();
    }

    // Method to validate email format using regex
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    // Method to validate phone number format using regex
    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^[0-9]{10,}$"; // Adjust the regex pattern as needed
        return phoneNumber.matches(regex);
    }

}
