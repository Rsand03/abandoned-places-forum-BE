package ee.taltech.iti0302project.app.entity.location;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private double lon;
    private double lat;

    @ManyToOne
    @JoinColumn(name = "main_category_id", referencedColumnName = "id")
    private LocationCategoryEntity mainCategory;

    @ManyToMany
    @JoinTable(
            name = "location_category_assignments",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "location_category_id")
    )
    private List<LocationCategoryEntity> subCategories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_condition_id", referencedColumnName = "id")
    private LocationConditionEntity condition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_status_id", referencedColumnName = "id")
    private LocationStatusEntity status;

    @Column(name = "additional_information")
    private String additionalInformation;
    @Column(name = "is_public")
    private boolean isPublic;
    @Column(name = "is_pending_publication_approval")
    private boolean isPendingPublicationApproval;
    @Column(name = "min_required_points_to_view")
    private int minRequiredPointsToView;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "location")
    private Set<LocationBookmarkEntity> bookmarks;
}
