package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.util.tuple.Tuple2;
import org.intellij.lang.annotations.Language;

/**
 * Displays fitting stats of a ship {@link Type}
 */
public class ShipFitting extends Component {
    private Type type;

    public ShipFitting(Type type, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.type = type;
    }

    @Override
    protected String buildHTML() {
        int highSlots = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 14), 0.0).intValue();
        int medSlots = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 13), 0.0).intValue();
        int lowSlots = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 12), 0.0).intValue();
        int rigSize = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 1547), 0.0).intValue();
        int rigSlots = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 1137), 0.0).intValue();
        int calibration = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 1132), 0.0).intValue();
        double powerGrid = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 11), 0.0);
        double cpuOutput = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 48), 0.0);
        int turretHardpoints = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 102), 0.0).intValue();
        int launcherHardpoints = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 101), 0.0).intValue();
        int subsystemSlots = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(type.typeID, 1367), 0.0).intValue();

        // Determine what parts of this component should be shown
        boolean showHMLSlots = highSlots != 0 || medSlots != 0 || lowSlots != 0;
        boolean showSubsystemSlots = subsystemSlots != 0;
        boolean showRigSlots = rigSlots != 0 || calibration != 0;
        boolean showPGCPU = powerGrid != 0 || cpuOutput != 0;
        boolean showHardpoints = turretHardpoints != 0 || launcherHardpoints != 0;


        if (showHMLSlots || showSubsystemSlots || showRigSlots || showPGCPU || showHardpoints) {
            @Language("HTML")
            String html = "<div class='component ship_fitting text_font'>\n" +
                    "        <table class='fitting_table'>";
            if (showHMLSlots) {
                html += "<tr>\n" +
                        "    <td colspan='2'><span class='fitting_span'><img src='" + new ResourceLocation("8_64_11.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>High slots: " + dataSupplier.unitify(highSlots, getUnitID(dataSupplier, 14), page) + "</span></td>\n" +
                        "    <td colspan='2'><span class='fitting_span'><img src='" + new ResourceLocation("8_64_10.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Med slots: " + dataSupplier.unitify(medSlots, getUnitID(dataSupplier, 13), page) + "</span></td>\n" +
                        "    <td colspan='2'><span class='fitting_span'><img src='" + new ResourceLocation("8_64_9.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Low slots: " + dataSupplier.unitify(lowSlots, getUnitID(dataSupplier, 12), page) + "</span></td>\n" +
                        "</tr>";
            }
            if (showSubsystemSlots) {
                html += "<tr>\n" +
                        "    <td colspan='6'><span class='fitting_span'><img src='" + new ResourceLocation("76_64_4.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Subsystem slots: " + dataSupplier.unitify(subsystemSlots, getUnitID(dataSupplier, 1367), page) + "</span></td>\n" +
                        "</tr>";
            }
            if (showRigSlots) {
                html += "<tr>\n" +
                        "    <td colspan='3'><span class='fitting_span'><img src='" + new ResourceLocation("68_64_1.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>" + dataSupplier.unitify(rigSize, getUnitID(dataSupplier, 1547), page) + " rig slots: " + dataSupplier.unitify(rigSlots, getUnitID(dataSupplier, 1137), page) + "</span></td>\n" +
                        "    <td colspan='3'><span class='fitting_span'><img src='" + new ResourceLocation("55_64_13.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Calibration: " + dataSupplier.unitify(calibration, getUnitID(dataSupplier, 1132), page) + "</span></td>\n" +
                        "</tr>";
            }
            if (showPGCPU) {
                html += "<tr>\n" +
                        "    <td colspan='3'><span class='fitting_span'><img src='" + new ResourceLocation("2_64_7.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Power Grid: " + dataSupplier.unitify(powerGrid, getUnitID(dataSupplier, 11), page) + "</span></td>\n" +
                        "    <td colspan='3'><span class='fitting_span'><img src='" + new ResourceLocation("12_64_7.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>CPU Output: " + dataSupplier.unitify(cpuOutput, getUnitID(dataSupplier, 48), page) + "</span></td>\n" +
                        "</tr>";
            }
            if (showHardpoints) {
                html += "<tr>\n" +
                        "    <td colspan='3'><span class='fitting_span'><img src='" + new ResourceLocation("12_64_9.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Turrets: " + dataSupplier.unitify(turretHardpoints, getUnitID(dataSupplier, 102), page) + "</span></td>\n" +
                        "    <td colspan='3'><span class='fitting_span'><img src='" + new ResourceLocation("12_64_12.png", ResourceLocation.Type.ITEM_ICON, dataSupplier, page) + "' height='48px' width='48px'>Launchers: " + dataSupplier.unitify(launcherHardpoints, getUnitID(dataSupplier, 101), page) + "</span></td>\n" +
                        "</tr>";
            }
            html += "</table></div>";
            return html;
        } else {
            return "";
        }
    }

    // Small utility method
    private int getUnitID(DataSupplier dataSupplier, int attributeID) {
        return dataSupplier.getAttributes().get(attributeID).unitID;
    }

    @Override
    protected String buildCSS() {
        return ".fitting_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".fitting_span {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}";
    }
}
