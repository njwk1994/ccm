package ccm.server.enums;

import ccm.server.util.CommonUtility;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public enum propertyDefinitionType {
    Name,
    Description,
    OBID(true),
    OBID1,
    OBID2,
    ByCustom(ccm.server.enums.propertyValueType.BooleanType),
    UID("obj_uid", true),
    ClassDefinitionUID("class_definition_uid"),
    ClassDefinitionUID1("class_definition_uid1"),
    ClassDefinitionUID2("class_definition_uid2"),
    InterfaceDefinitionUID("interface_def_uid"),
    PropertyDefinitionUID("property_def_uid"),
    InterfaceOBID("interface_obid"),
    OBJOBID("obj_obid"),
    DomainUID("domain_uid", true),
    UniqueKey("unique_key", true),
    CreationUser("creation_user", true),
    CreationDate("creation_date", true),
    LastUpdateUser("last_update_user", true),
    LastUpdateDate("last_update_date", true, ccm.server.enums.propertyValueType.DateTimeType),
    TerminationUser("termination_user", true),
    TerminationDate("termination_date", true, ccm.server.enums.propertyValueType.DateTimeType),
    STRVALUE("str_value"),
    UID1,
    UID2,
    Name1,
    Name2,
    ContainerID(true),
    RelDefUID("rel_def_uid"),
    Prefix,
    DomainUID1("domain_uid1"),
    DomainUID2("domain_uid2"),
    Delete12(ccm.server.enums.propertyValueType.BooleanType),
    Delete21(ccm.server.enums.propertyValueType.BooleanType),
    DefaultValue,
    Role1,
    Role2,
    Role1DisplayName,
    Role2DisplayName,
    Config,
    Copy21,
    End1Locality,
    End2Locality,
    IsAbstract(ccm.server.enums.propertyValueType.BooleanType),
    LinkInterfaces,
    Min1(ccm.server.enums.propertyValueType.IntegerType),
    Min2(ccm.server.enums.propertyValueType.IntegerType),
    Max1,
    Max2,
    SpecRelDefUID,
    DisplayAs,
    DisplayName,
    SchemaRev,
    SchemaVer,
    SchemaRevVer,
    IsRequired("is_required", ccm.server.enums.propertyValueType.BooleanType),
    TablePrefix,
    OrderValue("order_value", ccm.server.enums.propertyValueType.IntegerType),
    UniqueKeyPattern,
    SystemIDPattern,
    ControlledByConfig(ccm.server.enums.propertyValueType.BooleanType),
    RelDefControlledByConfig(ccm.server.enums.propertyValueType.BooleanType),
    InterfaceSequence(ccm.server.enums.propertyValueType.IntegerType),
    CIMSpoolUIDs,
    CIMPipeUIDs,
    CIMPipeComponentUIDs,
    CIMWeldUIDs,
    CIMSupportUIDs,
    CIMRevisionItemMinorRevision,
    CIMRevisionItemMajorRevision,
    CIMRevisionItemRevState,
    CIMRevisionItemDetailRevState,
    CIMRevisionItemOperationState,
    CIMDocTitle,
    CIMDocSubType,
    CIMDocType,
    CIMDocState,
    CIMDocCategory,
    CIMSignOffComments,
    CIMRevisionSchema,
    CIMRevIssueDate,
    CIMRevState,
    CIMMinorRevision,
    CIMMajorRevision,
    CIMExternalRevision,
    CIMVersionSupersededDate,
    CIMIsDocVersionSuperseded,
    CIMIsDocVersionCheckedOut,
    CIMDocVersion,
    CIMVersionCheckInUser,
    CIMVersionCheckInDate,
    CIMMajorSequence,
    CIMMajorSequenceMinLength,
    CIMMajorSequencePadChar,
    CIMMajorSequenceType,
    CIMMinorSequence,
    CIMMinorSequenceMinLength,
    CIMMinorSequencePadChar,
    CIMMinorSequenceType,
    CIMMinioUsername,
    CIMMinioPassword,
    CWA,
    CIMMinioIP,

    CIMMinioInterIP,
    CIMMinioPort,
    CIMMinioSSL,
    CIMMinioBucket,
    CIMFileExtension,
    CIMFileViewable,
    CIMFileEditable,
    FileName,
    FileExt,
    BucketName,
    BucketObjName,
    WSConsumeMaterial,
    WSComponentName,
    WSWeight,
    WSStatus,
    WSROPRule,
    WSComponentDesc,
    ROPGroupOrder,
    ROPGroupClassDefinitionUID,
    ROPRuleGroupName,
    ROPTargetPropertyDefinitionUID,
    ROPCalculationValue,
    ROPTargetPropertyValueUoM,
    ROPOverLoadInd,
    ROPWorkStepTPPhase,
    ROPWorkStepWPPhase,
    ROPWorkStepName,
    ROPWorkStepType,
    ROPHasHandleChange,
    ROPWorkStepAllowInd,
    WSTPProcessPhase,
    WSWPProcessPhase,
    ROPWorkStepGenerateMode,
    ROPGroupItemsHasUpdated,
    ROPGroupWorkStepHasUpdated,
    ROPWorkStepMaterialIssue,
    ROPWorkStepConsumeMaterialInd,
    ROPWorkStepWeightCalculateProperty,
    ROPWorkStepBaseWeight,
    ROPGroupWorkStepRevState,
    ROPWorkStepOrderValue,
    ROPGroupItemRevState,
    CCMPTPMSCalculateProp,
    CCMPTPMSCalculatePropValue,
    CCMPTPMSCategory,
    CCMPTPMaterialLength,
    CCMPTPMDesignToolsClassType,
    CCMPTPMPSize2,
    CCMPTPMBelongsMS,
    CCMPTPMPSize1,
    CCMPTPMMaterialCode,
    CCMPTPMDescription,
    CCMPipeLineUIDs,
    CCMBoltUIDs,
    CCMGasketUIDs,
    CCMEquipUIDs,
    CCMSubEquipUIDs,
    CCMCableTrayUIDs,
    CCMCableTrayComponentUIDs,
    CCMCableUIDs,
    CCMInstrumentUIDs,
    CCMDuctLineUIDs,
    CCMDuctComponentUIDs,
    CCMJunctionBoxUIDs,
    CCMSTPartUIDs,
    CCMSTComponentUIDs,
    CCMSTBlockUIDs,

    MaterialsClass,
    Size1,
    EnumNumber(ccm.server.enums.propertyValueType.IntegerType),
    StringType,
    BooleanType(ccm.server.enums.propertyValueType.BooleanType),
    DoubleType(ccm.server.enums.propertyValueType.DoubleType),
    IntegerType(ccm.server.enums.propertyValueType.IntegerType),
    YMDType(ccm.server.enums.propertyValueType.YMDType),
    DateTimeType(ccm.server.enums.propertyValueType.DateTimeType),
    EnumListType(ccm.server.enums.propertyValueType.EnumListType),
    EnumListLevelType(ccm.server.enums.propertyValueType.EnumListLevelType),
    UoMListType(ccm.server.enums.propertyValueType.UoMListType);

    public static List<String> PropertiesCannotBeUpdated() {
        List<String> result = new ArrayList<>();
        result.add(OBID.toString());
        result.add(OBID1.toString());
        result.add(OBID2.toString());
        result.add(UID.toString());
        result.add(UID1.toString());
        result.add(UID2.toString());
        result.add(DomainUID.toString());
        result.add(RelDefUID.toString());
        result.add(Config.toString());
        result.add(ClassDefinitionUID.toString());
        result.add(CreationDate.toString());
        result.add(CreationUser.toString());
        result.add(LastUpdateUser.toString());
        result.add(LastUpdateDate.toString());
        result.add(TerminationDate.toString());
        result.add(TerminationUser.toString());
        return result;
    }

    public static List<String> PropertiesNotShowToEndUser() {
        List<String> result = new ArrayList<>();
        result.add(OBID.toString());
        result.add(OBID1.toString());
        result.add(OBID2.toString());
        result.add(UID.toString());
        result.add(UID1.toString());
        result.add(UID2.toString());
        result.add(Prefix.toString());
        result.add(ContainerID.toString());
        result.add(ClassDefinitionUID.toString());
        result.add(ClassDefinitionUID1.toString());
        result.add(ClassDefinitionUID2.toString());
        result.add(DomainUID.toString());
        result.add(DomainUID2.toString());
        result.add(DomainUID1.toString());
        result.add(CreationUser.toString());
        result.add(CreationDate.toString());
        result.add(LastUpdateDate.toString());
        result.add(LastUpdateUser.toString());
        result.add(TerminationUser.toString());
        result.add(TerminationDate.toString());
        return result;
    }

    public static List<String> PropertiesNotToExport() {
        List<String> lcolResult = new ArrayList<>();
        lcolResult.add(CreationUser.toString());
        lcolResult.add(OBID.toString());
        lcolResult.add(CreationDate.toString());
        lcolResult.add(LastUpdateDate.toString());
        lcolResult.add(LastUpdateUser.toString());
        lcolResult.add(TerminationUser.toString());
        lcolResult.add(TerminationDate.toString());
        lcolResult.add(OrderValue.toString());
        lcolResult.add(Name1.toString());
        lcolResult.add(Name2.toString());
        lcolResult.add(OBID1.toString());
        lcolResult.add(OBID2.toString());
        lcolResult.add(DomainUID1.toString());
        lcolResult.add(DomainUID2.toString());
        lcolResult.add(Prefix.toString());
        lcolResult.add(ClassDefinitionUID1.toString());
        lcolResult.add(ClassDefinitionUID2.toString());
        return lcolResult;
    }

    public static List<String> OBJORRELTableProperties() {
        List<String> result = new ArrayList<>();
        result.add(Name.toString());
        result.add(Name1.toString());
        result.add(Name2.toString());
        result.add(Description.toString());
        result.add(OBID.toString());
        result.add(OBID1.toString());
        result.add(OBID2.toString());
        result.add(UID.toString());
        result.add(UID1.toString());
        result.add(UID2.toString());
        result.add(DomainUID.toString());
        result.add(DomainUID1.toString());
        result.add(DomainUID2.toString());
        result.add(IsRequired.toString());
        result.add(OrderValue.toString());
        result.add(RelDefUID.toString());
        result.add(Prefix.toString());
        result.add(Config.toString());
        result.add(UniqueKey.toString());
        result.add(ClassDefinitionUID.toString());
        result.add(ClassDefinitionUID2.toString());
        result.add(ClassDefinitionUID1.toString());
        result.add(CreationUser.toString());
        result.add(CreationDate.toString());
        result.add(LastUpdateDate.toString());
        result.add(LastUpdateUser.toString());
        result.add(TerminationUser.toString());
        result.add(TerminationDate.toString());
        return result;
    }

    public static List<String> RELTableProperties() {
        List<String> result = new ArrayList<>();
        result.add(Name.toString());
        result.add(Name1.toString());
        result.add(Name2.toString());
        result.add(OBID.toString());
        result.add(OBID1.toString());
        result.add(OBID2.toString());
        result.add(UID.toString());
        result.add(UID1.toString());
        result.add(UID2.toString());
        result.add(DomainUID.toString());
        result.add(DomainUID1.toString());
        result.add(DomainUID2.toString());
        result.add(IsRequired.toString());
        result.add(OrderValue.toString());
        result.add(RelDefUID.toString());
        result.add(Prefix.toString());
        result.add(Config.toString());
        result.add(ClassDefinitionUID2.toString());
        result.add(ClassDefinitionUID1.toString());
        result.add(CreationUser.toString());
        result.add(CreationDate.toString());
        result.add(TerminationUser.toString());
        result.add(TerminationDate.toString());
        return result;
    }

    public static List<String> OBJTableProperties() {
        List<String> result = new ArrayList<>();
        result.add(OBID.toString());
        result.add(UID.toString());
        result.add(DomainUID.toString());
        result.add(Name.toString());
        result.add(Description.toString());
        result.add(Config.toString());
        result.add(ClassDefinitionUID.toString());
        result.add(UniqueKey.toString());
        result.add(CreationDate.toString());
        result.add(CreationUser.toString());
        result.add(LastUpdateDate.toString());
        result.add(LastUpdateUser.toString());
        result.add(TerminationDate.toString());
        result.add(TerminationUser.toString());
        return result;
    }

    private String columnName;
    private boolean systemControlled;
    private String realizedBy;
    private propertyValueType propertyValueType = ccm.server.enums.propertyValueType.StringType;

    private void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        if (StringUtils.isEmpty(this.columnName))
            this.columnName = this.toString().toLowerCase();
        return this.columnName;
    }

    private propertyDefinitionType() {
        this.systemControlled = false;
        this.setColumnName(this.toString());
        this.propertyValueType = ccm.server.enums.propertyValueType.StringType;
    }

    private propertyDefinitionType(propertyValueType propertyValueType) {
        this.propertyValueType = propertyValueType;
        this.setColumnName(this.toString());
    }

    private propertyDefinitionType(boolean systemControlled) {
        this.systemControlled = systemControlled;
        this.setColumnName(this.toString());
    }

    private propertyDefinitionType(String columnName) {
        this.setColumnName(columnName);
        this.systemControlled = false;
    }

    private propertyDefinitionType(String columnName, boolean writeBySystem) {
        this.setColumnName(columnName);
        this.systemControlled = writeBySystem;
    }

    public static propertyDefinitionType ValueOf(String value) {
        if (!StringUtils.isEmpty(value)) {
            try {
                return propertyDefinitionType.valueOf(value);
            } catch (IllegalArgumentException e) {
                log.error(e.getCause().getMessage());
            }
        }
        return null;
    }

    private propertyDefinitionType(String columnName, propertyValueType propertyValueType) {
        this.setColumnName(columnName);
        this.propertyValueType = propertyValueType;
    }


    private propertyDefinitionType(String columnName, boolean writeBySystem, propertyValueType propertyValueType) {
        this.setColumnName(columnName);
        this.systemControlled = writeBySystem;
        this.propertyValueType = propertyValueType;
    }

    public static String[] getSystemControlledProperties() {
        List<String> result = new ArrayList<>();
        for (propertyDefinitionType value : propertyDefinitionType.values()) {
            if (value.systemControlled)
                result.add(value.toString());
        }
        return CommonUtility.convertToArray(result);
    }

    public static propertyValueType getPropertyValueType(String propertyDefinitionType) {
        if (!StringUtils.isEmpty(propertyDefinitionType)) {
            try {
                ccm.server.enums.propertyDefinitionType propertyDefinitionType1 = ccm.server.enums.propertyDefinitionType.valueOf(propertyDefinitionType);
                return propertyDefinitionType1.propertyValueType;
            } catch (Exception exception) {
                // log.warn(propertyDefinitionType + " is not exist in hard-code list");
            }
            return null;
        }
        return ccm.server.enums.propertyValueType.StringType;
    }

    public static List<String> relTableBaseProperty() {
        List<String> properties = new ArrayList<>();
        properties.add(propertyDefinitionType.Name.toString());
        properties.add(propertyDefinitionType.OBID.toString());
        properties.add(propertyDefinitionType.UID.toString());
        properties.add(propertyDefinitionType.ClassDefinitionUID.toString());
        return properties;
    }

    public String generateColumn(String table) {
        return table + StringPool.DOT + this.columnName;
    }
}
