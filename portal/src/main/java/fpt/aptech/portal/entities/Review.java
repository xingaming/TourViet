package fpt.aptech.portal.entities;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;


@Entity
@Table(name = "review")
@NamedQueries({
    @NamedQuery(name = "Review.findAll", query = "SELECT r FROM Review r"),
    @NamedQuery(name = "Review.findById", query = "SELECT r FROM Review r WHERE r.id = :id"),
    @NamedQuery(name = "Review.findByReviewDate", query = "SELECT r FROM Review r WHERE r.reviewDate = :reviewDate"),
    @NamedQuery(name = "Review.findByContent", query = "SELECT r FROM Review r WHERE r.content = :content"),
    @NamedQuery(name = "Review.findByReply", query = "SELECT r FROM Review r WHERE r.reply = :reply"),
    @NamedQuery(name = "Review.findByReplyDate", query = "SELECT r FROM Review r WHERE r.replyDate = :replyDate")})
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "review_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewDate;
    @Column(name = "content")
    private String content;
    @Column(name = "reply")
    private String reply;
    @Column(name = "reply_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date replyDate;
    @JoinColumn(name = "tour_id", referencedColumnName = "id")
    @ManyToOne
    private Tour tourId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private Users userId;
    
    @Column(name = "rate")
    private Float rate;

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public Review() {
    }

    public Review(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public Tour getTourId() {
        return tourId;
    }

    public void setTourId(Tour tourId) {
        this.tourId = tourId;
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
//        if (!(object instanceof Review)) {
//            return false;
//        }
//        Review other = (Review) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }

    @Override
    public String toString() {
        return "fpt.aptech.api.models.Review[ id=" + id + " ]";
    }
    
}
