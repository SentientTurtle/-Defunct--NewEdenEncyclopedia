package net.sentientturtle.nee.orm;

/**
 * Data object representing EVE Online attributes
 */
public class Attribute {
    public final int attributeID;
    public final int categoryID;
    public final String attributeName;
    public final String displayName;
    public final int unitID;
    public final int iconID;
    public final boolean published;

    public Attribute(int attributeID, int categoryID, String attributeName, String displayName, int unitID, int iconID, boolean published) {
        this.attributeID = attributeID;
        this.categoryID = categoryID;
        this.attributeName = attributeName;
        this.displayName = displayName;
        this.unitID = unitID;
        this.iconID = iconID;
        this.published = published;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "attributeID=" + attributeID +
                ", categoryID=" + categoryID +
                ", attributeName='" + attributeName + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute attribute = (Attribute) o;
        return attributeID == attribute.attributeID;
    }

    @Override
    public int hashCode() {
        return attributeID;
    }
}
