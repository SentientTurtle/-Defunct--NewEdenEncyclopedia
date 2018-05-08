package net.sentientturtle.nee.util;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.pages.PageType;
import org.intellij.lang.annotations.Language;

/**
 * Object for links to other pages, to allow for future debugging.
 */
public class PageReference {
    private final String pageName;
    private final PageType type;
    private final String text;
    private final int folderDepth;

    public PageReference(String pageName, PageType type, int folderDepth) {
        this(pageName, type, null, folderDepth);
    }

    public PageReference(String pageName, PageType type, String text, int folderDepth) {
        this.pageName = pageName;
        this.type = type;
        this.text = text;
        this.folderDepth = folderDepth;
    }

    public PageReference(Page destinationPage, int folderDepth) {
        this(destinationPage.getPageName(), destinationPage.getPageType(), folderDepth);
    }

    @SuppressWarnings("WeakerAccess")
    public static String cancelDepth(int folderDepth) {
        String depth = "";
        for (int i = 0; i < folderDepth; i++) {
            //noinspection StringConcatenationInLoop    Will be called 0 or 1 times the majority of the time, making Stringbuilders inefficient
            depth += "../";
        }
        return depth;
    }

    @Language("HTML")
    public String toString() {
        //noinspection LanguageMismatch
        return "<a href='" + cancelDepth(folderDepth) + type.getPageFilePath(pageName) + "'>" + (text == null ? pageName : text) + "</a>";
    }
}
