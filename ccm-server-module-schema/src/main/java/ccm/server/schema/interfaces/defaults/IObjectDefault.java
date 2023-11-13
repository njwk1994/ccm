package ccm.server.schema.interfaces.defaults;

import ccm.server.args.*;
import ccm.server.context.CIMContext;
import ccm.server.convert.impl.ValueConvertService;
import ccm.server.entity.MetaDataObj;
import ccm.server.entity.MetaDataObjInterface;
import ccm.server.entity.MetaDataObjProperty;
import ccm.server.entity.MetaDataRel;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.models.LiteObject;
import ccm.server.schema.collections.IInterfaceCollection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.collections.impl.RelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.interfaces.generated.IObjectBase;
import ccm.server.schema.model.*;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static ccm.server.enums.interfaceUpdateState.*;

@Slf4j
public class IObjectDefault extends IObjectBase {
    public String mstrCachedUID;
    private boolean mblnIsTerminating;
    private boolean mblnIsDeleting;
    private boolean mblnValidated;
    private boolean mblnKeepObjectUpToDate;
    private boolean mblnNeedsInflation;
    private boolean mblnUIDIsUnique;
    private boolean mblnHasUIDBeenGenerated = false;
    private ArrayList<String> mcolOBIDsWithSameUniqueKey;

    private final RelCollection mcolEnd1RelsForRemoval = new RelCollection(relCollectionTypes.End1s);
    private final RelCollection mcolEnd2RelsForRemoval = new RelCollection(relCollectionTypes.End2s);

    private Exception cancelException;
    private final List<Exception> cancelExceptions = new ArrayList<>();

    public boolean HasUIDBeenGenerated() {
        return this.mblnHasUIDBeenGenerated;
    }

    public void setHasUIDBeenGenerated(boolean value) {
        this.mblnHasUIDBeenGenerated = value;
    }

    public boolean KeepObjectUpToDate() {
        return this.mblnKeepObjectUpToDate;
    }

    public void setKeepObjectUpToDate(boolean value) {
        this.mblnKeepObjectUpToDate = value;
    }

    public boolean NeedsInflation() {
        return this.mblnNeedsInflation;
    }

    public void setNeedsInflation(boolean value) {
        this.mblnNeedsInflation = value;
    }


    public void setUIDIsUnique(boolean value) {
        this.mblnUIDIsUnique = value;
    }

    public ArrayList<String> OBIDsWithSameUniqueKey() {
        return this.mcolOBIDsWithSameUniqueKey;
    }

    public void setOBIDsWithSameUniqueKey(ArrayList<String> value) {
        this.mcolOBIDsWithSameUniqueKey = value;
    }

    public RelCollection End1RelsForRemoval() {
        return this.mcolEnd1RelsForRemoval;
    }

    public RelCollection End2RelsForRemoval() {
        return this.mcolEnd2RelsForRemoval;
    }

    public Exception CancelException() {
        return this.cancelException;
    }

    public void setCancelException(Exception value) {
        this.cancelException = value;
        this.cancelExceptions.add(value);
    }

    public List<Exception> CancelExceptions() {
        return this.cancelExceptions;
    }

    @Override
    public IInterface myNext(String interfaceDefinitionUid, List<String> processedInterfaceDefs) {
        return super.myNext(interfaceDefinitionUid, processedInterfaceDefs);
    }

