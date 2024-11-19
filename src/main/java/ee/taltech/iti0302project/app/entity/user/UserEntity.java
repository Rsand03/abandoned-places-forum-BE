package ee.taltech.iti0302project.app.entity.user;

import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "createdBy")
    private List<PostEntity> posts;
}
