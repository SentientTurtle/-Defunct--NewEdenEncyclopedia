package net.sentientturtle.nee.orm;

/**
 * Data object representing EVE Online MetaGroups, also known as item tiers.
 */
public class MetaGroup {
    public final int metaGroupID;
    public final String metaGroupName;

    public MetaGroup(int metaGroupID, String metaGroupName) {
        this.metaGroupID = metaGroupID;
        this.metaGroupName = metaGroupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaGroup metaGroup = (MetaGroup) o;
        return metaGroupID == metaGroup.metaGroupID;
    }

    @Override
    public int hashCode() {
        return metaGroupID;
    }

    @Override
    public String toString() {
        return "MetaGroup{" +
                "metaGroupID=" + metaGroupID +
                ", metaGroupName='" + metaGroupName + '\'' +
                '}';
    }
}
