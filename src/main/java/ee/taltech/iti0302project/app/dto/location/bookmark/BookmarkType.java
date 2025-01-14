package ee.taltech.iti0302project.app.dto.location.bookmark;

public enum BookmarkType {
    JAA_MEELDE("Jäta meelde"),
    JUBA_KULASTATUD("Juba külastatud"),
    SUUR_RISK("Suur risk"),
    OSALISELT_AVASTATUD ("Osaliselt avastatud");

    private final String label;

    BookmarkType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BookmarkType fromString(String label) {
        for (BookmarkType type : BookmarkType.values()) {
            if (type.label.equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown bookmark type: " + label);
    }
}
