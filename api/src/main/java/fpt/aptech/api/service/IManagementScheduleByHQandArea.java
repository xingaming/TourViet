package fpt.aptech.api.service;

import fpt.aptech.api.models.Schedule;
import fpt.aptech.api.models.Scheduleimage;
import fpt.aptech.api.models.Scheduleitem;
import fpt.aptech.api.models.Serviceitem;
import java.util.List;


public interface IManagementScheduleByHQandArea {
    //Management Schedule
    List<Schedule> viewSchedule(int adminId, int tourId);
    Schedule viewDetailSchedule(int adminId, int scheduleId);
    Schedule createSchedule(int adminId, Schedule newSchedule);
    Schedule modifySchedule(int adminId, Schedule schedule);
    void deleteSchedule(int adminId, int scheduleId);
    // Management Schedule item
    List<Scheduleitem> viewScheduleItems(int adminId, int scheduleId);
    Scheduleitem viewDetailScheduleItem(int adminId, int scheduleItemId);
    Scheduleitem createScheduleItem(int adminId, Scheduleitem newScheduleItem);
    Scheduleitem modifyScheduleItem(int adminId, Scheduleitem scheduleItem);
    void deleteScheduleItem(int adminId, int scheduleItemId);
    // Management Schedule image
    List<Scheduleimage> viewScheduleImages(int adminId, int scheduleItemId);
    Scheduleimage viewDetailScheduleImage(int adminId, int scheduleImageId);
    Scheduleimage createScheduleImage(int adminId, Scheduleimage newScheduleimage);
    Scheduleimage modifyScheduleImage(int adminId, Scheduleimage scheduleimage);
    void deleteScheduleImage(int adminId, int scheduleImageId);
    // Management Service item
    List<Serviceitem> viewServiceItems(int adminId, int scheduleId);
    Serviceitem viewDetailServiceItem(int adminId, int serviceItemId);
    Serviceitem createServiceItem(int adminId, Serviceitem newServiceitem);
    Serviceitem modifyServiceItem(int adminId, Serviceitem serviceitem);
    void deleteServiceItem(int adminId, int serviceItemId);
    
}
