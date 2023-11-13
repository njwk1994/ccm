package ccm.server.business.impl;

import ccm.server.business.ICCMPriorityService;
import ccm.server.business.ICCMTaskPackageService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.PriorityItemDTO;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.operator;
import ccm.server.enums.propertyValueType;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMForm;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/25 8:57
 */
@Service
@Slf4j
public class CCMPriorityServiceImpl implements ICCMPriorityService {

    private static final String PROPERTYDEF_CLASSDEF_DOCUMENT = DocumentUtils.I_DOCUMENT;
    private static final String PROPERTYDEF_CLASSDEF_COMPONENT = DataRetrieveUtils.I_COMPONENT;
    private static final String CLASSDEF_TASKPACKAGE = PackagesUtils.CCM_WORK_PACKAGE;
    private static final String PROPERTYDEF_TARGETPROPERTY = BasicTargetObjUtils.PROPERTY_TARGET_PROPERTY;
    private static final String PROPERTYDEF_OPERATOR = PriorityUtils.PRIORITY_ITEM_PROPERTY_OPERATOR;
    private static final String PROPERTYDEF_TARGETVALUE = BasicTargetObjUtils.PROPERTY_TARGET_VALUE;
    private static final String PROPERTYDEF_WEIGHT = PriorityUtils.PRIORITY_ITEM_PROPERTY_PRIORITY_WEIGHT;
    private static final String RELDEF_DOCUMENT_COMPONENT = DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ;
    private static final String RELDEF_TASKPACKAGE_DOCUMENT = PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT;


    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    @Autowired
    private ICCMTaskPackageService taskPackageService;

    @Override
    public List<OptionItemDTO> getPropertiesForPriorityItem() throws Exception {
        /*List<OptionItemDTO> result = new ArrayList<>();
        entityPackageCollection componentProperties = this.fdnBusinessService.getBaseQueryWrapper().expandRelationships(false, CommonUtility.generateBaseObjectDTO("", PROPERTYDEF_CLASSDEF_COMPONENT, classDefinitionType.classDef.toString()), relDefinitionType.exposes.toString());
        if (componentProperties != null && CommonUtility.hasValue(componentProperties.getEndObjs()))
            result.addAll(OptionItemDTO.toOptionItemsByObjectDTO(componentProperties.toObjectDTOs()));

        entityPackageCollection documentProperties = this.fdnBusinessService.getBaseQueryWrapper().expandRelationships(false, CommonUtility.generateBaseObjectDTO("", PROPERTYDEF_CLASSDEF_DOCUMENT, classDefinitionType.classDef.toString()), relDefinitionType.exposes.toString());
        if (documentProperties != null && CommonUtility.hasValue(documentProperties.getEndObjs()))
            result.addAll(OptionItemDTO.toOptionItemsByObjectDTO(documentProperties.toObjectDTOs()));
        result = result.stream().distinct().collect(Collectors.toList());
        return result;*/
        return null;
    }

