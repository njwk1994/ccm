package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class IWorkStepBase extends InterfaceDefault implements IWorkStep {
    public IWorkStepBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.IWorkStep.toString(), instantiateRequiredProperties);
    }

    @Override
    public double WSWeight() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSWeight.toString());
        return ValueConversionUtility.toDouble(property);
    }

    @Override
    public void setWSWeight(double value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSWeight.toString(), true).setValue(value);
    }

    @Override
    public void setWSStatus(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSStatus.toString(), true).setValue(value);
    }

    @Override
    public String WSStatus() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSStatus.toString());
        return ValueConversionUtility.toString(property);
    }

    /*
     * @Description: 计算工作步骤权重
     * @param targetPropertyDefinition  计算属性定义
     * @Return: double
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:32:09
     */
    @Override
    public double calculateWeight(String targetPropertyDefinition) {
        IROPWorkStep iropWorkStep = this.toInterface(IROPWorkStep.class);
        if (iropWorkStep != null) {
            double dblValue = 1.0;
            double weightPercentage = iropWorkStep.ROPWorkStepBaseWeight();
          //  log.info("base weight percentage is " + weightPercentage);
            IProperty property = this.getProperty(targetPropertyDefinition);
            if (property == null) {
               // log.warn("no property found for weight calculation under" + this.toErrorPop() + " and return base weight directly");
            } else {
                Object value = property.Value();
                if (value == null || StringUtils.isEmpty(value.toString())) {
                    log.error("null value under specified property and return base weight directly");
                } else {
                    try {
                        dblValue = Double.parseDouble(value.toString());
                    } catch (NumberFormatException e) {
                        log.error(value + " is not valid numeric style and use 1 as element");
                    }
                }
            }
            double result = dblValue * weightPercentage;
        //    log.info("calculated weight is " + result + this.toErrorPop());
            return result;
        } else {
          //  log.warn("can not find base weight setting for calculation and use 1.0 to instead of");
        }
        return 1.0;
    }

    @Override
    public void setROPRule(String ropRule) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSROPRule.toString(), true).setValue(ropRule);
    }

    @Override
    public String ROPRule() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSROPRule.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public boolean hasActualCompletedDate() {
        IProperty actualEnd = this.getProperty("ActualEnd");
        return actualEnd != null && actualEnd.Value() != null && !StringUtils.isEmpty(actualEnd.Value().toString());
    }

    @Override
    public boolean WSConsumeMaterial() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSConsumeMaterial.toString());
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setWSConsumeMaterial(boolean value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSConsumeMaterial.toString(), true).setValue(value);
    }

    @Override
    public String WSComponentName() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSComponentName.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setWSComponentName(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSComponentName.toString(), true).setValue(value);
    }

    @Override
    public String WSComponentDesc() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSComponentDesc.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setWSComponentDesc(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSComponentDesc.toString(), true).setValue(value);
    }

    //设置步骤属性,以及同步设计对象属性到步骤上
    @Override
    public void saveGeneralInfo(IObject pobjDesignObj, String propertyDefinitionForWeightCalculation) throws Exception {
        this.BeginUpdate();
        double weight = this.calculateWeight(propertyDefinitionForWeightCalculation);
        this.setWSWeight(weight);
        this.setWSStatus(workStepStatus.EN_Typical.toString());
        if (pobjDesignObj != null) {
            this.setWSComponentDesc(pobjDesignObj.Description());
            this.setWSComponentName(pobjDesignObj.Name());
           // this.setDescription(pobjDesignObj.Description());
        }
        this.syncInfoFromSource(pobjDesignObj, false);
        this.FinishUpdate();
    }

    /*
     * @Description: 是否关联了工作包
     * @param
     * @Return: boolean
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:31:47
     */
    @Override
    public boolean hasRelatedWorkPackage() throws Exception {
        IRelCollection relOfWorkPackage = this.GetEnd2Relationships().GetRels(relDefinitionType.CCMWorkPackage2WorkStep.toString(), false);
        return SchemaUtility.hasValue(relOfWorkPackage);

    }

    /*
     * @Description: 是否关联的试压包
     * @param
     * @Return: boolean
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:31:35
     */
    @Override
    public boolean hasRelatedPTPPackage() throws Exception {
        IRelCollection relOfPT = this.GetEnd2Relationships().GetRels(relDefinitionType.CCMPTPackage2WorkStep.toString(), false);
        return SchemaUtility.hasValue(relOfPT);
    }

    /*
     * @Description: 设置步骤状态
     * @param workStepStatus 步骤状态
     * @param needTransaction 是否开启书屋
     * @Return: void
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:31:11
     */
    @Override
    public void setWorkStepStatus(workStepStatus workStepStatus, boolean needTransaction) throws Exception {

        if (workStepStatus != null) {
            if (needTransaction) SchemaUtility.beginTransaction();
            this.BeginUpdate();
            this.setWSStatus(workStepStatus.toString());
            this.FinishUpdate();
            if (needTransaction) SchemaUtility.commitTransaction();
        }
    }

    private List<String> propertiesNotSyncFromComponent() {
        return new ArrayList<String>() {
            {
                add("ROPWorkStepPhase");
            }
        };
    }

    /*
     * @Description: 通过提供的对象同步对象属性
     * @param pobjDesignObj 设计对象
     * @return: void
     * @Author: Chen Jing
     * @Date: 2022-05-129 10:16:07
     */
    @Override
    public void syncInfoFromSource(IObject sourceObj, boolean updateOrNot) throws Exception {
        if (sourceObj != null) {
            IObjectCollection realizedDefinitionFromDesignObj = CIMContext.Instance.ProcessCache().item(sourceObj.ClassDefinitionUID(), domainInfo.SCHEMA.toString()).toInterface(IClassDef.class).getRealizedInterfaceDefs();
            if (realizedDefinitionFromDesignObj != null && realizedDefinitionFromDesignObj.hasValue()) {
                if (updateOrNot)
                    this.BeginUpdate();
                Iterator<IObject> iterator = realizedDefinitionFromDesignObj.GetEnumerator();
                while (iterator.hasNext()) {
                    IInterfaceDef interfaceDef = iterator.next().toInterface(IInterfaceDef.class);
                    if (!interfaceDef.UID().equalsIgnoreCase(interfaceDefinitionType.IObject.toString())) {
                        String interfaceDefinitionUID = interfaceDef.UID();
                        IInterface stepAnInterface = this.Interfaces().item(interfaceDefinitionUID, true);
                        if (stepAnInterface != null) {
                            IInterface objectAnInterface = sourceObj.Interfaces().get(interfaceDefinitionUID);
                            if (objectAnInterface != null) {
                                Iterator<Map.Entry<String, IProperty>> e = objectAnInterface.Properties().GetEnumerator();
                                while (e.hasNext()) {
                                    IProperty property = e.next().getValue();
                                    if (!propertiesNotSyncFromComponent().contains(property.getPropertyDefinitionUid())) {
                                        Object value = property.Value();
                                        stepAnInterface.Properties().item(property.getPropertyDefinitionUid(), true).setValue(value);
                                    }
                                }
                            }
                        }
                    }
                }
                if (updateOrNot)
                    this.FinishUpdate();
            }
        }
    }

    @Override
    public String WSTPProcessPhase() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSTPProcessPhase.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setWSTPProcessPhase(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSTPProcessPhase.toString(), true).setValue(value);
    }


    @Override
    public String WSWPProcessPhase() {
        IProperty property = this.getProperty(interfaceDefinitionType.IWorkStep.toString(), propertyDefinitionType.WSWPProcessPhase.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setWSWPProcessPhase(String wswpProcessPhase) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IWorkStep.toString(), true).Properties().item(propertyDefinitionType.WSWPProcessPhase.toString(), true).setValue(wswpProcessPhase);
    }

    /*
     * @Description: 标记步骤为升版删除状态
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:30:47
     */
    @Override
    public void markAsDeleteWhenRevise() throws Exception {
        this.setWorkStepStatus(workStepStatus.EN_RevisedDelete, false);
    }

    /*
     * @Description:标记步骤为升版遗留状态
     * @param null
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:30:25
     */
    @Override
    public void markAsBequeathWhenRevise() throws Exception {
        this.setWorkStepStatus(workStepStatus.EN_RevisedReserve, false);
    }

    /*
     * @param targetPhase
     * @Description: 移动步骤到指定阶段
     * @Return: void
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:29:34
     */
    @Override
    public void moveToSpecifiedPhase(String targetPhase) throws Exception {
        if (!StringUtils.isEmpty(targetPhase)) {
            this.setWSTPProcessPhase(targetPhase);
        }
    }

    /*
     * @param null
     * @Description: 获取步骤使用的ROP步骤模板的规则JSON
     * @Return: com.alibaba.fastjson.JSONObject
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:29:06
     */
    @Override
    public JSONObject getROPWorkStepRuleJSON() {
        String ropRule = this.ROPRule();
        return !StringUtils.isEmpty(ropRule) ? JSONObject.parseObject(ropRule) : null;
    }

    /*
     * @param null
     * @Description: 是否为正常生成步骤
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:28:39
     */
    @Override
    public boolean isNormal() {
        return this.toInterface(IROPWorkStep.class).isNormalGenerated();
    }

    /*
     * @param null
     * @Description: 判断步骤是否为强制生成步骤
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:28:21
     */
    @Override
    public boolean isForce() {
        return this.toInterface(IROPWorkStep.class).isForceGenerated();
    }

    /*
     * @param null
     * @Description: 判断步骤是否为升版遗留步骤
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:28:02
     */
    @Override
    public boolean isLegacy() {
        return this.toInterface(IROPWorkStep.class).isReserveGenerated();
    }

    /*
     * @param null
     * @Description: 判断步骤是否可以被删除
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:27:46
     */
    @Override
    public boolean isDeletable() {
        return !workStepStatus.EN_RevisedTempProcess.toString().equalsIgnoreCase(this.WSStatus());
    }

    /*
     * @Description: 判断步骤的所处阶段是否已经完成
     * @Return: boolean
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:26:35
     */
    @Override
    public boolean isMyPhaseAlreadyCompleted() throws Exception {
        IObject designObject = this.getDesignObject();
        if (designObject == null) throw new Exception("未找到步骤关联的设计对象信息!");
        IROPExecutableItem executableItem = designObject.toInterface(IROPExecutableItem.class);
        Map<IEnumEnum, List<IObject>> workStepsGroupByPhase = executableItem.getWorkStepsGroupByROPPhase();
        if (CommonUtility.hasValue(workStepsGroupByPhase)) {
            for (Map.Entry<IEnumEnum, List<IObject>> entry : workStepsGroupByPhase.entrySet()) {
                List<IObject> lcolWorkSteps = entry.getValue();
                if (lcolWorkSteps.stream().anyMatch(r -> this.OBID().equalsIgnoreCase(r.OBID()))) {
                    IObject lobjStepAllowInd = lcolWorkSteps.stream().filter(r -> r.toInterface(IROPWorkStep.class).ROPWorkStepAllowInd()).findFirst().orElse(null);
                    return lobjStepAllowInd != null && lobjStepAllowInd.toInterface(IWorkStep.class).hasActualCompletedDate();
                }
            }
        }
        return false;
    }

    /*
     * @param null
     * @Description: 获取工作步骤所属的设计对象
     * @Return:
     * @Author: Chen Jing
     * @Date: 2022/5/12 19:27:11
     */
    @Override
    public IObject getDesignObject() throws Exception {
        return this.GetEnd2Relationships().GetRel(relDefinitionType.CCMDesignObj2WorkStep.toString(), false).GetEnd1();
    }

    /**
     * 是否为图纸/ROP升版删除状态
     *
     * @return
     * @throws Exception
     */
    @Override
    public boolean isDeleteStatus() throws Exception {
        return WSStatus().equalsIgnoreCase(workStepStatus.EN_RevisedDelete.name()) || WSStatus().equalsIgnoreCase(workStepStatus.EN_ROPDelete.name());
    }

    /**
     * true-图纸升版删除;false-ROP升版删除;
     *
     * @return
     * @throws Exception 非删除状态
     */
    @Override
    public boolean isROPDelete() throws Exception {
        if (!isDeleteStatus()) {
            throw new Exception("非删除状态工作步骤,判断删除类型失败!");
        }
        return WSStatus().equalsIgnoreCase(workStepStatus.EN_ROPDelete.name());
    }
}
