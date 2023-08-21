package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "usergroup")
@NamedQueries({
    @NamedQuery(name = "Usergroup.findAll", query = "SELECT u FROM Usergroup u"),
    @NamedQuery(name = "Usergroup.findById", query = "SELECT u FROM Usergroup u WHERE u.id = :id")})
public class Usergroup implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @OneToMany(mappedBy = "usergroupId")
    private List<Chatmessage> chatmessageList;
    @JoinColumn(name = "groupchat_id", referencedColumnName = "id")
    @ManyToOne
    private Groupchat groupchatId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;

    public Usergroup() {
    }

    public Usergroup(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//    public List<Chatmessage> getChatmessageList() {
//        return chatmessageList;
//    }
//
//    public void setChatmessageList(List<Chatmessage> chatmessageList) {
//        this.chatmessageList = chatmessageList;
//    }

    public Groupchat getGroupchatId() {
        return groupchatId;
    }

    public void setGroupchatId(Groupchat groupchatId) {
        this.groupchatId = groupchatId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
//        if (!(object instanceof Usergroup)) {
//            return false;
//        }
//        Usergroup other = (Usergroup) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Usergroup[ id=" + id + " ]";
    }
    
}
