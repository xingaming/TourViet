package fpt.aptech.portal.entities;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "serviceitem")
@NamedQueries({
    @NamedQuery(name = "Serviceitem.findAll", query = "SELECT s FROM Serviceitem s"),
    @NamedQuery(name = "Serviceitem.findById", query = "SELECT s FROM Serviceitem s WHERE s.id = :id"),
    @NamedQuery(name = "Serviceitem.findByTitle", query = "SELECT s FROM Serviceitem s WHERE s.title = :title"),
    @NamedQuery(name = "Serviceitem.findByDescription", query = "SELECT s FROM Serviceitem s WHERE s.description = :description")})
public class Serviceitem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    @ManyToOne
    private Schedule scheduleId;

    public Serviceitem() {
    }

    public Serviceitem(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Schedule getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Schedule scheduleId) {
        this.scheduleId = scheduleId;
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
//        if (!(object instanceof Serviceitem)) {
//            return false;
//        }
//        Serviceitem other = (Serviceitem) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Serviceitem[ id=" + id + " ]";
    }
    
}
