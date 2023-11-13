package ccm.server.schema.model;

import ccm.server.enums.propertyValueUpdateState;

import java.util.Iterator;

public interface IPropertyValueCollection {
    IPropertyValue value(int index);

    void setValue(int index, IPropertyValue propertyValue);

    IPropertyValue latestValue();

    int add(IPropertyValue propertyValue);

    void copyTo(IPropertyValue[] sourceValue,int startIndex);

    void clear();

    int size();

    void remove(IPropertyValue propertyValue);

    IPropertyValue add(Object value, double pdblFloatValue, String pstrUoM, String pstrOBID, propertyValueUpdateState propertyValueUpdateState);

    Iterator<IPropertyValue> GetEnumerator();
}
