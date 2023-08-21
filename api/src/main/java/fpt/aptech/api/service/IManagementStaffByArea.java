package fpt.aptech.api.service;

import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import java.util.List;


public interface IManagementStaffByArea {
    
    List<Users> getAllTourguides(int userId);
    List<Users> tourguidesFilter(int userId, UsersFilter filter);
    
    Users tourguideDetails(int staffId, int userId);
    
    boolean blockAccount(int staffId, int userId);
    boolean unblockAccount(int staffId, int userId);
    
}