    @Override
    public boolean IsUniqueChecksOnOBIDAndUpdateState(ArrayList<String> parrOBIDs) {
        boolean result = true;
        if (parrOBIDs != null && parrOBIDs.size() > 0) {
            for (String s : parrOBIDs) {
                if (!CIMContext.Instance.Transaction().hasOBIDInDeletedOrTerminatedItems(s)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    public IObjectDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
        this.cancelException = null;
        this.mblnUIDIsUnique = true;
        this.mblnHasUIDBeenGenerated = false;
        this.mblnKeepObjectUpToDate = true;
        this.mstrCachedUID = "";
        this.Properties().add(Arrays.asList(new IProperty[]{
                new PropertyDefault(propertyDefinitionType.OBID.toString()),
                new PropertyDefault(propertyDefinitionType.UID.toString()),
                new PropertyDefault(propertyDefinitionType.DomainUID.toString()),
                new PropertyDefault(propertyDefinitionType.CreationDate.toString()),
                new PropertyDefault(propertyDefinitionType.TerminationDate.toString()),
                new PropertyDefault(propertyDefinitionType.LastUpdateDate.toString())
        }));
    }

    @Override
    public void OnCreate(createArgs e) throws Exception {
        String msg = this.toErrorPop();
        log.trace("enter to OnCreate for " + msg);
        StopWatch stopWatch = PerformanceUtility.start();
        boolean lblnDeferredMethod = false;
        if (!this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            if (this.useENS()) {
                lblnDeferredMethod = true;
            }
        }
        if (lblnDeferredMethod)
            this.ClassBase().addProcessedMethods(this, interfaceDefinitionType.IObject.toString(), "OnDeferredCreate", new Object[]{e}, true);
        else
            this.OnCreateInternal(e);
        // log.info("complete to OnCreate" + msg + PerformanceUtility.stop(stopWatch));
    }

    public void OnDeferredCreate(createArgs e) throws Exception {
        this.OnCreateInternal(e);
    }

    protected void OnCreateInternal(createArgs e) throws Exception {
        log.trace("enter to on-create-internal " + this.toErrorPop());
        String classDefinitionUID = this.ClassDefinitionUID();
        if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            String uid = this.UID();
            if (StringUtils.isEmpty(uid)) {
                this.setUID(this.toInterface(IRel.class).generateUID());
            }
        } else {
            if (CIMContext.Instance.Transaction().isENSActivated())
                this.callENSProcessing();
        }
    }

    private void callENSProcessing() {
        //to add ENS logic in future
    }

    protected boolean useENS() {
        return false;
    }

    @Override
    public void OnCreating(cancelArgs e) throws Exception {
//        String msg = this.toErrorPop();
//        StopWatch stopWatch = PerformanceUtility.start();
        //  log.trace("enter to on-creating " + msg);
        this.toInterface(interfaceDefinitionType.IObject.toString()).OnCreatingValidation(e);
        // log.info("complete to on-creating " + msg + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void OnCreatingValidation(cancelArgs e) throws Exception {
        log.trace("enter to on-creating-validation " + this.toErrorPop());
        StopWatch stopWatch = PerformanceUtility.start();
        if (!e.isCancel()) {
            String classDefinitionUID = this.ClassDefinitionUID();
            if (!classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                this.checkENSIfUse();
            }

            if (!e.isCancel() && !this.UIDIsUnique() && !this.IsUIDUniqueInTransaction()) {
                e.setCancel(true);
                e.setException(new Exception("object cannot create as UID is not unique with:" + UID() + "," + DomainUID() + "," + ClassDefinitionUID() + "," + Config()));
            }
            if (!e.isCancel()) {
                this.toInterface(interfaceDefinitionType.IObject.toString()).UniqueKeyValidation(e);
            }
            if (!e.isCancel()) {
                IInterface anInterface = this.toInterface(interfaceDefinitionType.IObject.toString());
                if (!anInterface.Validate()) {
                    e.setCancel(true);
                    e.setCancelMessage("error occurred during validate progress");
                }
            }
        }
        //  log.info("complete to on-creating-validation " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }

    protected void validateSpecialProperty(IProperty property) throws Exception {
        if (property != null) {
            if ((property.Value() == null || StringUtils.isEmpty(property.Value().toString())) &&
                    this.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.RelDef.toString()) &&
                    property.getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinitionType.RelDefUID.toString())) {
                throw new Exception("property ***" + property.getPropertyDefinitionUid() + "*** is required but value is not valid as it is NULL");
            }
        }
    }

    protected void validateForRequiredInterfacesAndProperties() throws Exception {
        IClassDef classDef = this.GetClassDefinition();
        HashSet<IInterfaceDef> lcolInterfaceDefs = this.onGetRealizedInterfaceDefsForClassDef(classDef);
        if (CommonUtility.hasValue(lcolInterfaceDefs)) {
            for (IInterfaceDef interfaceDef : lcolInterfaceDefs) {
                IInterface lobjInterface = this.Interfaces().get(interfaceDef.UID());
                if (lobjInterface == null || lobjInterface.UpdateState() == deleted || lobjInterface.UpdateState() == terminated) {
                    throw new Exception("interface definition " + interfaceDef.UID() + " realized but missing");
                } else if (lobjInterface.UpdateState() != deleted && lobjInterface.UpdateState() != terminated) {
                    IRelCollection relCollection = interfaceDef.GetEnd1Relationships().GetRels(relDefinitionType.exposes.toString(), true);
                    if (SchemaUtility.hasValue(relCollection)) {
                        Iterator<IObject> iterator = relCollection.GetEnumerator();
                        while (iterator.hasNext()) {
                            IRel rel = iterator.next().toInterface(IRel.class);
                            String propertyDef = rel.UID2();
                            IProperty property = lobjInterface.Properties().get(rel.UID2());
                            if (rel.IsRequired()) {
                                if (property == null || property.isTerminated())
                                    throw new Exception("property ***" + propertyDef + "*** is required but missing");
                            }
                            this.validateSpecialProperty(property);
                        }
                    }
                }
            }
        }
    }

    protected void doValidateRelationships(validateArgs e) throws Exception {
        IObjectCollection lcolEnd1RelDefsToCheck = new ObjectCollection();
        Iterator<IObject> iObjectIterator = this.GetEnd1Relationships().GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IRel rel = iObjectIterator.next().toInterface(IRel.class);
            objectUpdateState updateState = ((IInterface) rel).ClassBase().UpdateState();
            if (updateState == objectUpdateState.created || updateState == objectUpdateState.terminated) {
                IRelDef relDef = rel.GetRelationshipDefinition();
                if (!lcolEnd1RelDefsToCheck.contains(relDef))
                    lcolEnd1RelDefsToCheck.append(relDef);
            }
        }
        Iterator<IObject> iObjectIterator1 = this.End1RelsForRemoval().GetEnumerator();
        while (iObjectIterator1.hasNext()) {
            IRelDef relDef = iObjectIterator1.next().toInterface(IRel.class).GetRelationshipDefinition();
            if (!lcolEnd1RelDefsToCheck.contains(relDef))
                lcolEnd1RelDefsToCheck.append(relDef);
        }
        Iterator<IObject> iObjectIterator2 = lcolEnd1RelDefsToCheck.GetEnumerator();
        while (iObjectIterator2.hasNext()) {
            IRelDef relDef = iObjectIterator2.next().toInterface(IRelDef.class);
            this.onValidateEnd1RelDef(relDef, e);
        }

        IObjectCollection lcolEnd2RelDefsToCheck = new ObjectCollection();
        Iterator<IObject> iObjectIterator3 = this.GetEnd2Relationships().GetEnumerator();
        while (iObjectIterator3.hasNext()) {
            IRel rel = iObjectIterator3.next().toInterface(IRel.class);
            objectUpdateState objectUpdateState1 = ((IInterface) rel).ClassBase().UpdateState();
            if (objectUpdateState1 == objectUpdateState.created || objectUpdateState1 == objectUpdateState.terminated) {
                IRelDef relDef = rel.GetRelationshipDefinition();
                if (!lcolEnd2RelDefsToCheck.contains(relDef))
                    lcolEnd2RelDefsToCheck.append(relDef);
            }
        }
        Iterator<IObject> iObjectIterator4 = this.End2RelsForRemoval().GetEnumerator();
        while (iObjectIterator4.hasNext()) {
            IRelDef next = iObjectIterator4.next().toInterface(IRel.class).GetRelationshipDefinition();
            if (!lcolEnd2RelDefsToCheck.contains(next))
                lcolEnd2RelDefsToCheck.append(next);
        }
        Iterator<IObject> iObjectIterator5 = lcolEnd2RelDefsToCheck.GetEnumerator();
        while (iObjectIterator5.hasNext()) {
            IRelDef relDef = iObjectIterator5.next().toInterface(IRelDef.class);
            this.onValidateEnd2RelDef(relDef, e);
        }
    }

