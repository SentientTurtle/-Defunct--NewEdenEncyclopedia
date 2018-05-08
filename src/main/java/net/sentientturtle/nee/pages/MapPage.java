package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.*;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import org.jetbrains.annotations.Nullable;

/**
 * Map page for a {@link Mappable}
 */
public class MapPage extends Page {
    public final Mappable mappable;

    MapPage(Mappable mappable, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.mappable = mappable;
    }

    @Override
    protected void init() {
        leftComponents.add(new Title(mappable.getName(), getIcon(), dataSupplier, this));
        if (mappable.hasRender())
            leftComponents.add(new TypeRender(new ResourceLocation(mappable, ResourceLocation.Type.MAP, dataSupplier, this), dataSupplier, this));

        if (mappable.getSecurity(dataSupplier).isPresent())
            leftComponents.add(new MapSecurity(mappable, dataSupplier, this));

        if (mappable.getFactionID().isPresent())
            leftComponents.add(new MapSovereignty(mappable, dataSupplier, this));

        rightComponents.add(new MapList(mappable, dataSupplier, this));
    }

    @Override
    public PageType getPageType() {
        return PageType.MAP;
    }

    @Override
    public String getPageName() {
        return mappable.getName().replaceAll("[\\\\/:*?\"<>|\t]", "");
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return mappable.getIcon(dataSupplier, this);
    }
}
