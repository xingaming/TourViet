package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Serviceitem;
import fpt.aptech.portal.entities.Tour;
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
public class ServiceItemController {

    @RequestMapping("/serviceItem/{scheduleId}")
    public String serviceItem(Model model, HttpSession session, @PathVariable Integer scheduleId, RedirectAttributes redirectAttributes) {
        if (!checkAuth(session)) {
            return "/admin/error403";
        }

        Users user = (Users) session.getAttribute("user");
        RestTemplate restTemplate = new RestTemplate();

        model.addAttribute("scheduleId", scheduleId);

        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            String apiUrl = "http://localhost:8888/api/tours/scheduleId/{scheduleId}/serviceItems";
            try {
                ResponseEntity<Serviceitem[]> response = restTemplate.getForEntity(apiUrl, Serviceitem[].class, scheduleId);
                Serviceitem[] serviceItems = response.getBody();
                model.addAttribute("serviceItems", serviceItems);
            } catch (RuntimeException e) {
                model.addAttribute("message", "There are currently no schedules.");
                model.addAttribute("serviceItems", null);
            }

        } else if (user.getRoleId().getId() == Role.HQ.getValue() || user.getRoleId().getId() == Role.AREA.getValue()) {
            String apiUrl = "http://localhost:8888/api/admin/{adminId}/serviceItems/schedule/{scheduleId}";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getToken());

            String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                    .buildAndExpand(user.getId(), scheduleId)
                    .toUriString();

            ResponseEntity<List<Serviceitem>> response = restTemplate.exchange(
                    completeUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Serviceitem>>() {
            }
            );

            Schedule schedule = getScheduleById(user, scheduleId);
            model.addAttribute("tourId", schedule.getTourId().getId());

