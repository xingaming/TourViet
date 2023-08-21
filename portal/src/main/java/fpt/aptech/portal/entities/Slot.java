package fpt.aptech.portal.entities;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "slot")
@NamedQueries({
    @NamedQuery(name = "Slot.findAll", query = "SELECT s FROM Slot s"),
    @NamedQuery(name = "Slot.findById", query = "SELECT s FROM Slot s WHERE s.id = :id"),
    @NamedQuery(name = "Slot.findByQuantity", query = "SELECT s FROM Slot s WHERE s.quantity = :quantity")})
public class Slot implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "quantity")
    private Integer quantity;
    @JoinColumn(name = "tour_id", referencedColumnName = "id")
    @ManyToOne
    private Tour tourId;

    public Slot() {
    }

    public Slot(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Tour getTourId() {
        return tourId;
    }

    public void setTourId(Tour tourId) {
        this.tourId = tourId;
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
//        if (!(object instanceof Slot)) {
//            return false;
//        }
//        Slot other = (Slot) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Slot[ id=" + id + " ]";
    }
    
}
