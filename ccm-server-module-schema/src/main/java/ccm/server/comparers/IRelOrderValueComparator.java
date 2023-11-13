package ccm.server.comparers;

import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;

import java.util.Comparator;

public class IRelOrderValueComparator implements Comparator<IObject> {
    @Override
    public int compare(IObject o1, IObject o2) {
        if (o1 != null && o2 != null) {
            Integer int1 = o1.toInterface(IRel.class).OrderValue();
            Integer int2 = o2.toInterface(IRel.class).OrderValue();
            if (int1 != null && int2 != null)
                return int1.compareTo(int2);
        }
        return -1;
    }
}
