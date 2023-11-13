package ccm.server.comparers;

import ccm.server.context.CIMContext;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IProperty;
import ccm.server.utils.ValueConversionUtility;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Date;

@Slf4j
public class IObjectComparator implements Comparator<IObject> {
    private String interfaceDefinitionUID;
    private String propertyDefinitionUID;

    public IObjectComparator(String interfaceDefinitionUID, String propertyDefinitionUID) {
        this.interfaceDefinitionUID = interfaceDefinitionUID;
        this.propertyDefinitionUID = propertyDefinitionUID;
    }

    @SneakyThrows
    @Override
    public int compare(IObject o1, IObject o2) {
        IObject scopedByForPropertyDefinition = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(this.propertyDefinitionUID);
        if (scopedByForPropertyDefinition == null)
            throw new Exception("invalid property definition as property type is null for " + this.propertyDefinitionUID);
        if (o1 == null || o2 == null)
            return -1;
        IProperty property1 = o1.getProperty(this.interfaceDefinitionUID, this.propertyDefinitionUID);
        IProperty property2 = o2.getProperty(this.interfaceDefinitionUID, this.propertyDefinitionUID);
        if (property1 == null || property2 == null)
            return -1;
        switch (scopedByForPropertyDefinition.Name()) {
            case "IntegerType":
            case "DoubleType":
                return Double.compare(ValueConversionUtility.toDouble(property1), ValueConversionUtility.toDouble(property2));
            case "YMDType":
                Date date1 = ValueConversionUtility.toYMD(property1);
                Date date2 = ValueConversionUtility.toYMD(property2);
                if (date1 == null && date2 == null)
                    return 0;
                else if (date1 == null || date2 == null)
                    return -1;
                else
                    return date1.compareTo(date2);
            case "DateTimeType":
                date1 = ValueConversionUtility.toYMD(property1);
                date2 = ValueConversionUtility.toYMD(property2);
                if (date1 == null && date2 == null)
                    return 0;
                else if (date1 == null || date2 == null)
                    return -1;
                else
                    return date1.compareTo(date2);
            case "BooleanType":
                return Boolean.compare(ValueConversionUtility.toBoolean(property1), ValueConversionUtility.toBoolean(property2));
        }
        Object value1 = property1.Value();
        Object value2 = property2.Value();
        if (value1 == null && value2 == null)
            return 0;
        if (value1 == null || value2 == null)
            return -1;
        return value1.toString().compareTo(value2.toString());
    }
}
