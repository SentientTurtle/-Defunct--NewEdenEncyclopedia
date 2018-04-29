package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.ChildTable;
import net.sentientturtle.nee.components.Title;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Category;

/**
 * Page for a {@link Category}
 */
public class CategoryPage extends Page {
    public final Category category;

    public CategoryPage(Category category, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.category = category;
        leftComponents.add(new Title(category.name, category.iconID != null ? ResourceLocation.iconOfIconID(category.iconID, dataSupplier) : null));
        leftComponents.add(new ChildTable(category));
    }

    @Override
    public PageType getPageType() {
        return PageType.CATEGORY;
    }

    @Override
    public String getPageName() {
        return category.name.replaceAll("[\\\\/:*?\"<>|\t]", "");
    }
}
