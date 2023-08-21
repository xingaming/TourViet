package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Booking;
import fpt.aptech.portal.entities.Company;
import fpt.aptech.portal.entities.Payment;
import fpt.aptech.portal.entities.Tour;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; //cmn cay vlon
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @RequestMapping("/dashboard")
    public String page(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        String apiBooking = "http://localhost:8888/api/admin/";
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        RestTemplate rs = new RestTemplate();
        // HQ AND AREA
        if (user.getRoleId().getId() == Role.HQ.getValue() || user.getRoleId().getId() == Role.AREA.getValue()) {

            // BOOKING
            ResponseEntity<Booking[]> response = rs.getForEntity(apiBooking + user.getId() + "/bookings", Booking[].class);
            Booking[] result = response.getBody();

            // lấy status cho booking
            result = getStatusForBooking(user.getId(), result);

            // Đảo ngược mảng
            List<Booking> bookingList = Arrays.asList(result);
            Collections.reverse(bookingList);
            result = bookingList.toArray(new Booking[0]);

            int pendingBookingCount = (int) Arrays.stream(result)
                    .filter(booking -> booking.getStatus() == 0)
                    .count();
            int solvedBookingCount = (int) Arrays.stream(result)
                    .filter(booking -> booking.getStatus() == 1)
                    .count();
            Date currentDateBooking = new Date();

            int todayBookingCount = (int) Arrays.stream(result)
                    .filter(booking -> isSameDay(booking.getBookingDate(), currentDateBooking))
                    .count();
            // END BOOKING

            List<Tour> tours = getListTour(user);
            Tour[] tourArr = tours.toArray(new Tour[0]);
            int tourCount = (int) Arrays.stream(tourArr).count();

            // COMPANY
            Company company = getCompanyById(user.getCompanyId().getId());
            // END COMPANY

            // PAYMENT
            List<Payment> payments = getPaidPaymentByCompanyId(user.getCompanyId().getId());
            // END PAYMENT

            // Lấy ngày tháng năm hiện tại
            LocalDate currentDate = LocalDate.now();
            int year = currentDate.getYear();
            YearMonth yearMonth = YearMonth.from(currentDate);

            // Tính quý của thời gian hiện tại
            int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;
            // Lấy năm của thời gian hiện tại
            int currentYear = currentDate.getYear();

            // Tính tổng doanh thu theo quý, tháng, năm, và ngày
            BigDecimal totalRevenueByQuarter = getTotalRevenueByQuarter(payments, currentQuarter, currentYear);
            BigDecimal totalRevenueByMonth = getTotalRevenueByMonth(payments, yearMonth);
            BigDecimal totalRevenueByYear = getTotalRevenueByYear(payments, year);
            BigDecimal totalRevenueByDate = getTotalRevenueByDate(payments, currentDate);

            // Đưa các giá trị vào model attribute
            // Thống kê doanh thu
            model.addAttribute("totalRevenueByQuarter", totalRevenueByQuarter);
            model.addAttribute("totalRevenueByMonth", totalRevenueByMonth);
            model.addAttribute("totalRevenueByYear", totalRevenueByYear);
            model.addAttribute("totalRevenueByDate", totalRevenueByDate);

            // thống kê booking and tour
            model.addAttribute("pendingBookingCount", pendingBookingCount);
            model.addAttribute("solvedBookingCount", solvedBookingCount);
            model.addAttribute("tourCount", tourCount);
            model.addAttribute("todayBookingCount", todayBookingCount);

            model.addAttribute("payments", payments);
            model.addAttribute("companies", user.getCompanyId());
            model.addAttribute("totalCompanyRevenue", company.getTotalRevenue());
            model.addAttribute("listBooking", result);
            return "admin/dashboard";
        }

        // SUPER ADMIN
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {

            // USER
            List<Users> users = getAllAccounts(user.getId(), user.getToken());
            int userCount = users.size();
            // END USER

            // COMPANY
            List<Company> companies = getAllCompanies();
            int countCompany = (int) companies.stream()
                    .filter(company -> company.getStatus() != null && company.getStatus())
                    .count();
            // END COMPANY
            int countRequest = (int) companies.stream()
                    .filter(company -> company.getStatus() == null)
                    .count();
            // COUNT REQUEST
            
            // END COUNT REQUEST
            
            // TOUR
            List<Tour> tours = getAlltours();
            int countTour = tours.size();
            // END TOUR

            // PAYMENT
            List<Payment> payments = getPaidPayment();
            // Duyệt qua từng đối tượng Payment và cập nhật giá (price) theo yêu cầu
            for (Payment payment : payments) {
                BigDecimal originalPrice = payment.getPrice();
                BigDecimal newPrice = originalPrice.multiply(new BigDecimal("0.05"));
                payment.setPrice(newPrice); // Cập nhật giá mới vào đối tượng Payment
            }

            // END PAYMENT
            // Lấy ngày tháng năm hiện tại
            LocalDate currentDate = LocalDate.now();
            int year = currentDate.getYear();
            YearMonth yearMonth = YearMonth.from(currentDate);

            // Tính quý của thời gian hiện tại
            int currentQuarter = (currentDate.getMonthValue() - 1) / 3 + 1;
            // Lấy năm của thời gian hiện tại
            int currentYear = currentDate.getYear();

            // Tính tổng doanh thu theo quý, tháng, năm, và ngày
            BigDecimal totalRevenueByQuarter = getTotalRevenueByQuarter(payments, currentQuarter, currentYear);
            BigDecimal totalRevenueByMonth = getTotalRevenueByMonth(payments, yearMonth);
            BigDecimal totalRevenueByYear = getTotalRevenueByYear(payments, year);
            BigDecimal totalRevenueByDate = getTotalRevenueByDate(payments, currentDate);

            // Đưa các giá trị vào model attribute
            // Thống kê doanh thu
            model.addAttribute("totalRevenueByQuarter", totalRevenueByQuarter.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("totalRevenueByMonth", totalRevenueByMonth.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("totalRevenueByYear", totalRevenueByYear.setScale(2, RoundingMode.HALF_UP));
            model.addAttribute("totalRevenueByDate", totalRevenueByDate.setScale(2, RoundingMode.HALF_UP));

            // Số lượng user
            model.addAttribute("userCount", userCount);
            model.addAttribute("countCompany", countCompany);
            model.addAttribute("countTour", countTour);
            model.addAttribute("countRequest", countRequest);
            
            // Doanh thu của super admin
            Users superAdmin = getUserById(Role.SUPER_ADMIN.getValue());
            model.addAttribute("totalRevenue", superAdmin.getCoin());

            return "admin/dashboard";
        }

        return "admin/dashboard";
    }

    @RequestMapping("/revenue")
    public String revenue(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        List<Payment> payments = getPaidPaymentByCompanyId(user.getCompanyId().getId());
        model.addAttribute("payments", payments);
        return "admin/revenue";
    }

    @RequestMapping("/invoice")
    public String invoice(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        return "admin/invoice";
    }

    @RequestMapping("/user")
    public String user(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        return "admin/user";
    }

    @RequestMapping("/staff")
    public String staff(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        return "admin/staff";
    }

    @RequestMapping("/feedback")
    public String feedback(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        return "admin/feedback";
    }

    @RequestMapping("/contact")
    public String contact(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        return "admin/contact";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "admin/login";
    }

    @RequestMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "admin/forgot-password";
    }

    @RequestMapping("/error403")
    public String Forbidden(Model model) {
//        model.addAttribute("user", new Users());
        return "admin/error403";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session, HttpServletResponse response) {
        if (session != null) {
            session.invalidate(); // Xoá toàn bộ session của người dùng
        }
        // Tắt caching trên trình duyệt bằng cách sử dụng các thông báo HTTP
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return "redirect:/user/login"; // Chuyển hướng về trang đăng nhập sau khi logout thành công

    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
    }

    private Users getUserById(int userId) {
        String apiUrl = "http://localhost:8888/api/users/user/" + userId;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Users> response = restTemplate.getForEntity(apiUrl, Users.class);

        // You can process the response here and return it or handle errors.
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

    private Short getPaymentStatusAsync(int bookingId, int userId) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/" + userId + "/booking/" + bookingId + "/payment";

        Callable<Short> callable = () -> {
            ResponseEntity<Payment> response = restTemplate.getForEntity(apiUrl, Payment.class);
            Payment payment = response.getBody();
            return payment.getStatus();
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Short status = executor.submit(callable).get();
            executor.shutdown();
            return status;
        } catch (Exception e) {
            // Xử lý lỗi nếu cần
            return null;
        }
    }

    private Booking[] getStatusForBooking(int adminId, Booking[] result) {
        // Tạo danh sách các nhiệm vụ Callable để thực hiện gọi API bất đồng bộ
        List<Callable<Void>> tasks = new ArrayList<>();

        for (Booking booking : result) {
            Callable<Void> task = () -> {
                Short status = getPaymentStatusAsync(booking.getId(), adminId);
                booking.setStatus(status);
                return null;
            };
            tasks.add(task);
        }

        // Sử dụng ExecutorService để thực hiện các nhiệm vụ đồng thời và chờ cho đến khi tất cả hoàn thành
        ExecutorService executor = Executors.newFixedThreadPool(result.length);
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            // Xử lý lỗi nếu cần
        } finally {
            executor.shutdown();
        }
        return result;
    }

    private Booking[] reverseArr(Booking[] result) {
        // Đảo ngược mảng result
        int start = 0;
        int end = result.length - 1;
        while (start < end) {
            Booking temp = result[start];
            result[start] = result[end];
            result[end] = temp;
            start++;
            end--;
        }
        return result;
    }

    private Company getCompanyById(@PathVariable int companyId) {
        String apiUrl = "http://localhost:8888/api/company/" + companyId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company> response = restTemplate.getForEntity(apiUrl, Company.class);

        // You can process the response here and return it or handle errors.
        return response.getBody();
    }

    private List<Payment> getPaidPaymentByCompanyId(Integer companyId) {

        String apiUrl = "http://localhost:8888/api/company/";

        RestTemplate restTemplate = new RestTemplate();
        String url = apiUrl + companyId + "/paymentPAID";

        ResponseEntity<Payment[]> response = restTemplate.getForEntity(url, Payment[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Payment[] payments = response.getBody();
            return Arrays.asList(payments);
        } else {
            // Xử lý lỗi nếu cần
            return Collections.emptyList();
        }
    }

    private List<Payment> getPaidPayment() {
        String apiUrl = "http://localhost:8888/api/paymentPAID";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Payment[]> response = restTemplate.getForEntity(apiUrl, Payment[].class);
        Payment[] payments = response.getBody();

        return Arrays.asList(payments);
    }

    private List<Tour> getListTour(Users user) {
        // API endpoint URL with {userId} as a placeholder for the actual userId value
        String apiUrl = "http://localhost:8888/api/admin/{userId}/tours";

        // Build the complete URL with the actual userId value
        String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                .buildAndExpand(user.getId())
                .toUriString();

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();
        // Add the Authorization header with the token value
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + user.getToken());
        // Make the API call using the complete URL and the Authorization header
        ResponseEntity<?> response = restTemplate.exchange(
                completeUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Tour>>() {
        }
        );
        // Get the list of tours from the response body
        List<Tour> tours = (List<Tour>) response.getBody();
        return tours;
    }

    private List<Tour> getAlltours() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/tours/";

        ResponseEntity<Tour[]> response = restTemplate.getForEntity(apiUrl, Tour[].class);
        Tour[] tours = response.getBody();

        return Arrays.asList(tours);
    }

    private List<Company> getAllCompanies() {
        String apiUrl = "http://localhost:8888/api/companies";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company[]> response = restTemplate.getForEntity(apiUrl, Company[].class);
        Company[] companies = response.getBody();

        return Arrays.asList(companies);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        int day1 = cal1.get(Calendar.DAY_OF_MONTH);
        int month1 = cal1.get(Calendar.MONTH);
        int year1 = cal1.get(Calendar.YEAR);

        int day2 = cal2.get(Calendar.DAY_OF_MONTH);
        int month2 = cal2.get(Calendar.MONTH);
        int year2 = cal2.get(Calendar.YEAR);

        return day1 == day2 && month1 == month2 && year1 == year2;
    }

    // Phương thức tính tổng doanh thu theo ngày
    public BigDecimal getTotalRevenueByDate(List<Payment> payments, LocalDate date) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Payment payment : payments) {
            // Lấy ngày thanh toán từ payment
            Date paymentDate = payment.getDate();
            LocalDate localDate = Instant.ofEpochMilli(paymentDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Nếu ngày khớp với ngày được truyền vào, thì cộng vào tổng doanh thu
            if (localDate.equals(date)) {
                totalRevenue = totalRevenue.add(payment.getPrice());
            }
        }
        return totalRevenue;
    }

    // Phương thức tính tổng doanh thu theo tháng
    public BigDecimal getTotalRevenueByMonth(List<Payment> payments, YearMonth yearMonth) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Payment payment : payments) {
            // Lấy ngày thanh toán từ payment
            Date paymentDate = payment.getDate();
            LocalDate localDate = Instant.ofEpochMilli(paymentDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            YearMonth paymentYearMonth = YearMonth.of(localDate.getYear(), localDate.getMonth());

            // Nếu tháng khớp với tháng được truyền vào, thì cộng vào tổng doanh thu
            if (paymentYearMonth.equals(yearMonth)) {
                totalRevenue = totalRevenue.add(payment.getPrice());
            }
        }
        return totalRevenue;
    }

    // Phương thức tính tổng doanh thu cho từng quý
    public BigDecimal getTotalRevenueByQuarter(List<Payment> payments, int quarter, int year) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Payment payment : payments) {
            // Lấy ngày thanh toán từ payment
            Date paymentDate = payment.getDate();
            LocalDate localDate = Instant.ofEpochMilli(paymentDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            int paymentQuarter = (localDate.getMonthValue() - 1) / 3 + 1;
            int paymentYear = localDate.getYear();

            // Nếu quý và năm khớp với quý và năm được truyền vào, thì cộng vào tổng doanh thu
            if (paymentQuarter == quarter && paymentYear == year) {
                totalRevenue = totalRevenue.add(payment.getPrice());
            }
        }
        return totalRevenue;
    }

    // Phương thức tính tổng doanh thu theo năm
    public BigDecimal getTotalRevenueByYear(List<Payment> payments, int year) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Payment payment : payments) {
            // Lấy ngày thanh toán từ payment
            Date paymentDate = payment.getDate();
            LocalDate localDate = Instant.ofEpochMilli(paymentDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Nếu năm khớp với năm được truyền vào, thì cộng vào tổng doanh thu
            if (localDate.getYear() == year) {
                totalRevenue = totalRevenue.add(payment.getPrice());
            }
        }
        return totalRevenue;
    }

}
