package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;

import java.util.Map;

/**
 * Displays capacitor stats of a ship {@link Type}
 */
public class ShipCapacitor extends Component {
    private final Type type;

    public ShipCapacitor(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();
        return "<div class='component ship_capacitor text_font'>\n" +
                "    <table class='capacitor_table'>\n" +
                "        <tr>\n" +
                "            <td><span class='capacitor_span' title='Capacitor Capacity'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(482).iconID, dataSupplier) + "' height='32px' width='32px'>Capacitor Capacity: " + dataSupplier.unitify(attributeValueMap.get(new Tuple2<>(type.typeID, 482)), attributeMap.get(482).unitID) + "</span>\n" +
                "            </td>\n" +
                "            <td class='cap_recharge_time'><span class='capacitor_span' title='Capacitor Recharge Time'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(55).iconID, dataSupplier) + "' height='32px' width='32px'>Recharge Time: " + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 55), 0.0), attributeMap.get(55).unitID) + "</span>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>";
    }

    @Override
    public String buildCSS() {
        return ".capacitor_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".capacitor_span {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".cap_recharge_time {\n" +
                "  width: 46%;\n" +
                "}";
    }
}
