package net.sentientturtle.nee.components;

import net.sentientturtle.nee.data.DataSupplier;
import net.sentientturtle.nee.orm.Type;
import net.sentientturtle.nee.util.Tuple4;

/**
 * Displays armor stats of a ship {@link Type}
 * @see ShipResists
 */
public class ShipHull extends ShipResists {

    public ShipHull(Type type) {
        super(type);
    }

    @Override
    protected int getHpAttribute() {
        return 9;
    }

    @Override
    protected String getType() {
        return "Hull";
    }

    @Override
    protected Tuple4<Double, Double, Double, Double> getResists(DataSupplier dataSupplier) {
        return super.getResists(dataSupplier, 113, 110, 109, 111);
    }

    @Override
    protected String getRechargeText(DataSupplier dataSupplier) {
        return null;
    }
}
