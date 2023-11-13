package ccm.server.schema.model;

import ccm.server.args.*;
import ccm.server.context.CIMContext;
import ccm.server.convert.impl.ValueConvertService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.ObjectXmlDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.entity.MetaDataObj;
import ccm.server.entity.MetaDataObjInterface;
import ccm.server.enums.*;
import ccm.server.model.DynamicalDefinitionObj;
import ccm.server.schema.collections.IInterfaceCollection;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IPropertyCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.InterfaceCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.collections.impl.PropertyCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.interfaces.defaults.IObjectDefault;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.util.ReentrantLockUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Slf4j
public abstract class InterfaceBase implements IInterface {
    private final String IENTITY = interfaceDefinitionType.IObject.toString();
    private final String ISCHEMAENTITY = interfaceDefinitionType.ISchemaObject.toString();
    private String interfaceObid;
    private String interfaceCreationUser;
    private String interfaceCreationDate;
    private String interfaceTerminationUser;
    private String interfaceTerminationDate;
    private ClassBase parent;
    protected String interfaceDefinitionUid;
    private final IPropertyCollection properties = new PropertyCollection(this);
    private final boolean instantiateRequiredProperties;
    private boolean createdAndTerminated = false;
    private interfaceUpdateState updateState;
    private final List<String> processedInterfaceDefs = new ArrayList<>();
    private IObjectDefault mobjIObject;
    private boolean dynamical = false;

