package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple2;
import net.sentientturtle.nee.util.Tuple4;
/**
 * Displays shield stats of a ship {@link Type}
 * @see ShipResists
 */
public class ShipShield extends ShipResists {
    public ShipShield(Type type) {
        super(type);
    }

    @Override
    protected int getHpAttribute() {
        return 263;
    }

    @Override
    protected String getType() {
        return "Shield";
    }

    @Override
    protected Tuple4<Double, Double, Double, Double> getResists(DataSupplier dataSupplier) {
        return super.getResists(dataSupplier, 271, 274, 273, 272);
    }

    @Override
    protected String getRechargeText(DataSupplier dataSupplier) {
        double recharge = dataSupplier.getAttributeValues().getOrDefault(new Tuple2<>(super.type.typeID, 479), 0.0);
        if (recharge > 0) {
            return "Shield recharge time: " + dataSupplier.unitify(recharge, dataSupplier.getAttributes().get(479).unitID);
        } else {
            return null;
        }
    }
}
