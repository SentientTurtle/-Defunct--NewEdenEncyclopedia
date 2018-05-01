package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;
import net.sentientturtle.nee.util.Tuple4;
import org.intellij.lang.annotations.Language;

/**
 * Displays resists and health stats of a given subtype for a given ship {@link Type}
 * Subtype implemented by subclasses.
 * @see ShipShield
 * @see ShipArmor
 * @see ShipHull
 */
public abstract class ShipResists extends Component {
    protected Type type;

    public ShipResists(Type type) {
        this.type = type;
    }

    @Override
    @Language("HTML")
    protected String buildHTML(DataSupplier dataSupplier) {
        @Language("HTML")
        String html = "<div class='component ship_resists text_font'>\n" +
                "    <B class='headFont ship_resists_title head_font'>" + getType() + "</B>\n" +
                "    <table class='resists_table'>\n" +
                "        <tr>\n" +
                "            <td class='resists_icon_td'><img src='" + ResourceLocation.iconOfIconID(dataSupplier.getAttributes().get(getHpAttribute()).iconID, dataSupplier) + "' height='32px' width='32px'></td>\n" +
                "            <td>" + getType() + " Hitpoints: " + getHp(dataSupplier, getHpAttribute()) + "</td>\n" +
                "            <td></td>\n" +
                "        </tr>";
        if (getRechargeText(dataSupplier) != null) {
            html += "<tr>\n" +
                    "    <td class='resists_icon_td'><img src='" + new ResourceLocation("22_32_16.png", ResourceLocation.Type.ITEM_ICON, dataSupplier) + "' height='32px' width='32px'></td><td>" + getRechargeText(dataSupplier) + "</td><td></td>\n" +
                    "</tr>";
        }
        Tuple4<Double, Double, Double, Double> resistTuple = getResists(dataSupplier);
        if (resistTuple != null && (resistTuple.v1 != 0 || resistTuple.v2 != 0 || resistTuple.v3 != 0 || resistTuple.v4 != 0)) {
            html += "<tr title='Electromagnetic Damage Resistance'>\n" +
                    "    <td class='resists_icon_td'><img src='" + new ResourceLocation("22_32_12.png", ResourceLocation.Type.ITEM_ICON, dataSupplier) + "' height='32px' width='32px'></td><td class='resists_bar_td'><progress class='resist_bar em_resists' value=" + (1 - resistTuple.v1) + "></progress></td><td class='resists_text_td'>" + dataSupplier.unitify(resistTuple.v1, dataSupplier.getAttributes().get(267).unitID) + "</td>\n" +
                    "</tr>\n" +
                    "<tr title='Thermal Damage Resistance'>\n" +
                    "    <td class='resists_icon_td'><img src='" + new ResourceLocation("22_32_10.png", ResourceLocation.Type.ITEM_ICON, dataSupplier) + "' height='32px' width='32px'></td><td class='resists_bar_td'><progress class='resist_bar th_resists' value=" + (1 - resistTuple.v2) + "></progress></td><td class='resists_text_td'>" + dataSupplier.unitify(resistTuple.v2, dataSupplier.getAttributes().get(270).unitID) + "</td>\n" +
                    "</tr>\n" +
                    "<tr title='Kinetic Damage Resistance'>\n" +
                    "    <td class='resists_icon_td'><img src='" + new ResourceLocation("22_32_9.png", ResourceLocation.Type.ITEM_ICON, dataSupplier) + "' height='32px' width='32px'></td><td class='resists_bar_td'><progress class='resist_bar ki_resists' value=" + (1 - resistTuple.v3) + "></progress></td><td class='resists_text_td'>" + dataSupplier.unitify(resistTuple.v3, dataSupplier.getAttributes().get(269).unitID) + "</td>\n" +
                    "</tr>\n" +
                    "<tr title='Explosive Damage Resistance'>\n" +
                    "    <td class='resists_icon_td'><img src='" + new ResourceLocation("22_32_11.png", ResourceLocation.Type.ITEM_ICON, dataSupplier) + "' height='32px' width='32px'></td><td class='resists_bar_td'><progress class='resist_bar ex_resists' value=" + (1 - resistTuple.v4) + "></progress></td><td class='resists_text_td'>" + dataSupplier.unitify(resistTuple.v4, dataSupplier.getAttributes().get(268).unitID) + "</td>\n" +
                    "</tr>";
        }
        html += "</table></div>";
        return html;
    }

