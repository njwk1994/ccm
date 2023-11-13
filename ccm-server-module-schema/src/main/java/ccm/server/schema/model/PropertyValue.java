package ccm.server.schema.model;

import ccm.server.context.CIMContext;
import ccm.server.entity.MetaDataObjProperty;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.propertyValueUpdateState;
import ccm.server.convert.impl.ValueConvertService;
import ccm.server.model.ValueAndUoMPart;
import ccm.server.util.NumberFormatUtility;
import ccm.server.schema.interfaces.IEnumEnum;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IUoMEnum;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

@Slf4j
public class PropertyValue implements IPropertyValue, Comparable<IPropertyValue> {
    private IProperty mobjParent;
    private String mstrOBID;
    private String mstrValue;
    private Double mdblFloatValue;
    private String mstrUoM;
    private propertyValueUpdateState updateState;
    private boolean mblnCreatedAndTerminated = false;
    private boolean mblnIsTemporaryValue = false;
    private String mstrTerminationDate = null;
    private String mstrCreationDate = null;

    protected PropertyValue(IProperty propertyParent, Object pstrValue, Double pdblValue, String pstrUoM, String pstrOBID, String pstrTerminationDate) {
        this(propertyParent, pstrValue, pdblValue, pstrUoM, pstrOBID, pstrTerminationDate, propertyValueUpdateState.none);
    }

    public PropertyValue(IProperty property, Object pstrValue, Double pdblValue, String pstrUoM, String pstrOBID, String pstrTerminateDate, propertyValueUpdateState propertyValueUpdateState) {
        this.updateState = propertyValueUpdateState;
        this.mobjParent = property;
        this.mdblFloatValue = pdblValue;
        this.mstrUoM = pstrUoM;
        this.mstrOBID = pstrOBID;
        if (pstrTerminateDate == null || StringUtils.isEmpty(pstrTerminateDate))
            pstrTerminateDate = null;
        this.mstrTerminationDate = pstrTerminateDate;
        this.mblnIsTemporaryValue = true;
        this.setValue(pstrValue);
    }

    @Override
    public int compareTo(@NotNull IPropertyValue o) {
        int lintResult = 0;
        String currentCreationDate = this.mstrCreationDate;
        if (currentCreationDate == null)
            currentCreationDate = "";
        String otherCreationDate = o.CreationDate();
        if (otherCreationDate == null)
            otherCreationDate = "";
        lintResult = currentCreationDate.compareTo(otherCreationDate);
        if (lintResult == 0) {
            String currentTerminationDate = this.mstrTerminationDate;
            if (currentTerminationDate == null)
                currentTerminationDate = "";
            String otherTerminationDate = o.TerminationDate();
            if (otherTerminationDate == null)
                otherTerminationDate = "";
            lintResult = currentTerminationDate.compareTo(otherTerminationDate);
        }
        return lintResult;
    }

    @Override
    public propertyValueUpdateState UpdateState() {
        return this.updateState;
    }

    @Override
    public boolean isTerminated() {
        return this.mstrTerminationDate != null && !StringUtils.isEmpty(this.mstrTerminationDate);
    }

    @Override
    public IProperty getParent() {
        return this.mobjParent;
    }

    @Override
    public String OBID() {
        return this.mstrOBID;
    }

    @Override
    public void setOBID(String obid) {
        this.mstrOBID = obid;
    }

