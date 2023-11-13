package ccm.server.schema.model;

import ccm.server.enums.propertyValueUpdateState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class PropertyValueCollection implements IPropertyValueCollection {

    private IProperty parent;

    private final CopyOnWriteArrayList<IPropertyValue> mcolValueCollections = new CopyOnWriteArrayList<>();

    public IProperty getParent() {
        return this.parent;
    }

    public PropertyValueCollection(IProperty property) {
        this.parent = property;
    }

    @Override
    public IPropertyValue value(int index) {
        if (this.mcolValueCollections.size() > index)
            return this.mcolValueCollections.get(index);
        return null;
    }

    @Override
    public void setValue(int index, IPropertyValue propertyValue) {
        if (this.mcolValueCollections.size() > index)
            this.mcolValueCollections.add(index, propertyValue);
    }

    @Override
    public IPropertyValue latestValue() {
        if (this.mcolValueCollections.size() > 0)
            return this.mcolValueCollections.get(0);
        log.trace(this.getParent().getPropertyDefinitionUid() + " ** latest value is NULL as value collection is NULL");
        return null;
    }

    @Override
    public int add(IPropertyValue propertyValue) {
        if (propertyValue != null) {
            if (this.size() == 1 && (StringUtils.isEmpty(this.value(0).OBID()) || this.value(0).isTemporaryValue()))
                this.mcolValueCollections.remove(0);
            this.mcolValueCollections.add(propertyValue);
            return this.mcolValueCollections.size() - 1;
        }
        return -1;
    }

    @Override
    public void copyTo(IPropertyValue[] sourceValue, int startIndex) {
        if (sourceValue == null)
            sourceValue = new PropertyValue[this.size()];
        if (startIndex < 0)
            startIndex = 0;
        for (int i = startIndex; i < this.size(); i++) {
            sourceValue[i] = this.mcolValueCollections.get(i);
        }
    }

    @Override
    public void clear() {
        this.mcolValueCollections.clear();
    }

    @Override
    public int size() {
        return this.mcolValueCollections.size();
    }

    @Override
    public void remove(IPropertyValue propertyValue) {
        this.mcolValueCollections.remove(propertyValue);
    }

    @Override
    public IPropertyValue add(Object value, double pdblFloatValue, String pstrUoM, String pstrOBID, propertyValueUpdateState propertyValueUpdateState) {
        return null;
    }

    @Override
    public Iterator<IPropertyValue> GetEnumerator() {
        return this.mcolValueCollections.iterator();
    }
}
