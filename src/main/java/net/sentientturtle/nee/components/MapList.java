package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import net.sentientturtle.nee.pages.PageType;

/**
 * Lists subsections of a map, such as {@link net.sentientturtle.nee.orm.Region}s, {@link net.sentientturtle.nee.orm.Constellation}s or {@link net.sentientturtle.nee.orm.SolarSystem}s
 */
public class MapList extends Component {
    private final Mappable mappable;

    public MapList(Mappable mappable, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.mappable = mappable;
    }

    @Override
    protected String buildHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<div class='component map_list text_font'>"
                + "<span class='map_list_title head_font'>").append(mappable.getConstituentName()).append("</span>")
                .append("<table class='map_list_table'>");
        mappable.getConstituents(dataSupplier).forEach(mappable ->
                html.append("<tr><td class='map_list_td'>")
                .append("<img src='").append(mappable.getIcon(dataSupplier, page)).append("' height='64px' width='64px'>")
                .append("</td><td class='head_font map_list_td'><span class='map_list_type'>")
                .append(new PageReference(mappable.getName(), PageType.MAP, page.getPageType().getFolderDepth()))
                .append("</span></td></tr>")
        );
        html.append("</table></div>");
        return html.toString();
    }

    @Override
    protected String buildCSS() {
        return ".map_list {\n" +
                "  padding: 0.75em;\n" +
                "}\n" +
                "\n" +
                ".map_list_title {\n" +
                "  font-size: 1.75em;\n" +
                "  margin-left: 10px;\n" +
                "}\n" +
                "\n" +
                ".map_list_type {\n" +
                "  font-size: 1.25em;\n" +
                "  margin: 5px;\n" +
                "}\n" +
                "\n" +
                ".map_list_td {\n" +
                "  padding-top: 5px;\n" +
                "}";
    }
}
