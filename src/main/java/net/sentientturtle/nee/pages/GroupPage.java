package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.ChildTable;
import net.sentientturtle.nee.components.Title;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Group;
import org.jetbrains.annotations.Nullable;

/**
 * Page for a {@link Group}
 */
public class GroupPage extends Page {
    @SuppressWarnings("WeakerAccess")
    public final Group group;

    GroupPage(Group group, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.group = group;
    }

    @Override
    protected void init() {
        //noinspection LanguageMismatch
        leftComponents.add(new Title(group.name, getIcon(), dataSupplier, this));
        leftComponents.add(new ChildTable(group, dataSupplier, this));
    }

    @Override
    public PageType getPageType() {
        return PageType.GROUP;
    }

    @Override
    public String getPageName() {
        return group.name.replaceAll("[\\\\/:*?\"<>|\t]", "");
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return group.iconID != null ? ResourceLocation.iconOfIconID(group.iconID, dataSupplier,this) : null;
    }

    @Override
    protected boolean isMonoColumn() {
        return true;
    }
}
