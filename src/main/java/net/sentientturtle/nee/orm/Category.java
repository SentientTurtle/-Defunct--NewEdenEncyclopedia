package net.sentientturtle.nee.orm;

import org.jetbrains.annotations.Nullable;

/**
 * Data object representing EVE Online Type Categories
 */
public class Category {
    public final int categoryID;
    public final String name;
    /**
     * May be left null to indicate this Category has no icon.
     */
    @Nullable
    public final Integer iconID;
    public final boolean published;

    public Category(int categoryID, String name, @Nullable Integer iconID, boolean published) {
        this.categoryID = categoryID;
        this.name = name;
        this.iconID = iconID;
        this.published = published;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return categoryID == category.categoryID;
    }

    @Override
    public int hashCode() {
        return categoryID;
    }
}
