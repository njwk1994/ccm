package ccm.server.comparator;

import ccm.server.module.service.base.IUtilityService;

import java.util.Date;

public interface IValueComparatorService extends IUtilityService {

    boolean lessThan(Object value1, Object value2, boolean allowEqual);

    boolean largeThan(Object value1, Object value2, boolean allowEqual);

    boolean equal(Object value1, Object value2);

    boolean equal(Date date1, Date date2);

    boolean equal(Double double1, Double double2);

    boolean lessThan(Date value1, Date value2, boolean allowEqual);

    boolean largeThan(Date value1, Date value2, boolean allowEqual);

    boolean lessThan(Double value1, Double value2, boolean allowEqual);

    boolean largeThan(Double value, Double value2, boolean allowEqual);

    boolean between(Object value, Object value1, Object value2);

    boolean between(Double value, Double value1, Double value2);

    boolean between(Date value, Date value1, Date value2);

    boolean like(Object value, Object valuePattern);
}
