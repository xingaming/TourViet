package fpt.aptech.portal.Controller.user;

//import java.net.http.HttpHeaders;
import fpt.aptech.portal.TokenUtil.HashUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.aptech.portal.entities.Address;
import fpt.aptech.portal.entities.Booking;
import fpt.aptech.portal.entities.BookingRequest;
import fpt.aptech.portal.entities.In4BookingRequest;
import fpt.aptech.portal.entities.Informationbooking;
import fpt.aptech.portal.entities.MoMoPaymentResponse;
import fpt.aptech.portal.entities.Payment;
import fpt.aptech.portal.entities.Region;
import fpt.aptech.portal.entities.Review;
import fpt.aptech.portal.entities.Roles;
import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Scheduleimage;
import fpt.aptech.portal.entities.Scheduleitem;
import fpt.aptech.portal.entities.Serviceitem;
import fpt.aptech.portal.entities.StoreWithAnotherObject;
import fpt.aptech.portal.entities.Tour;
//import fpt.aptech.portal.entities.TourCreate;
import fpt.aptech.portal.entities.TourCreate;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import fpt.aptech.portal.enums.Status;
import fpt.aptech.portal.service.user.ScheduleService;
import fpt.aptech.portal.service.user.TourService;
//import fpt.aptech.portal.service.user.TourService;
//import fpt.aptech.portal.service.user.TourService;
import fpt.aptech.portal.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession; //cmn cay vlon
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private TourService tourService;

    @Autowired
    private ScheduleService scheduleService;

    private final String url = "http://localhost:8888/api/auth/";
    private final String urlTour = "http://localhost:8888/api/tours/";
    private final String urlPayment = "http://localhost:8888/api/payment/";
    private final String urlUser = "http://localhost:8888/api/users/";
    private final String urlAdmin = "http://localhost:8888/api/admin/";

    private String redirectUrl;

    @RequestMapping("/login")
    public String login(Model model, HttpSession session) {
        Users UserSession = (Users) session.getAttribute("user");

        if (UserSession != null) {
            return "redirect:/user/home";
        }
//        model.addAttribute("user", new Users());
        return "user/login";
    }

    @RequestMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new Users());
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String fname, @RequestParam String lname, @RequestParam String phone, @RequestParam String pass, @RequestParam String confirm, Model model) {
        // Create a new RestTemplate instance

//        if (email.isEmpty() || fname.isEmpty() || lname.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
//            model.addAttribute("error", "All fields are required. ");
//            return "user/register";
//        }
        if (!pass.equals(confirm)) {
            model.addAttribute("error", "Password and confirm password do not match. ");
            model.addAttribute("fname", fname);
            model.addAttribute("email", email);
            model.addAttribute("lname", lname);

            model.addAttribute("phone", phone);

            return "user/register";
        }

        Users user = new Users();
        user.setEmail(email);
        user.setFirstName(fname);
        user.setLastName(lname);

        user.setPassword(pass);
        user.setPhone(phone);
//        user.setEmail(email);

        if (!user.isEmailValid()) {
            model.addAttribute("error", "Invalid email format");
            model.addAttribute("fname", fname);
            model.addAttribute("lname", lname);
            model.addAttribute("pass", pass);
            model.addAttribute("phone", phone);

            model.addAttribute("confirm", confirm);

            return "user/register";
        }

        if (!user.isFname()) {
            model.addAttribute("error", "First name cannot be left blank");
            model.addAttribute("email", email);
            model.addAttribute("lname", lname);
            model.addAttribute("pass", pass);
            model.addAttribute("phone", phone);

            model.addAttribute("confirm", confirm);
            return "user/register";
        }

        if (!user.isLname()) {
            model.addAttribute("error", "Last name cannot be left blank");
            model.addAttribute("fname", fname);
            model.addAttribute("email", email);
            model.addAttribute("pass", pass);
            model.addAttribute("phone", phone);

            model.addAttribute("confirm", confirm);
            return "user/register";
        }

        if (!user.isPhoneValid()) {
            model.addAttribute("error", "Assuming phone numbers are 10-12 digits");
            model.addAttribute("fname", fname);
            model.addAttribute("email", email);
            model.addAttribute("pass", pass);
            model.addAttribute("lname", lname);

            model.addAttribute("confirm", confirm);
            return "user/register";
        }

        if (!user.isPasswordValid()) {
            model.addAttribute("error", "Password must be at least 8 characters long.");
            model.addAttribute("fname", fname);
            model.addAttribute("email", email);
            model.addAttribute("lname", pass);
            model.addAttribute("phone", phone);

//            model.addAttribute("confirm", confirm);
            return "user/register";
        }

        String registerApiUrl = url + "register";
        RestTemplate restTemplate = new RestTemplate();

        Roles role = new Roles();
        role.setId(Role.CUSTOMER.getValue());
        user.setRoleId(role);
        user.setStatus(Status.ACTIVE.getValue());
        try {
            ResponseEntity<Users> response = restTemplate.postForEntity(registerApiUrl, user, Users.class);
            Users newUsers = response.getBody();

            if (newUsers != null) {
                // Registration successful, you can handle the response data here
                model.addAttribute("message", "Registration Successful: ");
                return "user/login"; // Redirect to home page or show success message
            } else {
                // Registration failed, handle the error here
                model.addAttribute("error", "Email already exists");
//                model.addAttribute("error", "Invalid email format");
                model.addAttribute("fname", fname);
                model.addAttribute("lname", lname);
                model.addAttribute("pass", pass);
                model.addAttribute("phone", phone);

                model.addAttribute("confirm", confirm);
                return "user/register"; // Redirect back to register page with error message
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            // Handle the unauthorized error here (if needed)
            model.addAttribute("error", "Email already exists");
            model.addAttribute("fname", fname);
            model.addAttribute("lname", lname);
            model.addAttribute("pass", pass);
            model.addAttribute("phone", phone);

            model.addAttribute("confirm", confirm);
            return "user/register"; // Redirect back to register page with error message
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model, @RequestParam(name = "id", required = false) String id, HttpServletRequest request) {
        // Create a new RestTemplate instance
        String loginApiUrl = url + "login";
        RestTemplate restTemplate = new RestTemplate();

        // Create request body with email and password
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("email", email);
        requestBody.add("password", password);

        // Make the HTTP POST request to the login API
        Users user = new Users();
        try {
            user = restTemplate.postForObject(loginApiUrl, requestBody, Users.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            user = null;
        }

        if (user != null) {
            if (user.getStatus() == Status.BLOCK.getValue()) {
                return "/admin/block-account";
            }
            HttpSession session = request.getSession(true);

            session.setAttribute("user", user);
//            if(!id.equals("")){
//                
//            }
//            if (redirectUrl != null && !redirectUrl.isEmpty()) {
//
//                Users userSession = (Users) session.getAttribute("user");
//
//                if (userSession != null) {
//
//                    String firstName = userSession.getFirstName();
//
//                    // Đặt tên người dùng vào trong model để sử dụng trong template
//                    model.addAttribute("session", firstName);
//                }
//
//                return "redirect:" + redirectUrl;
//            }

            Users UserSession = (Users) session.getAttribute("user");
            if (UserSession.getRoleId().getId() == Role.CUSTOMER.getValue()) {
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    if (UserSession != null) {

                        String firstName = UserSession.getFirstName();

                        // Đặt tên người dùng vào trong model để sử dụng trong template
                        model.addAttribute("session", firstName);
                    }
                    return "redirect:" + redirectUrl;
                }
                model.addAttribute("message", "Login Successful: ");
                return "redirect:/user/home"; // Redirect to home page or show success message
            }

            model.addAttribute("message", "Login Successful: ");
            return "redirect:/admin/dashboard";
        } else {

            model.addAttribute("error", "Login failed. Please check your credentials.");
            return "user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

//        session.removeAttribute("user");
        session.invalidate();

        return "redirect:/user/login";

    }

    @GetMapping("/layout")
    public String layout(Model model, HttpSession session) {
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class);

        model.addAttribute("listRegion", result);
//        model.addAttribute("session", session);

        return "user/layout";
    }

    @RequestMapping("/home")
    public String home(Model model) {
//        model.addAttribute("attribute", "value");
        RestTemplate rs = new RestTemplate();

        ResponseEntity<List<Region>> responseRe = rs.exchange(
                urlTour + "region",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Region>>() {
        }
        );

        List<Region> result = responseRe.getBody();
//        List<Region> result = rs.getForObject(urlTour + "region", List.class);
        model.addAttribute("listRegion", result);

        ResponseEntity<List<Payment>> responseEntity = rs.exchange(
                urlUser + "all/payments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Payment>>() {
        }
        );

        List<Payment> resultPayment = responseEntity.getBody();

        List<Payment> filterByStatus = new ArrayList<>();
        for (Payment payment : resultPayment) {
            if (payment.getStatus() == 1) {
                filterByStatus.add(payment);
            }
        }

        List<Booking> filterByBooking = new ArrayList<>();
        List<Booking> listBooking = userService.getAllBooking();
        for (Booking booking : listBooking) {
            for (Payment payment : filterByStatus) {
                if (booking.getId() == payment.getBookingId().getId()) {
                    filterByBooking.add(booking);
                }
            }
        }

        Map<Integer, Integer> scheduleIdCounts = new HashMap<>();

        // Đếm số lần xuất hiện của mỗi schedule_id
        for (Booking booking : filterByBooking) {
            int scheduleId = booking.getScheduleId().getId();
            if (scheduleIdCounts.containsKey(scheduleId)) {
                scheduleIdCounts.put(scheduleId, scheduleIdCounts.get(scheduleId) + 1);
            } else {
                scheduleIdCounts.put(scheduleId, 1);
            }
        }

        // Sắp xếp Map theo giá trị từ cao đến thấp
        List<Map.Entry<Integer, Integer>> sortedScheduleIdCounts = new ArrayList<>(scheduleIdCounts.entrySet());
        sortedScheduleIdCounts.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Lấy ra các giá trị schedule_id đã sắp xếp
        List<Integer> sortedScheduleIds = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : sortedScheduleIdCounts) {
            sortedScheduleIds.add(entry.getKey());
        }

        ResponseEntity<List<Schedule>> responseEntity2 = rs.exchange(urlTour + "region" + "/tour",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Schedule>>() {
        });
        List<Schedule> tourList2 = responseEntity2.getBody();

        List<Schedule> popularSchedule = new ArrayList<>();
        int scheduleCountLimit = 3;
        for (Integer scheduleId : sortedScheduleIds) {
            if (popularSchedule.size() >= scheduleCountLimit) {
                break; // Đã đạt đủ số lượng cần lấy, thoát vòng lặp
            }

            System.out.println("schedule_id: " + scheduleId + ", count: " + scheduleIdCounts.get(scheduleId));
            for (Schedule se : tourList2) {
                if (scheduleId.equals(se.getId())) {
                    popularSchedule.add(se);
                    break;
                }
            }
        }

        List<Region> popularRegion = new ArrayList<>();
        Set<Integer> addedRegionIds = new HashSet<>(); // Set để theo dõi các Region đã thêm

        for (Schedule sch : popularSchedule) {
            for (Region se : result) {
                if (sch.getTourId().getRegionId().getId().equals(se.getId()) && !addedRegionIds.contains(se.getId())) {
                    popularRegion.add(se);
                    addedRegionIds.add(se.getId()); // Thêm Region vào Set
                }
            }
        }

        System.out.println(popularSchedule);
        System.out.println(popularRegion);
        model.addAttribute("popularRegion", popularRegion);

        model.addAttribute("popularSechdule", popularSchedule);

        return "user/home";
    }

    @RequestMapping("/search")
    public String search(Model model, @RequestParam String daterange,
            @RequestParam(value = "region", required = false) int id) {

        //get List of region
        RestTemplate rs = new RestTemplate();
//        List<Region> result = rs.getForObject(urlTour + "region", List.class);
        ResponseEntity<List<Region>> responseRegion = rs.exchange(urlTour + "region",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Region>>() {
        });

        List<Region> result = responseRegion.getBody();

        if (id != 0) {
            for (Region re : result) {
                if (re.getId() == id) {
                    model.addAttribute("regionName", re.getNameArea());
                    model.addAttribute("regionDes", re.getDescription());
                    model.addAttribute("regionImg", re.getImage());
                }
            }
        }

        model.addAttribute("listRegion", result);

        //get List of Tour
        String[] date = daterange.split("-");
        var dateStart = date[0].trim();
        var dateEnd = date[1].trim();

        String[] dateStartSplit = dateStart.split("/");
        String[] dateEndSplit = dateEnd.split("/");

        var resultDateStart = dateStartSplit[2] + "-" + dateStartSplit[0] + "-" + dateStartSplit[1];
        var resultDateEnd = dateEndSplit[2] + "-" + dateEndSplit[0] + "-" + dateEndSplit[1];

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("startDate", resultDateStart);
        requestBody.add("endDate", resultDateEnd);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate rs2 = new RestTemplate();

        ResponseEntity<List<Schedule>> responseEntity = rs.exchange(urlTour + "filteredByDate",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<Schedule>>() {
        });

        List<Schedule> tourList = responseEntity.getBody();

        ResponseEntity<List<Schedule>> responseEntity2 = rs.exchange(urlTour + "region" + "/tour",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Schedule>>() {
        });
        List<Schedule> tourList2 = responseEntity2.getBody();

        int countTour = 0;

        if (tourList == null) {
            model.addAttribute("tourList", tourList);
            model.addAttribute("countTour", countTour);
            return "user/destination";
        }
        List<Schedule> tourResult = new ArrayList<Schedule>();
        if (id != 0) {

            for (Schedule item : tourList) {
                if (item.getTourId().getRegionId().getId().equals(id) && item.getQuantityMax() > item.getQuantityPassenger()) {
                    tourResult.add(item);
                    countTour++;
                }
            }
            ResponseEntity<List<Review>> scheduleReviewMap = rs.exchange(urlUser + "allreviews",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Review>>() {
            });
            List<Review> listReview = scheduleReviewMap.getBody();
            List<Review> reviewResult = new ArrayList<Review>();

            for (Schedule nearestSchedule : tourResult) {
                int tourID = nearestSchedule.getTourId().getId();
                for (Review review : listReview) {
                    if (review.getTourId().getId() == tourID) {
                        reviewResult.add(review);
                    }
                }

            }

            model.addAttribute("scheduleReviewMap", reviewResult);
            model.addAttribute("tourResult", tourList2);
            model.addAttribute("tourList", tourResult);
            model.addAttribute("countTour", countTour);
            return "user/destination";
        }

        for (Schedule item : tourList) {
            countTour++;
        }
        model.addAttribute("tourList", tourList);
        model.addAttribute("tourResult", tourResult);
        model.addAttribute("countTour", countTour);
        return "user/destination";

    }

    @GetMapping("/destination/{id}")
    public String destination(Model model, @PathVariable("id") int id, HttpSession session) {
//        model.addAttribute("attribute", "value");
        RestTemplate rs = new RestTemplate();
//        List<Region> result = rs.getForObject(urlTour + "region", List.class);
        ResponseEntity<List<Region>> responseRegion = rs.exchange(urlTour + "region",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Region>>() {
        });
        List<Region> result = responseRegion.getBody();
        for (Region re : result) {
            int reId = re.getId();
            if (reId == id) {
                model.addAttribute("regionName", re.getNameArea());
                model.addAttribute("regionDes", re.getDescription());
                model.addAttribute("regionImg", re.getImage());
//                model.addAttribute("tourList", re);

            }
        }

        model.addAttribute("listRegion", result);

        ResponseEntity<List<Schedule>> responseEntity = rs.exchange(urlTour + "region" + "/tour",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Schedule>>() {
        });
        List<Schedule> tourList = responseEntity.getBody();

//        ResponseEntity<List<Tour>> responseTour = rs.exchange(urlTour + "all",
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<Tour>>() {
//        });
//        List<Tour> resultTours = responseTour.getBody();
        int countTour = 0;
//        List<Schedule> tourResult = new ArrayList<Schedule>();
        List<Schedule> tourResult = new ArrayList<Schedule>();

        if (tourList != null) {
            for (Schedule item : tourList) {
//                
                if (item.getTourId().getRegionId().getId().equals(id) && item.getQuantityPassenger() < item.getQuantityMax()) {
                    tourResult.add(item);
                    countTour++;
                }
            }
        }

//        List<Review> reviewResult = new ArrayList<Review>();
//        Map<Integer, List<Review>> scheduleReviewMap = new HashMap<>();
//        for (Schedule schedule : tourResult) {
//            int tourId = schedule.getTourId().getId();
//            ResponseEntity<List<Review>> responseServiceReview = rs.exchange(urlTour + tourId + "/reviews",
//                    HttpMethod.GET,
//                    null,
//                    new ParameterizedTypeReference<List<Review>>() {
//            });
//            List<Review> listReview = responseServiceReview.getBody();
//            model.addAttribute("scheduleReviewMap", listReview);
////            scheduleReviewMap.put(schedule.getId(), listReview);
//        }
        Map<Integer, Schedule> nearestScheduleMap = new HashMap<>();

        // Lấy ngày hiện tại
        Date currentDate = new Date();

        for (Schedule schedule : tourResult) {
            int tour = schedule.getTourId().getId();
            Date startDate = schedule.getStartDate();

            if (startDate.after(currentDate)) {

                if (!nearestScheduleMap.containsKey(tour) || startDate.before(nearestScheduleMap.get(tour).getStartDate())) {
                    nearestScheduleMap.put(tour, schedule);
                }
            }
        }

        Date date = new Date();
        List<Schedule> nearestSchedules = new ArrayList<>(nearestScheduleMap.values());

        ResponseEntity<List<Review>> scheduleReviewMap = rs.exchange(urlUser + "allreviews",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
        });
        List<Review> listReview = scheduleReviewMap.getBody();
        List<Review> reviewResult = new ArrayList<Review>();
        int regionID = 0;

        for (Schedule nearestSchedule : nearestSchedules) {
            int tourID = nearestSchedule.getTourId().getId();
            regionID = nearestSchedule.getTourId().getRegionId().getId();
            for (Review review : listReview) {
                if (review.getTourId().getId() == tourID) {
                    reviewResult.add(review);
                }
            }

        }
        
        String active = "active";

        model.addAttribute("active1", active);

        model.addAttribute("scheduleReviewMap", reviewResult);
        model.addAttribute("regionID", regionID);
        System.err.println(regionID);

//        model.addAttribute("totalCount", totalCount); // totalCount là một số nguyên (int) có giá trị hợp lệ
        model.addAttribute("tourList", nearestSchedules);
        model.addAttribute("date", date);

        model.addAttribute("tourResult", tourResult);

        model.addAttribute("countTour", countTour);
        return "user/destination";
    }

    @GetMapping("/destination/lowest_price/{regionId}")
    public String destination_filter(Model model, @PathVariable("regionId") int id, HttpSession session) {
//        model.addAttribute("attribute", "value");
        RestTemplate rs = new RestTemplate();
//        List<Region> result = rs.getForObject(urlTour + "region", List.class);
        ResponseEntity<List<Region>> responseRegion = rs.exchange(urlTour + "region",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Region>>() {
        });
        List<Region> result = responseRegion.getBody();
        for (Region re : result) {
            int reId = re.getId();
            if (reId == id) {
                model.addAttribute("regionName", re.getNameArea());
                model.addAttribute("regionDes", re.getDescription());
                model.addAttribute("regionImg", re.getImage());
//                model.addAttribute("tourList", re);

            }
        }

        model.addAttribute("listRegion", result);

//        ResponseEntity<List<Schedule>> responseEntity = rs.exchange(urlTour + "region" + "/tour",
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<Schedule>>() {
//        });
//        List<Schedule> tourList = responseEntity.getBody();
        List<Schedule> tourList = scheduleService.filterPrice();
//        ResponseEntity<List<Tour>> responseTour = rs.exchange(urlTour + "all",
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<Tour>>() {
//        });
//        List<Tour> resultTours = responseTour.getBody();
        int countTour = 0;
//        List<Schedule> tourResult = new ArrayList<Schedule>();
        List<Schedule> tourResult = new ArrayList<Schedule>();

        if (tourList != null) {
            for (Schedule item : tourList) {
//                
                if (item.getTourId().getRegionId().getId().equals(id) && item.getQuantityPassenger() < item.getQuantityMax()) {
                    tourResult.add(item);
                    countTour++;
                }
            }
        }

//        List<Review> reviewResult = new ArrayList<Review>();
//        Map<Integer, List<Review>> scheduleReviewMap = new HashMap<>();
//        for (Schedule schedule : tourResult) {
//            int tourId = schedule.getTourId().getId();
//            ResponseEntity<List<Review>> responseServiceReview = rs.exchange(urlTour + tourId + "/reviews",
//                    HttpMethod.GET,
//                    null,
//                    new ParameterizedTypeReference<List<Review>>() {
//            });
//            List<Review> listReview = responseServiceReview.getBody();
//            model.addAttribute("scheduleReviewMap", listReview);
////            scheduleReviewMap.put(schedule.getId(), listReview);
//        }
        Map<Integer, Schedule> nearestScheduleMap = new HashMap<>();

        // Lấy ngày hiện tại
        Date currentDate = new Date();

        for (Schedule schedule : tourResult) {
            int tour = schedule.getTourId().getId();
            Date startDate = schedule.getStartDate();

            if (startDate.after(currentDate)) {

                if (!nearestScheduleMap.containsKey(tour) || startDate.before(nearestScheduleMap.get(tour).getStartDate())) {
                    nearestScheduleMap.put(tour, schedule);
                }
            }
        }

        Date date = new Date();
        List<Schedule> nearestSchedules = new ArrayList<>(nearestScheduleMap.values());

        ResponseEntity<List<Review>> scheduleReviewMap = rs.exchange(urlUser + "allreviews",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Review>>() {
        });
        List<Review> listReview = scheduleReviewMap.getBody();
        List<Review> reviewResult = new ArrayList<Review>();
        int regionID = 0;

        for (Schedule nearestSchedule : nearestSchedules) {
            int tourID = nearestSchedule.getTourId().getId();
            regionID = nearestSchedule.getTourId().getRegionId().getId();
            for (Review review : listReview) {
                if (review.getTourId().getId() == tourID) {
                    reviewResult.add(review);
                }
            }

        }

        model.addAttribute("scheduleReviewMap", reviewResult);
        model.addAttribute("regionID", regionID);
        System.err.println(regionID);

        String active = "active";
//        model.addAttribute("totalCount", totalCount); // totalCount là một số nguyên (int) có giá trị hợp lệ
        model.addAttribute("tourList", tourResult);
        model.addAttribute("date", date);
        model.addAttribute("active", active);

        model.addAttribute("tourResult", tourResult);

        model.addAttribute("countTour", countTour);
        return "user/destination";
    }

    @RequestMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") int id, HttpSession session) {
//        Users u = (Users) session.getAttribute("user");
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class);
        ResponseEntity<Schedule> responseEntity = rs.exchange(urlTour + "schedule/" + id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Schedule>() {
        });
//            List<Schedule> tourShedule = responseEntity.getBody();

        Schedule tourShedule = responseEntity.getBody();
        List<Schedule> list = new ArrayList<>();
        list.add(tourShedule);

        // Create a map to associate each tourSI with its tourScheduleImage data
//        Map<Integer, List<Scheduleimage>> scheduleImageMap = new HashMap<>();
        int regionId = 0;
        for (Schedule schedule : list) {
            regionId = schedule.getTourId().getId();
            int scheduleId = schedule.getId();
            ResponseEntity<List<Serviceitem>> responseService = rs.exchange(urlTour + "scheduleId/" + scheduleId + "/serviceItems",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Serviceitem>>() {
            });
            List<Serviceitem> tourService = responseService.getBody();

            ResponseEntity<List<Scheduleitem>> responseScheduleItem = rs.exchange(urlTour + "scheduleId/" + scheduleId + "/scheduleItems",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Scheduleitem>>() {
            });
            List<Scheduleitem> tourScheduleItem = responseScheduleItem.getBody();

            List<Scheduleimage> listimg = new ArrayList<Scheduleimage>();
            Map<Integer, List<Scheduleimage>> scheduleImageMap = new HashMap<>();
            for (Scheduleitem scheduleI : tourScheduleItem) {
                int scheduleItemId = scheduleI.getId();
                ResponseEntity<List<Scheduleimage>> responseScheduleImage = rs.exchange(urlTour + "scheduleItemId/" + scheduleItemId + "/scheduleImages",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Scheduleimage>>() {
                });
                List<Scheduleimage> scheduleImages = responseScheduleImage.getBody();

                // Add the list of Scheduleimage to the existing listimg
//                listimg.addAll(scheduleImages);
                scheduleImageMap.put(scheduleI.getId(), scheduleImages);

            }

            model.addAttribute("scheduleImageMap", scheduleImageMap);

//            for (Scheduleimage scheduleimage : listimg) {
//                
//            }
            // Add the tourScheduleImage data to the scheduleImageMap using scheduleItemId as the key
//                scheduleImageMap.put(scheduleItemId, tourScheduleImage);
//            for (Scheduleitem scheduleitem : tourScheduleItem) {
//                
//            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(schedule.getStartDate());

            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(schedule.getEndDate());
            long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
            long duration = diffMillis / (24 * 60 * 60 * 1000);
            long duration1 = duration + 1;

            model.addAttribute("duration", duration);
            model.addAttribute("tourService", tourService);
            model.addAttribute("tourScheduleItem", tourScheduleItem);
            model.addAttribute("duration1", duration1);
        }

        ResponseEntity<List<Schedule>> responseRegionId = rs.exchange(urlTour + "tour/" + regionId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Schedule>>() {
        });
        List<Schedule> tourList = responseRegionId.getBody();

//        int countTour = 0;
//        List<Schedule> tourResult = new ArrayList<Schedule>();
//        List<Schedule> tourResult = new ArrayList<Schedule>();
//
//        if (tourList != null) {
//            for (Schedule item : tourList) {
////                
//                if (item.getTourId().getRegionId().getId().equals(6)) {
//                    tourResult.add(item);
//                    countTour++;
//                }
//            }
//        }
        model.addAttribute("tourList", tourList);

        // Add the scheduleImageMap to the model
        model.addAttribute("tourShedule", tourShedule);
        model.addAttribute("listRegion", result);
        String apiReview = "http://localhost:8888/api/tours/";
        List<Review> reviews = rs.getForObject(apiReview + regionId + "/reviews", List.class);
        model.addAttribute("listReviews", reviews);

        int idTour = 0;
//        List<Booking> bookings = getBookingForProfile(u, id);
//        for (Booking booking : bookings) {
//            idTour = booking.getScheduleId().getId();
//        }
//        model.addAttribute("tourId", idTour);
//        model.addAttribute("listBooking", bookings);
        model.addAttribute("idTour", id);
        return "user/detail";
    }

    @RequestMapping("/policy")
    public String policy(Model model) {
//        model.addAttribute("listRegion", result);
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class);
        model.addAttribute("listRegion", result);
        return "user/policy";
    }

    @RequestMapping("/booktour/{id}")
    public String booktour(Model model, RedirectAttributes redirectAttributes, @PathVariable("id") int id, HttpSession session, HttpServletRequest request) {
        Users UserSession = (Users) session.getAttribute("user");
        RestTemplate rs = new RestTemplate();
        ResponseEntity<List<Tour>> responseTour = rs.exchange(urlTour, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Tour>>() {
        });
        List<Tour> tour = responseTour.getBody();
//        for (Tour t : tour) {
//            if (t.getId() != id) {
//                return "redirect:/user/home";
//            }
//        }

        if (UserSession != null) {
//            if (UserSession.getRoleId().getId() == Role.CUSTOMER.getValue()) {

            ResponseEntity<Schedule> responseEntity = rs.exchange(urlTour + "schedule/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Schedule>() {
            });
//            List<Schedule> tourShedule = responseEntity.getBody();

            Schedule tourShedule = responseEntity.getBody();
            List<Schedule> list = new ArrayList<>();
            list.add(tourShedule);

            for (Schedule schedule : list) {
                int tourID = schedule.getTourId().getId();

                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(schedule.getStartDate());

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(schedule.getEndDate());
                long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
                long duration = diffMillis / (24 * 60 * 60 * 1000);
                long duration1 = duration + 1;

                model.addAttribute("duration", duration);

                model.addAttribute("duration1", duration1);
//                ResponseEntity<List<Schedule>> responseEntity1 = rs.exchange(urlTour + "tour/" + tourID,
//                        HttpMethod.GET,
//                        null,
//                        new ParameterizedTypeReference<List<Schedule>>() {
//                });
//                List<Schedule> tourShedule1 = responseEntity1.getBody();

            }

            List<Region> result = rs.getForObject(urlTour + "region", List.class);
            model.addAttribute("listRegion", result);
            model.addAttribute("tourShedule", list);

//                Users firstname = (Users) request.getSession().getAttribute("user");
//                Users UserSession = (Users) session.getAttribute("user");
//                model.addAttribute("session", UserSession.getRoleId().getId());
            return "user/booktour";
//            } else {
//                model.addAttribute("message", "Login Successful: ");
//                return "redirect:/admin/dashboard";
//            }
        } else {
            redirectUrl = "/user/booktour/" + id;
            model.addAttribute("message", "Please login before using the service. Thank you");

            return "user/login";
        }

    }

    @RequestMapping("/booktour/passenge/{id}")
    public String passenge(Model model, HttpSession session, @PathVariable("id") int id, RedirectAttributes redirectAttributes, HttpServletRequest request, @RequestParam(value = "count_adult", required = false) String count_adult, @RequestParam(value = "count_children", required = false) String count_children, @RequestParam(value = "count_baby", required = false) String count_baby, @RequestParam(value = "selected_option", required = false) String selectedOption, @RequestParam(value = "selected_option_payment", required = false) String selectedOptionPayment, @RequestParam(value = "check", required = false) String check) {
//        try {
        Users UserSession = (Users) session.getAttribute("user");

        if (UserSession != null) {
//            if (UserSession.getRoleId().getId() == Role.CUSTOMER.getValue()) {
//                HttpSession session = request.getSession(true);
            RestTemplate rs = new RestTemplate();
            int userID = UserSession.getId();
            model.addAttribute("userID", userID);

            if (count_adult == null || count_children == null || count_baby == null || selectedOptionPayment == null) {

                return "redirect:/user/booktour/" + id;
            }

            session.setAttribute("count_adult", count_adult);
            session.setAttribute("selectedOption", selectedOption);
            session.setAttribute("selectedOptionPayment", selectedOptionPayment);

            session.setAttribute("count_children", count_children);
            session.setAttribute("count_baby", count_baby);

            String adult = (String) session.getAttribute("count_adult");
            String children = (String) session.getAttribute("count_children");
            String baby = (String) session.getAttribute("count_baby");
            String optionguide = (String) session.getAttribute("selectedOption");
            String optionpayment = (String) session.getAttribute("selectedOptionPayment");

            if (adult.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "The number of adult passengers cannot be left empty");
                return "redirect:/user/booktour/" + id;
            } else if (check == null || !check.equals("agree") || check.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "You must accept the account of TourViet");
                redirectAttributes.addFlashAttribute("adult", adult);
                redirectAttributes.addFlashAttribute("children", children);
                redirectAttributes.addFlashAttribute("baby", baby);

                return "redirect:/user/booktour/" + id;
            } else {
                if (children.isEmpty()) {
                    children = "0";
                }

                if (baby.isEmpty()) {
                    baby = "0";
                }
                Integer adultcount = Integer.parseInt(adult);
                Integer childrentcount = Integer.parseInt(children);
                Integer babycount = Integer.parseInt(baby);

                int count_total = adultcount + childrentcount;

                List<String> count = new ArrayList<>();
                for (int i = 1; i <= adultcount; i++) {
                    count.add(String.valueOf(i));
                }

                List<Region> result = rs.getForObject(urlTour + "region", List.class);
//                    for (Region region : result) {

//                    }
                model.addAttribute("listRegion", result);

//                    Integer adultcount = Integer.parseInt(adult);
                model.addAttribute("adult", count);
                model.addAttribute("adult2", adultcount);

                model.addAttribute("children", childrentcount);
                model.addAttribute("baby", babycount);
                model.addAttribute("optionguide", optionguide);

                model.addAttribute("optionpayment", optionpayment);
//            int tourIdValue = Integer.parseInt(tourId);
                ResponseEntity<Schedule> responseEntity = rs.exchange(urlTour + "schedule/" + id,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Schedule>() {
                });
//            List<Schedule> tourShedule = responseEntity.getBody();

                Schedule tourShedule = responseEntity.getBody();

                if ((tourShedule.getQuantityMax() - tourShedule.getQuantityPassenger()) <= count_total) {
                    redirectAttributes.addFlashAttribute("message", "Too limit for allow");
                    return "redirect:/user/booktour/" + id;
                }
                List<Schedule> list = new ArrayList<>();
                list.add(tourShedule);

                for (Schedule schedule : list) {
                    int scheduleId = schedule.getId();
                    int regionId = schedule.getTourId().getRegionId().getId();
                    model.addAttribute("regionId", regionId);
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(schedule.getStartDate());

                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(schedule.getEndDate());
                    long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
                    long duration = diffMillis / (24 * 60 * 60 * 1000);
                    long duration1 = duration + 1;

                    BigDecimal priceAdult = schedule.getPrice();
                    BigDecimal priceChildren = priceAdult.divide(new BigDecimal(2));

                    BigDecimal total = priceAdult.multiply(new BigDecimal(adultcount)).add(priceChildren.multiply(new BigDecimal(childrentcount)));

                    model.addAttribute("total", total);

                    model.addAttribute("duration", duration);

                    model.addAttribute("duration1", duration1);
                    model.addAttribute("scheduleId", scheduleId);
                }

                model.addAttribute("tourShedule", list);
//                    StoreWithAnotherObject storeWithAnotherObject = new StoreWithAnotherObject();
//                    storeWithAnotherObject.setBook(new Booking());
//                    storeWithAnotherObject.setInfo(new Informationbooking());
//                    model.addAttribute("storeWithAnotherObject", storeWithAnotherObject);

                return "user/passenge";
            }
