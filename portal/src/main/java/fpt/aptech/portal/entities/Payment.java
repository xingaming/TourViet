package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;


@Entity
@Table(name = "payment")
@NamedQueries({
    @NamedQuery(name = "Payment.findAll", query = "SELECT p FROM Payment p"),
    @NamedQuery(name = "Payment.findById", query = "SELECT p FROM Payment p WHERE p.id = :id"),
    @NamedQuery(name = "Payment.findByPrice", query = "SELECT p FROM Payment p WHERE p.price = :price"),
    @NamedQuery(name = "Payment.findByDate", query = "SELECT p FROM Payment p WHERE p.date = :date"),
    @NamedQuery(name = "Payment.findByStatus", query = "SELECT p FROM Payment p WHERE p.status = :status"),
    @NamedQuery(name = "Payment.findByDescription", query = "SELECT p FROM Payment p WHERE p.description = :description")})
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(name = "status")
    private Short status;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    @ManyToOne
    private Booking bookingId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;
    @Column(name = "payment_method")
    private Short paymentMethod;

    public Payment() {
    }

    public Payment(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Booking getBookingId() {
        return bookingId;
    }

    public void setBookingId(Booking bookingId) {
        this.bookingId = bookingId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }
    
    public Short getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Short paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

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
//        if (!(object instanceof Payment)) {
//            return false;
//        }
//        Payment other = (Payment) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Payment[ id=" + id + " ]";
    }
    
}
