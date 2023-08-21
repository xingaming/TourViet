package fpt.aptech.portal.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findById", query = "SELECT u FROM Users u WHERE u.id = :id"),
    @NamedQuery(name = "Users.findByFirstName", query = "SELECT u FROM Users u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "Users.findByLastName", query = "SELECT u FROM Users u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "Users.findByEmail", query = "SELECT u FROM Users u WHERE u.email = :email"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password"),
    @NamedQuery(name = "Users.findByAddress", query = "SELECT u FROM Users u WHERE u.address = :address"),
    @NamedQuery(name = "Users.findByPhone", query = "SELECT u FROM Users u WHERE u.phone = :phone"),
    @NamedQuery(name = "Users.findByAvatar", query = "SELECT u FROM Users u WHERE u.avatar = :avatar"),
    @NamedQuery(name = "Users.findByVerifyCode", query = "SELECT u FROM Users u WHERE u.verifyCode = :verifyCode"),
    @NamedQuery(name = "Users.findByToken", query = "SELECT u FROM Users u WHERE u.token = :token"),
    @NamedQuery(name = "Users.findByTotalInvite", query = "SELECT u FROM Users u WHERE u.totalInvite = :totalInvite"),
    @NamedQuery(name = "Users.findByCoin", query = "SELECT u FROM Users u WHERE u.coin = :coin"),
    @NamedQuery(name = "Users.findByStatus", query = "SELECT u FROM Users u WHERE u.status = :status")})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "verify_code")
    private String verifyCode;
    @Column(name = "token")
    private String token;
    @Column(name = "total_invite")
    private Integer totalInvite;
    @Column(name = "coin")
    private BigDecimal coin;
    @Column(name = "status")
    @JsonDeserialize
    private Integer status;
    @ManyToMany(mappedBy = "usersList")
    private List<Tour> tourList;
    @OneToMany(mappedBy = "userId")
    private List<News> newsList;
    @OneToMany(mappedBy = "userId")
    private List<Booking> bookingList;
    @OneToMany(mappedBy = "userId")
    private List<Coupon> couponList;
    @OneToMany(mappedBy = "guideId")
    private List<Tour> tourList1;
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ManyToOne
    private Company companyId;
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @ManyToOne
    private Roles roleId;
    @OneToMany(mappedBy = "userId")
    private List<Post> postList;
    @OneToMany(mappedBy = "userId")
    private List<Review> reviewList;
    @OneToMany(mappedBy = "userId")
    private List<Usergroup> usergroupList;
    @OneToMany(mappedBy = "userId")
    private List<Payment> paymentList;
    @OneToMany(mappedBy = "senderId")
    private List<Invite> inviteList;
    @OneToMany(mappedBy = "recipientId")
    private List<Invite> inviteList1;

    public boolean isEmailValid() {
        return email != null && !email.isEmpty() && isValidEmailFormat(email);
    }

    public boolean isPasswordValid() {
        return password != null && !password.trim().isEmpty() && password.length() >= 8; // Password must be at least 8 characters long
    }

    public boolean isPhoneValid() {
        // Customize this method based on your phone number validation rules
        return phone != null && !phone.trim().isEmpty() && phone.matches("\\d{10,12}"); // Assuming phone numbers are 10-12 digits
    }

    public boolean isFname() {
        // Kiểm tra xem firstName có nội dung thực sự hoặc không
        return firstName != null && !firstName.trim().isEmpty();
    }

    public boolean isLname() {
        // Kiểm tra xem lastName có nội dung thực sự hoặc không
        return lastName != null && !lastName.trim().isEmpty();
    }

    // Helper method for email format validation using a simple regular expression
    private boolean isValidEmailFormat(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    public Users() {
    }

    public Users(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getTotalInvite() {
        return totalInvite;
    }

    public void setTotalInvite(Integer totalInvite) {
        this.totalInvite = totalInvite;
    }

    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

//    public List<Tour> getTourList() {
//        return tourList;
//    }
//
//    public void setTourList(List<Tour> tourList) {
//        this.tourList = tourList;
//    }
//
//    public List<News> getNewsList() {
//        return newsList;
//    }
//
//    public void setNewsList(List<News> newsList) {
//        this.newsList = newsList;
//    }
//
//    public List<Booking> getBookingList() {
//        return bookingList;
//    }
//
//    public void setBookingList(List<Booking> bookingList) {
//        this.bookingList = bookingList;
//    }
//
//    public List<Coupon> getCouponList() {
//        return couponList;
//    }
//
//    public void setCouponList(List<Coupon> couponList) {
//        this.couponList = couponList;
//    }
//
//    public List<Tour> getTourList1() {
//        return tourList1;
//    }
//
//    public void setTourList1(List<Tour> tourList1) {
//        this.tourList1 = tourList1;
//    }
    public Company getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

    public Roles getRoleId() {
        return roleId;
    }

    public void setRoleId(Roles roleId) {
        this.roleId = roleId;
    }

//    public List<Post> getPostList() {
//        return postList;
//    }
//
//    public void setPostList(List<Post> postList) {
//        this.postList = postList;
//    }
//
//    public List<Review> getReviewList() {
//        return reviewList;
//    }
//
//    public void setReviewList(List<Review> reviewList) {
//        this.reviewList = reviewList;
//    }
//
//    public List<Usergroup> getUsergroupList() {
//        return usergroupList;
//    }
//
//    public void setUsergroupList(List<Usergroup> usergroupList) {
//        this.usergroupList = usergroupList;
//    }
//
//    public List<Payment> getPaymentList() {
//        return paymentList;
//    }
//
//    public void setPaymentList(List<Payment> paymentList) {
//        this.paymentList = paymentList;
//    }
//
//    public List<Invite> getInviteList() {
//        return inviteList;
//    }
//
//    public void setInviteList(List<Invite> inviteList) {
//        this.inviteList = inviteList;
//    }
//
//    public List<Invite> getInviteList1() {
//        return inviteList1;
//    }
//
//    public void setInviteList1(List<Invite> inviteList1) {
//        this.inviteList1 = inviteList1;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Users)) {
//            return false;
//        }
//        Users other = (Users) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
}
