package fpt.aptech.api.service;

import fpt.aptech.api.models.Address;
import fpt.aptech.api.models.Review;
import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleimage;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Serviceitem;
import fpt.aptech.api.models.Tour;
import fpt.aptech.api.models.TourFilter;
import java.util.Date;
import java.util.List;

public interface ITourGetList {

    public List<Tour> getListTour();

    public List<Tour> getAllTour();

    public Tour getTourById(int id);

    public List<Tour> getFilteredTours(TourFilter filter, String sortBy, String sortOrder);

    public List<Review> getReviewByTour(int tourId);

    public List<Tour> getTourByRegion(int regionId);

    public List<Tour> getFilteredToursPortal(boolean lowestPrice, boolean mostPopular, boolean isTopPicks);

    public List<Tour> getFilteredTour(Date startDate, Date endDate);

    public List<Schedule> getFilteredByDate(Date startDate, Date endDate);

    public List<Tour> getFilteredToursMobile(Date startDate, Date endDate, Long minPrice, Long maxPrice, Float rate);

    public List<Schedule> getScheduleByTourId(int tourId);

    public Schedule getScheduleById(int Id);

    public List<Schedule> getAllSchedule();

    public List<Scheduleitem> getScheduleItemByScheduleId(int scheduleId);

    public List<Scheduleimage> getScheduleImageByScheduleId(int scheduleId);

    public List<Serviceitem> getServiceItemByScheduleId(int scheduleId);

    public Scheduleitem getScheduleItemById(int scheduleItemId);

    public Scheduleimage getScheduleImageById(int scheduleImageId);

    public Serviceitem getServiceItemById(int serviceItemId);

    public List<Address> getAddressByRegionId(int regionId);

    public List<Address> getAddress();

}
