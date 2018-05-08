package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.Page;
import net.sentientturtle.util.tuple.Tuple2;

import java.util.Map;

/**
 * Displays required skills to use a {@link Type}
 */
public class TypeSkills extends Component {
    private Type type;
    private static final int[] skillAttributes = {182, 183, 184, 1285, 1289, 1290};
    private static final int[] levelAttributes = {277, 278, 279, 1286, 1287, 1288};

    public TypeSkills(Type type, DataSupplier dataSupplier, Page page) {
        super(dataSupplier, page);
        this.type = type;
    }

    @Override
    protected String buildHTML() {
        StringBuilder html = new StringBuilder();
        html.append("<div class='component type_skills head_font'>" +
                "<div class='type_skill_title'>Required skills</div>" +
                "<div class='type_skill_boxes'>");
        fetchSkills(html, type.typeID, dataSupplier);
        html.append("</div></div>");
        return html.toString();
    }

    private void fetchSkills(StringBuilder html, int typeID, DataSupplier dataSupplier) {
        Map<Tuple2<Integer, Integer>, Double> attributeValues = dataSupplier.getAttributeValues();
        html.append("<div class='type_skill_box'>");
        for (int i = 0; i < skillAttributes.length; i++) {
            Double skill = attributeValues.get(new Tuple2<>(typeID, skillAttributes[i]));
            if (skill != null) {
                int level = attributeValues.get(new Tuple2<>(typeID, levelAttributes[i])).intValue();
                String levelString;
                switch (level) {
                    case 1:
                        levelString = " I";
                        break;
                    case 2:
                        levelString = " II";
                        break;
                    case 3:
                        levelString = " III";
                        break;
                    case 4:
                        levelString = " IV";
                        break;
                    case 5:
                        levelString = " V";
                        break;
                    default:
                        levelString = "";
                        break;
                }
                html.append(dataSupplier.unitify(skill, 116, page)).append(levelString); // 116 = typeID unit
                if (attributeValues.containsKey(new Tuple2<>(skill.intValue(), 182))) {
                    fetchSkills(html, skill.intValue(), dataSupplier);
                } else {
                    html.append("<br>");
                }
            }
        }
        html.append("</div>");
    }

    @Override
    protected String buildCSS() {
        return ".type_skills {\n" +
                "    padding: 0.75em;\n" +
                "}\n" +
                "\n" +
                ".type_skill_title {\n" +
                "    font-size: 1.75em;\n" +
                "    margin-bottom: 5px;\n" +
                "}\n" +
                "\n" +
                ".type_skill_boxes {\n" +
                "    margin-left: -10px;\n" +
                "    font-size: 1.25em;\n" +
                "}\n" +
                "\n" +
                ".type_skill_box {\n" +
                "    margin-left: 10px;\n" +
                "    padding-left: 5px;\n" +
                "    border: 1px none #525252;\n" +
                "    border-left-style: solid;\n" +
                "}\n" +
                "\n";
    }
}
