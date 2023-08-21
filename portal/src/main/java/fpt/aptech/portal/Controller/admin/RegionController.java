package fpt.aptech.portal.Controller.admin;

import fpt.aptech.portal.entities.Region;
import fpt.aptech.portal.entities.Users;
import fpt.aptech.portal.enums.Role;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
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

@Controller
@RequestMapping("/admin")
public class RegionController {

    @RequestMapping("/regions")
    public String regions(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // Lấy regions
        model.addAttribute("regions", getRegions());
        return "admin/region/region";
    }

    @RequestMapping("/create-region")
    public String createRegion(Model model, HttpSession session, @ModelAttribute("Region") Region region) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        return "admin/region/create-region";
    }

    @RequestMapping(value = "/create-region", method = RequestMethod.POST)
    public String createRegionn(Model model, HttpSession session, @ModelAttribute("Region") Region region, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // CHECK REGION
        if ((region.getImage().isEmpty() && imageFile.isEmpty()) || region.getNameArea().isEmpty()) {
            // set ảnh để kiểm tra
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    region.setImage(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            redirectAttributes.addFlashAttribute("region", region);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/create-region";
        }

        // END CHECK REGION
        // Xử lý ảnh
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                region.setImage(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                model.addAttribute("message", "An error occurred while processing the image.");
            }
        }
        // Kết thúc xử lý ảnh

        // CALL API
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/create-region"; // Điều chỉnh URL tương ứng

        // Đặt header cho request (Authorization header nếu cần)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Đặt body cho request
        HttpEntity<Region> requestEntity = new HttpEntity<>(region, headers);

        // Gọi API bằng phương thức POST và nhận kết quả trả về
        ResponseEntity<Region> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                Region.class
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            Region createdRegion = response.getBody();
            // Xử lý khi tạo khu vực thành công
            redirectAttributes.addFlashAttribute("message", "Successfully created a new region.");
            return "redirect:/admin/regions";
        }
        return "admin/error505";
    }

    @RequestMapping("/edit-region/{regionId}")
    public String editRegion(Model model, HttpSession session, @PathVariable("regionId") int regionId) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        model.addAttribute("region", getRegionByRegionId(regionId));
        if(session.getAttribute("region") != null){
            Region region = (Region) session.getAttribute("region");
            model.addAttribute("region", region);
            session.removeAttribute("region");
        }
        return "admin/region/edit-region";
    }

    @RequestMapping(value = "/edit-region", method = RequestMethod.POST)
    public String editRegionn(Model model, HttpSession session, @ModelAttribute("Region") Region region, @RequestParam("images") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }

        // CHECK REGION
        if ((region.getImage().isEmpty() && imageFile.isEmpty()) || region.getNameArea().isEmpty()) {
            // set ảnh để kiểm tra
            if (!imageFile.isEmpty()) {
                try {
                    byte[] imageBytes = imageFile.getBytes();
                    String base64Image = convertImageToBase64(imageBytes);
                    region.setImage(base64Image);
                } catch (Exception e) {
                    // Xử lý lỗi nếu có (nếu cần)
                    model.addAttribute("message", "An error occurred while processing the image.");
                }
            }
            session.setAttribute("region", region);
            redirectAttributes.addFlashAttribute("errorMessage", "cannot be left blank.");
            return "redirect:/admin/edit-region/" + region.getId();
        }

        // END CHECK REGION
        // Xử lý ảnh
        if (!imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                String base64Image = convertImageToBase64(imageBytes);
                region.setImage(base64Image);
            } catch (Exception e) {
                // Xử lý lỗi nếu có (nếu cần)
                model.addAttribute("message", "An error occurred while processing the image.");
            }
        }
        // Kết thúc xử lý ảnh

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/edit-region/" + region.getId(); // Điều chỉnh URL tương ứng

        // Đặt header cho request (Authorization header nếu cần)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Region> requestEntity = new HttpEntity<>(region, headers);

        // Gọi API bằng phương thức PUT và nhận kết quả trả về
        ResponseEntity<Region> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.PUT,
                requestEntity,
                Region.class
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            Region editedRegion = response.getBody();
            // Xử lý khi chỉnh sửa khu vực thành công
        } else {
            // Xử lý khi gọi API chỉnh sửa thất bại
        }

        redirectAttributes.addFlashAttribute("message", "Successfully updated the region.");
        return "redirect:/admin/regions";
    }
    
    @RequestMapping("/delete-region/{regionId}")
    public String deleteRegion(Model model, HttpSession session, @PathVariable("regionId") int regionId, RedirectAttributes redirectAttributes){
        Users user = (Users) session.getAttribute("user");
        if (checkAuth(session) == false || user.getRoleId().getId() != Role.SUPER_ADMIN.getValue()) {
            return "/admin/error403";
        }
        
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/delete-region/" + regionId; // Điều chỉnh URL tương ứng

        // Đặt header cho request (Authorization header nếu cần)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //HttpEntity<Region> requestEntity = new HttpEntity<>(region, headers);

        // Gọi API bằng phương thức PUT và nhận kết quả trả về
        ResponseEntity<Region> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Region.class
        );

        // Kiểm tra kết quả trả về từ server
        if (response.getStatusCode().is2xxSuccessful()) {
            Region deletedRegion = response.getBody();
            // Xử lý khi chỉnh sửa khu vực thành công
        } else {
            // Xử lý khi gọi API chỉnh sửa thất bại
        }
        
        redirectAttributes.addFlashAttribute("message", "Successfully deleted the region.");
        return "redirect:/admin/regions";
    }

    private boolean checkAuth(HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null || user.getRoleId().getId() == Role.CUSTOMER.getValue()) {
            return false;
        }
        return true;
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

    private Region getRegionByRegionId(int regionId) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8888/api/region/" + regionId; // Điều chỉnh URL tương ứng

        // Gọi API bằng phương thức GET và nhận kết quả trả về
        ResponseEntity<Region> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                Region.class
        );

        return response.getBody();
    }

    private String convertImageToBase64(byte[] imageBytes) {
        // Chuyển đổi mảng byte của hình ảnh thành mã Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }

}
