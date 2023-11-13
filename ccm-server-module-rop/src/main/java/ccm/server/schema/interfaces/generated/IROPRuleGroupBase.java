package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.enums.ropRevState;
import ccm.server.module.context.ROPCache;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.util.CommonUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public abstract class IROPRuleGroupBase extends InterfaceDefault implements IROPRuleGroup {
    public IROPRuleGroupBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.IROPRuleGroup.toString(), instantiateRequiredProperties);
    }

    @Override
    public String ROPGroupClassDefinitionUID() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupClassDefinitionUID
                .toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPGroupClassDefinitionUID(String value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPGroupClassDefinitionUID.toString(), true).setValue(value);
    }

    @Override
    public void setROPGroupItemsHasUpdated(boolean hasUpdated) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPGroupItemsHasUpdated.toString(), true).setValue(hasUpdated);
    }

    @Override
    public boolean ROPGroupItemsHasUpdated() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupItemsHasUpdated.toString());
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setROPGroupWorkStepHasUpdated(boolean workStepHasUpdated) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPGroupWorkStepHasUpdated.toString(), true).setValue(workStepHasUpdated);
    }

    @Override
    public boolean ROPGroupWorkStepHasUpdated() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupWorkStepHasUpdated.toString());
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setROPHasHandleChange(boolean hasHandleChange) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPHasHandleChange.toString(), true).setValue(hasHandleChange);
    }

    @Override
    public boolean ROPHasHandleChange() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPHasHandleChange.toString());
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setROPGroupWorkStepRevState(String ropGroupWorkStepRevState) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPGroupWorkStepRevState.toString(), true).setValue(ropGroupWorkStepRevState);
    }

    @Override
    public String ROPGroupWorkStepRevState() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupWorkStepRevState.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPGroupItemRevState(String ropGroupRevState) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPGroupItemRevState.toString(), true).setValue(ropGroupRevState);
    }

    @Override
    public String ROPGroupItemRevState() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupItemRevState.toString());
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setROPGroupOrder(int value) throws Exception {
        this.Interfaces().item(interfaceDefinitionType.IROPRuleGroup.toString(), true).Properties().item(propertyDefinitionType.ROPGroupOrder.toString(), true).setValue(value);
    }

    @Override
    public Integer ROPGroupOrder() {
        IProperty property = this.getProperty(interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPGroupOrder.toString());
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public boolean isHintForProvidedObject(IObject compObj) throws Exception {
        boolean result = false;
        if (compObj != null) {
            IObjectCollection ropRuleGroupItems = this.getItems();
            if (SchemaUtility.hasValue(ropRuleGroupItems)) {
                result = true;
                Iterator<IObject> e = ropRuleGroupItems.GetEnumerator();
                while (e.hasNext()) {
                    IROPRuleGroupItem ruleGroupItem = e.next().toInterface(IROPRuleGroupItem.class);
                    if (ruleGroupItem == null)
                        throw new Exception("invalid rop rule group item for processing as it is null");
                    boolean flag = ruleGroupItem.isHintForProvidedObject(compObj);
                    if (!flag) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public IObjectCollection getROPWorkSteps() {
        return ROPCache.ROPWorkSteps.get(this.OBID());
    }


    //是否存在放行步骤
    @Override
    public boolean hasIssueStep() throws Exception {
        return CommonUtility.hasValue(this.getIssueSteps());
    }

    //获取所有放行步骤 Key 步骤的阶段Enum  Value 步骤对象
    @Override
    public Map<IEnumEnum, IObject> getIssueSteps() throws Exception {
        Map<IEnumEnum, List<IObject>> ropWorkStepsGroupByPhase = this.getROPWorkStepsGroupByPhase();
        if (CommonUtility.hasValue(ropWorkStepsGroupByPhase)) {
            Map<IEnumEnum, IObject> lcolResult = new HashMap<>();
            for (Map.Entry<IEnumEnum, List<IObject>> entry : ropWorkStepsGroupByPhase.entrySet()) {
                List<IObject> workSteps = entry.getValue();
                workSteps.stream().filter(r -> r.toInterface(IROPWorkStep.class).ROPWorkStepAllowInd()).findFirst().ifPresent(lobjIssueStep -> lcolResult.put(entry.getKey(), lobjIssueStep));
            }
            return lcolResult;
        }
        return null;
    }

    //按照阶段分组所有步骤
    @Override
    public Map<IEnumEnum, List<IObject>> getROPWorkStepsGroupByPhase() throws Exception {
        IObjectCollection lcolSteps = this.getROPWorkSteps();
        if (SchemaUtility.hasValue(lcolSteps)) {
            Map<IEnumEnum, List<IObject>> lcolResult = new HashMap<>();
            Iterator<IObject> e = lcolSteps.GetEnumerator();
            while (e.hasNext()) {
                IROPWorkStep workStep = e.next().toInterface(IROPWorkStep.class);
                String lstrStepPhase = workStep.ROPWorkStepTPPhase();
                IEnumEnum enumEnum = CIMContext.Instance.ProcessCache().getEnumListLevelType(propertyDefinitionType.ROPWorkStepTPPhase.toString(), lstrStepPhase);
                if (enumEnum == null) throw new Exception("未找到EnumEnum对象信息,UID:" + lstrStepPhase);
                if (this.containsByOBID(lcolResult, enumEnum)) {
                    List<IObject> keyByEnumEnum = this.getKeyByEnumEnum(lcolResult, enumEnum);
                    assert keyByEnumEnum != null;
                    keyByEnumEnum.add(enumEnum);
                } else {
                    lcolResult.put(enumEnum, new ArrayList<IObject>() {{
                        add(workStep);
                    }});
                }
            }
            return lcolResult;
        }
        return null;
    }

    private boolean containsByOBID(Map<IEnumEnum, List<IObject>> container, IEnumEnum enumEnum) {
        if (container != null && enumEnum != null) {
            return container.keySet().stream().anyMatch(r -> r.OBID().equalsIgnoreCase(enumEnum.OBID()));
        }
        return false;
    }

    private List<IObject> getKeyByEnumEnum(Map<IEnumEnum, List<IObject>> container, IEnumEnum enumEnum) {
        if (CommonUtility.hasValue(container) && enumEnum != null) {
            for (Map.Entry<IEnumEnum, List<IObject>> entry : container.entrySet()) {
                if (enumEnum.OBID().equalsIgnoreCase(entry.getKey().OBID())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public IObjectCollection getItems() {
        return ROPCache.ROPGroupItems.get(this.OBID());
    }

    @Override
    public void deleteItemsAndStep() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.ROPRuleGroup2Item.toString());
        if (SchemaUtility.hasValue(relCollection)) {
            IObjectCollection lcolItems = relCollection.GetEnd2s();
            relCollection.Delete();
            lcolItems.Delete();
        }
        IRelCollection relOfWorkSteps = this.GetEnd1Relationships().GetRels(relDefinitionType.ROPRuleGroup2ROPWorkStep.toString());
        if (SchemaUtility.hasValue(relOfWorkSteps)) {
            IObjectCollection lcolWorkSteps = relOfWorkSteps.GetEnd2s();
            relOfWorkSteps.Delete();
            lcolWorkSteps.Delete();
        }
    }

    /*
     * @Description: 设置ROP规则组的初始状态
     * @param needBeginUpdate 是否需要开启更新标识
     * @return:
     * @Author: Chen Jing
     * @Date: 2022-05-129 17:04:18
     */
    @Override
    public void setROPInitStatus(boolean needBeginUpdate) throws Exception {
        if (needBeginUpdate) this.BeginUpdate();
        this.setROPGroupItemsHasUpdated(false);
        this.setROPHasHandleChange(false);
        this.setROPGroupWorkStepHasUpdated(false);
        if (needBeginUpdate) this.FinishUpdate();
    }


    /*
     * @Description: 执行了升版同步以后设置ROP规则组状态
     * @param needBeginUpdate 是否需要开启更新标识
     * @return:
     * @Author: Chen Jing
     * @Date: 2022-05-129 17:03:26
     */
    @Override
    public void setROPRuleGroupHasHandleChangedStatus(boolean needBeginUpdate) throws Exception {
        if (needBeginUpdate) this.BeginUpdate();
        this.setROPGroupItemRevState(ropRevState.EN_None_ROPTemplate.toString());
        this.setROPGroupWorkStepRevState(ropRevState.EN_None_ROPTemplate.toString());
        this.setROPHasHandleChange(true);
        this.setROPGroupItemsHasUpdated(false);
        this.setROPGroupWorkStepHasUpdated(false);
        if (needBeginUpdate) this.FinishUpdate();
    }

    /*
     * @Description: 根据ROP步骤模板的变化设置规则组的状态属性
     * @param workStepHasChanged 步骤模板是否发生变化
     * @param needBeginUpdate  是否需要开启更新标识
     * @return: void
     * @Author: Chen Jing
     * @Date: 2022-05-129 16:09:02
     */
    @Override
    public void setROPGroupStatusByWorkStepChanged(boolean workStepHasChanged, boolean needBeginUpdate) throws Exception {
        if (needBeginUpdate) this.BeginUpdate();
        if (workStepHasChanged) {
            //Steps发生变化了  如果规则组步骤没有被更新过,则修改ROP步骤状态为更新,并且设置更新标记为已经更新过,如果已经被更新过,只要不执行刷新ROP步骤,那么状态都会保持原样
            if (!this.ROPGroupWorkStepHasUpdated()) {
                this.setROPGroupWorkStepRevState(ropRevState.EN_Updated_ROPTemplate.toString());
                this.setROPHasHandleChange(false);
                this.setROPGroupWorkStepHasUpdated(true);
            }
        } else {
            //如果没有发生变化,ROPGroup也咩有被更新过,就设置为None
            if (!this.ROPGroupWorkStepHasUpdated() && !ropRevState.EN_Created_ROPTemplate.toString().equalsIgnoreCase(this.ROPGroupWorkStepRevState())) {
                this.setROPGroupWorkStepRevState(ropRevState.EN_None_ROPTemplate.toString());
            }
        }
        if (needBeginUpdate) this.FinishUpdate();
    }

    /*
     * @Description: 根据ROP规则组条目的变化设置规则组的状态属性
     * @param itemsHasChanged 条目是否发生变化
     * @param needBeginUpdate  是否需要开启更新标识
     * @return: void
     * @Author: Chen Jing
     * @Date: 2022-05-129 16:09:02
     */
    @Override
    public void setROPGroupStatusByItemsChanged(boolean itemsHasChanged, boolean needBeginUpdate) throws Exception {
        if (needBeginUpdate) this.BeginUpdate();
        if (itemsHasChanged) {
            //Items发生变化了  如果规则组步骤没有被更新过,则修改ROP步骤状态为更新,并且设置更新标记为已经更新过,如果已经被更新过,只要不执行刷新ROP步骤,那么状态都会保持原样
            if (!this.ROPGroupItemsHasUpdated()) {
                this.setROPGroupItemRevState(ropRevState.EN_Updated_ROPTemplate.toString());
                this.setROPHasHandleChange(false);
                this.setROPGroupItemsHasUpdated(true);
            }
        } else {
            //如果没有发生变化,ROPGroup也咩有被更新过,判断之前状态是不是Created 如果是Created 说明,连着导入了相同的模板 ,则不改变状态,只有是Updated的时候才去修改为None
            if (!this.ROPGroupItemsHasUpdated() && !ropRevState.EN_Created_ROPTemplate.toString().equalsIgnoreCase(this.ROPGroupItemRevState())) {
                this.setROPGroupItemRevState(ropRevState.EN_None_ROPTemplate.toString());
            }
        }
        if (needBeginUpdate) this.FinishUpdate();
    }


}
