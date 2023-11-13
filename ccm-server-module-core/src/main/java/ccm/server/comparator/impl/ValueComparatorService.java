package ccm.server.comparator.impl;

import ccm.server.comparator.IValueComparatorService;
import ccm.server.convert.IValueConvertService;
import ccm.server.module.impl.general.UtilityServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service("valueComparatorService")
@Slf4j
public class ValueComparatorService extends UtilityServiceImpl implements IValueComparatorService {
    @Autowired
    private IValueConvertService converterService;

    public static ValueComparatorService Instance;

    @PostConstruct
    public void doInit() {
        Instance = this;
        Instance.converterService = this.converterService;
    }

    @Override
    public boolean lessThan(Object value1, Object value2, boolean allowEqual) {
        if (value1 == null && value2 == null) {
            return allowEqual;
        }
        if (value1 == null || value2 == null)
            return false;
        Date date1 = this.converterService.DateTime(value1);
        Date date2 = this.converterService.DateTime(value2);
        if (date1 != null && date2 != null)
            return this.lessThan(date1, date2, allowEqual);

        Double double1 = this.converterService.Double(value1);
        Double double2 = this.converterService.Double(value2);
        if (double1 != null && double2 != null)
            return this.lessThan(double1, double2, allowEqual);
        if (allowEqual) {
            return value1.toString().length() <= value2.toString().length();
        } else
            return value1.toString().length() < value2.toString().length();
    }

    @Override
    public boolean lessThan(Date value1, Date value2, boolean allowEqual) {
        if (value1 == null && value2 == null) {
            return allowEqual;
        }
        if (value1 == null || value2 == null)
            return false;
        int compareTo = value1.compareTo(value2);
        if (allowEqual) {
            return compareTo <= 0;
        } else
            return compareTo < 0;
    }

    @Override
    public boolean largeThan(Date value1, Date value2, boolean allowEqual) {
        if (value1 == null && value2 == null) {
            return allowEqual;
        }
        if (value1 == null || value2 == null)
            return false;
        int compareTo = value1.compareTo(value2);
        if (allowEqual) {
            return compareTo >= 0;
        } else
            return compareTo > 0;
    }

    @Override
    public boolean lessThan(Double value1, Double value2, boolean allowEqual) {
        if (value1 == null && value2 == null) {
            return allowEqual;
        }
        if (value1 == null || value2 == null)
            return false;
        int compareTo = value1.compareTo(value2);
        if (allowEqual) {
            return compareTo <= 0;
        } else
            return compareTo < 0;

    }

    @Override
    public boolean largeThan(Double value1, Double value2, boolean allowEqual) {
        if (value1 == null && value2 == null) {
            return allowEqual;
        }
        if (value1 == null || value2 == null)
            return false;
        int compareTo = value1.compareTo(value2);
        if (allowEqual) {
            return compareTo >= 0;
        } else
            return compareTo > 0;
    }

    @Override
    public boolean between(Object value, Object value1, Object value2) {
        if (value != null && value1 != null && value2 != null) {
            Date date = this.converterService.DateTime(value);
            Date date1 = this.converterService.DateTime(value1);
            Date date2 = this.converterService.DateTime(value2);
            boolean result = this.between(date, date1, date2);
            if (!result) {
                Double dbl = this.converterService.Double(value);
                Double dbl1 = this.converterService.Double(value1);
                Double dbl2 = this.converterService.Double(value2);
                result = this.between(dbl, dbl1, dbl2);
            }
            if (!result) {
                result = value.toString().length() >= value1.toString().length() && value2.toString().length() >= value.toString().length();
            }
            return result;

        }
        return false;
    }

    @Override
    public boolean between(Double value, Double value1, Double value2) {
        if (value1 != null && value2 != null) {
            return value.compareTo(value1) >= 0 && value2.compareTo(value) >= 0;
        }
        return false;
    }

    @Override
    public boolean between(Date value, Date value1, Date value2) {
        if (value != null && value1 != null && value2 != null) {
            return value.compareTo(value1) >= 0 && value2.compareTo(value) >= 0;
        }
        return false;
    }

    @Override
    public boolean like(Object value, Object valuePattern) {
        if (value != null && valuePattern != null) {
            String[] patterns = valuePattern.toString().split(StringPool.PERCENT);
            int position = -1;
            String valueString = value.toString();
            for (String pattern : patterns) {
                if (pattern.length() > 0 && !StringUtils.isEmpty(pattern)) {
                    int currentPosition = valueString.toUpperCase().indexOf(pattern.toUpperCase());
                    if (currentPosition > position) {
                        valueString = valueString.substring(currentPosition + pattern.length());
                        position = currentPosition;
                    } else
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean largeThan(Object value1, Object value2, boolean allowEqual) {
        if (value1 == null && value2 == null) {
            return allowEqual;
        }
        if (value1 == null || value2 == null)
            return false;
        Date date1 = this.converterService.DateTime(value1);
        Date date2 = this.converterService.DateTime(value2);
        if (date1 != null && date2 != null)
            return this.largeThan(date1, date2, allowEqual);

        Double double1 = this.converterService.Double(value1);
        Double double2 = this.converterService.Double(value2);
        if (double1 != null && double2 != null)
            return this.largeThan(double1, double2, allowEqual);

        if (allowEqual) {
            return value1.toString().length() >= value2.toString().length();
        } else
            return value1.toString().length() > value2.toString().length();
    }

    @Override
    public boolean equal(Object value1, Object value2) {
        if (value1 != null && value2 != null) {
            Date date1 = this.converterService.DateTime(value1);
            Date date2 = this.converterService.DateTime(value2);
            boolean result = this.equal(date1, date2);
            if (!result) {
                Double double1 = this.converterService.Double(value1);
                Double double2 = this.converterService.Double(value2);
                result = this.equal(double1, double2);
            }
            if (!result)
                result = value1.toString().equalsIgnoreCase(value2.toString());
            return result;
        }
        return false;
    }

    @Override
    public boolean equal(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.compareTo(date2) == 0;
        }
        return false;
    }

    @Override
    public boolean equal(Double double1, Double double2) {
        if (double1 != null && double2 != null) {
            return double1.compareTo(double2) == 0;
        }
        return false;
    }

}
