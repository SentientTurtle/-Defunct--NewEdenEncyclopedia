package net.sentientturtle.nee.data;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import net.sentientturtle.nee.orm.*;
import net.sentientturtle.nee.util.*;

import java.io.File;
import java.util.*;

/**
 * {@link DataSupplier} implementation that retrieves data from an SQLite conversion of the EVE Online Static Data Export,
 * with optional support for the Granny3D parser data.
 *
 * @see SDEUtils
 */
public class SQLiteDataSupplier extends DataSupplier {
    private LockableMap<Integer, Category> categories;
    private LockableMap<Integer, Group> groups;
    private LockableMap<Integer, Type> types;
    private LockableMap<Integer, Attribute> attributes;
    private LockableMap<Integer, Tuple3<Double, Double, Double>> typeSizes;
    private LockableMap<Integer, Map<Integer, List<Tuple3<Double, String, Integer>>>> typeTraits;
    private LockableMap<Tuple2<Integer, Integer>, Double> attributeValues;
    private LockableMap<Integer, Set<Integer>> typeAttributes;
    private LockableMap<Integer, String> eveIcons;
    private LockableMap<Integer, String> unitStrings;
    private LockableMap<Integer, IndustryActivityType> industryActivityTypes;
    private LockableMap<Integer, Set<IndustryActivity>> bpActivityMap;
    private LockableMap<Integer, Set<IndustryActivity>> materialActivityMap;
    private LockableMap<Integer, Set<IndustryActivity>> productActivityMap;
    private LockableMap<Integer, Set<IndustryActivity>> skillActivityMap;
    private LockableMap<Integer, MetaGroup> metaGroups;
    private LockableMap<Integer, Tuple2<Integer, Integer>> parentTypes;
    private LockableMap<Integer, Set<Integer>> metaTypes;
    private LockableList<SolarSystem> solarSystems;
    private LockableList<Constellation> constellations;
    private LockableList<Region> regions;
    private LockableList<Tuple2<Integer, Integer>> jumps;
    private LockableMap<Integer, Faction> factions;

