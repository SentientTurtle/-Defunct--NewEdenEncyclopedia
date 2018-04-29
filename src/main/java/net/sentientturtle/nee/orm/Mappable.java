package net.sentientturtle.nee.orm;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.util.Tuple2;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Stream;

/**
 * Data interface for objects that can be descriped as a map/chart.
 */
public interface Mappable {
    int getID();
    String getName();
    ResourceLocation getIcon(DataSupplier dataSupplier);

    double getX();
    double getY();
    double getZ();

    /**
     * @param dataSupplier Data supplier to use
     * @return Stream of mappable objects that form points on the map/chart of this mappable
     */
    Stream<? extends Mappable> getMapPoints(DataSupplier dataSupplier);

    /**
     * Returns a stream of lines between the points provided by {@link #getMapPoints(DataSupplier)
     * @param dataSupplier Datasupplier to use
     * @return Stream of Line-tuple of IDs (of Mappables in stream returned by {@link #getMapPoints(DataSupplier)}
     */
    //
    Stream<Tuple2<Integer, Integer>> getMapLines(DataSupplier dataSupplier);

    OptionalInt getFactionID();
    OptionalDouble getSecurity(DataSupplier dataSupplier);

    /**
     * @return The plural noun of the constituents of this mappable (Such as "Regions" being the constituents of a {@link net.sentientturtle.nee.orm.singleton.Cluster})
     */
    String getConstituentName();
    Stream<? extends Mappable> getConstituents(DataSupplier dataSupplier);

    boolean hasRender();
}
