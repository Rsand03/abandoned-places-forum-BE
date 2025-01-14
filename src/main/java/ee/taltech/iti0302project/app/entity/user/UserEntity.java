package ee.taltech.iti0302project.app.entity.user;

import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String password;
    private String email;
    private Integer points;
    private String role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "createdBy")
    private List<PostEntity> posts;
}
