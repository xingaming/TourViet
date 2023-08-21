package fpt.aptech.api.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;


@Entity
@Table(name = "coupon")
@NamedQueries({
    @NamedQuery(name = "Coupon.findAll", query = "SELECT c FROM Coupon c"),
    @NamedQuery(name = "Coupon.findById", query = "SELECT c FROM Coupon c WHERE c.id = :id"),
    @NamedQuery(name = "Coupon.findByCode", query = "SELECT c FROM Coupon c WHERE c.code = :code"),
    @NamedQuery(name = "Coupon.findByDiscount", query = "SELECT c FROM Coupon c WHERE c.discount = :discount"),
    @NamedQuery(name = "Coupon.findByExpirationDate", query = "SELECT c FROM Coupon c WHERE c.expirationDate = :expirationDate")})
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "code")
    private String code;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "discount")
    private BigDecimal discount;
    @Column(name = "expiration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;

    public Coupon() {
    }

    public Coupon(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
//        if (!(object instanceof Coupon)) {
//            return false;
//        }
//        Coupon other = (Coupon) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Coupon[ id=" + id + " ]";
    }
    
}
