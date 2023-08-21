package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "company")
@NamedQueries({
    @NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c"),
    @NamedQuery(name = "Company.findById", query = "SELECT c FROM Company c WHERE c.id = :id"),
    @NamedQuery(name = "Company.findByName", query = "SELECT c FROM Company c WHERE c.name = :name"),
    @NamedQuery(name = "Company.findByLogo", query = "SELECT c FROM Company c WHERE c.logo = :logo"),
    @NamedQuery(name = "Company.findByStatus", query = "SELECT c FROM Company c WHERE c.status = :status"),
    @NamedQuery(name = "Company.findByAddress", query = "SELECT c FROM Company c WHERE c.address = :address"),
    @NamedQuery(name = "Company.findByTotalRevenue", query = "SELECT c FROM Company c WHERE c.totalRevenue = :totalRevenue")})
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "logo")
    private String logo;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "address")
    private String address;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;
    @OneToMany(mappedBy = "companyId")
    private List<Users> usersList;
    
    @Column(name = "hotline")
    private String hotline;
    @Column(name = "email")
    private String email;

    public Company() {
    }

    public Company(Integer id) {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public List<Users> getUsersList() {
//        return usersList;
//    }
//
//    public void setUsersList(List<Users> usersList) {
//        this.usersList = usersList;
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
//        if (!(object instanceof Company)) {
//            return false;
//        }
//        Company other = (Company) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Company[ id=" + id + " ]";
    }
    
}