//            } else {
//                model.addAttribute("message", "Login Successful: ");
//                return "redirect:/admin/dashboard";
//            }
        } else {
            redirectUrl = "/user/booktour/" + id;
            model.addAttribute("message", "Please login before using the service. Thank you");

            return "user/login";
        }

    }

    @PostMapping("/booktour/payment/{id}")
    public String createBookingCashPayment(Model model, RedirectAttributes redirectAttributes, @PathVariable("id") int id, HttpServletRequest request, @RequestParam("formData") String formDataJson) {
        RestTemplate rs = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpSession session = request.getSession(true);
        Users userSession = (Users) session.getAttribute("user");
        int userID = userSession.getId();
        String tokenID = userSession.getToken();
        String urlCashPayment = urlPayment + "cash-payment/" + userID;
        String urlMomoPayment = urlPayment + "create-booking-v1/" + userID;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonDataInfo = objectMapper.readTree(formDataJson);

            JsonNode in4bookingRequestDataArray = jsonDataInfo.get("in4bookingRequest");

            for (JsonNode formData : in4bookingRequestDataArray) {
                String name = formData.get("name").asText();
                String address = formData.get("address").asText();

                if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Name and address cannot be left blank");
                    return "redirect:/user/booktour/passenge/" + id;
                }
            }

            StoreWithAnotherObject formData1 = objectMapper.readValue(formDataJson, StoreWithAnotherObject.class);
            BookingRequest bookingRequest = formData1.getBookingRequest();
            List<In4BookingRequest> in4BookingRequest = formData1.getIn4BookingRequest();

            String jsonData = objectMapper.writeValueAsString(formData1);
            System.out.println(jsonData);

            String optionpayment = (String) session.getAttribute("selectedOptionPayment");

            if (optionpayment.equals("cash,")) {
                headers.set("Authorization", "Bearer " + tokenID);
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, headers);
                ResponseEntity<Booking> response = rs.exchange(
                        urlCashPayment,
                        HttpMethod.POST,
                        requestEntity,
                        Booking.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    // Gửi email xác nhận đặt tour
                    userService.saveFeedback(userSession.getEmail(), id);
                }
                List<Booking> listBooking = userService.getAllBooking();
                for (Booking list : listBooking) {
                    if (list.getScheduleId().getId() == id) {
                        model.addAttribute("listBooking", list);
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(list.getScheduleId().getStartDate());

                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.setTime(list.getScheduleId().getEndDate());
                        long diffMillis = calendar2.getTimeInMillis() - calendar1.getTimeInMillis();
                        long duration = diffMillis / (24 * 60 * 60 * 1000);
                        long duration1 = duration + 1;

//                    BigDecimal price = list.getScheduleId().getPrice() * 100;
//                    BigDecimal price1 = list.getScheduleId().getPrice();
//                    BigDecimal pricechildren = price1 / 2 * 100;
//                    BigDecimal pricechildren1 = price1 / 2;
//
//                    BigDecimal total = (list.getAdult() * price1) + (list.getChildren() * pricechildren1);
//                    BigDecimal totalend = total * 100;
//                    String formattedTotal = String.format("%.2f", totalend / 100.0);
//                    String formattedPrice = String.format("%.2f", price / 100.0);
//                    String formattedPrice1 = String.format("%.2f", pricechildren / 100.0);
                        BigDecimal price = list.getScheduleId().getPrice().multiply(new BigDecimal(100));
                        BigDecimal price1 = list.getScheduleId().getPrice();
                        BigDecimal pricechildren = price1.divide(new BigDecimal(2)).multiply(new BigDecimal(100));
                        BigDecimal pricechildren1 = price1.divide(new BigDecimal(2));

                        BigDecimal total = (new BigDecimal(list.getAdult()).multiply(price1))
                                .add(new BigDecimal(list.getChildren()).multiply(pricechildren1));

                        BigDecimal totalend = total.multiply(new BigDecimal(100));
                        String formattedTotal = totalend.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
                        String formattedPrice = price.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
                        String formattedPrice1 = pricechildren.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();

//                    long priceAdult = list.getScheduleId().getTourId().getPrice();
//                    long priceChildren = priceAdult / 2;
//                    long total = (priceAdult * list.getAdult()) + (priceChildren * list.getChildren());
                        model.addAttribute("formattedTotal", formattedTotal);
                        model.addAttribute("formattedPrice", formattedPrice);
                        model.addAttribute("formattedPrice1", formattedPrice1);

//                    model.addAttribute("total", total);
                        model.addAttribute("duration", duration);

                        model.addAttribute("duration1", duration1);

                    }
                }
                List<Region> result = rs.getForObject(urlTour + "region", List.class);
                model.addAttribute("listRegion", result);
                if (userSession != null) {

                    String firstName = userSession.getFirstName();
                    String lastName = userSession.getLastName();

                    String address = userSession.getAddress();
                    String phone = userSession.getPhone();
                    String email = userSession.getEmail();

                    model.addAttribute("sessionFName", firstName);
                    model.addAttribute("sessionLName", lastName);

                    model.addAttribute("sessionAddress", address);
                    model.addAttribute("sessionPhone", phone);
                    model.addAttribute("sessionEmail", email);

                }
                return "user/payment";
            } else {
                headers.set("Authorization", "Bearer " + tokenID);
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, headers);
                ResponseEntity<MoMoPaymentResponse> response1 = rs.exchange(
                        urlMomoPayment,
                        HttpMethod.POST,
                        requestEntity,
                        MoMoPaymentResponse.class
                );
                if (response1.getStatusCode() == HttpStatus.OK) {
                    MoMoPaymentResponse responsePayment = response1.getBody();
                    String urlmomo = responsePayment.getPayUrl();

                    System.out.println("URL from momo.getPayUrl(): " + urlmomo);
                    System.out.println("URL from momo.getPayUrl(): " + optionpayment);

//                    session.setAttribute("urlmomo", urlmomo);
//                    redirectAttributes.addFlashAttribute("urlmomo", urlmomo);
//                    return "redirect:/user/redirect";
                    redirectAttributes.addFlashAttribute("urlmomo", urlmomo);
                    return "redirect:/user/redirect";

                }
                List<Region> result = rs.getForObject(urlTour + "region", List.class
                );
                model.addAttribute("listRegion", result);
                return "user/contact";

            }

        } catch (Exception e) {
            e.printStackTrace();
            List<Region> result = rs.getForObject(urlTour + "region", List.class);
            model.addAttribute("listRegion", result);
            return "user/home";
        }
    }

    @GetMapping("/redirect")
    public String handleRedirect(@ModelAttribute("urlmomo") String urlmomo) {

        return "redirect:" + urlmomo;
    }

    @GetMapping("/payment/callback")
