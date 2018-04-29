package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.PageType;
import org.intellij.lang.annotations.Language;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Displays description of a {@link Type}
 */
public class TypeDescription extends Component {
    private static Pattern linkHref = Pattern.compile("<a href=\"(.+?)\">(.+?)</a>");
    private static Pattern showInfoHref = Pattern.compile("<a href=showinfo:(\\d+?)>");
    private final Type type;

    public TypeDescription(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {   // Called lazily, hence the large amount of computation in this method
        @SuppressWarnings("LanguageMismatch")
        @Language("HTML")
        String descriptionText = type.description;
        Matcher matcher = linkHref.matcher(descriptionText);
        while (matcher.find()) {
            String url = matcher.group(1);
            String typeName = matcher.group(2);
            descriptionText = descriptionText.replace("<a href='" + url + "'>" + typeName + "</a>", typeName);
        }
        matcher = showInfoHref.matcher(descriptionText);
        while (matcher.find()) {
            String typeID = matcher.group(1);
            descriptionText = descriptionText.replace("<a href=showinfo:" + typeID + ">", new PageReference(dataSupplier.getTypes().get(Integer.valueOf(typeID)).name, PageType.TYPE).toString());
        }
        descriptionText = descriptionText
                .replaceAll("<color='.*'>", "").replaceAll("<font .*?=\".*\">", "").replaceAll("</color>", "").replaceAll("</font>", "")  // Clear markup
                .replaceAll("<a href=showinfo:\\d+?//\\d+?>", "");  // Clear links with itemIDs

        return "<div class='component item_description'><header class='head_font description_header'>Description: </header>" +
                "<span class='html_text description_text text_font'>" + descriptionText + "</span></div>";
    }

    @Override
    public String buildCSS() {
        return ".item_description {\n" +
                "  padding: 1em;\n" +
                "}\n" +
                "\n" +
                ".description_header {\n" +
                "  font-size: 1.5em;\n" +
                "}\n" +
                "\n" +
                ".description_text {\n" +
                "  font-style: italic;\n" +
                "  font-size: 0.9em;\n" +
                "}";
    }
}
