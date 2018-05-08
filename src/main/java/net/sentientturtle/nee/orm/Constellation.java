package net.sentientturtle.nee.orm;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.util.tuple.Tuple2;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Data object representing EVE Online Constellations
 */
@SuppressWarnings("WeakerAccess")
public class Constellation implements Mappable {
    public final int regionID;
    public final int constellationID;
    public final String constellationName;
    public final double x;
    public final double y;
    public final double z;
    /**
     * May be left null to indicate this constellation does not belong to a faction
     */
    @Nullable
    public final Integer factionID;

    public Constellation(int regionID, int constellationID, String constellationName, double x, double y, double z, @Nullable Integer factionID) {
        this.regionID = regionID;
        this.constellationID = constellationID;
        this.constellationName = constellationName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.factionID = factionID;
    }

    @Override
    public int getID() {
        return constellationID;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public Stream<SolarSystem> getMapPoints(DataSupplier dataSupplier) {
        return dataSupplier.getSolarSystems().stream().filter(solarSystem -> solarSystem.constellationID == constellationID);
    }

    @Override
    public Stream<Tuple2<Integer, Integer>> getMapLines(DataSupplier dataSupplier) {
        Set<Integer> systems = getMapPoints(dataSupplier).map(solarSystem -> solarSystem.solarSystemID).collect(Collectors.toSet());
        return dataSupplier.getJumps().stream().filter(t -> systems.contains(t.v1) && systems.contains(t.v2));
    }


    @Override
    @Nullable
    public OptionalInt getFactionID() {
        return factionID != null ? OptionalInt.of(factionID) : OptionalInt.empty();
    }

    @Override
    @Nullable
    public OptionalDouble getSecurity(DataSupplier dataSupplier) {
        return getMapPoints(dataSupplier).mapToDouble(solarsystem -> solarsystem.security).average();
    }

    @Override
    public String getConstituentName() {
        return "Solarsystems";
    }

    @Override
    public Stream<SolarSystem> getConstituents(DataSupplier dataSupplier) {
        return dataSupplier.getSolarSystems().stream().filter(solarSystem -> solarSystem.constellationID == constellationID);
    }

    @Override
    public boolean hasRender() {
        return true;
    }

    @Override
    public String getName() {
        return constellationName;
    }

    @Override
    public ResourceLocation getIcon(DataSupplier dataSupplier, Page page) {
        return new ResourceLocation("7_64_4.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page);
    }

    @Override
    public String toString() {
        return "Constellation{" +
                "regionID=" + regionID +
                ", constellationID=" + constellationID +
                ", constellationName='" + constellationName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", factionID=" + factionID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constellation that = (Constellation) o;
        return constellationID == that.constellationID;
    }

    @Override
    public int hashCode() {
        return constellationID;
    }
}
