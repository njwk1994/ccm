package ccm.server.dto.base;

import ccm.server.entity.MetaDataObjProperty;
import ccm.server.entity.MetaDataRel;
import ccm.server.enums.ExpansionMode;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relDirection;
import ccm.server.model.LiteCriteria;
import ccm.server.util.CommonUtility;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@ApiModel("前后端交互用于创建更新对象")
@Slf4j
public class ObjectItemDTO implements Serializable {
    private static final long serializableId = 1L;
    private boolean dynamical = false;
    private String defUID;
    private classDefinitionType defType;
    private String label;
    private Object displayValue;
    private String propertyValueType;
    private String tooltip;
    private String groupHeader;
    private Integer orderValue;

    private Integer sectionOrderValue;
    private double width = 100.0;
    private Boolean readOnly;
    private Boolean hidden;
    private Boolean mandatory;
    private String criteria;
    private String operator;
    private int columnSpan = 1;
    private String obid;//current property or relationship id
    private final List<OptionItemDTO> options = new ArrayList<>();
    private ccm.server.enums.relDirection relDirection;
    private String objObid; //current property belong to object's id, or relationship from object's id
    private final List<ObjectItemDTO> dependentOn = new ArrayList<>();

    public String expansionModePlusIdentity() {
        ExpansionMode expansionMode = ExpansionMode.none;
        if (this.defType == classDefinitionType.RelDef)
            expansionMode = ExpansionMode.relatedObject;
        else if (this.defType == classDefinitionType.Rel)
            expansionMode = ExpansionMode.relationship;
        String mainPart = this.identity();
        return expansionMode + "->>>" + mainPart;
    }

    public String identity() {
        String mainPart = this.defUID;
        relDirection relDirection = this.relDirection;
        if (relDirection == null || relDirection == ccm.server.enums.relDirection._unknown)
            relDirection = ccm.server.enums.relDirection._1To2;
        String prefix = relDirection.getPrefix();
        if (this.defType != classDefinitionType.PropertyDef) {
            if (!mainPart.contains(".")) {
                mainPart = prefix + mainPart + ".Name";
            }
        }
        return mainPart;
    }

    public LiteCriteria liteCriteria() {
        ExpansionMode expansionMode = ExpansionMode.none;
        if (this.defType == classDefinitionType.RelDef)
            expansionMode = ExpansionMode.relatedObject;
        else if (this.defType == classDefinitionType.Rel)
            expansionMode = ExpansionMode.relationship;
        return CommonUtility.getLiteCriteria(expansionMode, this.expansionModePlusIdentity());
    }

    public void specialProgressForDisplayItem() {
        ObjectItemDTO objectItemDTO = this;
        String defUid = objectItemDTO.getDefUID();
        Boolean readOnly = objectItemDTO.getReadOnly();
        objectItemDTO.setReadOnly(true);
        if (defUid.equalsIgnoreCase(propertyDefinitionType.LastUpdateDate.toString())) {
            objectItemDTO.setLabel("最后修改时间");
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.LastUpdateUser.toString())) {
            objectItemDTO.setLabel("最后修改人员");
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.CreationUser.toString())) {
            objectItemDTO.setLabel("创建人");
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.CreationDate.toString())) {
            objectItemDTO.setLabel("创建日期");
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.UID.toString())) {
            objectItemDTO.setLabel("唯一标识码");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.OBID.toString())) {
            objectItemDTO.setLabel("表主键码");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.ClassDefinitionUID.toString())) {
            objectItemDTO.setLabel("类型定义");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.DomainUID.toString())) {
            objectItemDTO.setLabel("所属域标识");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.ContainerID.toString())) {
            objectItemDTO.setLabel("所属容器标识");
            objectItemDTO.setHidden(true);
            objectItemDTO.setReadOnly(false);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.UniqueKey.toString())) {
            objectItemDTO.setLabel("全局唯一标识码");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.Config.toString())) {
            objectItemDTO.setLabel("所属项目");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.TerminationUser.toString())) {
            objectItemDTO.setLabel("终止人");
            objectItemDTO.setHidden(true);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.TerminationDate.toString())) {
            objectItemDTO.setLabel("终止时间");
            objectItemDTO.setHidden(true);
        }
