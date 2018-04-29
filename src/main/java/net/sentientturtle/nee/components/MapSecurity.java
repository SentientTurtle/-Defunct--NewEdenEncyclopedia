package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Mappable;
import net.sentientturtle.nee.util.MapRenderer;

import java.util.OptionalDouble;

/**
 * Displays security rating for a {@link Mappable} object.
 */
public class MapSecurity extends Component {
    private Mappable mappable;

    public MapSecurity(Mappable mappable) {
        this.mappable = mappable;
    }

    @Override
    protected String buildHTML(DataSupplier dataSupplier) {
        OptionalDouble security = mappable.getSecurity(dataSupplier);
        assert security.isPresent();
        int color = (int) Math.max(0, Math.round(security.getAsDouble()*10));
        return "<div class='component map_security'>"
                + "<b class='head_font'>Security: " + (double) Math.round(security.getAsDouble()*100) / 100 + "</b>"
                + "<span class='map_security_square' style='background-color: " + MapRenderer.SECURITY_COLORS[color] + "'> </span>"
        + "</div>";
    }

    @Override
    protected String buildCSS() {
        return ".map_security {\n" +
                "  padding: 1em;\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "  font-size: 1.5em;\n" +
                "}\n" +
                "\n" +
                ".map_security_square {\n" +
                "    width: 1em;\n" +
                "    height: 1em;\n" +
                "    border-radius: 3px 3px 3px 3px;\n" +
                "    margin-left: 5px;\n" +
                "}";
    }
}
