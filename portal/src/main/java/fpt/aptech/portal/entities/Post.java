package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;


@Entity
@Table(name = "post")
@NamedQueries({
    @NamedQuery(name = "Post.findAll", query = "SELECT p FROM Post p"),
    @NamedQuery(name = "Post.findById", query = "SELECT p FROM Post p WHERE p.id = :id"),
    @NamedQuery(name = "Post.findByImage", query = "SELECT p FROM Post p WHERE p.image = :image"),
    @NamedQuery(name = "Post.findByStatus", query = "SELECT p FROM Post p WHERE p.status = :status"),
    @NamedQuery(name = "Post.findByPostDate", query = "SELECT p FROM Post p WHERE p.postDate = :postDate"),
    @NamedQuery(name = "Post.findByHashtag", query = "SELECT p FROM Post p WHERE p.hashtag = :hashtag")})
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "image")
    private String image;
    @Column(name = "status")
    private String status;
    @Column(name = "post_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postDate;
    @Column(name = "hashtag")
    private String hashtag;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;

    public Post() {
    }

    public Post(Integer id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
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
//        if (!(object instanceof Post)) {
//            return false;
//        }
//        Post other = (Post) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Post[ id=" + id + " ]";
    }
    
}
