package ccm.server.schema.model;

import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.models.LiteObject;
import ccm.server.schema.collections.IInterfaceCollection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.InterfaceCollection;
import ccm.server.schema.collections.impl.RelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.interfaces.defaults.IObjectDefault;
import ccm.server.schema.model.pointer.MethodPointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class ClassBase implements Serializable {
    private static final long serializableId = 1L;

    private String classDefinitionUid;
    private IRelCollection end1Relationships;
    private IRelCollection end2Relationships;
    private final Boolean instantiateRequiredItems;
    private final IInterfaceCollection interfaces = new InterfaceCollection(this);
    private boolean createdAndTerminated = false;
    private objectUpdateState updateState;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private IObjectCollection parentCollection;
    private final Stack<MethodPointer> mobjDeferredCancelMethods = new Stack<>();
    private final Stack<MethodPointer> mobjDeferredCompletedMethods = new Stack<>();
    private final Stack<MethodPointer> mobjDeferredPostProcessedMethods = new Stack<>();
    private final Stack<MethodPointer> mobjDeferredPreProcessMethods = new Stack<>();
    private final Stack<MethodPointer> mobjDeferredProcessMethods = new Stack<>();
    private final Stack<MethodPointer> mobjDeferredProcessedMethods = new Stack<>();

    public void clearDeferredInfo() {
        this.mobjDeferredCancelMethods.clear();
        this.mobjDeferredCompletedMethods.clear();
        this.mobjDeferredPostProcessedMethods.clear();
        this.mobjDeferredPreProcessMethods.clear();
        this.mobjDeferredProcessMethods.clear();
        this.mobjDeferredProcessedMethods.clear();
    }

    public Stack<MethodPointer> CancelMethods() {
        return this.mobjDeferredCancelMethods;
    }

    public Stack<MethodPointer> CompletedMethods() {
        return this.mobjDeferredCompletedMethods;
    }

    public Stack<MethodPointer> PostProcessedMethods() {
        return this.mobjDeferredPostProcessedMethods;
    }

    public Stack<MethodPointer> PreProcessMethods() {
        return this.mobjDeferredPreProcessMethods;
    }

    public Stack<MethodPointer> ProcessMethods() {
        return this.mobjDeferredProcessMethods;
    }

    public Stack<MethodPointer> ProcessedMethods() {
        return this.mobjDeferredProcessedMethods;
    }

    public void addCancelMethod(IObject iObject, String pstrInterfaceDef, String pstrMethodName, Object[] parrArgs) {
        this.addCancelMethod(new MethodPointer(iObject, pstrInterfaceDef, pstrMethodName, parrArgs));
    }

    public void addCancelMethod(MethodPointer methodPointer) {
        if (methodPointer != null)
            this.mobjDeferredCancelMethods.push(methodPointer);
    }

    public void addCompletedMethod(IObject iObject, String pstrInterfaceDef, String pstrMethodName, Object[] parrArgs) {
        this.addCompletedMethod(new MethodPointer(iObject, pstrInterfaceDef, pstrMethodName, parrArgs));
    }

    public void addCompletedMethod(MethodPointer methodPointer) {
        if (methodPointer != null) {
            methodPointer.setUseToInterface(true);
            this.mobjDeferredCompletedMethods.push(methodPointer);
        }
    }

    public void addPostProcessMethod(IObject iObject, String pstrInterfaceDef, String pstrMethod, Object[] parrArgs) {
        this.addPostProcessMethod(iObject, pstrInterfaceDef, pstrMethod, parrArgs, true);
    }

    public void addPostProcessMethod(IObject iObject, String pstrInterfaceDef, String pstrMethod, Object[] parrArgs, boolean pblnUseToInterface) {
        MethodPointer methodPointer = new MethodPointer(iObject, pstrInterfaceDef, pstrMethod, parrArgs);
        methodPointer.setUseToInterface(pblnUseToInterface);
        this.addPostProcessMethod(methodPointer);
    }

    public void addPostProcessMethod(MethodPointer methodPointer) {
        if (methodPointer != null)
            this.mobjDeferredPostProcessedMethods.push(methodPointer);
    }

    public void addPreProcessMethod(IObject iObject, String pstrInterfaceDef, String pstrMethod, Object[] parrArgs) {
        MethodPointer methodPointer = new MethodPointer(iObject, pstrInterfaceDef, pstrMethod, parrArgs);
        this.addPostProcessMethod(methodPointer);
    }

    public void addPreProcessMethod(MethodPointer methodPointer) {
        if (methodPointer != null)
            this.mobjDeferredPreProcessMethods.push(methodPointer);
    }

    public void addProcessMethods(IObject iObject, String pstrInterfaceDef, String pstrMethod, Object[] parrArgs) {
        MethodPointer methodPointer = new MethodPointer(iObject, pstrInterfaceDef, pstrMethod, parrArgs);
        this.addProcessMethods(methodPointer);
    }

    public void addProcessMethods(MethodPointer methodPointer) {
        if (methodPointer != null)
            this.mobjDeferredProcessMethods.push(methodPointer);
    }

    public void addProcessedMethods(IObject iObject, String pstrInterfaceDef, String pstrMethod, Object[] parrArgs, boolean pblnUseToInterface) {
        MethodPointer methodPointer = new MethodPointer(iObject, pstrInterfaceDef, pstrMethod, parrArgs);
        methodPointer.setUseToInterface(pblnUseToInterface);
        this.addProcessedMethods(methodPointer);
    }

    public void addProcessedMethods(IObject iObject, String pstrInterfaceDef, String pstrMethod, Object[] parrArgs) {
        this.addProcessedMethods(iObject, pstrInterfaceDef, pstrMethod, parrArgs, true);
    }

    public void addProcessedMethods(MethodPointer methodPointer) {
        if (methodPointer != null)
            this.mobjDeferredProcessedMethods.push(methodPointer);
    }


    public IObjectCollection ParentCollection() {
        return this.parentCollection;
    }

    public objectUpdateState UpdateState() {
        return this.updateState;
    }

    public IInterfaceCollection Interfaces() {
        return this.interfaces;
    }

    public IInterfaceCollection SortedInterfaces() {
        return this.interfaces;
    }

    public ReentrantReadWriteLock Lock() {
        return this.lock;
    }

    public IObject IObject() throws Exception {
        return this.Interfaces().item(interfaceDefinitionType.IObject.toString());
    }

    public String ClassDefinitionUID() {
        return this.classDefinitionUid;
    }

    public boolean CreatedAndTerminated() {
        return this.createdAndTerminated;
    }

    public boolean ClassDefinitionUIDIsSet() throws Exception {
        IInterface item = this.interfaces.item(interfaceDefinitionType.IObject.toString(), false);
        if (item != null)
            return !StringUtils.isEmpty(item.ClassDefinitionUID());
        return false;
    }

    public IClassDef ClassDefinition() throws Exception {
        return CIMContext.Instance.ProcessCache().item(this.ClassDefinitionUID(), domainInfo.SCHEMA.toString(), true
        ).toInterface(IClassDef.class);
    }

    private void ForceParentKeyGeneration(IObject parent, String propertyDefUID) throws Exception {
        String lstrKeyDefinition = "";
        String parentUID = "";
        boolean lblnUID = true;
        if (!propertyDefUID.equalsIgnoreCase(propertyDefinitionType.UID.toString())) {
            if (propertyDefUID.equalsIgnoreCase(propertyDefinitionType.UniqueKey.toString())) {
                lblnUID = false;
                lstrKeyDefinition = parent.ClassDefinition().UniqueKeyPattern();
                parentUID = parent.UniqueKey();
            }
        } else {
            lstrKeyDefinition = parent.ClassDefinition().SystemIDPattern();
            parentUID = parent.UID();
        }
        if (!StringUtils.isEmpty(lstrKeyDefinition) && (StringUtils.isEmpty(parentUID) || !parentUID.contains("_"))) {
            if (lblnUID)
                parent.Interfaces().item(interfaceDefinitionType.IObject.toString()).ClassBase().generateUID();
            else
                parent.Interfaces().item(interfaceDefinitionType.IObject.toString()).ClassBase().generateUniqueKey();
        }
    }

    protected boolean hasProperty(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                IInterface anInterface = entryIterator.next().getValue();
                if (anInterface.Properties().hasProperty(propertyDefinitionUID))
                    return true;
            }
        }
        return false;
    }

    protected String getPropertyValue(String propertyDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                IInterface anInterface = entryIterator.next().getValue();
                if (anInterface.Properties() != null) {
                    Iterator<Map.Entry<String, IProperty>> entryIterator1 = anInterface.Properties().GetEnumerator();
                    while (entryIterator1.hasNext()) {
                        IProperty iProperty = entryIterator1.next().getValue();
                        if (iProperty.getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinitionUID)) {
                            Object value = iProperty.Value();
                            return value != null ? value.toString() : "";
                        }
                    }
                }
            }
        }
        return "";
    }

    private String getMyConfigurationItemUID() throws Exception {
        ICIMConfigurationItem currentScope = CIMContext.Instance.getMyConfigurationItem(null);
        if (currentScope == null)
            return "";
        return currentScope.UID();
    }

    public String generateKey(String pstrKeyDefinition, Boolean isScopeWised) throws Exception {
        String result = "";
        if (!StringUtils.isEmpty(pstrKeyDefinition)) {
            boolean flag = false;
            List<String> parts = new ArrayList<>();
            String[] strings = pstrKeyDefinition.split(",");
            for (String keyDefinitionPart : strings) {
                if (keyDefinitionPart.equalsIgnoreCase("UUID"))
                    parts.add(UUID.randomUUID().toString());
                else if (this.hasProperty(keyDefinitionPart)) {
                    parts.add(this.getPropertyValue(keyDefinitionPart));
                } else if (keyDefinitionPart.equalsIgnoreCase("CURRENTPROJECT")) {
                    flag = true;
                    parts.add(this.getMyConfigurationItemUID());
                } else
                    parts.add(keyDefinitionPart);
            }
            result = String.join("_", parts);
            if (!flag && isScopeWised != null && isScopeWised) {
                String configInfo = this.getMyConfigurationItemUID();
                result = result + (StringUtils.isEmpty(configInfo) ? "" : "_" + configInfo);
            }
        }
        if (StringUtils.isEmpty(result))
            result = null;
        return result;
    }

    public String generateKey(String pstrKeyDefinition) throws Exception {
        return this.generateKey(pstrKeyDefinition, null);
    }

    public void setParentCollection(IObjectCollection parentCollection) {
        this.parentCollection = parentCollection;
    }

    private String generateKeyPart(String relDefExpression, String defaultValue) {
        String lstrKeyPart = "";
        return lstrKeyPart;
    }

    public void generateUID(Boolean isScopeWise) throws Exception {
        String systemIDPattern = this.IObject().ClassDefinition().SystemIDPattern();
        if (StringUtils.isEmpty(systemIDPattern))
            systemIDPattern = "UUID";
        this.IObject().setUID(this.generateKey(systemIDPattern));
        ((IObjectDefault) this.IObject()).setHasUIDBeenGenerated(true);

    }

    public void generateUID() throws Exception {
        this.generateUID(null);
    }

    public void generateUniqueKey() throws Exception {
        if (this.UpdateState() != objectUpdateState.terminated && this.UpdateState() != objectUpdateState.deleted) {
            String lstrUniqueKeyPattern = this.IObject().ClassDefinition().UniqueKey();
            String uniqueKey = this.generateKey(lstrUniqueKeyPattern);
            if (!StringUtils.isEmpty(uniqueKey)) {
                if (!StringUtils.isEmpty(this.IObject().UniqueKey())) {
                    if (!uniqueKey.equalsIgnoreCase(this.IObject().UniqueKey()))
                        this.IObject().setUniqueKey(uniqueKey);
                } else {
                    this.IObject().setUniqueKey(uniqueKey);
                }
            }
        }
    }

    private String OnGenerateKey(IObject object, boolean pblnForUID) throws Exception {
        if (object != null) {
            String result = "";
            if (pblnForUID)
                result = ((IObjectDefault) object.Interfaces().item(interfaceDefinitionType.IObject.toString())).GetUID();
            else
                result = ((IObjectDefault) object.Interfaces().item(interfaceDefinitionType.IObject.toString())).GenerateUniqueKey();
            return result;
        }
        return "";
    }

    private String OnGetMasterKey(IObject revision, boolean pblnForUID) throws Exception {
        if (revision != null) {
            String result = "";
            IRel rel = revision.GetEnd2Relationships().GetRel(relDefinitionType.CIMDocumentRevisions.toString());
            if (rel != null) {
                IObject master = rel.GetEnd1();
                if (master != null) {
                    result = this.OnGenerateKey(master, pblnForUID);
                    if (pblnForUID) {
                        try {
                            UUID.fromString(result);
                            if (master.Lock().isWriteLocked()) {
                                master.setUID("");
                                result = this.OnGenerateKey(master, true);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            log.error(exception.getMessage(), exception);
                        }
                    }
                }
            }
            return result;
        }
        return "";
    }

    private String OnGetParentKey(boolean pblnForUID) throws Exception {
        String result = "";
        if (this.IObject().IsTypeOf("ICIMDocumentRevision"))
            result = this.OnGetMasterKey(this.IObject(), pblnForUID);
        else if (this.IObject().IsTypeOf("ICIMDocumentVersion")) {
            IRel rel = this.IObject().GetEnd2Relationships().GetRel(relDefinitionType.CIMDocumentRevisionVersions.toString());
            if (rel != null) {
                IObject revision = rel.GetEnd1();
                if (revision != null) {
                    String masterKey = this.OnGetMasterKey(revision, pblnForUID);
                    String revisionKey = this.OnGenerateKey(revision, pblnForUID);
                    if (pblnForUID && !((IObjectDefault) revision.Interfaces().item(interfaceDefinitionType.IObject.toString())).HasUIDBeenGenerated()) {
                        if (!revisionKey.startsWith(masterKey) && revision.Lock().isWriteLocked()) {
                            revision.setUID("");
                            result = this.OnGenerateKey(revision, true);
                        } else
                            result = revisionKey;
                    } else
                        result = revisionKey;
                }
            }
        }
        return result;
    }


    public boolean hasInterface(String interfaceDefinitionUid) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid))
            return this.interfaces.hasInterface(interfaceDefinitionUid);
        return false;
    }

    public void setUpdateState(objectUpdateState value) {
        if (value == objectUpdateState.terminated && this.updateState == objectUpdateState.created)
            this.createdAndTerminated = true;
        if (this.updateState != objectUpdateState.instantiated)
            this.updateState = value;
    }

    public void setClassDefinitionUid(String classDefinitionUid) throws Exception {
        this.classDefinitionUid = classDefinitionUid;
        if (this.instantiateRequiredItems) {
            this.instantiateRequiredInterfaces();
        }
    }

    private void instantiateRequiredInterfaces() throws Exception {
        IClassDef classDef = this.IObject().ClassDefinition();
        IRelCollection end1Relationships = classDef.GetEnd1Relationships();
        IRelCollection relCollection = end1Relationships.GetRels(relDefinitionType.realizes.toString(), true);
        if (relCollection != null) {
            Iterator<IObject> enumerator = relCollection.GetEnumerator();
            while (enumerator.hasNext()) {
                IRel rel = enumerator.next().toInterface(IRel.class);
                if (rel.IsRequired() && this.IObject().InstantiateRequiredItems() && !this.Interfaces().hasInterface(rel.UID2())) {
                    IInterfaceDef interfaceDef = rel.GetEnd2().toInterface(IInterfaceDef.class);
                    this.Interfaces().add((IInterface) interfaceDef.Instantiate(this.IObject().InstantiateRequiredItems()));
                }
            }
            if (this.IObject().InstantiateRequiredItems())
                this.Interfaces().sort();
        }
    }

    public IRelCollection GetEnd1Relationships() throws Exception {
        if (this.end1Relationships == null) {
            this.end1Relationships = new RelCollection(relCollectionTypes.End1s);
            this.end1Relationships.setParent(this.IObject());
        }
        return this.end1Relationships;
    }

    public void SetEnd1Relationships(IRelCollection collection) throws Exception {
        this.end1Relationships = collection;
        this.end1Relationships.setParent(this.Interfaces().item(interfaceDefinitionType.IObject.toString()));
    }

    public IRelCollection GetEnd2Relationships() throws Exception {
        if (this.end2Relationships == null) {
            this.end2Relationships = new RelCollection(relCollectionTypes.End2s);
            this.end2Relationships.setParent(this.IObject());
        }
        return this.end2Relationships;
    }

    public void SetEnd2Relationships(IRelCollection collection) throws Exception {
        this.end2Relationships = collection;
        this.end2Relationships.setParent(this.Interfaces().item(interfaceDefinitionType.IObject.toString()));
    }

    public boolean InstantiateRequiredItems() {
        return this.instantiateRequiredItems;
    }

    public void SetObjectOBIDs() {
        Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
        while (entryIterator.hasNext()) {
            IInterface lobjInterface = entryIterator.next().getValue();
            if (StringUtils.isEmpty(lobjInterface.InterfaceOBID())) {
                lobjInterface.setInterfaceUpdateState(interfaceUpdateState.created);
                lobjInterface.setInterfaceOBID(CIMContext.Instance.generateOBIDForInterface());
            }
            Iterator<Map.Entry<String, IProperty>> entryIterator1 = lobjInterface.Properties().GetEnumerator();
            while (entryIterator1.hasNext()) {
                IProperty property = entryIterator1.next().getValue();
                String propertyDefinitionUID = property.getPropertyDefinitionUid();
                if (!LiteObject.isObjOrRelTableProperty(propertyDefinitionUID, this.classDefinitionUid)) {
                    Iterator<IPropertyValue> iPropertyValueIterator = property.PropertyValues().GetEnumerator();
                    while (iPropertyValueIterator.hasNext()) {
                        IPropertyValue propertyValue = iPropertyValueIterator.next();
                        if (propertyValue.OBID() == null || StringUtils.isEmpty(propertyValue.OBID())) {
                            propertyValue.setOBID(CIMContext.Instance.generateOBIDForProperty());
                            propertyValue.setUpdateState(propertyValueUpdateState.created);
                        }
                    }
                }
            }
        }
    }

    public ClassBase(boolean instantiateRequiredItems) throws Exception {
        this.interfaces.add(new IObjectDefault(instantiateRequiredItems));
        this.instantiateRequiredItems = instantiateRequiredItems;
        this.createdAndTerminated = false;
        this.updateState = objectUpdateState.none;
    }
}
