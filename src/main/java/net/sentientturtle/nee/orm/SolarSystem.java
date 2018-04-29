package net.sentientturtle.nee.orm;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.util.Tuple2;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Stream;

/**
 * Data object representing EVE Online SolarSystems
 * (The name 'SolarSystem' was chosen as EVE Online APIs use the term 'SolarSystem', rather than the more accurate 'Planetary system')
 */
public class SolarSystem implements Mappable {
    public final int regionID;
    public final int constellationID;
    public final int solarSystemID;
    public final String solarSystemName;
    public final double x;
    public final double y;
    public final double z;
    public final double security;
    @Nullable
    public final Integer factionID;
    public final int sunTypeID;

    public SolarSystem(int regionID, int constellationID, int solarSystemID, String solarSystemName, double x, double y, double z, double security, @Nullable Integer factionID, int sunTypeID) {
        this.regionID = regionID;
        this.constellationID = constellationID;
        this.solarSystemID = solarSystemID;
        this.solarSystemName = solarSystemName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.security = security;
        this.factionID = factionID;
        this.sunTypeID = sunTypeID;
    }

    @Override
    public int getID() {
        return solarSystemID;
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
    public Stream<? extends Mappable> getMapPoints(DataSupplier dataSupplier) {
        return Stream.empty();
    }

    @Override
    public Stream<Tuple2<Integer, Integer>> getMapLines(DataSupplier dataSupplier) {
        return Stream.empty();
    }

    @Override
    @Nullable
    public OptionalInt getFactionID() {
        return factionID != null ? OptionalInt.of(factionID) : OptionalInt.empty();
    }

    @Override
    @Nullable
    public OptionalDouble getSecurity(DataSupplier dataSupplier) {
        return OptionalDouble.of(security);
    }

    @Override
    public String getConstituentName() {
        return "Orbitals";
    }

    @Override
    public Stream<Mappable> getConstituents(DataSupplier dataSupplier) {
        return Stream.empty();
    }

    @Override
    public boolean hasRender() {
        return false;
    }

    @Override
    public String getName() {
        return solarSystemName;
    }

    @Override
    public ResourceLocation getIcon(DataSupplier dataSupplier) {
        return ResourceLocation.iconOfTypeID(sunTypeID, dataSupplier);
    }
}