    @Override
    public void OnValidate(validateArgs e) throws Exception {
        //  log.trace("enter to on validate " + this.toErrorPop());
        StopWatch stopWatch = PerformanceUtility.start();
        boolean lblnValidation = false;
        if (!this.mblnValidated) {
            lblnValidation = true;
            this.mblnValidated = true;
        }

        if (lblnValidation) {
            objectUpdateState objectUpdateState = this.ClassBase().UpdateState();
            if (objectUpdateState == ccm.server.enums.objectUpdateState.created || objectUpdateState == ccm.server.enums.objectUpdateState.none) {
                this.validateForRequiredInterfacesAndProperties();
                if (objectUpdateState == ccm.server.enums.objectUpdateState.none) {
                    this.doValidateRelationships(e);
                } else {
                    Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
                    while (entryIterator.hasNext()) {
                        IInterface anInterface = entryIterator.next().getValue();
                        IInterfaceDef interfaceDefinition = anInterface.getInterfaceDefinition();
                        if (interfaceDefinition != null) {
                            IObjectCollection end1RelDefs = interfaceDefinition.getEnd1RelDefs();
                            if (end1RelDefs != null && end1RelDefs.hasValue()) {
                                Iterator<IObject> iObjectIterator = end1RelDefs.GetEnumerator();
                                while (iObjectIterator.hasNext()) {
                                    IRelDef relDef = iObjectIterator.next().toInterface(IRelDef.class);
                                    this.onValidateEnd1RelDef(relDef, e);
                                }
                            }

                            IObjectCollection end2RelDefs = interfaceDefinition.getEnd2RelDefs();
                            if (end2RelDefs != null && end2RelDefs.hasValue()) {
                                Iterator<IObject> iObjectIterator1 = end2RelDefs.GetEnumerator();
                                while (iObjectIterator1.hasNext()) {
                                    IRelDef relDef = iObjectIterator1.next().toInterface(IRelDef.class);
                                    this.onValidateEnd2RelDef(relDef, e);
                                }
                            }
                        } else
                            log.error("on-validate failed", new Exception("invalid interface definition:" + anInterface.InterfaceDefinitionUID()));
                    }
                }
            }
        }
        // log.info("complete to do on-validate " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }

    private void onValidateEnd2RelDef(IRelDef relDef, validateArgs e) {

    }

    protected void onValidateEnd1RelDef(IRelDef relDef, validateArgs e) {

    }

    @Override
    public boolean Validate() throws Exception {
        // log.trace("enter to validate");
        validateArgs validateArgs = new validateArgs(new Exception("validation progress error:" + this.ClassDefinitionUID() + "," + this.OBID() + "," + this.UID()));
        this.toInterface(interfaceDefinitionType.IObject.toString()).OnValidate(validateArgs);
        return validateArgs.isValid();
    }

    protected boolean IsUIDUniqueInTransaction() {
        boolean result = true;
        StopWatch stopWatch = PerformanceUtility.start();
        // log.trace("enter to is UID unique in transaction judgement");
        Map<String, IObject> stringIObjectMap = CIMContext.Instance.Transaction().ObjectsInTransaction();
        if (stringIObjectMap != null && stringIObjectMap.size() > 0) {
            List<IObject> objects = stringIObjectMap.values().stream().filter(c -> c.UID().equalsIgnoreCase(this.UID()) && c.DomainUID().equalsIgnoreCase(this.DomainUID())).collect(Collectors.toList());
            if (objects.size() > 0) {
                // log.trace("transaction result:" + objects.size());
                for (IObject object : objects) {
                    if (!StringUtils.isEmpty(this.Config())) {
                        if (!object.Config().equalsIgnoreCase(this.Config()))
                            continue;
                    }
                    if (object.ObjectUpdateState() != objectUpdateState.deleted && object.ObjectUpdateState() != objectUpdateState.terminated) {
                        result = false;
                        break;
                    }
                }
            }
        }
        //log.trace("uid unique identification result:" + result + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
        return result;
    }

    @Override
    public void UniqueKeyValidation(cancelArgs cancelArgs) throws Exception {
        // log.trace("do unique key validation");
        StopWatch stopWatch = PerformanceUtility.start();
        IProperty uniqueKeyProperty = this.Interfaces().get(interfaceDefinitionType.IObject.toString()).Properties().get(propertyDefinitionType.UniqueKey.toString());
        if (uniqueKeyProperty != null) {
            IPropertyValue propertyValue = uniqueKeyProperty.CurrentValue();
            if (propertyValue != null) {
                propertyValueUpdateState updateState = propertyValue.UpdateState();
                if (updateState == propertyValueUpdateState.updated || updateState == propertyValueUpdateState.revive || updateState == propertyValueUpdateState.created) {
                    String uniqueKey = this.UniqueKey();
                    if ((uniqueKey != null) && uniqueKey.contains(" ")) {
                        cancelArgs.setCancel(true);
                        cancelArgs.setException(new Exception("unique key is not valid as it contains blank"));
                    }
                    if (this.GetClassDefinition().UniqueKeyPattern() != null) {
                        if (uniqueKey != null && !this.toInterface(interfaceDefinitionType.IObject.toString()).IsUniqueKeyUnique()) {
                            cancelArgs.setCancel(true);
                            cancelArgs.setException(new Exception("object cannot be created as the unique key is not unique with " + this.ClassDefinitionUID() + "," + uniqueKey + "," + this.GetClassDefinition().DisplayName()));
                        }
                    }
                }
            }
        }
        //   log.trace("complete to unique key validation " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }


    public boolean UIDIsUnique() {
        return this.mblnUIDIsUnique;
    }

    @Override
    public void OnCreated(suppressibleArgs e) throws Exception {
        //   log.trace("enter to on-created " + this.toErrorPop());
        StopWatch stopWatch = PerformanceUtility.start();
        String classDefinitionUID = this.ClassDefinitionUID();
        if (classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            IRel rel = this.toInterface(IRel.class);
            if (rel != null) {
                IObject end1 = rel.GetEnd1();
                if (end1 != null) {
                    end1.toInterface(IObject.class).OnRelationshipAdded(new relArgs(rel, relDirection._1To2, e.getUsername(), e.getConfigurationItem()));
                }
                IObject end2 = rel.GetEnd2();
                if (end2 != null)
                    end2.toInterface(IObject.class).OnRelationshipAdded(new relArgs(rel, relDirection._2To1, e.getUsername(), e.getConfigurationItem()));
            }
        } else
            CIMContext.Instance.Transaction().NewItemsToSerialize().add(this);
        //  log.info("complete to on-created " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void OnCopy(copyArgs e) throws Exception {
        throw new Exception("not implemented");
    }

    @Override
    public void OnCopies(copyArgs e) throws Exception {
        throw new Exception("not implemented");
    }

    @Override
    public void OnCopying(cancelArgs e) throws Exception {
        throw new Exception("not implemented");
    }

    protected void checkENSIfUse() {
        //to be added in future
    }

    @Override
    public void OnRelationshipUpdating(relArgs e) throws Exception {

    }

    @Override
    public void OnUpdatingValidation(cancelArgs e) throws Exception {
        // log.trace("enter to on-updating-validation " + this.toErrorPop());
        StopWatch stopWatch = PerformanceUtility.start();
        this.UniqueKeyValidation(e);
        if (!this.UIDIsUnique() && !this.IsUIDUniqueInTransaction()) {
            e.setCancel(true);
            if (this.Config() == null) {
                e.setException(new Exception("object cannot be created, UID is not unique:" + UID()));
            }
        }
        if (!e.isCancel() && !this.toInterface(interfaceDefinitionType.IObject.toString()).Validate()) {
            e.setCancel(true);
            e.setCancelMessage("error occurred during updating process");
        }
        //log.info("complete to on-updating-validation " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void setInterfaceDefinitionUID(String interfaceDefinitionUID) {
        super.setInterfaceDefinitionUID(interfaceDefinitionUID);
    }

    @Override
    public void OnDelete(suppressibleArgs e) throws Exception {
        this.toInterface(interfaceDefinitionType.IObject.toString()).doDelete();
        this.ClassBase().addCompletedMethod(this, interfaceDefinitionType.IObject.toString(), deferredMethods.OnDeleted.toString(), new Object[]{new suppressibleArgs(e.isSuppressEvents(), e.getUsername(), e.getConfigurationItem())});
    }

    @Override
    public void OnDeleting(cancelArgs e) {
        //  log.trace("enter to on-deleting " + this.toErrorPop());
        StopWatch stopWatch = PerformanceUtility.start();
        if (!e.isSuppressEvents()) {
            //to add event subscription in future
        }
        //  log.info("complete to on-deleting " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void OnDeleted(suppressibleArgs e) throws Exception {
        // log.trace("enter to on-deleted " + this.toErrorPop());
        StopWatch stopWatch = PerformanceUtility.start();
        if (!this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
            CIMContext.Instance.Transaction().DeletedItemsToSerialize().add(this);
        else {
            IRel rel = this.toInterface(IRel.class);
            if (rel != null) {
                IObject end1 = rel.GetEnd1();
                if (end1 != null) {
                    end1.toInterface(IObject.class).OnRelationshipRemoved(new relArgs(rel, relDirection._1To2, e.getUsername(), e.getConfigurationItem()));
                }
                IObject end2 = rel.GetEnd2();
                if (end2 != null)
                    end2.toInterface(IObject.class).OnRelationshipRemoved(new relArgs(rel, relDirection._2To1, e.getUsername(), e.getConfigurationItem()));
            }
        }
        if (!e.isSuppressEvents()) {
            //to add event subscription in future
        }
        //   log.info("complete to on-deleted " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void OnPreProcess(createArgs e) {

    }

    @Override
    public void OnRelationshipAdd(relArgs e) {

    }

    @Override
    public void OnRelationshipAdding(relArgs e) throws Exception {
        if (!CIMContext.Instance.Transaction().contains(this)) {
            if (!this.toInterface(IObject.class).Validate()) {
                e.setCancel(true);
                e.setException(new Exception("validation error for adding progress or it does not submit to transaction yet"));
            }
        }
    }

    @Override
    public void OnRelationshipAdded(relArgs e) throws Exception {
        //for self implementation
        if (e.getRel() != null) {
            if (e.getRel().OBID1().equalsIgnoreCase(this.OBID()))
                this.GetEnd1Relationships().add(e.getRel());
            if (e.getRel().OBID2().equalsIgnoreCase(this.OBID()))
                this.GetEnd2Relationships().add(e.getRel());
        }
    }

    private boolean isRemoveMinCardinalityRels() throws Exception {
        boolean result = false;
        HashSet<String> lcolRelDefs = new HashSet<>();
        Iterator<IObject> iObjectIterator = this.End1RelsForRemoval().GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IRel rel = iObjectIterator.next().toInterface(IRel.class);
            if (!lcolRelDefs.contains(rel.RelDefUID())) {
                lcolRelDefs.add(rel.RelDefUID());
                if (rel.GetRelationshipDefinition().Min2() == 1) {
                    result = true;
                    break;
                }
            }
        }
        if (!result) {
            lcolRelDefs.clear();
            Iterator<IObject> iObjectIterator1 = this.End2RelsForRemoval().GetEnumerator();
            while (iObjectIterator1.hasNext()) {
                IRel rel = iObjectIterator1.next().toInterface(IRel.class);
                if (!lcolRelDefs.contains(rel.RelDefUID())) {
                    lcolRelDefs.add(rel.RelDefUID());
                    if (rel.GetRelationshipDefinition().Min1() == 1) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void OnRelationshipRemoved(relArgs e) throws Exception {
        //to add self logic
        if (e.getRel() != null) {
            if (e.getRel().OBID1().equalsIgnoreCase(this.OBID()))
                this.GetEnd1Relationships().remove(e.getRel());
            if (e.getRel().OBID2().equalsIgnoreCase(this.OBID()))
                this.GetEnd2Relationships().remove(e.getRel());
        }
    }

    @Override
    public void OnRelationshipRemoving(relArgs e) throws Exception {
        if (this.isRemoveMinCardinalityRels()) {
            if (!this.toInterface(IObject.class).Validate()) {
                e.setCancel(true);
                e.setException(new Exception("validation progress error or it cannot be removed as it is mandatory"));
            }
        }
        this.End2RelsForRemoval().clear();
        this.End1RelsForRemoval().clear();
    }

    @Override
    public void OnTerminate(suppressibleArgs e) throws Exception {
        this.ClassBase().setUpdateState(objectUpdateState.terminated);
        Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
        while (entryIterator.hasNext()) {
            IInterface anInterface = entryIterator.next().getValue();
            anInterface.setInterfaceUpdateState(terminated);
            Iterator<Map.Entry<String, IProperty>> entryIterator1 = anInterface.Properties().GetEnumerator();
            while (entryIterator1.hasNext()) {
                IProperty property = entryIterator1.next().getValue();
                property.terminateProperty();
            }
        }
        this.ClassBase().CancelMethods().clear();
        this.ClassBase().CompletedMethods().clear();
        this.ClassBase().ProcessedMethods().clear();
        this.ClassBase().ProcessMethods().clear();
        this.ClassBase().addCompletedMethod(this, interfaceDefinitionType.IObject.toString(), deferredMethods.OnTerminated.toString(), new Object[]{new suppressibleArgs(e.isSuppressEvents(), e.getUsername(), e.getConfigurationItem())});
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL) && (!StringUtils.isEmpty(this.UID())))
            this.setUID(this.toInterface(IRel.class).generateUID());
    }

    @Override
    public void OnTerminating(cancelArgs e) {
/*        if (!e.isSuppressEvents()) {

        }*/
    }

    @Override
    public void OnTerminated(suppressibleArgs e) throws Exception {
        String username = CIMContext.Instance.Transaction().getLoginUser();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.Transaction().getConfigurationItem();
        String classDefinitionUID = this.ClassDefinitionUID();
        if (!classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
            CIMContext.Instance.Transaction().TerminatedItemsToSerialize().add(this);
        else {
            IRel rel = this.toInterface(IRel.class);
            if (rel != null) {
                IObject end1 = rel.GetEnd1();
                if (end1 != null) {
                    end1.toInterface(IObject.class).OnRelationshipRemoved(new relArgs(rel, relDirection._1To2, username, configurationItem));
                }
                IObject end2 = rel.GetEnd2();
                if (end2 != null)
                    end2.toInterface(IObject.class).OnRelationshipRemoved(new relArgs(rel, relDirection._2To1, username, configurationItem));
            }
        }
    }

    @Override
    public void OnUpdate(updateArgs e) throws Exception {
        String errorPop = this.toErrorPop();
        //log.trace("enter to OnUpdate to " + errorPop);
        StopWatch stopWatch = PerformanceUtility.start();
/*        if (!e.isSuppressEvents()) {

        }*/
        this.processProperties(e);
        String classDefinitionUID = this.ClassDefinitionUID();
        if (!classDefinitionUID.equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            this.ClassBase().generateUniqueKey();
/*            if (this.hasNameChanged(this)) {

            }*/
        }
        // log.info("complete to OnUpdate for " + errorPop + PerformanceUtility.stop(stopWatch));
    }

    protected boolean hasNameChanged(IObject pobjItem) throws Exception {
        boolean result = false;
        if (pobjItem != null) {
            objectUpdateState objectUpdateState = pobjItem.ObjectUpdateState();
            if (objectUpdateState != ccm.server.enums.objectUpdateState.deleted &&
                    objectUpdateState != ccm.server.enums.objectUpdateState.terminated &&
                    pobjItem.toInterface(interfaceDefinitionType.IObject.toString()).Properties().hasProperty(propertyDefinitionType.Name.toString()) && pobjItem.toInterface(interfaceDefinitionType.IObject.toString()).Properties().item(propertyDefinitionType.Name.toString()).PropertyValues().size() > 1) {
                result = true;
            }
        }
        return result;
    }

    protected void processProperties(updateArgs e) {
        boolean lblnFlagPerAttribute = false;

    }

    @Override
    public void OnUpdated(suppressibleArgs e) throws Exception {
        if (!this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
            CIMContext.Instance.Transaction().RefreshItemsToSerialize().add(this);
/*        if (!e.isSuppressEvents()) {

        }*/
    }

    @Override
    public void OnUpdating(cancelArgs e) throws Exception {
        this.toInterface(interfaceDefinitionType.IObject.toString()).OnUpdatingValidation(e);
//        if (!e.isSuppressEvents()) {
//
//        }
    }

    protected HashSet<IInterfaceDef> onGetRealizedInterfaceDefsForClassDef(IClassDef classDef) throws Exception {
        HashSet<IInterfaceDef> lcolIDefs = new HashSet<>();
        if (classDef != null) {
            List<String> realizedInterfaceDefs = CIMContext.Instance.ProcessCache().getRealizedInterfaceDefByClassDef(classDef.UID(), true);
            if (CommonUtility.hasValue(realizedInterfaceDefs)) {
                for (String interfaceDef : realizedInterfaceDefs) {
                    IInterfaceDef interfaceDef1 = CIMContext.Instance.ProcessCache().item(interfaceDef, domainInfo.SCHEMA.toString()).toInterface(IInterfaceDef.class);
                    lcolIDefs.add(interfaceDef1);
                }
            } else {
                IRelCollection relCollection = classDef.GetEnd1Relationships().GetRels(relDefinitionType.realizes.toString(), true);
                if (relCollection != null) {
                    Iterator<IObject> iObjectIterator = relCollection.GetEnumerator();
                    while (iObjectIterator.hasNext()) {
                        IRel lobjRel = iObjectIterator.next().toInterface(IRel.class);
                        if (lobjRel.IsRequired())
                            lcolIDefs.add(lobjRel.GetEnd2().toInterface(IInterfaceDef.class));
                    }
                }
            }
        }
        return lcolIDefs;
    }

    protected void clearDeferredInfo() {
        this.ClassBase().clearDeferredInfo();
    }

    @Override
    public void commit() {
        this.mblnValidated = false;
        try {
            if (!this.Terminated() && !this.Deleted()) {
                Iterator<Map.Entry<String, IInterface>> i = this.Interfaces().GetEnumerator();
                while (i.hasNext()) {
                    IInterface anInterface = i.next().getValue();
                    if (anInterface.UpdateState() != deleted && anInterface.UpdateState() != terminated) {
                        anInterface.setInterfaceUpdateState(interfaceUpdateState.none);
                        Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
                        while (p.hasNext()) {
                            IProperty property = p.next().getValue();
                            IPropertyValue value = property.CurrentValue();
                            if (value != null && value.UpdateState() != propertyValueUpdateState.deleted && value.UpdateState() != propertyValueUpdateState.terminated) {
                                IPropertyValue[] larrPropertyValues = new PropertyValue[property.PropertyValues().size()];
                                property.PropertyValues().copyTo(larrPropertyValues, 0);
                                for (IPropertyValue propertyValue : larrPropertyValues) {
                                    if (propertyValue.UpdateState() != propertyValueUpdateState.deleted && propertyValue.UpdateState() != propertyValueUpdateState.terminated) {
                                        propertyValue.setUpdateState(propertyValueUpdateState.none);
                                        propertyValue.setIsTemporaryValue(false);
                                    }
                                }
                            }
                        }
                    }
                }
                this.ClassBase().setUpdateState(objectUpdateState.none);
            }
        } catch (Exception exception) {
            log.error("IObject commit error for " + this.toErrorPop(), exception);
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
        }
    }

    protected void doCopy(IObject pobjNewObject, IObjectCollection pcolEnd1RelDefs) throws Exception {
        if (pobjNewObject != null && pcolEnd1RelDefs != null && pcolEnd1RelDefs.hasValue()) {
            Iterator<IObject> objectIterator = pcolEnd1RelDefs.GetEnumerator();
            while (objectIterator.hasNext()) {
                IRelDef relDef = objectIterator.next().toInterface(IRelDef.class);
                if (!relDef.IsAbstract() && this.ObjectUpdateState() == objectUpdateState.instantiated) {
                    switch (relDef.Copy12()) {
                        case "CopyObject":
                        case "#2":
                            IRelCollection relCollection1 = this.GetEnd1Relationships().GetRels(relDef.UID());
                            if (relCollection1 != null && relCollection1.hasValue()) {
                                Iterator<IObject> iterator = relCollection1.GetEnumerator();
                                while (iterator.hasNext()) {
                                    IRel rel = iterator.next().toInterface(IRel.class);
                                    if (rel != null) {
                                        IObject end2Object = rel.GetEnd2();
                                        if (end2Object != null) {

                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public IObject Copy() throws Exception {
        IObject result = null;
        String username = CIMContext.Instance.Transaction().getLoginUser();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.Transaction().getConfigurationItem();
        cancelArgs cancelArgs = new cancelArgs();
        this.toInterface(interfaceDefinitionType.IObject.toString()).OnCopying(cancelArgs);
        if (!cancelArgs.isCancel()) {
            copyArgs copyArgs = new copyArgs(this, username, configurationItem);
            this.toInterface(interfaceDefinitionType.IObject.toString()).OnCopy(copyArgs);
            result = copyArgs.getNewObject();
            if (result != null) {
                Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
                while (entryIterator.hasNext()) {
                    IInterface anInterface = entryIterator.next().getValue();
                    IObjectCollection end1RelDefs = anInterface.getInterfaceDefinition().getEnd1RelDefs();
                    if (end1RelDefs != null && end1RelDefs.hasValue()) {

                    }
                }
            }
        }
        return result;
    }

    @Override
    public ICIMConfigurationItem getConfig() throws Exception {
        if (!StringUtils.isEmpty(this.Config())) {
            IObject item = CIMContext.Instance.ProcessCache().item(this.Config(), domainInfo.ADMIN.toString());
            return item != null ? item.toInterface(ICIMConfigurationItem.class) : null;
        }
        return null;
    }

    @Override
    public boolean IsUniqueKeyUniqueInConfig() throws Exception {
        boolean result = true;
        ArrayList<String> larrOBIDs = new ArrayList<>();
        if (CommonUtility.hasValue(this.marrOBIDsWithSameUniqueKey)) {
            larrOBIDs.addAll(this.marrOBIDsWithSameUniqueKey);
            larrOBIDs.remove(this.OBID());
        }
        if (!this.toInterface(interfaceDefinitionType.IObject.toString()).IsUniqueChecksOnOBIDAndUpdateState(larrOBIDs))
            result = false;
        else if (!this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            HashSet<IObject> objects = CIMContext.Instance.Transaction().CreatesAndUpdatesByUniqueKey().getOrDefault(this.UniqueKey(), null);
            List<String> lcolUIDAndDomainUIDs = new ArrayList<>();
            for (IObject next : objects) {
                String uniqueKey = next.UID() + "," + next.DomainUID();
                if (lcolUIDAndDomainUIDs.contains(uniqueKey.toUpperCase())) {
                    result = false;
                    break;
                }
                lcolUIDAndDomainUIDs.add(uniqueKey.toUpperCase());
            }
        }
        return result;
    }

    private final ArrayList<String> marrOBIDsWithSameUniqueKey = new ArrayList<>();

    @Override
    public boolean IsUniqueKeyUnique() throws Exception {
        //log.trace("enter to is unique-key unique identification");
        StopWatch stopWatch = PerformanceUtility.start();
        boolean result = true;
        ArrayList<String> larrOBIDs = new ArrayList<>();
        if (CommonUtility.hasValue(marrOBIDsWithSameUniqueKey)) {
            larrOBIDs.addAll(this.marrOBIDsWithSameUniqueKey);
            larrOBIDs.remove(this.OBID());
        }
        if (!this.toInterface(interfaceDefinitionType.IObject.toString()).IsUniqueChecksOnOBIDAndUpdateState(larrOBIDs))
            result = false;
        else if (!this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            if (CIMContext.Instance.Transaction().CreatesAndUpdatesByUniqueKey().containsKey(this.UniqueKey())) {
                HashSet<IObject> objects = CIMContext.Instance.Transaction().CreatesAndUpdatesByUniqueKey().get(this.UniqueKey());
                result = !(objects != null && objects.size() > 1);
            }
        }
        // log.trace("complete to do unique identification for unique-key " + this.toErrorPop() + PerformanceUtility.stop(stopWatch));
        return result;
    }

    @Override
    public void Delete() throws Exception {
        this.toInterface(interfaceDefinitionType.IObject.toString()).Delete(false);
    }

    protected void deleteRelatedInfo(IObjectCollection objectCollection, boolean pblnSuppressEvent) throws Exception {
        if (objectCollection != null && objectCollection.hasValue()) {
            Iterator<IObject> objectIterator = objectCollection.GetEnumerator();
            while (objectIterator.hasNext()) {
                IObject iObject = objectIterator.next();
                iObject.Delete();
            }
        }
    }

    @Override
    public void Delete(boolean pblnSuppressEvent) throws Exception {
        String errorPop = this.toErrorPop();
        StopWatch stopWatch = PerformanceUtility.start();
        //  log.trace("enter to delete " + errorPop + ",suppress event:" + pblnSuppressEvent);
        if (!this.mblnIsDeleting) {
            this.mblnIsDeleting = true;
            try {
                String username = CIMContext.Instance.Transaction().getLoginUser();
                ICIMConfigurationItem configurationItem = CIMContext.Instance.Transaction().getConfigurationItem();
                this.BeginUpdate();
                cancelArgs cancelArgs = new cancelArgs(pblnSuppressEvent, username, configurationItem);
                IObjectCollection relatedInfoForObject = CIMContext.Instance.GraphExpansion().getRelatedInfoForObject(this);
                this.toInterface(interfaceDefinitionType.IObject.toString()).OnDeleting(cancelArgs);
                if (cancelArgs.isCancel())
                    throw new Exception("delete progress failed as some exception occurred");
                this.deleteRelatedInfo(relatedInfoForObject, pblnSuppressEvent);
                this.OnDelete(new suppressibleArgs(pblnSuppressEvent, username, configurationItem));
            } finally {
                this.mblnIsDeleting = false;
                this.FinishUpdate();
            }
        }
        // log.trace("complete to delete " + errorPop + PerformanceUtility.stop(stopWatch));
    }

    @Override
    public void doDelete() throws Exception {
        this.ClassBase().setUpdateState(objectUpdateState.deleted);
        Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            IInterface anInterface = e.next().getValue();
            anInterface.setInterfaceUpdateState(deleted);
            Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
            while (p.hasNext()) {
                IProperty property = p.next().getValue();
                property.deleteProperty();
            }
        }

        this.ClassBase().CancelMethods().clear();
        this.ClassBase().CompletedMethods().clear();
        this.ClassBase().ProcessedMethods().clear();
        this.ClassBase().ProcessMethods().clear();

        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL) && StringUtils.isEmpty(this.UID()))
            this.setUID(this.toInterface(IRel.class).generateUID());
    }

    @Override
    public void Terminate() throws Exception {
        this.toInterface(interfaceDefinitionType.IObject.toString()).Terminate(false);
    }

    @Override
    public void Terminate(boolean pblnSuppressEvent) throws Exception {
        if (!this.mblnIsTerminating) {
            this.mblnIsTerminating = true;
            try {
                String username = CIMContext.Instance.Transaction().getLoginUser();
                ICIMConfigurationItem configurationItem = CIMContext.Instance.Transaction().getConfigurationItem();
                this.BeginUpdate();
                cancelArgs cancelArgs = new cancelArgs(pblnSuppressEvent, username, configurationItem);
                this.toInterface(interfaceDefinitionType.IObject.toString()).OnTerminating(cancelArgs);
                if (cancelArgs.isCancel())
                    throw new Exception("Terminate progress failed as some exception occurred");
                this.OnTerminate(new suppressibleArgs(pblnSuppressEvent, username, configurationItem));
            } catch (Exception exception) {
                log.error("terminate failed", exception);
            } finally {
                this.FinishUpdate();
                this.mblnIsTerminating = false;
            }
        }
    }

    @Override
    public void BeginUpdate() throws Exception {
        this.toInterface(interfaceDefinitionType.IObject.toString()).BeginUpdate(true, false);
    }

    @Override
    public void BeginUpdate(boolean pblnValidateForClaim) throws Exception {
        this.toInterface(interfaceDefinitionType.IObject.toString()).BeginUpdate(pblnValidateForClaim, false);
    }

    @Override
    public void BeginUpdate(boolean validateForClaim, boolean suppressEvents) throws Exception {
        IObjectDefault lobjIObjectDefault = (IObjectDefault) this.Interfaces().item(interfaceDefinitionType.IObject.toString());
        if (lobjIObjectDefault.NeedsInflation()) {
            this.InflateObjectThatCameFromCache();
            lobjIObjectDefault.setNeedsInflation(false);
        }

        if (!CIMContext.Instance.Transaction().inTransaction())
            throw new Exception("transaction is not started, you have to start transaction firstly before DML");

        String username = CIMContext.Instance.Transaction().getLoginUser();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.Transaction().getConfigurationItem();
        if (this.ObjectUpdateState() != objectUpdateState.none) {
            if (!ReentrantLockUtility.tryToAcquireWriteLock(this.Lock()))
                throw new Exception("acquired write lock failed");
        } else {
            if (!ReentrantLockUtility.tryToAcquireWriteLock(this.Lock())) {
                this.ClassBase().addPreProcessMethod(this, interfaceDefinitionType.IObject.toString(), deferredMethods.OnPreProcess.toString(), new Object[]{new createArgs(suppressEvents, username, configurationItem)});
            } else {
                if (this.ClassBase().CancelMethods().size() == 0) {
                    this.ClassBase().addProcessMethods(this, interfaceDefinitionType.IObject.toString(), deferredMethods.OnUpdate.toString(), new Object[]{new updateArgs(suppressEvents, username, configurationItem)});
                    this.ClassBase().addCancelMethod(this, interfaceDefinitionType.IObject.toString(), deferredMethods.OnUpdating.toString(), new Object[]{new cancelArgs(suppressEvents, username, configurationItem)});
                    this.ClassBase().addCompletedMethod(this, interfaceDefinitionType.IObject.toString(), deferredMethods.OnUpdated.toString(), new Object[]{new suppressibleArgs(suppressEvents, username, configurationItem)});
                }
            }
        }
    }

    public void InflateObjectThatCameFromCache() throws Exception {
        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
            CIMContext.Instance.ProcessCache().inflateCachedIRelFromDataBase(this.toInterface(IRel.class));
        } else {
            CIMContext.Instance.ProcessCache().inflateCachedIObjectFromDataBase(this);
        }
    }

    public void resetValidateFlag() {
        this.mblnValidated = false;
    }

    @Override
    public void rollback() throws Exception {
        this.resetValidateFlag();
        try {
            if (this.ClassBase().UpdateState() == objectUpdateState.created || this.ClassBase().UpdateState() == objectUpdateState.instantiated) {
                //initial object's status is created or instantiated, after roll back status has to set to be deleted, and during rollback it will be removed from process cache
                this.ClassBase().setUpdateState(objectUpdateState.deleted);
                CIMContext.Instance.Transaction().DeletedItemsToSerialize().add(this);
                if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL)) {
                    IRel rel = this.toInterface(IRel.class);
                    IObject end1 = rel.GetEnd1();
                    if (end1 != null) {
                        end1.GetEnd1Relationships().remove(this);
                        if (!end1.Lock().isWriteLocked())
                            ((IObjectDefault) end1).resetValidateFlag();
                    }
                    IObject end2 = rel.GetEnd2();
                    if (end2 != null) {
                        end2.GetEnd2Relationships().remove(this);
                        if (!end2.Lock().isWriteLocked())
                            ((IObjectDefault) end2).resetValidateFlag();
                    }
                }
            } else {
                switch (this.UpdateState()) {
                    case deleted:
                    case terminated:
                        CIMContext.Instance.Transaction().RefreshItemsToSerialize().add(this);
                        if (this.hasInterface(interfaceDefinitionType.IRel.toString())) {
                            IRel rel = this.toInterface(IRel.class);
                            IObject end1 = rel.GetEnd1();
                            if (end1 != null)
                                end1.GetEnd1Relationships().add(rel);

                            IObject end2 = rel.GetEnd2();
                            if (end2 != null)
                                end2.GetEnd2Relationships().add(rel);
                        }
                        if (this.ClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
                            this.toInterface(IObject.class).setTerminationDate(null);
                        break;
                    default:
                        Iterator<Map.Entry<String, IInterface>> i = this.Interfaces().GetEnumerator();
                        List<IInterface> lcolInterfaceDefUIDsToBeRemoved = new ArrayList<>();
                        while (i.hasNext()) {
                            IInterface anInterface = i.next().getValue();
                            if (anInterface.UpdateState() == interfaceUpdateState.created || anInterface.UpdateState() == interfaceUpdateState.revive)
                                lcolInterfaceDefUIDsToBeRemoved.add(anInterface);
                            else {
                                anInterface.setInterfaceUpdateState(none);
                                Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
                                List<IProperty> propertiesToBeRemoved = new ArrayList<>();
                                while (p.hasNext()) {
                                    IProperty property = p.next().getValue();
                                    IPropertyValue[] arrPropertyValues = new IPropertyValue[property.PropertyValues().size()];
                                    property.PropertyValues().copyTo(arrPropertyValues, 0);
                                    for (IPropertyValue propertyValue : arrPropertyValues) {
                                        switch (propertyValue.UpdateState()) {
                                            case revive:
                                            case created:
                                                property.PropertyValues().remove(propertyValue);
                                                break;
                                            case deleted:
                                            case terminated:
                                                propertyValue.setTerminationDate(null);
                                                propertyValue.setUpdateState(propertyValueUpdateState.none);
                                                propertyValue.setIsTemporaryValue(false);
                                                break;
                                        }
                                    }
                                    if (property.PropertyValues().size() == 0)
                                        propertiesToBeRemoved.add(property);
                                }
                                if (propertiesToBeRemoved.size() > 0) {
                                    for (IProperty property : propertiesToBeRemoved) {
                                        anInterface.Properties().remove(property);
                                    }
                                }
                            }
                        }
                        if (lcolInterfaceDefUIDsToBeRemoved.size() > 0) {
                            for (IInterface anInterface : lcolInterfaceDefUIDsToBeRemoved) {
                                this.Interfaces().remove(anInterface);
                            }
                        }
                        break;
                }
                this.ClassBase().setUpdateState(objectUpdateState.none);
                this.clearDeferredInfo();
            }
        } finally {
            //  log.trace("after roll back progress finally to un-lock write lock anyway");
            ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
        }
    }

    @Override
    public void FinishUpdate() throws Exception {
        if (CIMContext.Instance.Transaction().inTransaction())
            CIMContext.Instance.Transaction().add(this);
        ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
    }

    @Override
    public IRelCollection getRels(String relOfEdgeDefUID) throws Exception {
        IRelCollection relCollection = null;
        if (relOfEdgeDefUID.startsWith("+") || relOfEdgeDefUID.startsWith("-") || relOfEdgeDefUID.endsWith("_12") || relOfEdgeDefUID.endsWith("_21")) {
            String lstrActualDef = relOfEdgeDefUID.substring(1);
            if (relOfEdgeDefUID.substring(0, 1).equalsIgnoreCase("+"))
                relCollection = this.GetEnd1Relationships().GetRels(lstrActualDef);
            else if (relOfEdgeDefUID.substring(0, 1).equalsIgnoreCase("-"))
                relCollection = this.GetEnd2Relationships().GetRels(lstrActualDef);
            else if (relOfEdgeDefUID.endsWith("_12"))
                relCollection = this.GetEnd1Relationships().GetRels(lstrActualDef.substring(0, lstrActualDef.indexOf("_12")));
            else if (relOfEdgeDefUID.endsWith("_21"))
                relCollection = this.GetEnd2Relationships().GetRels(lstrActualDef.substring(0, lstrActualDef.indexOf("_21")));
        } else
            relCollection = this.GetEnd1Relationships().GetRels(relOfEdgeDefUID);
        return relCollection;
    }

    @Override
    public String GetIconName() throws Exception {
        IInterface anInterface = this.toInterface(interfaceDefinitionType.IObject.toString());
        return anInterface.OnGetIconNamePrefix() + anInterface + OnGetIconNameSuffix();
    }

    @Override
    public String OnGetIconNamePrefix() {
        return this.ClassDefinitionUID();
    }

    @Override
    public String OnGetIconNameSuffix() {
        return "";
    }

    public String GetUID() throws Exception {
        if ((StringUtils.isEmpty(this.UID()) || !this.mblnHasUIDBeenGenerated) && this.UpdateState() == interfaceUpdateState.created) {
            super.ClassBase().generateUID();
        }
        return this.UID();
    }

    public String GenerateUniqueKey() throws Exception {
        if (StringUtils.isEmpty(this.UniqueKey())) {
            super.ClassBase().generateUniqueKey();
        }
        return this.UniqueKey();
    }

    public static void filling(LiteObject liteObject, IInterfaceCollection interfaceCollection) throws Exception {
        if (liteObject != null && interfaceCollection != null && interfaceCollection.hasInterface()) {
            String objOBID = liteObject.getOBID();
            Iterator<Map.Entry<String, IInterface>> entryIterator = interfaceCollection.GetEnumerator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, IInterface> interfaceEntry = entryIterator.next();
                MetaDataObjInterface metaDataObjInterface = new MetaDataObjInterface();
                metaDataObjInterface.setObjObid(objOBID);
                metaDataObjInterface.setInterfaceDefUid(interfaceEntry.getValue().InterfaceDefinitionUID());
                metaDataObjInterface.setObid(interfaceEntry.getValue().InterfaceOBID());
                metaDataObjInterface.setTerminationDate(ValueConvertService.Instance.Date(interfaceEntry.getValue().InterfaceTerminationDate()));
                metaDataObjInterface.setUpdateState(interfaceEntry.getValue().UpdateState());
                liteObject.setInterface(metaDataObjInterface);
                if (interfaceEntry.getValue().Properties().hasProperty()) {
                    Iterator<Map.Entry<String, IProperty>> iterator = interfaceEntry.getValue().Properties().GetEnumerator();
                    while (iterator.hasNext()) {
                        IProperty property = iterator.next().getValue();
                        MetaDataObjProperty metaDataObjProperty = new MetaDataObjProperty();
                        metaDataObjProperty.setInterfaceDefUid(interfaceEntry.getKey());
                        metaDataObjProperty.setObjObid(objOBID);
                        metaDataObjProperty.setInterfaceObid(interfaceEntry.getValue().InterfaceOBID());
                        metaDataObjProperty.setPropertyDefUid(property.getPropertyDefinitionUid());
                        Object value = property.Value();
                        metaDataObjProperty.setStrValue(value != null ? value.toString() : "");
                        metaDataObjProperty.setUom(property.getUom());
                        metaDataObjProperty.setTerminationDate(ValueConvertService.Instance.Date(property.getTerminationDate()));
                        metaDataObjProperty.setUpdateState(property.CurrentValue().UpdateState());
                        liteObject.setProperty(metaDataObjProperty);
                    }

                }
            }
        }
    }

    public static MetaDataRel toRelEntity(IObject object) throws Exception {
        if (object != null) {
            MetaDataRel metaDataRel = new MetaDataRel();
            metaDataRel.setObid(object.OBID());
            metaDataRel.setObjUid(object.UID());
            metaDataRel.setDomainUid(object.DomainUID());
            metaDataRel.setConfig(object.Config());
            metaDataRel.setCreationDate(object.CreationDate());
            metaDataRel.setCreationUser(object.CreationUser());
            metaDataRel.setTerminationUser(object.TerminationUser());
            metaDataRel.setTerminationDate(object.TerminationDate());
            IRel rel = object.toInterface(IRel.class);
            if (rel != null) {
                metaDataRel.setUid1(rel.UID1());
                metaDataRel.setUid2(rel.UID2());
                metaDataRel.setDomainUid1(rel.DomainUID1());
                metaDataRel.setDomainUid2(rel.DomainUID2());
                metaDataRel.setClassDefinitionUid1(rel.ClassDefinitionUID1());
                metaDataRel.setClassDefinitionUid2(rel.ClassDefinitionUID2());
                metaDataRel.setName1(rel.Name1());
                metaDataRel.setName2(rel.Name2());
                metaDataRel.setObid1(rel.OBID1());
                metaDataRel.setObid2(rel.OBID2());
                metaDataRel.setRelDefUid(rel.RelDefUID());
                metaDataRel.setPrefix(rel.Prefix());
                metaDataRel.setOrderValue(rel.OrderValue());
                metaDataRel.setIsRequired(rel.IsRequired());
            }
            metaDataRel.setUpdateState(object.ObjectUpdateState());
            return metaDataRel;
        }
        return null;
    }

    public static MetaDataObj toObjEntity(IObject object) {
        if (object != null) {
            MetaDataObj metaDataObj = new MetaDataObj();
            metaDataObj.setDomainUid(object.DomainUID());
            metaDataObj.setObjUid(object.UID());
            metaDataObj.setObid(object.OBID());
            metaDataObj.setName(object.Name());
            metaDataObj.setDescription(object.Description());
            metaDataObj.setConfig(object.Config());
            metaDataObj.setClassDefinitionUid(object.ClassDefinitionUID());
            metaDataObj.setUpdateState(object.ObjectUpdateState());
            metaDataObj.setUniqueKey(object.UniqueKey());
            metaDataObj.setCreationDate(object.CreationDate());
            metaDataObj.setCreationUser(object.CreationUser());
            metaDataObj.setLastUpdateUser(object.LastUpdateUser());
            metaDataObj.setLastUpdateDate(object.LastUpdateDate());
            metaDataObj.setTerminationUser(object.TerminationUser());
            metaDataObj.setTerminationDate(object.TerminationDate());
            return metaDataObj;
        }
        return null;
    }
}
