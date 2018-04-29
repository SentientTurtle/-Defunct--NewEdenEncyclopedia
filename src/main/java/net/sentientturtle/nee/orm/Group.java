package net.sentientturtle.nee.orm;

import org.jetbrains.annotations.Nullable;

/**
 * Data object to represent EVE Online Type Groups
 */
public class Group {
    public final int groupID;
    public final int categoryID;
    public final String name;
    /**
     * May be left null to indicate this group has no icon
     */
    @Nullable
    public final Integer iconID;
    public final boolean published;

    public Group(int groupID, int categoryID, String name, @Nullable Integer iconID, boolean published) {
        this.groupID = groupID;
        this.categoryID = categoryID;
        this.name = name;
        this.iconID = iconID;
        this.published = published;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupID=" + groupID +
                ", categoryID=" + categoryID +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return groupID == group.groupID;
    }

    @Override
    public int hashCode() {
        return groupID;
    }
}
