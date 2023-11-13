package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class IROPWorkStepBase extends InterfaceDefault implements IROPWorkStep {

    public IROPWorkStepBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.IROPWorkStep.toString(), instantiateRequiredProperties);
    }

    @Override
    public IROPRuleGroup getROPRuleGroup() throws Exception {
        IObject iObject = this.GetEnd2Relationships().GetRel(relDefinitionType.ROPRuleGroup2ROPWorkStep.toString()).GetEnd1();
        return iObject != null ? iObject.toInterface(IROPRuleGroup.class) : null;
    }


    @Override
    public void setROPWorkStepMaterialIssue(String materialIssue) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepMaterialIssue.toString(), true).setValue(materialIssue);
    }

    @Override
    public String ROPWorkStepMaterialIssue() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepMaterialIssue.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public String ROPWorkStepTPPhase() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepTPPhase.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPWorkStepTPPhase(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepTPPhase.toString(), true).setValue(value);
    }

    @Override
    public String ROPWorkStepWPPhase() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepWPPhase.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPWorkStepWPPhase(String workStepWPPhase) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepWPPhase.toString(), true).setValue(workStepWPPhase);
    }

    @Override
    public String ROPWorkStepGenerateMode() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepGenerateMode.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPWorkStepGenerateMode(String generateMode) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepGenerateMode.toString(), true).setValue(generateMode);
    }

    @Override
    public String ROPWorkStepName() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepName.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPWorkStepName(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepName.toString(), true).setValue(value);
    }

    @Override
    public String ROPWorkStepType() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepType.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPWorkStepType(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepType.toString(), true).setValue(value);
    }

    @Override
    public boolean ROPWorkStepAllowInd() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepAllowInd.toString());
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setROPWorkStepAllowInd(boolean value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepAllowInd
                .toString(), true).setValue(value);
    }

    @Override
    public boolean ROPWorkStepConsumeMaterialInd() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepConsumeMaterialInd.toString());
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setROPWorkStepConsumeMaterialInd(boolean value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepConsumeMaterialInd
                .toString(), true).setValue(value);
    }

    @Override
    public String ROPWorkStepWeightCalculateProperty() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepWeightCalculateProperty
                .toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPWorkStepWeightCalculateProperty(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepWeightCalculateProperty.toString(), true).setValue(value);
    }

    @Override
    public double ROPWorkStepBaseWeight() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepBaseWeight.toString());
        return ValueConversionUtility.toDouble(property);
    }

    @Override
    public void setROPWorkStepBaseWeight(double value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepBaseWeight
                .toString(), true).setValue(value);
    }

    @Override
    public int ROPWorkStepOrderValue() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPWorkStep.toString(), propertyDefinitionType.ROPWorkStepOrderValue.toString());
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setROPWorkStepOrderValue(int value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPWorkStep.toString(), true).Properties().item(propertyDefinitionType.ROPWorkStepOrderValue.toString(), true).setValue(value);
    }

    /*
     * @Description: 生成工作步骤对象
     * @param
     * @return: IWorkStep
     * @Author: Chen Jing
     * @Date: 2022-05-129 10:18:01
     */
    @Override
    public IObject generateWorkStepObject() throws Exception {
        return this.generateWorkStepObject(null);
    }

    @Override
    public String getWorkStepDisplayName() throws Exception {
        String workStepName = this.ROPWorkStepName();
        IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(propertyDefinitionType.ROPWorkStepName.toString(), workStepName);
        if (enumEnum != null)
            return enumEnum.generateDisplayAs();
        return workStepName;
    }

    @Override
    public IObject generateWorkStepObject(String infoString) throws Exception {
        IObject workStep;
        if (!StringUtils.isEmpty(infoString)) {
            workStep = SchemaUtility.newIObject(classDefinitionType.CCMWorkStep.toString(), this.getWorkStepDisplayName(), this.getWorkStepDisplayName() + infoString, null, null);
        } else {
            workStep = SchemaUtility.newIObject(classDefinitionType.CCMWorkStep.toString(), this.Name(), this.Name(), null, null);
        }
        if (workStep == null)
            throw new Exception("failed to create object for " + classDefinitionType.CCMWorkStep + " with name " + this.Name());
        IWorkStep workStepObject = workStep.Interfaces().item(IWorkStep.class.getSimpleName(), true).toInterface(IWorkStep.class);
        workStepObject.setROPRule(JSON.toJSONString(generateIdentity()));
        workStepObject.setWSConsumeMaterial(this.ROPWorkStepConsumeMaterialInd());
        workStepObject.setWSTPProcessPhase(this.ROPWorkStepTPPhase());
        workStepObject.setWSWPProcessPhase(this.ROPWorkStepWPPhase());
        IROPWorkStep iropWorkStep = workStep.Interfaces().item(IROPWorkStep.class.getSimpleName(), true).toInterface(IROPWorkStep.class);
        iropWorkStep.setROPWorkStepAllowInd(this.ROPWorkStepAllowInd());
        iropWorkStep.setROPWorkStepBaseWeight(this.ROPWorkStepBaseWeight());
        iropWorkStep.setROPWorkStepConsumeMaterialInd(this.ROPWorkStepConsumeMaterialInd());
        iropWorkStep.setROPWorkStepName(this.ROPWorkStepName());
        iropWorkStep.setROPWorkStepTPPhase(this.ROPWorkStepTPPhase());
        iropWorkStep.setROPWorkStepWPPhase(this.ROPWorkStepWPPhase());
        iropWorkStep.setROPWorkStepType(this.ROPWorkStepType());
        iropWorkStep.setROPWorkStepWeightCalculateProperty(this.ROPWorkStepWeightCalculateProperty());
        iropWorkStep.setROPWorkStepMaterialIssue(this.ROPWorkStepMaterialIssue());
        iropWorkStep.setROPWorkStepOrderValue(this.ROPWorkStepOrderValue());
        iropWorkStep.setROPWorkStepGenerateMode(this.ROPWorkStepGenerateMode());
        workStep.ClassDefinition().FinishCreate(workStep);
        return workStep;
    }


    @Override
    public Map<String, Object> generateIdentity() {
        Map<String, Object> map = new HashMap<>();
        map.put(propertyDefinitionType.ROPWorkStepTPPhase.toString(), this.ROPWorkStepTPPhase());
        map.put(propertyDefinitionType.ROPWorkStepWPPhase.toString(), this.ROPWorkStepWPPhase());
        map.put(propertyDefinitionType.ROPWorkStepName.toString(), this.ROPWorkStepName());
        map.put(propertyDefinitionType.ROPWorkStepType.toString(), this.ROPWorkStepType());
        map.put(propertyDefinitionType.ROPWorkStepConsumeMaterialInd.toString(), this.ROPWorkStepConsumeMaterialInd());
        map.put(propertyDefinitionType.ROPWorkStepAllowInd.toString(), this.ROPWorkStepAllowInd());
        map.put(propertyDefinitionType.ROPWorkStepBaseWeight.toString(), this.ROPWorkStepBaseWeight());
        map.put(propertyDefinitionType.ROPWorkStepWeightCalculateProperty.toString(), this.ROPWorkStepWeightCalculateProperty());
        map.put(propertyDefinitionType.ROPWorkStepOrderValue.toString(), this.ROPWorkStepOrderValue());
        map.put(propertyDefinitionType.ROPWorkStepMaterialIssue.toString(), this.ROPWorkStepMaterialIssue());
        map.put(propertyDefinitionType.ROPWorkStepGenerateMode.toString(), this.ROPWorkStepGenerateMode());
        return map;
    }

    //是否为正常生成步骤
    @Override
    public boolean isNormalGenerated() {
        return this.ROPWorkStepGenerateMode().equalsIgnoreCase(ropWorkStepGenerateMode.EN_NormalGenerate.toString());
    }

    //是否为升版遗留生成步骤
    @Override
    public boolean isReserveGenerated() {
        return this.ROPWorkStepGenerateMode().equalsIgnoreCase(ropWorkStepGenerateMode.EN_ReserveGenerate.toString());
    }

    //是否为强制生成步骤
    @Override
    public boolean isForceGenerated() {
        return this.ROPWorkStepGenerateMode().equalsIgnoreCase(ropWorkStepGenerateMode.EN_ForceGenerated.toString());
    }
}
