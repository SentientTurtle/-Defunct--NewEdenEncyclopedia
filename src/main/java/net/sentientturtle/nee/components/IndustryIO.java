package net.sentientturtle.nee.components;

import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.IndustryActivity;
import net.sentientturtle.nee.orm.IndustryActivityType;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.PageType;

import java.util.Comparator;
import java.util.Set;

/**
 * Lists all industry materials, products, and skills for a given blueprint {@link Type}
 */
public class IndustryIO extends Component {
    private Type type;

    public IndustryIO(Type type, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.type = type;
    }

    @Override
    protected String buildHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<div class='component industry_io'>");
        Set<IndustryActivity> industryActivities = dataSupplier.getBpActivityMap().get(type.typeID);
        industryActivities.stream().sorted(Comparator.comparingInt(o -> o.activityID)).forEach(industryActivity -> {
            IndustryActivityType activityType = dataSupplier.getIndustryActivityTypes().get(industryActivity.activityID);
            html.append("<div class='io_wrapper'>")
                    .append("<div class='head_font io_title'>")
                    .append(activityType.activityName)
                    .append("</div>");
            if (industryActivity.materialMap.size() > 0) {
                html.append("<span class='head_font io_type'>Materials</span>");
                html.append("<div>");
                industryActivity.materialMap.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())) // Sort in order of decreasing quantity
                        .forEach(entry -> html.append("<div class='io_item text_font '>")
                                .append("<img src='").append(ResourceLocation.iconOfTypeID(entry.getKey(), dataSupplier, page)).append("' height='48' width='48'>")
                                .append("<span class='io_item_text io_item_type'><b>")
                                .append(new PageReference(dataSupplier.getTypes().get(entry.getKey()).name, PageType.TYPE, page.getPageType().getFolderDepth()))
                                .append("</b></span>")
                                .append("<span class='io_item_text'><b>")
                                .append(dataSupplier.unitify(entry.getValue(), -1, page)) // Unitify without unit
                                .append("</b>")
                                .append("</div>")
                        );
                html.append("</div>");
            }
            if (industryActivity.productMap.size() > 0) {
                html.append("<span class='head_font io_type'>Products</span>");
                html.append("<div>");
                industryActivity.productMap.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())) // Sort in order of decreasing quantity
                        .forEach(entry -> html.append("<div class='io_item text_font '>")
                                .append("<img src='").append(ResourceLocation.iconOfTypeID(entry.getKey(), dataSupplier, page)).append("' height='48' width='48'>")
                                .append("<span class='io_item_text io_item_type'><b>")
                                .append(new PageReference(dataSupplier.getTypes().get(entry.getKey()).name, PageType.TYPE, page.getPageType().getFolderDepth()))
                                .append("</b></span>")
                                .append("<span class='io_item_text'><b>")
                                .append(dataSupplier.unitify(entry.getValue(), -1, page)) // Unitify without unit
                                .append("</b>")
                                .append("</div>")
                        );
                html.append("</div>");
            }
            if (industryActivity.skillMap.size() > 0) {
                html.append("<span class='head_font io_type'>Skills required</span>");
                html.append("<div>");
                industryActivity.skillMap.entrySet().stream()
                        .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue())) // Sort in order of decreasing level
                        .forEach(entry -> html.append("<div class='io_item text_font '>")
                                .append("<img src='").append(ResourceLocation.iconOfTypeID(entry.getKey(), dataSupplier, page)).append("' height='32' width='32'>")
                                .append("<span class='io_item_text io_item_type'><b>")
                                .append(new PageReference(dataSupplier.getTypes().get(entry.getKey()).name, PageType.TYPE, page.getPageType().getFolderDepth()))
                                .append("</b></span>")
                                .append("<span class='io_item_text'><b>")
                                .append(dataSupplier.unitify(entry.getValue(), -1, page)) // Unitify without unit
                                .append("</b>")
                                .append("</div>")
                        );
                html.append("</div>");
            }
            html.append("</div>");
        });
        html.append("</div>");
        return html.toString();
    }

    @Override
    protected String buildCSS() {
        return ".industry_io {\n" +
                "    padding: 1em;\n" +
                "}\n" +
                "\n" +
                ".io_wrapper {\n" +
                "    margin-bottom: 1em;\n" +
                "}\n" +
                "\n" +
                ".io_wrapper:last-child {\n" +
                "    margin-bottom: 0;\n" +
                "}\n" +
                "\n" +
                ".io_title {\n" +
                "    font-size: 1.5em;\n" +
                "}\n" +
                "\n" +
                ".io_type {\n" +
                "    font-size: 1.25em;\n" +
                "}\n" +
                "\n" +
                ".io_item {\n" +
                "    width: 100%;\n" +
                "    font-size: 1.25em;\n" +
                "    display: flex;\n" +
                "    align-items: center;\n" +
                "}\n" +
                "\n" +
                ".io_item_text {\n" +
                "    margin: 5px;\n" +
                "}\n" +
                "\n" +
                ".io_item_type {\n" +
                "    flex-grow: 2;\n" +
                "}";
    }
}
