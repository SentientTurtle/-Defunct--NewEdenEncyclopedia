package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.util.tuple.Tuple2;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Lists attributes of a {@link Type}
 */
public class TypeAttributes extends Component {
    private Type type;

    public TypeAttributes(Type type, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.type = type;
    }

    @Override
    protected String buildHTML() {
        assert dataSupplier.getTypeAttributes().containsKey(type.typeID);
        StringBuilder html = new StringBuilder();

        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();
        Set<Integer> attributes = dataSupplier.getTypeAttributes().get(type.typeID);
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        HashMap<Attribute, Double> attributeValues = new LinkedHashMap<>();   // Ordered map is required here

        if (type.mass > 0 && type.mass <= 200000000000.0) attributeValues.put(attributeMap.get(4), type.mass);
        if (type.volume > 0) attributeValues.put(attributeMap.get(161), type.volume);

        attributes.stream()
                .sorted()   // Attribute ordering required
                .map(attributeMap::get)
                .filter(attribute -> attribute.published)
                .forEach(attribute -> attributeValues.put(attribute, attributeValueMap.get(new Tuple2<>(type.typeID, attribute.attributeID))));

        html.append("<div class='component item_attributes text_font'><table class='attribute_table'>");

        attributeValues.forEach((attribute, value) -> {
            html.append("<tr>");
            if (attribute.iconID != 0) {
                html.append("<td class='attribute_table_data attribute_table_icon'><img src='").append(ResourceLocation.iconOfIconID(attribute.iconID, dataSupplier, page)).append("' height='32px' width='32px'></td>");
            } else {
                html.append("<td class='attribute_table_data attribute_table_icon'></td>");
            }
            html.append("<td class='attribute_table_data'><span>").append(attribute.displayName == null ? attribute.attributeName : attribute.displayName).append("</span></td>")
                    .append("<td class='attribute_table_data'><span>").append(dataSupplier.unitify(value, attribute.unitID, page)).append("</span></td>")
                    .append("</tr>");
        });

        html.append("</table></div>");
        return html.toString();
    }

    @Override
    protected String buildCSS() {
        return ".attribute_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".attribute_table_data {\n" +
                "  height: 32px;\n" +
                "  font-size: 1em;\n" +
                "  padding-right: 10px;\n" +
                "}\n" +
                "\n" +
                ".attribute_table_icon {\n" +
                "  width: 32px;\n" +
                "}";
    }
}
