package ccm.server.schema.interfaces.generated;

import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.params.PageRequest;
import ccm.server.module.utils.ROPUtils;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IPropertyCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IROPCalculate;
import ccm.server.schema.interfaces.IROPCriteriaSet;
import ccm.server.schema.interfaces.IROPStepSet;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.SchemaUtility;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class IROPCalculateBase extends InterfaceDefault implements IROPCalculate {

    public IROPCalculateBase(boolean instantiateRequiredProperties) {
        super(ROPUtils.IROPCalculate_InterfaceDef, instantiateRequiredProperties);
    }

    /**
     * 生成工作步骤
     */
    @Override
    public void generateWorkStep() throws Exception {
        IObjectCollection workSteps = getAllWorkStep();
        Iterator<IObject> iteratorSteps = workSteps.GetEnumerator();
        while (iteratorSteps.hasNext()) {
            IObject ROPStep = iteratorSteps.next();
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            IObject iObject = SchemaUtility.newIObject(ROPUtils.ConstructionStep_ClassDef, "", "", domainInfo.SCHEMA.toString(), "");
            IInterface iConstructionStep = iObject.Interfaces().item(ROPUtils.IConstructionStep_InterfaceDef);
            IInterface iROPStep = ROPStep.Interfaces().item(ROPUtils.IROPStepSet_InterfaceDef);
            IPropertyCollection stepPropertys = iROPStep.Properties();
            Iterator<Map.Entry<String, IProperty>> iteratorPropertys = stepPropertys.GetEnumerator();
            while (iteratorPropertys.hasNext()) {
                IProperty propertyROPStep = iteratorPropertys.next().getValue();
                IProperty propertyConstructionStep = iConstructionStep.Properties().item(propertyROPStep.getPropertyDefinitionUid());
                propertyConstructionStep.setValue(propertyROPStep.Value());
            }
            iObject.setDescription(this.OBID());//做设计数据对象与施工步骤数据的关联
            // 结束创建
            iObject.ClassDefinition().FinishCreate(iObject);
            // 提交事务
            CIMContext.Instance.Transaction().commit();
        }
    }

    /**
     * 根据阶段名称获取工作步骤
     */
    @Override
    public IObjectCollection getWorkSteps(String stageName) throws Exception {
        IObjectCollection workSteps = getAllWorkStep();
        Iterator<IObject> iteratorSteps = workSteps.GetEnumerator();
        IObjectCollection result = new ObjectCollection();
        while (iteratorSteps.hasNext()) {
            IObject ROPStep = iteratorSteps.next();
            if (ROPStep.getProperty("ROPConstructionPurpose").toString().equals(stageName)) {
                result.append(ROPStep);
            }
        }
        return result;
    }

    /**
     * 根据权重计算阶段的计划量
     */
    @Override
    public Object calculate(String stageName) throws Exception {
        IObjectCollection workSteps = getWorkSteps(stageName);
        Iterator<IObject> iteratorSteps = workSteps.GetEnumerator();
        double result = 0;
        while (iteratorSteps.hasNext()) {
            IObject ROPStep = iteratorSteps.next();
            if (ROPStep.getProperty("ROPConstructionPurpose").toString().equals(stageName)) {
                String targetProperty = ROPStep.getProperty("ROPCalculateProperty").toString();
                double weight = Double.parseDouble(ROPStep.getProperty("ROPProgressWeight").toString());
                double value = Double.parseDouble(this.getProperty(targetProperty).toString());
                result = result + weight * value;
            }
        }
        return result;
    }

    /**
     * 阶段是否耗材
     */
    @Override
    public Boolean isConsumesMaterial(String stageName) throws Exception {
        IObjectCollection workSteps = getAllWorkStep();
        Iterator<IObject> iteratorSteps = workSteps.GetEnumerator();
        Boolean result = false;
        while (iteratorSteps.hasNext()) {
            IObject ROPStep = iteratorSteps.next();
            if (ROPStep.getProperty("ROPConstructionPurpose").toString().equals(stageName)) {
                Object consumesMaterial = ROPStep.getProperty("ROPConsumesMaterial");
                if (consumesMaterial != null) {
                    result = Boolean.parseBoolean(consumesMaterial.toString());
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获取数据类型下的所有施工步骤
     */
    @Override
    public IObjectCollection getAllWorkStep() throws Exception {
        IObject iobjectCriteria = SchemaUtility.newIObject(ROPUtils.ROPCriteriaSet_ClassDef, "", "", domainInfo.SCHEMA.toString(), "");
        IROPCriteriaSet iROPCriteriaSet = iobjectCriteria.toInterface(IROPCriteriaSet.class);
        IObjectCollection ROPCriterias = iROPCriteriaSet.getROPCriteria(new PageRequest(1, Integer.MAX_VALUE), this.OBID());
        Iterator<IObject> iteratorCriteria = ROPCriterias.GetEnumerator();
        Map<String, Integer> PointerCount = new HashMap<>();
        while (iteratorCriteria.hasNext()) {
            IObject ROPCriteria = iteratorCriteria.next();
            String targetProperty = ROPCriteria.getProperty("ROPCalculateProperty").toString();
            String propertyValue = this.getProperty(targetProperty).toString();
            String condition = ROPCriteria.getProperty("ROPCondition").toString();
            if (StringUtils.isNotBlank(propertyValue) && StringUtils.isNotBlank(condition)) {
                Boolean flag = false;
                if (condition.equals("*")) {
                    flag = true;
                } else if (condition.startsWith("(") && condition.endsWith(")")) {
                    String tempCondition1 = condition.replace("(", "").replace(")", "");
                    String[] tempCondition2 = tempCondition1.split(",");
                    Float leftCon = Float.parseFloat(tempCondition2[0]);
                    Float rightCon = Float.parseFloat(tempCondition2[1]);
                    Float con = Float.parseFloat(propertyValue);
                    if (leftCon <= con && con <= rightCon) {
                        flag = true;
                    }
                } else if (condition.startsWith("[") && condition.endsWith(")")) {
                    String tempCondition1 = condition.replace("[", "").replace(")", "");
                    String[] tempCondition2 = tempCondition1.split(",");
                    Float leftCon = Float.parseFloat(tempCondition2[0]);
                    Float rightCon = Float.parseFloat(tempCondition2[1]);
                    Float con = Float.parseFloat(propertyValue);
                    if (leftCon < con && con <= rightCon) {
                        flag = true;
                    }
                } else if (condition.startsWith("(") && condition.endsWith("]")) {
                    String tempCondition1 = condition.replace("(", "").replace("]", "");
                    String[] tempCondition2 = tempCondition1.split(",");
                    Float leftCon = Float.parseFloat(tempCondition2[0]);
                    Float rightCon = Float.parseFloat(tempCondition2[1]);
                    Float con = Float.parseFloat(propertyValue);
                    if (leftCon <= con && con < rightCon) {
                        flag = true;
                    }
                } else if (condition.startsWith("[") && condition.endsWith("]")) {
                    String tempCondition1 = condition.replace("[", "").replace("]", "");
                    String[] tempCondition2 = tempCondition1.split(",");
                    Float leftCon = Float.parseFloat(tempCondition2[0]);
                    Float rightCon = Float.parseFloat(tempCondition2[1]);
                    Float con = Float.parseFloat(propertyValue);
                    if (leftCon < con && con < rightCon) {
                        flag = true;
                    }
                } else if (condition.startsWith("{") && condition.endsWith("}")) {
                    String tempCondition1 = condition.replace("{", "").replace("}", "");
                    List<String> tempCondition2 = Arrays.stream(tempCondition1.split("|")).collect(Collectors.toList());
                    if (tempCondition2.contains(propertyValue)) {
                        flag = true;
                    }
                } else {
                    if (propertyValue.equals(condition)) {
                        flag = true;
                    }
                }
                if (flag) {
                    String groupName = ROPCriteria.getProperty("ROPGroupName").toString();
                    if (PointerCount.containsKey(groupName)) {
                        PointerCount.put(groupName, PointerCount.get(groupName) + 1);
                    } else {
                        PointerCount.put(groupName, 1);
                    }
                }
            }
        }
        List<Map.Entry<String, Integer>> list = new ArrayList(PointerCount.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue() - o1.getValue()));
        String selectGroupName = list.get(0).getKey();
        IObject iobjectStep = SchemaUtility.newIObject(ROPUtils.ROPStepSet_ClassDef, "", "", domainInfo.SCHEMA.toString(), "");
        IROPStepSet iROPStepSet = iobjectStep.toInterface(IROPStepSet.class);
        IObjectCollection ROPSteps = iROPStepSet.getROPStep(new PageRequest(1, Integer.MAX_VALUE), this.OBID());
        Iterator<IObject> iteratorSteps = ROPSteps.GetEnumerator();
        IObjectCollection result = new ObjectCollection();
        while (iteratorSteps.hasNext()) {
            IObject ROPStep = iteratorSteps.next();
            if (ROPStep.getProperty("ROPGroupName").toString().equals(selectGroupName)) {
                result.append(ROPStep);
            }
        }
        return result;
    }
}
