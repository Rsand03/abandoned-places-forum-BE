package ee.taltech.iti0302project.app.entity.feed;

import ee.taltech.iti0302project.app.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "posts")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @Column(name = "location_id")
    private UUID locationId;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UpvoteEntity> upvotes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;
}
