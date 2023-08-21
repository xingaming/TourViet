/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;

/**
 *
 * @author NGOC THAI
 */
@Entity
@Table(name = "tourcreate")
@NamedQueries({
    @NamedQuery(name = "TourCreate.findAll", query = "SELECT t FROM TourCreate t"),
    @NamedQuery(name = "TourCreate.findById", query = "SELECT t FROM TourCreate t WHERE t.id = :id"),
    @NamedQuery(name = "TourCreate.findByRegion", query = "SELECT t FROM TourCreate t WHERE t.region = :region"),
    @NamedQuery(name = "TourCreate.findByAccommodation", query = "SELECT t FROM TourCreate t WHERE t.accommodation = :accommodation"),
    @NamedQuery(name = "TourCreate.findByTransport", query = "SELECT t FROM TourCreate t WHERE t.transport = :transport"),
    @NamedQuery(name = "TourCreate.findByDestination", query = "SELECT t FROM TourCreate t WHERE t.destination = :destination"),
    @NamedQuery(name = "TourCreate.findByDescription", query = "SELECT t FROM TourCreate t WHERE t.description = :description"),
    @NamedQuery(name = "TourCreate.findByAdult", query = "SELECT t FROM TourCreate t WHERE t.adult = :adult"),
    @NamedQuery(name = "TourCreate.findByChildren", query = "SELECT t FROM TourCreate t WHERE t.children = :children"),
    @NamedQuery(name = "TourCreate.findByBaby", query = "SELECT t FROM TourCreate t WHERE t.baby = :baby"),
    @NamedQuery(name = "TourCreate.findByStartdate", query = "SELECT t FROM TourCreate t WHERE t.startdate = :startdate"),
    @NamedQuery(name = "TourCreate.findByEnddate", query = "SELECT t FROM TourCreate t WHERE t.enddate = :enddate"),
    @NamedQuery(name = "TourCreate.findByFullname", query = "SELECT t FROM TourCreate t WHERE t.fullname = :fullname"),
    @NamedQuery(name = "TourCreate.findByEmail", query = "SELECT t FROM TourCreate t WHERE t.email = :email"),
    @NamedQuery(name = "TourCreate.findByPhone", query = "SELECT t FROM TourCreate t WHERE t.phone = :phone"),
    @NamedQuery(name = "TourCreate.findByAddress", query = "SELECT t FROM TourCreate t WHERE t.address = :address"),
    @NamedQuery(name = "TourCreate.findByCompanyId", query = "SELECT t FROM TourCreate t WHERE t.companyId = :companyId"),
    @NamedQuery(name = "TourCreate.findByUserId", query = "SELECT t FROM TourCreate t WHERE t.userId = :userId"),
    @NamedQuery(name = "TourCreate.findByPrice", query = "SELECT t FROM TourCreate t WHERE t.price = :price"),
    @NamedQuery(name = "TourCreate.findByTourguide", query = "SELECT t FROM TourCreate t WHERE t.tourguide = :tourguide"),
    @NamedQuery(name = "TourCreate.findByNote", query = "SELECT t FROM TourCreate t WHERE t.note = :note")})
public class TourCreate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "region")
    private String region;
    @Column(name = "accommodation")
    private String accommodation;
    @Column(name = "transport")
    private String transport;
    @Column(name = "destination")
    private String destination;
    @Column(name = "description")
    private String description;
    @Column(name = "adult")
    private Integer adult;
    @Column(name = "children")
    private Integer children;
    @Column(name = "baby")
    private Integer baby;
    @Column(name = "startdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startdate;
    @Column(name = "enddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enddate;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Column(name = "address")
    private String address;
    @Column(name = "company_id")
    private Integer companyId;
    @Column(name = "user_id")
    private Integer userId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "tourguide")
    private Boolean tourguide;
    @Column(name = "note")
    private String note;
    @Column(name = "type")
    private String type;

    public TourCreate() {
    }

    public TourCreate(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAdult() {
        return adult;
    }

    public void setAdult(Integer adult) {
        this.adult = adult;
    }

    public Integer getChildren() {
        return children;
    }

    public void setChildren(Integer children) {
        this.children = children;
    }

    public Integer getBaby() {
        return baby;
    }

    public void setBaby(Integer baby) {
        this.baby = baby;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getTourguide() {
        return tourguide;
    }

    public void setTourguide(Boolean tourguide) {
        this.tourguide = tourguide;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

   

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TourCreate)) {
            return false;
        }
        TourCreate other = (TourCreate) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fpt.aptech.portal.entities.TourCreate[ id=" + id + " ]";
    }

}
