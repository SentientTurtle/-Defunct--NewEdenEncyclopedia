package net.sentientturtle.nee.orm.singleton;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import net.sentientturtle.nee.orm.SolarSystem;
import net.sentientturtle.nee.util.Tuple2;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Stream;

/**
 * Contains singleton {@link Mappable} instances for the two star clusters in EVE Online
 */
public class Cluster {
    /**
     * Singleton representing the "New Eden Cluster", also known as "Known Space"
     */
    public static final Mappable K_SPACE = new Mappable() {
        @Override
        public int getID() {
            return -1;
        }

        @Override
        public double getX() {
            return 0;
        }

        @Override
        public double getY() {
            return 0;
        }

        @Override
        public double getZ() {
            return 0;
        }

        @Override
        public Stream<SolarSystem> getMapPoints(DataSupplier dataSupplier) {
            return dataSupplier.getSolarSystems().stream().filter(solarSystem -> solarSystem.solarSystemID < 31_000_000);
        }

        @Override
        public Stream<Tuple2<Integer, Integer>> getMapLines(DataSupplier dataSupplier) {
            return Stream.empty();
        }


        @Override
        public OptionalInt getFactionID() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalDouble getSecurity(DataSupplier dataSupplier) {
            return getMapPoints(dataSupplier).mapToDouble(solarsystem -> solarsystem.security).average();
        }

        @Override
        public String getConstituentName() {
            return "Regions";
        }

        @Override
        public Stream<? extends Mappable> getConstituents(DataSupplier dataSupplier) {
            return dataSupplier.getRegions().stream().filter(region -> region.regionID < 11_000_000);
        }

        @Override
        public boolean hasRender() {
            return true;
        }

        @Override
        public String getName() {
            return "New Eden Cluster";
        }

        @Override
        public ResourceLocation getIcon(DataSupplier dataSupplier) {
            return new ResourceLocation("7_64_4.png", ResourceLocation.Type.ITEM_ICON, dataSupplier);
        }
    };

    /**
     * Singleton representing the "Anoikis" cluster, also known as "Wormhole Space" or "Unknown Space"
     */
    public static final Mappable W_SPACE = new Mappable() {
        @Override
        public int getID() {
            return -2;
        }

        @Override
        public double getX() {
            return 0;
        }

        @Override
        public double getY() {
            return 0;
        }

        @Override
        public double getZ() {
            return 0;
        }

        @Override
        public Stream<SolarSystem> getMapPoints(DataSupplier dataSupplier) {
            return dataSupplier.getSolarSystems().stream().filter(solarSystem -> solarSystem.solarSystemID > 31_000_000);
        }

        @Override
        public Stream<Tuple2<Integer, Integer>> getMapLines(DataSupplier dataSupplier) {
            return Stream.empty();
        }

        @Override
        public OptionalInt getFactionID() {
            return OptionalInt.empty();
        }

        @Override
        public OptionalDouble getSecurity(DataSupplier dataSupplier) {
            return getMapPoints(dataSupplier).mapToDouble(solarsystem -> solarsystem.security).average();
        }

        @Override
        public String getConstituentName() {
            return "Regions";
        }

        @Override
        public Stream<? extends Mappable> getConstituents(DataSupplier dataSupplier) {
            return dataSupplier.getRegions().stream().filter(region -> region.regionID > 11_000_000);
        }

        @Override
        public boolean hasRender() {
            return true;
        }

        @Override
        public String getName() {
            return "Anoikis";
        }

        @Override
        public ResourceLocation getIcon(DataSupplier dataSupplier) {
            return new ResourceLocation("7_64_4.png", ResourceLocation.Type.ITEM_ICON, dataSupplier);
        }
    };
}
