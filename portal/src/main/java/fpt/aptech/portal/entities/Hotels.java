package fpt.aptech.portal.entities;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "hotels")
@NamedQueries({
    @NamedQuery(name = "Hotels.findAll", query = "SELECT h FROM Hotels h"),
    @NamedQuery(name = "Hotels.findById", query = "SELECT h FROM Hotels h WHERE h.id = :id"),
    @NamedQuery(name = "Hotels.findByHotelName", query = "SELECT h FROM Hotels h WHERE h.hotelName = :hotelName")})
public class Hotels implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "hotel_name")
    private String hotelName;
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    @ManyToOne
    private Region regionId;

    public Hotels() {
    }

    public Hotels(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public Region getRegionId() {
        return regionId;
    }

    public void setRegionId(Region regionId) {
        this.regionId = regionId;
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
//        if (!(object instanceof Hotels)) {
//            return false;
//        }
//        Hotels other = (Hotels) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Hotels[ id=" + id + " ]";
    }
    
}
