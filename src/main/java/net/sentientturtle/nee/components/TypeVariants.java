package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.nee.util.Tuple2;

/**
 * Lists the variants of a {@link Type}
 */
public class TypeVariants extends Component {
    private final Type type;

    public TypeVariants(Type type) {
        this.type = type;
    }

    @Override
    protected String buildHTML(DataSupplier dataSupplier) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='component type_variants text_font'>" +
                "<span class='type_variants_title head_font'>Variants</span>" +
                "<table class='type_variants_table'>");
        int parentTypeID = dataSupplier.getParentTypes().getOrDefault(type.typeID, new Tuple2<>(type.typeID, 1)).v1;
        for (int typeID : dataSupplier.getMetaTypes().get(parentTypeID)) {
            if (typeID != type.typeID) {
                html.append("<tr><td class='type_variants_td'>")
                        .append("<img src='").append(ResourceLocation.iconOfTypeID(typeID, dataSupplier)).append("' height='64px' width='64px'>")
                        .append("</td><td class='head_font type_variants_td'><span class='type_variants_type'>")
                        .append(new PageReference(dataSupplier.getTypes().get(typeID).name, PageType.TYPE))
                        .append("</span></td></tr>");
            }
        }
        html.append("</table></div>");
        return html.toString();
    }

    @Override
    protected String buildCSS() {
        return ".type_variants {\n" +
                "  padding: 0.75em;\n" +
                "}\n" +
                "\n" +
                ".type_variants_title {\n" +
                "  font-size: 1.75em;\n" +
                "  margin-left: 10px;\n" +
                "}\n" +
                "\n" +
                ".type_variants_type {\n" +
                "  font-size: 1.25em;\n" +
                "  margin: 5px;\n" +
                "}\n" +
                "\n" +
                ".type_variants_td {\n" +
                "  padding-top: 5px;\n" +
                "}";
    }
}