    /**
     * @return Attribute for the Hitpoints attribute for the health type implemented by subclass
     */
    protected abstract int getHpAttribute();

    /**
     * @return Name of health type implemented by subclass
     */
    protected abstract String getType();

    /**
     * Specifies resist attribute values for the health type implemented by subclass
     * @param dataSupplier Data supplier to use
     * @return Tuple4 of resist attribute values, in order (Electromagnetic, Thermal, Kinetic, Explosive)
     */
    protected abstract Tuple4<Double, Double, Double, Double> getResists(DataSupplier dataSupplier);

    /**
     * Specifies text to be shown for HP regeneration for the health type implemented by subclass
     * @param dataSupplier Data supplier to use
     * @return Text to be shown for HP regeneration, or null if no such text should be shown
     */
    protected abstract String getRechargeText(DataSupplier dataSupplier);

    // Helper method for fetching hitpoints string
    private String getHp(DataSupplier dataSupplier, int hpAttributeID) {
        return dataSupplier.unitify(dataSupplier.getAttributeValues().get(new Tuple2<>(type.typeID, hpAttributeID)), dataSupplier.getAttributes().get(hpAttributeID).unitID);
    }


    /**
     * Helper method for subclasses, fetches resists from a given data supplier using attributes for the resists of the health type implemented by subclass
     * @param dataSupplier Data supplier to use
     * @param emAttribute Electromagnetic resistance attributeID
     * @param thAttribute Thermal resistance attributeID
     * @param kiAttribute Kinetic resistance attributeID
     * @param exAttribute Explosive resistance attributeID
     * @see #getResists(DataSupplier)
     */
    protected Tuple4<Double, Double, Double, Double> getResists(DataSupplier dataSupplier, int emAttribute, int thAttribute, int kiAttribute, int exAttribute) {
        return new Tuple4<>(
                dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, emAttribute), 0.0),
                dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, thAttribute), 0.0),
                dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, kiAttribute), 0.0),
                dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, exAttribute), 0.0)
        );
    }

    @Language("CSS")
    protected String buildCSS() {
        return ".ship_resists {\n" +
                "    padding: 5px;\n" +
                "}\n" +
                "\n" +
                ".ship_resists_title {\n" +
                "    font-size: 1.5em;\n" +
                "    padding: 5px;\n" +
                "}\n" +
                "\n" +
                ".resists_table {\n" +
                "    width: 100%;\n" +
                "}\n" +
                "\n" +
                ".resists_icon_td {\n" +
                "    width: 32px;\n" +
                "}\n" +
                "\n" +
                ".resists_text_td {\n" +
                "    padding-left: 10px;\n" +
                "    padding-right: 10px;\n" +
                "    white-space: nowrap;\n" +
                "}\n" +
                "\n" +
                ".resists_bar_td {\n" +
                "    width: 100%;\n" +
                "    padding-left: 10px;\n" +
                "    padding-top: 5px;\n" +
                "    padding-bottom: 5px\n" +
                "}\n" +
                "\n" +
                "progress[value] {\n" +
                "    /* Reset the default appearance */\n" +
                "    -webkit-appearance: none;\n" +
                "    /* appearance: none; */\n" +
                "}\n" +
                "\n" +
                "progress::-webkit-progress-bar {\n" +
                "    background: #242526;\n" +
                "}\n" +
                "\n" +
                ".resist_bar {\n" +
                "    height: 24px;\n" +
                "    width: 100%;\n" +
                "}\n" +
                "\n" +
                ".em_resists::-webkit-progress-value {\n" +
                "    background: #258BCE;\n" +
                "}\n" +
                "\n" +
                ".th_resists::-webkit-progress-value {\n" +
                "    background: #CE2525;\n" +
                "}\n" +
                "\n" +
                ".ki_resists::-webkit-progress-value {\n" +
                "    background: #A9A9A9;\n" +
                "}\n" +
                "\n" +
                ".ex_resists::-webkit-progress-value {\n" +
                "    background: #CE8B25;\n" +
                "}";
    }
}
