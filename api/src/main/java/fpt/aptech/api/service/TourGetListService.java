package fpt.aptech.api.service;

import fpt.aptech.api.models.Address;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleimage;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Serviceitem;
import fpt.aptech.api.models.Tour;
import fpt.aptech.api.models.TourFilter;
import fpt.aptech.api.respository.AddressRepository;
import fpt.aptech.api.respository.ReviewRepository;
import fpt.aptech.api.respository.ScheduleRepository;
import fpt.aptech.api.respository.ScheduleimageRepository;
import fpt.aptech.api.respository.ScheduleitemRepository;
import fpt.aptech.api.respository.ServiceitemRepository;
import fpt.aptech.api.respository.TourRepository;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TourGetListService implements ITourGetList {

    private final TourRepository tourRepository;
    private final ReviewRepository reviewRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleitemRepository scheduleitemRepository;
    private final ScheduleimageRepository scheduleimageRepository;
    private final ServiceitemRepository serviceRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public TourGetListService(TourRepository tourRepository, ReviewRepository reviewRepository, ScheduleRepository scheduleRepository, ScheduleitemRepository scheduleitemRepository, ScheduleimageRepository scheduleimageRepository, ServiceitemRepository serviceRepository, AddressRepository addressRepository) {
        this.tourRepository = tourRepository;
        this.reviewRepository = reviewRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleitemRepository = scheduleitemRepository;
        this.scheduleimageRepository = scheduleimageRepository;
        this.serviceRepository = serviceRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<Tour> getListTour() {
        return tourRepository.findAll();
    }
    
    @Override
    public List<Tour> getAllTour() {
        return tourRepository.findAll();
    }

    @Override
    public Tour getTourById(int id) {
        return tourRepository.findById(id).get();
    }

    @Override
    public List<Tour> getFilteredTours(TourFilter filter, String sortBy, String sortOrder) {
        // Xử lý filter
        List<Tour> filteredTours;

        // Lấy tất cả các tour
        filteredTours = tourRepository.findAll();

        if (filter.getName() != null) {
            filteredTours = filteredTours.stream()
                    .filter(tour -> tour.getName().contains(filter.getName()))
                    .collect(Collectors.toList());
        }

        if (filter.getDiscount() != null) {
            filteredTours = filteredTours.stream()
                    .filter(tour -> Objects.equals(tour.getDiscount(), filter.getDiscount()))
                    .collect(Collectors.toList());
        }

        if (filter.getTransportId() != null) {
            filteredTours = filteredTours.stream()
                    .filter(tour -> Objects.equals(tour.getTransportId().getId(), filter.getTransportId()))
                    .collect(Collectors.toList());
        }

        // Xử lý sắp xếp
        //asc là tăng dần còn, desc là giảm dần
        if (sortBy != null && sortOrder != null) {
            if (sortBy.equalsIgnoreCase("discount")) {
                if (sortOrder.equalsIgnoreCase("asc")) {
                    filteredTours.sort(Comparator.comparing(Tour::getDiscount));
                } else if (sortOrder.equalsIgnoreCase("desc")) {
                    filteredTours.sort(Comparator.comparing(Tour::getDiscount).reversed());
                }
            }
        }

        return filteredTours;
    }

    @Override
    public List<Review> getReviewByTour(int tourId) {
        return reviewRepository.getReviewsByTour(tourId);
    }

    @Override
    public List<Tour> getTourByRegion(int regionId) {
        return tourRepository.getTourByRegion(regionId);
    }
    
    

    @Override
    public List<Tour> getFilteredToursPortal(boolean lowestPrice, boolean mostPopular, boolean isTopPicks) {
        // Lấy tất cả các tour
        List<Tour> filteredTours = tourRepository.findAll();

        // Xử lý filter
        

        if (mostPopular) {
            // Sắp xếp danh sách tour theo lượt xem giảm dần
            filteredTours = filteredTours.stream()
                    .sorted(Comparator.comparing(Tour::getViews).reversed())
                    .collect(Collectors.toList());
        }

        if (isTopPicks) {
            // Lọc danh sách các tour là top picks
            filteredTours = filteredTours.stream()
                    .filter(Tour::getIsTopPick)
                    .collect(Collectors.toList());
        }
        return filteredTours;
    }

    @Override
    public List<Tour> getFilteredTour(Date startDate, Date endDate) {
        // Kiểm tra nếu startDate hoặc endDate là null
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate must not be null.");
        }

        // Kiểm tra nếu startDate lớn hơn endDate
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate.");
        }

        // Sử dụng phương thức của TourRepository để lấy danh sách các tour dựa trên startDate và endDate của schedule
        return tourRepository.findByScheduleStartDateBetween(startDate, endDate);
    }
    
    @Override
    public List<Schedule> getFilteredByDate(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate must not be null.");
        }

        // Kiểm tra nếu startDate lớn hơn endDate
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate.");
        }

        // Sử dụng phương thức của TourRepository để lấy danh sách các tour dựa trên startDate và endDate của schedule
        return scheduleRepository.findSchedulesByDate(startDate, endDate);
    }
    

    @Override
    public List<Tour> getFilteredToursMobile(Date startDate, Date endDate, Long minPrice, Long maxPrice, Float rate) {

        // Xử lý filter
        List<Tour> filteredTours;

        // Lấy tất cả các tour
        filteredTours = tourRepository.findAll();

        // Kiểm tra nếu startDate hoặc endDate là null
        if ((startDate != null && endDate != null) || (startDate == null && endDate == null)) {
            if (startDate != null & endDate != null) {
                // Kiểm tra nếu startDate lớn hơn endDate
                if (startDate.after(endDate)) {
                    throw new IllegalArgumentException("startDate must not be after endDate.");
                }

                filteredTours = tourRepository.findByScheduleStartDateBetween(startDate, endDate);
            }
        } else {
            throw new IllegalArgumentException("startDate and endDate must both have values or both be null.");
        }

//        if ((minPrice != null && maxPrice != null) || (minPrice == null && maxPrice == null)) {
//            if (minPrice != null && maxPrice != null) {
//
//                if (minPrice > maxPrice) {
//                    throw new IllegalArgumentException("The maximum price cannot be lower than the minimum price.");
//                }
//
//                filteredTours = filteredTours.stream()
//                        .filter(tour -> tour.getPrice() >= minPrice && tour.getPrice() <= maxPrice)
//                        .collect(Collectors.toList());
//            }
//        } else {
//            throw new IllegalArgumentException("minPrice and maxPrice must both have values or both be null.");
//        }

        if (rate != null) {
            // Sắp xếp danh sách tour theo rate giảm dần
            filteredTours = filteredTours.stream()
                    .sorted(Comparator.comparing(Tour::getRate, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }

        return filteredTours;
    }
    
    @Override
    public List<Schedule> getAllSchedule() {
        return scheduleRepository.findAllSchedules();
    }

    @Override
    public List<Schedule> getScheduleByTourId(int tourId) {
        // Kiểm tra nếu tourId là số dương
        if (tourId <= 0) {
            throw new IllegalArgumentException("tourId must be a positive integer.");
        }

        // Lấy danh sách lịch trình từ scheduleRepository bằng tourId
        List<Schedule> schedules = scheduleRepository.findSchedulesByTourId(tourId);

        // Kiểm tra nếu danh sách lịch trình là null hoặc rỗng
        if (schedules == null || schedules.isEmpty()) {
            throw new RuntimeException("No schedules found for the specified tourId.");
        }

        return schedules;
    }
    
    
    @Override
    public Schedule getScheduleById(int Id) {
        // Kiểm tra nếu tourId là số dương
        if (Id <= 0) {
            throw new IllegalArgumentException("tourId must be a positive integer.");
        }

        // Lấy danh sách lịch trình từ scheduleRepository bằng tourId
        Schedule schedules = scheduleRepository.findSchedulesById(Id);

        // Kiểm tra nếu danh sách lịch trình là null hoặc rỗng
        if (schedules == null) {
            throw new RuntimeException("No schedules found for the specified tourId.");
        }

        return schedules;
    }

    @Override
    public List<Scheduleitem> getScheduleItemByScheduleId(int scheduleId) {
        // Kiểm tra nếu scheduleId không tồn tại
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException("Schedule with id " + scheduleId + " does not exist.");
        }

        // Lấy danh sách ScheduleItem theo scheduleId
        return scheduleitemRepository.findScheduleItemsByScheduleId(scheduleId);
    }

    @Override
    public List<Scheduleimage> getScheduleImageByScheduleId(int scheduleItemId) {
        // Kiểm tra nếu scheduleItemId không tồn tại
        if (!scheduleitemRepository.existsById(scheduleItemId)) {
            throw new IllegalArgumentException("ScheduleItem with id " + scheduleItemId + " does not exist.");
        }

        // Lấy danh sách ScheduleImage theo scheduleItemId
        return scheduleimageRepository.findScheduleImagesByScheduleItemId(scheduleItemId);
    }

    @Override
    public List<Serviceitem> getServiceItemByScheduleId(int scheduleId) {
        // Kiểm tra nếu scheduleId không tồn tại
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException("Schedule with id " + scheduleId + " does not exist.");
        }

        // Lấy danh sách ServiceItem theo scheduleId
        return serviceRepository.findServiceItemByScheduleId(scheduleId);
    }

    @Override
    public Scheduleitem getScheduleItemById(int scheduleItemId) {
        return scheduleitemRepository.findById(scheduleItemId).get();
    }

    @Override
    public Scheduleimage getScheduleImageById(int scheduleImageId) {
        return scheduleimageRepository.findById(scheduleImageId).get();
    }

    @Override
    public Serviceitem getServiceItemById(int serviceItemId) {
        return serviceRepository.findById(serviceItemId).get();
    }

    // http://localhost:8888/api/tours/address/region/{regionId}
    @Override
    public List<Address> getAddressByRegionId(int regionId) {
        return addressRepository.getAddressByRegionId(regionId);
    }

    @Override
    public List<Address> getAddress() {
        return addressRepository.findAll();
    }

    


    

}
