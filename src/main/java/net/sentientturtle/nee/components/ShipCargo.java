package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;

import java.util.Map;

/**
 * Displays cargo stats of a ship {@link Type}
 */
public class ShipCargo extends Component {
    private final Type type;

    public ShipCargo(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        StringBuilder builder = new StringBuilder("<div class='component ship_cargo text_font'>\n" +
                "        <b class='head_font ship_cargo_title'>Cargo bays</b>\n" +
                "        <br>\n" +
                "        <br>\n" +
                "        <table class='cargo_table'>");
        int length = builder.length();
        if (type.capacity > 0) {
            builder.append("<tr><td>Cargo bay capacity</td><td>").append(dataSupplier.unitify(type.capacity, 9)).append("</td></tr>");
        }
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();

        attributeMap.values().stream().filter(attribute -> (attribute.attributeID != 161)
                && ((attribute.categoryID == 4) || (attribute.categoryID == 40))
                && (attribute.unitID == 9))
                .forEach(attribute -> {
                    Double value = attributeValueMap.get(new Tuple2<>(type.typeID, attribute.attributeID));
                    if (value != null) {
                        builder.append("<tr><td>").append(attribute.displayName).append("</td><td>").append(dataSupplier.unitify(value, attribute.unitID)).append("</td></tr>");
                    }
                });
        if (builder.length() > length) {
            return builder.append("</table></div>").toString();
        } else {
            return "";
        }
    }

    @Override
    public String buildCSS() {
        return ".ship_cargo {\n" +
                "  padding: 1em;\n" +
                "}\n" +
                "\n" +
                ".ship_cargo_title {\n" +
                "  font-size: 1.75em;\n" +
                "}\n" +
                "\n" +
                ".cargo_table {\n" +
                "  width: 100%;\n" +
                "}";
    }
}