//    @ResponseBody
    public String handleCallback(HttpSession session, @RequestParam String partnerCode, @RequestParam String accessKey, @RequestParam String requestId, @RequestParam String amount, @RequestParam String orderId, @RequestParam String orderInfo, @RequestParam String orderType, @RequestParam String transId, @RequestParam String message, @RequestParam String localMessage, @RequestParam String responseTime, @RequestParam String errorCode, @RequestParam String payType, @RequestParam String extraData, @RequestParam String signature) throws NoSuchAlgorithmException {
        String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
        String rawHash = "partnerCode=" + partnerCode + "&accessKey=" + accessKey + "&requestId=" + requestId + "&amount=" + amount + "&orderId=" + orderId + "&orderInfo=" + orderInfo + "&orderType=" + orderType + "&transId=" + transId + "&message=" + message + "&localMessage=" + localMessage + "&responseTime=" + responseTime + "&errorCode=" + errorCode + "&payType=" + payType + "&extraData=" + extraData;
        String calculatedSignature = null;
        try {
            calculatedSignature = HashUtils.signSHA256(rawHash, secretKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            return "Error calculating signature";
        }
        if (!signature.equals(calculatedSignature)) {
            return "Invalid signature";
        }
        if ("0".equals(errorCode)) {
//            HttpSession session;
            Users u = (Users) session.getAttribute("user");
            int proId = u.getId();
            return "redirect:/profile/account/" + proId;
//            return "user/home";
        } else {
            return "Payment failed";
        }
    }

    private void sendBookingConfirmationEmail(String userEmail, int bookingId) {
        // Logic gửi email của bạn ở đây
        // Phương thức này nên chịu trách nhiệm gửi email cho người dùng với thông tin đặt tour
        // Hãy chắc chắn triển khai logic để lấy thông tin đặt tour dựa trên bookingId
        // và sau đó gửi email cho userEmail.
    }

    @GetMapping("/booktour/payment/{id}")
    public String handleGetRequest(Model model, RedirectAttributes redirectAttributes, @PathVariable("id") Long id) {
        if (model.containsAttribute("error")) {
            String error = (String) model.getAttribute("error");
            redirectAttributes.addFlashAttribute("message", error);
        }
        return "redirect:/user/booktour/" + id;
    }

    @RequestMapping("/booktour/confirm/{id}")
    public String confirm(Model model, HttpSession session, @PathVariable("id") int id
    ) {
        Users UserSession = (Users) session.getAttribute("user");
        if (UserSession == null) {
            redirectUrl = "/user/booktour/confirm/" + id;
            model.addAttribute("message", "Please login before using the service. Thank you");

            return "user/login";
        }
        List<Payment> resultB = getPaymentForProfile(UserSession, UserSession.getId());
//        model.addAttribute("listPayment", resultB);
        List<Payment> list = new ArrayList<>();
        for (Payment payment : resultB) {
            if (payment.getBookingId().getId().equals(id)) {
                if (payment.getStatus() == 0) {
                    return "redirect:/profile/account/" + UserSession.getId();
                }

                list.add(payment);
            }
        }

        List<Informationbooking> info = userService.getInfo(id);

        model.addAttribute("listPayment", list);
        model.addAttribute("info", info);

        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );
        model.addAttribute("listRegion", result);
        return "user/confirm";
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

    @RequestMapping("/create-tour")
    public String create_tour(Model model, HttpSession session
    ) {

        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );
        List<Address> resultAdd = rs.getForObject(urlTour + "address/region", List.class
        );
        model.addAttribute("listRegion", result);
        model.addAttribute("listAddress", resultAdd);

        return "user/create_tour";

    }

    @GetMapping("/getTouristData")
    @ResponseBody
    public ResponseEntity<List<Address>> getTouristData(@RequestParam int regionId) {
        RestTemplate rs = new RestTemplate();
        List<Address> resultAdd = rs.getForObject(urlTour + "address/region/" + regionId, List.class
        );
        return ResponseEntity.ok(resultAdd);
    }

    @PostMapping("/create-tour")
    public String create_tour(Model model,
            HttpSession session,
            //            HttpRequest request,
            @RequestParam("region") String region,
            @RequestParam("accommodation") String accommodation,
            @RequestParam("transport") String transport,
            @RequestParam("count_adult") String count_adult,
            @RequestParam("count_children") String count_children,
            @RequestParam("count_baby") String count_baby,
            //            @RequestParam("input_region") String input_region,
            @RequestParam("input_note") String input_note,
            @RequestParam("fullname") String fullname,
            @RequestParam("email") String email,
            @RequestParam("price") String price,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("startDay") String startDay,
            @RequestParam("endDay") String endDay,
            @RequestParam("destination") String destination,
            @RequestParam("inputFields") String inputFields,
            @RequestParam("selected_option") String selected_option
    //            @RequestBody FormData formData
    ) {

        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );
        model.addAttribute("listRegion", result);
        model.addAttribute("region", region);
