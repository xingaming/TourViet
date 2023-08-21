package fpt.aptech.api.models;

import java.io.Serializable;
import jakarta.persistence.*;


@Entity
@Table(name = "invite")
@NamedQueries({
    @NamedQuery(name = "Invite.findAll", query = "SELECT i FROM Invite i"),
    @NamedQuery(name = "Invite.findById", query = "SELECT i FROM Invite i WHERE i.id = :id"),
    @NamedQuery(name = "Invite.findByStatus", query = "SELECT i FROM Invite i WHERE i.status = :status")})
public class Invite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "status")
    private Boolean status;
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @ManyToOne
    private Users senderId;
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    @ManyToOne
    private Users recipientId;

    public Invite() {
    }

    public Invite(Integer id) {
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

    public Users getSenderId() {
        return senderId;
    }

    public void setSenderId(Users senderId) {
        this.senderId = senderId;
    }

    public Users getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Users recipientId) {
        this.recipientId = recipientId;
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
//        if (!(object instanceof Invite)) {
//            return false;
//        }
//        Invite other = (Invite) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Invite[ id=" + id + " ]";
    }
    
}
