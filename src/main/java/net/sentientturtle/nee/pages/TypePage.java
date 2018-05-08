package net.sentientturtle.nee.pages;

import net.sentientturtle.nee.util.ResourceLocation;
import net.sentientturtle.nee.components.*;
import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.util.tuple.Tuple2;
import net.sentientturtle.util.tuple.Tuple3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Page for a {@link Type}
 */
public class TypePage extends Page {
    public final Type type;

    TypePage(Type type, DataSupplier dataSupplier) {
        super(dataSupplier);
        this.type = type;
    }

    @Override
    protected void init() {
        Map<Integer, Tuple3<Double, Double, Double>> typeSizes = dataSupplier.getTypeSizes();
        int categoryID = dataSupplier.getGroups().get(type.groupID).categoryID;
        if ((categoryID == 6 || categoryID == 18 || categoryID == 65 || categoryID == 87)) {   // Check if type is a Ship, Drone, Citadel or Fighter
            //noinspection LanguageMismatch
            leftComponents.add(new Title(type.name, null, dataSupplier, this));
            leftComponents.add(new TypeGroup(type, dataSupplier, this));
            leftComponents.add(new TypeRender(type, dataSupplier, this));
            if (typeSizes.containsKey(type.typeID))
                leftComponents.add(new TypeSize(typeSizes.get(type.typeID), dataSupplier, this));
        } else {
            //noinspection LanguageMismatch
            leftComponents.add(new Title(type.name, getIcon(), dataSupplier, this));
            leftComponents.add(new TypeGroup(type, dataSupplier, this));
        }
        if (type.description != null && type.description.length() > 0)
            leftComponents.add(new TypeDescription(type, dataSupplier, this));

        if (dataSupplier.getTypeTraits().containsKey(type.typeID))
            leftComponents.add(new TypeTraits(type, dataSupplier, this));

        Map<Tuple2<Integer, Integer>, Double> attributeMap = dataSupplier.getAttributeValues();


        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2217), 0.0) > 0         // Light fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2218), 0.0) > 0  // Support fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2219), 0.0) > 0  // Heavy fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2737), 0.0) > 0  // Standup light fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2738), 0.0) > 0  // Standup support fighter slots
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 2739), 0.0) > 0) // Standup heavy fighter slots
            leftComponents.add(new ShipFighters(type, dataSupplier, this));

        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 1271), 0.0) > 0) // Drone bandwidth > 0
            leftComponents.add(new ShipDrones(type, dataSupplier, this));

        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 482), 0.0) > 0) // Capacitor capacity > 0
            leftComponents.add(new ShipCapacitor(type, dataSupplier, this));


        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 37), 0.0) > 0) // Max velocity > 0
            leftComponents.add(new ShipPropulsion(type, dataSupplier, this));

        // Has targeting range, signature strength or sensor strength
        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 76), 0.0) > 0 || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 552), 0.0) > 0
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 208), 0.0) > 0 || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 209), 0.0) > 0
                || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 210), 0.0) > 0 || attributeMap.getOrDefault(new Tuple2<>(type.typeID, 211), 0.0) > 0)
            leftComponents.add(new ShipSensors(type, dataSupplier, this));

        leftComponents.add(new ShipCargo(type, dataSupplier, this));

        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 263), 0.0) > 0)
            rightComponents.add(new ShipShield(type, dataSupplier, this));
        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 265), 0.0) > 0)
            rightComponents.add(new ShipArmor(type, dataSupplier, this));
        if (attributeMap.getOrDefault(new Tuple2<>(type.typeID, 9), 0.0) > 0)
            rightComponents.add(new ShipHull(type, dataSupplier, this));

        rightComponents.add(new ShipFitting(type, dataSupplier, this));

        if (dataSupplier.getTypeAttributes().containsKey(type.typeID))
            rightComponents.add(new TypeAttributes(type, dataSupplier, this));

        if (dataSupplier.getProductActivityMap().containsKey(type.typeID))
            rightComponents.add(new TypeProduction(type, dataSupplier, this));

        if (dataSupplier.getBpActivityMap().containsKey(type.typeID))
            rightComponents.add(new IndustryIO(type, dataSupplier, this));

        if (dataSupplier.getAttributeValues().containsKey(new Tuple2<>(type.typeID, 182)))  // Has a skill-required-1 attribute
            rightComponents.add(new TypeSkills(type, dataSupplier, this));

        if (dataSupplier.getParentTypes().containsKey(type.typeID)  // Check if type has a parent type
                || dataSupplier.getMetaTypes().containsKey(type.typeID))    // Check if type is the parent type
            rightComponents.add(new TypeVariants(type, dataSupplier, this));
    }

    @Override
    public PageType getPageType() {
        return PageType.TYPE;
    }

    @Override
    public String getPageName() {
        return type.name.replaceAll("[\\\\/:*?\"<>|\t]", "");
    }

    @Nullable
    @Override
    public ResourceLocation getIcon() {
        return ResourceLocation.iconOfTypeID(type.typeID, dataSupplier, this);
    }
}
