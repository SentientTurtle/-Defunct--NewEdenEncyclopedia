package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.util.tuple.Tuple3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Displays the size of a {@link net.sentientturtle.nee.orm.Type}
 */
public class TypeSize extends Component {
    private final Tuple3<Double, Double, Double> sizeTuple;
    private static final ThreadLocal<DecimalFormat> smallFormat;
    private static final ThreadLocal<DecimalFormat> largeFormat;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        smallFormat = ThreadLocal.withInitial(() ->new DecimalFormat("#,###.#", symbols));
        largeFormat = ThreadLocal.withInitial(() -> new DecimalFormat("#,###", symbols));
    }

    public TypeSize(Tuple3<Double, Double, Double> sizeTuple, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.sizeTuple = sizeTuple;
    }

    @Override
    public String buildHTML() {
        return "<div class='component type_size text_font'>\n" +
                "    <span>Width: " + (sizeTuple.v1 < 100 ? smallFormat.get().format(sizeTuple.v1) : largeFormat.get().format(sizeTuple.v1)) + "m</span>\n" +
                "    <span>Height: " + (sizeTuple.v2 < 100 ? smallFormat.get().format(sizeTuple.v2) : largeFormat.get().format(sizeTuple.v2)) + "m</span>\n" +
                "    <span>Length: " + (sizeTuple.v3 < 100 ? smallFormat.get().format(sizeTuple.v3) : largeFormat.get().format(sizeTuple.v3)) + "m</span>\n" +
                "</div>";
    }

    @Override
    public String buildCSS() {
        return ".type_size {\n" +
                "  padding: 1em;\n" +
                "  display: flex;\n" +
                "}\n" +
                "\n" +
                ".type_size span {\n" +
                "  flex: 1;\n" +
                "  display: flex;\n" +
                "  justify-content: center;\n" +
                "}";
    }
}
