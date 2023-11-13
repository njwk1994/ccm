package ccm.server.schema.collections;


import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.IPropertyValue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface IPropertyCollection {

    List<IProperty> toArrayList();

    IProperty get(String propertyDefinitionUid);

    IProperty item(String propertyDefUid) throws Exception;

    IProperty item(String propertyDefUid, boolean activate) throws Exception;

    void add(IProperty property);

    void addDynamical(String propertyDefinitionUID, Object value);

    void add(Collection<IProperty> propertyCollection);

    void clear();

    IPropertyValue CurrentValue(String propertyDefUID);

    IInterface getInterface();

    void remove(IProperty property);

    void remove(String propertyDefUid);

    boolean hasProperty(String propertyDefinition);

    boolean hasProperty();

    Iterator<Map.Entry<String, IProperty>> GetEnumerator();


}