    @Override
    public int InterfaceSequence() {
        try {
            IInterfaceDef iInterfaceDef = this.getInterfaceDefinition();
            if (iInterfaceDef != null)
                return iInterfaceDef.InterfaceSequence();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 100;
    }

    @Override
    public List<String> getPropertiesThatCannotBeUpdated() {
        return new ArrayList<String>() {{
            this.add(propertyDefinitionType.UID.toString());
            this.add(propertyDefinitionType.OBID.toString());
            this.add(propertyDefinitionType.ClassDefinitionUID.toString());
            this.add(propertyDefinitionType.DomainUID.toString());
            this.add(propertyDefinitionType.CreationDate.toString());
            this.add(propertyDefinitionType.CreationUser.toString());
            this.add(propertyDefinitionType.LastUpdateDate.toString());
            this.add(propertyDefinitionType.LastUpdateUser.toString());
            this.add(propertyDefinitionType.TerminationDate.toString());
            this.add(propertyDefinitionType.TerminationUser.toString());
        }};
    }

    @Override
    public boolean underConfigInd(String configurationUid) {
        if (!CIMContext.Instance.ProcessCache().isDefinitionUnderConfigControl(this.ClassDefinitionUID()))
            return true;
        if (StringUtils.isEmpty(configurationUid)) {
            return true;
        }
        String config = this.Config();
        if (config.equalsIgnoreCase(configurationUid)) {
            return true;
        }
        return config.toUpperCase().contains(configurationUid.toUpperCase());
    }

    @Override
    public String getConfigForMetaData() {
        String result = "";
        String config = this.Config();
        if (!StringUtils.isEmpty(config)) {
            List<String> temp = new ArrayList<>();
            String[] strings1 = config.split(";");
            for (String s : strings1) {
                if (StringUtils.isEmpty(s))
                    temp.add("");
                else {
                    String[] strings = config.split("<<>>");
                    temp.add(strings[0]);
                }
            }
            result = String.join(";", temp);
        }
        return result;
    }

    @Override
    public void amountDate(Date date, String userName) throws Exception {
        switch (this.ObjectUpdateState()) {
            case updated:
                this.setLastUpdateDate(date);
                this.setLastUpdateUser(userName);
                break;
            case terminated:
                this.setTerminationDate(date);
                this.setTerminationUser(userName);
                break;
            case created:
                this.setCreationDate(date);
                this.setCreationUser(userName);
                break;
        }
        Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            IInterface anInterface = e.next().getValue();
            switch (anInterface.UpdateState()) {
                case created:
                case revive:
                    anInterface.setInterfaceCreationDate(DateUtils.formatDate(date, ValueConvertService.SUPPORTED_DATE_FORMATS[0]));
                    anInterface.setInterfaceCreationUser(userName);
                    break;
                case terminated:
                    anInterface.setInterfaceTerminationDate(DateUtils.formatDate(date, ValueConvertService.SUPPORTED_DATE_FORMATS[0]));
                    anInterface.setInterfaceTerminationUser(userName);
                    break;
            }

            Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
            while (p.hasNext()) {
                IProperty property = p.next().getValue();
                IPropertyValueCollection propertyValueCollection = property.PropertyValues();
                if (propertyValueCollection != null) {
                    Iterator<IPropertyValue> valueIterator = propertyValueCollection.GetEnumerator();
                    while (valueIterator.hasNext()) {
                        IPropertyValue propertyValue = valueIterator.next();
                        switch (propertyValue.UpdateState()) {
                            case revive:
                            case created:
                            case updated:
                                propertyValue.setCreationDate(DateUtils.formatDate(date, ValueConvertService.SUPPORTED_DATE_FORMATS[0]));
                                break;
                            case terminated:
                                propertyValue.setTerminationDate(DateUtils.formatDate(date, ValueConvertService.SUPPORTED_DATE_FORMATS[0]));
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public IObject copyTo() throws Exception {
        StopWatch stopWatch = PerformanceUtility.start();
        String classDefinitionUID = this.ClassDefinitionUID();
        IClassDef classDef = CIMContext.Instance.ProcessCache().item(classDefinitionUID, domainInfo.SCHEMA.toString()).toInterface(IClassDef.class);
        if (classDef == null)
            throw new Exception("invalid class definition " + classDefinitionUID);
        IObject result = SchemaUtility.newIObject(classDefinitionUID, this.Name(), this.Description(), this.DomainUID(), null);
        if (result != null) {
            Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
            while (e.hasNext()) {
                IInterface anInterface = e.next().getValue();
                if (anInterface.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString()))
                    continue;
                IInterface anInterface1 = result.Interfaces().item(anInterface.InterfaceDefinitionUID());
                Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
                while (p.hasNext()) {
                    IProperty property = p.next().getValue();
                    anInterface1.Properties().item(property.getPropertyDefinitionUid(), true).setValue(property.Value());
                }
            }
        }
        classDef.FinishCreate(result);
        log.info("copy to complete" + PerformanceUtility.stop(stopWatch));
        return result;
    }

    @Override
    public String generateDisplayAs() {
        return new ArrayList<String>() {{
            this.add(Name());
            this.add(Description());
        }}.stream().filter(c -> !StringUtils.isEmpty(c)).distinct().collect(Collectors.joining(","));
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
    public void fillingProperties(@NotNull List<ObjectItemDTO> pcolProperties, boolean pblnNeedUpdateDesc) throws Exception {
        try {
            if (pblnNeedUpdateDesc) {
                String lstrName = CommonUtility.getSpecialValueFromProperties(pcolProperties, propertyDefinitionType.Name.toString());
                String lstrDesc = CommonUtility.getSpecialValueFromProperties(pcolProperties, propertyDefinitionType.Description.toString());
                //手动更新名称和描述

                IPropertyCollection properties = this.Interfaces().get(interfaceDefinitionType.IObject.toString()).Properties();
                if (properties.get(propertyDefinitionType.Description.toString()) == null) {
                    properties.item(propertyDefinitionType.Description.toString(), true);
                }
                if (properties.get(propertyDefinitionType.Name.toString()) == null) {
                    properties.item(propertyDefinitionType.Name.toString(), true);
                }
                this.Interfaces().get(interfaceDefinitionType.IObject.toString()).Properties().get(propertyDefinitionType.Name.toString()).setValue(lstrName);
                this.Interfaces().get(interfaceDefinitionType.IObject.toString()).Properties().get(propertyDefinitionType.Description.toString()).setValue(lstrDesc);
            }
            //批量更新其他属性
            Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
            while (e.hasNext()) {
                Map.Entry<String, IInterface> interfaceEntry = e.next();
                //IObject对象不更新信息
                String lstrInterfaceDefUID = interfaceEntry.getKey();
                if (interfaceDefinitionType.IObject.toString().equals(lstrInterfaceDefUID)) continue;
                IInterface lobjInterface = interfaceEntry.getValue();
                IPropertyCollection properties = lobjInterface.Properties();
                //遍历对象的所有属性
                for (ObjectItemDTO prop : pcolProperties) {
                    String lstrPropDef = prop.getDefUID();
                    //获取暴露接口
                    String lstrExposeInterfaceDef = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(lstrPropDef);
                    if (!StringUtils.isEmpty(lstrExposeInterfaceDef) && lstrExposeInterfaceDef.equalsIgnoreCase(lstrInterfaceDefUID)) {
                        //更新属性
                        IProperty lobjProp = properties.get(lstrPropDef);
                        if (lobjProp == null) {
                            lobjProp = properties.item(lstrPropDef, true);
                        }
                        if (lobjProp != null) {
                            lobjProp.setValue(prop.getDisplayValue());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.toString());
            ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            throw ex;
        }

    }

    @Override
    public void fillingInterfaces(String[] parrInterfaces) throws Exception {
        if (parrInterfaces != null && parrInterfaces.length > 0) {
            try {
                String classDefinitionUid = this.ClassDefinitionUID();
                List<String> realizedInterfaceDefs = CIMContext.Instance.ProcessCache().getRealizedInterfaceDefByClassDef(classDefinitionUid, false);
                if (CommonUtility.hasValue(realizedInterfaceDefs)) {
                    for (String interfaceDefinitionUid : parrInterfaces) {
                        if (realizedInterfaceDefs.contains(interfaceDefinitionUid)) {
                            if (!this.Interfaces().hasInterface(interfaceDefinitionUid)) {
                                IInterface interfaceDefinition = this.Interfaces().item(interfaceDefinitionUid, true);
                                if (interfaceDefinition == null)
                                    throw new Exception("实例化接口:" + interfaceDefinitionUid + "失败!");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
                ex.printStackTrace();
                log.error(ex.toString());
                throw ex;
            }
        }
    }

    @Override
    public void fillingInterfaces(@NotNull JSONObject obj) throws Exception {
        String[] interfaces = SchemaUtility.parseInterfaces(obj);
        fillingInterfaces(interfaces);
    }

    @Override
    public void fillingInterfaceAndProperties(Map<String, Map<String, String>> interfaceProperties) throws Exception {
        if (interfaceProperties != null && interfaceProperties.size() > 0) {
            try {
                for (Map.Entry<String, Map<String, String>> property : interfaceProperties.entrySet()) {
                    String interfaceDef = property.getKey();
                    IInterface lobjInterface = this.Interfaces().get(interfaceDef);
                    if (lobjInterface == null) {
                        lobjInterface = this.Interfaces().item(interfaceDef, true);
                    }
                    if (lobjInterface == null)
                        throw new Exception(interfaceDef + " is valid interfaceDef");

                    Map<String, String> propertyValue = property.getValue();
                    if (propertyValue != null && propertyValue.size() > 0) {
                        for (Map.Entry<String, String> entry : propertyValue.entrySet()) {
                            String propertyDef = entry.getKey();
                            String value = entry.getValue();
                            IProperty lobjProperty = lobjInterface.Properties().item(propertyDef, true);
                            if (lobjProperty != null)
                                lobjProperty.setValue(value);
                            else
                                throw new Exception("invalid property definition " + property.getKey() + " as it is not defined by schema");
                        }
                    }
                }
            } catch (Exception ex) {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
                log.error(ex.toString());
                throw ex;
            }
        }
    }

    @Override
    public void fillingProperties(boolean ignoreNullOrEmptyProperty, List<Map.Entry<String, Object>> properties) throws Exception {
        if (CommonUtility.hasValue(properties)) {
            String errorProperty = "";
            Object errorValue = null;
            try {
                for (Map.Entry<String, Object> propertyDefAndValue : properties) {
                    String propertyDefinitionUid = propertyDefAndValue.getKey();
                    errorProperty = propertyDefinitionUid;
                    if (!SchemaUtility.validatePropDefNeedFilling(propertyDefinitionUid)) continue;
                    String interfaceDefinitionUid = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(propertyDefAndValue.getKey());
                    Object value = propertyDefAndValue.getValue();
                    errorValue = value;
                    if (!StringUtils.isEmpty(interfaceDefinitionUid)) {
                        IInterface interfaceDef = this.Interfaces().get(interfaceDefinitionUid);
                        boolean flag = value == null || StringUtils.isEmpty(value.toString());
                        if (ignoreNullOrEmptyProperty) {
                            if (flag) {
                                continue;
                            }
                        }
                        if (interfaceDef != null) {
                            IProperty property = interfaceDef.Properties().hasProperty(propertyDefinitionUid) ? interfaceDef.Properties().get(propertyDefinitionUid) : interfaceDef.Properties().item(propertyDefinitionUid, true);
                            if (property != null) {
                                IPropertyDef propertyDefinition = property.getPropertyDefinition();
                                IPropertyType scopedByPropertyType = propertyDefinition.getScopedByPropertyType();
                                propertyValueType valueType;
                                if (scopedByPropertyType.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyType.toString()))
                                    valueType = propertyValueType.valueOf(scopedByPropertyType.Name());
                                else
                                    valueType = propertyValueType.valueOf(scopedByPropertyType.ClassDefinitionUID());
                                switch (valueType) {
                                    case StringType:
                                    case EnumListType:
                                    case EnumListLevelType:
                                        value = value == null ? null : value.toString();
                                        break;
                                    case BooleanType:
                                        value = ValueConversionUtility.toBoolean(value);
                                        break;
                                    case IntegerType:
                                        value = ValueConversionUtility.toInteger(value);
                                        break;
                                    case DoubleType:
                                        value = ValueConversionUtility.toDouble(value);
                                        break;
                                    case YMDType:
                                    case DateTimeType:
                                        value = ValueConvertService.Instance.Date(value);
                                        break;
                                }
                                property.setValue(value);
                            } else {
                                throw new Exception("无效的属性定义: " + propertyDefinitionUid + " ,请检查Schema");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
                log.error("filling properties error! error property is \"{}\",error value is \"{}\",error message is {}.", errorProperty, errorValue, ExceptionUtil.getMessage(ex), ExceptionUtil.getRootCause(ex));
                throw new Exception("filling properties error! error property is \"" + errorProperty + "\", error value is \"" + errorValue + "\", error message is" + ExceptionUtil.getSimpleMessage(ex));
            }
        }
    }

    @Override
    public void fillingProperties(@NotNull JSONObject object) throws Exception {
        List<Map.Entry<String, Object>> properties = SchemaUtility.parseProperties(object);
        fillingProperties(false, properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {

        }
        return super.equals(obj);
    }

    @Override
    public void fillingForObjectDTO(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null) {
            for (ObjectItemDTO objectDTOItem : objectDTO.getItems()) {
                String defUid = objectDTOItem.getDefUID();
                classDefinitionType defType = objectDTOItem.getDefType();
                if (defType == classDefinitionType.RelDef || defType == classDefinitionType.EdgeDef || defType == classDefinitionType.Rel) {
                    String[] strings = defUid.split("\\.");
                    String relDefUID = CommonUtility.toActualDefinition(strings[0]);
                    String propertyDefUID = strings.length > 1 ? strings[1] : "Name";
                    IObjectCollection endObjs = null;
                    IRelCollection rels = null;
                    if (defType == classDefinitionType.Rel) {
                        switch (objectDTOItem.getRelDirection()) {
                            case _1To2:
                                rels = this.GetEnd1Relationships().GetRels(relDefUID);
                                break;
                            case _2To1:
                                rels = this.GetEnd2Relationships().GetRels(relDefUID);
                                break;
                        }
                        if (rels != null && rels.hasValue()) {
                            List<String> collect = rels.toList().stream().map(c -> c.getValue(propertyDefUID)).filter(c -> !StringUtils.isEmpty(c)).distinct().sorted().collect(Collectors.toList());
                            if (collect.size() > 0) {
                                objectDTOItem.setDisplayValue(String.join(";", collect));
                            } else
                                objectDTOItem.setDisplayValue("");
                        }
                    } else {
                        switch (objectDTOItem.getRelDirection()) {
                            case _1To2:
                                endObjs = this.GetEnd1Relationships().GetRels(relDefUID).GetEnd2s();
                                break;
                            case _2To1:
                                endObjs = this.GetEnd2Relationships().GetRels(relDefUID).GetEnd1s();
                                break;
                        }
                        if (endObjs != null && endObjs.hasValue()) {
                            List<String> collect = endObjs.toList().stream().map(c -> c.getValue(propertyDefUID)).filter(c -> !StringUtils.isEmpty(c)).distinct().sorted().collect(Collectors.toList());
                            if (collect.size() > 0) {
                                objectDTOItem.setDisplayValue(String.join(";", collect));
                            } else
                                objectDTOItem.setDisplayValue("");
                        }
                    }
                } else {
                    String value = this.getValue(defUid);
                    if (value != null && !StringUtils.isEmpty(value)) {
                        if (objectDTOItem.hasOptions()) {
                            String finalValue = value;
                            OptionItemDTO optionItemDTO = objectDTOItem.getOptions().stream().filter(c -> c.getName().equalsIgnoreCase(finalValue) || c.getUid().equalsIgnoreCase(finalValue)).findFirst().orElse(null);
                            if (optionItemDTO != null)
                                value = optionItemDTO.getName();
                        }
                        objectDTOItem.setDisplayValue(value);
                    } else
                        objectDTOItem.setDisplayValue("");
                }
            }
        }

    }

    @Override
    public boolean isIObjectOrIRel() {
        return this.InterfaceDefinitionUID() != null && (this.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString()) || this.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IRel.toString()));
    }

    @Override
    public boolean terminatedOrDeleted() {
        return this.updateState != null && (this.updateState == interfaceUpdateState.terminated || this.updateState == interfaceUpdateState.deleted);
    }

    @Override
    public void setValue(String propertyDefinitionUID, Object value) throws Exception {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            String interfaceByPropertyDef = CIMContext.Instance.ProcessCache().getExposedInterfaceByPropertyDef(propertyDefinitionUID);
            if (StringUtils.isEmpty(interfaceByPropertyDef))
                throw new Exception("exposed NULL interface for property definition " + propertyDefinitionUID);
            IInterface anInterface = this.Interfaces().get(interfaceByPropertyDef);
            if (anInterface == null)
                anInterface = this.Interfaces().item(interfaceByPropertyDef, true);
            if (anInterface != null) {
                IProperty property = anInterface.Properties().item(propertyDefinitionUID, true);
                if (property == null)
                    throw new Exception("initialized property failed for " + propertyDefinitionUID);
                property.setValue(value);
            }
        }
    }

    @Override
    public String getDisplayValue(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
            while (e.hasNext()) {
                IInterface anInterface = e.next().getValue();
                Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
                while (p.hasNext()) {
                    IProperty property = p.next().getValue();
                    if (property.getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinitionUID)) {
                        return property.toDisplayValue();
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getValue(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
            while (e.hasNext()) {
                IInterface anInterface = e.next().getValue();
                Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
                while (p.hasNext()) {
                    IProperty property = p.next().getValue();
                    if (property.getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinitionUID)) {
                        try {
                            IPropertyValue propertyValue = property.CurrentValue();
                            if (propertyValue != null) {
                                Object value = propertyValue.Value();
                                if (value != null) {
                                    if (value instanceof Date) {
                                        // 2022.07.22 HT 日期时间少8小时问题修复
                                        Date dateValue = (Date) value;
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        return sdf.format(dateValue);
                                        // 2022.07.22 HT 日期时间少8小时问题修复
                                    } else {
                                        return value.toString();
                                    }
                                }
                                return "";
                            }
                        } catch (Exception ex) {
                            log.error(ex.getMessage());
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String toErrorPop() {
        return this.ClassDefinitionUID() + "," + this.DomainUID() + "->" + this.Name() + " with " + this.UID() + "," + this.OBID();
    }

    @Override
    public boolean fromDb() {
        if (this.UpdateState() != interfaceUpdateState.none)
            return false;
        Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
        while (entryIterator.hasNext()) {
            IInterface anInterface = entryIterator.next().getValue();
            if (anInterface.UpdateState() != interfaceUpdateState.none)
                return false;
            Iterator<Map.Entry<String, IProperty>> entryIterator1 = anInterface.Properties().GetEnumerator();
            while (entryIterator1.hasNext()) {
                IProperty property = entryIterator1.next().getValue();
                IPropertyValue propertyValue = property.CurrentValue();
                if (propertyValue != null && propertyValue.UpdateState() != propertyValueUpdateState.none)
                    return false;
            }
        }
        return true;
    }

    @Override
    public MetaDataObjInterface toDataInterface() {
        MetaDataObjInterface dataObjInterface = new MetaDataObjInterface();
        dataObjInterface.setObid(this.InterfaceOBID());
        dataObjInterface.setObjObid(this.OBID());
        dataObjInterface.setCreationDate(ValueConvertService.Instance.Date(this.InterfaceCreationDate()));
        dataObjInterface.setCreationUser(this.InterfaceCreationUser());
        dataObjInterface.setInterfaceDefUid(this.InterfaceDefinitionUID());
        dataObjInterface.setTerminationDate(ValueConvertService.Instance.Date(this.InterfaceTerminationDate()));
        dataObjInterface.setTerminationUser(this.InterfaceTerminationUser());
        dataObjInterface.setUpdateState(this.UpdateState());
        return dataObjInterface;
    }

    @Override
    public IObjectCollection toIObjectCollection() {
        IObjectCollection collection = new ObjectCollection();
        collection.append(this);
        return collection;
    }

    @Override
    public abstract MetaDataObj toMetaDataObject() throws Exception;

    @Override
    public void selfCheck() throws Exception {
        log.info("do self check with abstract, you have to extend by yourself");
    }

    @Override
    public abstract void OnCreate(createArgs e) throws Exception;

    @Override
    public abstract void OnCreating(cancelArgs e) throws Exception;

    @Override
    public abstract void OnCreated(suppressibleArgs e) throws Exception;

    @Override
    public abstract void OnDelete(suppressibleArgs e) throws Exception;

    @Override
    public abstract void OnDeleting(cancelArgs e) throws Exception;

    @Override
    public abstract void OnDeleted(suppressibleArgs e) throws Exception;

    @Override
    public abstract void OnPreProcess(createArgs e) throws Exception;

    @Override
    public abstract void OnRelationshipAdd(relArgs e) throws Exception;

    @Override
    public abstract void OnRelationshipAdded(relArgs e) throws Exception;

    @Override
    public abstract void OnRelationshipAdding(relArgs e) throws Exception;

    @Override
    public abstract void OnRelationshipRemoved(relArgs e) throws Exception;

    @Override
    public abstract void OnRelationshipRemoving(relArgs e) throws Exception;

    @Override
    public abstract void OnTerminate(suppressibleArgs e) throws Exception;

    @Override
    public abstract void OnTerminating(cancelArgs e) throws Exception;

    @Override
    public abstract void OnTerminated(suppressibleArgs e) throws Exception;

    @Override
    public abstract void OnUpdate(updateArgs e) throws Exception;

    @Override
    public abstract void OnUpdated(suppressibleArgs e) throws Exception;

    @Override
    public abstract void OnUpdating(cancelArgs e) throws Exception;

    @Override
    public abstract void OnValidate(validateArgs e) throws Exception;

    @Override
    public boolean hasProperty(String propertyDefinition) {
        if (!StringUtils.isEmpty(propertyDefinition)) {
            Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                Iterator<Map.Entry<String, IProperty>> entryIterator1 = entryIterator.next().getValue().Properties().GetEnumerator();
                while (entryIterator1.hasNext()) {
                    if (entryIterator1.next().getValue().getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinition))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public IProperty getProperty(String interfaceDefinitionUID, String propertyDefinitionUID) {
        if (StringUtils.isEmpty(propertyDefinitionUID))
            return null;
        if (StringUtils.isEmpty(interfaceDefinitionUID))
            return this.getProperty(propertyDefinitionUID);
        IInterface anInterface = this.Interfaces().get(interfaceDefinitionUID);
        if (anInterface != null) {
            return anInterface.getProperty(propertyDefinitionUID);
        }
        return null;
    }

    @Override
    public IProperty getProperty(String propertyDefinitionUID) {
        if (!StringUtils.isEmpty(propertyDefinitionUID)) {
            Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
            while (e.hasNext()) {
                IInterface value = e.next().getValue();
                Iterator<Map.Entry<String, IProperty>> p = value.Properties().GetEnumerator();
                while (p.hasNext()) {
                    IProperty property = p.next().getValue();
                    if (property.getPropertyDefinitionUid().equalsIgnoreCase(propertyDefinitionUID))
                        return property;
                }
            }
        }
        return null;
    }

    @Override
    public <T> T toInterface(Class<T> tClass) {
        if (tClass != null) {
            String interfaceDefUID = tClass.getSimpleName();
            IInterface anInterface = this.parent.Interfaces().get(interfaceDefUID);
            if (anInterface != null)
                return (T) anInterface;
        }
        return null;
    }

    private boolean InterfaceDefinitionUIDIsSet() {
        return !StringUtils.isEmpty(this.interfaceDefinitionUid);
    }

    @Override
    public IInterfaceCollection Interfaces() {
        return this.parent.Interfaces();
    }

    @Override
    public IClassDef ClassDefinition() throws Exception {
        return this.parent.ClassDefinition();
    }

    @Override
    public IRelCollection GetEnd1Relationships() throws Exception {
        return this.parent.GetEnd1Relationships();
    }

    @Override
    public IRelCollection GetEnd2Relationships() throws Exception {
        return this.parent.GetEnd2Relationships();
    }

    @Override
    public abstract void commit();

    @Override
    public abstract IObject Copy() throws Exception;

    public InterfaceBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        this.instantiateRequiredProperties = instantiateRequiredProperties;
        this.interfaceDefinitionUid = interfaceDefinitionUid;
        this.updateState = interfaceUpdateState.none;
        this.createdAndTerminated = false;
//        this.setInterfaceCreationDate(new Date().toString());
//        this.setInterfaceCreationUser(CIMContext.Instance.userName());
    }

    @Override
    public objectUpdateState ObjectUpdateState() {
        return this.ClassBase().UpdateState();
    }

    @Override
    public String InterfaceDefinitionUID() {
        return this.interfaceDefinitionUid;
    }

    @Override
    public IPropertyCollection Properties() {
        return this.properties;
    }

    @Override
    public String InterfaceOBID() {
        return this.interfaceObid;
    }

    @Override
    public void setInterfaceOBID(String interfaceObid) {
        this.interfaceObid = interfaceObid;
    }

    @Override
    public void setInterfaceCreationDate(String interfaceCreationDate) {
        this.interfaceCreationDate = interfaceCreationDate;
    }

    @Override
    public String InterfaceCreationDate() {
        return this.interfaceCreationDate;
    }

    @Override
    public void setInterfaceTerminationDate(String interfaceTerminationDate) {
        this.interfaceTerminationDate = interfaceTerminationDate;
    }

    @Override
    public String InterfaceTerminationDate() {
        return this.interfaceTerminationDate;
    }

    @Override
    public void setInterfaceTerminationUser(String interfaceTerminationUser) {
        this.interfaceTerminationUser = interfaceTerminationUser;
    }

    @Override
    public String InterfaceTerminationUser() {
        return this.interfaceTerminationUser;
    }

    @Override
    public void setInterfaceCreationUser(String interfaceCreationUser) {
        this.interfaceCreationUser = interfaceCreationUser;
    }

    @Override
    public String InterfaceCreationUser() {
        return this.interfaceCreationUser;
    }

    @Override
    public interfaceUpdateState UpdateState() {
        return this.updateState;
    }

    @Override
    public boolean CreateAndTerminated() {
        return this.createdAndTerminated;
    }

    @Override
    public void setInterfaceUpdateState(interfaceUpdateState interfaceUpdateState) {
        this.updateState = interfaceUpdateState;
        if (interfaceUpdateState == ccm.server.enums.interfaceUpdateState.terminated && this.updateState == ccm.server.enums.interfaceUpdateState.created)
            this.createdAndTerminated = true;
    }

    @Override
    public String OBID() {
        return this.Interfaces().getPropertyValue(IENTITY, "OBID");
    }

    @Override
    public void setOBID(String obid) throws Exception {
        this.setPropertyValue(IENTITY, "OBID", obid);
    }

    @Override
    public String UID() {
        return this.Interfaces().getPropertyValue(IENTITY, "UID");
    }

    @Override
    public void setUID(String uid) throws Exception {
        this.setPropertyValue(IENTITY, "UID", uid);
    }

    @Override
    public String Name() {
        String name = this.Interfaces().getPropertyValue(IENTITY, "Name");
        if (name == null)
            name = "";
        return name;
    }

    @Override
    public void setName(String name) throws Exception {
        this.setPropertyValue(IENTITY, "Name", name);
    }

    @Override
    public String ContainerID() {
        return this.Interfaces().getPropertyValue(IENTITY, "ContainerId");
    }

    @Override
    public void setContainerID(String containerId) throws Exception {
        this.setPropertyValue(IENTITY, "ContainerId", containerId);
    }

    @Override
    public String Description() {
        return this.Interfaces().getPropertyValue(IENTITY, "Description");
    }

    @Override
    public void setDescription(String description) throws Exception {
        this.setPropertyValue(IENTITY, "Description", description);
    }

    @Override
    public String Config() {
        return this.Interfaces().getPropertyValue(IENTITY, "Config");
    }

    @Override
    public void setConfig(String config) throws Exception {
        this.setPropertyValue(IENTITY, "Config", config);
    }

    @Override
    public abstract ICIMConfigurationItem getConfig() throws Exception;

    @Override
    public abstract boolean IsUniqueKeyUniqueInConfig() throws Exception;

    @Override
    public abstract boolean IsUniqueKeyUnique() throws Exception;

    @Override
    public String DomainUID() {
        String domainUID = this.Interfaces().getPropertyValue(IENTITY, "DomainUID");
        if (StringUtils.isEmpty(domainUID))
            domainUID = domainInfo.UNKNOWN.toString();
        return domainUID;
    }

    @Override
    public void setDomainUID(String domainUid) throws Exception {
        this.setPropertyValue(IENTITY, "DomainUID", domainUid);
    }

    @Override
    public Date LastUpdateDate() {
        String creationDate = this.Interfaces().getPropertyValue(IENTITY, "LastUpdateDate");
        if (!StringUtils.isEmpty(creationDate)) {
            return ValueConvertService.Instance.Date(creationDate);
        }
        return null;
    }

    @Override
    public void setLastUpdateDate(Date date) throws Exception {
        this.setPropertyValue(IENTITY, "LastUpdateDate", date);
    }

    @Override
    public Date CreationDate() {
        String creationDate = this.Interfaces().getPropertyValue(IENTITY, "CreationDate");
        if (!StringUtils.isEmpty(creationDate)) {
            return ValueConvertService.Instance.Date(creationDate);
        }
        return null;
    }

    @Override
    public void setCreationDate(Date date) throws Exception {
        this.setPropertyValue(IENTITY, "CreationDate", date);
    }

    @Override
    public String CreationUser() {
        return this.Interfaces().getPropertyValue(IENTITY, "CreationUser");
    }

    @Override
    public void setCreationUser(String creationUser) throws Exception {
        this.setPropertyValue(IENTITY, "CreationUser", creationUser);
    }

    @Override
    public String TerminationUser() {
        String terminationUser = this.Interfaces().getPropertyValue(IENTITY, "TerminationUser");
        if (StringUtils.isEmpty(terminationUser))
            return null;
        return terminationUser;
    }

    @Override
    public void setTerminationUser(String terminationUser) throws Exception {
        this.setPropertyValue(IENTITY, "TerminationUser", terminationUser);
    }

    @Override
    public Date TerminationDate() {
        String creationDate = this.Interfaces().getPropertyValue(IENTITY, "TerminationDate");
        if (!StringUtils.isEmpty(creationDate)) {
            return ValueConvertService.Instance.Date(creationDate);
        }
        return null;
    }

    @Override
    public void setTerminationDate(Date terminationDate) throws Exception {
        this.setPropertyValue(IENTITY, "TerminationDate", terminationDate);
    }

    @Override
    public String LastUpdateUser() {
        return this.Interfaces().getPropertyValue(IENTITY, "lastUpdateUser");
    }

    @Override
    public void setLastUpdateUser(String lastUpdateUser) throws Exception {
        this.setPropertyValue(IENTITY, "lastUpdateUser", lastUpdateUser);
    }

    @Override
    public String ClassDefinitionUID() {
        return this.parent.ClassDefinitionUID();
    }

    @Override
    public void setClassDefinitionUID(String classDefinitionUid) throws Exception {
        this.parent.setClassDefinitionUid(classDefinitionUid);
    }

    @Override
    public abstract void Delete() throws Exception;

    @Override
    public abstract void Delete(boolean pblnSuppressEvent) throws Exception;

    @Override
    public abstract void doDelete() throws Exception;

    @Override
    public void setParentCollection(IObjectCollection parentCollection) {
        this.parent.setParentCollection(parentCollection);
    }

    @Override
    public abstract void Terminate() throws Exception;

    @Override
    public abstract void Terminate(boolean pblnSuppressEvent) throws Exception;

    @Override
    public abstract boolean Validate() throws Exception;

    @Override
    public abstract void BeginUpdate() throws Exception;

    @Override
    public abstract void BeginUpdate(boolean pblnValidateForClaim) throws Exception;

    @Override
    public abstract void BeginUpdate(boolean pblnValidateForClaim, boolean pblnSuppressEvents) throws Exception;

    @Override
    public abstract void rollback() throws Exception;

    @Override
    public IInterface toInterface(String interfaceDefUID) throws Exception {
        if (!StringUtils.isEmpty(interfaceDefUID)) {
            Iterator<Map.Entry<String, IInterface>> entryIterator = this.parent.Interfaces().GetEnumerator();
            while (entryIterator.hasNext()) {
                IInterface lobjInterface = entryIterator.next().getValue();
                if (lobjInterface.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString()))
                    return lobjInterface;
                else {
                    Class<?>[] interfaces = lobjInterface.getClass().getInterfaces();
                    if (CommonUtility.hasValue(interfaces)) {
                        List<String> stringList = Arrays.stream(interfaces).map(Class::getName).collect(Collectors.toList());
                        if (stringList.contains(interfaceDefUID))
                            return lobjInterface;
                    }
                }
            }
            return this.Interfaces().item(interfaceDefUID);
        }
        return null;
    }

    @Override
    public abstract void OnCopy(copyArgs e) throws Exception;

    @Override
    public abstract void OnCopies(copyArgs e) throws Exception;

    @Override
    public abstract void OnCopying(cancelArgs e) throws Exception;

    @Override
    public abstract void OnCreatingValidation(cancelArgs e) throws Exception;

    @Override
    public abstract void OnRelationshipUpdating(relArgs e) throws Exception;

    @Override
    public abstract void OnUpdatingValidation(cancelArgs e) throws Exception;

    @Override
    public boolean IsTypeOf(String interfaceDefUID) {
        return this.Interfaces().hasInterface(interfaceDefUID);
    }

    @Override
    public abstract void FinishUpdate() throws Exception;

    @Override
    public abstract IRelCollection getRels(String relOfEdgeDefUID) throws Exception;

    @Override
    public abstract String GetIconName() throws Exception;

    @Override
    public abstract String OnGetIconNamePrefix() throws Exception;

    @Override
    public abstract String OnGetIconNameSuffix() throws Exception;

    @Override
    public boolean IsInitialized() {
        return true;
    }

    @Override
    public IObjectCollection ParentCollection() {
        return this.parent.ParentCollection();
    }

    @Override
    public boolean InstantiateRequiredItems() {
        return this.ClassBase().InstantiateRequiredItems();
    }

    @Override
    public boolean Deleted() {
        return this.ClassBase().UpdateState() == objectUpdateState.deleted;
    }

    @Override
    public boolean Terminated() {
        return this.ClassBase().UpdateState() == objectUpdateState.terminated;
    }

    @Override
    public ReentrantReadWriteLock Lock() {
        return this.ClassBase().Lock();
    }

    @Override
    public boolean CanUpdate() {
        return this.ClassBase().Lock().isWriteLocked();
    }

    @Override
    public String UniqueKey() {
        return this.Interfaces().getPropertyValue(IENTITY, "UniqueKey");
    }

    @Override
    public void setUniqueKey(String uniqueKey) throws Exception {
        this.setPropertyValue(IENTITY, "UniqueKey", uniqueKey);
    }

    @Override
    public void setPropertyValue(String interfaceDefinitionUid, String propertyDefinitionUid, Object value) throws Exception {
        if (!StringUtils.isEmpty(interfaceDefinitionUid) && !StringUtils.isEmpty(propertyDefinitionUid)) {
            IInterface entityInterface = this.Interfaces().item(interfaceDefinitionUid);
            if (entityInterface != null) {
                IProperty entityProperty = entityInterface.Properties().item(propertyDefinitionUid);
                if (entityProperty != null)
                    entityProperty.setValue(value);
            }
        }
    }

    @Override
    public boolean hasInterface(String interfaceDefinitionUid) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid))
            return this.Interfaces().hasInterface(interfaceDefinitionUid);
        return false;
    }

    @Override
    public void setClass(ClassBase classBase) throws Exception {
        this.parent = classBase;
        if (this.parent != null && this.instantiateRequiredProperties) {
            this.doInstantiateRequiredProperties();
        }
    }

    @Override
    public ClassBase ClassBase() {
        return this.parent;
    }

    private void doInstantiateRequiredProperties() throws Exception {
        IInterfaceDef definition = this.getInterfaceDefinition();
        if (definition != null) {
            IRelCollection relCollection = definition.GetEnd1Relationships().GetRels(relDefinitionType.exposes.toString(), true);
            if (relCollection != null && relCollection.hasValue()) {
                Iterator<IObject> enumerator = relCollection.GetEnumerator();
                while (enumerator.hasNext()) {
                    IRel rel = enumerator.next().toInterface(IRel.class);
                    if (rel.IsRequired() && this.Properties().hasProperty(rel.UID2()) && this.parent.Lock().isWriteLocked()) {
                        IPropertyDef propertyDef = (IPropertyDef) rel.GetEnd2().Interfaces().item(interfaceDefinitionType.IPropertyDef.toString());
                        IProperty property = (IProperty) propertyDef.Instantiate(this.parent.IObject().InstantiateRequiredItems());
                        if (!StringUtils.isEmpty(propertyDef.DefaultValue()))
                            property.setValue(propertyDef.DefaultValue());
                        this.Properties().add(property);
                    }
                }
            }
        }
    }

    @Override
    public void deleteInterface() throws Exception {
        if (this.Lock().isWriteLocked()) {
            if (this.UpdateState() == interfaceUpdateState.deleted)
                return;
            if (!this.ClassDefinition().GetEnd1Relationships().GetRel(relDefinitionType.realizes.toString(), this.InterfaceDefinitionUID()).IsRequired()) {
                this.setInterfaceUpdateState(interfaceUpdateState.deleted);
                Iterator<Map.Entry<String, IProperty>> entryIterator = this.Properties().GetEnumerator();
                while (entryIterator.hasNext()) {
                    entryIterator.next().getValue().deleteProperty();
                }
                this.deleteOrTerminateRels(objectUpdateState.deleted);
                Iterator<Map.Entry<String, IInterface>> entryIterator1 = this.getImpliedByInterfacesForTermination().GetEnumerator();
                while (entryIterator1.hasNext()) {
                    entryIterator1.next().getValue().deleteInterface();
                }
            } else
                throw new Exception("current interface is required for Class Definition, cannot delete it:" + this.InterfaceDefinitionUID());
        }
    }

    @Override
    public void terminateInterface() throws Exception {
        if (this.UpdateState() == interfaceUpdateState.terminated)
            return;
        if (this.Lock().isWriteLocked()) {
            boolean isRequired = false;
            IRel realizedRel = this.GetClassDefinition().GetEnd1Relationships().GetRel(relDefinitionType.realizes.toString(), this.InterfaceDefinitionUID());
            if (realizedRel != null)
                isRequired = realizedRel.IsRequired();
            if (!isRequired) {
                this.setInterfaceUpdateState(interfaceUpdateState.terminated);
                this.setInterfaceTerminationDate(new Date().toString());
                this.setInterfaceTerminationUser(CIMContext.Instance.getLoginUserName());
                Iterator<Map.Entry<String, IProperty>> entryIterator = this.Properties().GetEnumerator();
                while (entryIterator.hasNext()) {
                    entryIterator.next().getValue().terminateProperty();
                }
                this.deleteOrTerminateRels(objectUpdateState.terminated);
                Iterator<Map.Entry<String, IInterface>> entryIterator1 = this.getImpliedByInterfacesForTermination().GetEnumerator();
                while (entryIterator1.hasNext()) {
                    entryIterator1.next().getValue().terminateInterface();
                }
            } else
                throw new Exception("cannot terminate interface:" + this.InterfaceDefinitionUID() + " as it is required by Class Definition:" + this.parent.ClassDefinitionUID());
        }
    }

    protected IInterfaceCollection getImpliedByInterfacesForTermination() throws Exception {
        IInterfaceCollection lcolInterfaces = new InterfaceCollection(this.parent);
        Iterator<Map.Entry<String, IInterface>> entryIterator = this.getActiveImpliedByInterfaces(this.InterfaceDefinitionUID()).GetEnumerator();
        while (entryIterator.hasNext()) {
            IInterface lobjInterfaceForTermination = entryIterator.next().getValue();
            boolean lblnTerminate = true;
            Iterator<Map.Entry<String, IInterface>> iterator = this.Interfaces().GetEnumerator();
            while (iterator.hasNext()) {
                IInterface lobjRealisedInterface = iterator.next().getValue();
                boolean impliedBy = lobjInterfaceForTermination.getInterfaceDefinition().isImpliedBy(lobjInterfaceForTermination.InterfaceDefinitionUID(), false);
                if (impliedBy && !lobjRealisedInterface.InterfaceDefinitionUID().equalsIgnoreCase(this.InterfaceDefinitionUID())) {
                    lblnTerminate = false;
                    break;
                }
            }
            if (lblnTerminate) {
                lcolInterfaces.add(lobjInterfaceForTermination);
            }
        }
        return lcolInterfaces;
    }

    protected IInterfaceCollection getActiveImpliedByInterfaces(String pstrImpliedInterfaceDefUID) throws Exception {
        if (!StringUtils.isEmpty(pstrImpliedInterfaceDefUID)) {
            IInterfaceCollection lcolInterfaces = new InterfaceCollection(this.parent);
            Iterator<Map.Entry<String, IInterface>> iterator = this.Interfaces().GetEnumerator();
            while (iterator.hasNext()) {
                IInterface lobjInterface = iterator.next().getValue();
                IRel realizeRel = this.ClassDefinition().GetEnd1Relationships().GetRel(relDefinitionType.realizes.toString(), lobjInterface.InterfaceDefinitionUID());
                if (realizeRel != null && realizeRel.IsRequired() && (lobjInterface.UpdateState() == interfaceUpdateState.deleted || lobjInterface.UpdateState() == interfaceUpdateState.terminated)) {
                    IInterfaceDef lobjInterfaceDef = lobjInterface.getInterfaceDefinition();
                    if (lobjInterfaceDef.isImplied(pstrImpliedInterfaceDefUID, false)) {
                        IRel lobjRel = lobjInterfaceDef.GetEnd1Relationships().GetRel(relDefinitionType.implies.toString(), pstrImpliedInterfaceDefUID);
                        if (lobjRel != null && lobjRel.IsRequired())
                            lcolInterfaces.add(lobjInterface);
                    }
                }
            }
            return lcolInterfaces;
        }
        return null;
    }

    protected void deleteOrTerminateRels(objectUpdateState objectUpdateState) throws Exception {
        IInterfaceDef definition = this.getInterfaceDefinition();
        if (definition == null)
            throw new Exception("invalid interface instance as its' definition is not valid");
        Iterator<IObject> iterator = definition.getEnd1RelDefs().GetEnumerator();
        while (iterator.hasNext()) {
            IRelDef lobjRelDef = iterator.next().toInterface(IRelDef.class);
            boolean lblnDelete12 = lobjRelDef.Delete12();
            Iterator<IObject> e1 = this.GetEnd1Relationships().GetRels(lobjRelDef.UID()).GetEnumerator();
            while (e1.hasNext()) {
                IRel lobjRel = e1.next().toInterface(IRel.class);
                IObject lobjEnd2 = lobjRel.GetEnd2();
                if (objectUpdateState == ccm.server.enums.objectUpdateState.terminated) {
                    lobjRel.Terminate();
                    if (lblnDelete12)
                        lobjEnd2.Terminate();
                } else if (objectUpdateState == ccm.server.enums.objectUpdateState.deleted) {
                    lobjRel.Delete();
                    if (lblnDelete12)
                        lobjEnd2.Delete();
                }
            }
        }

        Iterator<IObject> iterator1 = definition.getEnd2RelDefs().GetEnumerator();
        while (iterator1.hasNext()) {
            IRelDef lobjRelDef = iterator1.next().toInterface(IRelDef.class);
            boolean lblnDelete21 = lobjRelDef.Delete21();
            Iterator<IObject> e2 = this.GetEnd2Relationships().GetRels(lobjRelDef.UID()).GetEnumerator();
            while (e2.hasNext()) {
                IRel lobjRel = e2.next().toInterface(IRel.class);
                IObject lobjEnd1 = lobjRel.GetEnd1();
                if (objectUpdateState == ccm.server.enums.objectUpdateState.terminated) {
                    lobjRel.Terminate();
                    if (lblnDelete21) lobjEnd1.Terminate();
                } else if (objectUpdateState == ccm.server.enums.objectUpdateState.deleted) {
                    lobjRel.Delete();
                    if (lblnDelete21) lobjEnd1.Delete();
                }
            }
        }
    }

    @Override
    public IInterfaceDef getInterfaceDefinition() throws Exception {
        IObject interfaceDefinition = CIMContext.Instance.ProcessCache().item(this.InterfaceDefinitionUID());
        if (interfaceDefinition != null) {
            return interfaceDefinition.toInterface(IInterfaceDef.class);
        }
        return null;
    }


    @Override
    public IInterface myNext(String interfaceDefinitionUid, List<String> processedInterfaceDefs) {
        if (!StringUtils.isEmpty(interfaceDefinitionUid)) {
            boolean isIObject = interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.IObject.toString());
            boolean isSchemaObject = interfaceDefinitionUid.equalsIgnoreCase(interfaceDefinitionType.ISchemaObject.toString());
            IObjectCollection lcolIDefs = null;
            if (!isIObject && !isSchemaObject) {
                lcolIDefs = this.OnGetAllIDefsForObject(this.parent.Interfaces());
            }
            IInterfaceCollection interfaceCollection = this.parent.Interfaces();
            interfaceCollection.sort();
            int startIndex = interfaceCollection.indexOf(this);
            int maximumIndex = interfaceCollection.size() - 1;
            IInterface result = null;
            while (startIndex <= maximumIndex) {
                IInterface current = interfaceCollection.get(startIndex);
                String lstrUID = current.InterfaceDefinitionUID();
                if (isIObject) {
                    if (!current.getClass().getName().equalsIgnoreCase(this.getClass().getName()))
                        return current;
                    else {
                        startIndex++;
                        continue;
                    }
                } else {
                    if (!lstrUID.equalsIgnoreCase(interfaceDefinitionType.ISchemaObject.toString())) {
                        if (isSchemaObject) {
                            if (CIMContext.Instance.ProcessCache().schemaObjectImpliedBy().containsKey(lstrUID))
                                return current;
                        } else {
                            if (lstrUID.equalsIgnoreCase(interfaceDefinitionUid))
                                return current;
                            if (lcolIDefs != null && lcolIDefs.hasValue()) {
                                IObject item = lcolIDefs.item(lstrUID);
                                if (item != null && item.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.InterfaceDef.toString())) {
                                    if (this.isImplied(interfaceDefinitionUid, CIMContext.Instance.ProcessCache().getImpliedByIDef(lstrUID)))
                                        return current;
                                }
                            }
                        }
                    } else {
                        return current;
                    }
                }
                startIndex++;
            }
        }
        return (IInterface) this.toInterface(IObject.class);
    }

    protected boolean isImplied(String pstrInterfaceDefinitionUID, List<String> pcolImpliedIDefs) {
        boolean result = false;
        if (!StringUtils.isEmpty(pstrInterfaceDefinitionUID) && CommonUtility.hasValue(pcolImpliedIDefs)) {
            List<String> lcolDeepImpliedInterfaces = new ArrayList<>();
            result = pcolImpliedIDefs.contains(pstrInterfaceDefinitionUID);
            if (!result) {
                for (String impliedIDef : pcolImpliedIDefs) {
                    if (!this.processedInterfaceDefs.contains(impliedIDef)) {
                        lcolDeepImpliedInterfaces.addAll(CIMContext.Instance.ProcessCache().getImpliedByIDef(impliedIDef));
                        this.processedInterfaceDefs.add(impliedIDef);
                    }
                }
                if (lcolDeepImpliedInterfaces.size() > 0) {
                    lcolDeepImpliedInterfaces = lcolDeepImpliedInterfaces.stream().distinct().collect(Collectors.toList());
                    result = this.isImplied(pstrInterfaceDefinitionUID, lcolDeepImpliedInterfaces);
                }
            }
        }
        return result;
    }

    protected IObjectCollection OnGetAllIDefsForObject(IInterfaceCollection interfaces) {
        if (interfaces != null) {
            List<String> lcolIDefs = new ArrayList<>();
            Iterator<Map.Entry<String, IInterface>> entryIterator = interfaces.GetEnumerator();
            while (entryIterator.hasNext()) {
                Map.Entry<String, IInterface> next = entryIterator.next();
                IInterface lobjIDef = next.getValue();
                lcolIDefs.add(lobjIDef.InterfaceDefinitionUID());
            }
            return CIMContext.Instance.ProcessCache().getObjectByUID(lcolIDefs);
        }
        return null;
    }

    @Override
    public abstract void UniqueKeyValidation(cancelArgs cancelArgs) throws Exception;

    @Override
    public IClassDef GetClassDefinition() throws Exception {
        return this.ClassBase().ClassDefinition();
    }

    @Override
    public abstract boolean IsUniqueChecksOnOBIDAndUpdateState(ArrayList<String> parrOBIDs) throws Exception;

    @Override
    public void clearAllProperties() {
        Iterator<Map.Entry<String, IInterface>> entryIterator = this.Interfaces().GetEnumerator();
        while (entryIterator.hasNext()) {
            IInterface value = entryIterator.next().getValue();
            value.Properties().clear();
        }
    }

    @Override
    public void resetWithProvidedIObjectAsNewCache(IObject iObject) {
        if (iObject != null) {
            try {
                ReentrantLockUtility.tryToAcquireWriteLock(this.Lock());
                this.clearAllProperties();
                this.setIObjectProperty(iObject);
                this.syncInterfaceAndProperty(iObject);
            } catch (Exception exception) {
                log.error("reset with provided IObject as new Cache failed", exception);
            } finally {
                ReentrantLockUtility.tryToUnlockWriteLock(this.Lock());
            }

        }
    }

    protected void setIObjectProperty(IObject iObject) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.Name.toString(), iObject.Name());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.OBID.toString(), iObject.OBID());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.Description.toString(), iObject.Description());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.UID.toString(), iObject.UID());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.ClassDefinitionUID.toString(), iObject.ClassDefinitionUID());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.Config.toString(), iObject.Config());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.DomainUID.toString(), iObject.DomainUID());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.CreationUser.toString(), iObject.CreationUser());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.CreationDate.toString(), iObject.CreationDate());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.LastUpdateUser.toString(), iObject.LastUpdateUser());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.LastUpdateDate.toString(), iObject.LastUpdateDate());
        this.setPropertyValue(interfaceDefinitionType.IObject.toString(), propertyDefinitionType.UniqueKey.toString(), iObject.UniqueKey());
    }

    protected void syncInterfaceAndProperty(IObject iObject) throws Exception {
        if (iObject != null) {
            Iterator<Map.Entry<String, IInterface>> iterator = iObject.Interfaces().GetEnumerator();
            while (iterator.hasNext()) {
                IInterface anInterface = iterator.next().getValue();
                if (!anInterface.InterfaceDefinitionUID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString())) {
                    IInterface current = null;
                    if (!this.hasInterface(anInterface.InterfaceDefinitionUID())) {
                        current = this.Interfaces().item(anInterface.InterfaceDefinitionUID());
                    } else
                        current = this.Interfaces().get(anInterface.InterfaceDefinitionUID());
                    if (current != null) {
                        Iterator<Map.Entry<String, IProperty>> entryIterator = anInterface.Properties().GetEnumerator();
                        while ((entryIterator.hasNext())) {
                            IProperty property = entryIterator.next().getValue();
                            current.Properties().item(property.getPropertyDefinitionUid()).setValue(property.Value());
                        }
                    }
                }
            }
        }
    }

    @Override
    public ObjectDTO toObjectDTO() throws Exception {
        ObjectDTO objectDTO = new ObjectDTO();
        Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            IInterface anInterface = e.next().getValue();
            Iterator<Map.Entry<String, IProperty>> p = anInterface.Properties().GetEnumerator();
            while (p.hasNext()) {
                IProperty iProperty = p.next().getValue();
                ObjectItemDTO objectItemDTO = new ObjectItemDTO();
                objectItemDTO.setDefUID(iProperty.getPropertyDefinitionUid());
                objectItemDTO.setDisplayValue(iProperty.toDisplayValue());
                IPropertyDef propertyDef = iProperty.getPropertyDefinition();
                if (propertyDef != null) {
                    propertyValueType propertyValueType = propertyDef.checkPropertyValueType();
                    if (propertyValueType == null)
                        propertyValueType = ccm.server.enums.propertyValueType.StringType;
                    objectItemDTO.setPropertyValueType(propertyValueType.toString());
                    objectItemDTO.setLabel(iProperty.getPropertyDefinition().DisplayName());
                } else {
                    DynamicalDefinitionObj dynamicalDefObj = CIMContext.Instance.ProcessCache().getDynamicalDefinitionObj(iProperty.getPropertyDefinitionUid());
                    if (dynamicalDefObj != null) {
                        objectItemDTO.setPropertyValueType(dynamicalDefObj.getPropertyValueType().toString());
                        objectItemDTO.setLabel(dynamicalDefObj.getDisplayAs());
                    } else {
                        objectItemDTO.setPropertyValueType(ccm.server.enums.propertyValueType.StringType.toString());
                        objectItemDTO.setLabel(iProperty.getPropertyDefinitionUid());
                    }
                }
                if (StringUtils.isEmpty(objectItemDTO.getLabel()))
                    objectItemDTO.setLabel(iProperty.getPropertyDefinitionUid());
                objectItemDTO.setObid(iProperty.getObid());
                objectItemDTO.setObjObid(this.OBID());
                if (iProperty.isEnumList()) {
                    objectItemDTO.setOptions(iProperty.getPropertyDefinition().generateOptions());
                }
                objectDTO.add(objectItemDTO);
            }
        }
        return objectDTO;
    }

    @Override
    public String toXml() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<").append(this.ClassDefinitionUID()).append(">");
        stringBuilder.append("\r");
        stringBuilder.append("\t");
        Iterator<Map.Entry<String, IInterface>> interfaceIterator = this.Interfaces().GetEnumerator();
        int count = 1;
        while (interfaceIterator.hasNext()) {
            Map.Entry<String, IInterface> next = interfaceIterator.next();
            IInterface lobjInterface = next.getValue();
            String lstrInterfaceDefUID = next.getKey();
            stringBuilder.append("<").append(lstrInterfaceDefUID).append("  ");
            Iterator<Map.Entry<String, IProperty>> propIterator = lobjInterface.Properties().GetEnumerator();
            while (propIterator.hasNext()) {
                Map.Entry<String, IProperty> propNext = propIterator.next();
                String lstrPropDefUID = propNext.getKey();
                //if (propertyDefinitionType.PropertiesNotToExport().contains(lstrPropDefUID)) continue;
                IProperty lobjProp = propNext.getValue();
                stringBuilder.append(lstrPropDefUID).append("=").append("\"").append(lobjProp.toDisplayValue()).append("\"").append("  ");
            }
            stringBuilder.append("/>").append("\r");
            if (count != this.Interfaces().size()) {
                stringBuilder.append("\t");
            }
            count++;
        }
        stringBuilder.append("</").append(this.ClassDefinitionUID()).append(">");
        return stringBuilder.toString();
    }

