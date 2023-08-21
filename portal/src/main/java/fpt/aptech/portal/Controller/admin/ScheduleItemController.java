package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Scheduleitem;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/admin")
public class ScheduleItemController {

    @RequestMapping("/scheduleItem/{scheduleId}")
    public String scheduleItem(Model model, HttpSession session, @PathVariable("scheduleId") Integer scheduleId) {
        if (!checkAuth(session)) {
            return "/admin/error403";
        }

        Users user = (Users) session.getAttribute("user");
        RestTemplate restTemplate = new RestTemplate();

        model.addAttribute("scheduleId", scheduleId);

        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            String apiUrl = "http://localhost:8888/api/tours/scheduleId/{scheduleId}/scheduleItems";

            try {
                ResponseEntity<Scheduleitem[]> response = restTemplate.getForEntity(apiUrl, Scheduleitem[].class, scheduleId);
                Scheduleitem[] scheduleItems = response.getBody();
                model.addAttribute("scheduleItems", scheduleItems);
            } catch (RuntimeException e) {
                model.addAttribute("message", "There are currently no schedules.");
                model.addAttribute("scheduleItems", null);
            }

        } else if (user.getRoleId().getId() == Role.HQ.getValue() || user.getRoleId().getId() == Role.AREA.getValue()) {
            String apiUrl = "http://localhost:8888/api/admin/{adminId}/scheduleItems/schedule/{scheduleId}";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getToken());

            String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                    .buildAndExpand(user.getId(), scheduleId)
                    .toUriString();

            ResponseEntity<List<Scheduleitem>> response = restTemplate.exchange(
                    completeUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Scheduleitem>>() {
            }
            );

            Schedule schedule = getScheduleById(user, scheduleId);
            model.addAttribute("tourId", schedule.getTourId().getId());

            List<Scheduleitem> scheduleItems = response.getBody();
            model.addAttribute("scheduleItems", scheduleItems);
        }

        model.addAttribute("scheduleId", scheduleId);

        return "admin/scheduleItem/scheduleItem";
    }

    @RequestMapping("/create-scheduleItem/{scheduleId}")
    public String createSchedule(Model model, HttpSession session, @PathVariable Integer scheduleId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Schedule schedule = getScheduleById(user, scheduleId);

        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("tourId", schedule.getTourId().getId());

        return "admin/scheduleItem/create-ScheduleItem";
    }

    @RequestMapping(value = "/create-scheduleItem", method = RequestMethod.POST)
    public String createScheduleItem(Model model, @ModelAttribute("Scheduleitem") Scheduleitem scheduleItem, HttpSession session, @RequestParam("scheduleid") Integer scheduleid, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (scheduleItem.getNameDay().trim().isEmpty() || scheduleItem.getTitle().trim().isEmpty() || scheduleItem.getDescription().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("scheduleItem", scheduleItem);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-scheduleItem/" + scheduleid;
        }

        // Set giá trị cho Schedule item
        scheduleItem.setScheduleId(getScheduleById(user, scheduleid));

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Scheduleitem> requestEntity = new HttpEntity<>(scheduleItem, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Scheduleitem> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{adminId}/scheduleItems",
                HttpMethod.POST,
                requestEntity,
                Scheduleitem.class,
                user.getId()
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Scheduleitem scheduleitems = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully created a new schedule item.");
            return "redirect:/admin/scheduleItem/" + scheduleid;
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping("/edit-scheduleItem/{scheduleId}/{scheduleItemId}")
    public String editScheduleItem(Model model, HttpSession session, @PathVariable Integer scheduleId, @PathVariable Integer scheduleItemId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Get the schedule item by its ID
        Scheduleitem scheduleItem = getScheduleItemById(user, scheduleItemId);

        model.addAttribute("scheduleItemId", scheduleItemId);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("tourId", scheduleItem.getScheduleId().getTourId().getId());
        if(session.getAttribute("currentScheduleItem") != null){
            scheduleItem = (Scheduleitem) session.getAttribute("currentScheduleItem");
        }
        model.addAttribute("currentScheduleItem", scheduleItem);
        session.removeAttribute("currentScheduleItem");

        return "admin/scheduleItem/edit-scheduleItem";
    }

    @RequestMapping(value = "/edit-scheduleItem", method = RequestMethod.POST)
    public String editScheduleItem(Model model, @ModelAttribute("Scheduleitem") Scheduleitem scheduleItem, HttpSession session, @RequestParam("scheduleid") Integer scheduleid, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (scheduleItem.getNameDay().trim().isEmpty() || scheduleItem.getTitle().trim().isEmpty() || scheduleItem.getDescription().trim().isEmpty()) {
            session.setAttribute("currentScheduleItem", scheduleItem);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/edit-scheduleItem/" + scheduleid + "/" + scheduleItem.getId();
        }

        // Set giá trị cho Schedule item
        scheduleItem.setScheduleId(getScheduleById(user, scheduleid));

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Scheduleitem> requestEntity = new HttpEntity<>(scheduleItem, headers);

        try {
            // Gọi API bằng phương thức POST và nhận kết quả trả về
            ResponseEntity<Scheduleitem> response = restTemplate.exchange(
                    "http://localhost:8888/api/admin/{adminId}/scheduleItems",
                    HttpMethod.PUT,
                    requestEntity,
                    Scheduleitem.class,
                    user.getId()
            );

            // Kiểm tra kết quả trả về từ server
            if (response.getStatusCode().is2xxSuccessful()) {
                // Lấy tour được tạo từ kết quả trả về
                Scheduleitem scheduleitems = response.getBody();
                // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

                // Redirect hoặc trả về view tùy theo logic của ứng dụng
                redirectAttributes.addFlashAttribute("message", "Successfully updated the schedule.");
                return "redirect:/admin/scheduleItem/" + scheduleid;
            } else {
                // Xử lý lỗi nếu có (nếu cần)
                // Redirect hoặc trả về view tùy theo logic của ứng dụng
                return "/admin/error505";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Nothing has changed in your edits.");
            return "redirect:/admin/edit-scheduleItem/" + scheduleid + "/" + scheduleItem.getId();
        }
    }

    @RequestMapping(value = "/delete-scheduleItem/{scheduleItemId}", method = RequestMethod.GET)
    public String deleteScheduleItem(Model model, HttpSession session, @PathVariable Integer scheduleItemId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Scheduleitem scheduleitem = getScheduleItemById(user, scheduleItemId);

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
                "http://localhost:8888/api/admin/{adminId}/scheduleItems/{scheduleItemId}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                user.getId(),
                scheduleItemId
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Xử lý các công việc khác sau khi xóa thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng sau khi xóa thành công
            redirectAttributes.addFlashAttribute("message", "Successfully updated the schedule.");
            return "redirect:/admin/scheduleItem/" + scheduleitem.getScheduleId().getId();
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng nếu xóa không thành công
            return "/admin/error505";
        }
    }
    
    @RequestMapping("/scheduleItem/detail-scheduleItem/{scheduleId}/{scheduleItemId}")
    public String detailcheduleItem(Model model, HttpSession session, @PathVariable Integer scheduleId, @PathVariable Integer scheduleItemId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }

        // Get the schedule item by its ID
        Scheduleitem scheduleItem = getScheduleItemById2(scheduleItemId);

        model.addAttribute("scheduleItemId", scheduleItemId);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("tourId", scheduleItem.getScheduleId().getTourId().getId());
        model.addAttribute("scheduleItem", scheduleItem);

        return "admin/scheduleItem/detail-scheduleItem";
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

}
