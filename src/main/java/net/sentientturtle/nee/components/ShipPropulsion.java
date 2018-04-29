package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

/**
 * Displays propulsion stats of a ship {@link Type}
 */
public class ShipPropulsion extends Component {
    private final Type type;

    public ShipPropulsion(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();

        return "<DIV class='component text_font'>\n" +
                "    <table class='propulsion_table'>\n" +
                "        <tr>\n" +
                "            <td colspan='1'><span class='propulsion_span' title='Maximum Velocity'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(37).iconID, dataSupplier) + "' height='32px' width='32px'>Maximum Velocity: " + dataSupplier.unitify(attributeValueMap.get(new Tuple2<>(type.typeID, 37)), attributeMap.get(47).unitID) + "</span>\n" +
                "            </td>\n" +
                "            <td colspan='1' class='propulsion_warp_speed'><span class='propulsion_span' title='Ship Warp Speed'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(600).iconID, dataSupplier) + "' height='32px' width='32px'>Warp Speed: " + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 600), 0.0), -1) + "AU/s</span>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</DIV>";
    }

    @Override
    public String buildCSS() {
        return ".propulsion_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".propulsion_span {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".propulsion_warp_speed {\n" +
                "  width: 46%;\n" +
                "}";
    }
}