package fpt.aptech.api.models;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "scheduleimage")
@NamedQueries({
    @NamedQuery(name = "Scheduleimage.findAll", query = "SELECT s FROM Scheduleimage s"),
    @NamedQuery(name = "Scheduleimage.findById", query = "SELECT s FROM Scheduleimage s WHERE s.id = :id"),
    @NamedQuery(name = "Scheduleimage.findByImage", query = "SELECT s FROM Scheduleimage s WHERE s.image = :image"),
    @NamedQuery(name = "Scheduleimage.findByDescription", query = "SELECT s FROM Scheduleimage s WHERE s.description = :description")})
public class Scheduleimage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "image")
    private String image;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "schedule_item_id", referencedColumnName = "id")
    @ManyToOne
    private Scheduleitem scheduleItemId;

    public Scheduleimage() {
    }

    public Scheduleimage(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Scheduleitem getScheduleItemId() {
        return scheduleItemId;
    }

    public void setScheduleItemId(Scheduleitem scheduleItemId) {
        this.scheduleItemId = scheduleItemId;
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
        if (!(object instanceof Scheduleimage)) {
            return false;
        }
        Scheduleimage other = (Scheduleimage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Scheduleimage[ id=" + id + " ]";
    }
    
}
