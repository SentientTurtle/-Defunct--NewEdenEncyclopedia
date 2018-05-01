package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;

import java.util.Map;

/**
 * Displays sensor stats of a ship {@link Type}
 */
public class ShipSensors extends Component {
    private final Type type;

    public ShipSensors(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();

        return "<div class='component text_font'>\n" +
                "    <table class='sensor_table'>\n" +
                "        <tr>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Targeting Range'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(76).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 76), 0.0), attributeMap.get(76).unitID) + "</span></td>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Scan Resolution'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(564).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 564), 0.0), attributeMap.get(564).unitID) + "</span></td>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Maximum Targets'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(192).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 192), 0.0), attributeMap.get(192).unitID) + "</span></td>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Signature Radius'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(552).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 552), 0.0), attributeMap.get(552).unitID) + "</span></td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Radar Sensor Strength'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(208).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 208), 0.0), attributeMap.get(208).unitID) + "</span></td>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Magnetometric Sensor Strength'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(210).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 210), 0.0), attributeMap.get(210).unitID) + "</span></td>\n" +
                "            <td colspan='1'><span class='sensor_span' title='Gravimetric Sensor Strength'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(211).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 211), 0.0), attributeMap.get(211).unitID) + "</span></td>\n" +
                "            <td colspan='1'><span class='sensor_span' title='LADAR Sensor Strength'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(209).iconID, dataSupplier) + "' height='32px' width='32px'>" + dataSupplier.unitify(attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 209), 0.0), attributeMap.get(209).unitID) + "</span></td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>";
    }

    @Override
    public String buildCSS() {
        return ".sensor_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".sensor_span {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}";
    }
}
