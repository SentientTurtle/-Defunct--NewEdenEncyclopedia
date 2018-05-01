package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import org.intellij.lang.annotations.Language;

/**
 * Page component abstract class, builds HTML & matching CSS snippets.
 * Components are appended in {@link net.sentientturtle.nee.pages.Page} objects
 */
public abstract class Component {
    @Language("HTML")
    private String html;
    @Language("CSS")
    private String css;

    /**
     * Builds HTML snippet using a given data supplier
     * @param dataSupplier Data supplier to use
     * @return String containing the HTML for this component
     */
    @Language("HTML")
    protected abstract String buildHTML(DataSupplier dataSupplier);

    /**
     * Builds CSS snippet
     * @return String containing CSS snippet for this component
     */
    @Language("CSS")
    protected abstract String buildCSS();

    /**
     * Cached getter for {@link Component#buildHTML(DataSupplier)}
     */
    @Language("HTML")
    public String getHTML(DataSupplier dataSupplier) {
        return html != null ? html : (html = buildHTML(dataSupplier));  // Cache HTML
    }

    /**
     * Cached getter for {@link Component#buildCSS()}
     */
    @Language("CSS")
    public String getCSS(DataSupplier dataSupplier) {
        return getHTML(dataSupplier).length() > 0 ?
                css != null ?
                        css :
                        (css = buildCSS().replaceAll("\\s+(\\{)|(:)\\s+|;\\n\\s*(})|\\n\\s*|/\\*.*?\\*/|(,)\\s+","$1$2$3$4"))
                : "";    // Return blank CSS if HTML is blank
    }

    /**
     * Immediately throws RunTimeException
     * Exception thrown to provide a defined behaviour failure for older code that is not forwards-compatible.
     * @throws RuntimeException Immediately thrown.
     */
    public String toString() throws RuntimeException {
        throw new RuntimeException("Components should not be appended through use of the toString method!");
    }
}
