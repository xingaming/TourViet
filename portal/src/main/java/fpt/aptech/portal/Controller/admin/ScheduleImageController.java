package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Scheduleimage;
import fpt.aptech.portal.entities.Scheduleitem;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
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
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/admin")
public class ScheduleImageController {

    @RequestMapping("/scheduleImage/{scheduleItemId}")
    public String scheduleItem(Model model, HttpSession session, @PathVariable("scheduleItemId") Integer scheduleItemId) {
        if (!checkAuth(session)) {
            return "/admin/error403";
        }

        Users user = (Users) session.getAttribute("user");
        RestTemplate restTemplate = new RestTemplate();

        model.addAttribute("scheduleItemId", scheduleItemId);

        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            String apiUrl = "http://localhost:8888/api/tours/scheduleItemId/{scheduleItemId}/scheduleImages";

            try {
                ResponseEntity<Scheduleimage[]> response = restTemplate.getForEntity(apiUrl, Scheduleimage[].class, scheduleItemId);
                Scheduleimage[] scheduleImages = response.getBody();
                model.addAttribute("scheduleImages", scheduleImages);
            } catch (RuntimeException e) {
                model.addAttribute("message", "There are currently no schedules.");
                model.addAttribute("scheduleImages", null);
            }

        } else if (user.getRoleId().getId() == Role.HQ.getValue() || user.getRoleId().getId() == Role.AREA.getValue()) {
            String apiUrl = "http://localhost:8888/api/admin/{adminId}/scheduleImages/scheduleItem/{scheduleItemId}";

            // detail http://localhost:8888/api/admin/2/scheduleImages/6
            // create http://localhost:8888/api/admin/2/scheduleImages
            // update http://localhost:8888/api/admin/2/scheduleImages
            // delete http://localhost:8888/api/admin/2/scheduleImages/12
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getToken());

            String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                    .buildAndExpand(user.getId(), scheduleItemId)
                    .toUriString();

            ResponseEntity<List<Scheduleimage>> response = restTemplate.exchange(
                    completeUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Scheduleimage>>() {
            }
            );

            Scheduleitem scheduleItem = getScheduleItemById(user, scheduleItemId);
            Schedule schedule = getScheduleById(user, scheduleItem.getScheduleId().getId());
            model.addAttribute("scheduleId", schedule.getId());
            model.addAttribute("tourId", schedule.getTourId().getId());

