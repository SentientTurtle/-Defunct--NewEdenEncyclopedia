package net.sentientturtle.nee.components;

import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.nee.util.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Displays the traits of a {@link Type}, usually a Ship.
 */
public class TypeTraits extends Component {
    private static Pattern showInfoHref = Pattern.compile("<a href=showinfo:(\\d+?)>(.+?)</a>");
    private final Type type;

    public TypeTraits(Type type) {
        this.type = type;
    }

    @Override
    public String buildHTML(DataSupplier dataSupplier) {   // Called lazily, hence the large amount of computation in this method
        boolean first = true;
        //language=HTML
        StringBuilder traitString = new StringBuilder("<div class='component type_traits text_font'><table class='trait_table'>");
        Map<Integer, List<Tuple3<Double, String, Integer>>> traitMap = dataSupplier.getTypeTraits().get(type.typeID);
        ArrayList<Integer> keyList = new ArrayList<>(traitMap.keySet());
        keyList.sort(Integer::compareUnsigned);
        for (Integer skillID : keyList) {
            if (first) {
                first = false;
            } else {
                traitString.append("<tr><td class='trait_blank' colspan='2'></td></tr>");
            }
            if (skillID == -1) {
                traitString.append("<tr class='head_font'><th class='trait_table_head' colspan='2'>Role Bonus</th></tr>");
            } else if (skillID == -2) {
                traitString.append("<tr class='head_font'><th class='trait_table_head' colspan='2'>Misc Bonus</th></tr>");
            } else {
                traitString.append("<tr class='head_font'><th class='trait_table_head' colspan='2'>").append(new PageReference(dataSupplier.getTypes().get(skillID).name, PageType.TYPE)).append(" bonuses (per skill level)</th></tr>");
            }

            if (skillID == -2 && type.groupID == 1305) {
                List<Tuple3<Double, String, Integer>> traits = traitMap.get(skillID);
                List<Tuple3<Double, String, Integer>> modeTraits = traits.subList(4, traits.size());
                modeTraits.stream()
                        .filter(t -> !(t.v2.contains("Defense Mode") || t.v2.contains("Propulsion Mode") || t.v2.contains("Sharpshooter Mode")))
                        .forEach(trait -> appendTrait(traitString, trait, dataSupplier));

                appendTrait(traitString, traits.get(0), dataSupplier);
                for (int i = 1; i < 4; i++) {
                    Tuple3<Double, String, Integer> mode = traits.get(i);
                    appendTrait(traitString, mode, dataSupplier);
                    String modeString;
                    if (mode.v2.contains("Defense")) {
                        modeString = "Defense Mode";
                    } else if (mode.v2.contains("Propulsion")) {
                        modeString = "Propulsion Mode";
                    } else if (mode.v2.contains("Sharpshooter")) {
                        modeString = "Sharpshooter Mode";
                    } else {
                        throw new RuntimeException("Unknown T3D Mode!");
                    }
                    modeTraits.stream()
                            .filter(trait -> trait.v2.contains(modeString))
                            .forEach(trait -> appendTrait(traitString, trait, dataSupplier));
                }
            } else {
                for (Tuple3<Double, String, Integer> traitTuple : traitMap.get(skillID)) {
                    appendTrait(traitString, traitTuple, dataSupplier);
                }
            }
        }
        traitString.append("</table></div>");
        return traitString.toString();
    }

    private void appendTrait(StringBuilder traitString, Tuple3<Double, String, Integer> traitTuple, DataSupplier dataSupplier) {
        String bonusText = traitTuple.v2;
        Matcher matcher = showInfoHref.matcher(bonusText);
        while (matcher.find()) {
            String typeID = matcher.group(1);
            bonusText = bonusText.replace("<a href=showinfo:" + typeID + ">" + matcher.group(2) + "</a>", new PageReference(dataSupplier.getTypes().get(Integer.valueOf(typeID)).name, PageType.TYPE, matcher.group(2)).toString());
        }
        traitString.append("<tr><td class='trait_data'>").append(traitTuple.v1 != 0 ? dataSupplier.unitify(traitTuple.v1, traitTuple.v3) : "").append("</td><td class='trait_data'>").append(bonusText).append("</td>");
    }

    @Override
    public String buildCSS() {
        return ".type_traits {\n" +
                "  padding: 1em;\n" +
                "}\n" +
                "\n" +
                ".trait_table {\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                ".trait_table_head {\n" +
                "  font-size: 1.5em;\n" +
                "}\n" +
                "\n" +
                ".trait_blank {\n" +
                "  height: 1em;\n" +
                "}\n" +
                "\n" +
                ".trait_data {\n" +
                "  padding-top: 5px;\n" +
                "  font-size: 0.9em;\n" +
                "}";
    }
}
