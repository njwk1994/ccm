package ccm.server.utils;

import ccm.server.context.CIMContext;
import ccm.server.convert.impl.ValueConvertService;
import ccm.server.enums.classDefinitionType;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IEnumEnum;
import ccm.server.schema.interfaces.IEnumListType;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IProperty;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Iterator;

@Slf4j
public class ValueConversionUtility {
    public static Boolean toBoolean(Object value) {
        if (value != null && !StringUtils.isEmpty(value)) {
            try {
                if (value instanceof String) {
                    if (value.equals("是") || ((String) value).equalsIgnoreCase("true")) {
                        value = "true";
                    } else {
                        value = "false";
                    }
                }
                return Boolean.parseBoolean(value.toString());
            } catch (Exception exception) {
                log.error(exception.getMessage());
                Integer integer = toInteger(value);
                return integer != 0;
            }
        }
        return false;
    }

    public static boolean toBoolean(IProperty property) {
        Object value = property != null ? property.Value() : "";
        return toBoolean(value);
    }

    public static String toString(IProperty property) {
        if (property != null) {
            Object value = property.Value();
            if (value != null) {
                if (value instanceof Date) {
                    IObject scopedBy = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(property.getPropertyDefinitionUid());
                    if (scopedBy.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyType.toString())) {
                        switch (scopedBy.Name()) {
                            case "DateTimeType":
                                return DateUtils.formatDate((Date) value, "yyyy-MM-dd HH:mm:ss");
                            case "YMDType":
                                return DateUtils.formatDate((Date) value, "yyyy-MM-dd");
                        }
                    }
                }
                return value.toString();
            }
        }
        return "";
    }

    public static Double toDouble(Object value) {
        if (value != null && !StringUtils.isEmpty(value)) {
            try {
                return Double.parseDouble(value.toString());
            } catch (Exception exception) {
                log.warn(exception.getMessage());
            }
        }
        return 0.0;
    }

    public static double toDouble(IProperty property) {
        Object value = property != null ? property.Value() : "";
        return toDouble(value);
    }

    public static Date toDateTime(IProperty property) {
        Object value = property != null ? property.Value() : null;
        if (value != null) {
            if (value instanceof Date)
                return (Date) value;
            else
                return ValueConvertService.Instance.Date(value.toString());
        }
        return null;
    }

    public static Date toYMD(IProperty property) {
        Object value = property != null ? property.Value() : null;
        if (value != null) {
            if (value instanceof Date)
                return (Date) value;
            else
                return ValueConvertService.Instance.YMD(value.toString());
        }
        return null;
    }

    public static Integer toInteger(Object value) {
        if (value != null && !StringUtils.isEmpty(value)) {
            try {
                return Double.valueOf(value.toString()).intValue();
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
        return -1;
    }

    public static int toInteger(IProperty property) {
        Object value = property != null ? property.Value() : "";
        return toInteger(value);
    }

    public static String toEnumUID(@NotNull Object propValue, @NotNull IEnumListType enumListType) throws Exception {
        IObjectCollection entries = enumListType.getEntries();
        if (SchemaUtility.hasValue(entries)) {
            Iterator<IObject> e = entries.GetEnumerator();
            while (e.hasNext()) {
                IEnumEnum enumEnum = e.next().toInterface(IEnumEnum.class);
                if (enumEnum.isHint(propValue.toString())) {
                    return enumEnum.UID();
                }
            }
        }
        return "";
    }
}
