package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.ChildTable;
import net.sentientturtle.nee.components.Title;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Group;

/**
 * Page for a {@link Group}
 */
public class GroupPage extends Page {
    public final Group group;

    public GroupPage(Group group, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.group = group;
        leftComponents.add(new Title(group.name, group.iconID != null ? ResourceLocation.iconOfIconID(group.iconID, dataSupplier) : null));
        leftComponents.add(new ChildTable(group));
    }

    @Override
    public PageType getPageType() {
        return PageType.GROUP;
    }

    @Override
    public String getPageName() {
        return group.name.replaceAll("[\\\\/:*?\"<>|\t]", "");
    }
}
