package ccm.server.business.impl;

import ccm.server.business.ICCMSchedulePolicyService;
import ccm.server.business.ICCMScheduleService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.operator;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.ICCMBasicTargetObj;
import ccm.server.schema.interfaces.ICCMPriorityItem;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.utils.SchemaUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/5 9:58
 */
@Service
public class CCMSchedulePolicyServiceImpl implements ICCMSchedulePolicyService {

    @Autowired
    private ICCMScheduleService scheduleService;

    /**
     * 创建计划策略条件
     *
     * @param scheduleOBID
     * @param policyItem
     * @throws Exception
     */
    @Override
    public void createSchedulePolicyItem(String scheduleOBID, ObjectDTO policyItem) throws Exception {
        IObject scheduleByOBID = scheduleService.getScheduleByOBID(scheduleOBID);
        SchemaUtility.beginTransaction();
        IObject ccmPolicyItem = createPolicyItem(policyItem);
        SchemaUtility.createRelationShip("CCMSchedule2SchedulePolicyItem", scheduleByOBID, ccmPolicyItem, false);
        SchemaUtility.commitTransaction();
    }

    /**
     * 批量创建计划策略条件
     *
     * @param scheduleOBID
     * @param policyItems
     * @throws Exception
     */
    @Override
    public void createSchedulePolicyItems(String scheduleOBID, List<ObjectDTO> policyItems) throws Exception {
        IObject scheduleByOBID = scheduleService.getScheduleByOBID(scheduleOBID);
        SchemaUtility.beginTransaction();
        for (ObjectDTO policyItem : policyItems) {
            IObject ccmPolicyItem = createPolicyItem(policyItem);
            SchemaUtility.createRelationShip("CCMSchedule2SchedulePolicyItem", scheduleByOBID, ccmPolicyItem, false);
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 删除计划策略条件
     *
     * @param scheduleOBID    计划OBID
     * @param policyItemOBIDs 策略OBID集合
     * @throws Exception
     */
    @Override
    public void deleteSchedulePolicyItems(String scheduleOBID, String policyItemOBIDs) throws Exception {
        IQueryEngine toDeletedRelEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = toDeletedRelEngine.start();
        toDeletedRelEngine.setQueryForRelationship(queryRequest, true);
        toDeletedRelEngine.addRelDefUidForQuery(queryRequest, operator.equal, "CCMSchedule2SchedulePolicyItem");
        toDeletedRelEngine.addRelOrEdgeDefForQuery(queryRequest, "", "", propertyDefinitionType.OBID1.name(), operator.equal, scheduleOBID);
        toDeletedRelEngine.addRelOrEdgeDefForQuery(queryRequest, "", "", propertyDefinitionType.OBID2.name(), operator.in, policyItemOBIDs);
        IObjectCollection relCollection = toDeletedRelEngine.query(queryRequest);
        Iterator<IObject> relIter = relCollection.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (relIter.hasNext()) {
            IObject relObj = relIter.next();
            IRel iRel = relObj.toInterface(IRel.class);
            IObject policyItemObj = iRel.GetEnd2();
            // 删除关联关系和策略条件对象
            iRel.Delete();
            policyItemObj.Delete();
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 新建计划策略条件
     *
     * @param policyItem
     * @return
     * @throws Exception
     */
    private IObject createPolicyItem(ObjectDTO policyItem) throws Exception {
        IObject ccmSchedulePolicyItem = SchemaUtility.newIObject("CCMSchedulePolicyItem", policyItem.getName(), policyItem.getDescription(), null, null);
        if (null != ccmSchedulePolicyItem) {
            List<ObjectItemDTO> items = policyItem.getItems();
            for (ObjectItemDTO item : items) {
                ccmSchedulePolicyItem.setValue(item.getDefUID(), item.getDisplayValue());
            }
            ccmSchedulePolicyItem.ClassDefinition().FinishCreate(ccmSchedulePolicyItem);
        }
        return ccmSchedulePolicyItem;
    }

    /**
     * 根据计划策略条件归集数据
     * <p>
     * 根据计划策略条件查询对应设计数据并且和计划创建关联关系
     * </p>
     *
     * @param scheduleOBID 计划OBID
     * @throws Exception
     */
    @Override
    public void dataCollection(String scheduleOBID) throws Exception {
        IObject scheduleByOBID = scheduleService.getScheduleByOBID(scheduleOBID);

        IRelCollection ccmSchedule2Document = scheduleByOBID.GetEnd1Relationships().GetRels("CCMSchedule2Document");
        List<String> relatedDocuments = new ArrayList<>();
        if (null != ccmSchedule2Document && ccmSchedule2Document.hasValue()) {
            relatedDocuments.addAll(ccmSchedule2Document.GetEnd2s().listOfOBID());
        }

        List<String> toDeletedDocuments = new ArrayList<>(relatedDocuments);
        // 获取计划条件
        IRelCollection ccmSchedule2SchedulePolicyItem = scheduleByOBID.GetEnd1Relationships().GetRels("CCMSchedule2SchedulePolicyItem");
        if (null != ccmSchedule2SchedulePolicyItem && ccmSchedule2SchedulePolicyItem.hasValue()) {
            IObjectCollection schedulePolicyItemCollection = ccmSchedule2SchedulePolicyItem.GetEnd2s();
            Iterator<IObject> schedulePolicyItemIter = schedulePolicyItemCollection.GetEnumerator();
            // 获取设计数据
            IQueryEngine documentEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = documentEngine.start();
            documentEngine.addClassDefForQuery(queryRequest, classDefinitionType.CIMDocumentMaster.name());

            while (schedulePolicyItemIter.hasNext()) {
                IObject schedulePolicyItemObj = schedulePolicyItemIter.next();
                ICCMBasicTargetObj iccmBasicTargetObj = schedulePolicyItemObj.toInterface(ICCMBasicTargetObj.class);
                String targetProperty = iccmBasicTargetObj.getTargetProperty();
                String targetValue = iccmBasicTargetObj.getTargetValue();
                ICCMPriorityItem iccmPriorityItem = schedulePolicyItemObj.toInterface(ICCMPriorityItem.class);
                String operatorStr = iccmPriorityItem.Operator();
                operator operator = convertToOperation(operatorStr);
                // 填充条件
                documentEngine.addPropertyForQuery(queryRequest,
                        "", targetProperty, operator, targetValue);
            }
            IObjectCollection documentCollection = documentEngine.query(queryRequest);
            List<String> toRelateDocuments = documentCollection.listOfOBID();
            toDeletedDocuments.removeAll(toRelateDocuments);

            SchemaUtility.beginTransaction();
            // 删除不需要的关联关系
            deleteRelatedDocument(scheduleOBID, toDeletedDocuments);
            // 创建新的关联关系
            relateDocument(scheduleByOBID, documentCollection, relatedDocuments);
            SchemaUtility.commitTransaction();
        } else {
            if (relatedDocuments.size() > 0) {
                SchemaUtility.beginTransaction();
                // 删除不需要的关联关系
                deleteRelatedDocument(scheduleOBID, relatedDocuments);
                SchemaUtility.commitTransaction();
            }
        }
    }

    /**
     * 删除新过滤条件下不需要的已关联的关联关系
     *
     * @param scheduleOBID       计划OBID
     * @param toDeletedDocuments 需要解除关联关系的图纸OBID的List
     * @throws Exception
     */
    private void deleteRelatedDocument(String scheduleOBID, List<String> toDeletedDocuments) throws Exception {
        // 直接获取关联关系对象集合
        IQueryEngine toDeletedRelEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = toDeletedRelEngine.start();
        toDeletedRelEngine.setQueryForRelationship(queryRequest, true);
        toDeletedRelEngine.addRelDefUidForQuery(queryRequest, operator.equal, "CCMSchedule2Document");
        toDeletedRelEngine.addRelOrEdgeDefForQuery(queryRequest, "", "", propertyDefinitionType.OBID1.name(), operator.equal, scheduleOBID);
        toDeletedRelEngine.addRelOrEdgeDefForQuery(queryRequest, "", "", propertyDefinitionType.OBID2.name(), operator.in, String.join(",", toDeletedDocuments));
        IObjectCollection relCollection = toDeletedRelEngine.query(queryRequest);
        Iterator<IObject> relIterator = relCollection.GetEnumerator();
        while (relIterator.hasNext()) {
            IObject rel = relIterator.next();
            rel.Delete();
        }
    }

    /**
     * 删除新过滤条件下不需要的已关联的关联关系
     *
     * @param scheduleByOBID     计划对象
     * @param documentCollection 需要关联的图纸
     * @param relatedDocuments   已关联的图纸OBID集合
     * @throws Exception
     */
    private void relateDocument(IObject scheduleByOBID, IObjectCollection documentCollection, List<String> relatedDocuments) throws Exception {
        Iterator<IObject> documentIter = documentCollection.GetEnumerator();
        while (documentIter.hasNext()) {
            IObject documentObj = documentIter.next();
            if (!relatedDocuments.contains(documentObj.OBID())) {
                // 如果已关联的图纸不存在当前图纸时创建关联关系
                SchemaUtility.createRelationShip("CCMSchedule2Document", scheduleByOBID, documentObj, false);
            }
        }
    }

    /**
     * 根据枚举类型匹配条件符号
     *
     * @param operatorStr
     * @return
     */
    private operator convertToOperation(String operatorStr) {
        operator result;
        if ("EN_!=".equalsIgnoreCase(operatorStr)) {
            result = operator.notEqual;
        } else if ("EN_<".equalsIgnoreCase(operatorStr)) {
            result = operator.lessThan;
        } else if ("EN_<=".equalsIgnoreCase(operatorStr)) {
            result = operator.lessOrEqualThan;
        } else if ("EN_>".equalsIgnoreCase(operatorStr)) {
            result = operator.largeThan;
        } else if ("EN_>=".equalsIgnoreCase(operatorStr)) {
            result = operator.largeOrEqualThan;
        } else {
            result = operator.equal;
        }
        return result;
    }


}