//        else if (defUid.equalsIgnoreCase(propertyDefinitionType.Name.toString())) {
//            objectItemDTO.setLabel("编号");
//            objectItemDTO.setHidden(false);
//        }
        else if (defUid.equalsIgnoreCase(propertyDefinitionType.Description.toString())) {
            objectItemDTO.setLabel("描述");
            objectItemDTO.setHidden(false);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.DisplayAs.toString())) {
            objectItemDTO.setLabel("显示名");
            objectItemDTO.setHidden(false);
        } else if (defUid.equalsIgnoreCase(propertyDefinitionType.DisplayName.toString())) {
            objectItemDTO.setLabel("显示名");
            objectItemDTO.setHidden(false);
        } else
            objectItemDTO.setReadOnly(readOnly);
    }

    public boolean hasOptions() {
        return this.options.size() > 0;
    }

    public ObjectItemDTO copyTo() {
        ObjectItemDTO result = new ObjectItemDTO();
        result.setDefUID(this.defUID);
        result.setRelDirection(this.relDirection);
        result.setDefType(this.defType);
        result.setLabel(this.label);
        result.setDisplayValue(this.displayValue);
        result.setPropertyValueType(this.propertyValueType);
        result.setTooltip(this.tooltip);
        result.setGroupHeader(this.groupHeader);
        result.setOrderValue(this.orderValue);
        result.setReadOnly(this.readOnly);
        result.setHidden(this.hidden);
        result.setMandatory(this.mandatory);
        result.setCriteria(this.criteria);
        result.setOperator(this.operator);
        result.setObid(this.obid);
        result.setWidth(this.width);
        result.setColumnSpan(this.columnSpan);
        result.getOptions().addAll(this.options.stream().map(OptionItemDTO::copyTo).collect(Collectors.toList()));
        result.setObjObid(this.objObid);
        result.getDependentOn().addAll(this.dependentOn);
        return result;
    }

    public String getObid() {
        if (this.obid == null)
            this.obid = "";
        return this.obid;
    }

    public String getObjObid() {
        if (this.objObid == null)
            this.objObid = "";
        return this.objObid;
    }

    public String getDefUID() {
        if (this.defUID == null)
            this.defUID = "";
        return this.defUID;
    }

    public void setOptions(List<OptionItemDTO> options) {
        this.getOptions().addAll(options);
    }

    public String toValue() {
        if (this.getDisplayValue() != null) {
            if (this.getDisplayValue().getClass().isInstance(OptionItemDTO.class)) {
                OptionItemDTO optionItemDTO = (OptionItemDTO) this.getDisplayValue();
                if (optionItemDTO != null)
                    return optionItemDTO.getUid();
            } else
                return this.getDisplayValue().toString();
        }
        return "";
    }

    public ObjectItemDTO() {
        this.setDynamical(false);
        this.setOrderValue(-1);
        this.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
        this.setDefType(classDefinitionType.PropertyDef);
        this.setRelDirection(ccm.server.enums.relDirection._unknown);
    }

    public ObjectItemDTO(MetaDataObjProperty property) {
        this();
        if (property != null) {
            this.setDefUID(property.getPropertyDefUid());
            this.setDisplayValue(property.getStrValue());
            this.setObid(property.getObid());
            this.setObjObid(property.getObjObid());
        }
    }

    public ObjectItemDTO(MetaDataRel rel, relDirection relDirection, String labelName, String tooltip, int orderValue, String groupHeader) {
        this(rel != null ? rel.getRelDefUid() : "", labelName, tooltip, "", ccm.server.enums.propertyValueType.EnumListType.toString(), groupHeader, orderValue, false);
        this.setObid(rel != null ? rel.getObid() : "");
        this.setDefType(classDefinitionType.RelDef);
        switch (relDirection) {
            case _1To2:
                this.setDisplayValue(rel != null ? rel.getUid2() : "");
                break;
            case _2To1:
                this.setDisplayValue(rel != null ? rel.getUid1() : "");
                break;
        }
    }

    public ObjectItemDTO(String defUID, String labelName, String tooltip, String value, String valueType, String groupHeader, Integer orderValue, Boolean readOnly) {
        this();
        this.setDefUID(defUID);
        this.setLabel(labelName);
        this.setTooltip(tooltip);
        this.setDisplayValue(value);
        this.setPropertyValueType(valueType);
        this.setGroupHeader(groupHeader);
        this.setOrderValue(orderValue);
        this.setReadOnly(readOnly);
    }

    @Override
    public String toString() {
        return "ObjectItemDTO{" +
                "defUID='" + defUID + '\'' +
                ", defType=" + defType +
                ", objId='" + objObid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectItemDTO that = (ObjectItemDTO) o;
        return defUID.equals(that.defUID) &&
                defType == that.defType &&
                objObid.equals(that.objObid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defUID, defType, objObid);
    }

    public static ObjectItemDTO toGetItem(List<ObjectItemDTO> items, String defUID) {
        if (!StringUtils.isEmpty(defUID) && CommonUtility.hasValue(items)) {
            Optional<ObjectItemDTO> first = items.stream().filter(c -> c.getDefUID().equalsIgnoreCase(defUID)).findFirst();
            if (first.isPresent())
                return first.get();
        }
        return null;
    }

    public static String toGetValue(List<ObjectItemDTO> items, String defUID) {
        if (!StringUtils.isEmpty(defUID) && CommonUtility.hasValue(items)) {
            ObjectItemDTO item = toGetItem(items, defUID);
            if (item != null)
                return item.toValue();
        }
        return "";
    }
}
