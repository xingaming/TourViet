package fpt.aptech.api.service;

import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import java.util.List;


public interface IManagementStaffBySuperAdmin {
    
    List<Users> getAllAccounts();
    
    List<Users> filteredAccounts(UsersFilter filter);
    
    boolean blockAccount(int userId);
    
    boolean unblockAccount(int userId);
    
}
