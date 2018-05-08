package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

/**
 * Displays page title, with an optional icon
 */
public class Title extends Component {
    @Language("HTML")
    private final String text;
    private final ResourceLocation icon;

    public Title(@Language("HTML") String text, @Nullable ResourceLocation icon, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.text = text;
        this.icon = icon;
    }


    @Override
    public String buildHTML() {
        if (icon != null) {
            return "<div class='component title'><img src='" + icon + "' height='64' width='64'><b class='head_font title_text'>" + text + "</b></div>";
        } else {
            return "<div class='component title'><b class='head_font title_text'>" + text + "</b></div>";
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
                ".title_text {\n" +
                "    font-size: 1.5em;\n" +
                "    margin-left: 10px;\n" +
                "}\n" +
                "\n" +
                "@media screen and (min-width: 1100px) {\n" +
                "    .title_text {\n" +
                "        font-size: 2em;\n" +
                "        margin-left: 10px;\n" +
                "    }\n" +
                "}";
    }
}
