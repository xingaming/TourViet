package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "commentpost")
@NamedQueries({
    @NamedQuery(name = "Commentpost.findAll", query = "SELECT c FROM Commentpost c"),
    @NamedQuery(name = "Commentpost.findById", query = "SELECT c FROM Commentpost c WHERE c.id = :id"),
    @NamedQuery(name = "Commentpost.findByCommentDate", query = "SELECT c FROM Commentpost c WHERE c.commentDate = :commentDate"),
    @NamedQuery(name = "Commentpost.findByContent", query = "SELECT c FROM Commentpost c WHERE c.content = :content")})
public class Commentpost implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "comment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date commentDate;
    @Column(name = "content")
    private String content;
    @OneToMany(mappedBy = "parentId")
    private List<Commentpost> commentpostList;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private Commentpost parentId;
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @ManyToOne
    private Post postId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;

    public Commentpost() {
    }

    public Commentpost(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public List<Commentpost> getCommentpostList() {
//        return commentpostList;
//    }
//
//    public void setCommentpostList(List<Commentpost> commentpostList) {
//        this.commentpostList = commentpostList;
//    }

    public Commentpost getParentId() {
        return parentId;
    }

    public void setParentId(Commentpost parentId) {
        this.parentId = parentId;
    }

    public Post getPostId() {
        return postId;
    }

    public void setPostId(Post postId) {
        this.postId = postId;
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
//        if (!(object instanceof Commentpost)) {
//            return false;
//        }
//        Commentpost other = (Commentpost) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Commentpost[ id=" + id + " ]";
    }
    
}
