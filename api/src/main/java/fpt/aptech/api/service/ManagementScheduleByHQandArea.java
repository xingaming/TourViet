package fpt.aptech.api.service;

import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleimage;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Serviceitem;
import fpt.aptech.api.respository.ScheduleRepository;
import fpt.aptech.api.respository.ScheduleimageRepository;
import fpt.aptech.api.respository.ScheduleitemRepository;
import fpt.aptech.api.respository.ServiceitemRepository;
import fpt.aptech.api.respository.TourRepository;
import fpt.aptech.api.respository.UsersRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ManagementScheduleByHQandArea implements IManagementScheduleByHQandArea {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleitemRepository scheduleitemRepository;
    private final ScheduleimageRepository scheduleimageRepository;
    private final ServiceitemRepository serviceitemRepository;
    private final UsersRepository userRepository;
    private final TourRepository tourRepository;

    @Autowired
    public ManagementScheduleByHQandArea(ScheduleRepository scheduleRepository, ScheduleitemRepository scheduleitemRepository,
            ScheduleimageRepository scheduleimageRepository,
            ServiceitemRepository serviceitemRepository,
            UsersRepository userRepository, TourRepository tourRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleitemRepository = scheduleitemRepository;
        this.scheduleimageRepository = scheduleimageRepository;
        this.serviceitemRepository = serviceitemRepository;
        this.userRepository = userRepository;
        this.tourRepository = tourRepository;
    }

    @Override
    public List<Schedule> viewSchedule(int adminId, int tourId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Check xem tourId có tồn tại hay không
        if (tourRepository.findById(tourId) == null) {
            throw new IllegalArgumentException("Tour Id not found!");
        }

        // Lấy danh sách ScheduleImage theo companyId của admin
        return scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getTourId().getCompanyId().getId().equals(adminCompanyId) && schedule.getTourId().getId().equals(tourId))
                .collect(Collectors.toList());
    }

    @Override
    public Schedule viewDetailSchedule(int adminId, int scheduleId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy thông tin chi tiết của Schedule dựa trên scheduleId
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);

        // Kiểm tra xem Schedule có tồn tại hay không
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException("Schedule not found for scheduleId " + scheduleId);
        }

        Schedule schedule = optionalSchedule.get();

        // Kiểm tra nếu Schedule không thuộc công ty của admin
        if (!schedule.getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to access this Schedule.");
        }

        return schedule;
    }

    @Override
    public Schedule createSchedule(int adminId, Schedule newSchedule) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem Scheduleitem có tồn tại hay không
        if (newSchedule == null) {
            throw new IllegalArgumentException("Scheduleitem cannot be null.");
        }

        // Kiểm tra nếu Schedule không thuộc công ty của admin
        if (!newSchedule.getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to create this Schedule.");
        }

        try {
            // Tiến hành lưu mới Scheduleitem và trả về kết quả
            return scheduleRepository.save(newSchedule);
        } catch (Exception ex) {
            // Xử lý ngoại lệ nếu có
            throw new RuntimeException("Failed to create Schedule.", ex);
        }
    }

    @Override
    public Schedule modifySchedule(int adminId, Schedule schedule) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy thông tin chi tiết của Schedule dựa trên scheduleId
        Schedule existingSchedule = scheduleRepository.findById(schedule.getId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id " + schedule.getId()));

        // Kiểm tra xem Schedule thuộc công ty của admin hay không
        if (!existingSchedule.getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to modify this Schedule.");
        }

        // Kiểm tra xem Schedule đã thay đổi hay chưa
        if (!isScheduleChanged(schedule, existingSchedule)) {
            throw new IllegalArgumentException("No changes found in the Schedule.");
        }

        try {
            // Lưu thông tin của Schedule sau khi sửa đổi và trả về kết quả
            return scheduleRepository.save(schedule);
        } catch (Exception ex) {
            // Xử lý nếu có lỗi trong quá trình lưu Schedule
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteSchedule(int adminId, int scheduleId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy thông tin chi tiết của Schedule dựa trên scheduleId
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with id " + scheduleId));

        // Kiểm tra xem Schedule thuộc công ty của admin hay không
        if (!existingSchedule.getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to delete this Schedule.");
        }

        try {
            List<Scheduleitem> scheduleitems = scheduleitemRepository.findScheduleItemsByScheduleId(scheduleId);
            for (Scheduleitem scheduleitem : scheduleitems) {
                scheduleimageRepository.deleteByScheduleItemId(scheduleitem.getId());
            }
            scheduleitemRepository.deleteSchedulesItemByScheduleId(scheduleId);
            serviceitemRepository.deleteServiceItemByScheduleId(scheduleId);
            // Xóa Schedule dựa vào scheduleId
            scheduleRepository.delete(existingSchedule);
        } catch (Exception ex) {
            // Xử lý nếu có lỗi trong quá trình xóa Schedule
            throw new RuntimeException("Failed to delete Schedule.", ex);
        }

    }

    @Override
    public List<Scheduleitem> viewScheduleItems(int adminId, int scheduleId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // check xem scheduleId có tồn tại hay không
        if (scheduleRepository.findById(scheduleId) == null) {
            throw new IllegalArgumentException("Schedule Id not found!");
        }

        return scheduleitemRepository.findAll().stream()
                // Lọc theo companyId
                .filter(scheduleItem -> {
                    Integer companyId = scheduleItem.getScheduleId().getTourId().getCompanyId().getId();
                    return companyId != null && companyId.equals(adminCompanyId) && scheduleItem.getScheduleId().getId().equals(scheduleId);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Scheduleitem viewDetailScheduleItem(int adminId, int scheduleItemId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy thông tin chi tiết của scheduleitem theo scheduleItemId và companyId
        Scheduleitem scheduleitem = scheduleitemRepository.findById(scheduleItemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid scheduleItemId"));

        // Kiểm tra xem scheduleitem có thuộc công ty của admin hay không
        Integer companyId = scheduleitem.getScheduleId().getTourId().getCompanyId().getId();
        if (companyId == null || !companyId.equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action");
        }

        return scheduleitem;
    }

    @Override
    public Scheduleitem createScheduleItem(int adminId, Scheduleitem newScheduleItem) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        try {
            // Kiểm tra xem newScheduleItem có null hay không
            if (newScheduleItem == null) {
                throw new IllegalArgumentException("New ScheduleItem must not be null.");
            }

            // Kiểm tra xem newScheduleItem thuộc công ty của admin hay không
            Integer companyId = newScheduleItem.getScheduleId().getTourId().getCompanyId().getId();
            if (companyId == null || !companyId.equals(adminCompanyId)) {
                throw new IllegalArgumentException("You do not have permission to perform this action");
            }

            // Lưu newScheduleItem vào CSDL và trả về đối tượng đã lưu
            return scheduleitemRepository.save(newScheduleItem);
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi tạo mới và trả về phản hồi lỗi cho người dùng hoặc ứng dụng
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public Scheduleitem modifyScheduleItem(int adminId, Scheduleitem scheduleItem) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem scheduleItem có null hay không
        if (scheduleItem == null) {
            throw new IllegalArgumentException("ScheduleItem must not be null.");
        }

        // Kiểm tra xem scheduleItem thuộc công ty của admin hay không
        Integer companyId = scheduleItem.getScheduleId().getTourId().getCompanyId().getId();
        if (companyId == null || !companyId.equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action");
        }

        // Lấy thông tin của scheduleItem từ CSDL bằng cách tìm kiếm theo id
        Scheduleitem existingScheduleItem = scheduleitemRepository.findById(scheduleItem.getId())
                .orElseThrow(() -> new IllegalArgumentException("ScheduleItem with id " + scheduleItem.getId() + " not found."));

        // Kiểm tra xem Schedule đã thay đổi hay chưa
        if (!isScheduleItemChanged(scheduleItem, existingScheduleItem)) {
            throw new IllegalArgumentException("No changes found in the Schedule.");
        }

        try {
            // Lưu scheduleItem vào CSDL và trả về đối tượng đã lưu
            return scheduleitemRepository.save(scheduleItem);

        } catch (IllegalArgumentException e) {
            // Xử lý lỗi chỉnh sửa và trả về phản hồi lỗi cho người dùng hoặc ứng dụng
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void deleteScheduleItem(int adminId, int scheduleItemId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        try {
            // Kiểm tra xem scheduleItemId có hợp lệ hay không
            Scheduleitem scheduleItem = scheduleitemRepository.findById(scheduleItemId)
                    .orElseThrow(() -> new IllegalArgumentException("ScheduleItem with id " + scheduleItemId + " not found."));

            // Kiểm tra xem scheduleItemId thuộc công ty của admin hay không
            Integer companyId = scheduleItem.getScheduleId().getTourId().getCompanyId().getId();
            if (companyId == null || !companyId.equals(adminCompanyId)) {
                throw new IllegalArgumentException("You do not have permission to perform this action");
            }

            scheduleimageRepository.deleteByScheduleItemId(scheduleItemId);
            // Thực hiện xóa scheduleItem từ CSDL
            scheduleitemRepository.delete(scheduleItem);
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi nếu có
            // Ví dụ: ném ra một exception khác hoặc trả về một phản hồi lỗi tùy vào yêu cầu của ứng dụng
            throw new IllegalStateException("Error deleting ScheduleItem with id " + scheduleItemId, e);
        }
    }

    @Override
    public List<Scheduleimage> viewScheduleImages(int adminId, int scheduleItemId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Check xem scheduleItemId có tồn tại hay không
        if (scheduleitemRepository.findById(scheduleItemId) == null) {
            throw new IllegalArgumentException("Schedule item id not found!");
        }

        // Lấy danh sách ScheduleImage theo companyId của admin
        return scheduleimageRepository.findAll().stream()
                .filter(scheduleImage -> scheduleImage.getScheduleItemId().getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId) && scheduleImage.getScheduleItemId().getId().equals(scheduleItemId))
                .collect(Collectors.toList());
    }

    @Override
    public Scheduleimage viewDetailScheduleImage(int adminId, int scheduleImageId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem scheduleImageId có tồn tại hay không
        Scheduleimage scheduleImage = scheduleimageRepository.findById(scheduleImageId)
                .orElseThrow(() -> new IllegalArgumentException("ScheduleImage with id " + scheduleImageId + " not found."));

        // Kiểm tra xem hình ảnh thuộc công ty của admin hay không
        if (!scheduleImage.getScheduleItemId().getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        // Trả về thông tin chi tiết của hình ảnh
        return scheduleImage;
    }

    @Override
    public Scheduleimage createScheduleImage(int adminId, Scheduleimage newScheduleimage) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem newScheduleimage có null hay không
        if (newScheduleimage == null) {
            throw new IllegalArgumentException("New ScheduleImage cannot be null.");
        }

        // Kiểm tra xem newScheduleimage có thuộc công ty của admin hay không
        if (!newScheduleimage.getScheduleItemId().getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        try {
            // Lưu mới ScheduleImage vào CSDL và trả về đối tượng đã lưu
            return scheduleimageRepository.save(newScheduleimage);
        } catch (DataAccessException ex) {
            // Xử lý lỗi CSDL nếu cần thiết, ví dụ ghi log hoặc thông báo lỗi
            // Sau đó, ném lại lỗi để controller hoặc mã gọi phương thức này có thể bắt được
            throw new RuntimeException("Error while saving ScheduleImage.", ex);
        } catch (RuntimeException ex) {
            // Xử lý các lỗi runtime nếu cần thiết
            // Sau đó, ném lại lỗi để controller hoặc mã gọi phương thức này có thể bắt được
            throw ex;
        }
    }

    @Override
    public Scheduleimage modifyScheduleImage(int adminId, Scheduleimage scheduleimage) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem scheduleimage có null hay không
        if (scheduleimage == null) {
            throw new IllegalArgumentException("ScheduleImage cannot be null.");
        }

        // Kiểm tra xem scheduleimage có thuộc công ty của admin hay không
        if (!scheduleimage.getScheduleItemId().getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        // Lấy scheduleimage từ CSDL bằng id
        Scheduleimage existingScheduleimage = scheduleimageRepository.findById(scheduleimage.getId())
                .orElseThrow(() -> new IllegalArgumentException("ScheduleImage with id " + scheduleimage.getId() + " not found."));

        // Kiểm tra sự thay đổi của scheduleimage trước khi lưu
        if (!isScheduleimageChanged(existingScheduleimage, scheduleimage)) {
            throw new Error("Nothing has changed in your edits.");
        }

        try {
            // Lưu scheduleimage đã thay đổi vào CSDL và trả về đối tượng đã lưu
            return scheduleimageRepository.save(scheduleimage);
        } catch (DataAccessException ex) {
            // Xử lý lỗi CSDL nếu cần thiết, ví dụ ghi log hoặc thông báo lỗi
            throw new RuntimeException("Error while saving ScheduleImage.", ex);
        } catch (RuntimeException ex) {
            // Xử lý các lỗi runtime nếu cần thiết
            throw ex;
        }
    }

    @Override
    public void deleteScheduleImage(int adminId, int scheduleImageId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy thông tin chi tiết của scheduleImage theo scheduleImageId
        Scheduleimage scheduleImage = scheduleimageRepository.findById(scheduleImageId)
                .orElseThrow(() -> new IllegalArgumentException("ScheduleImage with id " + scheduleImageId + " not found."));

        // Kiểm tra xem scheduleImage có thuộc công ty của admin hay không
        if (!scheduleImage.getScheduleItemId().getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        try {
            // Xóa scheduleImage khỏi CSDL
            scheduleimageRepository.delete(scheduleImage);
        } catch (DataAccessException ex) {
            // Xử lý lỗi CSDL nếu cần thiết, ví dụ ghi log hoặc thông báo lỗi
            // Sau đó, ném lại lỗi để controller hoặc mã gọi phương thức này có thể bắt được
            throw new RuntimeException("Error while deleting ScheduleImage with id " + scheduleImageId, ex);
        } catch (RuntimeException ex) {
            // Xử lý các lỗi runtime nếu cần thiết
            // Sau đó, ném lại lỗi để controller hoặc mã gọi phương thức này có thể bắt được
            throw ex;
        }
    }

    @Override
    public List<Serviceitem> viewServiceItems(int adminId, int scheduleId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Check xem scheduleId có tồn tại hay không
        if (scheduleRepository.findById(scheduleId) == null) {
            throw new IllegalArgumentException("Schedule Id not found!");
        }

        // Lấy danh sách ScheduleImage theo companyId của admin
        return serviceitemRepository.findAll().stream()
                .filter(serviceItem -> serviceItem.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId) && serviceItem.getScheduleId().getId().equals(scheduleId))
                .collect(Collectors.toList());
    }

    @Override
    public Serviceitem viewDetailServiceItem(int adminId, int serviceItemId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy thông tin chi tiết của Serviceitem theo serviceItemId
        Serviceitem serviceitem = serviceitemRepository.findById(serviceItemId)
                .orElseThrow(() -> new IllegalArgumentException("Serviceitem not found for serviceItemId " + serviceItemId));

        // Kiểm tra xem Serviceitem có thuộc công ty của admin không
        if (!serviceitem.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        return serviceitem;
    }

    @Override
    public Serviceitem createServiceItem(int adminId, Serviceitem newServiceitem) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem newServiceitem có thuộc công ty của admin không
        if (!newServiceitem.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        try {
            // Lưu newServiceitem vào cơ sở dữ liệu
            return serviceitemRepository.save(newServiceitem);
        } catch (Exception e) {
            // Xử lý lỗi khi lưu dữ liệu
            throw new RuntimeException("Error occurred while saving Serviceitem: " + e.getMessage());
        }
    }

    @Override
    public Serviceitem modifyServiceItem(int adminId, Serviceitem modifiedServiceitem) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách Serviceitem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Kiểm tra xem modifiedServiceitem có thuộc công ty của admin không
        if (!modifiedServiceitem.getScheduleId().getTourId().getCompanyId().getId().equals(adminCompanyId)) {
            throw new IllegalArgumentException("You do not have permission to perform this action.");
        }

        // Tìm dữ liệu gốc của serviceitem trong cơ sở dữ liệu
        Serviceitem originalServiceitem = serviceitemRepository.findById(modifiedServiceitem.getId())
                .orElseThrow(() -> new IllegalArgumentException("Serviceitem not found with id " + modifiedServiceitem.getId()));

        // Kiểm tra xem dữ liệu có thay đổi hay không
        if (!isServiceitemChanged(originalServiceitem, modifiedServiceitem)) {
            throw new IllegalArgumentException("Nothing has changed in your edits.");
        }

        try {
            // Lưu thông tin chỉnh sửa của serviceitem vào cơ sở dữ liệu
            return serviceitemRepository.save(modifiedServiceitem);
        } catch (Exception e) {
            // Xử lý lỗi khi lưu dữ liệu
            throw new RuntimeException("Error occurred while modifying Serviceitem: " + e.getMessage());
        }
    }

    @Override
    public void deleteServiceItem(int adminId, int serviceItemId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách Serviceitem thuộc công ty đó
        Integer adminCompanyId = userRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        try {
            // Kiểm tra xem serviceItemId có hợp lệ hay không
            Serviceitem serviceItem = serviceitemRepository.findById(serviceItemId)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceItem with id " + serviceItemId + " not found."));

            // Kiểm tra xem serviceItemId thuộc công ty của admin hay không
            Integer companyId = serviceItem.getScheduleId().getTourId().getCompanyId().getId();
            if (companyId == null || !companyId.equals(adminCompanyId)) {
                throw new IllegalArgumentException("You do not have permission to perform this action");
            }

            // Thực hiện xóa serviceItem từ CSDL
            serviceitemRepository.delete(serviceItem);
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi nếu có
            // Ví dụ: ném ra một exception khác hoặc trả về một phản hồi lỗi tùy vào yêu cầu của ứng dụng
            throw new IllegalStateException("Error deleting ServiceItem with id " + serviceItemId, e);
        }
    }

    // Phương thức kiểm tra xem Schedule đã thay đổi hay chưa
    private boolean isScheduleChanged(Schedule newSchedule, Schedule existingSchedule) {
        // So sánh các thuộc tính của newSchedule với existingSchedule
        // Nếu có ít nhất một thuộc tính khác nhau, trả về true
        // Nếu tất cả các thuộc tính giống nhau, trả về false
        return !Objects.equals(newSchedule.getStartDate(), existingSchedule.getStartDate())
                || !Objects.equals(newSchedule.getEndDate(), existingSchedule.getEndDate())
                || !Objects.equals(newSchedule.getQuantityPassenger(), existingSchedule.getQuantityPassenger())
                || !Objects.equals(newSchedule.getStartDate(), existingSchedule.getStartDate())
                || !Objects.equals(newSchedule.getPrice(), existingSchedule.getPrice())
                || !Objects.equals(newSchedule.getQuantityMin(), existingSchedule.getQuantityMin())
                || !Objects.equals(newSchedule.getQuantityMax(), existingSchedule.getQuantityMax());
    }

    // Phương thức kiểm tra xem scheduleItem đã thay đổi hay chưa
    private boolean isScheduleItemChanged(Scheduleitem newItem, Scheduleitem existingItem) {
        // So sánh các thuộc tính của newItem với existingItem
        // Nếu có ít nhất một thuộc tính khác nhau, trả về true
        // Nếu tất cả các thuộc tính giống nhau, trả về false
        return !Objects.equals(newItem.getNameDay(), existingItem.getNameDay())
                || !Objects.equals(newItem.getTitle(), existingItem.getTitle())
                || !Objects.equals(newItem.getDescription(), existingItem.getDescription());
    }

    // Phương thức kiểm tra sự thay đổi của scheduleimage
    private boolean isScheduleimageChanged(Scheduleimage existingScheduleimage, Scheduleimage newScheduleimage) {
        // So sánh các thuộc tính của scheduleimage, nếu có ít nhất một thuộc tính thay đổi thì trả về true
        // Nếu không có thay đổi, trả về false
        // Ví dụ:
        return !Objects.equals(existingScheduleimage.getImage(), newScheduleimage.getImage())
                || !Objects.equals(existingScheduleimage.getDescription(), newScheduleimage.getDescription());
    }

    // Phương thức kiểm tra xem dữ liệu có thay đổi hay không
    private boolean isServiceitemChanged(Serviceitem originalServiceitem, Serviceitem modifiedServiceitem) {
        // Kiểm tra từng thuộc tính của serviceitem xem có thay đổi hay không
        // Ví dụ:
        return !Objects.equals(originalServiceitem.getTitle(), modifiedServiceitem.getTitle())
                || !Objects.equals(originalServiceitem.getDescription(), modifiedServiceitem.getDescription());
    }

}
