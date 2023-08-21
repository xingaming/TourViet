package fpt.aptech.api.models;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "groupchat")
@NamedQueries({
    @NamedQuery(name = "Groupchat.findAll", query = "SELECT g FROM Groupchat g"),
    @NamedQuery(name = "Groupchat.findById", query = "SELECT g FROM Groupchat g WHERE g.id = :id"),
    @NamedQuery(name = "Groupchat.findByName", query = "SELECT g FROM Groupchat g WHERE g.name = :name"),
    @NamedQuery(name = "Groupchat.findByMemberCount", query = "SELECT g FROM Groupchat g WHERE g.memberCount = :memberCount")})
public class Groupchat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "member_count")
    private Integer memberCount;
    @OneToMany(mappedBy = "groupchatId")
    private List<Usergroup> usergroupList;

    public Groupchat() {
    }

    public Groupchat(Integer id) {
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

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

//    public List<Usergroup> getUsergroupList() {
//        return usergroupList;
//    }
//
//    public void setUsergroupList(List<Usergroup> usergroupList) {
//        this.usergroupList = usergroupList;
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
//        if (!(object instanceof Groupchat)) {
//            return false;
//        }
//        Groupchat other = (Groupchat) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Groupchat[ id=" + id + " ]";
    }
    
}
