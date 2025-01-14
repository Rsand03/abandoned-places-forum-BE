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
    private String additionalInformation;
    private boolean isPublic;
    private boolean isPendingPublicationApproval;
    private int minRequiredPointsToView;
    private UUID createdBy;

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToMany(mappedBy = "location")
    private Set<LocationBookmarkEntity> bookmarks;
}