            List<Serviceitem> serviceItems = response.getBody();
            model.addAttribute("serviceItems", serviceItems);
        }

        model.addAttribute("scheduleId", scheduleId);

        return "admin/serviceItem/serviceItem";
    }

    @RequestMapping("/create-serviceItem/{scheduleId}")
    public String createServiceItem(Model model, HttpSession session, @PathVariable Integer scheduleId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Schedule schedule = getScheduleById(user, scheduleId);

        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("tourId", schedule.getTourId().getId());

        return "admin/serviceItem/create-serviceItem";
    }

    @RequestMapping(value = "/create-serviceItem", method = RequestMethod.POST)
    public String createServiceItem(Model model, @ModelAttribute("Serviceitem") Serviceitem serviceItem, HttpSession session, @RequestParam("scheduleid") Integer scheduleid, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (serviceItem.getTitle().isEmpty()) {
            redirectAttributes.addFlashAttribute("serviceItem", serviceItem);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-serviceItem/" + scheduleid;
        }

        // Set giá trị cho Schedule item
        serviceItem.setScheduleId(getScheduleById(user, scheduleid));

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Serviceitem> requestEntity = new HttpEntity<>(serviceItem, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Serviceitem> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{adminId}/serviceItems",
                HttpMethod.POST,
                requestEntity,
                Serviceitem.class,
                user.getId()
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Serviceitem serviceItems = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully created a new service.");
            return "redirect:/admin/serviceItem/" + scheduleid;
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping("/edit-serviceItem/{scheduleId}/{serviceItemId}")
    public String editServiceItem(Model model, HttpSession session, @PathVariable Integer scheduleId, @PathVariable Integer serviceItemId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Lấy thông báo lỗi nếu có
        if (model.containsAttribute("message")) {
            String message = (String) model.asMap().get("message");
            model.addAttribute("message", message);
        }

        // Get the schedule item by its ID
        Serviceitem serviceItem = getServiceItemById(user, serviceItemId);

        model.addAttribute("serviceItemId", serviceItemId);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("tourId", serviceItem.getScheduleId().getTourId().getId());
        if (session.getAttribute("currentServiceItem") != null) {
            serviceItem = (Serviceitem) session.getAttribute("currentServiceItem");
        }
        model.addAttribute("currentServiceItem", serviceItem);
        session.removeAttribute("currentServiceItem");

        return "admin/serviceItem/edit-serviceItem";
    }

    @RequestMapping(value = "/edit-serviceItem", method = RequestMethod.POST)
    public String editServiceItem(Model model, @ModelAttribute("Serviceitem") Serviceitem serviceItem, HttpSession session, @RequestParam("scheduleid") Integer scheduleid, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (serviceItem.getTitle().isEmpty()) {
            session.setAttribute("currentServiceItem", serviceItem);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/edit-serviceItem/" + scheduleid + "/" + serviceItem.getId();
        }

        // Set giá trị cho Schedule item
        serviceItem.setScheduleId(getScheduleById(user, scheduleid));

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Serviceitem> requestEntity = new HttpEntity<>(serviceItem, headers);

        try {

            // Gọi API bằng phương thức POST và nhận kết quả trả về
            ResponseEntity<Serviceitem> response = restTemplate.exchange(
                    "http://localhost:8888/api/admin/{adminId}/serviceItems",
                    HttpMethod.PUT,
                    requestEntity,
                    Serviceitem.class,
                    user.getId()
            );

            // Kiểm tra kết quả trả về từ server
            if (response.getStatusCode().is2xxSuccessful()) {
                // Lấy tour được tạo từ kết quả trả về
                Serviceitem serviceItems = response.getBody();
                // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

                // Redirect hoặc trả về view tùy theo logic của ứng dụng
                redirectAttributes.addFlashAttribute("message", "Service information has been updated successfully.");
                return "redirect:/admin/serviceItem/" + scheduleid;
            } else {
                // Xử lý lỗi nếu có (nếu cần)
                // Redirect hoặc trả về view tùy theo logic của ứng dụng
                return "/admin/error505";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Nothing has changed in your edits.");
            return "redirect:/admin/edit-serviceItem/" + scheduleid + "/" + serviceItem.getId();
        }
    }

    @RequestMapping(value = "/delete-serviceItem/{serviceItemId}", method = RequestMethod.GET)
    public String deleteServiceItem(Model model, HttpSession session, @PathVariable Integer serviceItemId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        Serviceitem serviceItem = getServiceItemById(user, serviceItemId);

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
                "http://localhost:8888/api/admin/{adminId}/serviceItems/{serviceItemId}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                user.getId(),
                serviceItemId
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Xử lý các công việc khác sau khi xóa thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng sau khi xóa thành công
            redirectAttributes.addFlashAttribute("message", "Service has been successfully deleted.");
            return "redirect:/admin/serviceItem/" + serviceItem.getScheduleId().getId();
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng nếu xóa không thành công
            return "/admin/error505";
        }
    }
    
    @RequestMapping("/detail-serviceItem/{scheduleId}/{serviceItemId}")
    public String detailServiceItem(Model model, HttpSession session, @PathVariable Integer scheduleId, @PathVariable Integer serviceItemId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }

        // Get the schedule item by its ID
        Serviceitem serviceItem = getServiceItemById2(serviceItemId);

        model.addAttribute("serviceItemId", serviceItemId);
        model.addAttribute("scheduleId", scheduleId);
        model.addAttribute("tourId", serviceItem.getScheduleId().getTourId().getId());
        model.addAttribute("currentServiceItem", serviceItem);

        return "admin/serviceItem/detail-serviceItem";
    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
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

    private Serviceitem getServiceItemById(Users user, Integer serviceItemId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/admin/{adminId}/serviceItems/{serviceItemId}")
                .uriVariables(Collections.singletonMap("adminId", user.getId()))
                .uriVariables(Collections.singletonMap("serviceItemId", serviceItemId));

        ResponseEntity<Serviceitem> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Serviceitem.class
        );

        return response.getBody();
    }
    
    private Serviceitem getServiceItemById2(Integer serviceItemId) {
        // Declare restTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set up headers with the authentication token
        HttpHeaders headers = new HttpHeaders();

        // Set the headers in the request entity
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // Build the URL with path variables
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888/api/tours/serviceItem/{serviceItemId}")
                .uriVariables(Collections.singletonMap("serviceItemId", serviceItemId));

        ResponseEntity<Serviceitem> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                Serviceitem.class
        );

        return response.getBody();
    }

}
