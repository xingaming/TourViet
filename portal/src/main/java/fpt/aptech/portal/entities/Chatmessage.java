package fpt.aptech.portal.entities;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "chatmessage")
@NamedQueries({
    @NamedQuery(name = "Chatmessage.findAll", query = "SELECT c FROM Chatmessage c"),
    @NamedQuery(name = "Chatmessage.findById", query = "SELECT c FROM Chatmessage c WHERE c.id = :id"),
    @NamedQuery(name = "Chatmessage.findByContent", query = "SELECT c FROM Chatmessage c WHERE c.content = :content")})
public class Chatmessage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "content")
    private String content;
    @JoinColumn(name = "usergroup_id", referencedColumnName = "id")
    @ManyToOne
    private Usergroup usergroupId;

    public Chatmessage() {
    }

    public Chatmessage(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Usergroup getUsergroupId() {
        return usergroupId;
    }

    public void setUsergroupId(Usergroup usergroupId) {
        this.usergroupId = usergroupId;
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
//        if (!(object instanceof Chatmessage)) {
//            return false;
//        }
//        Chatmessage other = (Chatmessage) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Chatmessage[ id=" + id + " ]";
    }
    
}
