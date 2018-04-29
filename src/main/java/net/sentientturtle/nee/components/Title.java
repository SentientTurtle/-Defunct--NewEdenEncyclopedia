package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import org.jetbrains.annotations.Nullable;

/**
 * Displays page title, with an optional icon
 */
public class Title extends Component {
    private final String text;
    private final ResourceLocation icon;

    public Title(String text, @Nullable ResourceLocation icon) {
        this.text = text;
        this.icon = icon;
    }


    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        if (icon != null) {
            return "<div class='component title'><img src='" + icon + "' height='64' width='64'><b class='head_font titleText'>" + text + "</b></div>";
        } else {
            return "<div class='component title'><b class='head_font titleText'>" + text + "</b></div>";
        }
    }


    @Override
    public String buildCSS() {
        return ".title {\n" +
                "    height: 64px;\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "}\n" +
                "\n" +
                "\n" +
                ".titleText {\n" +
                "    font-size: 1.5em;\n" +
                "    margin-left: 10px;\n" +
                "}\n" +
                "\n" +
                "@media screen and (min-width: 1100px) {\n" +
                "    .titleText {\n" +
                "        font-size: 2em;\n" +
                "        margin-left: 10px;\n" +
                "    }\n" +
                "}";
    }
}