    @Override
    public Object Value() {
        if (this.mobjParent.Dynamical())
            return this.mstrValue;
        if (!StringUtils.isEmpty(this.mstrValue)) {
            String propertyDefinitionUid = this.mobjParent.getPropertyDefinitionUid();
            propertyValueType propertyValueType = propertyDefinitionType.getPropertyValueType(propertyDefinitionUid);
            if (propertyValueType == null) {
                IObject scopedByForPropertyDefinition = CIMContext.Instance.ProcessCache().getScopedByForPropertyDefinition(propertyDefinitionUid);
                if (scopedByForPropertyDefinition != null) {
                    if (scopedByForPropertyDefinition.ClassDefinitionUID().equalsIgnoreCase(classDefinitionType.PropertyType.toString()))
                        propertyValueType = ccm.server.enums.propertyValueType.valueOf(scopedByForPropertyDefinition.Name());
                    else
                        propertyValueType = ccm.server.enums.propertyValueType.valueOf(scopedByForPropertyDefinition.ClassDefinitionUID());
                }
            }
            if (propertyValueType != null) {
                switch (propertyValueType) {
                    case StringType:
                    case EnumListType:
                    case EnumListLevelType:
                        return this.mstrValue;
                    case BooleanType:
                        return ValueConversionUtility.toBoolean(this.mstrValue);
                    case IntegerType:
                        return ValueConversionUtility.toInteger(this.mstrValue);
                    case DoubleType:
                        return ValueConversionUtility.toDouble(this.mstrValue);
                    case YMDType:
                        return ValueConvertService.Instance.YMD(this.mstrValue);
                    case DateTimeType:
                        return ValueConvertService.Instance.Date(this.mstrValue);
                    case UoMListType:
                        return this.mstrValue + "~" + this.mstrUoM;
                }
            }
        }
        return null;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            this.mstrValue = null;
            this.mdblFloatValue = null;
            this.mstrUoM = null;
        } else if (StringUtils.isEmpty(this.mobjParent.getPropertyDefinitionUid())) {
            this.mstrValue = value.toString();
            this.mdblFloatValue = null;
        } else {
            String scopedBy = CIMContext.Instance.ProcessCache().getPropertyValueTypeClassDefForPropertyDefinition(this.mobjParent.getPropertyDefinitionUid());
            boolean flag = true;
            try {
                if (StringUtils.isEmpty(scopedBy))
                    scopedBy = propertyDefinitionType.StringType.toString();
                propertyDefinitionType propertyDefinitionType = ccm.server.enums.propertyDefinitionType.ValueOf(scopedBy);
                switch (Objects.requireNonNull(propertyDefinitionType)) {
                    case EnumListLevelType:
                        this.setEnumListLevelType(value.toString());
                        break;
                    case EnumListType:
                        this.setEnumListType(value.toString());
                        break;
                    case StringType:
                    case BooleanType:
                    case IntegerType:
                        this.mstrValue = value.toString();
                        this.mdblFloatValue = null;
                        this.mstrUoM = null;
                        break;
                    case DoubleType:
                        this.mstrValue = value.toString();
                        this.mdblFloatValue = ValueConversionUtility.toDouble(value.toString());
                        this.mstrUoM = null;
                        break;
                    case YMDType:
                        Date ymd = null;
                        // 2022.02.27 HT 处理日期转换问题 当value不为日期格式时才进行转换
                        if (!(value instanceof Date)) {
                            ymd = ValueConvertService.Instance.Date(value.toString());
                        } else {
                            ymd = (Date) value;
                        }
                        // 2022.02.27 HT 处理日期转换问题 当value不为日期格式时才进行转换
                        if (ymd != null)
                            // 2022.02.27 HT 修改DateTimeType日期输出String格式统一为 yyyy-MM-dd HH:mm:ss
                            this.mstrValue = DateUtils.date_sdf.get().format(ymd);
                            // 2022.02.27 HT 修改DateTimeType日期输出String格式统一为 yyyy-MM-dd HH:mm:ss
                        else
                            this.mstrValue = null;
                        this.mdblFloatValue = null;
                        this.mstrUoM = null;
                        break;
                    case DateTimeType:
                        Date dateTime = null;
                        // 2022.02.27 HT 处理日期转换问题 当value不为日期格式时才进行转换
                        if (!(value instanceof Date)) {
                            dateTime = ValueConvertService.Instance.Date(value.toString());
                        } else {
                            dateTime = (Date) value;
                        }
                        // 2022.02.27 HT 处理日期转换问题 当value不为日期格式时才进行转换
                        if (dateTime != null)
                            // 2022.02.27 HT 修改DateTimeType日期输出String格式统一为 yyyy-MM-dd HH:mm:ss
                            this.mstrValue = DateUtils.datetimeFormat.get().format(dateTime);
                            // 2022.02.27 HT 修改DateTimeType日期输出String格式统一为 yyyy-MM-dd HH:mm:ss
                        else
                            this.mstrValue = null;
                        this.mdblFloatValue = null;
                        this.mstrUoM = null;
                        break;
                    case UoMListType:
                        this.setUoMListType(value.toString());
                        break;
                }
            } catch (Exception exception) {
                flag = false;
                log.warn(exception.getMessage(), exception);
            }
            if (!flag) {
                this.mstrValue = value.toString();
                this.mstrUoM = null;
                this.mdblFloatValue = null;
            }
        }
    }

    private void setUoMListType(String toString) throws Exception {
        String lstrUnParsedNumericValue = null;
        String lstrUoM = null;
        String lstrParsedValue = null;
        Double ldblFloatValue = null;
        ValueAndUoMPart valueAndUoMPart = new ValueAndUoMPart();
        boolean lblnSplitSuccessfully = NumberFormatUtility.splitUoM(toString, valueAndUoMPart);
        lstrParsedValue = valueAndUoMPart.getValuePart();
        lstrUnParsedNumericValue = valueAndUoMPart.getUnParsedPart();
        lstrUoM = valueAndUoMPart.getUomPart();
        if (lblnSplitSuccessfully && StringUtils.isEmpty(lstrUoM) && !StringUtils.isEmpty(this.mstrUoM))
            lstrUoM = this.mstrUoM;
        IUoMEnum lobjUomEnum = null;
        if (!StringUtils.isEmpty(lstrUoM)) {
            lobjUomEnum = this.mobjParent.getUoMEntry(lstrUoM);
            if (lobjUomEnum == null)
                throw new Exception("invalid uom set with name:" + lstrUoM);
            lstrUoM = lobjUomEnum.UID();
        }
        if (lblnSplitSuccessfully && lobjUomEnum != null) {
            lstrUoM = lobjUomEnum.UID();
            Double ldblSplitFloatValue = Double.parseDouble(lstrParsedValue);
            ldblFloatValue = this.getFloatValueWithUoMSet(ldblSplitFloatValue, lobjUomEnum);
        }
        if (!lblnSplitSuccessfully && StringUtils.isEmpty(lstrUoM)) {
            String lstrValue = toString.trim();
            IUoMEnum uoMEntry = this.mobjParent.getUoMEntry(lstrValue);
            if (uoMEntry != null) {
                lstrUoM = lobjUomEnum.UID();
                lstrUnParsedNumericValue = "";
            } else
                lstrUnParsedNumericValue = lstrValue;
        }
        if (lstrUnParsedNumericValue != null) {
            lstrUnParsedNumericValue = lstrUnParsedNumericValue.trim();
        }
        this.mstrValue = lstrUnParsedNumericValue;
        this.mstrUoM = lstrUoM;
        this.mdblFloatValue = ldblFloatValue;
    }

    public Double getFloatValueWithUoMSet(Double pdblSplitFloatValue, IUoMEnum uoMEnum) {
        return pdblSplitFloatValue;
    }

    private void setEnumListType(String toString) throws Exception {
        IEnumEnum enumListEntry = this.mobjParent.getEnumListEntry(toString);
        if (enumListEntry != null) {
            this.mstrValue = enumListEntry.UID();
        } else {
            this.mstrValue = toString;
        }
        this.mdblFloatValue = null;
        this.mstrUoM = null;
    }

    private void setEnumListLevelType(String toString) throws Exception {
        IEnumEnum enumListLevelType = this.mobjParent.getEnumListLevelType(toString);
        if (enumListLevelType != null) {
            this.mstrValue = enumListLevelType.UID();
        } else {
            this.mstrValue = toString;
        }
        this.mdblFloatValue = null;
        this.mstrUoM = null;
    }

    @Override
    public double FloatValue() {
        return this.mdblFloatValue;
    }

    @Override
    public void setFloatValue(double floatValue) {
        this.mdblFloatValue = floatValue;
    }

    @Override
    public String UoM() {
        return this.mstrUoM;
    }

    @Override
    public void setUoM(String uoM) {
        this.mstrUoM = uoM;
    }

    public void setUpdateState(propertyValueUpdateState propertyValueUpdateState) {
        if (propertyValueUpdateState == ccm.server.enums.propertyValueUpdateState.terminated && this.updateState == ccm.server.enums.propertyValueUpdateState.created)
            this.mblnCreatedAndTerminated = true;
        this.updateState = propertyValueUpdateState;
    }

    @Override
    public boolean isTemporaryValue() {
        return this.mblnIsTemporaryValue;
    }

    @Override
    public void setIsTemporaryValue(boolean isTemporaryValue) {
        this.mblnIsTemporaryValue = isTemporaryValue;
    }

    @Override
    public boolean isValidValue() {
        //add logic to judge provided value whether correct or not with property definition's scoped by
        return true;
    }

    @Override
    public String toString() {
        return this.mstrValue;
    }

    @Override
    public boolean CreatedAndTerminated() {
        return this.mblnCreatedAndTerminated;
    }

    @Override
    public String TerminationDate() {
        return this.mstrTerminationDate;
    }

    @Override
    public void setTerminationDate(String pstrTerminationDate) {
        this.mstrTerminationDate = pstrTerminationDate;
    }

    @Override
    public IEnumEnum getEnumListEntry() throws Exception {
        return this.mobjParent.getEnumListEntry(this.mstrValue);
    }

    @Override
    public IEnumEnum getEnumListLevelType() throws Exception {
        return this.mobjParent.getEnumListLevelType(this.mstrValue);
    }

    @Override
    public IUoMEnum getUoMEntry() throws Exception {
        return this.mobjParent.getUoMEntry(this.mstrUoM);
    }

    @Override
    public String CreationDate() {
        return this.mstrCreationDate;
    }

    @Override
    public void setCreationDate(String pstrCreationDate) {
        this.mstrCreationDate = pstrCreationDate;
    }

    @Override
    public MetaDataObjProperty toDataProperty() {
        MetaDataObjProperty metaDataObjProperty = new MetaDataObjProperty();
        metaDataObjProperty.setStrValue(this.mstrValue);
        if (this.mdblFloatValue != null)
            metaDataObjProperty.setStrValue(this.mdblFloatValue.toString());
        metaDataObjProperty.setUom(this.mstrUoM);
        metaDataObjProperty.setCreationDate(ValueConvertService.Instance.Date(this.mstrCreationDate));
        metaDataObjProperty.setPropertyDefUid(this.mobjParent.getPropertyDefinitionUid());
        metaDataObjProperty.setObid(this.mstrOBID);
        metaDataObjProperty.setObjObid(this.mobjParent.getParent().OBID());
        metaDataObjProperty.setInterfaceObid(this.mobjParent.getParent().InterfaceOBID());
        metaDataObjProperty.setInterfaceDefUid(this.mobjParent.getParent().InterfaceDefinitionUID());
        metaDataObjProperty.setTerminationDate(ValueConvertService.Instance.Date(this.mstrTerminationDate));
        return metaDataObjProperty;
    }

    @Override
    public boolean terminatedOrDeleted() {
        return this.updateState != null && (this.updateState == propertyValueUpdateState.deleted || this.updateState == propertyValueUpdateState.terminated);
    }
}
