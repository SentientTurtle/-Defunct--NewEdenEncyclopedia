package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.singleton.Cluster;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Enum containing the various types of {@link Page}s
 */
public enum PageType {
    CATEGORY(dataSupplier -> dataSupplier.getCategories().values().stream().filter(category -> category.published).map(category -> new CategoryPage(category, dataSupplier))),
    GROUP(dataSupplier -> dataSupplier.getGroups().values().stream().filter(group -> group.published).map(group -> new GroupPage(group, dataSupplier))),
    TYPE(dataSupplier -> dataSupplier.getTypes().values().stream().filter(type -> type.published).map(type -> new TypePage(type, dataSupplier))),
    MAP(dataSupplier -> {
        return Stream.of(
                Stream.of(Cluster.K_SPACE, Cluster.W_SPACE),
                dataSupplier.getRegions().stream(),
                dataSupplier.getConstellations().stream(),
                dataSupplier.getSolarSystems().stream()
        ).flatMap(Function.identity())
                .map(mappable -> new MapPage(mappable, dataSupplier));
    });

    private final Function<DataSupplier, Stream<Page>> streamSupplier;

    /**
     * @param streamSupplier Function that returns a stream of all pages of the type, using a specified data supplier.
     */
    PageType(Function<DataSupplier, Stream<Page>> streamSupplier) {
        this.streamSupplier = streamSupplier;
    }

    /**
     * Utility method to get a single stream containing all pages that can be generated.
     * @param dataSupplier Data supplier to use.
     * @return Stream containing all pages that can be generated.
     */
    public static Stream<Page> pageStream(DataSupplier dataSupplier) {
        return Arrays.stream(values()).flatMap((Function<PageType, Stream<Page>>) pageType -> pageType.streamSupplier.apply(dataSupplier));
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}