    public SQLiteDataSupplier(SQLiteConnection connection, String grannyDBPath) throws SQLiteException {
        if (!connection.isOpen()) connection.open();

        boolean skipGranny = false;
        if (new File(grannyDBPath).exists()) {  // Check if data from parsing Granny3D files is present, and skip all related parsing if it is not.
            connection.exec("ATTACH '" + grannyDBPath + "' AS 'GRANNY'");
        } else {
            skipGranny = true;
        }
        categories = produceMap();
        SQLiteStatement st = connection.prepare("SELECT\n" +
                "  categoryID,\n" +
                "  categoryName,\n" +
                "  iconID,\n" +
                "  published\n" +
                "FROM invCategories");
        while (st.step()) {
            categories.put(st.columnInt(0), new Category(st.columnInt(0), st.columnString(1), st.columnNull(2) ? null : st.columnInt(2), st.columnInt(3) == 1));
        }
        st.dispose();
        categories.lock();


        groups = produceMap();
        st = connection.prepare("SELECT\n" +
                "  invGroups.groupID,\n" +
                "  invGroups.categoryID,\n" +
                "  groupName,\n" +
                "  iconID,\n" +
                "  published\n" +
                "FROM invGroups");
        while (st.step()) {
            groups.put(st.columnInt(0), new Group(st.columnInt(0), st.columnInt(1), st.columnString(2), st.columnNull(3) ? null : st.columnInt(3), st.columnInt(4) == 1));
        }
        st.dispose();
        groups.lock();


        types = produceMap();
        st = connection.prepare("SELECT\n" +
                "  typeID,\n" +
                "  groupID,\n" +
                "  typeName,\n" +
                "  description,\n" +
                "  mass,\n" +
                "  volume,\n" +
                "  capacity,\n" +
                "  published\n" +
                "FROM invTypes\n");
        while (st.step()) {
            types.put(st.columnInt(0), new Type(st.columnInt(0), st.columnInt(1), st.columnString(2), st.columnString(3), st.columnDouble(4), st.columnDouble(5), st.columnDouble(6), st.columnInt(7) == 1));
        }
        st.dispose();
        DataSupplier.Patcher.patchTypes(types);
        types.lock();


        typeSizes = produceMap();
        if (!skipGranny) {
            st = connection.prepare("SELECT typeID, sizeX, sizeY, sizeZ FROM invSizes");
            while (st.step()) {
                typeSizes.put(st.columnInt(0), new Tuple3<>(st.columnDouble(1), st.columnDouble(2), st.columnDouble(3)));
            }
            st.dispose();
        }
        typeSizes.lock();


        typeTraits = produceMap();
        st = connection.prepare("SELECT\n" +
                "  typeID,\n" +
                "  skillID,\n" +
                "  bonus,\n" +
                "  bonusText,\n" +
                "  unitID\n" +
                "FROM invTraits");
        while (st.step()) {
            typeTraits.computeIfAbsent(st.columnInt(0), this::produceMap)
                    .computeIfAbsent(st.columnInt(1), this::produceList)
                    .add(new Tuple3<>(st.columnDouble(2), st.columnString(3), st.columnInt(4)));
        }
        st.dispose();
        for (Map<Integer, List<Tuple3<Double, String, Integer>>> traitMap : typeTraits.values()) {
            traitMap.values().forEach(l -> ((LockableList) l).lock());
            traitMap.replaceAll((i, l) -> Collections.unmodifiableList(l));
        }
        typeTraits.lock();
        typeTraits.values().forEach(map -> ((LockableMap) map).lock());


        unitStrings = produceMap();
        st = connection.prepare("SELECT unitID, displayName FROM eveUnits");
        while (st.step()) {
            if (st.columnString(1) != null) {
                unitStrings.put(st.columnInt(0), st.columnString(1).replace("m2", "m²").replace("m3", "m³"));
            } else {
                unitStrings.put(st.columnInt(0), "");
            }
        }
        st.dispose();
        unitStrings.lock();


        attributeValues = produceMap();
        st = connection.prepare("SELECT typeID, attributeID, valueFloat, valueInt FROM dgmTypeAttributes");
        while (st.step()) {
            attributeValues.put(new Tuple2<>(st.columnInt(0), st.columnInt(1)), st.columnNull(2) ? st.columnDouble(3) : st.columnDouble(2));
        }
        st.dispose();
        DataSupplier.Patcher.patchAttributeValues(attributeValues);
        attributeValues.lock();


        typeAttributes = produceMap();
        for (Tuple2<Integer, Integer> typeAttribute : attributeValues.keySet()) {
            typeAttributes.computeIfAbsent(typeAttribute.v1, this::produceSet).add(typeAttribute.v2);
        }
        typeAttributes.lock();
        typeAttributes.values().forEach(set -> ((LockableSet) set).lock());


        attributes = produceMap();
        st = connection.prepare("SELECT attributeID, categoryID, attributeName, displayName, unitID, iconID, published FROM dgmAttributeTypes");
        while (st.step()) {
            attributes.put(st.columnInt(0), new Attribute(st.columnInt(0), st.columnInt(1), st.columnString(2), st.columnString(3), st.columnInt(4), st.columnInt(5), st.columnInt(6) == 1));
        }
        st.dispose();
        DataSupplier.Patcher.patchAttributes(attributes);
        attributes.lock();


        eveIcons = produceMap();
        st = connection.prepare("SELECT iconID, iconFile FROM eveIcons");
        while (st.step()) {
            eveIcons.put(st.columnInt(0), st.columnString(1));
        }
        st.dispose();
        eveIcons.lock();

        industryActivityTypes = produceMap();
        st = connection.prepare("SELECT activityID, activityName, published FROM ramActivities");
        while (st.step()) {
            industryActivityTypes.put(st.columnInt(0), new IndustryActivityType(st.columnInt(0), st.columnString(1), st.columnInt(2) == 1));
        }
        st.dispose();
        industryActivityTypes.lock();

        HashMap<Tuple2<Integer, Integer>, Integer> activityTimeMap = new HashMap<>();                           // BP TypeID+activityID mapped to time
        HashMap<Tuple2<Integer, Integer>, LockableMap<Integer, Integer>> activityMaterialMap = new HashMap<>();     // BP TypeID+activityID mapped to map of materialTypeID mapped to quantity
        HashMap<Tuple2<Integer, Integer>, LockableMap<Integer, Integer>> activityProductMap = new HashMap<>();      // BP TypeID+activityID mapped to map of productTypeID mapped to quantity
        HashMap<Tuple2<Integer, Integer>, LockableMap<Integer, Double>> activityProbabilityMap = new HashMap<>();   // BP TypeID+activityID mapped to map of productTypeID mapped to probability
        HashMap<Tuple2<Integer, Integer>, LockableMap<Integer, Integer>> activitySkillMap = new HashMap<>();        // BP TypeID+activityID mapped to map of skillID mapped to level

        st = connection.prepare("SELECT industryActivity.typeID, industryActivity.activityID, industryActivity.time FROM industryActivity");
        while (st.step()) {
            activityTimeMap.put(new Tuple2<>(st.columnInt(0), st.columnInt(1)), st.columnInt(2));
        }
        st.dispose();

        st = connection.prepare("SELECT typeID, activityID, materialTypeID, quantity FROM industryActivityMaterials");
        while (st.step()) {
            activityMaterialMap.computeIfAbsent(new Tuple2<>(st.columnInt(0), st.columnInt(1)), this::produceMap).put(st.columnInt(2), st.columnInt(3));
        }
        st.dispose();

        st = connection.prepare("SELECT typeID, activityID, productTypeID, probability FROM industryActivityProbabilities");
        while (st.step()) {
            activityProbabilityMap.computeIfAbsent(new Tuple2<>(st.columnInt(0), st.columnInt(1)), this::produceMap).put(st.columnInt(2), st.columnDouble(3));
        }
        st.dispose();

        st = connection.prepare("SELECT typeID, activityID, productTypeID, quantity FROM industryActivityProducts");
        while (st.step()) {
            activityProductMap.computeIfAbsent(new Tuple2<>(st.columnInt(0), st.columnInt(1)), this::produceMap).put(st.columnInt(2), st.columnInt(3));
        }
        st.dispose();

        st = connection.prepare("SELECT typeID, activityID, skillID, level FROM industryActivitySkills");
        while (st.step()) {
            activitySkillMap.computeIfAbsent(new Tuple2<>(st.columnInt(0), st.columnInt(1)), this::produceMap).put(st.columnInt(2), st.columnInt(3));
        }
        st.dispose();

        // Lock maps that are passed to IndustryActivity objects
        activityMaterialMap.values().forEach(LockableMap::lock);
        activityProductMap.values().forEach(LockableMap::lock);
        activityProbabilityMap.values().forEach(LockableMap::lock);
        activitySkillMap.values().forEach(LockableMap::lock);

        bpActivityMap = produceMap();
        materialActivityMap = produceMap();
        productActivityMap = produceMap();
        skillActivityMap = produceMap();

        activityTimeMap.forEach((typeActivityTuple, time) -> {
            if (activityMaterialMap.containsKey(typeActivityTuple) || activityProductMap.containsKey(typeActivityTuple)) {
                IndustryActivity activity = new IndustryActivity(
                        typeActivityTuple.v1,
                        typeActivityTuple.v2,
                        time,
                        activityMaterialMap.getOrDefault(typeActivityTuple, new LockableMap<>(Collections.emptyMap())),
                        activityProductMap.getOrDefault(typeActivityTuple, new LockableMap<>(Collections.emptyMap())),
                        activityProbabilityMap.getOrDefault(typeActivityTuple, new LockableMap<>(Collections.emptyMap())),
                        activitySkillMap.getOrDefault(typeActivityTuple, new LockableMap<>(Collections.emptyMap()))
                );
                bpActivityMap.computeIfAbsent(activity.bpTypeID, this::produceSet).add(activity);
                for (Integer materialID : activity.materialMap.keySet())
                    materialActivityMap.computeIfAbsent(materialID, this::produceSet).add(activity);
                for (Integer productID : activity.productMap.keySet())
                    productActivityMap.computeIfAbsent(productID, this::produceSet).add(activity);
                for (Integer skillID : activity.skillMap.keySet())
                    skillActivityMap.computeIfAbsent(skillID, this::produceSet).add(activity);
            }
        });

        bpActivityMap.lock();
        bpActivityMap.values().forEach(set -> ((LockableSet) set).lock());
        materialActivityMap.lock();
        materialActivityMap.values().forEach(set -> ((LockableSet) set).lock());
        productActivityMap.lock();
        productActivityMap.values().forEach(set -> ((LockableSet) set).lock());
        skillActivityMap.lock();
        skillActivityMap.values().forEach(set -> ((LockableSet) set).lock());

        metaGroups = produceMap();
        st = connection.prepare("SELECT metaGroupID, metaGroupName FROM invMetaGroups");
        while (st.step()) {
            metaGroups.put(st.columnInt(0), new MetaGroup(st.columnInt(0), st.columnString(1)));
        }
        st.dispose();
        metaGroups.lock();

        parentTypes = produceMap();
        metaTypes = produceMap();
        st = connection.prepare("SELECT typeID, parentTypeID, metaGroupID FROM invMetaTypes");
        while (st.step()) {
            parentTypes.put(st.columnInt(0), new Tuple2<>(st.columnInt(1), st.columnInt(2)));
            metaTypes.computeIfAbsent(st.columnInt(1), this::produceSet).add(st.columnInt(0));
        }
        st.dispose();
        parentTypes.lock();
        for (Map.Entry<Integer, Set<Integer>> setEntry : metaTypes.entrySet()) setEntry.getValue().add(setEntry.getKey());
        metaTypes.lock();
        metaTypes.values().forEach(set -> ((LockableSet) set).lock());

        solarSystems = produceList();
        st = connection.prepare("SELECT\n" +
                "  regionID,\n" +
                "  constellationID,\n" +
                "  solarSystemID,\n" +
                "  solarSystemName,\n" +
                "  x,\n" +
                "  y,\n" +
                "  z,\n" +
                "  security,\n" +
                "  factionID,\n" +
                "  sunTypeID\n" +
                "FROM mapSolarSystems\n");
        while (st.step()) {
            solarSystems.add(new SolarSystem(
                    st.columnInt(0),
                    st.columnInt(1),
                    st.columnInt(2),
                    st.columnString(3),
                    st.columnDouble(4),
                    st.columnDouble(5),
                    st.columnDouble(6),
                    st.columnDouble(7),
                    st.columnNull(8) ? null : st.columnInt(8),
                    st.columnInt(9)
            ));
        }
        st.dispose();
        solarSystems.lock();

        constellations = produceList();
        st = connection.prepare("SELECT regionID, constellationID, constellationName, x, y, z, factionID FROM mapConstellations");
        while (st.step()) {
            constellations.add(new Constellation(
                    st.columnInt(0),
                    st.columnInt(1),
                    st.columnString(2),
                    st.columnDouble(3),
                    st.columnDouble(4),
                    st.columnDouble(5),
                    st.columnNull(6) ? null : st.columnInt(6)
            ));
        }
        st.dispose();
        constellations.lock();

        regions = produceList();
        st = connection.prepare("SELECT regionID, regionName, x, y, z, factionID FROM mapRegions");
        while (st.step()) {
            regions.add(new Region(
                    st.columnInt(0),
                    st.columnString(1),
                    st.columnDouble(2),
                    st.columnDouble(3),
                    st.columnDouble(4),
                    st.columnNull(5) ? null : st.columnInt(5)
            ));
        }
        st.dispose();
        regions.lock();

        jumps = produceList();
        st = connection.prepare("SELECT fromSolarSystemID, toSolarSystemID FROM mapSolarSystemJumps");
        while (st.step()) {
            jumps.add(new Tuple2<>(st.columnInt(0), st.columnInt(1)));
        }
        st.dispose();
        jumps.lock();


        factions = produceMap();
        st = connection.prepare("SELECT factionID, factionName, corporationID from chrFactions");
        while (st.step()) {
            factions.put(st.columnInt(0), new Faction(
                    st.columnInt(0),
                    st.columnString(1),
                    st.columnInt(2)
            ));
        }
        st.dispose();
        factions.lock();

        connection.dispose();
    }

