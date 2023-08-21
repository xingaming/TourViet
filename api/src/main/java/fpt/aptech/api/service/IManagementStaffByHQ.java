package fpt.aptech.api.service;

import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import java.util.List;


public interface IManagementStaffByHQ {
     // UC201 - View / Search Area and Tour Guide

    List<Users> getAllAreas(int userId);
    List<Users> getAllTourguides(int userId);

    List<Users> areasFilter(UsersFilter filter, int userId);
    List<Users> tourguideFilter(UsersFilter filter, int userId);

    // UC202 - Management Account
    Users getStaffDetails(int staffId, int userId);

    // UC204 - Block Account
    boolean blockAccount(int staffId, int userId);
    boolean unblockAccount(int staffId, int userId);
}
