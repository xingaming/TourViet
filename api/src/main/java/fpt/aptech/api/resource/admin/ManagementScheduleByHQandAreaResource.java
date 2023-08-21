package fpt.aptech.api.resource.admin;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleimage;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Serviceitem;
import fpt.aptech.api.service.IManagementScheduleByHQandArea;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/admin")
public class ManagementScheduleByHQandAreaResource {

    @Autowired
    IManagementScheduleByHQandArea service;

    @GetMapping("/{adminId}/schedules/tour/{tourId}")
    public ResponseEntity<List<Schedule>> viewSchedules(@PathVariable int adminId, @PathVariable int tourId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Schedule>>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            List<Schedule> schedules = service.viewSchedule(userId, tourId);
            return ResponseEntity.ok(schedules);
        }));
    }

    @GetMapping("/{adminId}/schedules/{scheduleId}")
    public ResponseEntity<Schedule> viewDetailSchedule(@PathVariable int adminId, @PathVariable int scheduleId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Schedule>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Schedule schedule = service.viewDetailSchedule(userId, scheduleId);
            return ResponseEntity.ok(schedule);
        }));
    }

    @PostMapping("/{adminId}/schedules")
    public ResponseEntity<Schedule> createSchedule(@PathVariable int adminId, @RequestBody Schedule newSchedule, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Schedule>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Schedule createdSchedule = service.createSchedule(userId, newSchedule);
            return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
        }));
    }

    @PutMapping("/{adminId}/schedules")
    public ResponseEntity<Schedule> modifySchedule(@PathVariable int adminId, @RequestBody Schedule modifiedSchedule, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Schedule>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Schedule updatedSchedule = service.modifySchedule(userId, modifiedSchedule);
            return ResponseEntity.ok(updatedSchedule);
        }));
    }

    @DeleteMapping("/{adminId}/schedules/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable int adminId, @PathVariable int scheduleId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<String>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            service.deleteSchedule(userId, scheduleId);
            return ResponseEntity.ok("Schedule with id " + scheduleId + " has been deleted.");
        }));
    }

    @GetMapping("/{adminId}/scheduleItems/schedule/{scheduleId}")
    public ResponseEntity<List<Scheduleitem>> viewScheduleItems(@PathVariable int adminId, @PathVariable int scheduleId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Scheduleitem>>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            List<Scheduleitem> scheduleItems = service.viewScheduleItems(userId, scheduleId);
            return ResponseEntity.ok(scheduleItems);
        }));
    }

    @GetMapping("/{adminId}/scheduleItems/{scheduleItemId}")
    public ResponseEntity<Scheduleitem> viewDetailScheduleItem(@PathVariable int adminId, @PathVariable int scheduleItemId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Scheduleitem>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Scheduleitem scheduleItem = service.viewDetailScheduleItem(userId, scheduleItemId);
            return ResponseEntity.ok(scheduleItem);
        }));
    }

    @PostMapping("/{adminId}/scheduleItems")
    public ResponseEntity<Scheduleitem> createScheduleItem(@PathVariable int adminId, @RequestBody Scheduleitem newScheduleItem, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Scheduleitem>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Scheduleitem createdScheduleItem = service.createScheduleItem(userId, newScheduleItem);
            return new ResponseEntity<>(createdScheduleItem, HttpStatus.CREATED);
        }));
    }

    @PutMapping("/{adminId}/scheduleItems")
    public ResponseEntity<Scheduleitem> modifyScheduleItem(@PathVariable int adminId, @RequestBody Scheduleitem modifiedScheduleItem, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Scheduleitem>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Scheduleitem updatedScheduleItem = service.modifyScheduleItem(userId, modifiedScheduleItem);
            return ResponseEntity.ok(updatedScheduleItem);
        }));
    }

    @DeleteMapping("/{adminId}/scheduleItems/{scheduleItemId}")
    public ResponseEntity<String> deleteScheduleItem(@PathVariable int adminId, @PathVariable int scheduleItemId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<String>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            service.deleteScheduleItem(userId, scheduleItemId);
            return ResponseEntity.ok("ScheduleItem with id " + scheduleItemId + " has been deleted.");
        }));
    }

    @GetMapping("/{adminId}/scheduleImages/scheduleItem/{scheduleItemId}")
    public ResponseEntity<List<Scheduleimage>> viewScheduleImages(@PathVariable int adminId, @PathVariable int scheduleItemId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Scheduleimage>>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            List<Scheduleimage> scheduleImages = service.viewScheduleImages(userId, scheduleItemId);
            return ResponseEntity.ok(scheduleImages);
        }));
    }

    @GetMapping("/{adminId}/scheduleImages/{scheduleImageId}")
    public ResponseEntity<Scheduleimage> viewDetailScheduleImage(@PathVariable int adminId, @PathVariable int scheduleImageId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Scheduleimage>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Scheduleimage scheduleImage = service.viewDetailScheduleImage(userId, scheduleImageId);
            return ResponseEntity.ok(scheduleImage);
        }));
    }

    @PostMapping("/{adminId}/scheduleImages")
    public ResponseEntity<Scheduleimage> createScheduleImage(@PathVariable int adminId, @RequestBody Scheduleimage newScheduleImage, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Scheduleimage>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Scheduleimage createdScheduleImage = service.createScheduleImage(userId, newScheduleImage);
            return new ResponseEntity<>(createdScheduleImage, HttpStatus.CREATED);
        }));
    }

    @PutMapping("/{adminId}/scheduleImages")
    public ResponseEntity<Scheduleimage> modifyScheduleImage(@PathVariable int adminId, @RequestBody Scheduleimage modifiedScheduleImage, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Scheduleimage>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Scheduleimage updatedScheduleImage = service.modifyScheduleImage(userId, modifiedScheduleImage);
            return ResponseEntity.ok(updatedScheduleImage);
        }));
    }

    @DeleteMapping("/{adminId}/scheduleImages/{scheduleImageId}")
    public ResponseEntity<String> deleteScheduleImage(@PathVariable int adminId, @PathVariable int scheduleImageId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<String>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            service.deleteScheduleImage(userId, scheduleImageId);
            return ResponseEntity.ok("ScheduleImage with id " + scheduleImageId + " has been deleted.");
        }));
    }

    @GetMapping("/{adminId}/serviceItems/schedule/{scheduleId}")
    public ResponseEntity<List<Serviceitem>> viewServiceItems(@PathVariable int adminId, @PathVariable int scheduleId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Serviceitem>>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            List<Serviceitem> serviceItems = service.viewServiceItems(userId, scheduleId);
            return ResponseEntity.ok(serviceItems);
        }));
    }

    @GetMapping("/{adminId}/serviceItems/{serviceItemId}")
    public ResponseEntity<Serviceitem> viewDetailServiceItem(@PathVariable int adminId, @PathVariable int serviceItemId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Serviceitem>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Serviceitem serviceItem = service.viewDetailServiceItem(userId, serviceItemId);
            return ResponseEntity.ok(serviceItem);
        }));
    }

    @PostMapping("/{adminId}/serviceItems")
    public ResponseEntity<Serviceitem> createServiceItem(@PathVariable int adminId, @RequestBody Serviceitem newServiceItem, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Serviceitem>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Serviceitem createdServiceItem = service.createServiceItem(userId, newServiceItem);
            return new ResponseEntity<>(createdServiceItem, HttpStatus.CREATED);
        }));
    }

    @PutMapping("/{adminId}/serviceItems")
    public ResponseEntity<Serviceitem> modifyServiceItem(@PathVariable int adminId, @RequestBody Serviceitem modifiedServiceItem, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Serviceitem>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            Serviceitem updatedServiceItem = service.modifyServiceItem(userId, modifiedServiceItem);
            return ResponseEntity.ok(updatedServiceItem);
        }));
    }

    @DeleteMapping("/{adminId}/serviceItems/{serviceItemId}")
    public ResponseEntity<String> deleteServiceItem(@PathVariable int adminId, @PathVariable int serviceItemId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<String>) handleErrors(() -> getUserData(adminId, token, (userId, roleId) -> {
            service.deleteServiceItem(userId, serviceItemId);
            return ResponseEntity.ok("ServiceItem with id " + serviceItemId + " has been deleted.");
        }));
    }

    private ResponseEntity<?> getUserData(int userId, String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Lấy phần JWT token bằng cách loại bỏ tiền tố "Bearer "
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        // Lấy authenticatedRoleId từ JWT
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dựa trên authenticatedRoleId và authenticatedUserId
        if ((authenticatedRoleId == RoleId.HQ.getValue() && authenticatedUserId == userId)
                || (authenticatedRoleId == RoleId.AREA.getValue() && authenticatedUserId == userId)) {
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseEntity<?> handleErrors(Supplier<ResponseEntity<?>> action) {
        try {
            ResponseEntity<?> response = action.get();

            // Kiểm tra nếu response có mã trạng thái là 401 Unauthorized thì trả về luôn response đó
            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return response;
            }

            // Xử lý trả về lỗi 400 Bad Request hoặc lỗi 500 Internal Server Error
            return ResponseEntity.ok(response.getBody());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

}
