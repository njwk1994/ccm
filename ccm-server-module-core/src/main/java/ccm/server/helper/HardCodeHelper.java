package ccm.server.helper;

import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.*;
import ccm.server.util.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class HardCodeHelper {
    private static final long serializableId = 1L;
    public static final String[] OBJ_QUERY_INDICATORS = new String[]{
        propertyDefinitionType.OBID.toString(),
        propertyDefinitionType.Name.toString(),
        propertyDefinitionType.Description.toString(),
        propertyDefinitionType.UID.toString(),
        propertyDefinitionType.DomainUID.toString(),
        propertyDefinitionType.UniqueKey.toString(),
        propertyDefinitionType.ClassDefinitionUID.toString(),
        propertyDefinitionType.CreationDate.toString(),
        propertyDefinitionType.CreationUser.toString(),
        propertyDefinitionType.LastUpdateDate.toString(),
        propertyDefinitionType.LastUpdateUser.toString(),
    };

    public static final String[] REL_QUERY_INDICATORS = new String[]{
        propertyDefinitionType.OBID.toString(),
        propertyDefinitionType.UID.toString(),
        propertyDefinitionType.OBID1.toString(),
        propertyDefinitionType.OBID2.toString(),
        propertyDefinitionType.Name1.toString(),
        propertyDefinitionType.Name2.toString(),
        propertyDefinitionType.UID1.toString(),
        propertyDefinitionType.UID2.toString(),
        propertyDefinitionType.RelDefUID.toString(),
        propertyDefinitionType.Prefix.toString(),
        propertyDefinitionType.IsRequired.toString(),
        propertyDefinitionType.OrderValue.toString(),
        propertyDefinitionType.Config.toString(),
        propertyDefinitionType.CreationDate.toString(),
        propertyDefinitionType.CreationUser.toString(),
    };

    public static final String UNIQUE_EDGE_UID_PREFIX = "EDG_";

    public static final String RELDEF_USER_TO_QUERY_SCOPE = "User2QueryConfig";
    public static final String RELDEF_USER_TO_CREATE_SCOPE = "user2ConfigurationItem";

    public static final String CLASSDEF_REL = "Rel";
    public static final String ALL_RELATIONSHIPS = "ALL";


    public static String[] propertiesControlledBySystem() {
        return propertyDefinitionType.getSystemControlledProperties();
    }

    public static List<Map.Entry<String, classDefinitionType>> interfaceDefinitionProperties() {
        List<Map.Entry<String, classDefinitionType>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.InterfaceSequence.toString(), classDefinitionType.PropertyDef));
        return result;
    }

    public static List<Map.Entry<String, classDefinitionType>> classDefinitionProperties() {
        List<Map.Entry<String, classDefinitionType>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.UniqueKeyPattern.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.ControlledByConfig.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.SystemIDPattern.toString(), classDefinitionType.PropertyDef));
        return result;
    }

    public static List<Map.Entry<String, classDefinitionType>> relDefinitionProperties() {
        List<Map.Entry<String, classDefinitionType>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.End1Locality.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.End2Locality.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Role1DisplayName.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Role2DisplayName.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Role1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Role2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Min1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Max1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Min2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Max2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.UID1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.UID2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Delete12.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Delete21.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.SpecRelDefUID.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.LinkInterfaces.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.IsAbstract.toString(), classDefinitionType.PropertyDef));
        return result;
    }

    public static List<Map.Entry<String, classDefinitionType>> relProperties() {
        List<Map.Entry<String, classDefinitionType>> result = new ArrayList<>();
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.UID1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.UID2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.RelDefUID.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.DomainUID1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.DomainUID2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Name1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.Name2.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.OBID1.toString(), classDefinitionType.PropertyDef));
        result.add(new AbstractMap.SimpleEntry<String, classDefinitionType>(propertyDefinitionType.OBID2.toString(), classDefinitionType.PropertyDef));
        return result;
    }

    public static boolean isPropertyControlledBySystem(String pstrPropertyDef) {
        if (!StringUtils.isEmpty(pstrPropertyDef)) {
            String[] items = propertiesControlledBySystem();
            return Arrays.stream(items).anyMatch(c -> c.equalsIgnoreCase(pstrPropertyDef));
        }
        return false;
    }

    public static ObjectItemDTO Obid() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(true);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的唯一识别码，由系统生成");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.OBID.toString());
        item.setLabel("系统标识唯一码：");
        item.setMandatory(false);
        item.setOrderValue(10);
        return item;
    }

    //OBJECT NAME
    public static ObjectItemDTO Name() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的名称或编码，由用户填写");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.Name.toString());
        item.setLabel("名称/编号：");
        item.setMandatory(true);
        item.setOrderValue(20);
        item.setDisplayValue("");
        return item;
    }

    //OBJECT DESCRIPTION
    public static ObjectItemDTO Description() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的描述信息，由用户填写");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.Description.toString());
        item.setLabel("描述：");
        item.setMandatory(false);
        item.setOrderValue(30);
        return item;
    }

    //OBJECT CLASS DEFINITION UID
    public static ObjectItemDTO ClassDefinitionUID(String classDefinitionType) {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(true);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("用于实例化对象的定义，应被系统被支持");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.ClassDefinitionUID.toString());
        item.setLabel("对象定义：");
        item.setDisplayValue(classDefinitionType);
        item.setMandatory(true);
        item.setOrderValue(40);
        return item;
    }

    public static ObjectItemDTO ClassDefinitionUID() {
        return ClassDefinitionUID("");
    }

    //OBJECT CREATION USER
    public static ObjectItemDTO CreationUser() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的创建人信息");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.CreationUser.toString());
        item.setLabel("创建人：");
        item.setOrderValue(50);
        return item;
    }

    //OBJECT CREATION DATE
    public static ObjectItemDTO CreationDate() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的创建日期信息");
        item.setPropertyValueType(propertyValueType.DateTimeType.toString());
        item.setDefUID(propertyDefinitionType.CreationDate.toString());
        item.setLabel("创建日期：");
        item.setOrderValue(60);
        return item;
    }

    //OBJECT LAST UPDATE USER
    public static ObjectItemDTO LastUpdateUser() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的最后更新人信息");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.LastUpdateUser.toString());
        item.setLabel("最后更新人：");
        item.setOrderValue(7);
        return item;
    }

    //OBJECT LAST UPDATE DATE
    public static ObjectItemDTO LastUpdateDate() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的最后更新日期");
        item.setPropertyValueType(propertyValueType.DateTimeType.toString());
        item.setDefUID(propertyDefinitionType.LastUpdateDate.toString());
        item.setLabel("最后更新日期：");
        item.setOrderValue(8);
        return item;
    }

    //OBJECT TERMINATION USER
    public static ObjectItemDTO TerminationUser() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的终结人信息");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.TerminationUser.toString());
        item.setLabel("终结人：");
        item.setOrderValue(9);
        return item;
    }

    //OBJECT TERMINATION DATE
    public static ObjectItemDTO TerminationDate() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的终结信息");
        item.setPropertyValueType(propertyValueType.DateTimeType.toString());
        item.setDefUID(propertyDefinitionType.TerminationDate.toString());
        item.setLabel("终结日期：");
        item.setOrderValue(10);
        return item;
    }

    public static ObjectItemDTO UniqueID() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的唯一标识码，用于同域对象的重复性比对");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.UID.toString());
        item.setLabel("唯一标识Id：");
        item.setOrderValue(11);
        return item;
    }

    public static ObjectItemDTO UniqueKey() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(true);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("对象的唯一标识码，用于全局对象的重复性比对");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.UniqueKey.toString());
        item.setLabel("唯一键值：");
        item.setOrderValue(12);
        return item;
    }

    public static ObjectItemDTO IsRoot() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("标记是否为根节点，仅可做为父节点存在");
        item.setPropertyValueType(propertyValueType.BooleanType.toString());
        item.setLabel("是否为根节点：");
        item.setMandatory(false);
        item.setOrderValue(100);
        return item;
    }

    //DATA OBJECT PROPERTY's PROPERTY VALUE TYPE
    public static ObjectItemDTO PropertyValueType() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("值的类型，根据属性定义关联的值类型设置");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setLabel("值类型：");
        item.setMandatory(true);
        return item;
    }

    //CLASS DEFINITION EXPOSES PROPERTY DEFINITION OR RELATIONSHIP DEFINITION
    //TO SET ORDER FOR SEQUENTIAL
    public static ObjectItemDTO OrderValue() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("属性项序列号，根据此序号进行排序显示");
        item.setPropertyValueType(propertyValueType.IntegerType.toString());
        item.setLabel("属性项序列：");
        item.setDisplayValue(1);
        item.setMandatory(true);
        item.setOrderValue(100);
        return item;
    }

    //CLASS DEFINITION EXPOSES PROPERTY DEFINITION OR RELATIONSHIP DEFINITION
    //TO SET IS REQUIRED
    public static ObjectItemDTO IsRequired() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("信息是否为必须的");
        item.setPropertyValueType(propertyValueType.BooleanType.toString());
        item.getOptions().addAll(OptionItemDTO.booleanOptions());
        item.setLabel("是否必须：");
        item.setMandatory(true);
        item.setOrderValue(110);
        return item;
    }

    //CLASS DEFINITION EXPOSES PROPERTY DEFINITION OR RELATIONSHIP DEFINITION
    //TO SET DISPLAY AS
    public static ObjectItemDTO DisplayAs() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("显示名称，根据实际情况设定");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setLabel("显示信息：");
        item.setMandatory(false);
        item.setOrderValue(120);
        return item;
    }

    //CLASS DEFINITION EXPOSES PROPERTY DEFINITION OR RELATIONSHIP DEFINITION
    //TO SET REL DIRECTION
    public static ObjectItemDTO RelDirection() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("关联关系的方向，从起源端对象计算");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setLabel("关联关系展开方向：");
        item.setMandatory(false);
        item.setOptions(OptionItemDTO.relDirectionOptions());
        item.setOrderValue(140);
        return item;
    }

    //CLASS DEFINITION EXPOSES PROPERTY DEFINITION OR RELATIONSHIP DEFINITION
    //TO SET GROUP
    public static ObjectItemDTO GroupHeader() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("属性项组别，根据此信息进行属性分组显示");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setLabel("属性项组别：");
        item.setMandatory(false);
        item.setOrderValue(130);
        return item;
    }

    //CLASS DEFINITION'S UNIQUE KEY DEF
    public static ObjectItemDTO UniqueKeyDef() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("类型定义中唯一键值的定义，对象通过该类型定义实例化时通过该规则进行键值生成");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.UniqueKeyPattern.toString());
        item.setLabel("唯一键值的定义：");
        item.setMandatory(false);
        item.setOrderValue(100);
        return item;
    }

    //ENUM LIST LEVEL TYPE'S TARGET LEVEL
    public static ObjectItemDTO TargetLevel() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("目标层级所在层数，基准枚举对象所在层级为-1");
        item.setPropertyValueType(propertyValueType.IntegerType.toString());
        item.setLabel("目标层级所在层数：");
        item.setMandatory(true);
        item.setOrderValue(100);
        return item;
    }

    //ENUM LIST LEVEL TYPE'S BASE ENUM LIST LEVEL ID
    public static ObjectItemDTO BaseEnumListTypeId() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("起源枚举对象Id");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setLabel("源于枚举对象：");
        item.setMandatory(true);
        item.setOrderValue(110);
        return item;
    }

    //CLASS DEFINITION EXPOSES PROPERTY DEFINITION OR RELATIONSHIP DEFINITION
    //TO SET PURPOSE TO INDICATE CREATE/UPDATE/QUERY/INFO
    public static ObjectItemDTO Purpose() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("操作目的，系统将依此构建必要的表单或其他");
        item.setPropertyValueType(propertyValueType.Relationship.toString());
        item.setLabel("操作目的：");
        item.setMandatory(true);
        item.setOrderValue(150);
        item.setOptions(OptionItemDTO.operationPurposeOptions());
        return item;
    }

    //PROPERTY DEFINITION'S SCOPED BY
    //REL BETWEEN PROPERTY DEFINITION AND PROPERTY VALUE TYPE
    public static ObjectItemDTO ScopedBy() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.RELATIONSHIP_GROUP_GENERAL);
        item.setTooltip("标识该属性的值类型，诸如字符串、整型、布尔值等等");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(relDefinitionType.scopedBy.toString());
        item.setDefType(classDefinitionType.RelDef);
        item.setRelDirection(relDirection._1To2);
        item.setLabel("值类型：");
        item.setMandatory(true);
        item.setOrderValue(100);
        return item;
    }

    //RELATIONSHIP DEFINITION'S END1 UID
    //VALUE IS FROM CLASS DEFINITION's NAME
    public static ObjectItemDTO UID1() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("一端对象类型定义");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.UID1.toString());
        item.setLabel("一端对象类型定义：");
        item.setMandatory(true);
        item.setOrderValue(100);
        return item;
    }

    //RELATIONSHIP DEFINITION'S END2 UID
    //VALUE IS FROM CLASS DEFINITION's NAME
    public static ObjectItemDTO UID2() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("二端对象类型定义");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.UID2.toString());
        item.setLabel("二端对象类型定义：");
        item.setMandatory(true);
        item.setOrderValue(200);
        return item;
    }

    //Object's Property indicator
    public static ObjectItemDTO OBJUID() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(true);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("所属对象的Id");
        item.setPropertyValueType(propertyValueType.StringType.toString());
        item.setDefUID(propertyDefinitionType.UID.toString());
        item.setLabel("所属对象：");
        item.setMandatory(true);
        item.setOrderValue(100);
        return item;
    }

    //RELATIONSHIP DEFINITION'S END1 MIN QUANTITY
    //VALUE IS INTEGER
    //0 is not required
    //1 is at least one
    //* is multiply
    public static ObjectItemDTO Min1() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("关联关系一端对象数量的最小限定值");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.Min1.toString());
        item.setLabel("最小数量限定值（左端）：");
        item.setOptions(OptionItemDTO.endQuantityOptions());
        item.setMandatory(true);
        item.setOrderValue(110);
        return item;
    }

    public static ObjectItemDTO Min2() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("关联关系二端对象数量的最小限定值");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.Min2.toString());
        item.setLabel("最小数量限定值（右端）：");
        item.setOptions(OptionItemDTO.endQuantityOptions());
        item.setMandatory(true);
        item.setOrderValue(210);
        return item;
    }

    public static ObjectItemDTO Max1() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("关联关系一端对象数量的最大限定值");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.Max1.toString());
        item.setLabel("最大数量限定值（左端）：");
        item.setOptions(OptionItemDTO.endQuantityOptions());
        item.setMandatory(true);
        item.setOrderValue(120);
        return item;
    }

    public static ObjectItemDTO Max2() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setTooltip("关联关系二端对象数量的最大限定值");
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.Max2.toString());
        item.setLabel("最大数量限定值（右端）：");
        item.setOptions(OptionItemDTO.endQuantityOptions());
        item.setMandatory(true);
        item.setOrderValue(220);
        return item;
    }

    //REL INSTANCE's END1 ID
    public static ObjectItemDTO End1Id() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.UID1.toString());
        item.setLabel("对象Id（左端）：");
        item.setMandatory(true);
        item.setOrderValue(205);
        return item;
    }

    //REL INSTANCE's END2 ID
    public static ObjectItemDTO End2Id() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.UID2.toString());
        item.setLabel("对象Id（右端）：");
        item.setMandatory(true);
        item.setOrderValue(220);
        return item;
    }

    //REL INSTANCE'S DEF UID
    public static ObjectItemDTO DefUID() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.RelDefUID.toString());
        item.setLabel("关联关系名称：");
        item.setMandatory(true);
        item.setOrderValue(200);
        return item;
    }

    //REL INSTANCE'S End1 Name
    public static ObjectItemDTO End1Name() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.Name1.toString());
        item.setLabel("对象名称（左端）：");
        item.setMandatory(false);
        item.setOrderValue(210);
        return item;
    }

    //REL INSTANCE'S End2 Name
    public static ObjectItemDTO End2Name() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.EnumListType.toString());
        item.setDefUID(propertyDefinitionType.Name2.toString());
        item.setLabel("对象名称（右端）：");
        item.setMandatory(false);
        item.setOrderValue(225);
        return item;
    }

    public static ObjectItemDTO WhenDelete1ToDelete2() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.BooleanType.toString());
        item.setDefUID(propertyDefinitionType.Delete12.toString());
        item.setLabel("删除一端的同时删除二端：");
        item.setMandatory(false);
        item.setOrderValue(130);
        return item;
    }

    public static ObjectItemDTO WhenDelete2ToDelete1() {
        ObjectItemDTO item = new ObjectItemDTO();
        item.setReadOnly(false);
        item.setHidden(false);
        item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        item.setPropertyValueType(propertyValueType.BooleanType.toString());
        item.setDefUID(propertyDefinitionType.Delete21.toString());
        item.setLabel("删除二端的同时删除一端：");
        item.setMandatory(false);
        item.setOrderValue(230);
        return item;
    }

    public static Map<String, ObjectItemDTO> buildInObjectItems() {
        Map<String, ObjectItemDTO> _defaultObjectItemDTOs = new HashMap<>();
        _defaultObjectItemDTOs.put(propertyDefinitionType.OBID.toString(), Obid());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Name.toString(), Name());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Description.toString(), Description());
        _defaultObjectItemDTOs.put(propertyDefinitionType.CreationUser.toString(), CreationUser());
        _defaultObjectItemDTOs.put(propertyDefinitionType.CreationDate.toString(), CreationDate());
        _defaultObjectItemDTOs.put(propertyDefinitionType.LastUpdateUser.toString(), LastUpdateUser());
        _defaultObjectItemDTOs.put(propertyDefinitionType.LastUpdateDate.toString(), LastUpdateDate());
        _defaultObjectItemDTOs.put(propertyDefinitionType.TerminationUser.toString(), TerminationUser());
        _defaultObjectItemDTOs.put(propertyDefinitionType.TerminationDate.toString(), TerminationDate());
        _defaultObjectItemDTOs.put(propertyDefinitionType.UniqueKeyPattern.toString(), UniqueKeyDef());
        _defaultObjectItemDTOs.put(propertyDefinitionType.ClassDefinitionUID.toString(), ClassDefinitionUID());
        _defaultObjectItemDTOs.put(propertyDefinitionType.UID1.toString(), UID1());
        _defaultObjectItemDTOs.put(propertyDefinitionType.UID2.toString(), UID2());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Min1.toString(), Min1());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Max1.toString(), Max1());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Min2.toString(), Min2());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Max2.toString(), Max2());
        _defaultObjectItemDTOs.put(propertyDefinitionType.RelDefUID.toString(), DefUID());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Name1.toString(), End1Name());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Name2.toString(), End2Name());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Delete12.toString(), WhenDelete1ToDelete2());
        _defaultObjectItemDTOs.put(propertyDefinitionType.Delete21.toString(), WhenDelete2ToDelete1());
        _defaultObjectItemDTOs.put(propertyDefinitionType.UniqueKey.toString(), UniqueKey());
        _defaultObjectItemDTOs.put(propertyDefinitionType.UID.toString(), UniqueID());
        _defaultObjectItemDTOs.put(relDefinitionType.scopedBy.toString(), ScopedBy());
        return _defaultObjectItemDTOs;
    }

}
