package ccm.server.schema.model;

import ccm.server.context.CIMContext;
import ccm.server.convert.impl.ValueConvertService;
import ccm.server.enums.*;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.interfaces.defaults.IObjectDefault;
import ccm.server.util.CommonUtility;
import ccm.server.util.ReentrantLockUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class PropertyBase implements IProperty {
    private IInterface parent;
    private final String propertyDefinitionUid;
    private propertyValueUpdateState propertyValueUpdateState;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private IPropertyValueCollection propertyValues;
    private IPropertyValue currentValue;
    protected boolean dynamical = false;

    @Override
    public void setParent(IInterface objInterface) {
        this.parent = objInterface;
    }

    @Override
    public void terminateProperty() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null) {
            iPropertyValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.terminated);
            this.setTerminationDate(new Date().toString());
        }
    }

    @Override
    public void deleteProperty() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null) {
            iPropertyValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.deleted);
        }
    }

    @Override
    public IObjectCollection getEnumEntries() throws Exception {
        if (this.isEnumList()) {
            IObject scopedBy = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(this.getPropertyDefinitionUid());
            return scopedBy.toInterface(IEnumListType.class).getEntries();
        } else if (this.isEnumListLevel()) {

        }
        return null;
    }

    public PropertyBase(String propertyDefinitionUid) {
        this.propertyDefinitionUid = propertyDefinitionUid;
    }

    @Override
    public String getPropertyDefinitionUid() {
        return this.propertyDefinitionUid;
    }

    @Override
    public boolean Dynamical() {
        return this.dynamical;
    }

    @Override
    public void setDynamical(boolean value) {
        this.dynamical = value;
    }

    @Override
    public boolean isTerminated() {
        return !StringUtils.isEmpty(this.getTerminationDate());
    }

    @Override
    public IPropertyValueCollection PropertyValues() {
        if (this.propertyValues != null)
            return this.propertyValues;
        try {
            ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
            if (this.propertyValues == null) {
                this.propertyValues = new PropertyValueCollection(this);
                if (this.currentValue != null) {
                    this.propertyValues.add(this.currentValue);
                    this.currentValue = null;
                }
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        } finally {
            ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
        }
        return this.propertyValues;
    }

    @Override
    public IPropertyValue CurrentValue() {
        if (this.propertyValues != null && this.propertyValues.size() > 0) {
            return this.propertyValues.latestValue();
        }
        return this.currentValue;
    }

    @Override
    public Object Value() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null) {
            return iPropertyValue.Value();
        }
        return null;
    }

    @Override
    public String getObid() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null) {
            return iPropertyValue.OBID();
        }
        return null;
    }

    @Override
    public void setObid(String obid) {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null)
            iPropertyValue.setOBID(obid);
    }

    @Override
    public void setValue(Object value) throws Exception {
        this.setValue(value, null);
    }

    private String onGenerateOBID() {
        String lstrOBID = UUID.randomUUID().toString();
        if (this.getParent() != null) {
            if (this.getParent().ClassBase().UpdateState() != objectUpdateState.instantiated) {
                lstrOBID = CIMContext.Instance.generateOBIDForProperty();
            }
        }
        return lstrOBID;
    }

    @Override
    public void setValue(Object value, String uom) throws Exception {
        IInterface lobjInterface = this.getParent();
        if (lobjInterface == null)
            throw new Exception("invalid interface instance for current property:" + this.getPropertyDefinitionUid());
        ClassBase classBase = lobjInterface.ClassBase();
        if (classBase == null)
            throw new Exception("class base is not valid as it is null:" + lobjInterface.InterfaceDefinitionUID() + "," + this.getPropertyDefinitionUid());
        if (classBase.Lock() == null)
            throw new Exception("no lock found for class base:" + lobjInterface.ClassDefinitionUID() + "," + lobjInterface);
        if (this.dynamical) {
            this.currentValue = new PropertyValue(this, value, null, uom, this.onGenerateOBID(), null, ccm.server.enums.propertyValueUpdateState.created);
            IPropertyValueCollection valueCollection = this.PropertyValues();
        } else {
            if (!classBase.Lock().isWriteLocked())
                throw new Exception("cannot to set value for property as it is out of date");
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.lock);
                log.trace("current property " + this.propertyDefinitionUid + " value status:" + (this.propertyValueUpdateState != null ? this.propertyValueUpdateState.toString() : "NULL"));
                ccm.server.enums.propertyValueUpdateState state = this.identifySetValueAction(value, uom);
                this.propertyValueUpdateState = state;
                switch (state) {
                    case created:
                        log.trace("start to create a property value");
                        String lstrOBID = "";
                        IObjectDefault iObject = (IObjectDefault) this.parent.toInterface(IObject.class);
                        if (iObject.NeedsInflation())
                            lstrOBID = "INFLATION_TEMP";
                        else
                            lstrOBID = this.onGenerateOBID();
                        IPropertyValue newPropertyValue = new PropertyValue(this, value, null, uom, lstrOBID, null, ccm.server.enums.propertyValueUpdateState.created);
                        IPropertyValue currentPropertyValue = this.CurrentValue();
                        if (currentPropertyValue != null) {
                            if (currentPropertyValue.UpdateState() == ccm.server.enums.propertyValueUpdateState.created) {
                                log.trace("current value is not NULL, status is created and value is same with new one, will remove it from property value collection directly");
                                this.PropertyValues().remove(currentPropertyValue);
                            } else if (currentPropertyValue.UpdateState() != ccm.server.enums.propertyValueUpdateState.created && currentPropertyValue.CreationDate().equalsIgnoreCase(newPropertyValue.CreationDate())) {
                                log.trace("current value is not NULL, status is not created and creation date is different from new one, will set to be Deleted");
                                currentPropertyValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.deleted);
                            } else {
                                log.trace("current value is not NULL and terminated it directly");
                                currentPropertyValue.setTerminationDate(new Date().toString());
                                currentPropertyValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.terminated);
                            }
                            log.trace("add new property value into value collection");
                            this.PropertyValues().add(newPropertyValue);
                            if (this.getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinitionType.UID.toString()) && this.PropertyValues().size() > 1) {
                                if (CIMContext.Instance.ProcessCache().containsByOBID(iObject.OBID())) {
                                    CIMContext.Instance.ProcessCache().removeByOBID(iObject.OBID());
                                    CIMContext.Instance.ProcessCache().removeByUID(currentPropertyValue.Value().toString());
                                    CIMContext.Instance.ProcessCache().addLocally(iObject);
                                }
                                iObject.mstrCachedUID = newPropertyValue.Value().toString();
                            }
                        } else {
                            log.trace("current value is null and clear value collection, reset current value with new one");
                            this.currentValue = newPropertyValue;
                            IPropertyValueCollection valueCollection = this.PropertyValues();
                        }
                        break;
                    case updated:
                        log.trace("start to update a property for current value directly");
                        IPropertyValue myUpdatedCurrentValue = this.CurrentValue();
                        if (myUpdatedCurrentValue != null) {
                            myUpdatedCurrentValue.setValue(value);
                            myUpdatedCurrentValue.setUoM(uom);
                            myUpdatedCurrentValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.updated);
                        }
                        break;
                    case terminated:
                        log.trace("start to terminate a property");
                        IPropertyValue myTerminatedCurrentValue = this.CurrentValue();
                        if (myTerminatedCurrentValue != null) {
                            myTerminatedCurrentValue.setTerminationDate(new Date().toString());
                            myTerminatedCurrentValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.terminated);
                        }
                        break;
                    case none:
                        IPropertyValue currentValue1 = this.CurrentValue();
                        if (currentValue1 != null)
                            currentValue1.setUpdateState(ccm.server.enums.propertyValueUpdateState.none);
                        break;
                    default:
                        break;
                }
                IPropertyValue currentValue1 = this.CurrentValue();
                log.trace("**** after set Value operation: current Value [" + (currentValue1 != null ? currentValue1.Value() : "NULL") + "] and value collection quantity:" + (this.propertyValues != null ? this.propertyValues.size() : "NULL"));
            } catch (Exception exception) {
                log.error("set value on property base failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.lock);
            }
        }
    }

    protected void resetUpdateStateIfTerminatedOrDeleted() {
        log.trace("enter to reset Update State if terminated or deleted");
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null) {
            ccm.server.enums.propertyValueUpdateState propertyValueUpdateState = iPropertyValue.UpdateState();
            log.trace("current Value is not NULL and state:" + iPropertyValue.UpdateState().toString());
            if (propertyValueUpdateState == ccm.server.enums.propertyValueUpdateState.deleted || propertyValueUpdateState == ccm.server.enums.propertyValueUpdateState.terminated)
                iPropertyValue.setUpdateState(ccm.server.enums.propertyValueUpdateState.none);
        }
    }

    protected propertyValueUpdateState identifySetValueAction(Object value, String pstrUoM) throws Exception {
        IPropertyValue iPropertyValue = this.CurrentValue();
        log.trace("enter to identify set value action with provided value:" + (value != null ? value.toString() : "NULL") + ", UoM:" + pstrUoM);
        if (iPropertyValue == null) {
            log.trace("current Value is NULL and set Value action is Created");
            return ccm.server.enums.propertyValueUpdateState.created;
        } else if (iPropertyValue.UpdateState() == ccm.server.enums.propertyValueUpdateState.created) {
            return ccm.server.enums.propertyValueUpdateState.created;
        } else if ((value == null || StringUtils.isEmpty(value.toString())) && CIMContext.Instance.ProcessCache().isStringTypeThatCannotBeZeroLength(this.getPropertyDefinitionUid())) {
            log.trace("current Value is not allow to be empty and set action is Terminated");
            return ccm.server.enums.propertyValueUpdateState.terminated;
        } else {
            if (value != null) {
                if (!this.hasPropertyChanged(value.toString())) {
                    this.resetUpdateStateIfTerminatedOrDeleted();
                    log.trace("value is not changed and set value action is None");
                    return iPropertyValue.UpdateState();
                }
            }
            if (CIMContext.Instance.ProcessCache().isPropertyDefinitionHistoryRetained(this.getPropertyDefinitionUid())) {
                log.trace(this.getPropertyDefinitionUid() + "'s value should be saved in every time and set action is Created");
                return ccm.server.enums.propertyValueUpdateState.created;
            } else {
                ccm.server.enums.propertyValueUpdateState propertyValueUpdateState = iPropertyValue.UpdateState();
                if (propertyValueUpdateState == ccm.server.enums.propertyValueUpdateState.created) {
                    log.trace("current Value's status is created and set action is Created");
                    return ccm.server.enums.propertyValueUpdateState.created;
                } else {
                    log.trace("last set action is Updated");
                    return ccm.server.enums.propertyValueUpdateState.updated;
                }
            }
        }
    }

    private boolean hasPropertyChangedAsUoM(String pstrNewValue) throws Exception {
        String lstrValue = null;
        if (this.Value() != null)
            lstrValue = this.Value().toString();
        IEnumEnum lobjUoMEntry = this.getUoMEntry();
        String lstrNewValueWithTrim = pstrNewValue != null ? pstrNewValue.trim() : "";
        String lstrNewValue = "";
        String lstrNewUoM = "";
        int lintTildaSeq = lstrNewValueWithTrim.lastIndexOf("~");
        int lintSpaceSeq = lstrNewValueWithTrim.lastIndexOf(" ");
        if (lintTildaSeq > 0) {
            lstrNewValue = lstrNewValueWithTrim.substring(0, lintTildaSeq);
            lstrNewUoM = lstrNewValueWithTrim.substring(lintTildaSeq + 1);
        } else if (lintTildaSeq == 0)
            lstrNewUoM = lstrNewValueWithTrim.substring(lintTildaSeq + 1);
        else if (lintSpaceSeq > 0) {
            lstrNewValue = lstrNewValueWithTrim.substring(0, lintSpaceSeq);
            lstrNewUoM = lstrNewValueWithTrim.substring(lintSpaceSeq + 1);
        } else if (lintSpaceSeq == -1) {
            lstrNewValue = lstrNewValueWithTrim;
            lstrNewUoM = "";
        } else {
            lstrNewValue = "";
            lstrNewUoM = lstrNewValueWithTrim;
        }
        IEnumEnum lobjNewUoMEntry = this.getUoMEntry(lstrNewUoM);
        boolean lblnUoMChanged = false;
        if (lobjNewUoMEntry == null && lobjUoMEntry == null && !StringUtils.isEmpty(lstrNewUoM))
            lblnUoMChanged = true;
        String lstrOldEnumUID = "";
        String lstrNewEnumUID = "";
        if (lobjUoMEntry != null)
            lstrOldEnumUID = lobjUoMEntry.OBID();
        if (lobjNewUoMEntry != null)
            lstrNewEnumUID = lobjNewUoMEntry.OBID();
        return !lstrNewValue.equalsIgnoreCase(lstrValue) || lblnUoMChanged || !lstrOldEnumUID.equalsIgnoreCase(lstrNewEnumUID);
    }

    protected boolean hasPropertyChangedAsEnumListType(String pstrNewValue) throws Exception {
        IEnumEnum enumListEntry = this.getEnumListEntry();
        IEnumEnum enumListEntry1 = this.getEnumListEntry(pstrNewValue);
        String lstrCurrent = "";
        String lstrNew = "";
        if (enumListEntry != null)
            lstrCurrent = enumListEntry.OBID();
        if (enumListEntry1 != null)
            lstrNew = enumListEntry1.OBID();
        return !lstrCurrent.equalsIgnoreCase(lstrNew);
    }

    protected boolean hasPropertyChangedAsEnumListLevelType(String pstrNewValue) throws Exception {
        IEnumEnum enumListEntry = this.getEnumListLevelType();
        IEnumEnum enumListEntry1 = this.getEnumListLevelType(pstrNewValue);
        String lstrCurrent = "";
        String lstrNew = "";
        if (enumListEntry != null)
            lstrCurrent = enumListEntry.OBID();
        if (enumListEntry1 != null)
            lstrNew = enumListEntry1.OBID();
        return !lstrCurrent.equalsIgnoreCase(lstrNew);
    }

    public boolean hasPropertyChanged(String pstrNewValue) throws Exception {
        log.trace("enter to hasPropertyChanged with new value:" + pstrNewValue);
        boolean result = false;
        if (pstrNewValue != null && StringUtils.isEmpty(pstrNewValue) && this.parent.toInterface(IObject.class).ClassBase().UpdateState() == objectUpdateState.created)
            result = true;
        else if (pstrNewValue != null && this.CurrentValue() == null)
            result = true;
        else if ((pstrNewValue == null && this.Value() != null) && StringUtils.isEmpty(this.Value().toString()))
            result = true;
        else if (this.isUoM())
            result = this.hasPropertyChangedAsUoM(pstrNewValue);
        else if (this.isEnumList())
            result = this.hasPropertyChangedAsEnumListType(pstrNewValue);
        else if (this.isEnumListLevel())
            result = this.hasPropertyChangedAsEnumListLevelType(pstrNewValue);
        else {
            String lstrValue = null;
            if (this.Value() != null)
                lstrValue = this.Value().toString();
            if (lstrValue == null)
                lstrValue = "NULL";
            if (pstrNewValue == null)
                pstrNewValue = "NULL";
            if (this.isBoolean() || this.isDouble() || this.isInteger() || this.isDate() || this.isYMD())
                result = !pstrNewValue.equalsIgnoreCase(lstrValue);
            else
                result = !pstrNewValue.equals(lstrValue);
        }
        log.trace("property changed result:" + result);
        return result;
    }

    @Override
    public boolean isUoM() {
        if (this.dynamical)
            return false;
        IPropertyDef propertyDefinition = this.getPropertyDefinition();
        if (propertyDefinition != null) {
            String definition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.propertyDefinitionUid);
            return definition != null && definition.equalsIgnoreCase(propertyDefinitionType.UoMListType.toString());
        }
        return false;
    }

    @Override
    public boolean isBoolean() {
        IPropertyDef propertyDefinition = this.getPropertyDefinition();
        if (propertyDefinition != null) {
            String definition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.propertyDefinitionUid);
            return definition != null && definition.equalsIgnoreCase(propertyValueType.BooleanType.toString());
        }
        return false;
    }

    @Override
    public boolean isDouble() {
        IPropertyDef propertyDefinition = this.getPropertyDefinition();
        if (propertyDefinition != null) {
            String definition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.propertyDefinitionUid);
            return definition != null && definition.equalsIgnoreCase(propertyValueType.DoubleType.toString());
        }
        return false;
    }

    @Override
    public boolean isDate() {
        IPropertyDef propertyDefinition = this.getPropertyDefinition();
        if (propertyDefinition != null) {
            String definition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.propertyDefinitionUid);
            return definition != null && definition.equalsIgnoreCase(propertyValueType.DateTimeType.toString());
        }
        return false;
    }

    @Override
    public boolean isYMD() {
        IPropertyDef propertyDefinition = this.getPropertyDefinition();
        if (propertyDefinition != null) {
            String definition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.propertyDefinitionUid);
            return definition != null && definition.equalsIgnoreCase(propertyValueType.YMDType.toString());
        }
        return false;
    }

    @Override
    public boolean isInteger() {
        IPropertyDef propertyDefinition = this.getPropertyDefinition();
        if (propertyDefinition != null) {
            String definition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.propertyDefinitionUid);
            return definition != null && definition.equalsIgnoreCase(propertyValueType.IntegerType.toString());
        }
        return false;
    }

    @Override
    public boolean isValidValue() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        return iPropertyValue == null || iPropertyValue.isValidValue();
    }

    @Override
    public boolean isUpdatable() throws Exception {
        if (this.dynamical)
            return true;
        boolean result = true;
        Object value = this.Value();
        if (value != null && !StringUtils.isEmpty(value.toString())) {
            String propertyDefinitionUID = this.getPropertyDefinitionUid();
            try {
                propertyDefinitionType definitionType = propertyDefinitionType.valueOf(propertyDefinitionUID);
                switch (definitionType) {
                    case OBID:
                    case Config:
                    case DomainUID:
                    case DomainUID1:
                    case DomainUID2:
                    case UID:
                    case OBID1:
                    case OBID2:
                    case CreationDate:
                        result = false;
                }
            } catch (IllegalArgumentException e) {
                //log.warn(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public String toShortDisplayValue() throws Exception {
        Object value = this.Value();
        if (this.dynamical)
            return value != null ? value.toString() : "";

        if (this.isUoM()) {
            if (value != null && !StringUtils.isEmpty(value.toString())) {
                IEnumEnum uoMEntry = this.getUoMEntry();
                if (uoMEntry != null) {
                    return value.toString() + "~" + uoMEntry.Name();
                }
                return value.toString();
            }
        } else if (this.isEnumList()) {
            IEnumEnum enumListEntry = this.getEnumListEntry();
            if (enumListEntry != null)
                return enumListEntry.Name();
        } else if (this.isEnumListLevel()) {
            IEnumEnum enumListLevelType = this.getEnumListLevelType();
            if (enumListLevelType != null)
                return enumListLevelType.Name();
        } else if (value != null)
            return value.toString();
        return "";
    }

    @Override
    public String toDisplayValue() {
        IObjectDefault objectDefault = (IObjectDefault) this.parent.toInterface(IObject.class);
        if (objectDefault.NeedsInflation()) {
            try {
                objectDefault.InflateObjectThatCameFromCache();
            } catch (Exception exception) {
                log.error("inflate object failed", exception);
            }
        }
        Object value = this.Value();
        if (this.dynamical)
            return value != null ? value.toString() : "";

        if (value != null) {
            try {
                if (this.isUoM()) {
                    if (!StringUtils.isEmpty(value.toString())) {
                        IEnumEnum uoMEntry = this.getUoMEntry();
                        if (uoMEntry != null)
                            return value.toString() + "~" + uoMEntry.DisplayName();
                        return value.toString();
                    }
                } else if (this.isEnumList()) {
                    IEnumEnum enumListEntry = this.getEnumListEntry();
                    if (enumListEntry != null)
                        return enumListEntry.Name();
                } else if (this.isEnumListLevel()) {
                    IEnumEnum enumListLevelType = this.getEnumListLevelType();
                    if (enumListLevelType != null) {
                        return value.toString() + ":" + enumListLevelType.DisplayName();
                    }
                } else {
                    if (value instanceof Date) {
                        return CommonUtility.formatDateWithDateFormat((Date) value);
                    } else
                        return value.toString();
                }
            } catch (Exception exception) {
                log.error("to display value failed", exception);
            }
        }
        return "";
    }

    public IPropertyDef getPropertyDefinition() {
        IObject item = CIMContext.Instance.ProcessCache().item(this.getPropertyDefinitionUid(), domainInfo.SCHEMA.toString(), false);
        if (item != null)
            return item.toInterface(IPropertyDef.class);
        return null;
    }

    @Override
    public IEnumEnum getEnumListEntry() throws Exception {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null)
            return iPropertyValue.getEnumListEntry();
        return null;
    }

    @Override
    public IEnumEnum getEnumListEntry(String pstrValue) throws Exception {
        if (pstrValue == null || StringUtils.isEmpty(pstrValue))
            return null;
        IEnumEnum listLevelType = CIMContext.Instance.ProcessCache().getEnumListLevelType(this.getPropertyDefinitionUid(), pstrValue);
        if (listLevelType != null)
            return listLevelType.toInterface(IEnumEnum.class);
        return null;
    }

    protected IObject getObjectByUIDOrName(String pstrUIDOrName, IObjectCollection pcolCollection) {
        if (!StringUtils.isEmpty(pstrUIDOrName) && pcolCollection != null && pcolCollection.hasValue()) {
            IObject item = pcolCollection.item(pstrUIDOrName);
            if (item == null)
                item = pcolCollection.get(queryTypes.name, pstrUIDOrName);
            return item;
        }
        return null;
    }

    @Override
    public IEnumEnum getEnumListEntry(String pstrValue, IObjectCollection pcolCollection) {
        if (pstrValue == null || StringUtils.isEmpty(pstrValue))
            return null;
        IObject object = this.getObjectByUIDOrName(pstrValue, pcolCollection);
        if (object == null && pstrValue.startsWith("@"))
            object = this.getObjectByUIDOrName(pstrValue.substring(1), pcolCollection);
        if (object == null && pstrValue.lastIndexOf(":") > -1)
            object = this.getObjectByUIDOrName(pstrValue.substring(0, pstrValue.lastIndexOf(":")), pcolCollection);
        if (object == null && pstrValue.startsWith("#") && ValueConvertService.Instance.isNumeric(pstrValue.substring(1))) {
            int lintNumber = Integer.parseInt(pstrValue.substring(1));
            Iterator<IObject> iObjectIterator = pcolCollection.GetEnumerator();
            while (iObjectIterator.hasNext()) {
                IEnumEnum enumEnum = iObjectIterator.next().toInterface(IEnumEnum.class);
                if (enumEnum.EnumNumber() == lintNumber) {
                    object = enumEnum;
                    break;
                }
            }
        }
        if (object != null)
            return object.toInterface(IEnumEnum.class);
        return null;
    }

    @Override
    public IEnumEnum getEnumListLevelType() throws Exception {
        return this.getEnumListLevelType(this.CurrentValue());
    }

    @Override
    public IEnumEnum getEnumListLevelType(IPropertyValue propertyValue) throws Exception {
        if (propertyValue != null)
            return propertyValue.getEnumListLevelType();
        return null;
    }

    @Override
    public IEnumEnum getEnumListLevelType(String pstrValue) throws Exception {
        if (!StringUtils.isEmpty(pstrValue)) {
            IEnumEnum enumListLevelType = CIMContext.Instance.ProcessCache().getEnumListLevelType(this.getPropertyDefinitionUid(), pstrValue);
            if (enumListLevelType != null)
                return enumListLevelType.toInterface(IEnumEnum.class);
        }
        return null;
    }

    @Override
    public IEnumEnum getEnumListLevelType(IEnumListType pobjEnumList, String pstrValue, int pintTargetLevel,
                                          int pintCurrentLevel) throws Exception {
        if (pobjEnumList != null) {
            if (pintCurrentLevel == pintTargetLevel)
                return this.getEnumListEntry(pstrValue, pobjEnumList.getEntries());
            else if (pintCurrentLevel < pintTargetLevel) {
                Iterator<IObject> iObjectIterator = pobjEnumList.getEntries().GetEnumerator();
                while (iObjectIterator.hasNext()) {
                    IObject next = iObjectIterator.next();
                    if (next.IsTypeOf(IEnumListType.class.getSimpleName())) {
                        IEnumListType enumListType = next.toInterface(IEnumListType.class);
                        IEnumEnum current = this.getEnumListLevelType(enumListType, pstrValue, pintTargetLevel, pintCurrentLevel++);
                        if (current != null)
                            return current;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public IEnumEnum getUoMEntry() throws Exception {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null) {
            return iPropertyValue.getUoMEntry();
        }
        return null;
    }

    @Override
    public IUoMEnum getUoMEntry(String pstrValue) throws Exception {
        if (!StringUtils.isEmpty(pstrValue)) {
            IEnumEnum enumListEntry = this.getEnumListEntry(pstrValue);
            if (enumListEntry != null && enumListEntry.IsTypeOf(IUoMEnum.class.getSimpleName()))
                return enumListEntry.toInterface(IUoMEnum.class);
        }
        return null;
    }

    @Override
    public boolean isEnumList() {
        if (this.dynamical)
            return false;
        String classDefForPropertyDefinition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.getPropertyDefinitionUid());
        return classDefForPropertyDefinition.equals(classDefinitionType.EnumListType.toString());
    }

    @Override
    public boolean isEnumListLevel() {
        if (this.dynamical)
            return false;
//        try {
//            propertyDefinitionType propertyDefinitionType = ccm.server.module.enums.propertyDefinitionType.valueOf(this.getPropertyDefinitionUid());
//            return false;
//        } catch (Exception exception) {
//            //log.warn(exception.getMessage(), exception);
//        }
        String classDefForPropertyDefinition = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.getPropertyDefinitionUid());
        return classDefForPropertyDefinition.equals(classDefinitionType.EnumListLevelType.toString());
    }

    @Override
    public String getUom() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        return iPropertyValue != null ? iPropertyValue.UoM() : null;
    }

    @Override
    public void setUom(String uom) {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null)
            iPropertyValue.setUoM(uom);
    }

    @Override
    public String getTerminationDate() {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null)
            return iPropertyValue.TerminationDate();
        return null;
    }

    @Override
    public void setTerminationDate(String terminationDate) {
        IPropertyValue iPropertyValue = this.CurrentValue();
        if (iPropertyValue != null)
            iPropertyValue.setTerminationDate(terminationDate);
    }

    @Override
    public IInterface getParent() {
        return this.parent;
    }
}