    /*
     * Utility methods to allow usage as method reference, and anonymous-subclassing of the generated maps/lists/sets for debugging.
     */
    private <K, V> LockableMap<K, V> produceMap() {
        return new LockableMap<>(new HashMap<>());
    }

    private <K, V> LockableMap<K, V> produceMap(Object input) {  // For use as a method reference
        return produceMap();
    }

    private <E> LockableSet<E> produceSet() {
        return new LockableSet<>(new HashSet<>());
    }

    private <E> LockableSet<E> produceSet(Object input) {
        return produceSet();
    }

    private <E> LockableList<E> produceList() {
        return new LockableList<>(new ArrayList<>());
    }

    private <E> LockableList<E> produceList(Object input) {
        return produceList();
    }

    @Override
    public Map<Integer, Category> getCategories() {
        return categories;
    }

    @Override
    public Map<Integer, Group> getGroups() {
        return groups;
    }

    @Override
    public Map<Integer, Type> getTypes() {
        return types;
    }

    @Override
    public Map<Integer, Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public Map<Integer, Tuple3<Double, Double, Double>> getTypeSizes() {
        return typeSizes;
    }

    @Override
    public Map<Integer, Map<Integer, List<Tuple3<Double, String, Integer>>>> getTypeTraits() {
        return typeTraits;
    }

