package fpt.aptech.api.resource.admin;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import fpt.aptech.api.service.IManagementStaffByArea;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/admin/area")
public class ManagementStaffByAreaResource {

    @Autowired
    private IManagementStaffByArea service;

    @GetMapping("/{adminId}/tourguides")
    public ResponseEntity<List<Users>> getAllTourguides(@PathVariable int adminId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Users>>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> tourguides = service.getAllTourguides(adminId);
            return ResponseEntity.ok(tourguides);
        });
    }

    @PostMapping("/{adminId}/tourguides/filter")
    public ResponseEntity<List<Users>> filterTourguides(@PathVariable int adminId, @RequestHeader("Authorization") String token, @RequestBody UsersFilter filter) {
        return (ResponseEntity<List<Users>>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> filteredTourguides = service.tourguidesFilter(adminId, filter);
            return ResponseEntity.ok(filteredTourguides);
        });
    }

    @GetMapping("/{adminId}/tourguides/{staffId}")
    public ResponseEntity<Users> getTourguideDetails(@PathVariable int adminId, @PathVariable int staffId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Users>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            Users tourguide = service.tourguideDetails(staffId, adminId);
            return ResponseEntity.ok(tourguide);
        });
    }

    @PutMapping("/{adminId}/tourguides/block/{staffId}")
    public ResponseEntity<Boolean> blockAccount(@PathVariable int adminId, @PathVariable int staffId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Boolean>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean success = service.blockAccount(staffId, adminId);
            return ResponseEntity.ok(success);
        });
    }

    @PutMapping("/{adminId}/tourguides/unblock/{staffId}")
    public ResponseEntity<Boolean> unblockAccount(@PathVariable int adminId, @PathVariable int staffId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Boolean>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean success = service.unblockAccount(staffId, adminId);
            return ResponseEntity.ok(success);
        });
    }

    private ResponseEntity<?> getUserData(int adminId, String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Lấy phần JWT token bằng cách loại bỏ tiền tố "Bearer "
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        // Lấy authenticatedRoleId từ JWT
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dựa trên authenticatedRoleId và authenticatedUserId
        if (authenticatedRoleId == RoleId.AREA.getValue() && authenticatedUserId == adminId) {
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
