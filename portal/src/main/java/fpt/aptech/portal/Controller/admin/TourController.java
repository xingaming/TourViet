package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Region;
import fpt.aptech.portal.entities.Tour;
import fpt.aptech.portal.entities.TourCreate;
import fpt.aptech.portal.entities.Transport;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import fpt.aptech.portal.service.user.TourService;
import jakarta.annotation.Nullable;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/admin")
public class TourController {

    @Autowired
    private TourService tourService;

    @RequestMapping("/tour")
    public String tour(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }

        Users user = (Users) session.getAttribute("user");

        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Kiểm tra điều kiện
        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            String apiUrl = "http://localhost:8888/api/tours/";

            ResponseEntity<Tour[]> response = restTemplate.getForEntity(apiUrl, Tour[].class);
            Tour[] tours = response.getBody();

            model.addAttribute("tours", tours);

        } else if ((user.getRoleId().getId() == Role.HQ.getValue()) || (user.getRoleId().getId() == Role.AREA.getValue())) {
            // API endpoint URL with {userId} as a placeholder for the actual userId value
            String apiUrl = "http://localhost:8888/api/admin/{userId}/tours";

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
                    new ParameterizedTypeReference<List<Tour>>() {
            }
            );
            // Get the list of tours from the response body
            List<Tour> tours = (List<Tour>) response.getBody();

            // Lưu danh sách các Tour vào model để sử dụng trong view
            model.addAttribute("tours", tours);
        }

        return "admin/tour/tour";
    }

    @RequestMapping("/tour_create")

    public String tour_create(Model model, HttpSession session) {
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }
        RestTemplate rs = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/made_tours";
        ResponseEntity<List<TourCreate>> responseEntity = rs.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TourCreate>>() {
        }
        );

        Users user = (Users) session.getAttribute("user");
        int comId = user.getCompanyId().getId();
        List<TourCreate> resultTourCreate = responseEntity.getBody();
        model.addAttribute("resultTourCreate", resultTourCreate);
        model.addAttribute("comId", comId);

        System.out.println(resultTourCreate);
        return "admin/made_tour/tour";
    }

    @RequestMapping("/tour_create/create/{tourId}")
    public String detail(Model model, HttpSession session, @PathVariable("tourId") int tourId) {
        RestTemplate rs = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/made_tours/" + tourId;
        ResponseEntity<TourCreate> responseEntity = rs.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TourCreate>() {
        }
        );

        TourCreate resultTourCreate = responseEntity.getBody();
        Users user = (Users) session.getAttribute("user");

        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            // Gọi API và lấy dữ liệu từ các đường dẫn
            List<Region> regions = getRegions();
            List<Transport> transports = getTransports();
            List<Users> users = getUsersHQ(user);

            // Gửi dữ liệu lấy được từ API tới view thông qua Model
            model.addAttribute("regions", regions);
            model.addAttribute("transports", transports);
            model.addAttribute("users", users);
            model.addAttribute("resultTour", resultTourCreate);

            return "admin/made_tour/view-detail";
        }
        List<Region> regions = getRegions();
        List<Transport> transports = getTransports();
        List<Users> users = getUsersArea(user);

        // Gửi dữ liệu lấy được từ API tới view thông qua Model
        model.addAttribute("regions", regions);
        model.addAttribute("transports", transports);
        model.addAttribute("users", users);
        model.addAttribute("resultTour", resultTourCreate);

        System.out.println(resultTourCreate);
        return "admin/made_tour/view-detail";
    }
    
    @RequestMapping("/tour_create/detail/{tourId}")
    public String view_detail(Model model, HttpSession session, @PathVariable("tourId") int tourId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }
        RestTemplate rs = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/made_tours/" + tourId;
        ResponseEntity<TourCreate> responseEntity = rs.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TourCreate>() {
        }
        );

        TourCreate resultTourCreate = responseEntity.getBody();
        

        
        model.addAttribute("resultTour", resultTourCreate);

        System.out.println(resultTourCreate);
        return "admin/made_tour/detail";
    }

    @RequestMapping(value = "/tour_create", method = RequestMethod.POST)
    public String createTour(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price_adult") String price_adult,
            @RequestParam("price_children") String price_children,
            @RequestParam(name = "guideId", required = false) String guideId,
            @RequestParam(name = "fullname", required = false) String fullname,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "accommodation", required = false) String accommodation,
            @RequestParam(name = "price", required = false) String price,
            @RequestParam(name = "adult", required = false) String adult,
            @RequestParam(name = "children", required = false) String children,
            @RequestParam(name = "baby", required = false) String baby,
            @RequestParam(name = "startdate", required = false) String startdate,
            @RequestParam(name = "enddate", required = false) String enddate,
            @RequestParam(name = "note", required = false) String note,
            @RequestParam(name = "tourId", required = false) String tourId,
            @RequestParam(name = "destination", required = false) String destination
    ) {
        Users user = (Users) session.getAttribute("user");
//        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
//            return "/admin/error403";
//        }
        int tourID = Integer.parseInt(tourId);
        int countadult = Integer.parseInt(adult);
        int countchildren = Integer.parseInt(children);
        int countbaby = Integer.parseInt(baby);

        if (name.isEmpty() && description.isEmpty() && price_adult.isEmpty() && price_children.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("errorMessage1", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("errorMessage2", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("errorMessage3", "cannot be left blank.");

            return "redirect:/admin/tour_create/detail/" + tourID;
        }

        if (name.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("price_adult", price_adult);
            redirectAttributes.addFlashAttribute("price_children", price_children);

            return "redirect:/admin/tour_create/detail/" + tourID;
        }

        if (description.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage1", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("price_adult", price_adult);
            redirectAttributes.addFlashAttribute("price_children", price_children);

            return "redirect:/admin/tour_create/detail/" + tourID;
        }

        if (price_adult.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage2", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("price_children", price_children);

            return "redirect:/admin/tour_create/detail/" + tourID;
        }

        if (price_children.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage2", "cannot be left blank.");
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("price_adult", price_adult);

            return "redirect:/admin/tour_create/detail/" + tourID;
        }

        RestTemplate rs = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/admin/made_tours/" + tourID;
        ResponseEntity<TourCreate> responseEntity = rs.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<TourCreate>() {
        }
        );

        TourCreate resultTourCreate = responseEntity.getBody();
        int companyId = user.getCompanyId().getId();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = dateFormat.parse(startdate);
            Date endDate = dateFormat.parse(enddate);

            resultTourCreate.setStartdate(startDate);
            resultTourCreate.setEnddate(endDate);

        } catch (ParseException e) {
            // Handle the ParseException appropriately, e.g., log the error or show a user-friendly message.
        }

        resultTourCreate.setAccommodation(accommodation);
        resultTourCreate.setAddress(address);
        resultTourCreate.setAdult(countadult);
        resultTourCreate.setBaby(countbaby);
        resultTourCreate.setChildren(countchildren);
        resultTourCreate.setCompanyId(companyId);
        resultTourCreate.setDescription(description);
        resultTourCreate.setDestination(name);
        resultTourCreate.setEmail(email);
        resultTourCreate.setFullname(fullname);
        resultTourCreate.setNote(note);
        resultTourCreate.setPhone(phone);
        BigDecimal priceconvert = new BigDecimal(price_adult);

        resultTourCreate.setPrice(priceconvert);
        resultTourCreate.setRegion(region);

        if (guideId == null || guideId.isEmpty()) {
            resultTourCreate.setTourguide(false);
        } else {
            resultTourCreate.setTourguide(true);
        }

        tourService.save(resultTourCreate);

        tourService.saveFeedback(email, tourID);

        return "redirect:/admin/tour_create";
    }

    @RequestMapping("/create-tour")
    public String createTour(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Lấy thông báo lỗi nếu có
        if (model.containsAttribute("message")) {
            String message = (String) model.asMap().get("message");
            model.addAttribute("message", message);
        }

        // Lấy thông báo lỗi nếu có
        if (model.containsAttribute("errorMessage")) {
            String errorMessage = (String) model.asMap().get("errorMessage");
            model.addAttribute("errorMessage", errorMessage);
        }

        // Kiểm tra xem có tour trong flash attribute không
        if (redirectAttributes.containsAttribute("tour")) {
            Tour tour = (Tour) redirectAttributes.getFlashAttributes().get("tour");
            // Sử dụng giá trị của tour ở đây
            model.addAttribute("tour", tour); // Thêm tour vào model để truyền vào view
        }

        // Role HQ
        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            // Gọi API và lấy dữ liệu từ các đường dẫn
            List<Region> regions = getRegions();
            List<Transport> transports = getTransports();
            List<Users> users = getUsersHQ(user);

            // Gửi dữ liệu lấy được từ API tới view thông qua Model
            model.addAttribute("regions", regions);
            model.addAttribute("transports", transports);
            model.addAttribute("users", users);

            return "admin/tour/create-tour";
        }
        //Role area
        // Gọi API và lấy dữ liệu từ các đường dẫn
        List<Region> regions = getRegions();
        List<Transport> transports = getTransports();
        List<Users> users = getUsersArea(user);

        // Gửi dữ liệu lấy được từ API tới view thông qua Model
        model.addAttribute("regions", regions);
        model.addAttribute("transports", transports);
        model.addAttribute("users", users);
        return "admin/tour/create-tour";
    }

    @RequestMapping(value = "/create-tour", method = RequestMethod.POST)
    public String createTour(Model model, HttpSession session, @Nullable @ModelAttribute("Tour") Tour tour, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (tour.getName().isEmpty() || tour.getDescription().isEmpty() || tour.getDiscount() == null || (tour.getImage().isEmpty() && imageFile.isEmpty())) {
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    tour.setImage(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            redirectAttributes.addFlashAttribute("tour", tour);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-tour";
        }

        // Set giá trị cho tour
        tour.setCompanyId(user.getCompanyId());
        tour.setViews(0);
        tour.setIsTopPick(false);
        tour.setRate(null);

        // Kiểm tra xem người dùng đã chọn tệp ảnh hay chưa
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                tour.setImage(base64Image);
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
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Tour> requestEntity = new HttpEntity<>(tour, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Tour> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{userId}/tours",
                HttpMethod.POST,
                requestEntity,
                Tour.class,
                user.getId()
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Tour createdTour = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully created a new tour.");
            return "redirect:/admin/tour";
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping("/edit-tour/{tourId}")
    public String editTour(@PathVariable("tourId") Integer tourId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Lấy thông báo lỗi nếu có
        if (model.containsAttribute("message")) {
            String message = (String) model.asMap().get("message");
            model.addAttribute("message", message);
        }

        // Lấy thông báo lỗi nếu có
        if (model.containsAttribute("errorMessage")) {
            String errorMessage = (String) model.asMap().get("errorMessage");
            model.addAttribute("errorMessage", errorMessage);
        }

        // Role HQ
        if (user.getRoleId().getId() == Role.HQ.getValue()) {
            // Gọi API để lấy thông tin tour cần chỉnh sửa từ tourId
            Tour tour = getTourById(tourId);

            // Gọi API để lấy dữ liệu từ các đường dẫn cần thiết
            List<Region> regions = getRegions();
            List<Transport> transports = getTransports();
            List<Users> users = getUsersHQ(user);

            // Gửi dữ liệu lấy được từ API tới view thông qua Model
            if (!redirectAttributes.containsAttribute("tour")) {
                // Sử dụng giá trị của tour ở đây
                model.addAttribute("tour", tour);
            }
            // Kiểm tra xem có tour trong flash attribute không
            if (session.getAttribute("tour") != null) {
                tour = (Tour) session.getAttribute("tour");
                // Sử dụng giá trị của tour ở đây
                model.addAttribute("tour", tour); // Thêm tour vào model để truyền vào view
                session.removeAttribute("tour");
            }
            model.addAttribute("regions", regions);
            model.addAttribute("transports", transports);
            model.addAttribute("users", users);

            return "admin/tour/edit-tour";
        }

        // Role AREA
        // Gọi API để lấy thông tin tour cần chỉnh sửa từ tourId
        Tour tour = getTourById(tourId);

        // Gọi API để lấy dữ liệu từ các đường dẫn cần thiết
        List<Region> regions = getRegions();
        List<Transport> transports = getTransports();
        List<Users> users = getUsersArea(user);

        // Gửi dữ liệu lấy được từ API tới view thông qua Model
        model.addAttribute("tour", tour);
        // Kiểm tra xem có tour trong flash attribute không
        if (session.getAttribute("tour") != null) {
            tour = (Tour) session.getAttribute("tour");
            // Sử dụng giá trị của tour ở đây
            model.addAttribute("tour", tour); // Thêm tour vào model để truyền vào view
            session.removeAttribute("tour");
        }
        model.addAttribute("regions", regions);
        model.addAttribute("transports", transports);
        model.addAttribute("users", users);

        return "admin/tour/edit-tour";
    }

    @RequestMapping(value = "/edit-tour", method = RequestMethod.POST)
    public String editTour(@RequestParam("tourId") Integer tourId, Model model, HttpSession session, @Nullable @ModelAttribute("Tour") Tour tour, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (tour.getName().isEmpty() || tour.getDescription().isEmpty() || tour.getDiscount() == null || (tour.getImage().isEmpty() && imageFile.isEmpty())) {
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    tour.setImage(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            session.setAttribute("tour", tour);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/edit-tour/" + tourId;
        }

        // Lấy giá trị của tour để kiểm tra
        Tour existedTour = getTourById(tourId);
        // Set giá trị cho tour
        tour.setCompanyId(user.getCompanyId());
        tour.setViews(existedTour.getViews());
        tour.setIsTopPick(existedTour.getIsTopPick());
        tour.setRate(existedTour.getRate());
        tour.setImage(existedTour.getImage());

        // validate xem đã đổi dữ liệu chưa
        if (existedTour.getName().equals(tour.getName()) && existedTour.getDescription().equals(tour.getDescription()) && existedTour.getDiscount().equals(tour.getDiscount()) && existedTour.getGuideId().getId().equals(tour.getGuideId().getId()) && existedTour.getRegionId().getId().equals(tour.getRegionId().getId()) && existedTour.getTransportId().getId().equals(tour.getTransportId().getId()) && (existedTour.getImage().equals(tour.getImage()) && imageFile.isEmpty())) {
            // Xử lý khi có sự thay đổi
            redirectAttributes.addFlashAttribute("message", "Nothing has changed in your edits.");
            return "redirect:/admin/edit-tour/" + tourId;
        }

        // Kiểm tra xem người dùng đã chọn tệp ảnh hay chưa
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                tour.setImage(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                model.addAttribute("errorMessage", "An error occurred while processing the image.");
                return "admin/tour/edit-tour";
            }
        }

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Tour> requestEntity = new HttpEntity<>(tour, headers);

        // Gọi API bằng phương thức PUT hoặc PATCH và nhận kết quả trả về
        ResponseEntity<Tour> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{userId}/tours/{tourId}",
                HttpMethod.PUT, // Hoặc PATCH nếu API hỗ trợ
                requestEntity,
                Tour.class,
                user.getId(),
                tourId // Chú ý truyền tourId vào URL cho việc chỉnh sửa
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được cập nhật từ kết quả trả về
            Tour updatedTour = response.getBody();
            // Xử lý các công việc khác sau khi cập nhật thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully updated a new tour.");
            return "redirect:/admin/tour";
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping(value = "/delete-tour/{tourId}", method = RequestMethod.GET)
    public String deleteTour(@PathVariable("tourId") Integer tourId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        if (tourId == null) {
            return "/admin/error505";
        }

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Tour> requestEntity = new HttpEntity<>(headers);

        // Gọi API bằng phương thức DELETE và nhận kết quả trả về
        ResponseEntity<Tour> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{userId}/tours/{tourId}",
                HttpMethod.DELETE,
                requestEntity,
                Tour.class,
                user.getId(),
                tourId
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour đã bị xóa từ kết quả trả về (tùy thuộc vào API server trả về thông tin tour sau khi xóa)
            Tour deletedTour = response.getBody();

            // Xử lý các công việc khác sau khi xóa thành công (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully deleted the tour.");
            return "redirect:/admin/tour";
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping("/view-tour/{tourId}")
    public String viewTourDetail(@PathVariable("tourId") Integer tourId, Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue() && user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        if (tourId == null) {
            return "/admin/error505";
        }

        // Gọi service để lấy thông tin chi tiết tour theo tourId
        Tour tour = getTourById(tourId);
        if (tour == null) {
            return "/admin/error404"; // Hoặc điều hướng đến trang lỗi 404
        }

        // Đưa thông tin chi tiết tour vào model để hiển thị lên view
        model.addAttribute("tour", tour);

        // Redirect hoặc trả về view tùy theo logic của ứng dụng
        return "admin/tour/view-detail";
    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
    }

    private List<Users> getUsersHQ(Users user) {
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

    private List<Users> getUsersArea(Users user) {
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

    private List<Region> getRegions() {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Region>> response = restTemplate.exchange(
                "http://localhost:8888/api/tours/region",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Region>>() {
        }
        );
        return response.getBody();
    }

    private List<Transport> getTransports() {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Transport>> response = restTemplate.exchange(
                "http://localhost:8888/api/transports",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Transport>>() {
        }
        );
        return response.getBody();
    }

    private Tour getTourById(Integer tourId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Tour> response = restTemplate.exchange(
                "http://localhost:8888/api/tours/{tourId}",
                HttpMethod.GET,
                null,
                Tour.class, // Chỉ định kiểu trả về là Tour.class
                tourId // Truyền giá trị cho tham số {tourId} trong URL
        );

        return response.getBody();
    }

    // Khai báo InitBinder
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Chuyển đổi String thành kiểu Integer cho các trường 'guideId', 'regionId', và 'transportId'
        binder.registerCustomEditor(Users.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                // Chuyển đổi String thành Integer và set giá trị cho trường 'guideId'
                Integer guideId = Integer.valueOf(text);
                setValue(new Users(guideId));
            }
        });

        binder.registerCustomEditor(Region.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                // Chuyển đổi String thành Integer và set giá trị cho trường 'regionId'
                Integer regionId = Integer.valueOf(text);
                setValue(new Region(regionId));
            }
        });

        binder.registerCustomEditor(Transport.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                // Chuyển đổi String thành Integer và set giá trị cho trường 'transportId'
                Integer transportId = Integer.valueOf(text);
                setValue(new Transport(transportId));
            }
        });
    }

    private String convertImageToBase64(byte[] imageBytes) {
        // Chuyển đổi mảng byte của hình ảnh thành mã Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(200000000L));
        factory.setMaxRequestSize(DataSize.ofBytes(200000000L));
        return factory.createMultipartConfig();
    }

}