//        model.addAttribute("input_region", input_region);
        model.addAttribute("input_note", input_note);
        model.addAttribute("selected_option", selected_option);

        model.addAttribute("accommodation", accommodation);

        if (count_children == null || count_children.isEmpty()) {
            model.addAttribute("count_children", 0);
        } else {
            model.addAttribute("count_children", count_children);
        }

        if (count_baby == null || count_baby.isEmpty()) {
            model.addAttribute("count_baby", 0);
        } else {
            model.addAttribute("count_baby", count_baby);
        }
        model.addAttribute("count_adult", count_adult);
        model.addAttribute("transport", transport);
        model.addAttribute("fullname", fullname);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate1 = dateFormat.parse(startDay);
            Date endDate1 = dateFormat.parse(endDay);

            model.addAttribute("startDay", startDate1);
            model.addAttribute("endDay", endDate1);

        } catch (ParseException e) {
            // Handle the ParseException appropriately, e.g., log the error or show a user-friendly message.
        }

// Set the Date objects as attributes in the model
        model.addAttribute("address", address);

        model.addAttribute("destination", destination);

//        String[] values = inputFields.split(", ");
        model.addAttribute("values", inputFields);
        model.addAttribute("price", price);

        TourCreate tour = new TourCreate();
        tour.setFullname(fullname);
        tour.setEmail(email);
        tour.setPhone(phone);
        tour.setAddress(address);

        if (count_adult != null && !count_adult.isEmpty()) {
            try {
                tour.setAdult(Integer.valueOf(count_adult));
            } catch (NumberFormatException e) {

            }
        } else {

        }

        if (count_children != null && !count_children.isEmpty()) {
            try {
                tour.setChildren(Integer.valueOf(count_children));

            } catch (NumberFormatException e) {

            }
        } else {
            tour.setChildren(0);
        }

        if (count_baby != null && !count_baby.isEmpty()) {
            try {
                tour.setBaby(Integer.valueOf(count_baby));

            } catch (NumberFormatException e) {

            }
        } else {
            tour.setBaby(0);
        }

