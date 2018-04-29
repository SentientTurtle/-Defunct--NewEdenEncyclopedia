package net.sentientturtle.nee.orm;

import java.util.Objects;

/**
 * Data object to represent EVE Online Factions
 */
public class Faction {
    public final int factionID;
    public final String factionName;
    public final int corporationID;

    public Faction(int factionID, String factionName, int corporationID) {
        this.factionID = factionID;
        this.factionName = factionName;
        this.corporationID = corporationID;
    }

    @Override
    public String toString() {
        return "Faction{" +
                "factionID=" + factionID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        return factionID == faction.factionID;
    }

    @Override
    public int hashCode() {
        return factionID;
    }
}
