package ccm.server.schema.interfaces;

import ccm.server.args.*;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.ObjectXmlDTO;
import ccm.server.entity.MetaDataObj;
import ccm.server.enums.objectUpdateState;
import ccm.server.schema.collections.IInterfaceCollection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.model.ClassBase;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface IObject extends Serializable {

    String getConfigForMetaData();

    void amountDate(Date date, String userName) throws Exception;

    IObject copyTo() throws Exception;

    String UID();

    List<String> getPropertiesThatCannotBeUpdated();

    String generateDisplayAs();

    void fillingProperties(@NotNull List<ObjectItemDTO> pcolProperties, boolean pblnNeedUpdateDesc) throws Exception;

    void fillingInterfaceAndProperties(Map<String, Map<String, String>> interfaceProperties) throws Exception;

    void fillingInterfaces(String[] parrInterfaceDefs) throws Exception;

    void fillingInterfaces(@NotNull JSONObject obj) throws Exception;

    void fillingProperties(boolean ignoreNullOrEmptyProperty, List<Map.Entry<String, Object>> properties) throws Exception;

    void fillingProperties(@NotNull JSONObject object) throws Exception;

    void fillingForObjectDTO(ObjectDTO objectDTO) throws Exception;

    void setUID(String uid) throws Exception;

    String Name();

    void setName(String name) throws Exception;

    String Description();

    void setDescription(String description) throws Exception;

    boolean fromDb();

    String Config();

    boolean underConfigInd(String configurationUid);

    void setConfig(String config) throws Exception;

    String ContainerID();

    void setContainerID(String containerId) throws Exception;

    Date CreationDate();

    void setCreationDate(Date date) throws Exception;

    String CreationUser();

    void setCreationUser(String creationUser) throws Exception;

    String DomainUID();

    void setDomainUID(String domainUid) throws Exception;

    Date LastUpdateDate();

    void setLastUpdateDate(Date date) throws Exception;

    String LastUpdateUser();

    void setLastUpdateUser(String lastUpdateUser) throws Exception;

    String OBID();

    void setOBID(String obid) throws Exception;

    String TerminationUser();

    void setTerminationUser(String terminationUser) throws Exception;

    Date TerminationDate();

    void setTerminationDate(Date terminationDate) throws Exception;

    String UniqueKey();

    void setUniqueKey(String uniqueKey) throws Exception;

    IInterfaceCollection Interfaces();

    String ClassDefinitionUID();

    boolean IsInitialized();

    IObjectCollection ParentCollection();

    boolean InstantiateRequiredItems();

    boolean Deleted();

    boolean Terminated();

    ReentrantReadWriteLock Lock();

    boolean CanUpdate();

    objectUpdateState ObjectUpdateState();

    IClassDef ClassDefinition() throws Exception;

    IRelCollection GetEnd1Relationships() throws Exception;

    IRelCollection GetEnd2Relationships() throws Exception;

    void commit();

    IObject Copy() throws Exception;

    void setClassDefinitionUID(String classDefinitionUid) throws Exception;

    void Delete() throws Exception;

    void Delete(boolean pblnSuppressEvent) throws Exception;

    void doDelete() throws Exception;

    void setParentCollection(IObjectCollection parentCollection);

    void Terminate() throws Exception;

    void Terminate(boolean pblnSuppressEvent) throws Exception;

    boolean Validate() throws Exception;

    void BeginUpdate() throws Exception;

    void BeginUpdate(boolean pblnValidateForClaim) throws Exception;

    void BeginUpdate(boolean pblnValidateForClaim, boolean pblnSuppressEvents) throws Exception;

    void rollback() throws Exception;

    IInterface toInterface(String interfaceDefUID) throws Exception;

    IInterface myNext(String interfaceDefinitionUid, List<String> processedInterfaceDefs);

    <T> T toInterface(Class<T> tClass);

    boolean IsTypeOf(String interfaceDefUID);

    void FinishUpdate() throws Exception;

    IRelCollection getRels(String relOfEdgeDefUID) throws Exception;

    String GetIconName() throws Exception;

    String OnGetIconNamePrefix() throws Exception;

    String OnGetIconNameSuffix() throws Exception;

    ICIMConfigurationItem getConfig() throws Exception;

    boolean IsUniqueKeyUniqueInConfig() throws Exception;

    boolean IsUniqueKeyUnique() throws Exception;

    void UniqueKeyValidation(cancelArgs cancelArgs) throws Exception;

    boolean IsUniqueChecksOnOBIDAndUpdateState(ArrayList<String> parrOBIDs) throws Exception;

    IProperty getProperty(String interfaceDefinitionUID, String propertyDefinitionUID);

    IProperty getProperty(String propertyDefinitionUID);

    void OnCreate(createArgs e) throws Exception;

    void OnCreating(cancelArgs e) throws Exception;

    void OnCreatingValidation(cancelArgs e) throws Exception;

    void OnCreated(suppressibleArgs e) throws Exception;

    void OnCopy(copyArgs e) throws Exception;

    void OnCopies(copyArgs e) throws Exception;

    void OnCopying(cancelArgs e) throws Exception;

    void OnDelete(suppressibleArgs e) throws Exception;

    void OnDeleting(cancelArgs e) throws Exception;

    void OnDeleted(suppressibleArgs e) throws Exception;

    void OnPreProcess(createArgs e) throws Exception;

    void OnRelationshipAdd(relArgs e) throws Exception;

    void OnRelationshipAdded(relArgs e) throws Exception;

    void OnRelationshipAdding(relArgs e) throws Exception;

    void OnRelationshipRemoved(relArgs e) throws Exception;

    void OnRelationshipRemoving(relArgs e) throws Exception;

    void OnRelationshipUpdating(relArgs e) throws Exception;

    void OnTerminate(suppressibleArgs e) throws Exception;

    void OnTerminating(cancelArgs e) throws Exception;

    void OnTerminated(suppressibleArgs e) throws Exception;

    void OnUpdate(updateArgs e) throws Exception;

    void OnUpdated(suppressibleArgs e) throws Exception;

    void OnUpdating(cancelArgs e) throws Exception;

    void OnUpdatingValidation(cancelArgs e) throws Exception;

    void OnValidate(validateArgs e) throws Exception;

    void clearAllProperties();

    void resetWithProvidedIObjectAsNewCache(IObject iObject);

    void selfCheck() throws Exception;

    MetaDataObj toMetaDataObject() throws Exception;

    IObjectCollection toIObjectCollection();

    ObjectDTO toObjectDTO() throws Exception;

    ObjectXmlDTO toObjectXmlDTO() throws Exception;

    String toXml() throws Exception;

    String toErrorPop();

    String getDisplayValue(String propertyDefinitionUID);

    String getValue(String propertyDefinitionUID);

    void setValue(String propertyDefinitionUID, Object value) throws Exception;

    void refreshObjectDTO(ObjectDTO objectDTO) throws Exception;

    boolean checkObjectSameAsOtherObj(IObject pobjOtherObj);

    ClassBase ClassBase();
}
