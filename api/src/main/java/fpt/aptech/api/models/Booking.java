package fpt.aptech.api.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "booking")
@NamedQueries({
    @NamedQuery(name = "Booking.findAll", query = "SELECT b FROM Booking b"),
    @NamedQuery(name = "Booking.findById", query = "SELECT b FROM Booking b WHERE b.id = :id"),
    @NamedQuery(name = "Booking.findByAdult", query = "SELECT b FROM Booking b WHERE b.adult = :adult"),
    @NamedQuery(name = "Booking.findByYoung", query = "SELECT b FROM Booking b WHERE b.baby = :baby"),
    @NamedQuery(name = "Booking.findBySenior", query = "SELECT b FROM Booking b WHERE b.senior = :senior"),
    @NamedQuery(name = "Booking.findByChildren", query = "SELECT b FROM Booking b WHERE b.children = :children"),
    @NamedQuery(name = "Booking.findByBookingDate", query = "SELECT b FROM Booking b WHERE b.bookingDate = :bookingDate")})
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "adult")
    private Integer adult;
    @Column(name = "baby")
    private Integer baby;
    @Column(name = "senior")
    private Integer senior;
    @Column(name = "children")
    private Integer children;
    @Column(name = "booking_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookingDate;
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    @ManyToOne
    private Region regionId;
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    @ManyToOne
    private Schedule scheduleId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;
    @OneToMany(mappedBy = "bookingId")
    private List<Payment> paymentList;


    public Booking() {
    }

    public Booking(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAdult() {
        return adult;
    }

    public void setAdult(Integer adult) {
        this.adult = adult;
    }

    public Integer getBaby() {
        return baby;
    }

    public void setBaby(Integer baby) {
        this.baby = baby;
    }

    public Integer getSenior() {
        return senior;
    }

    public void setSenior(Integer senior) {
        this.senior = senior;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Region getRegionId() {
        return regionId;
    }

    public void setRegionId(Region regionId) {
        this.regionId = regionId;
    }

    public Schedule getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Schedule scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

//    public List<Payment> getPaymentList() {
//        return paymentList;
//    }
//
//    public void setPaymentList(List<Payment> paymentList) {
//        this.paymentList = paymentList;
//    }

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
//        if (!(object instanceof Booking)) {
//            return false;
//        }
//        Booking other = (Booking) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Booking[ id=" + id + " ]";
    }
    
}
