package net.sentientturtle.nee.orm;

import java.util.Map;

/**
 * Data object to represent EVE Online Industry Activities
 */
public class IndustryActivity {
    public final int bpTypeID;
    public final int activityID;
    public final int time;
    public final Map<Integer, Integer> materialMap;
    public final Map<Integer, Integer> productMap;
    public final Map<Integer, Double> probabilityMap;
    public final Map<Integer, Integer> skillMap;

    public IndustryActivity(int bpTypeID, int activityID, int time, Map<Integer, Integer> materialMap, Map<Integer, Integer> productMap, Map<Integer, Double> probabilityMap, Map<Integer, Integer> skillMap) {
        this.bpTypeID = bpTypeID;
        this.activityID = activityID;
        this.time = time;
        this.materialMap = materialMap;
        this.productMap = productMap;
        this.probabilityMap = probabilityMap;
        this.skillMap = skillMap;
    }

    @Override
    public String toString() {
        return "IndustryActivity{" +
                "bpTypeID=" + bpTypeID +
                ", activityID=" + activityID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndustryActivity that = (IndustryActivity) o;
        return bpTypeID == that.bpTypeID &&
                activityID == that.activityID;
    }

    @Override
    public int hashCode() {
        return bpTypeID + (31 * activityID);
    }
}
