package ccm.server.schema.collections;


import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.enums.queryTypes;
import ccm.server.models.LiteObject;
import ccm.server.models.page.PageResult;
import ccm.server.schema.interfaces.IObject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface IObjectCollection {

    ObjectDTOCollection toObjectDTOCollection() throws Exception;

    void retrievePageResultInformation(PageResult<LiteObject> pageResult);

    PageResult<IObject> PageResult();

    void sortByObidList(List<String> obids);

    Comparator<IObject> Comparator();

    <T> List<T> toList(Class<T> clazz);

    boolean Sorted();

    void Delete() throws Exception;

    Map<String, IObjectCollection> mapByClassDefOrRelDef();

    IObjectCollection excludeFrom(IObjectCollection items);

    IObjectCollection itemsByOBIDs(List<String> pcolOBIDs);

    void replace(IObject object);

    ReentrantReadWriteLock Lock();

    int size();

    List<IObject> toList();

    void setSorted(boolean sorted);

    IObject Parent();

    void Commit() throws Exception;

    void setParent(IObject o);

    IObjectCollection ParentCollection();

    void setParentCollection(IObjectCollection parentCollection);

    boolean IsParentACollection();

    IObjectCollection copy();

    IObject itemByOBID(String pstrOBID);

    IObject get(int index);

    IObject item(String uid);

    IObject item(String uid, String domainUID);

    IObject item(String uid, String domainUID, Date terminationDate);

    IObjectCollection getItemsByInterfaceDefUID(@NotNull String pstrInterfaceDefUID);

    IObject get(queryTypes queryTypes, Object value);

    IObjectCollection Items(String classDefinitionUID);

    IRelCollection GetEnd1Relationships();

    IRelCollection GetEnd2Relationships();

    IObjectCollection getByClassDefinitionUids(String... classDefinitionUids);

    void append(IObject object);

    void onAdd(IObject iObject, String obid, String uid);

    void addRange(IObjectCollection collection);

    void addRange(Collection<IObject> collection);

    void addRangeUniquely(IObjectCollection collection);

    void addRangeUniquely(IObject o);

    IObjectCollection addItems(IObjectCollection value);

    void remove(String obid);

    void remove(IObject iObject);

    boolean contains(IObject o);

    boolean containsByOBID(String pstrOBID);

    boolean containsByUID(String pstrUID);

    boolean contains(String uid);

    boolean contains(String uid, String domainUID);

    boolean contains(String uid, String domainUID, Date terminationDate);

    boolean validate() throws Exception;

    Iterator<IObject> GetEnumerator();

    void sort();

    void sort(String interfaceDefUID, String propertyDefUID);

    void sorting(Comparator<IObject> comparator);

    boolean hasValue();

    int Size();

    IObject firstOrDefault();

    void clear();

    IObjectCollection createCollection();

    void commit() throws Exception;

    List<ObjectDTO> toObjectDTOs() throws Exception;

    List<String> listOfOBID();


}
