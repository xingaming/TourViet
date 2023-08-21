package fpt.aptech.api.models;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "informationbooking")
@NamedQueries({
    @NamedQuery(name = "Informationbooking.findAll", query = "SELECT i FROM Informationbooking i"),
    @NamedQuery(name = "Informationbooking.findById", query = "SELECT i FROM Informationbooking i WHERE i.id = :id"),
    @NamedQuery(name = "Informationbooking.findByName", query = "SELECT i FROM Informationbooking i WHERE i.name = :name"),
    @NamedQuery(name = "Informationbooking.findByPhone", query = "SELECT i FROM Informationbooking i WHERE i.phone = :phone"),
    @NamedQuery(name = "Informationbooking.findByEmail", query = "SELECT i FROM Informationbooking i WHERE i.email = :email"),
    @NamedQuery(name = "Informationbooking.findByAddress", query = "SELECT i FROM Informationbooking i WHERE i.address = :address"),
    @NamedQuery(name = "Informationbooking.findByGender", query = "SELECT i FROM Informationbooking i WHERE i.gender = :gender"),
    @NamedQuery(name = "Informationbooking.findByCccd", query = "SELECT i FROM Informationbooking i WHERE i.cccd = :cccd")})
public class Informationbooking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "address")
    private String address;
    @Column(name = "gender")
    private Boolean gender;
    @Column(name = "cccd")
    private String cccd;
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    @ManyToOne
    private Booking bookingId;

    public Informationbooking() {
    }

    public Informationbooking(Integer id) {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public Booking getBookingId() {
        return bookingId;
    }

    public void setBookingId(Booking bookingId) {
        this.bookingId = bookingId;
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
//        if (!(object instanceof Informationbooking)) {
//            return false;
//        }
//        Informationbooking other = (Informationbooking) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Informationbooking[ id=" + id + " ]";
    }
    
}
