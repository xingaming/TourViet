package fpt.aptech.api.service;

import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Tour;
import fpt.aptech.api.models.TourCreate;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.respository.BookingRepository;
import fpt.aptech.api.respository.PaymentRepository;
import fpt.aptech.api.respository.ReviewRepository;
import fpt.aptech.api.respository.ScheduleRepository;
import fpt.aptech.api.respository.ScheduleimageRepository;
import fpt.aptech.api.respository.ScheduleitemRepository;
import fpt.aptech.api.respository.ServiceitemRepository;
import fpt.aptech.api.respository.SlotRepository;
import fpt.aptech.api.respository.TourCreateRepository;
import fpt.aptech.api.respository.TourRepository;
import fpt.aptech.api.respository.UsersRepository;
import fpt.aptech.api.respository.informationbookingRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementToursForHQAndAreaService implements IManagementToursForHQAndArea {

    private final TourRepository tourRepository;
    private final TourCreateRepository tourCreateRepository;
    private final ReviewRepository reviewRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleimageRepository scheduleimageRepository;
    private final ScheduleitemRepository scheduleitemRepository;
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ServiceitemRepository serviceitemRepository;
    private final informationbookingRepository InformationbookingRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public ManagementToursForHQAndAreaService(TourRepository tourRepository, ReviewRepository reviewRepository, ScheduleRepository scheduleRepository,
            SlotRepository slotRepository, BookingRepository bookingRepository, PaymentRepository paymentRepository, ScheduleimageRepository scheduleimageRepository, ScheduleitemRepository scheduleitemRepository, ServiceitemRepository serviceitemRepository, informationbookingRepository InformationbookingRepository, UsersRepository usersRepository, TourCreateRepository tourCreateRepository1) {
        this.tourRepository = tourRepository;
        this.reviewRepository = reviewRepository;
        this.scheduleRepository = scheduleRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.scheduleimageRepository = scheduleimageRepository;
        this.scheduleitemRepository = scheduleitemRepository;
        this.serviceitemRepository = serviceitemRepository;
        this.InformationbookingRepository = InformationbookingRepository;
        this.usersRepository = usersRepository;
        this.tourCreateRepository = tourCreateRepository1;
    }

    @Override
    public List<Tour> viewTours(int adminId) {
        // Lấy companyId của HQ hoặc Area để chỉ lấy danh sách ScheduleItem thuộc công ty đó
        Integer adminCompanyId = usersRepository.findById(adminId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid adminId"));

        // Check xem adminId có tồn tại hay không
        if (adminCompanyId == null) {
            throw new IllegalArgumentException("Company Id not found for adminId " + adminId);
        }

        // Lấy danh sách ScheduleImage theo companyId của admin
        return tourRepository.findAll().stream()
                .filter(tour -> tour.getCompanyId().getId().equals(adminCompanyId))
                .collect(Collectors.toList());

    }

    @Override
    public Tour viewDetailTour(int adminId, int tourId) {
        // Kiểm tra xem tour có tồn tại hay không
        Optional<Tour> existingTourOptional = tourRepository.findById(tourId);
        if (existingTourOptional.isEmpty()) {
            throw new IllegalArgumentException("Tour not found");
        }

        // Kiểm tra quyền hạn
        Tour existingTour = existingTourOptional.get();
        Users user = usersRepository.findById(adminId).get();
        if (!existingTour.getCompanyId().getId().equals(user.getCompanyId().getId())) {
            throw new RuntimeException("You have no rights this tour");
        }

        return existingTour;
    }

    @Override
    public Tour createTour(int adminId, Tour tour) {
        // Kiểm tra null
        if (tour == null) {
            throw new IllegalArgumentException("Tour object is null");
        }

        //set companyId cho tour
        Users user = usersRepository.findById(adminId).get();
        tour.setCompanyId(user.getCompanyId());

        // Kiểm tra và xác thực các trường bắt buộc của tour
        if (tour.getName() == null || tour.getName().isEmpty()) {
            throw new IllegalArgumentException("Tour name is required");
        }

        // Thực hiện các kiểm tra và xác thực khác với đối tượng tour
        // ...
        try {
            // Lưu đối tượng tour vào cơ sở dữ liệu
            return tourRepository.save(tour);
        } catch (Exception e) {
            // Xử lý ngoại lệ khi lưu vào cơ sở dữ liệu không thành công
            throw new RuntimeException("Failed to create tour: " + e.getMessage());
        }
    }

    @Override
    public Tour modifyTour(int adminId, int id, Tour tour) {
        Optional<Tour> optionalTour = tourRepository.findById(id);
        if (optionalTour.isEmpty()) {
            throw new IllegalArgumentException("Tour not found");
        }

        // Kiểm tra quyền hạn
        Tour existingTour = optionalTour.get();
        Users user = usersRepository.findById(adminId).get();
        if (!existingTour.getCompanyId().getId().equals(user.getCompanyId().getId())) {
            throw new RuntimeException("You have no rights this tour");
        }

        //set companyId cho tour
        tour.setCompanyId(user.getCompanyId());
        //set các giá trị khác
        tour.setViews(existingTour.getViews());
        tour.setIsTopPick(existingTour.getIsTopPick());
        tour.setRate(existingTour.getRate());

        // Kiểm tra và xác thực dữ liệu
        if (tour.getName() != null && !tour.getName().isEmpty()) {
            existingTour.setName(tour.getName());
        }
        if (tour.getDescription() != null && !tour.getDescription().isEmpty()) {
            existingTour.setDescription(tour.getDescription());
        }
        if (tour.getDiscount() >= 0 && tour.getDiscount() <= 100) {
            existingTour.setDiscount(tour.getDiscount());
        }
        if (tour.getImage() != null && !tour.getImage().isEmpty()) {
            existingTour.setImage(tour.getImage());
        }

        // Thực hiện các kiểm tra và xác thực khác với đối tượng tour
        // ...
        try {
            // Lưu đối tượng tour đã được cập nhật vào cơ sở dữ liệu
            return tourRepository.save(existingTour);
        } catch (Exception e) {
            throw new RuntimeException("Failed to modify tour: " + e.getMessage());
        }
    }

    @Override
    public Tour removeTour(int adminId, int id) {
        // Kiểm tra xem tour có tồn tại hay không
        Optional<Tour> existingTourOptional = tourRepository.findById(id);
        if (existingTourOptional.isEmpty()) {
            throw new IllegalArgumentException("Tour not found");
        }

        // Kiểm tra quyền hạn
        Tour existingTour = existingTourOptional.get();
        Users user = usersRepository.findById(adminId).get();
        if (!existingTour.getCompanyId().getId().equals(user.getCompanyId().getId())) {
            throw new RuntimeException("You have no rights this tour");
        }

        // Xóa đánh giá liên quan đến tour
//        reviewRepository.deleteReviewsBySchedule(id);
        // Xóa lịch trình và các mục lịch trình liên quan đến tour
        List<Schedule> schedules = scheduleRepository.findSchedulesByTourId(id);
        for (Schedule schedule : schedules) {
            int scheduleId = schedule.getId();

            List<Scheduleitem> scheduleItems = scheduleitemRepository.findScheduleItemsByScheduleId(scheduleId);
            for (Scheduleitem scheduleItem : scheduleItems) {
                int scheduleItemId = scheduleItem.getId();
                scheduleimageRepository.deleteByScheduleItemId(scheduleItemId);
            }

            scheduleitemRepository.deleteSchedulesItemByScheduleId(scheduleId);
            serviceitemRepository.deleteServiceItemByScheduleId(scheduleId);
            // Xóa đặt tour và các thanh toán liên quan đến tour
            List<Booking> bookings = bookingRepository.findBookingsByScheduleId(scheduleId);
            for (Booking booking : bookings) {
                int bookingId = booking.getId();
                paymentRepository.deleteByBookingId(bookingId);
                InformationbookingRepository.deleteIn4BookingByBookingId(bookingId);
            }

            // Xóa các đặt tour liên quan đến tour
            bookingRepository.deleteBookingsByScheduleId(scheduleId);
        }

        // Xóa lịch trình liên quan đến tour
        scheduleRepository.deleteSchedulesByTour(id);

        // Xóa các khe trống liên quan đến tour
        slotRepository.deleteSlotsByTour(id);

        // Xóa tour
        tourRepository.deleteById(id);

        // Trả về tour đã bị xóa
        return existingTourOptional.get();
    }

    @Override
    public List<TourCreate> viewMadeTours() {

        return tourCreateRepository.findAll();
    }

    @Override
    public TourCreate viewMadeToursById(int TourId) {
        Optional<TourCreate> existingTourOptional = tourCreateRepository.findById(TourId);
        return existingTourOptional.orElse(null); // Return the TourCreate object, or null if not present
    }

}
