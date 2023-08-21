package fpt.aptech.api.service;

import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.enums.Status;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import fpt.aptech.api.respository.UsersRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementStaffByArea implements IManagementStaffByArea {

    private final UsersRepository userRepository;

    @Autowired
    public ManagementStaffByArea(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Users> getAllTourguides(int userId) {
        // Lấy companyId của Area để chỉ lấy danh sách nhân viên thuộc công ty đó
        int staffCompanyId = userRepository.findById(userId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        return userRepository.findAll().stream()
                // Lọc theo roleId và companyId
                .filter(user -> user.getRoleId().getId().equals(RoleId.TOUR_GUIDE.getValue()) && user.getCompanyId().getId().equals(staffCompanyId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Users> tourguidesFilter(int userId, UsersFilter filter) {
        // Lấy companyId của Area để chỉ lấy danh sách nhân viên thuộc công ty đó
        int staffCompanyId = userRepository.findById(userId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        return userRepository.findAll().stream()
                // Lọc theo roleId và companyId
                .filter(user -> user.getRoleId().getId().equals(RoleId.TOUR_GUIDE.getValue()) && user.getCompanyId().getId().equals(staffCompanyId))
                // Áp dụng các bộ lọc
                .filter(user -> filter.getFirstName() == null || user.getFirstName().contains(filter.getFirstName()))
                .filter(user -> filter.getLastName() == null || user.getLastName().contains(filter.getLastName()))
                .filter(user -> filter.getEmail() == null || user.getEmail().contains(filter.getEmail()))
                .filter(user -> filter.getPhone() == null || user.getPhone().contains(filter.getPhone()))
                .collect(Collectors.toList());
    }

    @Override
    public Users tourguideDetails(int staffId, int userId) {
        // Lấy companyId của HQ để chỉ lấy danh sách nhân viên thuộc công ty đó
        int staffCompanyId = userRepository.findById(userId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        Optional<Users> optionalStaffMember = userRepository.findById(staffId);
        if (optionalStaffMember.isEmpty()) {
            throw new IllegalArgumentException("Staff member not found!");
        }

        Users staffMember = optionalStaffMember.get();
        if (!staffMember.getCompanyId().getId().equals(staffCompanyId) || staffMember.getRoleId().getId().equals(RoleId.AREA.getValue()) || staffMember.getRoleId().getId().equals(RoleId.HQ.getValue())) {
            throw new IllegalArgumentException("Staff member is not under your admin rights!");
        }

        return staffMember;
    }

    @Override
    public boolean blockAccount(int staffId, int userId) {
        // Lấy companyId của Area để chỉ lấy danh sách nhân viên thuộc công ty đó
        int staffCompanyId = userRepository.findById(userId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        Users staffMember = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        if (!staffMember.getCompanyId().getId().equals(staffCompanyId) || staffMember.getRoleId().getId().equals(RoleId.AREA.getValue()) || staffMember.getRoleId().getId().equals(RoleId.HQ.getValue())) {
            throw new IllegalArgumentException("Staff member is not under your admin rights!");
        }

        // Kiểm tra nếu tài khoản người dùng đã bị khóa
        if (staffMember.getStatus() == Status.BLOCK.getValue()) {
            return false;
        }

        staffMember.setStatus(Status.BLOCK.getValue());
        userRepository.save(staffMember);

        return true;
    }

    @Override
    public boolean unblockAccount(int staffId, int userId) {
        // Lấy companyId của Area để chỉ lấy danh sách nhân viên thuộc công ty đó
        int staffCompanyId = userRepository.findById(userId)
                .map(user -> user.getCompanyId().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        Users staffMember = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found"));

        if (!staffMember.getCompanyId().getId().equals(staffCompanyId) || staffMember.getRoleId().getId().equals(RoleId.AREA.getValue()) || staffMember.getRoleId().getId().equals(RoleId.HQ.getValue())) {
            throw new IllegalArgumentException("Staff member is not under your admin rights!");
        }

        // Kiểm tra nếu tài khoản người dùng chưa bị khóa
        if (staffMember.getStatus() == Status.ACTIVE.getValue()) {
            return false;
        }

        staffMember.setStatus(Status.ACTIVE.getValue());
        userRepository.save(staffMember);

        return true;
    }

}
