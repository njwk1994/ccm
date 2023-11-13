package ccm.server.business.impl;

import ccm.server.business.*;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.*;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.materials.service.IMaterialService;
import ccm.server.params.PageRequest;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.utils.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/8 7:48
 */
@Slf4j
@Service
public class CCMPackageServiceImpl implements ICCMPackageService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;
    @Autowired
    private ICCMTaskPackageService taskPackageService;
    @Autowired
    private ICCMWorkPackageService workPackageService;
    @Autowired
    private ICCMPressureTestPackageService pressureTestPackageService;
    @Autowired
    private IMaterialService materialService;

    /**
     * 获取包对应的施工数据分类
     *
     * @param packageOBID
     * @param packageClassDefinitionUID
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getPackageConstructionTypes(String packageOBID, String packageClassDefinitionUID, boolean needConsumeMaterial) throws Exception {
        String classDefinitionUID = DataRetrieveUtils.CCM_CONSTRUCTION_TYPE;
        IQueryEngine constructionEngine = CIMContext.Instance.QueryEngine();
        QueryRequest constructionQueryRequest = constructionEngine.start();
        constructionEngine.addClassDefForQuery(constructionQueryRequest, classDefinitionUID);
        IObjectCollection constructionCollection = constructionEngine.query(constructionQueryRequest);
        List<IObject> constructions = constructionCollection.toList();

        if (!packageClassDefinitionUID.equalsIgnoreCase(PackagesUtils.CCM_TASK_PACKAGE) && !packageClassDefinitionUID.equalsIgnoreCase(PackagesUtils.CCM_WORK_PACKAGE) && !packageClassDefinitionUID.equalsIgnoreCase(PackagesUtils.CCM_PRESSURE_TEST_PACKAGE)) {
            throw new RuntimeException("包类型错误!只支持任务包、工作包和试压包!packageClassDefinitionUID:" + packageClassDefinitionUID);
        }

        PageRequest pageRequest = new PageRequest(1, 1);

        Iterator<IObject> constructionIter = constructionCollection.GetEnumerator();

        log.warn("开始查询包{}下是否存在有材料消耗的数据", packageOBID);
        long startTime = System.currentTimeMillis();
        List<Callable<String>> callableList = new ArrayList<>();
        while (constructionIter.hasNext()) {
            IObject construction = constructionIter.next();
            ICCMBasicTargetObj targetObj = construction.toInterface(ICCMBasicTargetObj.class);
            String targetClassDef = targetObj.getTargetClassDef();

            Callable<String> verifyCallable = () -> verifyTask(packageOBID, construction, packageClassDefinitionUID, targetClassDef, pageRequest, needConsumeMaterial);
            // 绑定subject到任务上
            Subject subject = ThreadContext.getSubject();
            subject.associateWith(verifyCallable);
            callableList.add(verifyCallable);
        }
        List<String> execute = ThreadsProcessor.Instance.execute(callableList);
        log.warn("查询{}下所有类型材料消耗数据结束,总耗时{}ms", packageOBID, System.currentTimeMillis() - startTime);

        // 如果对应设计数据没有数据,则去除对应施工数据分类
        List<IObject> collect = constructions.stream().filter(o -> !execute.contains(o.OBID())).collect(Collectors.toList());
        ObjectCollection objectCollection = new ObjectCollection();
        objectCollection.addRange(collect);
        return objectCollection;
    }

    private String verifyTask(String packageOBID, IObject construction, String packageClassDefinitionUID, String targetClassDef, PageRequest pageRequest, boolean needConsumeMaterial) throws Exception {
        IObjectCollection designCollection = null;
        long s = System.currentTimeMillis();
        /*if ("CCMSpool".equals(targetClassDef)){
            System.out.println("");
        }*/
        log.warn("开始查询包{}下{}类型是否存在有材料消耗的数据", packageOBID, construction.Name());
        if (packageClassDefinitionUID.equalsIgnoreCase(PackagesUtils.CCM_TASK_PACKAGE)) {
            // TODO 2022.08.10 HT 验证时数据和实际查询存在出入,替换为直接查询
            designCollection = taskPackageService.verifyDesignDataByPurposeAndConsumeMaterial(packageOBID, targetClassDef, pageRequest, needConsumeMaterial);
            // designCollection = taskPackageService.getDesignDataByPurposeAndConsumeMaterial(packageOBID, targetClassDef, pageRequest);
        }
        if (packageClassDefinitionUID.equalsIgnoreCase(PackagesUtils.CCM_WORK_PACKAGE)) {
            // TODO 2022.08.10 HT 验证时数据和实际查询存在出入,替换为直接查询
            designCollection = workPackageService.verifyDesignDataByPurposeAndConsumeMaterial(packageOBID, targetClassDef, pageRequest, needConsumeMaterial);
            // designCollection = workPackageService.getDesignDataByPurposeAndConsumeMaterial(packageOBID, targetClassDef, pageRequest);
        }
        if (packageClassDefinitionUID.equalsIgnoreCase(PackagesUtils.CCM_PRESSURE_TEST_PACKAGE)) {
            FiltersParam filtersParam = new FiltersParam(new JSONArray());
            OrderByParam orderByParam = new OrderByParam(new JSONObject());
            Map<String, String> filters = filtersParam.getFilters();
            filters.put("-CCMPressureTestPackage2DesignObj", packageOBID);

            designCollection = this.schemaBusinessService.generalQuery(targetClassDef, pageRequest.getPageIndex(), pageRequest.getPageSize(), orderByParam.getOrderByWrappers(), filters);
        }
        log.warn("查询{}下{}类型是否存在有材料消耗的数据结束,耗时{}ms", packageOBID, construction.Name(), System.currentTimeMillis() - s);
        if (null == designCollection) {
            throw new RuntimeException("获取设计数据" + construction.Name() + "类型失败!packageOBID:" + packageOBID + ",packageClassDefinitionUID:" + packageClassDefinitionUID);
        }
        if (!designCollection.hasValue()) {
            // 如果对应设计数据没有数据,则去除对应施工数据分类
            return construction.OBID();
        } else {
            return "";
        }
    }

    /* ************************************************* 预测预留 start ******************************************************* */

    /**
     * 包预测预留
     *
     * @param packageType    包类型
     * @param packageId      包OBID
     * @param projectId      项目号
     * @param requestName    包名称
     * @param requestType    FR是预测,RR是预留
     * @param warehouses     仓库
     * @param drawingNumbers 图纸号
     * @param searchColumn   查询字段
     * @param searchValue    查询值
     * @param procedureType  存储过程类型
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> existAndCreateRequest(PackageTypeEnum packageType, String packageId, String projectId,
                                                     String requestName, String requestType, String warehouses, String drawingNumbers,
                                                     String searchColumn, String searchValue,
                                                     ProcedureTypeEnum procedureType) throws Exception {
        PackageProcedureEnum packageProcedureEnum = PackageProcedureEnum.switch2PackageProcedure(packageType, procedureType);
        // TODO 参数检测优化
        // checkParam(packageId, packageId, requestName, requestType, warehouses, drawingNumbers, packageProcedureEnum);
        Map<String, Object> result = new HashMap<>();
        switch (packageProcedureEnum) {
            case TP_DefaultDoc:
                result = taskPackageService.existAndCreateNewStatusRequest(projectId, requestName, requestType, searchColumn, searchValue);
                break;
            case TP_DefaultDocCC:
                result = taskPackageService.existAndCreatePartialStatusRequest(packageId, projectId, requestName, requestType, drawingNumbers, searchColumn, searchValue);
                break;
            // 暂时不支持的类型
            case TP_DefaultDocIC:
            case WP_DefaultDocIC:
                break;
            case TP_NewDoc:
            case WP_NewDoc: {
                Map<String, String> requestParams = getRequestParams(packageType, packageId, drawingNumbers, false);
                result = materialService.existAndCreatePartialStatusRequest33(projectId, requestName, requestType,
                        warehouses,
                        requestParams.get(PackageRequestUtils.DRAWING_NUMBERS),
                        requestParams.get(PackageRequestUtils.COMMODITY_CODES),
                        searchColumn, searchValue);
                break;
            }

            case TP_NewDocCC:
            case TP_NewDocIC:
            case TP_NewDocICWarehouses:
            case WP_NewDocCC:
            case WP_NewDocIC:
            case WP_NewDocICWarehouses: {
                Map<String, String> requestParams = getRequestParams(packageType, packageId, drawingNumbers, true);
                result = materialService.existAndCreatePartialStatusRequest33(projectId, requestName, requestType,
                        warehouses,
                        requestParams.get(PackageRequestUtils.DRAWING_NUMBERS),
                        requestParams.get(PackageRequestUtils.COMMODITY_CODES),
                        searchColumn, searchValue);
                break;
            }
            case WP_DefaultDoc:
                result = workPackageService.existAndCreateNewStatusRequest(projectId, requestName, requestType, searchColumn, searchValue);
                break;
            case WP_DefaultDocCC:
                result = workPackageService.existAndCreatePartialStatusRequest(packageId, projectId, requestName, requestType, drawingNumbers, searchColumn, searchValue);
                break;
            default:
                throw new RuntimeException("不能识别的存储过程类型,包类型:" + packageType.name() + ",存储过程类型:" + procedureType.name());
        }
        return result;
    }

    private void checkParam(String packageId, String projectId,
                            String requestName, String requestType, String warehouses, String drawingNumbers,
                            PackageProcedureEnum packageProcedureEnum) throws Exception {
        switch (packageProcedureEnum) {
            case TP_DefaultDoc:
            case WP_DefaultDoc:
                String defaultDocParam = "项目号:[" + projectId
                        + "], 预测单号:[" + requestName
                        + "], 预测/预留:[" + requestType
                        + "]";
                if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(requestName) || StringUtils.isEmpty(requestType)) {
                    throw new RuntimeException("\"默认接口-图纸\"类型预测预留存在必填参数为空,请检查参数!" + defaultDocParam);
                }
                break;

            case TP_DefaultDocCC:
            case WP_DefaultDocCC:
                String defaultDocCCParam = "包OBID:[" + packageId
                        + "],项目号:[" + projectId
                        + "], 预测单号:[" + requestName
                        + "], 预测/预留:[" + requestType
                        + "], 存在材料消耗图纸集合:[" + drawingNumbers
                        + "]";
                if (StringUtils.isEmpty(packageId) || StringUtils.isEmpty(projectId) || StringUtils.isEmpty(requestName) || StringUtils.isEmpty(requestType) || StringUtils.isEmpty(drawingNumbers)) {
                    throw new RuntimeException("\"默认接口-图纸-CC码\"类型预测预留存在必填参数为空,请检查参数!" + defaultDocCCParam);
                }
                break;

            case TP_DefaultDocIC:
            case WP_DefaultDocIC:
                throw new RuntimeException("目前不支持\"默认接口-图纸-IdentCode\"类型存储过程!");

            case TP_NewDoc:
            case WP_NewDoc:
                String newDocParam = "包OBID:[" + packageId
                        + "],项目号:[" + projectId
                        + "], 预测单号:[" + requestName
                        + "], 预测/预留:[" + requestType
                        + "], 存在材料消耗图纸集合:[" + drawingNumbers
                        + "]";
                if (StringUtils.isEmpty(packageId) || StringUtils.isEmpty(projectId) || StringUtils.isEmpty(requestName) || StringUtils.isEmpty(requestType) || StringUtils.isEmpty(drawingNumbers)) {
                    throw new RuntimeException("\"新接口-图纸\"类型预测预留存在必填参数为空,请检查参数!" + newDocParam);
                }
                break;

            case TP_NewDocCC:
            case WP_NewDocCC:
                String newDocCCParam = "包OBID:[" + packageId
                        + "],项目号:[" + projectId
                        + "], 预测单号:[" + requestName
                        + "], 预测/预留:[" + requestType
                        + "], 存在材料消耗图纸集合:[" + drawingNumbers
                        + "]";
                if (StringUtils.isEmpty(packageId) || StringUtils.isEmpty(projectId) || StringUtils.isEmpty(requestName) || StringUtils.isEmpty(requestType) || StringUtils.isEmpty(drawingNumbers)) {
                    throw new RuntimeException("\"新接口-图纸-CC码\"类型预测预留存在必填参数为空,请检查参数!" + newDocCCParam);
                }
                break;

            case TP_NewDocIC:
            case WP_NewDocIC:
                String newDocICParam = "包OBID:[" + packageId
                        + "],项目号:[" + projectId
                        + "], 预测单号:[" + requestName
                        + "], 预测/预留:[" + requestType
                        + "], 存在材料消耗图纸集合:[" + drawingNumbers
                        + "]";
                if (StringUtils.isEmpty(packageId) || StringUtils.isEmpty(projectId) || StringUtils.isEmpty(requestName) || StringUtils.isEmpty(requestType) || StringUtils.isEmpty(drawingNumbers)) {
                    throw new RuntimeException("\"新接口-图纸-IdentCode\"类型预测预留存在必填参数为空,请检查参数!" + newDocICParam);
                }
                break;

            case TP_NewDocICWarehouses:
            case WP_NewDocICWarehouses:
                String newDocICWarehousesParam = "包OBID:[" + packageId
                        + "],项目号:[" + projectId
                        + "], 预测单号:[" + requestName
                        + "], 预测/预留:[" + requestType
                        + "], 存在材料消耗图纸集合:[" + drawingNumbers
                        + "], 仓库:[" + warehouses
                        + "]";
                if (StringUtils.isEmpty(packageId) || StringUtils.isEmpty(projectId) || StringUtils.isEmpty(requestName) || StringUtils.isEmpty(requestType) || StringUtils.isEmpty(drawingNumbers) || StringUtils.isEmpty(warehouses)) {
                    throw new RuntimeException("\"新接口-图纸-IdentCode-仓库\"类型预测预留存在必填参数为空,请检查参数!" + newDocICWarehousesParam);
                }
                break;
            default:
                break;

        }
    }

    private IObject getPackageObject(PackageTypeEnum packageType, String packageId) throws Exception {
        IObject packageByOBID = null;
        switch (packageType) {
            case TP:
                packageByOBID = taskPackageService.getTaskPackagesByOBID(packageId);
                if (null == packageByOBID) {
                    throw new RuntimeException("未获取到OBID为" + packageId + "的任务包!");
                }
                break;
            case WP:
                packageByOBID = workPackageService.getWorkPackageByOBID(packageId);
                if (null == packageByOBID) {
                    throw new RuntimeException("未获取到OBID为" + packageId + "的工作包!");
                }
                break;
        }
        return packageByOBID;
    }

    private Map<String, String> getPackagePurposeAndCWA(PackageTypeEnum packageType, IObject packagesByOBID) throws Exception {
        HashMap<String, String> result = new HashMap<>();
        switch (packageType) {
            case TP:
                ICCMTaskPackage iccmTaskPackage = packagesByOBID.toInterface(ICCMTaskPackage.class);
                String taskPackagePurpose = iccmTaskPackage.getPurpose();
                if (StringUtils.isEmpty(taskPackagePurpose)) {
                    throw new RuntimeException("未获取到任务包阶段!");
                }
                String taskPackageCWA = iccmTaskPackage.getCWA();
                result.put("Purpose", taskPackagePurpose);
                result.put("CWA", taskPackageCWA);
                break;
            case WP:
                ICCMWorkPackage iccmWorkPackage = packagesByOBID.toInterface(ICCMWorkPackage.class);
                String workPackagePurpose = iccmWorkPackage.getPurpose();
                if (StringUtils.isEmpty(workPackagePurpose)) {
                    throw new RuntimeException("未获取到工作包阶段!");
                }
                String workPackageCWA = iccmWorkPackage.getCWA();
                result.put("Purpose", workPackagePurpose);
                result.put("CWA", workPackageCWA);
                break;
        }
        return result;
    }

    private String getPackageROPWorkStepPhaseType(PackageTypeEnum packageType) throws Exception {
        String result = "";
        switch (packageType) {
            case TP:
                result = "ROPWorkStepTPPhase";
                break;
            case WP:
                result = "ROPWorkStepWPPhase";
                break;
        }
        if (StringUtils.isEmpty(result)) {
            throw new RuntimeException("获取ROP工作步骤阶段类型失败!");
        }
        return result;
    }

    /**
     * 获取包用于预测预留的参数
     *
     * @param packageId
     * @param drawingNumbers
     * @return
     * @throws Exception
     */
    private Map<String, String> getRequestParams(PackageTypeEnum packageType, String packageId, String drawingNumbers, boolean needCCOrIC) throws Exception {
        IObject packagesByOBID = getPackageObject(packageType, packageId);
        Map<String, String> packagePurposeAndCWA = getPackagePurposeAndCWA(packageType, packagesByOBID);
        String packagePurpose = packagePurposeAndCWA.get("Purpose");
        String packageCWA = packagePurposeAndCWA.get("CWA");


        Set<String> drawingNumberList = new HashSet<String>();
        List<String> materialCodeList = new ArrayList<String>();
        List<String> pSize1List = new ArrayList<>();
        List<String> pSize2List = new ArrayList<String>();

        //  MaterialCode+PSize1+PSize2 唯一
        List<String> filters = new ArrayList<String>();
        // 添加选择图纸支持
        if (!StringUtils.isEmpty(drawingNumbers)) {
            // 手动选择图纸时 根据包消耗材料进行预测预留
            IQueryEngine documentQueryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = documentQueryEngine.start();
            documentQueryEngine.addClassDefForQuery(queryRequest, DocumentUtils.CCM_DOCUMENT);
            documentQueryEngine.addOBIDForQuery(queryRequest, operator.in, drawingNumbers);
            IObjectCollection selectDocumentColl = documentQueryEngine.query(queryRequest);
            Iterator<IObject> selectDocumentIter = selectDocumentColl.GetEnumerator();
            while (selectDocumentIter.hasNext()) {
                IObject selectDocument = selectDocumentIter.next();
                IQueryEngine designObjQueryEngine = CIMContext.Instance.QueryEngine();
                QueryRequest queryRequest1 = designObjQueryEngine.start();
                designObjQueryEngine.addInterfaceForQuery(queryRequest1, DataRetrieveUtils.I_COMPONENT);
                designObjQueryEngine.addRelOrEdgeDefForQuery(queryRequest1,
                        "-" + DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ, "", propertyDefinitionType.OBID.toString(), operator.equal, selectDocument.OBID(), ExpansionMode.relatedObject);
                designObjQueryEngine.addPropertyForQuery(queryRequest1,
                        "", propertyDefinitionType.CIMRevisionItemOperationState.toString(), operator.notEqual, operationState.EN_Deleted.toString());
                // 施工区域过滤
                if (!StringUtils.isEmpty(packageCWA)) {
                    designObjQueryEngine.addPropertyForQuery(queryRequest1,
                            ICCMWBSUtils.I_CCM_WBS, ICCMWBSUtils.PROPERTY_CWA, operator.equal, packageCWA);
                }
                IObjectCollection designObjColl = designObjQueryEngine.query(queryRequest1);

                Iterator<IObject> designObjIter = designObjColl.GetEnumerator();
                while (designObjIter.hasNext()) {
                    IObject designObj = designObjIter.next();
                    String materialCode = designObj.getValue("MaterialCode");
                    // 材料编码为空的跳过
                    if (StringUtils.isEmpty(materialCode)) {
                        continue;
                    }
                    String pSize1 = designObj.getValue("PSize1");
                    pSize1 = StringUtils.isEmpty(pSize1) ? "0" : pSize1;
                    String pSize2 = designObj.getValue("PSize2");
                    pSize2 = StringUtils.isEmpty(pSize2) ? "0" : pSize2;

                    // 通过唯一标识判断过滤
                    String filter = materialCode + pSize1 + pSize2;
                    if (filters.contains(filter)) {
                        continue;
                    }
                    filters.add(filter);

                    IQueryEngine workStepQueryEngine = CIMContext.Instance.QueryEngine();
                    QueryRequest queryRequest2 = workStepQueryEngine.start();
                    workStepQueryEngine.addClassDefForQuery(queryRequest2, "CCMWorkStep");
                    workStepQueryEngine.addRelOrEdgeDefForQuery(queryRequest2,
                            "-" + PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP, "", propertyDefinitionType.OBID.toString(), operator.equal, designObj.OBID(), ExpansionMode.relatedObject);
                    workStepQueryEngine.addPropertyForQuery(queryRequest2,
                            "", propertyDefinitionType.WSStatus.toString(),
                            operator.notIn, workStepStatus.EN_RevisedDelete + "," + workStepStatus.EN_ROPDelete);
                    workStepQueryEngine.addPropertyForQuery(queryRequest2,
                            "", getPackageROPWorkStepPhaseType(packageType), operator.equal, packagePurpose);
                    IObjectCollection workStepColl = workStepQueryEngine.query(queryRequest2);
                    Iterator<IObject> workStepIter = workStepColl.GetEnumerator();
                    while (workStepIter.hasNext()) {
                        IObject workStepObj = workStepIter.next();
                        if (PackageTypeEnum.TP == packageType) {
                            IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                            if (!workStep.WSConsumeMaterial()) {
                                continue;
                            }
                        } else {
                            IRel wp2wsRel = workStepObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);
                            if (null != wp2wsRel) {
                                IObject relatedWorkPackage = wp2wsRel.GetEnd1();
                                if (!packagesByOBID.OBID().equalsIgnoreCase(relatedWorkPackage.OBID())) {
                                    continue;
                                }
                            }
                            IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
                            if (!workStep.WSConsumeMaterial()) {
                                continue;
                            }
                        }
                        if (needCCOrIC) {
                            materialCodeList.add(materialCode);
                            pSize1List.add(pSize1);
                            pSize2List.add(pSize2);
                        }
                        drawingNumberList.add(selectDocument.Name());

                    }
                }
            }
        } else {
            List<IObject> workStepsWithSamePurposeAndConsumeMaterial = new ArrayList<>();
            if (PackageTypeEnum.TP == packageType) {
                workStepsWithSamePurposeAndConsumeMaterial = taskPackageService.getWorkStepsWithSamePurposeAndConsumeMaterial(packagesByOBID);
            } else {
                ICCMWorkPackage iccmWorkPackage = packagesByOBID.toInterface(ICCMWorkPackage.class);
                IObjectCollection workSteps = iccmWorkPackage.getWorkStepsWithoutDeleted();
                workStepsWithSamePurposeAndConsumeMaterial = workSteps.toList().stream().filter(w -> {
                    IWorkStep iWorkStep = w.toInterface(IWorkStep.class);
                    // 返回有材料消耗的
                    return iWorkStep.WSConsumeMaterial();
                }).collect(Collectors.toList());
            }

            for (IObject workStepObj : workStepsWithSamePurposeAndConsumeMaterial) {
                // 获取材料编码
                IObject designDataObj = workStepObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd1();
                // 设计数据 版本状态
                String designRevisionItemOperationState = String.valueOf(designDataObj.getProperty(propertyDefinitionType.CIMRevisionItemOperationState.toString()).Value());
                // 过滤删除状态的设计数据
                if (operationState.EN_Deleted.toString().equalsIgnoreCase(designRevisionItemOperationState)) {
                    continue;
                }
                IObject documentObj = designDataObj.GetEnd2Relationships().GetRel(DocumentUtils.REL_DOCUMENT_2_DESIGN_OBJ).GetEnd1();
                if (needCCOrIC) {
                    String materialCode = designDataObj.getValue("MaterialCode");
                    // 材料编码为空的跳过
                    if (StringUtils.isEmpty(materialCode)) {
                        continue;
                    }
                    String pSize1 = designDataObj.getValue("PSize1");
                    pSize1 = StringUtils.isEmpty(pSize1) ? "0" : pSize1;
                    String pSize2 = designDataObj.getValue("PSize2");
                    pSize2 = StringUtils.isEmpty(pSize2) ? "0" : pSize2;
                    // 通过唯一标识判断过滤
                    String filter = materialCode + pSize1 + pSize2;
                    if (filters.contains(filter)) {
                        continue;
                    }
                    filters.add(filter);

                    materialCodeList.add(materialCode);
                    pSize1List.add(pSize1);
                    pSize2List.add(pSize2);
                }
                drawingNumberList.add(documentObj.Name());
            }
        }
        drawingNumbers = String.join(",", drawingNumberList);
        String commodityCodes = String.join(",", materialCodeList);
        String pSize1s = String.join(",", pSize1List);
        String pSize2s = String.join(",", pSize2List);
        String lpAttrValue = packagePurpose.replace("EN_", "").trim();

        Map<String, String> resultParams = new HashMap<>();
        resultParams.put(PackageRequestUtils.DRAWING_NUMBERS, drawingNumbers);
        resultParams.put(PackageRequestUtils.COMMODITY_CODES, commodityCodes);
        resultParams.put(PackageRequestUtils.P_SIZE1S, pSize1s);
        resultParams.put(PackageRequestUtils.P_SIZE2S, pSize2s);
        resultParams.put(PackageRequestUtils.LP_ATTR_VALUE, lpAttrValue);

        return resultParams;
    }

    /* ************************************************* 预测预留  end  ******************************************************* */
    /* ************************************************* 升版处理  start  ******************************************************* */

    @Override
    public void confirmRevision(String packageOBID, PackageTypeEnum packageType) throws Exception {
        IObject packageObj = null;
        switch (packageType) {
            case TP:
                packageObj = taskPackageService.getTaskPackagesByOBID(packageOBID);
                break;
            case WP:
                packageObj = workPackageService.getWorkPackageByOBID(packageOBID);
                break;
            case PTP:
                packageObj = pressureTestPackageService.getPressureTestPackagesByOBID(packageOBID);
                break;
        }
        if (null == packageObj) {
            throw new RuntimeException("未能根据包类型及OBID获取对应包!包类型:" + packageType.name() + ",包OBID:" + packageOBID);
        }
        Map<String, String> classDefRelMap = PackageRevisionUtils.switchPackage(packageObj);

        // 包逻辑处理
        IRelCollection iRelCollection = packageObj.GetEnd1Relationships().GetRels(classDefRelMap.get(PackageRevisionUtils.REL_2_DOCUMENT));
        Iterator<IObject> iRelIter = iRelCollection.GetEnumerator();
        SchemaUtility.beginTransaction();
        // 清除包-图纸关联关系上的升版标记
        while (iRelIter.hasNext()) {
            IObject iRelObj = iRelIter.next();
            IRel iRel = iRelObj.toInterface(IRel.class);
            try {
                PackageRevisionUtils.clearRelRevisedStatus(iRel);
            } catch (Exception exception) {
                log.error("清除包和图纸关联关系升版状态失败!", ExceptionUtil.getRootCause(exception));
                throw new RuntimeException("清除包和图纸关联关系升版状态失败!");
            }
        }
        // 工作包和试压包删除设计对象关联关系
        if (!PackagesUtils.CCM_TASK_PACKAGE.equalsIgnoreCase(classDefRelMap.get(PackageRevisionUtils.CLASS_DEF))) {
            IRelCollection package2DesignRel = packageObj.GetEnd1Relationships().GetRels(classDefRelMap.get(PackageRevisionUtils.REL_2_DESIGN_OBJ));
            Iterator<IObject> package2DesignRelIter = package2DesignRel.GetEnumerator();
            // 升版标记
            while (package2DesignRelIter.hasNext()) {
                IObject iRelObj = package2DesignRelIter.next();
                // 清除包 - 设计对象关联关系上的升版状态
                try {
                    PackageRevisionUtils.clearRelRevisedStatus(iRelObj);
                } catch (Exception exception) {
                    log.error("清除包和设计对象关联关系升版状态失败!", ExceptionUtil.getRootCause(exception));
                    throw new RuntimeException("清除包和设计对象关联关系升版状态失败!");
                }
                IRel iRel = iRelObj.toInterface(IRel.class);
                // 清除设计对象的升版状态
                IObject designObj = iRel.GetEnd2();
                try {
                    PackageRevisionUtils.clearObjectRevisedStatus(designObj);
                } catch (Exception exception) {
                    log.error("清除设计对象升版状态失败!", ExceptionUtil.getRootCause(exception));
                    throw new RuntimeException("清除设计对象升版状态失败!");
                }

            }
        }
        // 清除包自身升版标记
        try {
            PackageRevisionUtils.clearObjectRevisedStatus(packageObj);
        } catch (Exception exception) {
            log.error("清除包自身升版状态失败!", ExceptionUtil.getRootCause(exception));
            throw new RuntimeException("清除包自身升版状态失败!");
        }
        SchemaUtility.commitTransaction();
    }
    /* ************************************************* 升版处理  end  ******************************************************* */
}
