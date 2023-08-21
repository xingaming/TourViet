package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.entities.Schedule;
import fpt.aptech.portal.entities.Tour;
import fpt.aptech.portal.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
public class ScheduleController {

    @RequestMapping("/schedule/{tourId}")
    public String schedule(Model model, HttpSession session, @PathVariable("tourId") Integer tourId) {
        if (!checkAuth(session)) {
            return "/admin/error403";
        }

        Users user = (Users) session.getAttribute("user");
        RestTemplate restTemplate = new RestTemplate();

        model.addAttribute("tourId", tourId);

        if (user.getRoleId().getId() == Role.SUPER_ADMIN.getValue()) {
            String apiUrl = "http://localhost:8888/api/tours/tour/{tourId}";
            try {
                ResponseEntity<Schedule[]> response = restTemplate.getForEntity(apiUrl, Schedule[].class, tourId);
                Schedule[] schedules = response.getBody();
                model.addAttribute("schedules", schedules);
            } catch (RuntimeException e) {
                model.addAttribute("message", "There are currently no schedules.");
                model.addAttribute("schedules", null);
            }

        } else if (user.getRoleId().getId() == Role.HQ.getValue() || user.getRoleId().getId() == Role.AREA.getValue()) {
            String apiUrl = "http://localhost:8888/api/admin/{adminId}/schedules/tour/{tourId}";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + user.getToken());

            String completeUrl = UriComponentsBuilder.fromUriString(apiUrl)
                    .buildAndExpand(user.getId(), tourId)
                    .toUriString();

            ResponseEntity<List<Schedule>> response = restTemplate.exchange(
                    completeUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Schedule>>() {
            }
            );

            List<Schedule> schedules = response.getBody();
            model.addAttribute("schedules", schedules);
        }

        model.addAttribute("tourId", tourId);

