package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "schedule")
@NamedQueries({
    @NamedQuery(name = "Schedule.findAll", query = "SELECT s FROM Schedule s"),
    @NamedQuery(name = "Schedule.findById", query = "SELECT s FROM Schedule s WHERE s.id = :id"),
    @NamedQuery(name = "Schedule.findByStartDate", query = "SELECT s FROM Schedule s WHERE s.startDate = :startDate"),
    @NamedQuery(name = "Schedule.findByEndDate", query = "SELECT s FROM Schedule s WHERE s.endDate = :endDate")})
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name = "quantity_passenger")
    private Integer quantityPassenger;
    @Column(name = "quantity_min")
    private Integer quantityMin;
    @Column(name = "quantity_max")
    private Integer quantityMax;
    @Column(name = "price" , precision = 8, scale = 2)
    private BigDecimal  price;

    @JoinColumn(name = "tour_id", referencedColumnName = "id")
    @ManyToOne
    private Tour tourId;
    

    public Schedule() {
    }

    public Schedule(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Tour getTourId() {
        return tourId;
    }

    public void setTourId(Tour tourId) {
        this.tourId = tourId;
    }
    
    public Integer getQuantityPassenger() {
        return quantityPassenger;
    }

    public void setQuantityPassenger(Integer quantityPassenger) {
        this.quantityPassenger = quantityPassenger;
    }

    public Integer getQuantityMin() {
        return quantityMin;
    }

    public void setQuantityMin(Integer quantityMin) {
        this.quantityMin = quantityMin;
    }

    public Integer getQuantityMax() {
        return quantityMax;
    }

    public void setQuantityMax(Integer quantityMax) {
        this.quantityMax = quantityMax;
    }
    
    
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal  price) {
        this.price = price;
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
//        if (!(object instanceof Schedule)) {
//            return false;
//        }
//        Schedule other = (Schedule) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Schedule[ id=" + id + " ]";
    }
    
}
