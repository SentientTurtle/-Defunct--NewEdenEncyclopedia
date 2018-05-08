package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.ChildTable;
import net.sentientturtle.nee.components.Title;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Category;
import org.jetbrains.annotations.Nullable;

/**
 * Page for a {@link Category}
 */
public class CategoryPage extends Page {
    @SuppressWarnings("WeakerAccess")
    public final Category category;

    CategoryPage(Category category, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.category = category;
    }

    @Override
    protected void init() {
        //noinspection LanguageMismatch
        leftComponents.add(new Title(category.name, getIcon(), dataSupplier, this));
        leftComponents.add(new ChildTable(category, dataSupplier, this));
    }

    @Override
    public PageType getPageType() {
        return PageType.CATEGORY;
    }

    @Override
    public String getPageName() {
        return category.name.replaceAll("[\\\\/:*?\"<>|\t]", "");
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return category.iconID != null ? ResourceLocation.iconOfIconID(category.iconID, dataSupplier, this) : null;
    }

    @Override
    protected boolean isMonoColumn() {
        return true;
    }
}
