package ccm.server.enums;


import ccm.server.convert.impl.ValueConvertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;

@Slf4j
public enum propertyValueType {
    StringType(ccm.server.enums.controlType.TextBox),
    BooleanType(ccm.server.enums.controlType.CheckBox),
    IntegerType(ccm.server.enums.controlType.NumericBox),
    DoubleType(ccm.server.enums.controlType.NumericWithDotBox),
    DateTimeType(ccm.server.enums.controlType.DateTimePicker),
    YMDType(ccm.server.enums.controlType.DatePicker),
    EnumListType(ccm.server.enums.controlType.ComboBox),
    EnumListLevelType(ccm.server.enums.controlType.DependentComboBox),
    UoMListType(ccm.server.enums.controlType.TextBoxWithComboBox),
    SingleRelationship(ccm.server.enums.controlType.ComboBox),
    Relationship(ccm.server.enums.controlType.MultiComboBox),
    SearchSingleRelationship(ccm.server.enums.controlType.SearchSingleComboBox),
    SearchRelationship(ccm.server.enums.controlType.SearchMultiComboBox),
    Invalid(null);

    private controlType controlType;
    private String uid;

    propertyValueType(controlType controlType) {
        this.controlType = controlType;
        this.setUid("propertyValueType_" + this.toString());
    }

    public int compareTo(Object value1, Object value2) {
        if (value1 == null && value2 == null)
            return 0;
        else if (value1 != null && value2 != null) {
            switch (this) {
                case IntegerType:
                    return ((Integer) value1).compareTo((Integer) value2);
                case BooleanType:
                    return ((Boolean) value1).compareTo((Boolean) value2);
                case DoubleType:
                    return ((Double) value1).compareTo((Double) value2);
                case YMDType:
                case DateTimeType:
                    return ((Date) value1).compareTo((Date) value2);
                default:
                    return value1.toString().compareTo(value2.toString());
            }
        }
        return -1;
    }

    public Object parseValue(Object value) {
        switch (this) {
            case DateTimeType:
                return ValueConvertService.Instance.DateTime(value);
            case YMDType:
                return ValueConvertService.Instance.YMD(value);
            case DoubleType:
                return ValueConvertService.Instance.Double(value);
            case BooleanType:
                return ValueConvertService.Instance.Boolean(value);
            case IntegerType:
                return ValueConvertService.Instance.Integer(value);
            default:
                return value != null ? value.toString() : "";
        }
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    public static String[] dateValueTypes() {
        return new String[]{propertyValueType.DateTimeType.toString(), propertyValueType.YMDType.toString()};
    }

    public static String[] numericValueTypes() {
        return new String[]{propertyValueType.IntegerType.toString(), propertyValueType.DoubleType.toString()};
    }

    public static Boolean isNumericValueType(String valueType) {
        if (!StringUtils.isEmpty(valueType)) {
            return Arrays.stream(numericValueTypes()).anyMatch(c -> c.equalsIgnoreCase(valueType));
        }
        return false;
    }

    public static Boolean isDateValueType(String valueType) {
        if (!StringUtils.isEmpty(valueType)) {
            return Arrays.stream(dateValueTypes()).anyMatch(c -> c.equalsIgnoreCase(valueType));
        }
        return false;
    }

    public controlType getControlType() {
        return this.controlType;
    }

    public void setControlType(controlType controlType) {
        this.controlType = controlType;
    }

    public static propertyValueType toEnum(String value) {
        propertyValueType result = null;
        try {
            result = propertyValueType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            log.error(exception.getMessage());
        }
        return result;
    }
}
