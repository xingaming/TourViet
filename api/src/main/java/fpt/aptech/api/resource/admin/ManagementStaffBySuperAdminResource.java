package fpt.aptech.api.resource.admin;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import fpt.aptech.api.service.IManagementStaffBySuperAdmin;
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
@RequestMapping("/api/admin")
public class ManagementStaffBySuperAdminResource {

    @Autowired
    private IManagementStaffBySuperAdmin service;

    @GetMapping("/{adminId}/accounts")
    public ResponseEntity<List<Users>> getAllAccounts(@PathVariable int adminId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Users>>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> accounts = service.getAllAccounts();
            return ResponseEntity.ok(accounts);
        });
    }

    @PostMapping("/{adminId}/accounts/filter")
    public ResponseEntity<List<Users>> filterAccounts(@PathVariable int adminId, @RequestHeader("Authorization") String token, @RequestBody UsersFilter filter) {
        return (ResponseEntity<List<Users>>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            List<Users> filteredAccounts = service.filteredAccounts(filter);
            return ResponseEntity.ok(filteredAccounts);
        });
    }

    @PutMapping("/{adminId}/accounts/block/{userId}")
    public ResponseEntity<Boolean> blockAccount(@PathVariable int adminId, @PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Boolean>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean success = service.blockAccount(userId);
            return ResponseEntity.ok(success);
        });
    }

    @PutMapping("/{adminId}/accounts/unblock/{userId}")
    public ResponseEntity<Boolean> unblockAccount(@PathVariable int adminId, @PathVariable int userId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Boolean>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            boolean success = service.unblockAccount(userId);
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
        if ((authenticatedRoleId == RoleId.SUPER_ADMIN.getValue() && authenticatedUserId == userId)) {
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
