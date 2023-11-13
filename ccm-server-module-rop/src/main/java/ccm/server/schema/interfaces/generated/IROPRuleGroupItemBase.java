package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.module.param.ROPValueProcessor;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class IROPRuleGroupItemBase extends InterfaceDefault implements IROPRuleGroupItem {
    public IROPRuleGroupItemBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super("IROPRuleGroupItem", instantiateRequiredProperties);
    }

    public IROPRuleGroupItemBase(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }


    @Override
    public String ROPTargetPropertyDefinitionUID() {
        IProperty property = this.getProperty("IROPRuleGroupItem", "ROPTargetPropertyDefinitionUID");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPTargetPropertyDefinitionUID(String value) throws Exception {
        this.Interfaces().item("IROPRuleGroupItem", true).Properties().item("ROPTargetPropertyDefinitionUID", true).setValue(value);
    }

    @Override
    public String ROPCalculationValue() {
        IProperty property = this.getProperty("IROPRuleGroupItem", "ROPCalculationValue");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPCalculationValue(String value) throws Exception {
        this.Interfaces().item("IROPRuleGroupItem", true).Properties().item("ROPCalculationValue", true).setValue(value);
    }

    @Override
    public String ROPTargetPropertyValueUoM() {
        IProperty property = this.getProperty("IROPRuleGroupItem", "ROPTargetPropertyValueUoM");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPTargetPropertyValueUoM(String value) throws Exception {
        this.Interfaces().item("IROPRuleGroupItem", true).Properties().item("ROPTargetPropertyValueUoM", true).setValue(value);
    }

    @Override
    public IROPRuleGroup getGroup() throws Exception {
        IRel rel = this.GetEnd2Relationships().GetRel("ROPRuleGroup2Item");
        if (rel != null)
            return rel.GetEnd1().toInterface(IROPRuleGroup.class);
        return null;
    }

    protected String parseValueCriteria(String valueCriteria, String propertyDefinitionUID) throws Exception {
        String parsedValueCriteria = valueCriteria;
        boolean flag1 = false;
        boolean flag2 = false;
        if (valueCriteria.startsWith("{") && valueCriteria.endsWith("}")) {
            flag1 = true;
            parsedValueCriteria = parsedValueCriteria.substring(1, parsedValueCriteria.lastIndexOf("}"));
        } else if (!(valueCriteria.contains("[") || valueCriteria.contains("]") || valueCriteria.contains("(") || valueCriteria.contains(")")))
            flag2 = true;
        if (flag2 || flag1) {
            String[] strings = parsedValueCriteria.replace("|", ",").split(",");
            List<String> result = new ArrayList<>();
            for (String string : strings) {
                IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(propertyDefinitionUID, string);
                if (enumEnum != null) {
                    result.add(enumEnum.UID());
                } else
                    result.add(string);
            }
            if (flag1)
                return "{" + String.join("|", result) + "}";
            else
                return String.join("|", result);
        }
        return valueCriteria;
    }

    @Override
    public boolean isHintForProvidedObject(IObject object) throws Exception {
        if (object != null) {
            String propertyDefinitionUID = this.ROPTargetPropertyDefinitionUID();
            String valueCriteria = this.parseValueCriteria(this.ROPCalculationValue(), propertyDefinitionUID);
            return this.doHintIdentification(valueCriteria, object.getProperty(propertyDefinitionUID));
        }
        return false;
    }

    protected boolean doHintIdentification(String valueCriteria, IProperty property) throws Exception {
        if (!StringUtils.isEmpty(valueCriteria) && property != null) {
            ROPValueProcessor processor = new ROPValueProcessor(valueCriteria);
            Object value = property.Value();
            return processor.isHint(value);
        }
        return false;
    }

    @Override
    public String generateCalculatePropAndValueStr() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(propertyDefinitionType.ROPTargetPropertyDefinitionUID.toString(), this.ROPTargetPropertyDefinitionUID());
        jsonObject.put(propertyDefinitionType.ROPCalculationValue.toString(), this.ROPCalculationValue());
        return jsonObject.toJSONString();
    }

    @Override
    public String generateCalculatePropAndValueStr(@NotNull JSONObject jsonObject) {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(propertyDefinitionType.ROPTargetPropertyDefinitionUID.toString(), jsonObject.getString(propertyDefinitionType.ROPTargetPropertyDefinitionUID.toString()));
        jsonObject1.put(propertyDefinitionType.ROPCalculationValue.toString(), jsonObject.getString(propertyDefinitionType.ROPCalculationValue.toString()));
        return jsonObject1.toJSONString();
    }
}
