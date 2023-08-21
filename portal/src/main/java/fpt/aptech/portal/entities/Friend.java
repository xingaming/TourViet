package fpt.aptech.portal.entities;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "friend")
@NamedQueries({
    @NamedQuery(name = "Friend.findAll", query = "SELECT f FROM Friend f"),
    @NamedQuery(name = "Friend.findById", query = "SELECT f FROM Friend f WHERE f.id = :id"),
    @NamedQuery(name = "Friend.findByStatus", query = "SELECT f FROM Friend f WHERE f.status = :status")})
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "status")
    private Boolean status;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    @ManyToOne
    private Users friendId;

    public Friend() {
    }

    public Friend(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Users getFriendId() {
        return friendId;
    }

    public void setFriendId(Users friendId) {
        this.friendId = friendId;
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
//        if (!(object instanceof Friend)) {
//            return false;
//        }
//        Friend other = (Friend) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Friend[ id=" + id + " ]";
    }
    
}
