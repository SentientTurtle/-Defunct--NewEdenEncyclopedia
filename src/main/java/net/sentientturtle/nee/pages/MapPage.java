package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.*;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;

/**
 * Map page for a {@link Mappable}
 */
public class MapPage extends Page {
    public final Mappable mappable;

    public MapPage(Mappable mappable, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.mappable = mappable;

        leftComponents.add(new Title(mappable.getName(), mappable.getIcon(dataSupplier)));
        if (mappable.hasRender())
            leftComponents.add(new TypeRender(new ResourceLocation(mappable, ResourceLocation.Type.MAP, dataSupplier)));

        if (mappable.getSecurity(dataSupplier) != null)
            leftComponents.add(new MapSecurity(mappable));

        if (mappable.getFactionID() != null)
            leftComponents.add(new MapSovereignty(mappable));

        rightComponents.add(new MapList(mappable));
    }

    @Override
    public PageType getPageType() {
        return PageType.MAP;
    }

    @Override
    public String getPageName() {
        return mappable.getName().replaceAll("[\\\\/:*?\"<>|\t]", "");
    }
}
