package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Category;
import net.sentientturtle.nee.orm.Group;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.PageType;

/**
 * Displays {@link Group} and {@link Category} of a {@link Type}
 */
public class TypeGroup extends Component {
    private Type type;

    public TypeGroup(Type type) {
        this.type = type;
    }

    @SuppressWarnings("LanguageMismatch")
    @Override
    public String buildHTML(DataSupplier dataSupplier) {
        Group group = dataSupplier.getGroups().get(type.groupID);
        Category category;
        if (group != null) {
            category = dataSupplier.getCategories().get(group.categoryID);
            if (category == null) {
                throw new RuntimeException("Group has no associated category, this should not happen! " + group);
            }
        } else {
            throw new RuntimeException("Type has no associated group, this should not happen! " + type);
        }
        return "<div class='component type_group head_font'><span class='type_group_span'>" +
                "<img src='"
                + (group.iconID != null ? ResourceLocation.iconOfIconID(group.iconID, dataSupplier) : new ResourceLocation("7_64_15.png", ResourceLocation.Type.ITEM_ICON, dataSupplier))
                + "' height='48' width='48'>" +
                "<b class='head_text type_group_text'>" + new PageReference(group.name, PageType.GROUP) + "</b>" +
                "<span class='type_group_separator'>|</span>" +
                "<img src='"
                + (category.iconID != null ? ResourceLocation.iconOfIconID(category.iconID, dataSupplier) : new ResourceLocation("7_64_15.png", ResourceLocation.Type.ITEM_ICON, dataSupplier))
                + "' height='48' width='48'>" +
                "<b class='head_text type_category_text'>" + new PageReference(category.name, PageType.CATEGORY) + "</b>" +
                "</span></div>";
    }

    @Override
    public String buildCSS() {
        return ".type_group, .type_group_span {\n" +
                "  height: 48px;\n" +
                "  display: flex;\n" +
                "  align-items: center;\n" +
                "}\n" +
                "\n" +
                ".type_group_span {\n" +
                "  margin-left: 1em;\n" +
                "}\n" +
                "\n" +
                ".type_group_text, .type_category_text {\n" +
                "  font-size: 1.5em;\n" +
                "  margin-left: 10px;\n" +
                "}\n" +
                "\n" +
                ".type_group_separator {\n" +
                "  font-size: 2em;\n" +
                "  width: 1.5em;\n" +
                "  text-align: center;\n" +
                "}";
    }
}
