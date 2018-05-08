package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Group;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.orm.Category;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lists all {@link Group} in a {@link Category}, or all {@link net.sentientturtle.nee.orm.Type} in a {@link Group}
 */
public class ChildTable extends Component {
    private static final int TABLE_WIDTH = 3;
    private Category category;
    private Group group;

    public ChildTable(@NotNull Category category, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.category = category;
    }

    public ChildTable(@NotNull Group group, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.group = group;
    }

    @Override
    public String buildHTML() {
        assert (category != null && group == null) || (group != null && category == null);
        StringBuilder builder = new StringBuilder();

        //language=HTML
        builder.append("<div class='component'><div class='child_table_title head_font'><b>")
                .append(category != null ? "Item groups" : "Items").append("</b></div>")
                .append("<table class='child_table'>")
                .append("<col width='64'>")
                .append("<col>")
                .append("<col width='64'>")
                .append("<col>")
                .append("<col width='64'>")
                .append("<col>");

        AtomicInteger i = new AtomicInteger();
        if (category != null) {
            dataSupplier.getGroups().values().stream().filter(group -> group.published && group.categoryID == category.categoryID).forEach(group -> {
                if (i.get() % TABLE_WIDTH == 0) {
                    //language=HTML
                    builder.append("<tr class='child_table_row'>");
                }
                if (group.iconID != null) {
                    //language=HTML
                    builder.append("<td><img src='").append(ResourceLocation.iconOfIconID(group.iconID, dataSupplier, page)).append("' height='64px' width='64px'></td><td><span class='child_table_span head_font'>").append(new PageReference(group.name, PageType.GROUP, page.getPageType().getFolderDepth())).append("</span></td>");
                } else {
                    //language=HTML
                    builder.append("<td><span class='categoryGroupSpacer' style='width:64px'></span></td><td><span class='child_table_span head_font'>").append(new PageReference(group.name, PageType.GROUP, page.getPageType().getFolderDepth())).append("</span></td>");
                }
                if (i.get() % TABLE_WIDTH == (TABLE_WIDTH - 1)) {
                    //language=HTML
                    builder.append("<tr>");
                }
                i.getAndIncrement();
            });
        } else {
            dataSupplier.getTypes().values().stream().filter(type -> type.published && type.groupID == group.groupID).forEach(type -> {
                if (i.get() % TABLE_WIDTH == 0) {
                    //language=HTML
                    builder.append("<tr class='child_table_row'>");
                }
                if (group.iconID != null) {
                    //language=HTML
                    builder.append("<td><img src='").append(ResourceLocation.iconOfTypeID(type.typeID, dataSupplier, page)).append("' height='64px' width='64px'></td><td><span class='child_table_span head_font'>").append(new PageReference(type.name, PageType.TYPE, page.getPageType().getFolderDepth())).append("</span></td>");
                } else {
                    //language=HTML
                    builder.append("<td><span class='child_table_spacer'></span></td><td><span class='child_table_span head_font'>").append(new PageReference(type.name, PageType.TYPE, page.getPageType().getFolderDepth())).append("</span></td>");
                }
                if (i.get() % TABLE_WIDTH == (TABLE_WIDTH - 1)) {
                    //language=HTML
                    builder.append("<tr>");
                }
                i.getAndIncrement();
            });
        }
        if (i.get() % TABLE_WIDTH != (TABLE_WIDTH - 1)) {
            //language=HTML
            builder.append("<tr>");
        }
        //language=HTML
        builder.append("</table></div>");
        return builder.toString();
    }

    @Override
    public String buildCSS() {
        return ".child_table {\n" +
                "    table-layout: fixed;\n" +
                "    width: 95%;\n" +
                "    margin-left: 2.5%;\n" +
                "}\n" +
                "\n" +
                ".child_table_spacer {\n" +
                "    width: 64px;\n" +
                "}\n" +
                "\n" +
                ".child_table_span {\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    font-size: 1.5em;\n" +
                "}\n" +
                "\n" +
                ".child_table_row {\n" +
                "    height: 74px;\n" +
                "}\n" +
                "\n" +
                ".child_table_title {\n" +
                "    width: 100%;\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "    justify-content: center;\n" +
                "    font-size: 2em;\n" +
                "}";
    }
}