    @Override
    public Map<Integer, String> getUnitStrings() {
        return unitStrings;
    }

    @Override
    public Map<Tuple2<Integer, Integer>, Double> getAttributeValues() {
        return attributeValues;
    }

    @Override
    public Map<Integer, Set<Integer>> getTypeAttributes() {
        return typeAttributes;
    }

    @Override
    public Map<Integer, String> getEveIcons() {
        return eveIcons;
    }

    @Override
    public Map<Integer, IndustryActivityType> getIndustryActivityTypes() {
        return industryActivityTypes;
    }

    @Override
    public Map<Integer, Set<IndustryActivity>> getBpActivityMap() {
        return bpActivityMap;
    }

    @Override
    public Map<Integer, Set<IndustryActivity>> getMaterialActivityMap() {
        return materialActivityMap;
    }

    @Override
    public Map<Integer, Set<IndustryActivity>> getProductActivityMap() {
        return productActivityMap;
    }

    @Override
    public Map<Integer, Set<IndustryActivity>> getSkillActivityMap() {
        return skillActivityMap;
    }

    @Override
    public Map<Integer, MetaGroup> getMetaGroups() {
        return metaGroups;
    }

    @Override
    public Map<Integer, Tuple2<Integer, Integer>> getParentTypes() {
        return parentTypes;
    }

    @Override
    public Map<Integer, Set<Integer>> getMetaTypes() {
        return metaTypes;
    }

    @Override
    public List<SolarSystem> getSolarSystems() {
        return solarSystems;
    }

    @Override
    public List<Constellation> getConstellations() {
        return constellations;
    }

    @Override
    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public List<Tuple2<Integer, Integer>> getJumps() {
        return jumps;
    }

    @Override
    public Map<Integer, Faction> getFactions() {
        return factions;
    }
}
