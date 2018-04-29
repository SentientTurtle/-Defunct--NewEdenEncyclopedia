package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.*;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;
import net.sentientturtle.nee.util.Tuple3;

import java.util.Map;

/**
 * Page for a {@link Type}
 */
public class TypePage extends Page {
    public final Type type;

    public TypePage(Type type, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.type = type;
        Map<Integer, Tuple3<Double, Double, Double>> typeSizes = dataSupplier.getTypeSizes();
        int categoryID = dataSupplier.getGroups().get(type.groupID).categoryID;
        if ((categoryID == 6 || categoryID == 18 || categoryID == 65 || categoryID == 87)) {   // Check if type is a Ship, Drone, Citadel or Fighter
            leftComponents.add(new Title(type.name, null));
            leftComponents.add(new TypeGroup(type));
            leftComponents.add(new TypeRender(type, dataSupplier));
            if (typeSizes.containsKey(type.typeID))
                leftComponents.add(new TypeSize(typeSizes.get(type.typeID)));
        } else {
            leftComponents.add(new Title(type.name, ResourceLocation.iconOfTypeID(type.typeID, dataSupplier)));
            leftComponents.add(new TypeGroup(type));
        }
        if (type.description != null && type.description.length() > 0)
            leftComponents.add(new TypeDescription(type));

        if (dataSupplier.getTypeTraits().containsKey(type.typeID))
            leftComponents.add(new TypeTraits(type));

        Map<Tuple2<Integer, Integer>, Double> attributeMap = dataSupplier.getAttributeValues();


        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2217), 0.0) > 0         // Light fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2218), 0.0) > 0  // Support fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2219), 0.0) > 0  // Heavy fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2737), 0.0) > 0  // Standup light fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2738), 0.0) > 0  // Standup support fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2739), 0.0) > 0) // Standup heavy fighter slots
            leftComponents.add(new ShipFighters(type));

        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 1271), 0.0) > 0) // Drone bandwidth > 0
            leftComponents.add(new ShipDrones(type));

        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 482), 0.0) > 0) // Capacitor capacity > 0
            leftComponents.add(new ShipCapacitor(type));


        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 37), 0.0) > 0) // Max velocity > 0
            leftComponents.add(new ShipPropulsion(type));

        // Has targeting range, signature strength or sensor strength
        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 76), 0.0) > 0 || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 552), 0.0) > 0
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 208), 0.0) > 0 || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 209), 0.0) > 0
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 210), 0.0) > 0 || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 211), 0.0) > 0)
            leftComponents.add(new ShipSensors(type));

        leftComponents.add(new ShipCargo(type));

        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 263), 0.0) > 0)
            rightComponents.add(new ShipShield(type));
        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 265), 0.0) > 0)
            rightComponents.add(new ShipArmor(type));
        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 9), 0.0) > 0)
            rightComponents.add(new ShipHull(type));

        rightComponents.add(new ShipFitting(type));

        if (dataSupplier.getTypeAttributes().containsKey(type.typeID))
            rightComponents.add(new TypeAttributes(type));

        if (dataSupplier.getProductActivityMap().containsKey(type.typeID))
            rightComponents.add(new TypeProduction(type));

        if (dataSupplier.getBpActivityMap().containsKey(type.typeID))
            rightComponents.add(new IndustryIO(type));

        if (dataSupplier.getAttributeValues().containsKey(new Tuple2<>(type.typeID, 182)))  // Has a skill-required-1 attribute
            rightComponents.add(new TypeSkills(type));

        if (dataSupplier.getParentTypes().containsKey(type.typeID)  // Check if type has a parent type
                || dataSupplier.getMetaTypes().containsKey(type.typeID))    // Check if type is the parent type
            rightComponents.add(new TypeVariants(type));
    }

    @Override
    public PageType getPageType() {
        return PageType.TYPE;
    }

    @Override
    public String getPageName() {
        return type.name.replaceAll("[\\\\/:*?\"<>|\t]", "");
    }
}