    /**
     * 获取枚举中的Operator
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<OptionItemDTO> getOperators() throws Exception {
        IObjectCollection enumListTypeAndEnums = CIMContext.Instance.ProcessCache().getEnumListTypeAndEnums();
        Iterator<IObject> iObjectIterator = enumListTypeAndEnums.GetEnumerator();
        while (iObjectIterator.hasNext()) {
            IObject next = iObjectIterator.next();
            if (next.Name().equals(PROPERTYDEF_OPERATOR)) {

            }
        }
        return null;
    }

    @Override
    public ObjectDTO getPriorityForm(String formPurpose) throws Exception {
        ICIMForm form = schemaBusinessService.getForm(formPurpose, PriorityUtils.CCM_PRIORITY);
        return form.generatePopup(formPurpose);
    }

    @Override
    public ObjectDTO getPriorityItemForm(String formPurpose) throws Exception {
        /*ObjectDTO priorityItemForm = this.fdnBusinessService.getCreateOrUpdateWrapper().getForm("CCMPriorityItem", formPurpose, existObj);
        priorityItemForm.remove(CommonUtility.HARDCODE_PROPERTY_OBJECT_NAME);
        priorityItemForm.remove(CommonUtility.HARDCODE_PROPERTY_OBJECT_DESCRIPTION);

        priorityItemForm.toSetOptions(PROPERTYDEF_TARGETPROPERTY, this.getPropertiesForPriorityItem());
        priorityItemForm.toSetPropertyValueType(PROPERTYDEF_TARGETPROPERTY, propertyValueType.EnumList);
        return priorityItemForm;*/
        return null;
    }

    @Override
    public void createPriority(ObjectDTO toCreatePriority) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newPriority = SchemaUtility.newIObject(PriorityUtils.CCM_PRIORITY,
            toCreatePriority.getName(),
            toCreatePriority.getDescription(),
            "", "");
        /*InterfaceDefUtility.addInterface(newPriority,PriorityUtils.I_PRIORITY);*/
        for (ObjectItemDTO item : toCreatePriority.getItems()) {
            newPriority.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newPriority.ClassDefinition().FinishCreate(newPriority);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    private String getPriorityItemDisplay(ObjectDTO priorityItem) {
        if (priorityItem != null) {
            String targetProperty = priorityItem.toGetValue(PROPERTYDEF_TARGETPROPERTY);
            String operator = priorityItem.toGetValue(PROPERTYDEF_OPERATOR);
            String targetValue = priorityItem.toGetValue(PROPERTYDEF_TARGETVALUE);
            String weight = priorityItem.toGetValue(PROPERTYDEF_WEIGHT);
            return targetProperty + operator + targetValue + "(" + weight + ")";
        }
        return "";
    }

    @Override
    public void createPriorityItem(String priorityId, ObjectDTO toCreatePriorityItemDTO) throws Exception {
        if (toCreatePriorityItemDTO != null) {
            IObject priorityByOBID = getPriorityByOBID(priorityId);
            // 开启事务
            if (!CIMContext.Instance.Transaction().inTransaction()) {
                CIMContext.Instance.Transaction().start();
            }
            IObject newPriorityItem = SchemaUtility.newIObject(PriorityUtils.CCM_PRIORITY_ITEM,
                toCreatePriorityItemDTO.getName(),
                toCreatePriorityItemDTO.getDescription(),
                "", "");
            /*InterfaceDefUtility.addInterface(newPriorityItem,
                    PriorityUtils.I_PRIORITY_ITEM,
                    BasicTargetObjUtils.I_BASIC_TARGET_OBJ);*/
            for (ObjectItemDTO item : toCreatePriorityItemDTO.getItems()) {
                newPriorityItem.setValue(item.getDefUID(), item.getDisplayValue());
            }
            // 结束创建 并创建关联关系
            newPriorityItem.ClassDefinition().FinishCreate(newPriorityItem);
            SchemaUtility.createRelationShip(PriorityUtils.REL_PRIORITY_2_PRIORITY_ITEM, priorityByOBID, newPriorityItem, false);
            // 提交事务
            CIMContext.Instance.Transaction().commit();
        } else
            throw new Exception("invalid Priority item DTO as it is NULL");
    }

    @Override
    public IObjectCollection getPriorities(PageRequest pageRequest) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, PriorityUtils.I_PRIORITY);
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public IObject getPriorityByOBID(String priorityId) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, PriorityUtils.I_PRIORITY);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, priorityId);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public IObjectCollection getPriorityItems(String priorityOBID) throws Exception {
        IObject priorityByOBID = getPriorityByOBID(priorityOBID);
        return priorityByOBID.GetEnd1Relationships().GetRels(PriorityUtils.REL_PRIORITY_2_PRIORITY_ITEM).GetEnd2s();
    }

    @Override
    public IObject getPriorityItemByOBID(String priorityItemId) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, PriorityUtils.I_PRIORITY_ITEM);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, priorityItemId);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public void deletePriority(String priorityId) throws Exception {
        // 获取已存在的设计数据
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, PriorityUtils.I_PRIORITY);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, priorityId);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        iObject.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public void deletePriorityItem(String priorityItemOBID) throws Exception {
        // 获取已存在的设计数据
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, PriorityUtils.I_PRIORITY_ITEM);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, priorityItemOBID);
        IObject iObject = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        iObject.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    private void isValidPriority(ObjectDTO objectDTO) throws Exception {
        if (objectDTO != null) {
            if (StringUtils.isEmpty(objectDTO.getName()) || StringUtils.isEmpty(objectDTO.getDescription()))
                throw new Exception("Name or Description is Null, you have to fill these field first");
        } else
            throw new Exception("Object DTO is null, cannot identify");
    }

    @Override
    public void updatePriority(ObjectDTO toUpdatePriorityDTO) throws Exception {
        // 获取已存在的设计类型
        IObject existWorkPackage = getPriorityByOBID(toUpdatePriorityDTO.getObid());
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        for (ObjectItemDTO item : toUpdatePriorityDTO.getItems()) {
            existWorkPackage.setValue(item.getDefUID(), item.toValue());
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public void updatePriorityItem(ObjectDTO toUpdatePriorityItem) throws Exception {
        // 获取已存在的设计类型
        IObject existPriorityItem = getPriorityItemByOBID(toUpdatePriorityItem.getObid());
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        for (ObjectItemDTO item : toUpdatePriorityItem.getItems()) {
            existPriorityItem.setValue(item.getDefUID(), item.toValue());
        }
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public boolean startToExecutePriority(String priorityId) throws Exception {
        if (priorityId != null) {
            IObject priorityByOBID = getPriorityByOBID(priorityId);
            return true;
        }
        throw new Exception("invalid priority during parsing progress as Id or Name not found in database");
    }

    private IObjectCollection getTaskPackageRelatedDocuments(String taskPackageId) throws Exception {
        if (!StringUtils.isEmpty(taskPackageId)) {
            return taskPackageService.getRelatedDocuments(taskPackageId);
        }
        return null;
    }

    @Override
    public List<ObjectDTO> executePriority(String taskPackageId, String priorityId) throws Exception {
        if (priorityId != null) {
            IObjectCollection taskPackageRelatedDocuments = this.getTaskPackageRelatedDocuments(taskPackageId);
            List<ObjectDTO> existRelatedDocuments = ObjectDTOUtility.convertToObjectDTOList(taskPackageRelatedDocuments);
            IObjectCollection priorityItemsUnderPriority = getPriorityItems(priorityId);
            List<ObjectDTO> priorityItems = ObjectDTOUtility.convertToObjectDTOList(priorityItemsUnderPriority);

            List<ObjectDTO> contentItems = this.calculateByPriorityItems(priorityItems);
            List<ObjectDTO> allDocuments = this.tryToConvertBeDocuments(contentItems);
            for (ObjectDTO c : existRelatedDocuments) {
                allDocuments.remove(c);
            }
            allDocuments.sort((o1, o2) -> {
                double o1Weight = Double.parseDouble(o1.toGetValue(PROPERTYDEF_WEIGHT));
                double o2Weight = Double.parseDouble(o2.toGetValue(PROPERTYDEF_WEIGHT));
                return Double.compare(o1Weight, o2Weight);
            });
            return allDocuments;
        }
        throw new Exception("invalid priority during parsing progress as Id or Name not found in database");
    }

    private List<ObjectDTO> calculateByPriorityItems(List<ObjectDTO> priorityItems) throws Exception {
        if (CommonUtility.hasValue(priorityItems)) {
            List<ObjectItemDTO> queryCriteria = new ArrayList<>();
            List<PriorityItemDTO> priorityItemDTOs = new ArrayList<>();
            for (ObjectDTO item : priorityItems) {
                PriorityItemDTO itemDTO = item.toDTO(PriorityItemDTO.class);
                if (itemDTO == null)
                    throw new Exception("convert object DTO to priority item DTO failed");

                priorityItemDTOs.add(itemDTO);
                queryCriteria.add(this.generateQueryCriteriaByPriorityItem(itemDTO));
            }
            queryCriteria.add(this.generateQueryCriteriaOfClassDefinitionUID());
            List<ObjectDTO> queryData = new ArrayList<>();
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DataRetrieveUtils.I_COMPONENT);
            for (ObjectItemDTO queryCriterion : queryCriteria) {
                CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, "",
                    queryCriterion.getDefUID(), operator.valueOf(queryCriterion.getOperator()), queryCriterion.getDisplayValue().toString());
            }
            IObjectCollection designDataQuery = CIMContext.Instance.QueryEngine().query(queryRequest);
            List<ObjectDTO> designData = ObjectDTOUtility.convertToObjectDTOList(designDataQuery);
            
            queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, DocumentUtils.I_DOCUMENT);
            for (ObjectItemDTO queryCriterion : queryCriteria) {
                CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, "",
                    queryCriterion.getDefUID(), operator.valueOf(queryCriterion.getOperator()), queryCriterion.getDisplayValue().toString());
            }
            IObjectCollection documentQuery = CIMContext.Instance.QueryEngine().query(queryRequest);
            List<ObjectDTO> documents = ObjectDTOUtility.convertToObjectDTOList(documentQuery);

            queryData.addAll(designData);
            queryData.addAll(documents);

            if (CommonUtility.hasValue(queryData)) {
                List<ObjectDTO> dtoList = new ArrayList<>();
                queryData = queryData.stream().distinct().collect(Collectors.toList());
                for (ObjectDTO t : queryData) {
                    double currentWeight = this.getWeightForObject(t, priorityItemDTOs);
                    ObjectItemDTO objectItemDTO = new ObjectItemDTO();
                    objectItemDTO.setDefUID(PROPERTYDEF_WEIGHT);
                    objectItemDTO.setLabel("权重：");
                    objectItemDTO.setDisplayValue(currentWeight);
                    objectItemDTO.setPropertyValueType(propertyValueType.DoubleType.toString());
                    t.toSetValue(objectItemDTO);
                    dtoList.add(t);
                }
                return dtoList;
            }
        }
        return new ArrayList<>();
    }

    private List<ObjectDTO> tryToConvertBeDocuments(List<ObjectDTO> items) throws Exception {
        List<ObjectDTO> finalResult = new ArrayList<>();
        if (CommonUtility.hasValue(items)) {
            finalResult.addAll(CommonUtility.getObjectsByClassDef(items, PROPERTYDEF_CLASSDEF_DOCUMENT));
            List<ObjectDTO> components = items.stream().filter(c -> c.getClassDefinitionUID().equalsIgnoreCase(PROPERTYDEF_CLASSDEF_COMPONENT)).collect(Collectors.toList());
            if (CommonUtility.hasValue(components)) {
                List<String> componentOBIDs = components.stream().map(ObjectDTO::getObid).distinct().collect(Collectors.toList());
                QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
                CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, PackagesUtils.I_WORK_PACKAGE);
                CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.in, String.join(",", componentOBIDs));
                IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
                IObjectCollection documentCollection = query.GetEnd1Relationships().GetRels(RELDEF_DOCUMENT_COMPONENT).GetEnd1s();
                Iterator<IObject> iObjectIterator = documentCollection.GetEnumerator();
                while (iObjectIterator.hasNext()) {
                    IObject documentObject = iObjectIterator.next();

                }
                /*for (entityPackage componentPkg : documentPackageCollection.getStartObjs()) {
                    String id = componentPkg.getObj().getId();
                    ObjectDTO component = CommonUtility.toGetObject(components, id);
                    Double componentWeight = component != null ? component.toGetDoubleValue(PROPERTYDEF_WEIGHT) : 0.0;
                    List<String> documentIds = documentPackageCollection.getRelationships().stream().filter(c -> c.getObjREL().getEnd2Id().equalsIgnoreCase(id)).map(c -> c.getObjREL().getEnd1Id()).collect(Collectors.toList());
                    List<entityPackage> documents = documentPackageCollection.getEndObjs(documentIds);
                    if (CommonUtility.hasValue(documents)) {
                        for (entityPackage t : documents
                        ) {
                            ObjectDTO existDoc = CommonUtility.toGetObject(finalResult, t.getObj().getId());
                            if (existDoc != null) {
                                if (component != null)
                                    existDoc.toSumDoubleValue(PROPERTYDEF_WEIGHT, componentWeight);
                            } else {
                                ObjectDTO addDoc = t.toObjectDTO();
                                addDoc.toAddDynamicalProperty(PROPERTYDEF_WEIGHT, infoType.property, componentWeight, "权重：", "", propertyValueType.Double.toString());
                                finalResult.add(addDoc);
                            }
                        }
                    }
                }*/
            }
        }
        return finalResult;
    }

    // private final Map<String, criteriaHintWrapper> criteriaWrappersByPriorityItem = new HashMap<>();

    /*private criteriaHintWrapper getCriteriaWrapper(PriorityItemDTO priorityItemDTO) throws Exception {
        if (priorityItemDTO != null) {
            if (this.criteriaWrappersByPriorityItem.containsKey(priorityItemDTO.getUniqueIdentity()))
                return this.criteriaWrappersByPriorityItem.get(priorityItemDTO.getUniqueIdentity());
            else {
                criteriaHintWrapper criteriaHintWrapper = new criteriaHintWrapper(this.fdnBusinessService.getBaseQueryWrapper(), priorityItemDTO.toGetTargetProperty(), priorityItemDTO.toGetOperator(), priorityItemDTO.toGetTargetValue());
                this.criteriaWrappersByPriorityItem.put(priorityItemDTO.getUniqueIdentity(), criteriaHintWrapper);
                return criteriaHintWrapper;
            }
        }
        return null;
    }*/

    private Double getWeightForObject(ObjectDTO objectDTO, List<PriorityItemDTO> priorityItemDTOS) throws Exception {
        double result = 0.0;
        /*if (objectDTO != null && CommonUtility.hasValue(priorityItemDTOS)) {
            for (PriorityItemDTO t : priorityItemDTOS
            ) {
                criteriaHintWrapper criteriaWrapper = this.getCriteriaWrapper(t);
                if (criteriaHintWrapper != null && criteriaHintWrapper.isHint(objectDTO))
                    result = result + t.toGetWeight();
            }
        }*/
        return result;
    }

    // Unused
    /*private List<ObjectDTO> calculateByPriorityItem(ObjectDTO priorityItem) throws Exception {
        if (priorityItem != null) {
            PriorityItemDTO itemDTO = priorityItem.toDTO(PriorityItemDTO.class);
            if (itemDTO == null)
                throw new Exception("convert object DTO to priority item DTO failed");

            Double weight = itemDTO.toGetWeight();
            List<ObjectItemDTO> items = new ArrayList<>();
            items.add(this.generateQueryCriteriaByPriorityItem(itemDTO));
            items.add(this.generateQueryCriteriaOfClassDefinitionUID());
            List<ObjectDTO> objectDTOS = this.fdnBusinessService.getBaseQueryWrapper().advancedQueryForDATAUseAnd(items);
            if (CommonUtility.hasValue(objectDTOS)) {
                for (ObjectDTO t : objectDTOS
                ) {
                    t.toAddDynamicalProperty(PROPERTYDEF_WEIGHT, infoType.property, weight, "权重：", "", propertyValueType.Double.toString());
                }
                return objectDTOS;
            }
        }
        return null;
    }*/

    private ObjectItemDTO generateQueryCriteriaOfClassDefinitionUID() {
        String classDefs = PROPERTYDEF_CLASSDEF_DOCUMENT + "," + PROPERTYDEF_CLASSDEF_COMPONENT;
        ObjectItemDTO criteriaItem = new ObjectItemDTO();
        criteriaItem.setDisplayValue(classDefs);
        criteriaItem.setDefUID(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID);
        criteriaItem.setDefType(classDefinitionType.PropertyType);
        return criteriaItem;
    }

    private ObjectItemDTO generateQueryCriteriaByPriorityItem(PriorityItemDTO itemDTO) {
        if (itemDTO != null) {
            String propertyDef = itemDTO.toGetTargetProperty();
            String operator = itemDTO.toGetOperator();
            String value = itemDTO.toGetTargetValue();

            ObjectItemDTO criteria = new ObjectItemDTO();
            criteria.setOperator(operator);
            criteria.setDefType(classDefinitionType.PropertyType);
            criteria.setDefUID(propertyDef);
            criteria.setDisplayValue(value);
            return criteria;
        }
        return null;
    }
}
