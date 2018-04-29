package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Attribute;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;
import org.intellij.lang.annotations.Language;

import java.util.Map;

/**
 * Displays fighter stats of a ship {@link Type}
 */
public class ShipFighters extends Component {
    private final Type type;

    public ShipFighters(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        Map<Integer, Attribute> attributeMap = dataSupplier.getAttributes();
        Map<Tuple2<Integer, Integer>, Double> attributeValueMap = dataSupplier.getAttributeValues();

        int lightSquadronLimit = attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2217), attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2737), 0.0)).intValue();
        int supportSquadronLimit = attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2218), attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2738), 0.0)).intValue();
        int heavySquadronLimit = attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2219), attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2739), 0.0)).intValue();
        int launchTubes = attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2216), 0.0).intValue();
        double hangarCapacity = attributeValueMap.getOrDefault(new Tuple2<>(type.typeID, 2055), 0.0);
        ;
        String launchTubeString = dataSupplier.unitify(launchTubes, attributeMap.get(2216).unitID);
        String hangarCapacityString = dataSupplier.unitify(hangarCapacity, attributeMap.get(2055).unitID);
        String lightSquadronString = dataSupplier.unitify(lightSquadronLimit, attributeMap.get(2217).unitID);
        String supportSquadronString = dataSupplier.unitify(supportSquadronLimit, attributeMap.get(2218).unitID);

        @Language("HTML")
        String string = "<div class='component ship_fighters text_font'>" +
                "<table class='fighter_table'>" +
                "<tr>" +
                "<td><span class='fighter_span' title='Fighter Launch Tubes'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(2216).iconID, dataSupplier) + "' height='32px' width='32px'>Fighter Launch Tubes</span></td><td>" + launchTubeString + "</td>" +
                "<td><span class='fighter_span' title='Fighter Hangar Capacity'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(2055).iconID, dataSupplier) + "' height='32px' width='32px'>Hangar Size</span></td><td>" + hangarCapacityString + "</td>" +
                "</tr>" +
                "<tr>" +
                "<td><span class='fighter_span' title='Light Fighter Squadron Limit'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(2217).iconID, dataSupplier) + "' height='32px' width='32px'>Light Fighter Squadron Limit</span></td><td>" + lightSquadronString + "</td>" +
                "<td><span class='fighter_span' title='Support Fighter Squadron Limit'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(2218).iconID, dataSupplier) + "' height='32px' width='32px'>Support Fighter Squadron Limit</span></td><td>" + supportSquadronString + "</td>" +
                "</tr>";
        if (heavySquadronLimit > 0) {
            String heavySquadronString = dataSupplier.unitify(heavySquadronLimit, 2219);
            //language=HTML
            string += "<tr>"
                    + "<td colspan='4'><span class='fighter_span heavy_fighter_span' title='Heavy Fighter Squadron Limit'><img src='" + ResourceLocation.iconOfIconID(attributeMap.get(2219).iconID, dataSupplier) + "' height='32px' width='32px'>Heavy Fighter Squadron Limit " + heavySquadronString + "</span></td>"
                    + "</tr>";
        }
        string += "</table></div>";
        return string;
    }

    @Override
    public String buildCSS() {
        return ".fighter_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".fighter_span {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".ship_fighter_title {\n" +
                "  font-size: 1.5em;\n" +
                "  padding: 5px;\n" +
                "}\n" +
                "\n" +
                ".heavy_fighter_span {\n" +
                "  justify-content: center;\n" +
                "}";
    }
}
