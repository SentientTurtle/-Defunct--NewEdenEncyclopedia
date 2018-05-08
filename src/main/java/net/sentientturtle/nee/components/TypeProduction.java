package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.PageType;

/**
 * Displays industry origin of a {@link Type}
 */
public class TypeProduction extends Component {
    private final Type type;

    public TypeProduction(Type type, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.type = type;
    }

    @Override
    public String buildHTML() {
        assert dataSupplier.getProductActivityMap().containsKey(type.typeID);    // Should be handled by TypePage check
        //language=HTML
        final StringBuilder html = new StringBuilder()
                .append("<div class='component type_production text_font'>")
                .append("<span class='production_title'><b class='head_font'>Produced from:</b></span>");
        int length = html.length();
        dataSupplier.getProductActivityMap().get(type.typeID)
                .forEach(activity -> html.append("<br><div class='production_blueprint'><img src='").append(ResourceLocation.iconOfTypeID(activity.bpTypeID, dataSupplier, page)).append("' height='64' width='64'>")
                        .append("<b class='production_link head_font'>").append(new PageReference(dataSupplier.getTypes().get(activity.bpTypeID).name, PageType.TYPE, page.getPageType().getFolderDepth()))
                        .append(" (").append(dataSupplier.getIndustryActivityTypes().get(activity.activityID).activityName).append(')')
                        .append("</b></div>"));
        if (html.length() > length) {
            html.append("</div>");
            return html.toString();
        } else {
            return "";
        }
    }

    @Override
    public String buildCSS() {
        return ".type_production {\n" +
                "  padding: 1em;\n" +
                "}\n" +
                "\n" +
                ".production_title {\n" +
                "  font-size: 1.5em;\n" +
                "  text-align: center;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".production_blueprint {\n" +
                "  height: 64px;\n" +
                "  width: 100%;\n" +
                "  font-size: 1.25em;\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".production_link {\n" +
                "  margin: 5px;\n" +
                "}";
    }
}
