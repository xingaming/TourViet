package fpt.aptech.portal.entities;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "favoritepost")
@NamedQueries({
    @NamedQuery(name = "Favoritepost.findAll", query = "SELECT f FROM Favoritepost f"),
    @NamedQuery(name = "Favoritepost.findById", query = "SELECT f FROM Favoritepost f WHERE f.id = :id"),
    @NamedQuery(name = "Favoritepost.findByUserId", query = "SELECT f FROM Favoritepost f WHERE f.userId = :userId")})
public class Favoritepost implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @ManyToOne
    private Post postId;

    public Favoritepost() {
    }

    public Favoritepost(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
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
//        if (!(object instanceof Favoritepost)) {
//            return false;
//        }
//        Favoritepost other = (Favoritepost) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Favoritepost[ id=" + id + " ]";
    }
    
}
