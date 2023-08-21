package fpt.aptech.api.resource.admin;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import fpt.aptech.api.service.IManagementStaffByHQ;
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
@RequestMapping("/api/admin/hq")
public class ManagementStaffByHQResource {

    @Autowired
    private IManagementStaffByHQ service;

    @GetMapping("{userId}/areas")
    public ResponseEntity<List<Users>> getAllAreas(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Users>>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> areas = service.getAllAreas(userId);
            return ResponseEntity.ok(areas);
        });
    }

    @GetMapping("{userId}/tourguides")
    public ResponseEntity<List<Users>> getAllTourguides(@PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Users>>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> tourguides = service.getAllTourguides(userId);
            return ResponseEntity.ok(tourguides);
        });
    }

    @PostMapping("{userId}/areas/filter")
    public ResponseEntity<List<Users>> filterAreas(@PathVariable int userId, @RequestHeader("Authorization") String token, @RequestBody UsersFilter filter) {
        return (ResponseEntity<List<Users>>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> filteredAreas = service.areasFilter(filter, userId);
            return ResponseEntity.ok(filteredAreas);
        });
    }

    @PostMapping("{userId}/tourguides/filter")
    public ResponseEntity<List<Users>> filterTourguides(@PathVariable int userId, @RequestHeader("Authorization") String token, @RequestBody UsersFilter filter) {
        return (ResponseEntity<List<Users>>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> filteredTourguides = service.tourguideFilter(filter, userId);
            return ResponseEntity.ok(filteredTourguides);
        });
    }

    @GetMapping("{userId}/details/{staffId}")
    public ResponseEntity<Users> getStaffDetails(@PathVariable int userId, @PathVariable int staffId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Users>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            Users staffDetails = service.getStaffDetails(staffId, userId);
            return ResponseEntity.ok(staffDetails);
        });
    }

    @PutMapping("{userId}/block/{staffId}")
    public ResponseEntity<Boolean> blockAccount(@PathVariable int staffId, @PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Boolean>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean success = service.blockAccount(staffId, userId);
            return ResponseEntity.ok(success);
        });
    }

    @PutMapping("{userId}/unblock/{staffId}")
    public ResponseEntity<Boolean> unblockAccount(@PathVariable int staffId, @PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Boolean>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean success = service.unblockAccount(staffId, userId);
            return ResponseEntity.ok(success);
        });
    }

    private ResponseEntity<?> getUserData(int userId, String token, BiFunction<Integer, Integer, ResponseEntity<?>> action) {
        // Lấy phần JWT token bằng cách loại bỏ tiền tố "Bearer "
        String jwtToken = token.substring(7);
        // Lấy authenticatedUserId từ JWT
        int authenticatedUserId = TokenUtil.getUserIdFromToken(jwtToken);
        // Lấy authenticatedRoleId từ JWT
        int authenticatedRoleId = TokenUtil.getRoleIdFromToken(jwtToken);

        // Kiểm tra quyền truy cập dựa trên authenticatedRoleId và authenticatedUserId
        if (authenticatedRoleId == RoleId.HQ.getValue() && authenticatedUserId == userId) {
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