//        if (region == null) {
//            tour.setRegion(input_region);
//        } else {
        tour.setRegion(region);
//        }

        tour.setAccommodation(accommodation);
        tour.setNote(input_note);
        BigDecimal priceconvert = new BigDecimal(price);

        tour.setPrice(priceconvert);

        boolean tourguide;
        if (selected_option.equals("tour_guide,")) {
            tourguide = true;
        } else {
            tourguide = false;
        }
        tour.setTourguide(tourguide);
        tour.setTransport(transport);
        tour.setDestination(destination);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = dateFormat.parse(startDay);
            Date endDate = dateFormat.parse(endDay);

            tour.setStartdate(startDate);
            tour.setEnddate(endDate);

        } catch (ParseException e) {
            // Handle the ParseException appropriately, e.g., log the error or show a user-friendly message.
        }

        tour.setDescription(inputFields);

        tourService.save(tour);

        return "user/confirm_create";
    }

    @RequestMapping("/confirm_create")
    public String confirm_create(Model model, HttpSession session
    ) {
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );

        String region = (String) session.getAttribute("region");
        String accommodation = (String) session.getAttribute("accommodation");
        String transport = (String) session.getAttribute("transport");
        String count_adult = (String) session.getAttribute("count_adult");
        String count_children = (String) session.getAttribute("count_children");
        String count_baby = (String) session.getAttribute("count_baby");
