package net.sentientturtle.nee.orm;

/**
 * Data object representing EVE Online item Types
 */
public class Type {
    public final int typeID;
    public final int groupID;
    public final String name;
    public final String description;
    public final double mass;
    public final double volume;
    public final double capacity;
    public final boolean published;

    public Type(int typeID, int groupID, String name, String description, double mass, double volume, double capacity, boolean published) {
        this.typeID = typeID;
        this.groupID = groupID;
        this.name = name;
        this.description = description;
        this.mass = mass;
        this.volume = volume;
        this.capacity = capacity;
        this.published = published;
    }

    @Override
    public String toString() {
        return "Type{" +
                "typeID=" + typeID +
                ", groupID=" + groupID +
                ", name='" + name + '\'' +
                ", published=" + published +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return typeID == type.typeID;
    }

    @Override
    public int hashCode() {
        return typeID;
    }
}