        return "admin/schedule/schedule";
    }

    @RequestMapping("/create-schedule/{tourId}")
    public String createSchedule(Model model, HttpSession session, @PathVariable Integer tourId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        model.addAttribute("tourId", tourId);

        return "admin/schedule/create-schedule";
    }

    @RequestMapping(value = "/create-schedule", method = RequestMethod.POST)
    public String createSchedule(Model model, @Nullable @ModelAttribute("Schedule") Schedule schedule, HttpSession session, @RequestParam("tourid") Integer tourId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Kiểm tra null
        if (schedule.getStartDate() == null || schedule.getEndDate() == null || schedule.getPrice() == null || schedule.getQuantityMin() == null || schedule.getQuantityMax() == null) {
            redirectAttributes.addFlashAttribute("schedule", schedule);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-schedule/" + tourId;
        }

        // kiểm tra ngày tháng
        if (schedule.getStartDate() != null && schedule.getEndDate() != null) {
            if (schedule.getStartDate().compareTo(new Date()) <= 0) {
                redirectAttributes.addFlashAttribute("schedule", schedule);
                redirectAttributes.addFlashAttribute("message", "Start date cannot be less than current date.");
                return "redirect:/admin/create-schedule/" + tourId;
            }
            if (schedule.getStartDate().compareTo(schedule.getEndDate()) > 0) {
                redirectAttributes.addFlashAttribute("schedule", schedule);
                redirectAttributes.addFlashAttribute("message", "Start date cannot be bigger than end date.");
                return "redirect:/admin/create-schedule/" + tourId;
            }
        }

        if (schedule.getQuantityMax() <= schedule.getQuantityMin()) {
            redirectAttributes.addFlashAttribute("schedule", schedule);
            redirectAttributes.addFlashAttribute("message", "Max quantity cannot be less than min quantity.");
            return "redirect:/admin/create-schedule/" + tourId;
        }

        // Set giá trị cho tour
        schedule.setQuantityPassenger(0);
        schedule.setTourId(getTourById(tourId));

        // Tạo một RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Đặt header cho request (Authorization header)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + user.getToken());

        // Đặt kiểu dữ liệu là JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt header cho request
        HttpEntity<Schedule> requestEntity = new HttpEntity<>(schedule, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Schedule> response = restTemplate.exchange(
                "http://localhost:8888/api/admin/{adminId}/schedules",
                HttpMethod.POST,
                requestEntity,
                Schedule.class,
                user.getId()
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Lấy tour được tạo từ kết quả trả về
            Schedule schedules = response.getBody();
            // Xử lý các công việc khác sau khi tạo thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            redirectAttributes.addFlashAttribute("message", "Successfully created a new schedule.");
            return "redirect:/admin/schedule/" + tourId;
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng
            return "/admin/error505";
        }
    }

    @RequestMapping("/edit-schedule/{tourId}/{scheduleId}")
    public String editSchedule(Model model, HttpSession session, @PathVariable Integer tourId, @PathVariable Integer scheduleId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue() && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        model.addAttribute("tourId", tourId);
        model.addAttribute("schedule", getScheduleById(user, scheduleId));

        if (session.getAttribute("schedule") != null) {
            Schedule schedule = (Schedule) session.getAttribute("schedule");
            model.addAttribute("schedule", schedule);
        }
        session.removeAttribute("schedule");

        return "admin/schedule/edit-schedule";
    }

    @RequestMapping(value = "/edit-schedule", method = RequestMethod.POST)
    public String editSchedule(Model model, @ModelAttribute("Schedule") Schedule schedule,
            HttpSession session, @RequestParam("scheduleId") Integer scheduleId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue()
                && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // Fetch the existing schedule by scheduleId from the database
        Schedule existingSchedule = getScheduleById(user, scheduleId);

        // Kiểm tra null
        if (schedule.getStartDate() == null || schedule.getEndDate() == null || schedule.getPrice() == null || schedule.getQuantityMin() == null || schedule.getQuantityMax() == null) {
            session.setAttribute("schedule", schedule);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/edit-schedule/" + existingSchedule.getTourId().getId() + "/" + scheduleId;
        }

        // kiểm tra ngày tháng
        if (schedule.getStartDate() != null && schedule.getEndDate() != null) {
            if (schedule.getStartDate().compareTo(new Date()) <= 0) {
                session.setAttribute("schedule", schedule);
                redirectAttributes.addFlashAttribute("message", "Start date cannot be less than current date.");
                return "redirect:/admin/edit-schedule/" + existingSchedule.getTourId().getId() + "/" + scheduleId;
            }
            if (schedule.getStartDate().compareTo(schedule.getEndDate()) > 0) {
                session.setAttribute("schedule", schedule);
                redirectAttributes.addFlashAttribute("message", "Start date cannot be bigger than end date.");
                return "redirect:/admin/edit-schedule/" + existingSchedule.getTourId().getId() + "/" + scheduleId;
            }
        }
        
        if (schedule.getQuantityMax() <= schedule.getQuantityMin()) {
            session.setAttribute("schedule", schedule);
            redirectAttributes.addFlashAttribute("message", "Max quantity cannot be less than min quantity.");
            return "redirect:/admin/edit-schedule/" + existingSchedule.getTourId().getId() + "/" + scheduleId;
        }

        // Update the properties of the fetched schedule with the new values
        schedule.setQuantityPassenger(existingSchedule.getQuantityPassenger());
        schedule.setTourId(getTourById(existingSchedule.getTourId().getId()));
        // ... Update other properties as needed

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

            // Kiểm tra kết quả trả về từ server
            if (response.getStatusCode().is2xxSuccessful()) {
                // Lấy schedule được chỉnh sửa từ kết quả trả về
                Schedule editedSchedule = response.getBody();
                // Xử lý các công việc khác sau khi chỉnh sửa thành công (nếu cần)

                // Redirect hoặc trả về view tùy theo logic của ứng dụng
                redirectAttributes.addFlashAttribute("message", "Successfully updated the schedule.");
                return "redirect:/admin/schedule/" + schedule.getTourId().getId();
            } else {
                // Xử lý lỗi nếu có (nếu cần)
                // Redirect hoặc trả về view tùy theo logic của ứng dụng
                return "/admin/error505";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Nothing has changed in your edits.");
            return "redirect:/admin/edit-schedule/" + existingSchedule.getTourId().getId() + "/" + scheduleId;
        }
    }

    @RequestMapping(value = "/delete-schedule/{scheduleId}", method = RequestMethod.GET)
    public String deleteSchedule(HttpSession session, @PathVariable Integer scheduleId, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");

        if (checkAuth(session) == false || user.getRoleId().getId() != Role.AREA.getValue()
                && user.getRoleId().getId() != Role.HQ.getValue()) {
            return "/admin/error403";
        }

        // để trước nếu không sẽ bị lỗi
        Schedule schedule = getScheduleById(user, scheduleId);

        // Gọi API bằng phương thức DELETE
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
                "http://localhost:8888/api/admin/{adminId}/schedules/{scheduleId}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                user.getId(),
                scheduleId
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            // Xử lý các công việc khác sau khi xóa thành công (nếu cần)

            // Redirect hoặc trả về view tùy theo logic của ứng dụng sau khi xóa thành công
            redirectAttributes.addFlashAttribute("message", "Successfully deleted the schedule.");
            return "redirect:/admin/schedule/" + schedule.getTourId().getId(); // Ví dụ: chuyển hướng đến trang danh sách lịch trình
        } else {
            // Xử lý lỗi nếu có (nếu cần)
            // Redirect hoặc trả về view tùy theo logic của ứng dụng nếu xóa không thành công
            return "/admin/error505";
        }
    }

    @RequestMapping("/schedule/detail/{tourId}/{scheduleId}")
    public String scheduleDetail(Model model, HttpSession session, @PathVariable Integer tourId, @PathVariable Integer scheduleId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false) {
            return "/admin/error403";
        }

        // Lấy thông tin lịch trình từ API bằng scheduleId và user
        Schedule schedule = getScheduleById2(scheduleId);
        if (schedule == null) {
            // Xử lý khi không tìm thấy lịch trình
            return "redirect:/admin/tour"; // Hoặc trả về trang lỗi tùy theo logic của ứng dụng
        }

        model.addAttribute("tourId", tourId);
        model.addAttribute("schedule", schedule);

        return "admin/schedule/detail-schedule";
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

    // Khai báo InitBinder
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, "startDate", new CustomDateEditor(dateFormat, true));
        binder.registerCustomEditor(Date.class, "endDate", new CustomDateEditor(dateFormat, true));

        binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.trim().isEmpty()) {
                    try {
                        BigDecimal price = new BigDecimal(text);
                        setValue(price);
                    } catch (NumberFormatException e) {
                        // Xử lý lỗi nếu giá trị không hợp lệ
                        setValue(null); // Set giá trị thành null nếu không thể chuyển đổi thành BigDecimal
                    }
                } else {
                    setValue(null); // Set giá trị thành null nếu text rỗng hoặc null
                }
            }
        });

    }

}
