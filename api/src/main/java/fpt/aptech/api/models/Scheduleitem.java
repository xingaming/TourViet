package fpt.aptech.api.models;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "scheduleitem")
@NamedQueries({
    @NamedQuery(name = "Scheduleitem.findAll", query = "SELECT s FROM Scheduleitem s"),
    @NamedQuery(name = "Scheduleitem.findById", query = "SELECT s FROM Scheduleitem s WHERE s.id = :id"),
    @NamedQuery(name = "Scheduleitem.findByNameDay", query = "SELECT s FROM Scheduleitem s WHERE s.nameDay = :nameDay"),
    @NamedQuery(name = "Scheduleitem.findByTitle", query = "SELECT s FROM Scheduleitem s WHERE s.title = :title"),
    @NamedQuery(name = "Scheduleitem.findByDescription", query = "SELECT s FROM Scheduleitem s WHERE s.description = :description")})
public class Scheduleitem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name_day")
    private String nameDay;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    @ManyToOne
    private Schedule scheduleId;
    @OneToMany(mappedBy = "scheduleItemId")
    private List<Scheduleimage> scheduleimageList;

    public Scheduleitem() {
    }

    public Scheduleitem(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameDay() {
        return nameDay;
    }

    public void setNameDay(String nameDay) {
        this.nameDay = nameDay;
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

//    public List<Scheduleimage> getScheduleimageList() {
//        return scheduleimageList;
//    }
//
//    public void setScheduleimageList(List<Scheduleimage> scheduleimageList) {
//        this.scheduleimageList = scheduleimageList;
//    }
//
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
//        if (!(object instanceof Scheduleitem)) {
//            return false;
//        }
//        Scheduleitem other = (Scheduleitem) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Scheduleitem[ id=" + id + " ]";
    }
    
}