//        String input_region = (String)session.getAttribute("input_region");
//        String input_accommodation = (String)session.getAttribute("input_accommodation");
        String fullname = (String) session.getAttribute("fullname");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        String address = (String) session.getAttribute("address");
        String startDay = (String) session.getAttribute("startDay");
        String endDay = (String) session.getAttribute("endDay");
        String destination = (String) session.getAttribute("destination");
        String inputFields = (String) session.getAttribute("inputFields");

        model.addAttribute("region", region);
        model.addAttribute("accommodation", accommodation);
        model.addAttribute("transport", transport);
        model.addAttribute("count_adult", count_adult);
        model.addAttribute("count_children", count_children);
        model.addAttribute("count_baby", count_baby);
        model.addAttribute("fullname", fullname);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("address", address);
        model.addAttribute("startDay", startDay);
        model.addAttribute("endDay", endDay);
        model.addAttribute("destination", destination);
        model.addAttribute("inputFields", inputFields);

        model.addAttribute("listRegion", result);
        return "user/confirm_create";
    }

    @PostMapping("/delete/booking/{bookingId}")
    public String deleteBooking(@PathVariable int bookingId) {

        String deleteUrl = urlAdmin + "booking/" + bookingId;
        RestTemplate rs = new RestTemplate();
        rs
                .exchange(deleteUrl, HttpMethod.DELETE, null, String.class
                );

        return "redirect:/user/home";  // Chuyển hướng về trang chủ
    }

    @RequestMapping("/about")
    public String about(Model model
    ) {
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );
        model.addAttribute("listRegion", result);
        return "user/about";
    }

    @RequestMapping("/contact")
    public String contact(Model model
    ) {
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );
        model.addAttribute("listRegion", result);
        return "user/contact";
    }

    @RequestMapping("/service")
    public String service(Model model
    ) {
        RestTemplate rs = new RestTemplate();
        List<Region> result = rs.getForObject(urlTour + "region", List.class
        );
        model.addAttribute("listRegion", result);
        return "user/service";
    }

}
