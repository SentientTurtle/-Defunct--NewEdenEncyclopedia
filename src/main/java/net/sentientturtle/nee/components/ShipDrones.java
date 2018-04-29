package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;

import java.util.Map;

/**
 * Displays drone stats of a ship {@link Type}
 */
public class ShipDrones extends Component {
    private final Type type;

    public ShipDrones(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();

        return "<div class='component ship_drones text_font'>\n" +
                "    <table class='drone_table'>\n" +
                "        <tr>\n" +
                "            <td>\n" +
                "                <span class='drone_span' title='Drone Capacity'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(283).iconID, dataSupplier) + "' height='32px' width='32px'>Drone Capacity: " + dataSupplier.unitify(attributeValueMap.get(new Tuple2<>(type.typeID, 283)), attributeMap.get(283).unitID) + "</span>\n" +
                "            </td>\n" +
                "            <td class='drone_bandwidth'>\n" +
                "                <span class='drone_span' title='Drone Bandwidth'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(1271).iconID, dataSupplier) + "' height='32px' width='32px'>Drone Bandwidth: " + dataSupplier.unitify(attributeValueMap.get(new Tuple2<>(type.typeID, 1271)), attributeMap.get(1271).unitID) + "</span>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>";
    }

    @Override
    public String buildCSS() {
        return ".drone_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".drone_span {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".drone_bandwidth {\n" +
                "  width: 46%;\n" +
                "}";
    }
}
