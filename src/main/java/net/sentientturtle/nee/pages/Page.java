package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.components.Component;
import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.util.ResourceSupplier;
import net.sentientturtle.util.tuple.Tuple2;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Abstract class for Pages
 * Pages represent a full HTML document (i.e. A website page)
 */
@SuppressWarnings("WeakerAccess")
public abstract class Page {
    private static final int MERGE_THRESHOLD = 3;     // Treshold for merging both columns of a page, if either of the columns on the page is smaller than the threshold, they will be merged. Currently unused.
    protected final ArrayList<Component> leftComponents;
    protected final ArrayList<Component> rightComponents;
    protected final ArrayList<String> headEntries;
    private HashMap<String, ResourceSupplier> fileDependencies;
    private boolean isInitialized;

    @Language("HTML")
    private String htmlCache;
    @Language("CSS")
    private String cssCache;

    protected DataSupplier dataSupplier;

    protected Page(DataSupplier dataSupplier) {
        this.dataSupplier = dataSupplier;
        leftComponents = new ArrayList<>();
        rightComponents = new ArrayList<>();
        headEntries = new ArrayList<>();
        fileDependencies = new HashMap<>();
        isInitialized = false;
    }

    /**
     * Initializes this page; used to lazy initialization of this page object
     */
    protected abstract void init();

    /**
     * @return The {@link PageType} of this page
     */
    public abstract PageType getPageType();

    /**
     * @return The name of this page
     */
    public abstract String getPageName();

    /**
     * @return The icon for this page
     */
    public abstract @Nullable ResourceLocation getIcon();

    /**
     * @return The script for this page, defaults to empty string.
     */
    @Language("JS")
    protected String getScript() {
        return "";
    }

    /**
     * @return True if this page should be a full-width single column
     */
    protected boolean isMonoColumn() {
        return false;
    }


    /**
     * Adds a resource dependency
     * @param path Path of the resource
     * @param callable Callable that returns a byte-array of the resource
     */
    public void putFileDependency(String path, ResourceSupplier callable) {
        fileDependencies.put(path, callable);
    }

    /**
     * @return The map of file dependencies for this page; A map of paths to callables for their data
     */
    public HashMap<String, ResourceSupplier> getFileDependencies() {
        getHTML();  // Ensure all resource locations have been parsed
        return fileDependencies;
    }

    private String getContent() {
        int leftSize = leftComponents.size();
        int rightSize = rightComponents.size();

        if (isMonoColumn() || (rightSize <= MERGE_THRESHOLD && leftSize <= MERGE_THRESHOLD)) {
            leftComponents.addAll(rightComponents);
            rightComponents.clear();
            leftSize = leftComponents.size();
            rightSize = rightComponents.size();
        }

        StringBuilder contentBuilder = new StringBuilder();
        if (leftSize > 0) {
            //language=HTML
            if (isMonoColumn()) {
                contentBuilder.append("<div class='column_full_width'>");
            } else {
                contentBuilder.append("<div class='column'>");
            }
            leftComponents.stream().map(Component::getHTML).forEach(contentBuilder::append);
            contentBuilder.append("</div>");
        }
        if (rightSize > 0) {
            assert !isMonoColumn();
            //language=HTML
            contentBuilder.append("<div class='column'>");
            rightComponents.stream().map(Component::getHTML).forEach(contentBuilder::append);
            contentBuilder.append("</div>");
        }
        return contentBuilder.toString();
    }

