package fpt.aptech.api.resource.admin;

import fpt.aptech.api.TokenUtil.TokenUtil;
import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.models.Tour;
import fpt.aptech.api.models.TourCreate;
import fpt.aptech.api.service.IManagementToursForHQAndArea;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/admin")
public class ManagementToursForHQAndAreaResource {

    private final IManagementToursForHQAndArea service;

    @Autowired
    public ManagementToursForHQAndAreaResource(IManagementToursForHQAndArea service) {
        this.service = service;
    }

    @GetMapping("/{adminId}/tours")
    public ResponseEntity<List<Tour>> viewTours(@PathVariable int adminId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<List<Tour>>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            try {
                List<Tour> tours = service.viewTours(adminId);
                return ResponseEntity.ok(tours);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    @GetMapping("/made_tours")
    public List<TourCreate> viewMadeTours() {
        return service.viewMadeTours();

    }
    
    @GetMapping("/made_tours/{tourId}")
    public TourCreate viewMadeTours(@PathVariable int tourId) {
        return service.viewMadeToursById(tourId);

    }

    @GetMapping("/{adminId}/tours/{tourId}")
    public ResponseEntity<Tour> viewDetailTour(@PathVariable int adminId, @PathVariable int tourId, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Tour>) getUserData(adminId, token, (authenticatedUserId, authenticatedRoleId) -> {
            try {
                Tour tour = service.viewDetailTour(adminId, tourId);
                return ResponseEntity.ok(tour);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.notFound().build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    @PostMapping("/{userId}/tours")
    public ResponseEntity<Tour> createTour(@PathVariable int userId, @RequestBody Tour tour, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Tour>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            try {
                Tour createdTour = service.createTour(userId, tour);
                return ResponseEntity.ok(createdTour);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    @PutMapping("/{userId}/tours/{id}")
    public ResponseEntity<Tour> modifyTour(@PathVariable int userId, @PathVariable int id, @RequestBody Tour tour, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Tour>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            try {
                Tour modifiedTour = service.modifyTour(userId, id, tour);
                return ResponseEntity.ok(modifiedTour);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.notFound().build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });
    }

    @DeleteMapping("/{userId}/tours/{id}")
    public ResponseEntity<Tour> removeTour(@PathVariable int userId, @PathVariable int id, @RequestHeader("Authorization") String token) {
        return (ResponseEntity<Tour>) getUserData(userId, token, (authenticatedUserId, authenticatedRoleId) -> {
            try {
                Tour removedTour = service.removeTour(userId, id);
                return ResponseEntity.ok(removedTour);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.notFound().build();
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
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
        if ((authenticatedRoleId == RoleId.HQ.getValue() && authenticatedUserId == userId)
                || (authenticatedRoleId == RoleId.AREA.getValue() && authenticatedUserId == userId)) {
            return action.apply(authenticatedUserId, authenticatedRoleId);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
