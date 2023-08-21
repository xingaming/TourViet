package fpt.aptech.api.resource.user;

import fpt.aptech.api.models.Address;
import fpt.aptech.api.models.Booking;
import fpt.aptech.api.models.Region;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleimage;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Serviceitem;
import fpt.aptech.api.models.Tour;
import fpt.aptech.api.models.TourCreate;
import fpt.aptech.api.models.TourFilter;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.service.IBooking;
import fpt.aptech.api.service.ITourCreateService;
import fpt.aptech.api.service.ITourGetList;
import fpt.aptech.api.service.RegionGetListService;
import java.util.Date;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/tours")
public class TourGetListResource {

    @Autowired
    ITourGetList service;

    @Autowired
    RegionGetListService regionService;

    @Autowired
    ITourCreateService createTourService;
    @Autowired
    IBooking bookingTourService;
    
    //Lấy danh sách tất cả các tour
    @GetMapping("/")

    public List<Tour> getList() {
        return service.getListTour();
    }

    @GetMapping("/region")
    @ResponseStatus(HttpStatus.OK)
    public List<Region> list() {
        return regionService.getAllRegion();
    }

    //Lấy danh sách tất cả các tour theo Id
    @GetMapping("/{id}")
    public Tour getTourById(@PathVariable int id) {
        return service.getTourById(id);
    }

    @GetMapping("/all")
    public List<Tour> getAllTour() {
        return service.getAllTour();
    }

    @GetMapping("/filter")
    public List<Tour> getFilteredTours(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer discount,
            @RequestParam(required = false) Integer transportId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        TourFilter filter = new TourFilter();
        filter.setName(name);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setDiscount(discount);
        filter.setTransportId(transportId);

        return service.getFilteredTours(filter, sortBy, sortOrder);
    }

    @GetMapping("/{tourId}/reviews")
    public ResponseEntity<List<Review>> getReviewByTour(@PathVariable int tourId) {
        List<Review> reviews = service.getReviewByTour(tourId);
        if (reviews.isEmpty()) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(reviews);
        }
    }

    @GetMapping("/regions/{regionId}/tours")
    public ResponseEntity<List<Tour>> getTourByRegion(@PathVariable int regionId) {
        List<Tour> tours = service.getTourByRegion(regionId);
        if (tours.isEmpty()) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(tours);
        }
    }

    @GetMapping("/region/tour")
    public ResponseEntity<List<Schedule>> getTourByRegion2() {
        List<Schedule> schedule = service.getAllSchedule();
        if (schedule.isEmpty()) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(schedule);
        }
    }

    @GetMapping("/filteredPortal")
    public ResponseEntity<List<Tour>> getFilteredTours(@RequestParam(required = false) boolean lowestPrice,
            @RequestParam(required = false) boolean mostPopular,
            @RequestParam(required = false) boolean isTopPicks) {
        if (!lowestPrice && !mostPopular && !isTopPicks) {
            // Nếu không có filter nào được áp dụng, lấy tất cả các tour
            List<Tour> allTours = service.getListTour();
            return ResponseEntity.ok(allTours);
        }

        // Xử lý filter và lấy danh sách các tour phù hợp
        List<Tour> filteredTours = service.getFilteredToursPortal(lowestPrice, mostPopular, isTopPicks);
        return ResponseEntity.ok(filteredTours);
    }

    @GetMapping("/filtered")
    public ResponseEntity<?> getFilteredTour(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<Tour> filteredTours = service.getFilteredTour(startDate, endDate);
            return ResponseEntity.ok(filteredTours);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/filteredByDate")
    public ResponseEntity<?> getfilteredByDate(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<Schedule> filteredTours = service.getFilteredByDate(startDate, endDate);
            return ResponseEntity.ok(filteredTours);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/filteredMobile")
    public ResponseEntity<?> getFilteredToursMobile(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "minPrice", required = false) Long minPrice,
            @RequestParam(value = "maxPrice", required = false) Long maxPrice,
            @RequestParam(value = "rate", required = false) Float rate) {
        try {
            List<Tour> filteredTours = service.getFilteredToursMobile(startDate, endDate, minPrice, maxPrice, rate);
            return ResponseEntity.ok(filteredTours);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<?> getScheduleByTourId(@PathVariable int tourId) {
        try {
            List<Schedule> schedules = service.getScheduleByTourId(tourId);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable int id) {
        try {
            Schedule schedules = service.getScheduleById(id);
            return ResponseEntity.ok(schedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/scheduleItem/{id}")
    public ResponseEntity<?> getScheduleItemById(@PathVariable int id) {
        try {
            Scheduleitem scheduleitem = service.getScheduleItemById(id);
            return ResponseEntity.ok(scheduleitem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/scheduleImage/{id}")
    public ResponseEntity<?> getScheduleImageById(@PathVariable int id) {
        try {
            Scheduleimage scheduleimage = service.getScheduleImageById(id);
            return ResponseEntity.ok(scheduleimage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/serviceItem/{id}")
    public ResponseEntity<?> getServiceItemById(@PathVariable int id) {
        try {
            Serviceitem serviceItem = service.getServiceItemById(id);
            return ResponseEntity.ok(serviceItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/scheduleId/{scheduleId}/scheduleItems")
    public ResponseEntity<?> getScheduleItemsByScheduleId(@PathVariable int scheduleId) {
        try {
            List<Scheduleitem> scheduleItems = service.getScheduleItemByScheduleId(scheduleId);
            return ResponseEntity.ok(scheduleItems);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/scheduleItemId/{scheduleItemId}/scheduleImages")
    public ResponseEntity<?> getScheduleImagesByScheduleItemId(@PathVariable int scheduleItemId) {
        try {
            List<Scheduleimage> scheduleImages = service.getScheduleImageByScheduleId(scheduleItemId);
            return ResponseEntity.ok(scheduleImages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/scheduleId/{scheduleId}/serviceItems")
    public ResponseEntity<?> getServiceItemsByScheduleId(@PathVariable int scheduleId) {
        try {
            List<Serviceitem> serviceItems = service.getServiceItemByScheduleId(scheduleId);
            return ResponseEntity.ok(serviceItems);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("address/region/{regionId}")
    public List<Address> getAddressByRegionId(@PathVariable Integer regionId) {
        return service.getAddressByRegionId(regionId);
    }

    @GetMapping("address/region")
    public List<Address> getAddress() {
        return service.getAddress();
    }

    @PostMapping("create-tour")
    public void CreateTour(@RequestBody TourCreate tour) {
        createTourService.save(tour);
    }

    @PostMapping("/booking-tour")
    public void bookTour(@RequestBody Booking tour) {
        bookingTourService.bookTour(tour);
    }
}
