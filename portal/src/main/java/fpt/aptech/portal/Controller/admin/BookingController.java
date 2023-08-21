package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.email.EmailService;
import fpt.aptech.portal.entities.Booking;
import fpt.aptech.portal.entities.Company;
import fpt.aptech.portal.entities.Informationbooking;
import fpt.aptech.portal.entities.Payment;
import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.PaymentStatus;
import fpt.aptech.portal.enums.Role;
import fpt.aptech.portal.service.user.TourService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/admin")
public class BookingController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TourService tourService;

    @RequestMapping("/booking")
    public String booking(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Gọi API để lấy danh sách booking từ backend
        int adminId = user.getId();
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/" + adminId + "/bookings";
        ResponseEntity<Booking[]> response = restTemplate.getForEntity(apiUrl, Booking[].class);
        Booking[] bookings = response.getBody();

        // Lấy status cho booking
        bookings = getStatusForBooking(adminId, bookings);

        // Đảo ngược mảng
        List<Booking> bookingList = Arrays.asList(bookings);
        Collections.reverse(bookingList);
        bookings = bookingList.toArray(new Booking[0]);

        // Thêm danh sách booking vào model để hiển thị trong view
        model.addAttribute("bookings", bookings);

        return "admin/booking/booking";
    }

    @RequestMapping("/booking/{bookingId}/payment")
    public String viewPayment(@PathVariable int bookingId, Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Gọi API để lấy thông tin payment từ backend
        // PAYMENT
        int adminId = user.getId();
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId + "/payment";
        ResponseEntity<Payment> response = restTemplate.getForEntity(apiUrl, Payment.class);
        Payment payment = response.getBody();

        // Thêm thông tin payment vào model để hiển thị trong view
        model.addAttribute("payment", payment);
        // END PAYMENT

        // BOOKING
        String apiUrlBooking = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId;
        ResponseEntity<Booking> responseBooking = restTemplate.getForEntity(apiUrlBooking, Booking.class);
        Booking booking = responseBooking.getBody();

        model.addAttribute("booking", booking);
        model.addAttribute("totalBooking", booking.getAdult() + booking.getChildren() + booking.getSenior() + booking.getBaby());
        // END BOOKING

        // INFORMATION BOOKING
        String apiUrlIn4mationBooking = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId + "/informationBookings";
        ResponseEntity<Informationbooking[]> responseIn4 = restTemplate.getForEntity(apiUrlIn4mationBooking, Informationbooking[].class);
        Informationbooking[] informationBookings = responseIn4.getBody();

        // Thêm danh sách booking vào model để hiển thị trong view
        model.addAttribute("informationBookings", informationBookings);
        // END INFORMATION BOOKING

        return "admin/booking/payment";
    }

    @RequestMapping("/booking/{bookingId}/cancelBooking")
    public String cancelBooking(@PathVariable int bookingId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        model.addAttribute("bookingId", bookingId);

        return "admin/booking/cancel_booking";
    }

    @RequestMapping("/tour_create/{bookingId}/{email}/cancelBooking")
    public String cancelTourCreate(@PathVariable int bookingId, @PathVariable String email, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("email", email);

        return "admin/made_tour/cancel_booking";
    }

    @RequestMapping(value = "/tour_create/cancelBooking", method = RequestMethod.POST)
    public String cancelTourCreate(@RequestParam("reason") String reason, @RequestParam("booking_id") int bookingId, @RequestParam("email") String recipient, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }
        tourService.cancelFeedback(recipient, bookingId, reason);
        String deleteUrl = "http://localhost:8888/api/admin/tour_create/" + bookingId;
        RestTemplate rs = new RestTemplate();
        rs
                .exchange(deleteUrl, HttpMethod.DELETE, null, String.class
                );
        System.out.println(recipient);
        System.out.println(reason);
        System.out.println(bookingId);

        

        return "redirect:/admin/tour_create";

    }

    @RequestMapping(value = "/booking/payment", method = RequestMethod.POST)
    public String confirmBooking(@ModelAttribute("Payment") Payment payment, @RequestParam("booking_id") int bookingId, @RequestParam("user_id") int userBookingId, @RequestParam("userEmail") String userEmail, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // set thay đổi cho payment
        payment.setDate(new Date());
        Booking b = new Booking();
        b.setId(bookingId);
        payment.setBookingId(b);
        payment.setStatus(PaymentStatus.PAID.getValue());
        Users u = new Users();
        u.setId(userBookingId);
        payment.setUserId(u);
        payment.setDescription("PAID");

        // lấy thông tin cần thay đổi
        Users superAdmin = getUserById(Role.SUPER_ADMIN.getValue());
        Company company = getCompanyById(user.getCompanyId().getId());

        // cập nhật doanh thu cho company và super admin
        BigDecimal revenueToAdd = payment.getPrice().multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_UP);
        company.setTotalRevenue(company.getTotalRevenue().add(revenueToAdd));

        BigDecimal coinToAdd = payment.getPrice().multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        superAdmin.setCoin(superAdmin.getCoin().add(coinToAdd));

        // Lấy thông tin booking để gửi mail
        Booking booking = getBookingById(user.getId(), bookingId);
        List<Informationbooking> informationbookings = getListInformationBookingByBookingId(user.getId(), bookingId);

        // Lấy thông tin schedule
        int total = 0;
        total = booking.getAdult() + booking.getBaby() + booking.getChildren() + booking.getSenior();
        Schedule schedule = getScheduleById2(booking.getScheduleId().getId());
        schedule.setQuantityPassenger(schedule.getQuantityPassenger() + total);

        if (schedule.getQuantityPassenger() > schedule.getQuantityMax()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The number of tourists booking the tour has exceeded the maximum number of tourists on the tour.");
        }

        if (schedule.getQuantityPassenger().equals(schedule.getQuantityMax())) {

        }

        // Lưu thông tin đã cập nhật
        saveSchedule(user, schedule);
        saveCompany(user.getId(), company);
        saveUser(superAdmin);

        // Set up the RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        // Set the request headers, assuming you are sending JSON data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Assuming you want to pass the payment object as the request body
        HttpEntity<Payment> requestEntity = new HttpEntity<>(payment, headers);
        // The URL for the API endpoint
        String apiUrl = "http://localhost:8888/api/admin/" + user.getId() + "/payment";
        // Make the PUT request and get the response
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, String.class);

        // You can handle the response as needed
        if (response.getStatusCode().is2xxSuccessful()) {
            // API call was successful, handle the response
            String responseBody = response.getBody();

            // SEND MAIL
            emailService.sendConfirmationEmail(userEmail, booking.getUserId().getLastName() + ' ' + booking.getUserId().getFirstName(), bookingId, payment, booking, informationbookings);

            return "redirect:/admin/booking/" + bookingId + "/payment";
        } else {

            return "admin/error505";
        }
    }

    @RequestMapping(value = "/booking/cancelBooking", method = RequestMethod.POST)
    public String cancelBooking(@RequestParam("reason") String reason, @RequestParam("booking_id") int bookingId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // lấy thông tin cần thay đổi
        //Users superAdmin = getUserById(Role.SUPER_ADMIN.getValue());
        //Company company = getCompanyById(user.getCompanyId().getId());
        // Lấy thông tin booking để gửi mail
        Booking booking = getBookingById(user.getId(), bookingId);
        List<Informationbooking> informationbookings = getListInformationBookingByBookingId(user.getId(), bookingId);

        // PAYMENT
        int adminId = user.getId();
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId + "/payment";
        ResponseEntity<Payment> response = restTemplate.getForEntity(apiUrl, Payment.class);
        Payment payment = response.getBody();
        // END PAYMENT

        // Set the request headers, assuming you are sending JSON data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Assuming you want to pass the payment object as the request body
        HttpEntity<Booking> requestEntity = new HttpEntity<>(booking, headers);
        // The URL for the API endpoint
        String apiUrl2 = "http://localhost:8888/api/admin/" + user.getId() + "/booking/" + bookingId;
        // Make the PUT request and get the response
        ResponseEntity<String> response2 = restTemplate.exchange(apiUrl2, HttpMethod.DELETE, requestEntity, String.class);

        // You can handle the response as needed
        if (response.getStatusCode().is2xxSuccessful()) {
            // API call was successful, handle the response
            String responseBody = response2.getBody();
            // SEND MAIL
            emailService.sendCancelBookingEmail(payment.getUserId().getEmail(), booking.getUserId().getLastName() + ' ' + booking.getUserId().getFirstName(), reason, bookingId, payment, booking, informationbookings);

            redirectAttributes.addFlashAttribute("message", "Canceled tour booking successfully.");
            return "redirect:/admin/booking";
        } else {

            return "admin/error505";
        }
    }

    // Method phụ
    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
    }

    private Company getCompanyById(@PathVariable int companyId) {
        String apiUrl = "http://localhost:8888/api/company/" + companyId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company> response = restTemplate.getForEntity(apiUrl, Company.class);

        // You can process the response here and return it or handle errors.
        return response.getBody();
    }

    private Company saveCompany(int adminId, Company company) {
        String apiUrl = "http://localhost:8888/api/company/" + adminId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Company> requestEntity = new HttpEntity<>(company, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Company> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, Company.class);

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
//        headers.add("Authorization", "Bearer " + user.getToken());
//        HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Users> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, Users.class);
//
//        // You can process the response here and return it or handle errors.
//        return response.getBody();
        HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Users> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, Users.class);

        // Xử lý phản hồi ở đây nếu cần và trả về
        return response.getBody();
    }

    private Booking getBookingById(int adminId, int bookingId) {
        // BOOKING
        RestTemplate restTemplate = new RestTemplate();
        String apiUrlBooking = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId;
        ResponseEntity<Booking> responseBooking = restTemplate.getForEntity(apiUrlBooking, Booking.class);
        Booking booking = responseBooking.getBody();
        return booking;
    }

    private List<Informationbooking> getListInformationBookingByBookingId(int adminId, int bookingId) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrlIn4mationBooking = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId + "/informationBookings";
        ResponseEntity<Informationbooking[]> responseIn4 = restTemplate.getForEntity(apiUrlIn4mationBooking, Informationbooking[].class);
        Informationbooking[] informationBookings = responseIn4.getBody();

        // Chuyển mảng Informationbooking[] thành List<Informationbooking>
        List<Informationbooking> informationBookingList = Arrays.asList(informationBookings);

        return informationBookingList;
    }

    private Schedule getScheduleById2(Integer scheduleId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/tours/schedule/{id}")
                .uriVariables(Collections.singletonMap("id", scheduleId));

        ResponseEntity<Schedule> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Schedule.class
        );

        return response.getBody();
    }

    private Schedule saveSchedule(Users user, Schedule schedule) {
        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Schedule> requestEntity = new HttpEntity<>(schedule, headers);

        try {
            // Gọi API bằng phương thức PUT và nhận kết quả trả về
            ResponseEntity<Schedule> response = restTemplate.exchange(
                    "http://localhost:8888/api/admin/{adminId}/schedules",
                    HttpMethod.PUT,
                    requestEntity,
                    Schedule.class,
                    user.getId()
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private Booking[] getStatusForBooking(int adminId, Booking[] bookings) {
        // Lặp qua danh sách booking và gán status từ API vào mỗi booking
        // Tạo danh sách các nhiệm vụ Callable để thực hiện gọi API bất đồng bộ
        List<Callable<Void>> tasks = new ArrayList<>();

        for (Booking booking : bookings) {
            Callable<Void> task = () -> {
                Short status = getPaymentStatusAsync(booking.getId(), adminId);
                booking.setStatus(status);
                return null;
            };
            tasks.add(task);
        }

        // Sử dụng ExecutorService để thực hiện các nhiệm vụ đồng thời và chờ cho đến khi tất cả hoàn thành
        ExecutorService executor = Executors.newFixedThreadPool(bookings.length);
        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            // Xử lý lỗi nếu cần
        } finally {
            executor.shutdown();
        }
        // END get status
        return bookings;
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

//    @RequestMapping("/booking/{bookingId}/informationBooking")
//    public String informationBooking(@PathVariable int bookingId, Model model, HttpSession session) {
//        Users user = (Users) session.getAttribute("user");
//        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
//            return "/admin/error403";
//        }
//
//        // Gọi API để lấy danh sách booking từ backend
//        int adminId = user.getId();
//        RestTemplate restTemplate = new RestTemplate();
//        String apiUrl = "http://localhost:8888/api/admin/" + adminId + "/booking/" + bookingId + "/informationBookings";
//        ResponseEntity<Informationbooking[]> response = restTemplate.getForEntity(apiUrl, Informationbooking[].class);
//        Informationbooking[] informationBookings = response.getBody();
//
//        // Thêm danh sách booking vào model để hiển thị trong view
//        model.addAttribute("informationBookings", informationBookings);
//
//        return "admin/booking/informationBooking";
//    }
}