    @Language("HTML")
    private String getHead() {
        StringBuilder head = new StringBuilder();
        head.append("<head>\n")
                .append("<meta charset='UTF-8' name='viewport' content='width=device-width'>\n");
        if (ResourceLocation.FORMAT == ResourceLocation.Format.DATA) {  // :MAGIC:
            try {
                Tuple2<Object, ResourceLocation.OriginType> resetOrigin = new ResourceLocation("reset.css", ResourceLocation.Type.FILE, dataSupplier, this).getOrigin();
                Tuple2<Object, ResourceLocation.OriginType> styleOrigin = new ResourceLocation("stylesheet_main.css", ResourceLocation.Type.FILE, dataSupplier, this).getOrigin();
                head.append("<style>")
                        .append(new String(resetOrigin.v2.getData(resetOrigin.v1, dataSupplier), StandardCharsets.UTF_8)
                                .replaceAll("\\s+(\\{)|(:)\\s+|;\\n\\s*(})|\\n\\s*|/\\*.*?\\*/|(,)\\s+", "$1$2$3$4"))
                        .append("</style>");
                head.append("<style>")
                        .append(new String(styleOrigin.v2.getData(styleOrigin.v1, dataSupplier), StandardCharsets.UTF_8)
                                .replaceAll("\\s+(\\{)|(:)\\s+|;\\n\\s*(})|\\n\\s*|/\\*.*?\\*/|(,)\\s+", "$1$2$3$4"))
                        .append("</style>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            head.append("    <link rel='stylesheet' type='text/css' href='").append(new ResourceLocation("reset.css", ResourceLocation.Type.FILE, dataSupplier, this)).append("'>\n");
            head.append("    <link rel='stylesheet' type='text/css' href='").append(new ResourceLocation("stylesheet_main.css", ResourceLocation.Type.FILE, dataSupplier, this)).append("'>\n");
        }
        head.append("    <style>").append(getCSS()).append("</style>\n");
        head.append("    <link rel='shortcut icon' type='image/png' href='").append(new ResourceLocation("bookicon.png", ResourceLocation.Type.FILE, dataSupplier, this)).append("'>");

        for (String headEntry : headEntries) {
            head.append(headEntry).append('\n');
        }

        head.append("</head>\n");
        return head.toString();
    }

    @Language("HTML")
    private String getHeader() {
        return  "<div id='header'>\n" +
                "        <span class='header_span'>\n" +
                "            <img height='64' width='64' src='" + new ResourceLocation("bookicon.png", ResourceLocation.Type.FILE, dataSupplier, this) + "'>\n" +
                "            <b id='header_text' class='head_font'>" + new PageReference("index", PageType.STATIC, "New Eden Encyclopedia", this.getPageType().getFolderDepth()) + "</b>\n" +
                "        </span>\n" +
                "    <span class='header_span header_search'>" +
                "       <form class='head_font' action='" + (this.getPageType() == PageType.STATIC ? "" : "../") + PageType.STATIC.getPageFilePath("SearchResults") + "'>Search: <input class='head_font' id='search_input' type='text' placeholder='Search...' name='search'></form>" +
                "    </span>\n" +
                "</div>";
    }

    @Language("HTML")
    private static String getFooter() {
        return "<div id='footer'>\n" +
                "    <div class='footer_text text_font'>\n" +
                "        EVE Online and the EVE logo are the registered trademarks of CCP hf.\n" +
                "        All rights are reserved worldwide.\n" +
                "        All other trademarks are the property of their respective owners.\n" +
                "        EVE Online, the EVE logo, EVE and all associated logos and designs are the intellectual property of CCP hf.\n" +
                "        All artwork, screenshots, characters, vehicles, storylines, world facts or other recognizable features of the intellectual property relating\n" +
                "        to these trademarks are likewise the intellectual property of CCP hf.\n" +
                "        CCP hf. has granted permission to New Eden Encyclopedia to use EVE Online and all associated logos and designs for promotional and\n" +
                "        information purposes on its website but does not endorse, and is not in any way affiliated with, New Eden Encyclopedia.\n" +
                "        CCP is in no way responsible for the content on or functioning of this website, nor can it be liable for any damage arising from the use of\n" +
                "        this website.\n" +
                "    </div>\n" +
                "</div>";
    }

    // HTML Boilerblate
    @Language("HTML")
    public String getHTML() {
        if (!isInitialized) {
            init();
            isInitialized = true;
        }
        return htmlCache != null ? htmlCache : (htmlCache = "<!DOCTYPE html>\n" +
                "<html>\n" +
                getHead() +
                "<title>New Eden Encyclopedia</title>\n" +
                "<body>\n" +
                "<div class='container'>" +
                getHeader() +
                "<div class='center'>" +
                getContent() +
                "</div>\n" +
                getFooter() +
                "</div>\n" +
                "<script>" + getScript() + "</script>" +
                "</body>\n" +
                "</html>");
    }

    @Language("CSS")
    public String getCSS() {
        if (!isInitialized) {
            init();
            isInitialized = true;
        }
        if (cssCache != null) {
            return cssCache;
        } else {
            StringBuilder cssBuilder = new StringBuilder();
            Stream.concat(leftComponents.stream(), rightComponents.stream()).map(Component::getCSS).distinct().forEach(cssBuilder::append);
            return cssCache = cssBuilder.toString();
        }
    }

    /**
     * @return Returns hashcode of the HTML part of this page
     */
    public int hashDoc() {
        return getHTML().hashCode();
    }

    @Override
    public String toString() {
        return "Page{" +
                "name=" + getPageName() +
                ", type=" + getPageType() +
                '}';
    }
}
