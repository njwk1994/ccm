package ccm.server.dto.base;

import ccm.server.entity.MetaDataObj;
import ccm.server.entity.MetaDataObjProperty;
import ccm.server.entity.MetaDataRel;
import ccm.server.enums.*;
import ccm.server.helper.HardCodeHelper;
import ccm.server.util.CommonUtility;
import com.xkcoding.http.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ObjectDTO implements Serializable {

    public <T extends ObjectDTO> T toDTO(Class<T> tClass) throws IllegalAccessException, InstantiationException {
        T result = tClass.newInstance();
        result.add(this.getItems());
        return result;
    }

    public void addItemIfNotExist(String defUID, Object displayValue) {
        if (!this.hasItem(defUID)) {
            ObjectItemDTO objectItemDTO = new ObjectItemDTO();
            objectItemDTO.setDefUID(defUID);
            objectItemDTO.setDisplayValue(displayValue);
            this.add(objectItemDTO);
        }
    }

    public void manualSetFormPurpose(String formPurpose) {
        this.toSetReadOnly(true, propertyDefinitionType.ClassDefinitionUID.toString());
        this.toHideSystemProperties(true);
        if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Create.toString())) {

        } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Update.toString())) {
            this.toSetReadOnly(true, propertyDefinitionType.Name.toString());
        } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Info.toString())) {
            this.setAllPropertiesToBeReadOnly();
            this.toHideSystemProperties(false);
        } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.Query.toString())) {
            this.toHideSystemProperties(false);
        } else if (formPurpose.equalsIgnoreCase(ccm.server.enums.formPurpose.List.toString())) {

        }

    }

    public void manualSetClassDefinitionUID(String classDefinitionUID) {
        ObjectItemDTO itemDTO = this.toGetItem(propertyDefinitionType.ClassDefinitionUID.toString());
        if (itemDTO != null)
            itemDTO.setDisplayValue(classDefinitionUID);
        else {
            itemDTO = HardCodeHelper.ClassDefinitionUID(classDefinitionUID);
            this.add(itemDTO);
        }
    }

    public void setAllPropertiesToBeReadOnly() {
        for (ObjectItemDTO item : this.getItems()) {
            item.setReadOnly(true);
        }
    }

    public void removeProperty(String propertyDefUID) {
        if (!StringUtils.isEmpty(propertyDefUID)) {
            this.items.stream().filter(c -> c.getDefUID().equalsIgnoreCase(propertyDefUID)).findFirst().ifPresent(this.items::remove);
        }
    }

    public List<String> usedRelDefs() {
        if (this.hasRelationship())
            return this.items.stream().filter(c -> c.getDefType().equals(classDefinitionType.RelDef)).map(ObjectItemDTO::getDefUID).distinct().collect(Collectors.toList());
        return null;
    }

    public List<String> getExpansionPaths() {
        List<String> result = new ArrayList<>();
        if (this.hasItem()) {
            for (ObjectItemDTO item : this.items) {
                String str = "";
                switch (item.getDefType()) {
                    case EdgeDef:
                    case RelDef:
                        str = ExpansionMode.relatedObject.toString();
                        break;
                    case Rel:
                        str = ExpansionMode.relationship.toString();
                        break;
                }
                if (!StringUtils.isEmpty(str)) {
                    str = str + "->>>" + item.getRelDirection().getPrefix() + item.getDefUID();
                    result.add(str);
                }
            }
        }
        return result;
    }

    public List<String> usedEdgeDefs() {
        if (this.hasRelationship())
            return this.items.stream().filter(c -> c.getDefType().equals(classDefinitionType.EdgeDef)).map(ObjectItemDTO::getDefUID).distinct().collect(Collectors.toList());
        return null;
    }

    public boolean hasEdgeDef() {
        if (this.hasItem())
            return this.items.stream().anyMatch(c -> c.getDefType().equals(classDefinitionType.EdgeDef));
        return false;
    }

    public Boolean hasClassDef() {
        String classDef = this.getClassDefinitionUID();
        return classDef != null && !StringUtil.isEmpty(classDef.trim());
    }

    public Boolean needFromDb() {
        if (!StringUtils.isEmpty(this.toGetValue(propertyDefinitionType.OBID.toString())) && !StringUtils.isEmpty(this.getName()))
            return false;
        return true;
    }

    public void update(List<ObjectItemDTO> items) {
        if (CommonUtility.hasValue(items)) {
            for (ObjectItemDTO t : items
            ) {
                ObjectItemDTO current = this.toGetItem(t.getDefUID());
                if (current != null)
                    current.setDisplayValue(t.getDisplayValue());
            }
        }
    }

    public void remove(String defUID) {
        if (!StringUtils.isEmpty(defUID)) {
            ObjectItemDTO item = this.toGetItem(defUID);
            if (item != null)
                this.remove(item);
        }
    }

    public boolean validDBRecord() {
        return this.hasClassDef() && !StringUtil.isEmpty(this.toGetValue(propertyDefinitionType.OBID.toString()));
    }

    public boolean valid() {
        return this.validDBRecord();
    }

    public List<ObjectItemDTO> relationships(String relDef) {
        if (!StringUtils.isEmpty(relDef))
            return this.items.stream().filter(c -> c.getDefUID().equalsIgnoreCase(relDef) && c.getDefType().equals(classDefinitionType.RelDef)).collect(Collectors.toList());
        return null;
    }

    public boolean hasRelationship() {
        if (this.hasItem())
            return this.items.stream().anyMatch(c -> c.getDefType().equals(classDefinitionType.RelDef));
        return false;
    }

    public dataType dataType() {
        if (this.getClassDefinitionUID().equalsIgnoreCase(HardCodeHelper.CLASSDEF_REL))
            return dataType.rel;
        return dataType.data;
    }

    public void toLockRelationship() {
        this.toSetReadOnly(true,
                HardCodeHelper.relProperties().stream().map(Map.Entry::getKey).toArray());
    }

    @ApiModelProperty(hidden = true)
    protected Map<String, String> defaultProperties = new HashMap<>();

    //to set hidden attribute for all system properties
    //normally include CreationUser,CreationDate,LastUpdateDate,LastUpdateUser,TerminationUser,TerminationDate
    public void toHideSystemProperties(Boolean pblnHidden) {
        if (this.hasItem())
            this.toSetHidden(pblnHidden, CommonUtility.convertToArray(propertyDefinitionType.PropertiesNotShowToEndUser()));
    }

    //to set ReadOnly attribute for All properties with provided value
    //to  be All True of False
    public void toSetReadOnly(Boolean pblnReadOnly) {
        if (this.hasItem()) {
            for (ObjectItemDTO p : this.items)
                p.setReadOnly(pblnReadOnly);
        }
    }

    public void toSetOptions(String itemDefUID, List<OptionItemDTO> options) {
        if (!StringUtils.isEmpty(itemDefUID)) {
            ObjectItemDTO item = this.toGetItem(itemDefUID);
            if (item != null)
                item.setOptions(options);
        }
    }

    private static final long serializableId = 1L;

    //container to save all properties from MetaDataObj and MetaDataObjProperty tables
    private final List<ObjectItemDTO> items = new ArrayList<>();

    //to get property value of Description, normally Description is from MetaDataObj table
    public String getDescription() {
        return this.toGetValue(propertyDefinitionType.Description.toString());
    }

    //to get property value of Name, normally Name is from MetaDataObj table
    public String getName() {
        return this.toGetValue(propertyDefinitionType.Name.toString());
    }

    public String getUid() {
        return this.toGetValue(propertyDefinitionType.UID.toString());
    }

    public String getObid() {
        return this.toGetValue(propertyDefinitionType.OBID.toString());
    }

    //to reset object's properties only include provided property definition UIDs
    protected void ensureProperties(String... properties) {
        if (CommonUtility.hasValue(properties)) {
            List<ObjectItemDTO> resultItems = new ArrayList<>();
            for (ObjectItemDTO p : this.items) {
                if (Arrays.stream(properties).anyMatch(c -> p.getDefUID().equalsIgnoreCase(c)))
                    resultItems.add(p);
            }
            this.items.clear();
            this.items.addAll(resultItems);
        }
    }

    public void toSetValue(ObjectItemDTO itemDTO) {
        if (itemDTO != null && !StringUtils.isEmpty(itemDTO.getDefUID())) {
            ObjectItemDTO item = this.toGetItem(itemDTO.getDefUID());
            if (item != null)
                item.setDisplayValue(item.toValue());
            else
                this.add(itemDTO);
        }
    }

    //to set property value with provided property definition UID
    //if property item already there, use provided new value to override current display value
    //otherwise
    //create a new property item with default setting
    public void toSetValue(String propertyDef, String value) {
        ObjectItemDTO item = this.toGetItem(propertyDef);
        if (item != null)
            item.setDisplayValue(value);
        else {
            if (HardCodeHelper.buildInObjectItems().containsKey(propertyDef)) {
                item = HardCodeHelper.buildInObjectItems().get(propertyDef);
                item.setDisplayValue(value);
                this.add(item);
            } else {
                item = new ObjectItemDTO();
                item.setDisplayValue(value);
                item.setLabel(propertyDef);
                item.setDefUID(propertyDef);
                item.setPropertyValueType(propertyValueType.StringType.toString());
                item.setTooltip("object's" + propertyDef);
                item.setOrderValue(1);
                item.setGroupHeader(CommonUtility.PROPERTY_GROUP_GENERAL);
                item.setHidden(true);
                item.setReadOnly(true);
                item.setObjObid(this.toGetValue(propertyDefinitionType.OBID.toString()));
                this.add(item);
            }
        }
    }

    public void toSetPropertyValueType(String pstrDefUID, propertyValueType propertyValueType) {
        ObjectItemDTO itemProperty = this.toGetItem(pstrDefUID);
        if (itemProperty != null) {
            itemProperty.setPropertyValueType(propertyValueType.toString());
        }
    }

    //to get property item by specified property definition UID
    public ObjectItemDTO toGetItem(Object pstrPropertyDefUID) {
        if (pstrPropertyDefUID != null && !StringUtils.isEmpty(pstrPropertyDefUID.toString()))
            return this.items.stream().filter(c -> c.getDefUID().equalsIgnoreCase(pstrPropertyDefUID.toString())).findAny().orElse(null);
        return null;
    }

    //to calculate if object include specified property item with property definition UID
    public Boolean hasItem(String propertyDefUID) {
        if (!StringUtils.isEmpty(propertyDefUID)) {
            ObjectItemDTO objectItemDTO = this.toGetItem(propertyDefUID);
            if (objectItemDTO != null)
                return !StringUtils.isEmpty(objectItemDTO.toValue());
        }
        return false;
    }

    //to get indicator whether object includes any properties
    public Boolean hasItem() {
        return this.items.size() > 0;
    }


    public Double toGetDoubleValue(String defUID) {
        String value = this.toGetValue(defUID);
        double result = 0.0;
        if (!StringUtils.isEmpty(value)) {
            try {
                result = Double.parseDouble(value);
            } catch (Exception ignored) {
                log.error(ignored.getMessage());
            }
        }
        return result;
    }

    //to get property item value by specified property definition UID
    public String toGetValue(String defUID) {
        if (!StringUtils.isEmpty(defUID)) {
            if (this.items.size() > 0) {
                Optional<ObjectItemDTO> itemDTO = this.items.stream().filter(r -> r.getDefUID().equalsIgnoreCase(defUID)).findFirst();
                if (itemDTO.isPresent())
                    return itemDTO.get().toValue();
            }
        }
        return "";
    }


    //to set hidden attribute for property items by provided property definition UIDs
    public void toSetHidden(boolean pblnHidden, String... pcolDefUIDs) {
        if (pcolDefUIDs != null && pcolDefUIDs.length > 0) {
            for (String p : pcolDefUIDs) {
                ObjectItemDTO property = this.toGetItem(p);
                if (property != null) {
                    property.setHidden(pblnHidden);
                    if (!pblnHidden)
                        property.setMandatory(false);
                }
            }
        }
    }

    public void toSetMandatory(boolean pblnMandatory, String... pcolDefUIDs) {
        if (pcolDefUIDs != null && pcolDefUIDs.length > 0) {
            for (String p : pcolDefUIDs) {
                ObjectItemDTO property = this.toGetItem(p);
                if (property != null)
                    property.setMandatory(pblnMandatory);
            }
        }
    }

    //to set ReadOnly attribute for property items by provided property definition UIDs
    public void toSetReadOnly(Boolean pblnReadOnly, Object... pcolDefUIDs) {
        if (pcolDefUIDs != null && pcolDefUIDs.length > 0) {
            for (Object p : pcolDefUIDs) {
                ObjectItemDTO property = this.toGetItem(p);
                if (property != null)
                    property.setReadOnly(pblnReadOnly);
            }
        }
    }

    //constructor with no parameter
    public ObjectDTO() {
        this.setFormPurpose(operationPurpose.create.toString());
        this.setDefaultProperties();
        this.initialized = false;
    }

    protected void setDefaultProperties() {
        this.defaultProperties.put(propertyDefinitionType.OBID.toString(), classDefinitionType.PropertyDef.toString());
        this.defaultProperties.put(propertyDefinitionType.UID.toString(), classDefinitionType.PropertyDef.toString());
//        this.defaultProperties.put(propertyDefinitionType.Name.toString(), classDefinitionType.PropertyDef.toString());
//        this.defaultProperties.put(propertyDefinitionType.Description.toString(), classDefinitionType.PropertyDef.toString());
        this.defaultProperties.put(propertyDefinitionType.ClassDefinitionUID.toString(), classDefinitionType.PropertyDef.toString());
    }

    protected void addDefaultProperty(String pstrPropertyDefUID, classDefinitionType type) {
        if (this.defaultProperties.containsKey(pstrPropertyDefUID))
            this.defaultProperties.replace(pstrPropertyDefUID, type.toString());
        else
            this.defaultProperties.put(pstrPropertyDefUID, type.toString());
    }

    @ApiModelProperty(hidden = true)
    private Boolean initialized;

    public ObjectDTO(MetaDataRel objREL, List<? extends MetaDataObjProperty> objPRs) {
        this();
        if (objREL != null) {
            this.add(objREL.toObjectItemDTOs());
            this.toSetObjID(objREL.getObid());
        }
        List<String> relProperties = HardCodeHelper.relProperties().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        if (objPRs != null && objPRs.size() > 0) {
            this.add(objPRs.stream().map(c -> {
                try {
                    if (relProperties.stream().noneMatch(m -> m.equalsIgnoreCase(c.getPropertyDefUid())))
                        return c.toObjectItemDTO();
                } catch (Exception exception) {
                    log.error(exception.getMessage());
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        this.initialized = true;
    }

    public void toSetObjID(String objId) {
        if (!StringUtils.isEmpty(objId) && this.items.size() > 0) {
            for (ObjectItemDTO p : this.items) {
                p.setObjObid(objId);
            }
        }
    }

    //constructor with provided MetaDataObj and corresponding OBJPRs
    public ObjectDTO(MetaDataObj metaObj, List<? extends MetaDataObjProperty> objPRs) {
        this();
        if (metaObj != null) {
            this.add(metaObj.toObjectItemDTOs());
            this.toSetObjID(metaObj.getObid());
        }
        if (objPRs != null && objPRs.size() > 0) {
            this.add(objPRs.stream().map(MetaDataObjProperty::toObjectItemDTO).collect(Collectors.toList()));

        }
        this.initialized = true;
    }

    //to save formPurpose
    //to switch property's status automatically
    @ApiModelProperty(hidden = true)
    protected String formPurpose;

    public String getClassDefinitionUID() {
        return this.toGetValue(propertyDefinitionType.ClassDefinitionUID.toString());
    }

    public ObjectDTO copyTo() {
        ObjectDTO result = new ObjectDTO();
        for (ObjectItemDTO objectItemDTO : this.items) {
            result.add(objectItemDTO.copyTo());
        }
        return result;
    }

    //to add property item by provided ObjectItemDTO
    //firstly to judge whether property item already exist
    //if exist remove it
    //add with newest(provided) one
    //to set order with container quantity at same time
    public void add(ObjectItemDTO item) {
        if (this.items.stream().anyMatch(r -> r.getDefUID().equalsIgnoreCase(item.getDefUID()))) {
            Optional<ObjectItemDTO> itemDTO = this.items.stream().filter(r -> r.getDefUID().equalsIgnoreCase(item.getDefUID())).findFirst();
            if (itemDTO.isPresent()) {
                itemDTO.get().setObid(item.getObid());
                itemDTO.get().setDisplayValue(item.getDisplayValue());
                itemDTO.get().setObjObid(item.getObjObid());
                itemDTO.get().setPropertyValueType(item.getPropertyValueType());
                itemDTO.get().setCriteria(item.getCriteria());
                itemDTO.get().setGroupHeader(item.getGroupHeader());
                itemDTO.get().setHidden(item.getHidden());
                itemDTO.get().setReadOnly(item.getReadOnly());
                itemDTO.get().setDefType(item.getDefType());
                itemDTO.get().setOrderValue(item.getOrderValue());
                itemDTO.get().setRelDirection(item.getRelDirection());
                itemDTO.get().setMandatory(item.getMandatory());
                itemDTO.get().setLabel(item.getLabel());
                itemDTO.get().setTooltip(item.getTooltip());
                if (CommonUtility.hasValue(item.getOptions()))
                    itemDTO.get().setOptions(item.getOptions());
            } else {
                if (item.getOrderValue() == -1)
                    item.setOrderValue(this.items.size() + 1);
                this.items.add(item);
            }
        } else {
            if (item.getOrderValue() == -1)
                item.setOrderValue(this.items.size() + 1);
            this.items.add(item);
        }
    }

    //to add multiply items with for each
    //to use add method defined before
    public void add(List<ObjectItemDTO> items) {
        if (items != null && items.size() > 0) {
            for (ObjectItemDTO item : items) {
                this.add(item);
            }
        }
    }

    //to remove specified item from object's properties
    public void remove(ObjectItemDTO item) {
        if (item != null) {
            ObjectItemDTO existOne = this.toGetItem(item.getDefUID());
            if (existOne != null)
                this.items.remove(existOne);
        }
    }

    public void reset() {
        if (this.hasItem()) {
            for (ObjectItemDTO item : this.getItems()
            ) {
                if (item.getDefUID().equalsIgnoreCase(propertyDefinitionType.ClassDefinitionUID.toString()))
                    continue;
                item.setDisplayValue("");
            }
        }
    }

    //to sort all properties with provided order value
    //if order value not set, sort with property name
    public void sort() {
        if (this.items.size() > 0) {
            this.items.sort(new Comparator<ObjectItemDTO>() {
                @Override
                public int compare(ObjectItemDTO o1, ObjectItemDTO o2) {
                    if (o1.getOrderValue() > -1 || o2.getOrderValue() > -1)
                        return o1.getOrderValue().compareTo(o2.getOrderValue());
                    return o1.getLabel().compareTo(o2.getLabel());
                }
            });
        }
    }

    public List<ObjectItemDTO> generateProperties() {
        List<ObjectItemDTO> result = new ArrayList<>();
        Map<String, ObjectItemDTO> currentItems = HardCodeHelper.buildInObjectItems();
        if (this.defaultProperties.size() > 0) {
            for (Map.Entry<String, String> t : this.defaultProperties.entrySet()) {
                ObjectItemDTO p = this.toGetItem(t.getKey());
                if (p != null)
                    result.add(p);
                else {
                    if (currentItems.containsKey(t.getKey())) {
                        ObjectItemDTO defaultItem = currentItems.get(t.getKey());
                        if (defaultItem != null)
                            result.add(defaultItem);
                    }
                }
            }
        }
        return result;
    }

    public Object getPointedPropValue(@NotNull String pstrPropDefUID) {
        if (CommonUtility.hasValue(this.items)) {
            ObjectItemDTO objectItemDTO = this.items.stream().filter(r -> pstrPropDefUID.equalsIgnoreCase(r.getDefUID())).findFirst().orElse(null);
            if (objectItemDTO != null) {
                return objectItemDTO.getDisplayValue();
            }
        }
        return null;
    }

    //专门用来给设计试压包材料规格下的材料模板重设OBID信息使用,其他地方勿用 最后OBID 赋值为 试压包材料规格的OBID_材料模板的OBID
    public void setSpecialPropValue(String propDef, Object value) {
        this.items.stream().filter(r -> propDef.equalsIgnoreCase(r.getDefUID())).findFirst().ifPresent(objectItemDTO -> objectItemDTO.setDisplayValue(value));
    }
}
