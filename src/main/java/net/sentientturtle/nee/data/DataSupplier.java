package net.sentientturtle.nee.data;

import net.sentientturtle.nee.util.PageReference;
import net.sentientturtle.nee.orm.*;
import net.sentientturtle.nee.pages.PageType;
import net.sentientturtle.nee.util.Tuple2;
import net.sentientturtle.nee.util.Tuple3;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Object to provide access to EVE Online Data.
 * Split into an abstract class to allow swapping of data sources.
 * Planned sources are: SQLite Static Data Export conversions, and EVE Online's ESI API
 * @see SQLiteDataSupplier
 */
public abstract class DataSupplier {
    private static ThreadLocal<DecimalFormat> threadLocalDecimalFormat;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        threadLocalDecimalFormat = ThreadLocal.withInitial(() -> new DecimalFormat("###,##0.##########", symbols));
    }

    // Map<CategoryID, Category>
    public abstract Map<Integer, Category> getCategories();

    // Map<GroupID, Group>
    public abstract Map<Integer, Group> getGroups();

    // Map<TypeID, Type>
    public abstract Map<Integer, Type> getTypes();

    // Map<TypeID, (Width, Height, Length)>
    public abstract Map<Integer, Tuple3<Double, Double, Double>> getTypeSizes();

    // Map<TypeID, Map<SkillID, List<(Bonus, BonusText, UnitID)>>>
    public abstract Map<Integer, Map<Integer, List<Tuple3<Double, String, Integer>>>> getTypeTraits();

    // Map<AttributeID, Attribute>
    public abstract Map<Integer, Attribute> getAttributes();

    // Map<(TypeID, AttributeID), Value>
    public abstract Map<Tuple2<Integer, Integer>, Double> getAttributeValues();

    // Map<TypeID, Set<AttributeID>>
    public abstract Map<Integer, Set<Integer>> getTypeAttributes();

    // Map<UnitID, Unit postfix>
    public abstract Map<Integer, String> getUnitStrings();

    // Map<IconID, IconFile>
    public abstract Map<Integer, String> getEveIcons();

    // Map<ActivityID, IndustryActivityType>
    public abstract Map<Integer, IndustryActivityType> getIndustryActivityTypes();

    // Map<Blueprint TypeID, Set<IndustryActivity using blueprint>>
    public abstract Map<Integer, Set<IndustryActivity>> getBpActivityMap();

    // Map<Material TypeID, Set<IndustryActivity using material>>
    public abstract Map<Integer, Set<IndustryActivity>> getMaterialActivityMap();

    // Map<Product TypeID, Set<IndustryActivity producing product>>
    public abstract Map<Integer, Set<IndustryActivity>> getProductActivityMap();

    // Map<Skill TypeID, Set<IndustryActivity using skill>>
    public abstract Map<Integer, Set<IndustryActivity>> getSkillActivityMap();

    // Map<MetaGroupID, MetaGroup>
    public abstract Map<Integer, MetaGroup> getMetaGroups();

    // Map<TypeID, (ParentTypeID of type, metaGroupID)>
    public abstract Map<Integer, Tuple2<Integer, Integer>> getParentTypes();

    // Map<TypeID, Set<TypeID of variants>>
    public abstract Map<Integer, Set<Integer>> getMetaTypes();

    public abstract List<SolarSystem> getSolarSystems();

    public abstract List<Constellation> getConstellations();

    public abstract List<Region> getRegions();

    // List<(fromSolarSystemID, toSolarSystemID)>
    public abstract List<Tuple2<Integer, Integer>> getJumps();

    // Map<FactionID, Faction>
    public abstract Map<Integer, Faction> getFactions();


    /**
     * Formats a double value into a String with a given unit
     * @param value Value to format
     * @param unitID UnitID of the unit to format the value with
     * @return Value formatted as a String with the specified unit
     */
    public String unitify(double value, int unitID) {
        DecimalFormat decimalFormat = DataSupplier.threadLocalDecimalFormat.get();
        Map<Integer, String> unitMap = getUnitStrings();
        Map<Integer, Type> typeMap = getTypes();
        switch (unitID) {
            case 19:    // "Mass Fraction"	"kg/kg = 1"
            case 122:   // "Fitting slots"	"NULL"
            case 143:   // "Datetime"	"NULL"	"Date and time"
                return decimalFormat.format(value);
            case 103:   // MegaPascals  NULL
                return decimalFormat.format(value) + " MPa";
            case 101:   // "Milliseconds"	"s"
                if (value < 1000 * 60) {
                    return String.format("%2ds", (int) (value / 1000));
                } else if (value < 1000 * 60 * 60) {
                    return String.format("%2dm %2ds", (int) (value / 1000) / 60, (int) (value / 1000) % 60);
                } else if (value < 1000 * 60 * 60 * 24) {
                    return String.format("%2dh %2dm %2ds", (int) (value / 1000) / (60 * 60), (int) (value / 1000) % (60 * 60) / 60, (int) (value / 1000) % 60);
                } else if (value < 1000.0 * 60.0 * 60.0 * 24.0 * 30.0) {
                    return String.format("%2dd", (int) (value / 1000) / (60 * 60 * 24)) + " " + String.format("%2dh %2dm %2ds", (int) (value / 1000) % (60 * 60 * 24) / (60 * 60), (int) (value / 1000) % (60 * 60) / 60, (int) (value / 1000) % 60);
                } else if (value < 1000.0 * 60.0 * 60.0 * 24.0 * 365.0) {
                    return String.format("%2dm", (int) (value / 1000) / (60 * 60 * 24 * 30)) + " " + String.format("%2dd", (int) (value / 1000) % (60 * 60 * 24 * 30) / (60 * 60 * 24)) + " " + String.format("%2dh %2dm %2ds", (int) (value / 1000) % (60 * 60 * 24) / (60 * 60), (int) (value / 1000) % (60 * 60) / 60, (int) (value / 1000) % 60);
                } else {
                    return String.format("%2dy", (int) (value / 1000) / (60 * 60 * 24 * 365)) + " " + String.format("%2dm", (int) (value / 1000) % (60 * 60 * 24 * 365) / (60 * 60 * 24 * 30)) + " " + String.format("%2dd", (int) (value / 1000) % (60 * 60 * 24 * 30) / (60 * 60 * 24)) + " " + String.format("%2dh %2dm %2ds", (int) (value / 1000) % (60 * 60 * 24) / (60 * 60), (int) (value / 1000) % (60 * 60) / 60, (int) (value / 1000) % 60);
                }
            case 108:   // "Inverse Absolute Percent"	"%"	"Used for resistance."
            case 111:   // "Inversed Modifier Percent"	"%"	"Used to modify damage resistance. Damage resistance bonus."
                return Math.round((1 - value) * 100) + unitMap.get(unitID);
            case 109:   // "Modifier Percent"	"%"	"Used for multipliers displayed as %"
                return (Math.round((value - 1) * 100) > 0 ? "+" + Math.round((value - 1) * 100) : Math.round((value - 1) * 100)) + unitMap.get(unitID);
            case 133:   // "Money"	"ISK"	"ISK"
            case 113:   // "Hitpoints"	"HP"
                return decimalFormat.format(value) + " " + unitMap.get(unitID);
            case 120:   // "attributePoints"	"points"
            case 138:   // "Units"	"units"	"Units of something, for example fuel"
            case 141:   // "Hardpoints"	"hardpoints"	"For various counts to do with turret, launcher and rig hardpoints"
                String unit = unitMap.get(unitID);
                if (value == 1 || value == -1) {
                    return decimalFormat.format(value) + " " + unit.substring(0, unit.length() - 1);
                } else {
                    return decimalFormat.format(value) + " " + unit;
                }
            case 115:   // "groupID"	"groupID"
            case 119:   // "attributeID"	"attributeID"
            case 136:   // "Slot"	"Slot"	"Slot number prefix for various purposes"
            case 140:   // "Level"	"Level"	"For anything which is divided by levels"
                return unitMap.get(unitID) + " " + decimalFormat.format(value);
            case 116:   // "typeID"	"typeID"
                if (typeMap.containsKey((int) value)) {
                    return new PageReference(typeMap.get((int) value).name, PageType.TYPE).toString();
                } else {
                    System.err.println("UNKNOWN ITEM: " + value);
                    return "[UNKNOWN ITEM:" + (int) value + "]";
                }
            case 117:   // "Sizeclass"	"1=small 2=medium 3=l"
                if (value == 0.0) {
                    return "X-Small";
                } else if (value == 1.0) {
                    return "Small";
                } else if (value == 2.0) {
                    return "Medium";
                } else if (value == 3.0) {
                    return "Large";
                } else if (value == 4.0) {
                    return "X-Large";
                } else {
                    throw new RuntimeException("INVALID SIZE CLASS: " + value + " DATA SOURCE CORRUPT?");
                }
            case 121:   // "realPercent"	"%"	"Used for real percentages, i.e. the number 5 is 5%"
            case 124:   // "Modifier Relative Percent"	"%"	"Used for relative percentages displayed as %"
                return Math.round(value) + unitMap.get(unitID);
            case 127:   // "Absolute Percent"	"%"	"0.0 = 0% 1.0 = 100%"
                return Math.round(value * 100) + unitMap.get(unitID);
            case 129:   // "Hours"	"NULL"	"Hours"
                return value + "h";
            case 137:   // "Boolean"	"1=True 0=False"	"For displaying boolean flags"
                if (value == 1.0) {
                    return "True";
                } else if (value == 0.0) {
                    return "False";
                } else {
                    throw new RuntimeException("INVALID BOOLEAN: " + value + " DATA SOURCE CORRUPT?");
                }
            case 139:   // "Bonus"	"+"	"Forces a plus sign for positive values"
                if (value > 0) {
                    return "+" + decimalFormat.format(value);
                } else {
                    return decimalFormat.format(value);
                }
            case 142:   // "Sex"	"1=Male 2=Unisex 3=Female"
                if (value == 1.0) {
                    return "Male";
                } else if (value == 2.0) {
                    return "Unisex";
                } else if (value == 3.0) {
                    return "Female";
                } else {
                    throw new RuntimeException("INVALID SEX: " + value + " DATA SOURCE CORRUPT?");
                }
            case -1:    // unitMap doesn't contain -1, making this a sentinel value for the else clause below.
            default:
                return unitMap.containsKey(unitID) ? decimalFormat.format(value) + unitMap.get(unitID) : decimalFormat.format(value);
        }
    }

    /**
     * Class with some utility methods to patch errors and undesired values in EVE Online data.
     */
    public static class Patcher {
        private static final boolean PATCHER_VERBOSE_LOG = false;   // Debug option for logging of patched data


        private static final Set<Integer> blockedAttributeCategories = new HashSet<>();
        private static final Set<Integer> blockedAttributes = new HashSet<>();

        static {
            Collections.addAll(blockedAttributeCategories, 41, 1, 2, 3, 4, 6, 5, 29, 36, 37, 38);
            Collections.addAll(blockedAttributes, 1132, 1154, 600, 37, 70, 898, 866, 868, 867,
                    979, 1005, 716, 715, 2078, 2077, 2076, 610, 609, 606, 605, 604, 603, 602, 137, 120,
                    1211, 15, 49, 3, 283, 1271, 2055, 2216, 2217, 2218, 2219, 1556, 1557, 1558, 1559, 1560,
                    1561, 1562, 1563, 1564, 1573, 1646, 1653, 1770, 1804, 908, 912, 1549, 280, 291, 292, 293,
                    294, 306, 308, 310, 311, 312, 313, 314, 315, 317, 318, 319, 323, 327, 329, 335,
                    336, 337, 338, 349, 351, 353, 379, 413, 414, 418, 424, 432, 434, 437, 438, 440,
                    441, 449, 450, 452, 453, 455, 459, 468, 547, 550, 554, 557, 596, 601, 614, 624,
                    800, 806, 828, 832, 850, 958, 1079, 1547, 1290, 1289, 1288, 1287, 1286, 1285, 279, 278,
                    277, 184, 183, 182, 1971, 1972, 2467, 2657, 2675
            );
        }


        public static void patchTypes(Map<Integer, Type> types) {
            // Unpublish types with identical names
            try {
                Field published_type = Type.class.getField("published");
                published_type.setAccessible(true);
                Field description = Type.class.getField("description");
                description.setAccessible(true);

                HashMap<String, List<Type>> nameMap = new HashMap<>();
                for (Type type : types.values()) {
                    nameMap.computeIfAbsent(type.name, s -> new ArrayList<>()).add(type);

                    // Delete external links from descriptions
                    if (type.groupID == 1141 || type.typeID == 33099) {
                        description.set(type, type.description.replaceAll("<a.*?>|</a>", ""));
                        if (PATCHER_VERBOSE_LOG) System.out.println("De-URL'd: " + type);
                    } else if (type.groupID == 1977 || type.typeID == 29668 || type.typeID == 34496 || type.typeID == 44992) {
                        description.set(type, type.description.replaceAll("<url.*?>|</url>", ""));
                        if (PATCHER_VERBOSE_LOG) System.out.println("De-URL'd: " + type);
                    }
                }

                nameMap.values().stream().filter(list -> list.size() > 1).forEach(list -> {
                    Type rootType = list.stream()    // Select newest type with name
                            .filter(type -> type.published)
                            .max(Comparator.comparingInt(o -> o.typeID))
                            .orElse(list.stream().max(Comparator.comparingInt(o -> o.typeID)).get());
                    list.stream().filter(type -> type.typeID != rootType.typeID && type.published).forEach(type -> {
                        //noinspection Duplicates
                        try {
                            published_type.set(type, false);
                            if (PATCHER_VERBOSE_LOG) System.out.println("Unpublished: " + type);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
                });

            } catch (NoSuchFieldException e) {
                System.err.println("MISSING FIELD!");
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                System.err.println("Reflection error!");
                throw new RuntimeException(e);
            }
        }

        public static void patchAttributes(Map<Integer, Attribute> attributes) {
            try {
                Field published_attr = Attribute.class.getField("published");
                published_attr.setAccessible(true);
                attributes.values().stream()
                        .filter(attribute -> (attribute.published)
                                && (blockedAttributeCategories.contains(attribute.categoryID)
                                || blockedAttributes.contains(attribute.attributeID)))
                        .forEach(attribute -> {
                            //noinspection Duplicates
                            try {
                                published_attr.set(attribute, false);
                                if (PATCHER_VERBOSE_LOG) System.out.println("Unpublished: " + attribute);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (NoSuchFieldException e) {
                System.err.println("MISSING FIELD!");
                throw new RuntimeException(e);
            }
        }

        public static void patchAttributeValues(Map<Tuple2<Integer, Integer>, Double> attributeValues) {
            // Remove skill entries without matching level entry
            attributeValues.entrySet().removeIf(entry ->
                    (entry.getKey().v2 == 182 && attributeValues.get(new Tuple2<>(entry.getKey().v1, 277)) == null)
                    || (entry.getKey().v2 == 183 && attributeValues.get(new Tuple2<>(entry.getKey().v1, 278)) == null)
                    || (entry.getKey().v2 == 184 && attributeValues.get(new Tuple2<>(entry.getKey().v1, 279)) == null)
                    || (entry.getKey().v2 == 1285 && attributeValues.get(new Tuple2<>(entry.getKey().v1, 1286)) == null)
                    || (entry.getKey().v2 == 1289 && attributeValues.get(new Tuple2<>(entry.getKey().v1, 1287)) == null)
                    || (entry.getKey().v2 == 1290 && attributeValues.get(new Tuple2<>(entry.getKey().v1, 1288)) == null));
        }
    }
}
