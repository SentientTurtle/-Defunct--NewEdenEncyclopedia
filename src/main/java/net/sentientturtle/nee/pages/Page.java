package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.components.Component;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.util.Tuple2;
import org.intellij.lang.annotations.Language;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Abstract class for Pages
 * Pages represent a full HTML document (i.e. A website page)
 */
public abstract class Page {
//    private static final int MERGE_THRESHOLD = 3;     // Treshold for merging both columns of a page, if either of the columns on the page is smaller than the threshold, they will be merged. Currently unused.
    protected ArrayList<Component> leftComponents;
    protected ArrayList<Component> rightComponents;

    @Language("HTML")
    private String htmlCache;
    @Language("CSS")
    private String cssCache;

    private DataSupplier dataSupplier;

    protected Page(DataSupplier dataSupplier) {
        this.dataSupplier = dataSupplier;
        leftComponents = new ArrayList<>();
        rightComponents = new ArrayList<>();
    }

    /**
     * @return The {@link PageType} of this page
     */
    public abstract PageType getPageType();

    /**
     * @return The name of this page
     */
    public abstract String getPageName();

    private String getContent() {
        int leftSize = leftComponents.size();
        int rightSize = rightComponents.size();

//        if ((rightSize > 0 && rightSize <= MERGE_THRESHOLD) || (leftSize > 0 && leftSize <= MERGE_THRESHOLD)) {
//            leftComponents.addAll(rightComponents);
//            rightComponents.clear();
//            leftSize = leftComponents.size();
//            rightSize = rightComponents.size();
//        }

        StringBuilder contentBuilder = new StringBuilder();
        if (leftSize > 0) {
            //language=HTML
            contentBuilder.append(rightSize > 0 ? "<div class='column'>" : "<div class='column_full_width'>");
            leftComponents.stream().map(component -> component.getHTML(dataSupplier)).forEach(contentBuilder::append);
            contentBuilder.append("</div>");
        }
        if (rightSize > 0) {
            //language=HTML
            contentBuilder.append(leftSize > 0 ? "<div class='column'>" : "<div class='column_full_width'>");
            rightComponents.stream().map(component -> component.getHTML(dataSupplier)).forEach(contentBuilder::append);
            contentBuilder.append("</div>");
        }
        return contentBuilder.toString();
    }

    @Language("HTML")
    private String getHead() {
        StringBuilder head = new StringBuilder();
        head.append("<head>\n")
                .append("    <meta charset='UTF-8' name='viewport' content='width=device-width'>\n");
        if (ResourceLocation.format == ResourceLocation.Format.DATA) {  // :MAGIC:
            try {
                Tuple2<Object, ResourceLocation.OriginType> resetOrigin = new ResourceLocation("reset.css", ResourceLocation.Type.FILE, dataSupplier).getOrigin();
                Tuple2<Object, ResourceLocation.OriginType> styleOrigin = new ResourceLocation("stylesheet_main.css", ResourceLocation.Type.FILE, dataSupplier).getOrigin();
                head.append("<style>").append(new String(resetOrigin.v2.getData(resetOrigin.v1, dataSupplier), StandardCharsets.UTF_8)).append("</style>");
                head.append("<style>").append(new String(styleOrigin.v2.getData(styleOrigin.v1, dataSupplier), StandardCharsets.UTF_8)).append("</style>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            head.append("    <link rel='stylesheet' type='text/css' href='").append(new ResourceLocation("reset.css", ResourceLocation.Type.FILE, dataSupplier)).append("'>\n");
            head.append("    <link rel='stylesheet' type='text/css' href='").append(new ResourceLocation("stylesheet_main.css", ResourceLocation.Type.FILE, dataSupplier)).append("'>\n");
        }
        head.append("    <style>").append(getCSS()).append("</style>\n")
                .append("</head>\n");
        return head.toString();
    }

    @Language("HTML")
    private String getHeader() {
        return "<div id='header'>\n" +
                "    <img height='64' width='64' src='" + new ResourceLocation("bookicon.png", ResourceLocation.Type.FILE, dataSupplier) + "'>\n" + // TODO: Replace with resource reference
                "    <b id='header_text' class='head_font'>New Eden Encyclopedia</b>\n" +
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
                "</body>\n" +
                "</html>");
    }

    @Language("CSS")
    public String getCSS() {    // TODO: Minify
        if (cssCache != null) {
            return cssCache;
        } else {
            StringBuilder cssBuilder = new StringBuilder();
            Stream.concat(leftComponents.stream(), rightComponents.stream()).map(component -> component.getCSS(dataSupplier)).distinct().forEach(cssBuilder::append);
            return cssCache = cssBuilder.toString();
        }
    }

    /**
     * @return Returns hashcode of the HTML part of this page
     */
    public int hashDoc() {
        return getHTML().hashCode();
    }
}