            List<Scheduleimage> scheduleImages = response.getBody();
            model.addAttribute("scheduleImages", scheduleImages);
        }

        return "admin/scheduleImage/scheduleImage";
    }

    @RequestMapping("/create-scheduleImage/{scheduleItemId}")
    public String createScheduleImage(Model model, HttpSession session, @PathVariable Integer scheduleItemId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Scheduleitem scheduleItem = getScheduleItemById(user, scheduleItemId);

        model.addAttribute("scheduleItemId", scheduleItem.getId());
        model.addAttribute("scheduleId", scheduleItem.getScheduleId().getId());
        model.addAttribute("tourId", scheduleItem.getScheduleId().getTourId().getId());

        return "admin/scheduleImage/create-ScheduleImage";
    }

    @RequestMapping(value = "/create-scheduleImage", method = RequestMethod.POST)
    public String createScheduleImage(Model model, @ModelAttribute("Scheduleimage") Scheduleimage scheduleImage, HttpSession session, @RequestParam("scheduleItemid") Integer scheduleItemId, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if ((scheduleImage.getImage().isEmpty() && imageFile.isEmpty())) {
            redirectAttributes.addFlashAttribute("scheduleImage", scheduleImage);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-scheduleImage/" + scheduleItemId;
        }

        // Set giá trị cho Schedule item
        scheduleImage.setScheduleItemId(getScheduleItemById(user, scheduleItemId));

        // Kiểm tra xem người dùng đã chọn tệp ảnh hay chưa
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                scheduleImage.setImage(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                model.addAttribute("errorMessage", "An error occurred while processing the image.");
                return "admin/scheduleImage/create-scheduleImage";
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
        HttpEntity<Scheduleimage> requestEntity = new HttpEntity<>(scheduleImage, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Scheduleimage> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{adminId}/scheduleImages",
                HttpMethod.POST,
                requestEntity,
                Scheduleimage.class,
                user.getId()
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Scheduleimage scheduleimages = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully created a new schedule image.");
            return "redirect:/admin/scheduleImage/" + scheduleItemId;
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping("/edit-scheduleImage/{scheduleItemId}/{scheduleImageId}")
    public String editScheduleImage(Model model, HttpSession session, @PathVariable Integer scheduleItemId, @PathVariable Integer scheduleImageId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Scheduleitem scheduleItem = getScheduleItemById(user, scheduleItemId);
        Scheduleimage scheduleImage = getScheduleImageById(user, scheduleImageId);

        model.addAttribute("scheduleItemId", scheduleItem.getId());
        model.addAttribute("scheduleId", scheduleItem.getScheduleId().getId());
        model.addAttribute("tourId", scheduleItem.getScheduleId().getTourId().getId());
        model.addAttribute("currentScheduleImage", scheduleImage);

        return "admin/scheduleImage/edit-scheduleImage";
    }

    @RequestMapping(value = "/edit-scheduleImage", method = RequestMethod.POST)
    public String editScheduleImage(Model model, @ModelAttribute("Scheduleimage") Scheduleimage scheduleImage, HttpSession session, @RequestParam("scheduleItemid") Integer scheduleItemId, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if ((scheduleImage.getImage().isEmpty() && imageFile.isEmpty())) {
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    scheduleImage.setImage(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            redirectAttributes.addFlashAttribute("scheduleImage", scheduleImage);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/edit-scheduleImage/" + scheduleItemId + "/" + scheduleImage.getId();
        }

        Scheduleimage existedScheduleImage = getScheduleImageById(user, scheduleImage.getId());

        // Kiểm tra trùng
        if ((imageFile.isEmpty() && scheduleImage.getImage().equals(existedScheduleImage.getImage())) && scheduleImage.getDescription().equals(existedScheduleImage.getDescription())) {
            redirectAttributes.addFlashAttribute("scheduleImage", scheduleImage);
            redirectAttributes.addFlashAttribute("message", "Nothing has changed in your edits.");
            return "redirect:/admin/edit-scheduleImage/" + scheduleItemId + "/" + scheduleImage.getId();
        }

        // Set giá trị cho Schedule item
        scheduleImage.setScheduleItemId(getScheduleItemById(user, scheduleItemId));

        // Kiểm tra xem người dùng đã chọn tệp ảnh hay chưa
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                scheduleImage.setImage(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                model.addAttribute("errorMessage", "An error occurred while processing the image.");
                return "admin/scheduleImage/create-scheduleImage";
            }
            // Check trùng tiếp
            if (scheduleImage.getImage().equals(existedScheduleImage.getImage())) {
                redirectAttributes.addFlashAttribute("scheduleImage", scheduleImage);
                redirectAttributes.addFlashAttribute("message", "Nothing has changed in your edits.");
                return "redirect:/admin/edit-scheduleImage/" + scheduleItemId + "/" + scheduleImage.getId();
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
        HttpEntity<Scheduleimage> requestEntity = new HttpEntity<>(scheduleImage, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Scheduleimage> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{adminId}/scheduleImages",
                HttpMethod.PUT,
                requestEntity,
                Scheduleimage.class,
                user.getId()
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Scheduleimage scheduleimages = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully updated a schedule iamge.");
            return "redirect:/admin/scheduleImage/" + scheduleItemId;
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping(value = "/delete-scheduleImage/{scheduleImageId}", method = RequestMethod.GET)
    public String deleteScheduleItem(Model model, HttpSession session, @PathVariable Integer scheduleImageId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Scheduleimage scheduleimage = getScheduleImageById(user, scheduleImageId);

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Gọi API bằng phương thức DELETE và nhận kết quả trả về
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{adminId}/scheduleImages/{scheduleImageId}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                user.getId(),
                scheduleImageId
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Xử lý các công việc khác sau khi xóa thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng sau khi xóa thành công
            redirectAttributes.addFlashAttribute("message", "Successfully deleted the schedule image.");
            return "redirect:/admin/scheduleImage/" + scheduleimage.getScheduleItemId().getId();
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng nếu xóa không thành công
            return "/admin/error505";
        }
    }
    
    @RequestMapping("/detail-scheduleImage/{scheduleItemId}/{scheduleImageId}")
    public String detailScheduleImage(Model model, HttpSession session, @PathVariable Integer scheduleItemId, @PathVariable Integer scheduleImageId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }

        Scheduleitem scheduleItem = getScheduleItemById2(scheduleItemId);
        Scheduleimage scheduleImage = getScheduleImageById2(scheduleImageId);

        model.addAttribute("scheduleItemId", scheduleItem.getId());
        model.addAttribute("scheduleId", scheduleItem.getScheduleId().getId());
        model.addAttribute("tourId", scheduleItem.getScheduleId().getTourId().getId());
        model.addAttribute("currentScheduleImage", scheduleImage);

        return "admin/scheduleImage/detail-scheduleImage";
    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
    }

    private Schedule getScheduleById(Users user, Integer scheduleId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/admin/{adminId}/schedules/{scheduleId}")
                .uriVariables(Collections.singletonMap("adminId", user.getId()))
                .uriVariables(Collections.singletonMap("scheduleId", scheduleId));

        ResponseEntity<Schedule> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Schedule.class
        );

        return response.getBody();
    }

    private Scheduleitem getScheduleItemById(Users user, Integer scheduleItemId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/admin/{adminId}/scheduleItems/{scheduleItemId}")
                .uriVariables(Collections.singletonMap("adminId", user.getId()))
                .uriVariables(Collections.singletonMap("scheduleItemId", scheduleItemId));

        ResponseEntity<Scheduleitem> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Scheduleitem.class
        );

        return response.getBody();
    }

    private Scheduleimage getScheduleImageById(Users user, Integer scheduleImageId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/admin/{adminId}/scheduleImages/{scheduleImageId}")
                .uriVariables(Collections.singletonMap("adminId", user.getId()))
                .uriVariables(Collections.singletonMap("scheduleImageId", scheduleImageId));

        ResponseEntity<Scheduleimage> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Scheduleimage.class
        );

        return response.getBody();
    }
    
    private Scheduleimage getScheduleImageById2(Integer scheduleImageId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/tours/scheduleImage/{scheduleImageId}")
                .uriVariables(Collections.singletonMap("scheduleImageId", scheduleImageId));

        ResponseEntity<Scheduleimage> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Scheduleimage.class
        );

        return response.getBody();
    }
    
    private Scheduleitem getScheduleItemById2(Integer scheduleItemId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/tours/scheduleItem/{scheduleItemId}")
                .uriVariables(Collections.singletonMap("scheduleItemId", scheduleItemId));

        ResponseEntity<Scheduleitem> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Scheduleitem.class
        );

        return response.getBody();
    }

    private String convertImageToBase64(byte[] imageBytes) {
        // Chuyển đổi mảng byte của hình ảnh thành mã Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }

}
