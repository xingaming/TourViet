package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Company;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import fpt.aptech.portal.enums.Status;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class CompanyController {

    @RequestMapping("/companies")
    public String companies(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        String apiUrl = "http://localhost:8888/api/companies";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company[]> response = restTemplate.getForEntity(apiUrl, Company[].class);
        Company[] companies = response.getBody();

        model.addAttribute("companies", companies);
        return "admin/company/companies";
    }

    @RequestMapping("/create-company")
    public String createCompany(Model model, HttpSession session) {

        return "admin/company/create-company";
    }

    @RequestMapping(value = "/create-company", method = RequestMethod.POST)
    public String createCompany(Model model, HttpSession session, @Nullable @ModelAttribute("Company") Company company, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (company.getName().isEmpty() || company.getAddress().isEmpty() || company.getEmail().isEmpty() || company.getHotline().isEmpty() || (company.getLogo().isEmpty() && imageFile.isEmpty())) {
            // set ảnh để kiểm tra
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    company.setLogo(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }

            // Kiểm tra email và hotline
            if (!isValidEmail(company.getEmail()) && !isValidHotline(company.getHotline()) && !company.getEmail().isEmpty() && !company.getEmail().isEmpty()) {
                if (!imageFile.isEmpty()) {
                    try {
                        byte[] imageBytes = imageFile.getBytes();
                        String base64Image = convertImageToBase64(imageBytes);
                        company.setLogo(base64Image);
                    } catch (Exception e) {
                        // Xử lý lỗi nếu có (nếu cần)
                        model.addAttribute("message", "An error occurred while processing the image.");
                    }
                }
                redirectAttributes.addFlashAttribute("emailMessage", "Invalid email format.");
                redirectAttributes.addFlashAttribute("hotlineMessage", "Invalid hotline format (minimum 10 digits).");
            }

            // Kiểm tra email hoặc hotline
            if (!isValidEmail(company.getEmail()) || !isValidHotline(company.getHotline())) {
                if (!imageFile.isEmpty()) {
                    try {
                        byte[] imageBytes = imageFile.getBytes();
                        String base64Image = convertImageToBase64(imageBytes);
                        company.setLogo(base64Image);
                    } catch (Exception e) {
                        // Xử lý lỗi nếu có (nếu cần)
                        model.addAttribute("message", "An error occurred while processing the image.");
                    }
                }
                if (!isValidEmail(company.getEmail()) && !company.getEmail().isEmpty()) {
                    redirectAttributes.addFlashAttribute("emailMessage", "Invalid email format.");
                }
                if (!isValidHotline(company.getHotline()) && !company.getEmail().isEmpty()) {
                    redirectAttributes.addFlashAttribute("hotlineMessage", "Invalid hotline format (minimum 10 digits).");
                }
            }

            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-company";
        }

        // Kiểm tra email và hotline
        if (!isValidEmail(company.getEmail()) && !isValidHotline(company.getHotline())) {
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    company.setLogo(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            redirectAttributes.addFlashAttribute("company", company);
            redirectAttributes.addFlashAttribute("emailMessage", "Invalid email format.");
            redirectAttributes.addFlashAttribute("hotlineMessage", "Invalid hotline format (minimum 10 digits).");
            return "redirect:/admin/create-company";
        }

        // Kiểm tra email hoặc hotline
        if (!isValidEmail(company.getEmail()) || !isValidHotline(company.getHotline())) {
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    company.setLogo(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            if (!isValidEmail(company.getEmail())) {
                redirectAttributes.addFlashAttribute("emailMessage", "Invalid email format.");
            }
            if (!isValidHotline(company.getHotline())) {
                redirectAttributes.addFlashAttribute("hotlineMessage", "Invalid hotline format (minimum 10 digits).");
            }
            redirectAttributes.addFlashAttribute("company", company);
            return "redirect:/admin/create-company";
        }

        // Set giá trị cho company
        company.setStatus(true);
        company.setTotalRevenue(BigDecimal.ZERO);

        // Kiểm tra xem người dùng đã chọn tệp ảnh hay chưa
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                company.setLogo(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                model.addAttribute("errorMessage", "An error occurred while processing the image.");
                return "admin/tour/create-tour";
            }
        }

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

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Company createdCompany = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully created a new company.");
            return "redirect:/admin/companies";
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping(value = "/disable-company/{companyId}", method = RequestMethod.GET)
    public String disableCompany(Model model, HttpSession session, @PathVariable("companyId") int companyId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Disable company
        Company company = getCompanyById(companyId);
        company.setStatus(Boolean.FALSE);

        saveCompany(company);

        return "redirect:/admin/companies";
    }

    @RequestMapping(value = "/enable-company/{companyId}", method = RequestMethod.GET)
    public String enableCompany(Model model, HttpSession session, @PathVariable("companyId") int companyId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Disable company
        Company company = getCompanyById(companyId);
        company.setStatus(Boolean.TRUE);

        saveCompany(company);

        return "redirect:/admin/companies";
    }

    @RequestMapping("/detail-company/{companyId}")
    public String detailCompany(Model model, HttpSession session, @PathVariable("companyId") int companyId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        Company company = getCompanyById(companyId);

        model.addAttribute("company", company);
        return "admin/company/detail-company";
    }

    @RequestMapping("/registration-companies")
    public String registerCompany(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        String apiUrl = "http://localhost:8888/api/companies";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company[]> response = restTemplate.getForEntity(apiUrl, Company[].class);
        Company[] companies = response.getBody();

        model.addAttribute("companies", companies);
        return "admin/company/registration-required";
    }

    @RequestMapping("/registration-required/{companyId}")
    public String solveRequireRegisterCompany(Model model, HttpSession session, @PathVariable("companyId") int companyId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        List<Users> userss = getAllAccounts(user.getId(), user.getToken());
        int userId = userss.stream()
                .filter(u -> u.getCompanyId() != null && u.getCompanyId().getId() != null && u.getCompanyId().getId().equals(companyId))
                .findFirst()
                .map(Users::getId)
                .orElse(0);

        Company company = getCompanyById(companyId);
        Users users = getUserById(userId);
        model.addAttribute("company", company);
        model.addAttribute("user", users);
        model.addAttribute("companyId", companyId);

        return "admin/company/solve-company";
    }

    @RequestMapping(value = "/registration-required", method = RequestMethod.POST)
    public String solveRequireRegisterSuccessCompany(Model model, HttpSession session, @RequestParam("id") int companyId, @RequestParam("idUser") int userId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Lấy thông tin
        Company company = getCompanyById(companyId);
        Users users = getUserById(userId);
        
        // Set thông tin
        company.setStatus(Boolean.TRUE);
        users.setStatus(Status.ACTIVE.getValue());
        
        // Cập nhật thông tin
        saveCompany(company);
        saveUser(user);

        return "redirect:/admin/registration-required/" + companyId;
    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
    }

    // Hàm kiểm tra tính hợp lệ của email
    private boolean isValidEmail(String email) {
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

// Hàm kiểm tra tính hợp lệ của hotline
    private boolean isValidHotline(String hotline) {
        String hotlineRegex = "[0-9]{10,}";
        Pattern pattern = Pattern.compile(hotlineRegex);
        Matcher matcher = pattern.matcher(hotline);
        return matcher.matches();
    }

    private Company getCompanyById(@PathVariable int companyId) {
        String apiUrl = "http://localhost:8888/api/company/" + companyId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company> response = restTemplate.getForEntity(apiUrl, Company.class);

        // You can process the response here and return it or handle errors.
        return response.getBody();
    }

    private Users getUserById(int userId) {
        String apiUrl = "http://localhost:8888/api/users/user/" + userId;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Users> response = restTemplate.getForEntity(apiUrl, Users.class);

        // You can process the response here and return it or handle errors.
        return response.getBody();
    }

    private Users saveUser(Users user) {
        String apiUrl = "http://localhost:8888/api/users/user/" + user.getId();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Users> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, Users.class);

        // Xử lý phản hồi ở đây nếu cần và trả về
        return response.getBody();
    }

    private Company saveCompany(Company company) {
        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header nếu có)
        HttpHeaders headers = new HttpHeaders();
        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request (nếu có)
        // headers.set("Authorization", "Bearer your_token_here"); // Thay your_token_here bằng token của bạn nếu có
        // Đặt body cho request là đối tượng Company
        HttpEntity<Company> requestEntity = new HttpEntity<>(company, headers);

        // Gọi API bằng phương thức POST và không nhận kết quả trả về
        ResponseEntity<Company> response = restTemplate.exchange(
                "http://localhost:8888/api/edit-company",
                HttpMethod.PUT, requestEntity,
                Company.class
        );

        return response.getBody();
    }

    private List<Users> getAllAccounts(int adminId, String token) {
        String apiUrl = "http://localhost:8888/api/admin/" + adminId + "/accounts";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Chèn token vào header Authorization

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Users[]> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Users[].class);
        Users[] usersArray = response.getBody();

        return Arrays.asList(usersArray);
    }

    private String convertImageToBase64(byte[] imageBytes) {
        // Chuyển đổi mảng byte của hình ảnh thành mã Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }

}