    @Override
    public ObjectXmlDTO toObjectXmlDTO() throws Exception {
        ObjectXmlDTO xmlDTO = new ObjectXmlDTO();
        xmlDTO.setChildren(new ArrayList<>());
        xmlDTO.setClassDefinitionUID(this.ClassDefinitionUID());
        xmlDTO.setDomainUID(this.DomainUID());
        if (classDefinitionType.Rel.toString().equals(this.ClassDefinitionUID())) {
            IRel iRel = this.toInterface(IRel.class);
            xmlDTO.setName(iRel.RelDefUID());
        } else {
            xmlDTO.setName(this.Name());
        }
        xmlDTO.setUid(this.UID());
        xmlDTO.setObid(this.OBID());
        xmlDTO.setXmlInfo(this.toXml());
        return xmlDTO;
    }

    @Override
    public void refreshObjectDTO(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null) {
            for (ObjectItemDTO item : objectDTO.getItems()) {
                String value = this.getValue(item.getDefUID());
                item.setDisplayValue(item);
            }
        }
    }

    @Override
    public boolean checkObjectSameAsOtherObj(@NotNull IObject pobjOtherObj) {
        Iterator<Map.Entry<String, IInterface>> e = this.Interfaces().GetEnumerator();
        while (e.hasNext()) {
            Map.Entry<String, IInterface> next = e.next();
            IInterface lobjInterface = next.getValue();
            String lstrInterfaceDef = next.getKey();
            //跳过IObject接口的比对,
            if (lstrInterfaceDef.equalsIgnoreCase(interfaceDefinitionType.IObject.toString())) continue;
            //跳过版本管理接口对象
            if (lstrInterfaceDef.equalsIgnoreCase(interfaceDefinitionType.ICIMRevisionItem.toString())) continue;
            //判断接口是否一致,不一致直接返回false
            IInterface lobjCompareInterface = pobjOtherObj.Interfaces().get(lstrInterfaceDef);
            if (lobjCompareInterface != null) {
                Iterator<Map.Entry<String, IProperty>> entryIterator = lobjInterface.Properties().GetEnumerator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, IProperty> propNext = entryIterator.next();
                    IProperty lobjProp = propNext.getValue();
                    String lstrPropDefUID = propNext.getKey();
                    //比对属性是否一致
                    IProperty lobjCompareProp = lobjCompareInterface.Properties().get(lstrPropDefUID);
                    if (lobjCompareProp != null) {
                        Object value = lobjCompareProp.Value();
                        Object value1 = lobjProp.Value();
                        if (value != null && value1 == null) return false;
                        if (value == null && value1 != null) return false;
                        if (value != null) {
                            if (!value.equals(value1)) {
                                return false;
                            }
                        }
                    } else {
                        //相同属性不存在,直接返回FALSE
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
