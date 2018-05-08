package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Faction;
import net.sentientturtle.nee.orm.Mappable;

import java.util.OptionalInt;

/**
 * Displays sovereignty ownership of a {@link Mappable} object
 */
public class MapSovereignty extends Component {
    private Mappable mappable;

    public MapSovereignty(Mappable mappable, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.mappable = mappable;
    }

    @Override
    protected String buildHTML() {
        OptionalInt factionID = mappable.getFactionID();
        assert factionID.isPresent();
        Faction faction = dataSupplier.getFactions().get(factionID.getAsInt());
        //noinspection LanguageMismatch
        return "<div class='component map_sovereignty'><div class='head_font'>Sovereignty</div><div class='sovereignty_faction'>" +
                "<img src='" + ResourceLocation.iconOfCorpID(faction.corporationID, dataSupplier, page) + "' height='64' width='64'>" +
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
