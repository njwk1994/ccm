package ccm.server.context;

import ccm.server.engine.IQueryEngine;
import ccm.server.enums.ExpansionMode;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.operator;
import ccm.server.executor.PriorityWeightCalculationExecutor;
import ccm.server.model.DocumentPriorityWeightVo;
import ccm.server.models.query.QueryRequest;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.IProperty;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.utils.PackagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PriorityContext {
    public static final ThreadLocal<ConcurrentHashMap<String, IObjectCollection>> priorityResultThreadLocal = new ThreadLocal<>();

    public static PriorityContext Instance;
    @Autowired
    private ThreadsProcessor threadsProcessor;

    @PostConstruct
    public void doInit() {
        Instance = this;
        Instance.threadsProcessor = this.threadsProcessor;
    }

    protected IObjectCollection filterTagsByCWA(IObjectCollection tags, IProperty cwaProperty) {
        String cwa = "";
        if (cwaProperty != null && cwaProperty.Value() != null && !StringUtils.isEmpty(cwaProperty.Value().toString()))
            cwa = cwaProperty.Value().toString();
        if (tags != null && tags.hasValue()) {
            StopWatch stopWatch = PerformanceUtility.start();
            IObjectCollection result = new ObjectCollection();
            Iterator<IObject> e = tags.GetEnumerator();
            while (e.hasNext()) {
                IObject current = e.next();
                IProperty property = current.getProperty(PackagesUtils.PROPERTY_CWA);
                if (property != null) {
                    Object value = property.Value();
                    if (value != null && value.toString().equalsIgnoreCase(property.Value().toString()))
                        result.append(current);
                } else
                    result.append(current);

            }
            log.info("filter by CWA for component(s) quantity:" + tags.size() + PerformanceUtility.stop(stopWatch));
            return result;
        }
        return tags;
    }

    protected Map<String, Map.Entry<IObject, Double>> convertToMapFromIObjectCollection(IObjectCollection tags) {
        if (tags != null && tags.hasValue()) {
            Map<String, Map.Entry<IObject, Double>> result = new HashMap<>();
            Iterator<IObject> e = tags.GetEnumerator();
            while (e.hasNext()) {
                IObject current = e.next();
                result.put(current.OBID(), new AbstractMap.SimpleEntry<>(current, null));
            }
            return result;
        }
        return null;
    }

    protected Map<IObject, IObjectCollection> splitCompObjsByDocument(IObjectCollection compObjs) throws Exception {
        Map<IObject, IObjectCollection> result = new HashMap<>();
        StopWatch stopWatch = PerformanceUtility.start();
        if (compObjs != null) {
            IRelCollection relCollection = compObjs.GetEnd2Relationships().GetRels(PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ);
            if (relCollection != null) {
                IObjectCollection documents = relCollection.GetEnd1s();
                if (relCollection.size() > 0) {
                    Map<String, IObjectCollection> collectionHashMap = new HashMap<>();
                    Iterator<IObject> e = relCollection.GetEnumerator();
                    while (e.hasNext()) {
                        IRel rel = e.next().toInterface(IRel.class);
                        String documentOBID = rel.OBID1();
                        IObject compObj = compObjs.itemByOBID(rel.OBID2());
                        if (!StringUtils.isEmpty(documentOBID) && compObj != null) {
                            IObjectCollection currentCompObjs = collectionHashMap.getOrDefault(documentOBID, new ObjectCollection());
                            currentCompObjs.append(compObj);
                            if (currentCompObjs.size() == 1)
                                collectionHashMap.put(documentOBID, currentCompObjs);
                            else
                                collectionHashMap.replace(documentOBID, currentCompObjs);
                        }
                    }

                    for (Map.Entry<String, IObjectCollection> entry : collectionHashMap.entrySet()) {
                        IObject document = documents.itemByOBID(entry.getKey());
                        result.put(document, entry.getValue());
                    }
                }
            }
        }
        log.info("split component(s) by document and quantity :" + result.size() + PerformanceUtility.stop(stopWatch));
        return result;
    }

    protected ICCMWorkPackage getWorkPackageByOBID(String pstrOBID) {
        if (!StringUtils.isEmpty(pstrOBID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, pstrOBID);
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_PACKAGE);
            IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            return object != null ? object.toInterface(ICCMWorkPackage.class) : null;
        }
        return null;
    }

    protected ICCMTaskPackage getTaskPackageByOBID(String pstrOBID) {
        if (!StringUtils.isEmpty(pstrOBID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, null, pstrOBID);
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, PackagesUtils.CCM_TASK_PACKAGE);
            IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            return object != null ? object.toInterface(ICCMTaskPackage.class) : null;
        }
        return null;
    }

    protected IObjectCollection getRelatedCompObjByWorkSteps(IObjectCollection workSteps) throws Exception {
        if (workSteps != null && workSteps.hasValue()) {
            log.info("enter to ge related comp object(s) by work steps:" + workSteps.size());
            StopWatch stopWatch = PerformanceUtility.start();
            String relDefUID = PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP;
            IObjectCollection result = workSteps.GetEnd2Relationships().GetRels(relDefUID).GetEnd1s();
            log.info("complete to get related comp object(s) by work step(s)" + PerformanceUtility.stop(stopWatch));
            return result;
        }
        return null;
    }

    private static final String CLASSDEF_WORKSTEP = PackagesUtils.CCM_WORK_STEP;

    protected IObjectCollection getWorkStepsByPurposeForWP(String purpose) throws Exception {
        log.info("enter to get work step(s) by purpose " + purpose);
        if (!StringUtils.isEmpty(purpose)) {
            StopWatch stopWatch = PerformanceUtility.start();
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addClassDefForQuery(queryRequest, CLASSDEF_WORKSTEP);
            queryEngine.addPropertyForQuery(queryRequest, ICCMWBS.class.getSimpleName(), "WPPurpose", operator.equal, PriorityCache.Instance.getPurposeForCaseOfNotEntryUID(purpose));
            IObjectCollection result = queryEngine.query(queryRequest);
            log.info("complete to get work steps by purpose for work package:" + result.size() + PerformanceUtility.stop(stopWatch));
            return result;
        }
        return null;
    }

    protected IObjectCollection getWorkStepsByPurposeWithoutAnyWorkPackage() {
        return null;

    }

    public IObjectCollection calculatePriorityForWorkPackage(String uniqueId, String workPackageId, String priorityId, boolean fromCache) throws Exception {
        if (fromCache) {
            if (!StringUtils.isEmpty(uniqueId)) {
                return cachedPriorityDetails.getOrDefault(uniqueId, null);
            } else
                throw new Exception("global unique key not provided for cache process");

        }
        if (StringUtils.isEmpty(uniqueId))
            throw new Exception("global unique key not provided for cache process");
        IObjectCollection result = this.calculatePriorityForWorkPackage(workPackageId, priorityId);
        this.keepObjectForProvidedPropertyWithValue(result, "DesignPhase", "EN_ShopDesign");
        cachedPriorityDetails.put(uniqueId, result);
        return result;
    }

    public IObjectCollection calculatePriorityForWorkPackage(String workPackageId, String priorityId) throws Exception {
        if (!StringUtils.isEmpty(workPackageId) && !StringUtils.isEmpty(priorityId)) {
            IObject priority = CIMContext.Instance.ProcessCache().getObjectByOBIDCache(priorityId);
            if (priority == null) {
                priority = PriorityCache.Instance.getPriorityByOBID(priorityId);
            }
            if (priority == null)
                throw new Exception("cannot find priority configuration with " + priorityId + ",cannot do calculation now");

            ICCMWorkPackage workPackage = this.getWorkPackageByOBID(workPackageId);
            if (workPackage == null)
                throw new Exception("cannot find work package with OBID:" + workPackageId);

            return this.calculatePriorityForWorkPackage(workPackage, priority.toInterface(ICCMPriority.class));
        }
        return new ObjectCollection();
    }

    public IObjectCollection calculatePriorityForWorkPackage(ICCMWorkPackage workPackage, ICCMPriority priority) throws Exception {
        if (workPackage != null && priority != null) {
            IObjectCollection compObjs = null;
            Map<IObject, IObjectCollection> mapByDocument = null;
            IObjectCollection taskPackages = workPackage.getTaskPackages();
            if (taskPackages != null && taskPackages.hasValue()) {
                IObjectCollection documents = taskPackages.GetEnd1Relationships().GetRels("CCMTaskPackage2Document").GetEnd2s();
                if (documents != null && documents.hasValue()) {
                    compObjs = documents.GetEnd1Relationships().GetRels(PackagesUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd2s();
                }
            } else {
                IObjectCollection steps = this.getWorkStepsByPurposeForWP(workPackage.getValue(PackagesUtils.PROPERTY_PURPOSE));
                if (steps != null && steps.hasValue()) {
                    this.removeStepsThatAlreadyLinkToWorkPackage(steps);
                    compObjs = this.getRelatedCompObjByWorkSteps(steps);
                }
            }
            IProperty cwa = workPackage.getProperty(PackagesUtils.PROPERTY_CWA);
            compObjs = this.filterTagsByCWA(compObjs, cwa);
            if (compObjs != null && compObjs.hasValue()) {
                mapByDocument = this.splitCompObjsByDocument(compObjs);
            }
            Map<IObject, Double> mapOfDocumentWeight = null;
            if (mapByDocument != null) {
                this.ensureIFCDocuments(mapByDocument);
                mapOfDocumentWeight = this.doCalculateWeightAsPerDocument(mapByDocument, priority);
            } else
                log.warn("no document(s) related to provided components");
            return this.renderCollectionWithPriority(mapOfDocumentWeight);
        }
        return new ObjectCollection();
    }

    private void removeStepsThatAlreadyLinkToWorkPackage(IObjectCollection steps) throws Exception {
        if (steps != null && steps.hasValue()) {
            StopWatch stopWatch = PerformanceUtility.start();
            IRelCollection relCollection = steps.GetEnd2Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, false);
            if (relCollection != null && relCollection.hasValue()) {
                Iterator<IObject> e = relCollection.GetEnumerator();
                while (e.hasNext()) {
                    IRel rel = e.next().toInterface(IRel.class);
                    String currentStepOBID = rel.OBID2();
                    steps.remove(currentStepOBID);
                }
            }
            log.info("remove steps linked to work package" + PerformanceUtility.stop(stopWatch));
        }
    }

    private final static Map<String, IObjectCollection> cachedPriorityDetails = new ConcurrentHashMap<>();

    public IObjectCollection calculatePriorityForTaskPackage(String uniqueId, String taskPackageId, String priorityId, boolean fromCache) throws Exception {
        if (fromCache) {
            if (!StringUtils.isEmpty(uniqueId)) {
                return cachedPriorityDetails.getOrDefault(uniqueId, null);
            } else
                throw new Exception("global unique key not provided for cache process");
        }
        if (StringUtils.isEmpty(uniqueId))
            throw new Exception("global unique key not provided for cache process");
        StopWatch stopWatch = PerformanceUtility.start();
        IObjectCollection result = this.calculatePriorityForTaskPackage(taskPackageId, priorityId);
        cachedPriorityDetails.put(uniqueId, result);
        log.info("finally priority calculation completed" + PerformanceUtility.stop(stopWatch));
        return result;
    }

    public void keepObjectForProvidedPropertyWithValue(IObjectCollection result, String propertyDef, Object value) throws Exception {
        if (result != null && result.hasValue() && !StringUtils.isEmpty(propertyDef) && value != null && !StringUtils.isEmpty(value.toString())) {
            for (String obid : result.listOfOBID()) {
                IObject current = result.itemByOBID(obid);
                if (current != null) {
                    IProperty property = current.getProperty(propertyDef);
                    if (property != null) {
                        Object currentValue = property.Value();
                        if (currentValue != null) {
                            if (currentValue.equals(value)) {
                                continue;
                            } else {
                                String value1 = CIMContext.Instance.ProcessCache().parseExpectedValue(propertyDef, value.toString());
                                if (currentValue.toString().equalsIgnoreCase(value1))
                                    continue;
                            }
                            result.remove(obid);
                        }
                    }
                }
            }
        }
    }

    public IObjectCollection calculatePriorityForTaskPackage(String taskPackageId, String priorityId) throws Exception {
        if (!StringUtils.isEmpty(taskPackageId) && !StringUtils.isEmpty(priorityId)) {
            IObject priority = PriorityCache.Instance.getObjectByOBIDCache(priorityId);
            if (priority == null)
                throw new Exception("cannot find priority configuration with " + priorityId + ",cannot do calculation now");

            ICCMTaskPackage taskPackage = this.getTaskPackageByOBID(taskPackageId);
            if (taskPackage == null)
                throw new Exception("cannot find task package with OBID:" + taskPackageId);
            return this.calculatePriorityForTaskPackage(taskPackage, priority.toInterface(ICCMPriority.class));
        }
        return new ObjectCollection();
    }

    private final static String PROPERTY_CIMDocState = "CIMDocState";
    //Issued For Construction,IFC,EN_IFC

    private List<String> ifcInfo() {
        return new ArrayList<String>() {{
            this.add("Issued For Construction");
            this.add("IFC");
            this.add("EN_IFC");
            this.add("Issued For Construction,IFC");
        }};
    }

    protected void ensureIFCDocuments(Map<IObject, IObjectCollection> mapByDocuments) {
        if (mapByDocuments != null && mapByDocuments.size() > 0) {
            StopWatch stopWatch = PerformanceUtility.start();
            ArrayList<IObject> objects = new ArrayList<>(mapByDocuments.keySet());
            for (IObject object : objects) {
                String docState = object.getValue(PROPERTY_CIMDocState);
                if (this.ifcInfo().stream().noneMatch(c -> c.equalsIgnoreCase(docState))) {
                    mapByDocuments.remove(object);
                }
            }
            log.info("remove all document(s) without ifc status and remaining quantity: " + mapByDocuments.size() + PerformanceUtility.stop(stopWatch));
        }
    }

    protected void removeDocumentsAlreadyLinkToOtherTaskPackage(Map<IObject, IObjectCollection> mapByDocuments) throws Exception {
        if (mapByDocuments != null && mapByDocuments.size() > 0) {
            StopWatch stopWatch = PerformanceUtility.start();
            IObjectCollection documents = ObjectCollection.toObjectCollection(mapByDocuments.keySet());
            IRelCollection relCollection = documents.GetEnd2Relationships().GetRels(PackagesUtils.REL_TASK_PACKAGE_2_DOCUMENT, false);
            if (relCollection != null && relCollection.hasValue()) {
                Iterator<IObject> iterator = relCollection.GetEnumerator();
                while (iterator.hasNext()) {
                    IRel r = iterator.next().toInterface(IRel.class);
                    IObject currentDocument = documents.itemByOBID(r.OBID2());
                    if (currentDocument != null)
                        mapByDocuments.remove(currentDocument);
                }
            }
            log.info("remove document(s) linked to other package and remaining quantity: " + mapByDocuments.size() + PerformanceUtility.stop(stopWatch));
        }
    }

    protected IObjectCollection getCompObjsByRelatedStepPurpose(IProperty property) throws Exception {
        if (property != null) {
            StopWatch stopWatch = PerformanceUtility.start();
            Object value = property.Value();
            List<String> classDefsForDesignObj = CIMContext.Instance.ProcessCache().getClassDefsForDesignObj();
            if (value != null && !StringUtils.isEmpty(value.toString())) {
                String purpose = value.toString();
                QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
                CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, String.join(",", classDefsForDesignObj));
                CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, "+" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, interfaceDefinitionType.ICustomObject.toString(), property.getPropertyDefinitionUid(), operator.equal, purpose, ExpansionMode.relatedObject);
                IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
                log.info("query for component(s) with provided purpose:" + purpose + ",quantity is:" + query.size() + PerformanceUtility.stop(stopWatch));
                return query;
            }
        }
        return null;
    }

    protected IObjectCollection getCompObjsByRelatedStepPurposeWithoutAnyWorkPackage(IProperty purposeProperty) throws Exception {
        if (purposeProperty != null) {
            StopWatch stopWatch = PerformanceUtility.start();
            Object value = purposeProperty.Value();
            List<String> classDefsForDesignObj = CIMContext.Instance.ProcessCache().getClassDefsForDesignObj();
            if (value != null && !StringUtils.isEmpty(value.toString())) {
                String lstrPurpose = value.toString();
                QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
                CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, String.join(",", classDefsForDesignObj));
                CIMContext.Instance.QueryEngine().addRelOrEdgeDefForQuery(queryRequest, "+" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, interfaceDefinitionType.ICustomObject.toString(), purposeProperty.getPropertyDefinitionUid(), operator.equal, lstrPurpose, ExpansionMode.relatedObject);
                IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
                log.info("query for component(s) with provided purpose:" + lstrPurpose + ",quantity is:" + query.size() + PerformanceUtility.stop(stopWatch));
                return query;
            }
        }
        return null;
    }

    public IObjectCollection calculatePriorityForTaskPackage(ICCMTaskPackage taskPackage, ICCMPriority priority) throws Exception {
        if (taskPackage != null) {
            IObjectCollection compObjs = this.getCompObjsByRelatedStepPurpose(taskPackage.getProperty(PackagesUtils.PROPERTY_PURPOSE));
            IProperty cwa = taskPackage.getProperty(PackagesUtils.PROPERTY_CWA);
            compObjs = this.filterTagsByCWA(compObjs, cwa);
            if (compObjs != null && compObjs.hasValue()) {
                IObjectCollection relatedDocuments = taskPackage.getDocuments();
                Map<IObject, IObjectCollection> mapByDocument = this.splitCompObjsByDocument(compObjs);
                if (mapByDocument != null && mapByDocument.size() > 0) {
                    if (relatedDocuments != null && relatedDocuments.hasValue()) {
                        Iterator<IObject> objectIterator = relatedDocuments.GetEnumerator();
                        while (objectIterator.hasNext()) {
                            IObject document = objectIterator.next();
                            mapByDocument.keySet().stream().filter(c -> c.OBID().equalsIgnoreCase(document.OBID())).findFirst().ifPresent(mapByDocument::remove);
                        }
                    }
                    this.ensureIFCDocuments(mapByDocument);
                    this.removeDocumentsAlreadyLinkToOtherTaskPackage(mapByDocument);
                    Map<IObject, Double> mapDocumentWeight = this.doCalculateWeightAsPerDocument(mapByDocument, priority);
                    return this.renderCollectionWithPriority(mapDocumentWeight);
                }
            }
        }
        return new ObjectCollection();
    }

    protected Map<IObject, Double> doCalculateWeightAsPerDocument(Map<IObject, IObjectCollection> mapByDocument, ICCMPriority priority) throws Exception {
        Map<IObject, Double> mapOfDocumentWeight = new HashMap<>();
        if (mapByDocument != null && priority != null) {
            log.trace("enter to calculate weight for document" + mapByDocument.size() + " with priority:" + priority.toErrorPop());
            priority.getPriorityItemPerTargetProperty();
            StopWatch stopWatch = PerformanceUtility.start();
            List<Callable<DocumentPriorityWeightVo>> executors = new ArrayList<>();
            for (Map.Entry<IObject, IObjectCollection> collectionEntry : mapByDocument.entrySet()) {
                executors.add(new PriorityWeightCalculationExecutor(collectionEntry.getKey(), collectionEntry.getValue(), priority));
            }
            List<DocumentPriorityWeightVo> weightVos = this.threadsProcessor.execute(executors);
            if (CommonUtility.hasValue(weightVos)) {
                for (DocumentPriorityWeightVo weightVo : weightVos) {
                    mapOfDocumentWeight.put(weightVo.getDocument(), weightVo.getWeight());
                }
            }
            log.info("complete to do calculation and final status: " + mapOfDocumentWeight.size() + PerformanceUtility.stop(stopWatch));
        }
        return mapOfDocumentWeight;
    }

    protected IObjectCollection renderCollectionWithPriority(Map<IObject, Double> mapOfDocumentWeight) {
        IObjectCollection result = new ObjectCollection();
        StopWatch stopWatch = PerformanceUtility.start();
        if (mapOfDocumentWeight != null) {
            for (Map.Entry<IObject, Double> doubleEntry : mapOfDocumentWeight.entrySet()) {
                IInterface dynInterface = doubleEntry.getKey().Interfaces().addDynInterface("ICCMDynDocument");
                if (dynInterface != null) {
                    dynInterface.Properties().addDynamical("DynWeight", doubleEntry.getValue());
                }
                result.append(doubleEntry.getKey());
            }
        }
        log.info("render collection for priority" + PerformanceUtility.stop(stopWatch));
        return result;
    }

    public String generateCachedKey(String... items) {
        if (items != null && items.length > 0) {
            return String.join(",", items);
        }
        return UUID.randomUUID().toString();
    }
}
