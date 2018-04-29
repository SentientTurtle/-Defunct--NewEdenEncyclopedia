package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Faction;
import net.sentientturtle.nee.orm.Mappable;

/**
 * Displays sovereignty ownership of a {@link Mappable} object
 */
public class MapSovereignty extends Component {
    private Mappable mappable;

    public MapSovereignty(Mappable mappable) {
        this.mappable = mappable;
    }

    @Override
    protected String buildHTML(DataSupplier dataSupplier) {
        Faction faction = dataSupplier.getFactions().get(mappable.getFactionID());
        return "<div class='component map_sovereignty'><div class='head_font'>Sovereignty</div><div class='sovereignty_faction'>" +
                "<img src='" + ResourceLocation.iconOfCorpID(faction.corporationID, dataSupplier) + "' height='64' width='64'>" +
                "<b class='head_font sovereignty_text'>" + faction.factionName + "</b></div></div>";
    }

    @Override
    protected String buildCSS() {
        return ".map_sovereignty {\n" +
                "  padding: 1em;\n" +
                "}\n" +
                "\n" +
                ".sovereignty_faction {\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".sovereignty_text {\n" +
                "  font-size: 1.5em;\n" +
                "  margin-left: 10px;\n" +
                "}";
    }
}
