package fpt.aptech.api.models;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "tour")
@NamedQueries({
    @NamedQuery(name = "Tour.findAll", query = "SELECT t FROM Tour t"),
    @NamedQuery(name = "Tour.findById", query = "SELECT t FROM Tour t WHERE t.id = :id"),
    @NamedQuery(name = "Tour.findByName", query = "SELECT t FROM Tour t WHERE t.name = :name"),
    @NamedQuery(name = "Tour.findByDescription", query = "SELECT t FROM Tour t WHERE t.description = :description"),
    @NamedQuery(name = "Tour.findByTourcode", query = "SELECT t FROM Tour t WHERE t.Tourcode = :Tourcode"),
    @NamedQuery(name = "Tour.findByDiscount", query = "SELECT t FROM Tour t WHERE t.discount = :discount"),
    @NamedQuery(name = "Tour.findByImage", query = "SELECT t FROM Tour t WHERE t.image = :image")})
public class Tour implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
//    @Column(name = "price")
//    private Long price;
    @Column(name = "discount")
    private Integer discount;
    @Column(name = "views")
    private Integer views;
    @Column(name = "is_top_pick")
    private Boolean isTopPick;
    @Column(name = "rate")
    private Float rate;
    @Column(name = "image")
    private String image;
    @JoinTable(name = "favorite", joinColumns = {
        @JoinColumn(name = "tour_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Users> usersList;
    @OneToMany(mappedBy = "tourId")
//    private List<Booking> bookingList;
//    @OneToMany(mappedBy = "tourId")
    private List<Slot> slotList;
    
//    @OneToMany(mappedBy = "scheduleId")
//    private List<Review> reviewList;
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    @ManyToOne
    private Region regionId;
    @JoinColumn(name = "transport_id", referencedColumnName = "id")
    @ManyToOne
    private Transport transportId;
    @JoinColumn(name = "guide_id", referencedColumnName = "id")
    @ManyToOne
    private Users guideId;
    @OneToMany(mappedBy = "tourId")
    private List<Schedule> scheduleList;
//    @OneToMany(mappedBy = "tourId")
//    private List<Review> reviewList;
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    @ManyToOne
    private Company companyId;
    @Column(name = "Tourcode")
    private String Tourcode;

    public Tour() {
    }

    public Tour(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Long getPrice() {
//        return price;
//    }
//
//    public void setPrice(Long price) {
//        this.price = price;
//    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Boolean getIsTopPick() {
        return isTopPick;
    }

    public void setIsTopPick(Boolean isTopPick) {
        this.isTopPick = isTopPick;
    }
    
    public Region getRegionId() {
        return regionId;
    }

    public void setRegionId(Region regionId) {
        this.regionId = regionId;
    }
    
    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }
    
    public String getTourcode() {
        return Tourcode;
    }

    public void setTourcode(String Tourcode) {
        this.Tourcode = Tourcode;
    }

//    public List<Users> getUsersList() {
//        return usersList;
//    }
//
//    public void setUsersList(List<Users> usersList) {
//        this.usersList = usersList;
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
//    public List<Slot> getSlotList() {
//        return slotList;
//    }
//
//    public void setSlotList(List<Slot> slotList) {
//        this.slotList = slotList;
//    }

    public Transport getTransportId() {
        return transportId;
    }

    public void setTransportId(Transport transportId) {
        this.transportId = transportId;
    }

    public Users getGuideId() {
        return guideId;
    }

    public void setGuideId(Users guideId) {
        this.guideId = guideId;
    }
    
    public Company getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

//    public List<Schedule> getScheduleList() {
//        return scheduleList;
//    }
//
//    public void setScheduleList(List<Schedule> scheduleList) {
//        this.scheduleList = scheduleList;
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
//        if (!(object instanceof Tour)) {
//            return false;
//        }
//        Tour other = (Tour) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Tour[ id=" + id + " ]";
    }
    
}
