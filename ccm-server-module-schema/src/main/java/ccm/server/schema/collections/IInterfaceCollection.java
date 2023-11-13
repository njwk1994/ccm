package ccm.server.schema.collections;

import ccm.server.schema.model.ClassBase;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface IInterfaceCollection {
    
    boolean AllowSorting();

    List<IInterface> toArrayList();
    
    void setAllowSorting(boolean allowSorting);
    
    ClassBase GetClass();
    
    IInterface get(int index);
    
    IInterface get(String interfaceDefinitionUid);
    
    IInterface item(String interfaceDefinitionUid) throws Exception;
    
    IInterface item(String interfaceDefinitionUid, boolean activate) throws Exception;
    
    IInterface item(String interfaceDefinitionUid, boolean activate, boolean instantiateRequiredItems) throws Exception;
    
    void add(IInterface objectInterface) throws Exception;

    IInterface addDynInterface(String interfaceDefinitionUID) ;
    
    void add(IInterface objectInterface, boolean sortOrNot) throws Exception;
    
    String getPropertyValue(String interfaceDefinitionUid, String propertyDefinitionUid) ;
    
    IProperty getProperty(String interfaceDefinitionUid, String propertyDefinitionUid);
    
    IProperty getProperty(String propertyDefinitionUID);
    
    boolean hasInterface(String interfaceDefinitionUid);
    
    Iterator<Map.Entry<String, IInterface>> GetEnumerator();
    
    int indexOf(IInterface i);
    
    void remove(IInterface i);
    
    void sort();
    
    int size();

    boolean hasInterface();
}
