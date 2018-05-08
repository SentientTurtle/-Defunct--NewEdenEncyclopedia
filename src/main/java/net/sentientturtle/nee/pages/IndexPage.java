package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.components.Component;
import net.sentientturtle.nee.components.PageList;
import net.sentientturtle.nee.components.Title;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Region;
import net.sentientturtle.nee.orm.singleton.Cluster;
import net.sentientturtle.nee.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class IndexPage extends Page {
    IndexPage(DataSupplier dataSupplier) {
        super(dataSupplier);
    }

    @Override
    protected void init() {
        leftComponents.add(new Title("Welcome", null, dataSupplier, this));
        leftComponents.add(new Component(dataSupplier, this) {
            private String aboutText = "The New Eden Encyclopedia is an automatically updated encyclopedia that provides access to information about the videogame EVE Online. " +
                    "The project is currently still a work-in-progress, and information may be outdated, invalid, or completely absent. " +
                    "<br>Issues can be reported on the project's <a href='https://github.com/SentientTurtle/NewEdenEncyclopedia'>Github repository.</a>" +
                    "<br><br><i>The New Eden Encyclopedia project is not affiliated with CCP hf.</i>";

            @Override
            protected String buildHTML() {
                return "<div class='component index_about'><header class='head_font about_header'>About: </header>" +
                        "<span class='html_text about_text text_font'>" + aboutText + "</span></div>";
            }

            @Override
            protected String buildCSS() {
                return ".index_about {\n" +
                        "  padding: 1em;\n" +
                        "}\n" +
                        "\n" +
                        ".about_header {\n" +
                        "  font-size: 1.5em;\n" +
                        "}\n" +
                        "\n" +
                        ".about_text {\n" +
                        "  font-style: italic;\n" +
                        "  font-size: 0.9em;\n" +
                        "}";
            }
        });
        leftComponents.add(new PageList(PageType.MAP.streamSupplier.apply(dataSupplier)
                .filter(page -> ((MapPage) page).mappable instanceof Cluster)
                .toArray(Page[]::new),
                "Clusters",
                dataSupplier,
                this
        ));

        leftComponents.add(new PageList(PageType.MAP.streamSupplier.apply(dataSupplier)
                .filter(page -> ((MapPage) page).mappable instanceof Region && ((Region) ((MapPage) page).mappable).regionID < 11000000)
                .sorted((o1, o2) -> ((Region) ((MapPage) o1).mappable).regionName.compareToIgnoreCase(((Region) ((MapPage) o2).mappable).regionName))
                .toArray(Page[]::new),
                "Regions",
                dataSupplier,
                this
        ));

        rightComponents.add(new PageList(PageType.TYPE.streamSupplier.apply(dataSupplier)
                .filter(page -> ((TypePage) page).type.name.equals("Keepstar"))
                .toArray(Page[]::new),
                "Featured page",
                dataSupplier,
                this
        ));

        rightComponents.add(new PageList(PageType.CATEGORY.streamSupplier.apply(dataSupplier)
                .sorted(Comparator.comparingInt(o -> ((CategoryPage) o).category.categoryID))
                .toArray(Page[]::new),
                "Categories",
                dataSupplier,
                this
        ));
    }

    @Override
    public PageType getPageType() {
        return PageType.STATIC;
    }

    @Override
    public String getPageName() {
        return "index";
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation("bookicon.png", ResourceLocation.Type.FILE, dataSupplier, this);
    }
}
