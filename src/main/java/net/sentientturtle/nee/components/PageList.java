package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.util.ResourceLocation;

public class PageList extends Component {
    private final Page[] targetPages;
    private final String groupName;

    public PageList(Page[] targetPages, String groupName, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.targetPages = targetPages;
        this.groupName = groupName;
    }

    @Override
    protected String buildHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<div class='component page_list text_font'>"
                + "<span class='page_list_title head_font'>").append(groupName).append("</span>")
                .append("<table class='page_list_table'>");
        for (Page targetPage : targetPages) {
            html.append("<tr><td class='page_list_td'>");
            ResourceLocation icon = targetPage.getIcon();
            if (icon != null) html.append("<img src='").append(icon).append("' height='32px' width='32px'>");
            html.append("</td><td class='head_font page_list_td'><span class='page_list_type'>")
                    .append(new PageReference(targetPage, this.page.getPageType().getFolderDepth()))
                    .append("</span></td></tr>");
        }

        html.append("</table></div>");
        return html.toString();
    }

    @Override
    protected String buildCSS() {
        return ".page_list {\n" +
                "    padding: 0.75em;\n" +
                "}\n" +
                "\n" +
                ".page_list_title {\n" +
                "    font-size: 1.75em;\n" +
                "    margin-left: 10px;\n" +
                "}\n" +
                "\n" +
                ".page_list_type {\n" +
                "    font-size: 1.25em;\n" +
                "    margin: 5px;\n" +
                "}\n" +
                "\n" +
                ".page_list_td {\n" +
                "    padding-top: 5px;\n" +
                "    height: 32px;\n" +
                "}";
    }
}
