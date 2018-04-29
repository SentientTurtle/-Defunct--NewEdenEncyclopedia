package net.sentientturtle.nee.util;

import net.sentientturtle.nee.pages.PageType;
import org.intellij.lang.annotations.Language;

/**
 * Object for links to other pages, to allow for future debugging.
 */
public class PageReference {
    private final String pageName;
    private final PageType type;
    private final String text;

    public PageReference(String pageName, PageType type) {
        this(pageName, type, null);
    }

    public PageReference(String pageName, PageType type, String text) {
        this.pageName = pageName;
        this.type = type;
        this.text = text;
    }

    @Language("HTML")
    public String toString() {
        //noinspection LanguageMismatch
        return "<a href='../" + type + "/" + pageName.replaceAll("[\\\\/:*?\"<>|\t]", "") + ".html'>" + (text == null ? pageName : text) + "</a>";
    }
}
