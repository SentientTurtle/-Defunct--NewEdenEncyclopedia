package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.components.Component;
import net.sentientturtle.nee.components.Title;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class SearchResults extends Page {
    SearchResults(DataSupplier dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected void init() {
        headEntries.add("<script src=" + new ResourceLocation("search_index.js", ResourceLocation.Type.INDEX, dataSupplier, this) +"></script>");
        leftComponents.add(new Title("Search results:", null, dataSupplier, this));
        rightComponents.add(new Component(dataSupplier, this) {
            @Override
            protected String buildHTML() {
                return "<div class='component search_results'><table id='search_results_table'></table></div>";
            }

            @Override
            protected String buildCSS() {
                return ".search_results {\n" +
                        "    padding: 0.75em;\n" +
                        "}\n" +
                        "\n" +
                        ".search_results_page {\n" +
                        "    font-size: 1.25em;\n" +
                        "    margin: 5px;\n" +
                        "}\n" +
                        "\n" +
                        ".search_results_td {\n" +
                        "    padding-top: 5px;\n" +
                        "}";
            }
        });
    }

    @Override
    protected String getScript() {
        return "function escapeHtml(unsafe) {\n" +
                "    return unsafe\n" +
                "        .replace(/&/g, \"&amp;\")\n" +
                "        .replace(/</g, \"&lt;\")\n" +
                "        .replace(/>/g, \"&gt;\")\n" +
                "        .replace(/\"/g, \"&quot;\")\n" +
                "        .replace(/'/g, \"&#039;\");\n" +
                "}\n" +
                "\n" +
                "function getURLParameter(name) {\n" +
                "    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\\+/g, '%20')) || null;\n" +
                "}\n" +
                "\n" +
                "var param = getURLParameter('search')\n" +
                "if (param == null) param = \"\";\n" +
                "var query = escapeHtml(param);\n" +
                "\n" +
                "document.getElementsByClassName('title_text')[0].innerHTML = 'Search results: ' + query;\n" +
                "document.getElementById('search_input').value = query\n" +
                "\n" +
                "if (query.length >= 3) {\n" +
                "    var results = [];\n" +
                "    for (var i = 0; i < searchindex.length; i++) {\n" +
                "        if (searchindex[i].name.startsWith(query)) {\n" +
                "            results.push(searchindex[i]);\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "    results.sort(function (a, b) {\n" +
                "        return a.name.length - b.name.length;\n" +
                "    });\n" +
                "    var table = \"\";\n" +
                "    for (var j = 0; j < results.length; j++) {\n" +
                "        table += \"<tr>\";\n" +
                "        table += \"<td class='search_results_td'>\"\n" +
                "        if ('icon' in results[j]) {\n" +
                "            table += \"<img src='\" + results[j].icon + \"' height='64' width='64'>\"\n" +
                "        }\n" +
                "        table += \"</td><td class='search_results_td head_font'><span class='search_results_page'>\" + results[j].path + \"</span></td>\";\n" +
                "        table += \"</tr>\";\n" +
                "    }\n" +
                "    document.getElementById(\"search_results_table\").innerHTML = table;\n" +
                "} else {\n" +
                "    document.getElementById(\"search_results_table\").innerHTML = \"<div class='head_font'>Search query must be at least 3 characters!</div>\";\n" +
                "}\n" +
                "\n";
    }

    @Override
    public PageType getPageType() {
        return PageType.STATIC;
    }

    @Override
    public String getPageName() {
        return "SearchResults";
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return null;
    }
}
