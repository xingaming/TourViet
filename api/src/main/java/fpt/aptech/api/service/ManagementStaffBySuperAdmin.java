package fpt.aptech.api.service;

import fpt.aptech.api.enums.RoleId;
import fpt.aptech.api.enums.Status;
import fpt.aptech.api.models.Users;
import fpt.aptech.api.models.UsersFilter;
import fpt.aptech.api.respository.UsersRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementStaffBySuperAdmin implements IManagementStaffBySuperAdmin {

    private final UsersRepository userRepository;

    @Autowired
    public ManagementStaffBySuperAdmin(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<Users> getAllAccounts() {
        // Declare
        List<Users> users;
        // Get all
        users = userRepository.findAll();
        // Filter
        users = users.stream()
                .filter(user -> !user.getRoleId().getId().equals(RoleId.SUPER_ADMIN.getValue()))
                .collect(Collectors.toList());

        return users;
    }

    @Override
    public List<Users> filteredAccounts(UsersFilter filter) {
        return userRepository.findAll().stream()
                // Lấy tất cả trừ super admin
                .filter(user -> !user.getRoleId().getId().equals(RoleId.SUPER_ADMIN.getValue()))
                // Áp dụng các bộ lọc
                .filter(user -> filter.getFirstName() == null || user.getFirstName().contains(filter.getFirstName()))
                .filter(user -> filter.getLastName() == null || user.getLastName().contains(filter.getLastName()))
                .filter(user -> filter.getEmail() == null || user.getEmail().contains(filter.getEmail()))
                .filter(user -> filter.getPhone() == null || user.getPhone().contains(filter.getPhone()))
                .filter(user -> filter.getCompanyId() == null || (user.getCompanyId() != null && user.getCompanyId().getId().equals(filter.getCompanyId())))
                .filter(user -> filter.getRoleId() == null || user.getRoleId().getId().equals(filter.getRoleId()))
                .filter(user -> filter.getStatus() == null || (user.getStatus() != null && user.getStatus().equals(filter.getStatus())))
                .collect(Collectors.toList());
    }

    @Override
    public boolean blockAccount(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User member not found"));

        if (user.getRoleId().getId().equals(RoleId.SUPER_ADMIN.getValue())) {
            throw new IllegalArgumentException("Staff member is not under your admin rights!");
        }

        // Kiểm tra nếu tài khoản người dùng đã bị khóa
        if (user.getStatus() == Status.BLOCK.getValue()) {
            return false;
        }

        user.setStatus(Status.BLOCK.getValue());
        userRepository.save(user);

        return true;
    }

    @Override
    public boolean unblockAccount(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User member not found"));

        if (user.getRoleId().getId().equals(RoleId.SUPER_ADMIN.getValue())) {
            throw new IllegalArgumentException("Staff member is not under your admin rights!");
        }

        // Kiểm tra nếu tài khoản người dùng chưa bị khóa
        if (user.getStatus() == Status.ACTIVE.getValue()) {
            return false;
        }

        user.setStatus(Status.ACTIVE.getValue());
        userRepository.save(user);

        return true;
    }

}
