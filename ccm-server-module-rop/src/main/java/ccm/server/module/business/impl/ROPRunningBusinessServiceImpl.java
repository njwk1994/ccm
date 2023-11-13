package ccm.server.module.business.impl;

import ccm.server.business.ISchemaBusinessService;
import ccm.server.cache.IProcessCache;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.*;
import ccm.server.excel.entity.ExcelDataContent;
import ccm.server.excel.util.ExcelUtility;
import ccm.server.model.FiltersParam;
import ccm.server.model.LoaderReport;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.business.IROPRunningBusinessService;
import ccm.server.module.context.ROPCache;
import ccm.server.module.impl.general.InternalServiceImpl;
import ccm.server.module.task.ROPTemplateReviseTask;
import ccm.server.module.task.handler.ROPTemplateReviseHandler;
import ccm.server.module.utils.ROPUtils;
import ccm.server.module.vo.ROPReviseTaskVo;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.queue.handler.IQueueTaskHandler;
import ccm.server.queue.service.QueueGenerationService;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.*;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import ccm.server.utils.DataRetrieveUtils;
import ccm.server.utils.PageUtility;
import ccm.server.utils.SchemaUtility;
import ccm.server.utils.ValueConversionUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ROPRunningBusinessServiceImpl extends InternalServiceImpl implements IROPRunningBusinessService {

    @Override
    public IObjectCollection getComponentCategoriesByConstructionType(IObject constructionType) throws Exception {
        if (constructionType != null) {
            IRelCollection relCollection = constructionType.GetEnd1Relationships().GetRels(relDefinitionType.CCMConstructionType2ComponentCategory.toString(), false);
            if (SchemaUtility.hasValue(relCollection)) {
                return relCollection.GetEnd2s();
            }
        }
        return null;
    }

    @Override
    public IObjectCollection getROPGroupsByConstructionType(IObject constructionType) throws Exception {
        if (constructionType != null) {
            ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
            String targetClassDefs = ValueConversionUtility.toString(constructionType.getProperty("TargetClassDef"));
            IObjectCollection lcolROPGroups = ROPCache.ROPGroups.get(targetClassDefs + "|" + configurationItem.UID());
            if (SchemaUtility.hasValue(lcolROPGroups)) {
//                List<IObject> lcolCurrentConfigROPGroups = lcolROPGroups.toList().stream().filter(r -> configurationItem.UID().equalsIgnoreCase(r.Config())).collect(Collectors.toList());
//                if (CommonUtility.hasValue(lcolCurrentConfigROPGroups)) {
//                    return SchemaUtility.toIObjectDictionary(lcolCurrentConfigROPGroups);
//                }
                return SchemaUtility.toIObjectDictionary(lcolROPGroups.toList());
            }
            //  return ROPUtils.getROPGroupByROPGroupClassDefinitionUID(targetClassDefs.split(","), false);
        }
        return null;
    }

    @Override
    public IObjectCollection getROPGroupItemsByROPGroup(IObject pobjROPRuleGroup) {
        if (pobjROPRuleGroup != null) {
            return pobjROPRuleGroup.toInterface(IROPRuleGroup.class).getItems();
        }
        return null;
    }

    @Override
    public IObjectCollection getROPWorkStepTemplatesByROPRuleGroup(IObject ropRuleGroup) throws Exception {
        if (ropRuleGroup != null) {
            return ropRuleGroup.toInterface(IROPRuleGroup.class).getROPWorkSteps();
        }
        return null;
    }


    @Override
    public IObjectCollection generateWorkStepByDocument(IObject document) throws Exception {
        return this.generateWorkStepByDocument(document, false);
    }

    @Override
    public IObjectCollection generateWorkStepByDocument(IObject document, boolean transactionOrNot) throws Exception {
        IObjectCollection result = new ObjectCollection();
        StopWatch stopWatch = PerformanceUtility.start();
        if (document != null) {
            IObjectCollection designObjs = document.GetEnd1Relationships().GetRels(relDefinitionType.CCMDocument2DesignObj.toString()).GetEnd2s();
            if (designObjs != null && designObjs.hasValue()) {
                try {
                    if (transactionOrNot)
                        CIMContext.Instance.Transaction().start();
                    Iterator<IObject> e = designObjs.GetEnumerator();
                    while (e.hasNext()) {
                        IObject current = e.next();
                        IROPExecutableItem executableItem = current.toInterface(IROPExecutableItem.class);
                        if (executableItem != null) {
                            IObjectCollection objectCollection = executableItem.generateWorkStepObjects(false);
                            if (objectCollection != null)
                                result.addRangeUniquely(objectCollection);
                        } else
                            log.warn("object without IROPExecutableItem interface, cannot do ROP progress for " + current.toErrorPop());
                    }
                    if (transactionOrNot)
                        CIMContext.Instance.Transaction().commit();
                } catch (Exception exception) {
                    log.error("generate work step(s) for document failed", exception);
                    if (transactionOrNot)
                        CIMContext.Instance.Transaction().rollBack();
                }
            }
        }
        log.info("compete to generate work step(s) for document" + PerformanceUtility.stop(stopWatch));
        return result;
    }

    @Override
    public IObjectCollection generateWorkStepByDocument(String documentOBID, String classDefinitionUID) throws Exception {
        if (!StringUtils.isEmpty(documentOBID) && !StringUtils.isEmpty(classDefinitionUID)) {
            QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
            CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, new ArrayList<String>() {{
                this.add(documentOBID);
            }});
            CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionUID);
            IObject object = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
            if (object != null) {
                return this.generateWorkStepByDocument(object, true);
            }
        }
        return null;
    }

    @Override
    public IObjectCollection generateWorkStepForDesignObject(IObject designObject, IObjectCollection pcolExistObjects, String configName) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (designObject != null) {
            IROPExecutableItem executableItem = designObject.toInterface(IROPExecutableItem.class);
            if (executableItem != null) {
                IObjectCollection lcolROPGroups = ROPCache.ROPGroups.get(designObject.ClassDefinitionUID() + "|PL_" + configName);
                if (SchemaUtility.hasValue(lcolROPGroups)) {
                    List<IObject> lcolConfigRopGroups = lcolROPGroups.toList().stream().map(r -> r.toInterface(IROPRuleGroup.class)).sorted(Comparator.comparing(IROPRuleGroup::ROPGroupOrder)).collect(Collectors.toList());
                    //log.info("设计对象:{},ROP规则:{}", designObject.Name(), lcolConfigRopGroups.stream().map(IObject::Name).collect(Collectors.joining(",")));
                    lcolROPGroups = SchemaUtility.toIObjectDictionary(lcolConfigRopGroups);
                }
                IObjectCollection collection = executableItem.generateWorkStepObjects(false, lcolROPGroups, pcolExistObjects);
                if (collection != null)
                    result.addRange(collection);
            } else {
                throw new Exception("设计对象:" + designObject.Name() + ",UID:" + designObject.UID() + "未实现接口IROPExecutableItem");
            }
        }
        return result;
    }

    @Override
    public IObjectCollection generateWorkStepForDesignObjects(IObjectCollection pcolDesignObjects, IObjectCollection pcolExistObjects, String configItemName) throws Exception {
        if (SchemaUtility.hasValue(pcolDesignObjects)) {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            IObjectCollection lcolContainer = new ObjectCollection();
            Iterator<IObject> e = pcolDesignObjects.GetEnumerator();
            while (e.hasNext()) {
                IObject lobjDesignObj = e.next();
                IObjectCollection lcolWorkSteps = this.generateWorkStepForDesignObject(lobjDesignObj, pcolExistObjects, configItemName);
                if (SchemaUtility.hasValue(lcolWorkSteps)) {
                    lcolContainer.addRangeUniquely(lcolWorkSteps);
                }
            }
            stopWatch.stop();
            log.info("生成步骤用时:{},设计对象个数:{}个", CommonUtility.getTimeSpan(stopWatch), pcolDesignObjects.size());
            return lcolContainer;
        }
        return null;
    }

    @Override
    public List<ObjectDTO> getConstructionTypes() throws Exception {
        List<ObjectDTO> result = new ArrayList<>();
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, DataRetrieveUtils.CCM_CONSTRUCTION_TYPE);
        if (PageUtility.verifyPage(new PageRequest(0, 0))) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, new PageRequest(0, 0));
        }
        IObjectCollection constructionTypes = CIMContext.Instance.QueryEngine().query(queryRequest);
        Iterator<IObject> constructionTypesIterator = constructionTypes.GetEnumerator();
        while (constructionTypesIterator.hasNext()) {
            IObject item = constructionTypesIterator.next();
            result.add(item.toObjectDTO());
        }
        return result;
    }

    @Override
    public void generateROPTemplateData(HttpServletResponse response, String pstrROPGroupOBID) throws Exception {
        List<ExcelDataContent> ropTemplateInfo = ROPUtils.generateROPTemplateDefaultSheetContent();
        IObjectCollection ropGroups;
        if (!StringUtils.isEmpty(pstrROPGroupOBID)) {
            IObject lobjROPGroup = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrROPGroupOBID, classDefinitionType.ROPRuleGroup.toString());
            if (lobjROPGroup == null) throw new Exception("未找到OBID:" + pstrROPGroupOBID + "对象!");
            ropGroups = new ObjectCollection();
            ropGroups.append(lobjROPGroup);
        } else {
            ropGroups = SchemaUtility.getObjectsByClassDef(ROPUtils.CLASSDEF_ROP_GROUP);
        }
        ExcelDataContent ropGroup = ROPUtils.generateROPGroupContent(ropGroups);
        ropTemplateInfo.add(ropGroup);
        ExcelDataContent ropItems = ROPUtils.generateROPGroupItemDataContent();
        ROPUtils.setROPRuleGroupItemsContent(ropItems, ropGroups);
        ropTemplateInfo.add(ropItems);
        ExcelDataContent workStep = ROPUtils.generateROPWorkStepDataContent();
        ROPUtils.setROPRuleWorkStepContent(workStep, ropGroups);
        ropTemplateInfo.add(workStep);
        XSSFWorkbook workBook = ExcelUtility.getWorkBook(ropTemplateInfo);
        ExcelUtility.writeFileIntoHttpResponse(response, "ROPTemplateInformation.xlsx", workBook);
    }

    @Autowired
    private IProcessCache processCache;

    @Override
    public Boolean deleteROPTemplateInfo(String pstrOBID, String pstrClassDef, String pstrROPGroupOBID) throws Exception {
        if (!StringUtils.isEmpty(pstrOBID) && !StringUtils.isEmpty(pstrClassDef)) {
            String lstrParam;
            IObject lobj = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrOBID, pstrClassDef);
            if (lobj == null) throw new Exception("未找到" + pstrClassDef + "类型的对象,OBID:" + pstrOBID);
            SchemaUtility.beginTransaction();
            if (classDefinitionType.ROPRuleGroup.toString().equalsIgnoreCase(pstrClassDef)) {
                lstrParam = lobj.toInterface(IROPRuleGroup.class).ROPGroupClassDefinitionUID() + "|" + lobj.Config();
                lobj.Delete();
                //  this.refreshObjStatusByROPChanged();
            } else {
                lstrParam = pstrROPGroupOBID;
                this.schemaBusinessService.deleteObject(pstrOBID, pstrClassDef, false);
                IObject lobjROPGroup = CIMContext.Instance.ProcessCache().getObjectByOBID(pstrROPGroupOBID, classDefinitionType.ROPRuleGroup.toString());
                if (lobjROPGroup == null) throw new Exception("未找到规则组对象,OBID:" + pstrROPGroupOBID);
                IROPRuleGroup ruleGroup = lobjROPGroup.toInterface(IROPRuleGroup.class);
                ruleGroup.BeginUpdate();
                ruleGroup.setROPHasHandleChange(false);
                if (classDefinitionType.ROPRuleGroupItem.toString().equalsIgnoreCase(pstrClassDef)) {
                    ruleGroup.setROPGroupItemRevState(ropRevState.EN_Updated_ROPTemplate.toString());
                } else {
                    ruleGroup.setROPGroupWorkStepRevState(ropRevState.EN_Updated_ROPTemplate.toString());
                }
                ruleGroup.FinishUpdate();
            }
            SchemaUtility.commitTransaction();
            ROPCache.Instance.refreshROPInfoWhenDelete(pstrOBID, pstrClassDef, lstrParam);
        }
        return true;
    }

    @Override
    public List<ObjectDTO> getPropDefsForROPGroupItemByROPGroupOBID(String ropGroupOBID) throws Exception {
        if (!StringUtils.isEmpty(ropGroupOBID)) {
            IObjectCollection lcolContainer = new ObjectCollection();
            IObject lobjROPGroup = SchemaUtility.getObjectByOBIDAndClassDef(ropGroupOBID, classDefinitionType.ROPRuleGroup.toString());
            if (lobjROPGroup == null) throw new Exception("未找到相关对象!");
            IROPRuleGroup ruleGroup = lobjROPGroup.toInterface(IROPRuleGroup.class);
            String lstrClassDefUID = ruleGroup.ROPGroupClassDefinitionUID();
            List<String> realizedInterfaceDefs = CIMContext.Instance.ProcessCache().getRealizedInterfaceDefByClassDef(lstrClassDefUID, false);
            if (CommonUtility.hasValue(realizedInterfaceDefs)) {
                for (String lstrInterfaceDef : realizedInterfaceDefs) {
                    //跳过IObject 的接口
                    if (interfaceDefinitionType.IObject.toString().equals(lstrInterfaceDef)) continue;
                    IObject lobjInterfaceDef = CIMContext.Instance.ProcessCache().item(lstrInterfaceDef, domainInfo.SCHEMA.toString());
                    if (lobjInterfaceDef == null) {
                        throw new Exception("未找到接口定义:" + lstrInterfaceDef);
                    }
                    IInterfaceDef interfaceDef = lobjInterfaceDef.toInterface(IInterfaceDef.class);
                    IObjectCollection exposesPropertyDefinition = interfaceDef.getExposesPropertyDefinition();
                    if (SchemaUtility.hasValue(exposesPropertyDefinition)) {
                        lcolContainer.addRangeUniquely(exposesPropertyDefinition);
                    }
                }
            }
            if (lcolContainer.hasValue()) {
                return SchemaUtility.converterIObjectCollectionToDTOList(lcolContainer);
            }
        }
        return null;
    }

    @Autowired
    private QueueGenerationService queueGenerationService;

    /*
     * @Descriptions :  ROP模板发生变化后,触发ROP刷新,更新相关的设计对象关联的实际工步的状态
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:11
     * @param null
     * @Return:
     */
    @Override
    public void refreshObjStatusByROPChanged() throws Exception {
        Subject subject = SecurityUtils.getSubject();
        LoginUser loginUser = (LoginUser) subject.getPrincipal();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());
        IQueueTaskHandler ropTemplateReviseHandler = new ROPTemplateReviseHandler(new ROPTemplateReviseTask(this), CommonUtility.generateUUID(), loginUser.getUsername(), "ropTemplateRevise", subject, configurationItem.UID());
        this.queueGenerationService.addData(ropTemplateReviseHandler);
    }

    /*
     * create by: Chen Jing
     * description: 获取ROP升版任务的进度信息
     * create time: 2022/5/6 16:12
     * @return String[2]  进度百分比  执行过程信息
     * @Param taskUUID rop任务的UUID
     */
    @Override
    public ROPReviseTaskVo getROPProcessingTaskStatus(String taskUUID) {
        if (!StringUtils.isEmpty(taskUUID)) {
            IQueueTaskHandler processingHandler = this.queueGenerationService.getROPTemplateReviseProcessingHandler(taskUUID, CommonUtility.getLoginUserName());
            if (processingHandler != null) {
                return new ROPReviseTaskVo(processingHandler);
            }
        }
        return null;
    }

    /*
     * create by: Chen Jing
     * description: 清除ROP升版的历史记录信息
     * create time: 2022/5/6 16:19
     */
    @Override
    public void clearProcessingTasks() {
        this.queueGenerationService.clearROPTemplateReviseProcessingTasks(CommonUtility.getLoginUserName());
    }

    /*
     * @Description: 获取没有执行过同步升版的规则组
     * @param
     * @return: ccm.server.schema.collections.IObjectCollection
     * @Author: Chen Jing
     * @Date: 2022-05-09 15:13:59
     */
    @Override
    public Map<String, IObjectCollection> getNotProcessRevisedROPRuleGroups() {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.ROPRuleGroup.toString());
        CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest, interfaceDefinitionType.IROPRuleGroup.toString(), propertyDefinitionType.ROPHasHandleChange.toString(), operator.equal, Boolean.toString(false));
        IObjectCollection lcolResult = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (SchemaUtility.hasValue(lcolResult)) {
            Map<String, IObjectCollection> lcolTemp = new HashMap<>();
            Iterator<IObject> e = lcolResult.GetEnumerator();
            while (e.hasNext()) {
                IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                String groupClassDefinitionUID = ruleGroup.ROPGroupClassDefinitionUID();
                if (lcolTemp.containsKey(groupClassDefinitionUID)) {
                    lcolTemp.get(groupClassDefinitionUID).append(ruleGroup);
                } else {
                    IObjectCollection lcolContainer = new ObjectCollection();
                    lcolContainer.append(ruleGroup);
                    lcolTemp.put(groupClassDefinitionUID, lcolContainer);
                }
            }
            return lcolTemp;
        }
        return null;
    }

    /*
     * @Description: 执行ROP模板升版逻辑
     * @param Callable 执行的Task任务对象 主要设置进度信息用到
     * @return: ccm.server.model.LoaderReport
     * @Author: Chen Jing
     * @Date: 2022-05-09 15:13:15
     */
    @Override
    public LoaderReport processROPTemplateRevise(ROPTemplateReviseTask callable) {
        LoaderReport loaderReport = new LoaderReport();
        //获取系统所有未执行过升版同步的规则组
        ROPUtils.setROPTemplateReviseProcessingInfo(callable, 1, "收集未执行过升版的ROP规则组对象信息!");
        Map<String, IObjectCollection> lcolNotSyncRuleGroups = this.getNotProcessRevisedROPRuleGroups();
        if (CommonUtility.hasValue(lcolNotSyncRuleGroups)) {
            int count = 0;
            ROPUtils.setROPTemplateReviseProcessingInfo(callable, 5, "收集ROP规则组对象信息完成, 开始刷新同步相关的设计对象工步信息!");
            try {
                SchemaUtility.beginTransaction();
                for (Map.Entry<String, IObjectCollection> ropRuleGroups : lcolNotSyncRuleGroups.entrySet()) {
                    ROPUtils.setROPTemplateReviseProcessingInfo(callable, CommonUtility.calculatePercentage(count, lcolNotSyncRuleGroups.size()) - 1, "对象类型:" + ropRuleGroups.getKey() + ",共!" + lcolNotSyncRuleGroups.size() + "个,开始第" + (count + 1) + "个");
                    IObjectCollection lcolContainer = this.handleROPRuleGroupsRevise(callable, ropRuleGroups.getValue(), ropRuleGroups.getKey());
                    if (SchemaUtility.hasValue(lcolContainer)) {
                        loaderReport.addObjects(lcolContainer);
                    }
                    //变更ROPGroup状态
                    Iterator<IObject> e = ropRuleGroups.getValue().GetEnumerator();
                    while (e.hasNext()) {
                        IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                        ruleGroup.setROPInitStatus(true);
                    }
                    count++;
                }
                SchemaUtility.commitTransaction();
                ROPUtils.setROPTemplateReviseProcessingInfo(callable, 100, "执行完毕!");
            } catch (Exception ex) {
                ROPUtils.setROPTemplateReviseProcessingInfo(callable, 100, "错误:" + ExceptionUtil.getMessage(ex));
            }
        } else {
            ROPUtils.setROPTemplateReviseProcessingInfo(callable, 100, "没有需要同步的ROP模板信息!");
        }
        return loaderReport;
    }

    @Override
    public void getAllROPProcessingTasks(PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam, Result<List<ROPReviseTaskVo>> result) throws Exception {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());
        List<IQueueTaskHandler> processingHandler = this.queueGenerationService.getROPTemplateReviseProcessingHandler(configurationItem != null ? configurationItem.UID() : "");
        if (CommonUtility.hasValue(processingHandler)) {
            List<ROPReviseTaskVo> reviseTaskVos = processingHandler.stream().map(ROPReviseTaskVo::new).collect(Collectors.toList());
            List<List<ROPReviseTaskVo>> lists = CommonUtility.partitionList(reviseTaskVos, pageRequest.getPageSize());
            if (CommonUtility.hasValue(lists)) {
                result.setResult(lists.get(pageRequest.getPageIndex() - 1));
                result.setTotal(Long.parseLong(reviseTaskVos.size() + ""));
            }
        }
    }

    @Override
    public void getROPTemplateReviseHistoryTasks(PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam, ResultVo<List<ROPReviseTaskVo>> result) throws Exception {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());
        JSONArray hisTasks = this.queueGenerationService.getHisTasks("ropTemplateRevise");
        if (CommonUtility.hasValue(hisTasks)) {
            List<ROPReviseTaskVo> ropTemplateRevise = CommonUtility.toJSONObjList(hisTasks).stream().map(ROPReviseTaskVo::new).collect(Collectors.toList());
            if (configurationItem != null) {
                ropTemplateRevise = ropTemplateRevise.stream().filter(r -> configurationItem.UID().equalsIgnoreCase(r.getConfig())).collect(Collectors.toList());
            }
            ropTemplateRevise = ropTemplateRevise.stream().sorted(Comparator.comparing(ROPReviseTaskVo::getEndTime)).collect(Collectors.toList());
            Collections.reverse(ropTemplateRevise);
            List<List<ROPReviseTaskVo>> lists = CommonUtility.partitionList(ropTemplateRevise, pageRequest.getPageSize());
            if (CommonUtility.hasValue(lists)) {
                result.setResult(lists.get(pageRequest.getPageIndex() - 1));
                result.setTotal((int) Long.parseLong(ropTemplateRevise.size() + ""));
                result.setSuccess(true);
            }
        } else {
            result.setTotal(0);
            result.setResult(new ArrayList<>());
            result.setSuccess(true);
        }
    }

    /*
     * @param task                   task对象
     * @param pcolROPRuleGroups      同个对象定义下的ROP规则组
     * @param pstrClassDefinitionUID 生效的对象定义
     * @Description: 处理ROP规则组升版
     * @Return: ccm.server.schema.collections.IObjectCollection
     * @Author: Chen Jing
     * @Date: 2022/5/10 16:08:22
     */
    private IObjectCollection handleROPRuleGroupsRevise(ROPTemplateReviseTask task, IObjectCollection pcolROPRuleGroups, String pstrClassDefinitionUID) throws Exception {
        //根据生效的对象类型定义,查询出所有设计对象
        IObjectCollection lcolDesignObjects = SchemaUtility.getObjectsByClassDef(pstrClassDefinitionUID);
        if (SchemaUtility.hasValue(lcolDesignObjects)) {
            IObjectCollection lcolContainer = new ObjectCollection();
            //循环处理每一个设计对象的工作步骤信息
            Iterator<IObject> e = lcolDesignObjects.GetEnumerator();
            int count = 1;
            while (e.hasNext()) {
                IROPExecutableItem lobjDesignObj = e.next().toInterface(IROPExecutableItem.class);
                ROPUtils.setROPTemplateReviseProcessingInfo(task, "开始处理第" + count + "个设计对象,名称:" + lobjDesignObj.Name() + ",共" + lcolDesignObjects.size() + "个,请稍后...");
                IObjectCollection stepObjects = lobjDesignObj.generateWorkStepObjects(false, pcolROPRuleGroups);
                if (SchemaUtility.hasValue(stepObjects)) {
                    lcolContainer.addRangeUniquely(stepObjects);
                }
                count++;
            }
            return lcolContainer;
        }
        return null;
    }

    /*
     * @Descriptions :  删除对象已经关联
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:13
     * @param pobjEnd1 1端对象
     * @param pstrRelDef  关联关系定义
     * @Return:void
     */
    private void deleteRelAndObject(IObject pobjEnd1, String pstrRelDef) throws Exception {
        IRelCollection rels = pobjEnd1.GetEnd1Relationships().GetRels(pstrRelDef);
        if (SchemaUtility.hasValue(rels)) {
            Iterator<IObject> e = rels.GetEnumerator();
            while (e.hasNext()) {
                IRel lobjRel = e.next().toInterface(IRel.class);
                IObject lobj = lobjRel.GetEnd2();
                lobjRel.Delete();
                lobj.Delete();
            }
        }
    }

    @Override
    public IObjectCollection getWorkStepsForByDocument(IObject document) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (document != null) {
            StopWatch stopWatch = PerformanceUtility.start();
            IObjectCollection collection = document.GetEnd1Relationships().GetRels(relDefinitionType.CCMDocument2DesignObj.toString()).GetEnd2s().GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
            if (collection != null)
                result.addRange(collection);
            log.info("complete to get work step for document " + document.toErrorPop() + ": " + result.size() + PerformanceUtility.stop(stopWatch));
        }
        return result;
    }

    @Override
    public IObjectCollection getWorkStepsForDesignObject(IObject designObject) throws Exception {
        if (designObject != null) {
            StopWatch stopWatch = PerformanceUtility.start();
            IObjectCollection workSteps = designObject.GetEnd1Relationships().GetRels(relDefinitionType.CCMDesignObj2WorkStep.toString()).GetEnd2s();
            log.info("complete to get work step for design object " + designObject.toErrorPop() + ": " + workSteps.size() + PerformanceUtility.stop(stopWatch));
            return workSteps;
        }
        return null;
    }

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /*
     * @Descriptions : 删除所有ROP模板信息,包括ROPGroup , ROPGroupItem
     * @Author: Chen Jing
     * @Date: 2022/4/24 13:48
     * @param null
     * @Return:
     */
    @Override
    public void deleteAllROPTemplateInfo() throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addClassDefForQuery(queryRequest, classDefinitionType.ROPRuleGroup.toString());
        IObjectCollection lcolROPRuleGroups = CIMContext.Instance.QueryEngine().query(queryRequest);
        if (SchemaUtility.hasValue(lcolROPRuleGroups)) {
            SchemaUtility.beginTransaction();
            Iterator<IObject> e = lcolROPRuleGroups.GetEnumerator();
            while (e.hasNext()) {
                IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                ruleGroup.deleteItemsAndStep();
                ruleGroup.Delete();
            }
            SchemaUtility.commitTransaction();
        }
        this.refreshROPCache();
    }

    /*
     * @Descriptions : 通过ROP模板加载ROP信息
     * @Author: Chen Jing
     * @Date: 2022/4/24 13:46
     * @param file 模板文件
     * @Return:
     */
    @Override
    public synchronized void loadROPTemplateIntoSystem(@NotNull MultipartFile file) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        JSONObject jsonObject = ROPUtils.converterExcelFileToJSONObject(file);
        stopWatch.stop();
        log.info("解析Excel用时{}", stopWatch.getLastTaskTimeMillis() / 1000 + "s");
        stopWatch.start();
        //update:2022/4/24 ROP更新需要变更ROP,所以不在删除所有ROP信息,现在根据模板及系统里已有的ROP信息判断模板信息的变更状态
        this.validateROPTemplateInfoIsCorrectWithJSONObj(jsonObject);
        stopWatch.stop();
        log.info("校验数据用时{}", stopWatch.getLastTaskTimeMillis() / 1000 + "s");
//        stopWatch.start();
//        this.deleteAllROPTemplateInfo();
//        stopWatch.stop();
//        log.info("删除旧数据耗时{}", stopWatch.getLastTaskTimeMillis() / 1000 + "s");
        //update:2022/4/24 不在调用通用创建方法,重写专用创建ROP信息的方法
        stopWatch.start();
        // this.schemaBusinessService.loadObjectsByJSONObject(jsonObject);
        this.loadROPInfoIntoSystem(jsonObject);
        stopWatch.stop();
        log.info("导入ROP数据{}", stopWatch.getLastTaskTimeMillis() / 1000 + "s");

        //刷新缓存
        this.refreshROPCache();
    }

    /*
     * @Descriptions : 专用加载ROP模板信息方法
     * @Author: Chen Jing
     * @Date: 2022/4/24 14:07
     * @param jsonObject 解析到的ROP模板信息
     * @Return:void
     */
    private void loadROPInfoIntoSystem(JSONObject jsonObject) throws Exception {
        JSONArray jsonArray = jsonObject.getJSONArray(CommonUtility.JSON_FORMAT_ITEMS);
        if (jsonArray != null && jsonArray.size() > 0) {
            //根据UID 查询系统已经存在的对象ROPWorkStep略过  因为UID 没有生成规则,为GUID
            IObjectCollection lcolSystemExistObjects = SchemaUtility.queryExistObjectsFormJSONArray(jsonArray, new ArrayList<String>() {{
                add(classDefinitionType.ROPWorkStep.toString());
            }});
            //先将对象按照ClassDefinitionUID分组
            List<JSONObject> lcolJsonObjs = CommonUtility.toJSONObjList(jsonArray);
            Map<String, List<JSONObject>> groupByClassDef = lcolJsonObjs.stream().collect(Collectors.groupingBy(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID)));
            //获取所有ROPGroup
            List<JSONObject> lcolROPGroups = groupByClassDef.get(classDefinitionType.ROPRuleGroup.toString());
            //获取所有ROPGroupItems
            List<JSONObject> lcolTemplateROPGroupItems = groupByClassDef.get(classDefinitionType.ROPRuleGroupItem.toString());
            //获取所有ROPWorkStep
            List<JSONObject> lcolROPWorkSteps = groupByClassDef.get(classDefinitionType.ROPWorkStep.toString());
            //获取所有关联关系
            List<JSONObject> lcolRels = groupByClassDef.get(classDefinitionType.Rel.toString());
            //处理ROPGroup
            //根据ROPGroupClassDefinitionUID 分组
            SchemaUtility.beginTransaction();
            Map<String, List<JSONObject>> groupByClassDefUID = lcolROPGroups.stream().collect(Collectors.groupingBy(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPGroupClassDefinitionUID.toString())));
            for (Map.Entry<String, List<JSONObject>> entry : groupByClassDefUID.entrySet()) {
                String lstrROPGroupClassDefinitionUID = entry.getKey();
                //获取使用同样ClassDefUID 建立的ROPGroups
                IObjectCollection lcolSameTargetClassDefGroups = ROPUtils.getROPGroupByROPGroupClassDefinitionUID(lstrROPGroupClassDefinitionUID, false);
                List<Integer> lcolExistOrders = ROPUtils.getROPGroupOrders(lcolSameTargetClassDefGroups);
                for (JSONObject ropGroupJSON : entry.getValue()) {
                    String lstrROPGroupUID = SchemaUtility.getSpecialPropertyValue(ropGroupJSON, propertyDefinitionType.UID.toString());
                    String lstrROPGroupName = SchemaUtility.getSpecialPropertyValue(ropGroupJSON, propertyDefinitionType.Name.toString());
                    String lstrROPGroupDesc = SchemaUtility.getSpecialPropertyValue(ropGroupJSON, propertyDefinitionType.Description.toString());
                    Integer lintROPGroupOrder = Double.valueOf(SchemaUtility.getSpecialPropertyValue(ropGroupJSON, propertyDefinitionType.ROPGroupOrder.toString())).intValue();
                    IObject lobjROPGroupCreated = this.createOrUpdateROPRuleGroupFromTemplate(ropGroupJSON, lcolSystemExistObjects, lcolExistOrders, lstrROPGroupUID, lstrROPGroupName, lstrROPGroupDesc, lintROPGroupOrder, lstrROPGroupClassDefinitionUID);
                    //获取ROPGroup模板中关联的Item和WorkStep
                    List<String> lcolRelatedItemUIDs = ROPUtils.getROPGroupRelatedItemUIDsFromTemplateData(lstrROPGroupUID, lcolRels, relDirection._1To2);
                    if (CommonUtility.hasValue(lcolRelatedItemUIDs)) {
                        //获取关联的GroupItems
                        List<JSONObject> lcolRelatedTemplateROPGroupItems = lcolTemplateROPGroupItems.stream().filter(r -> lcolRelatedItemUIDs.contains(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID.toString()))).collect(Collectors.toList());
                        //处理GroupItems
                        handleROPGRuleGroupItems(lcolSystemExistObjects, lstrROPGroupName, lobjROPGroupCreated, lcolRelatedTemplateROPGroupItems);
                        //获取关联的WorkStep
                        List<JSONObject> lcolRelatedTemplateWorkSteps = lcolROPWorkSteps.stream().filter(r -> lcolRelatedItemUIDs.contains(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID.toString()))).collect(Collectors.toList());
                        if (!CommonUtility.hasValue(lcolRelatedTemplateWorkSteps)) {
                            throw new Exception("未找到规则组关联的步骤模板信息!规则组名称:" + lstrROPGroupName);
                        }
                        handleROPWorkSteps(lobjROPGroupCreated, lcolRelatedTemplateWorkSteps);
                    }
                }
            }
            SchemaUtility.commitTransaction();
        }
    }

    /*
     * @Descriptions : 处理工步的生成
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:14
     * @param pobjROPGroupCreated 已经创建的规则组
     * @param pcolRelatedTemplateWorkSteps  模板中关联的工步对象
     * @Return:void
     */
    private void handleROPWorkSteps(IObject pobjROPGroupCreated, List<JSONObject> pcolRelatedTemplateWorkSteps) throws Exception {
        //获取已经关联的工作步骤信息
        IObjectCollection lcolSystemHasRelatedWorkSteps = pobjROPGroupCreated.toInterface(IROPRuleGroup.class).getROPWorkSteps();
        if (!SchemaUtility.hasValue(lcolSystemHasRelatedWorkSteps)) {
            //没有关联的工作步骤,则根据模板信息创建新的工作步骤并关联
            for (JSONObject ropWorkStepJSON : pcolRelatedTemplateWorkSteps) {
                createROPWorkStepAndRelationshipByTemplateJSONObj(pobjROPGroupCreated, ropWorkStepJSON);
                //更新规则组状态
                pobjROPGroupCreated.BeginUpdate();
                pobjROPGroupCreated.toInterface(IROPRuleGroup.class).setROPGroupWorkStepRevState(ropRevState.EN_Created_ROPTemplate.toString());
                pobjROPGroupCreated.FinishUpdate();
            }
        } else {
            //获取所有模板中的步骤生成的规则串
            List<String> lcolTemplateROPWSRuleJSON = new ArrayList<>();
            for (JSONObject templateROPWS : pcolRelatedTemplateWorkSteps) {
                lcolTemplateROPWSRuleJSON.add(ROPUtils.generateRuleJSONForWorkStepJSONObjStr(templateROPWS));
            }
            boolean flag = false;
            //存在已经关联的工作步骤,遍历判断步骤属性是否一致 不一致的删除
            Iterator<IObject> e = lcolSystemHasRelatedWorkSteps.GetEnumerator();
            while (e.hasNext()) {
                IROPWorkStep lobjSystemROPWS = e.next().toInterface(IROPWorkStep.class);
                Map<String, Object> ruleJSON = lobjSystemROPWS.generateIdentity();
                if (lcolTemplateROPWSRuleJSON.stream().noneMatch(r -> JSON.parse(r).equals(JSON.parse(JSON.toJSONString(ruleJSON))))) {
                    //新的模板中没有该种规则的步骤,则删除
                    lobjSystemROPWS.Delete();
                    flag = true;
                }
            }
            //遍历模板中关联的工作步骤,如果不存在,就创建
            for (int i = 0; i < pcolRelatedTemplateWorkSteps.size(); i++) {
                JSONObject ropWSTemplate = pcolRelatedTemplateWorkSteps.get(i);
                String ruleJSON = lcolTemplateROPWSRuleJSON.get(i);
                if (!ROPUtils.containsByRuleJSON(lcolSystemHasRelatedWorkSteps, ruleJSON)) {
                    //不存在相同规则串 ,则创建
                    flag = true;
                    createROPWorkStepAndRelationshipByTemplateJSONObj(pobjROPGroupCreated, ropWSTemplate);
                }
            }
            //更新规则组状态
            IROPRuleGroup iropRuleGroup = pobjROPGroupCreated.toInterface(IROPRuleGroup.class);
            iropRuleGroup.setROPGroupStatusByWorkStepChanged(flag, true);

        }
    }

    /*
     * @Descriptions : 创建工步以及关联关系(通过模板解析的JSON对象)
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:15
     * @param pobjROPGroupCreated 创建或已经存在的规则组对象
     * @param ropWorkStepJSON 工步的JSON对象
     * @Return:void
     */
    private void createROPWorkStepAndRelationshipByTemplateJSONObj(IObject pobjROPGroupCreated, JSONObject ropWorkStepJSON) throws Exception {
        String lstrTemplateWorkStepName = SchemaUtility.getSpecialPropertyValue(ropWorkStepJSON, propertyDefinitionType.Name.toString());
        String lstrTemplateWorkStepDesc = SchemaUtility.getSpecialPropertyValue(ropWorkStepJSON, propertyDefinitionType.Description.toString());
        IObject lobjCreatedROPWorkStep = SchemaUtility.newIObject(classDefinitionType.ROPWorkStep.toString(), lstrTemplateWorkStepName, lstrTemplateWorkStepDesc, null, null);
        if (lobjCreatedROPWorkStep == null)
            throw new Exception("创建ROP工作步骤失败!名称:" + lstrTemplateWorkStepName);
        lobjCreatedROPWorkStep.fillingInterfaces(ropWorkStepJSON);
        lobjCreatedROPWorkStep.fillingProperties(ropWorkStepJSON);
        lobjCreatedROPWorkStep.ClassDefinition().FinishCreate(lobjCreatedROPWorkStep);
        //创建关联
        SchemaUtility.createRelationShip(relDefinitionType.ROPRuleGroup2ROPWorkStep.toString(), pobjROPGroupCreated, lobjCreatedROPWorkStep, false);
    }

    /*
     * @Descriptions : 处理规则组条目创建或更新
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:16
     * @param pcolSystemExistObjects 系统已经存在的对象
     * @param pstrROPGroupName  规则组名称
     * @param pobjROPGroupCreated   创建或更新的规则组对象
     * @param pcolRelatedTemplateROPGroupItems  模板中关联的规则组条目对象信息
     * @Return:void
     */
    private void handleROPGRuleGroupItems(IObjectCollection pcolSystemExistObjects, String pstrROPGroupName, IObject pobjROPGroupCreated, List<JSONObject> pcolRelatedTemplateROPGroupItems) throws Exception {
        if (CommonUtility.hasValue(pcolRelatedTemplateROPGroupItems)) {
            IROPRuleGroup ruleGroup = pobjROPGroupCreated.toInterface(IROPRuleGroup.class);
            //获取已经关联的条目 如果是新建 则已经关联的条目为空
            IObjectCollection lcolHasRelatedSystemROPGroupItems = ruleGroup.getItems();
            if (!SchemaUtility.hasValue(lcolHasRelatedSystemROPGroupItems)) {
                //如果没有已经关联的条目,则创建
                for (JSONObject ropTemplateGroupItemJSON : pcolRelatedTemplateROPGroupItems) {
                    String lstrROPGroupItemUID = SchemaUtility.getSpecialPropertyValue(ropTemplateGroupItemJSON, propertyDefinitionType.UID.toString());
                    String lstrROPGroupItemName = SchemaUtility.getSpecialPropertyValue(ropTemplateGroupItemJSON, propertyDefinitionType.Name.toString());
                    String lstrROPGroupItemDesc = SchemaUtility.getSpecialPropertyValue(ropTemplateGroupItemJSON, propertyDefinitionType.Description.toString());
                    if (SchemaUtility.hasValue(pcolSystemExistObjects) && pcolSystemExistObjects.contains(lstrROPGroupItemUID, domainInfo.CCMROPCONDIFS.toString())) {
                        throw new Exception("规则组条目:" + lstrROPGroupItemName + "已经在系统中存在,且不属于当前模板中提供的规则组:" + pstrROPGroupName + ",请检查修改后重新导入!");
                    }
                    //创建新的规则组
                    IObject lobjCreatedROPGroupItem = SchemaUtility.newIObject(classDefinitionType.ROPRuleGroupItem.toString(), lstrROPGroupItemName, lstrROPGroupItemDesc, null, lstrROPGroupItemUID);
                    if (lobjCreatedROPGroupItem == null)
                        throw new Exception("创建规则组条目失败,名称:" + lstrROPGroupItemName);
                    lobjCreatedROPGroupItem.fillingInterfaces(ropTemplateGroupItemJSON);
                    lobjCreatedROPGroupItem.fillingProperties(ropTemplateGroupItemJSON);
                    lobjCreatedROPGroupItem.ClassDefinition().FinishCreate(lobjCreatedROPGroupItem);
                    //创建关联
                    SchemaUtility.createRelationShip(relDefinitionType.ROPRuleGroup2Item.toString(), pobjROPGroupCreated, lobjCreatedROPGroupItem, false);
                }
                //更新ROPGroup状态

                IROPRuleGroup iropRuleGroup = pobjROPGroupCreated.toInterface(IROPRuleGroup.class);
                iropRuleGroup.BeginUpdate();
                iropRuleGroup.setROPGroupItemRevState(ropRevState.EN_Created_ROPTemplate.toString());
                iropRuleGroup.setROPHasHandleChange(false);
                iropRuleGroup.FinishUpdate();
            } else {
                //系统已经有关联了的条目信息 遍历模板关联的条目信息 用于创建新的条目项与规则组的关联
                //记录条目对象是否有变化
                boolean flag = false;
                for (JSONObject ropGroupItemJSON : pcolRelatedTemplateROPGroupItems) {
                    String lstrROPGroupItemUID = SchemaUtility.getSpecialPropertyValue(ropGroupItemJSON, propertyDefinitionType.UID.toString());
                    String lstrROPGroupItemName = SchemaUtility.getSpecialPropertyValue(ropGroupItemJSON, propertyDefinitionType.Name.toString());
                    String lstrROPGroupItemDesc = SchemaUtility.getSpecialPropertyValue(ropGroupItemJSON, propertyDefinitionType.Description.toString());
                    //判断条目是否已经在系统中存在
                    IObject lobjExistSystemROPGroupItem = SchemaUtility.hasValue(pcolSystemExistObjects) && pcolSystemExistObjects.contains(lstrROPGroupItemUID, domainInfo.CCMROPCONDIFS.toString()) ? pcolSystemExistObjects.item(lstrROPGroupItemUID, domainInfo.CCMROPCONDIFS.toString()) : null;
                    if (lobjExistSystemROPGroupItem == null) {
                        //不存在,则新建,创建关联 那已经关联的条目中肯定也不存在
                        lobjExistSystemROPGroupItem = SchemaUtility.newIObject(classDefinitionType.ROPRuleGroupItem.toString(), lstrROPGroupItemName, lstrROPGroupItemDesc, null, lstrROPGroupItemUID);
                        if (lobjExistSystemROPGroupItem == null)
                            throw new Exception("创建条目对象失败,名称:" + lstrROPGroupItemName);
                        lobjExistSystemROPGroupItem.fillingInterfaces(ropGroupItemJSON);
                        lobjExistSystemROPGroupItem.fillingProperties(ropGroupItemJSON);
                        lobjExistSystemROPGroupItem.ClassDefinition().FinishCreate(lobjExistSystemROPGroupItem);
                        SchemaUtility.createRelationShip(relDefinitionType.ROPRuleGroup2Item.toString(), pobjROPGroupCreated, lobjExistSystemROPGroupItem, false);
                        flag = true;
                    } else {
                        //如果存在,判断已经关联的条目中有没有该对象 ,如果没有,创建关联
                        IObject lobjSystemHasRelatedExistROPGroupItem = lcolHasRelatedSystemROPGroupItems.item(lstrROPGroupItemUID, domainInfo.CCMROPCONDIFS.toString());
                        if (lobjSystemHasRelatedExistROPGroupItem == null) {
                            SchemaUtility.createRelationShip(relDefinitionType.ROPRuleGroup2Item.toString(), pobjROPGroupCreated, lobjExistSystemROPGroupItem, false);
                            flag = true;
                        } else {
                            //已经被关联,判断对象的属性有没有发生变化
                            String lstrCalculatePropStr = ROPUtils.generateROPRuleGroupItemCompareStr(ropGroupItemJSON);
                            String lstrExistCalculatePropStr = lobjSystemHasRelatedExistROPGroupItem.toInterface(IROPRuleGroupItem.class).generateCalculatePropAndValueStr();
                            if (!lstrCalculatePropStr.equalsIgnoreCase(lstrExistCalculatePropStr)) {
                                //属性发生变化 需要创建对象保存旧 的属性信息, 更新已经关联的对象属性
                                //update 2022/4/29  取消创建对象保存旧的属性, 考虑在生成实际工步 时与规则组建立关联关系
                                flag = true;
                                lobjExistSystemROPGroupItem.BeginUpdate();
                                lobjExistSystemROPGroupItem.fillingInterfaces(ropGroupItemJSON);
                                lobjExistSystemROPGroupItem.fillingProperties(ropGroupItemJSON);
                                lobjExistSystemROPGroupItem.FinishUpdate();
                                //update 2022/4/29 取消创建关联
                            }
                        }
                    }
                }
                //遍历已经关联的条目项 ,用于删除新的条目中,不包含的旧条目项
                Iterator<IObject> e = lcolHasRelatedSystemROPGroupItems.GetEnumerator();
                while (e.hasNext()) {
                    IROPRuleGroupItem ruleGroupItem = e.next().toInterface(IROPRuleGroupItem.class);
                    //如果已经关联的条目UID 不在 模板提供的关联的条目项中,则删除
                    if (pcolRelatedTemplateROPGroupItems.stream().map(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.UID.toString())).noneMatch(r -> ruleGroupItem.UID().equalsIgnoreCase(r))) {
                        ruleGroupItem.Delete();
                        flag = true;
                    }
                }
                //更新ROPGroup的状态 如果标记还是false 说明ROPGroupItem没有改变 ,根据ROPGroup是否被更新过判断
                IROPRuleGroup iropRuleGroup = pobjROPGroupCreated.toInterface(IROPRuleGroup.class);
                iropRuleGroup.setROPGroupStatusByItemsChanged(flag, true);
            }
        } else {
            throw new Exception("未找到规则组关联的条目信息,规则组名称:" + pstrROPGroupName);
        }
    }

    /*
     * @Descriptions : 创建或更新规则组 (模板解析的JSON对象)
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:18
     * @param ropGroupJSON  待处理的规则组JSON对象
     * @param pcolExistObjects 系统中存在的对象
     * @param pcolExistOrders 系统存在的规则组顺序
     * @param pstrROPGroupUID 规则组对象根据UID 生成规则获取的UID
     * @param pstrROPGroupName 待处理规则组名称
     * @param pstrROPGroupDesc 待处理规则组描述
     * @param pintROPGroupOrder  待处理规则组顺序
     * @param pstrROPGroupClassDefinitionUID 待处理的规则组生效的类型定义
     * @Return:ccm.server.schema.interfaces.IObject
     */
    private IObject createOrUpdateROPRuleGroupFromTemplate(@NotNull JSONObject ropGroupJSON, IObjectCollection pcolExistObjects, List<Integer> pcolExistOrders, String pstrROPGroupUID, String pstrROPGroupName, String pstrROPGroupDesc, Integer pintROPGroupOrder, String pstrROPGroupClassDefinitionUID) throws Exception {
        //UID 在解析Excel已经根据UID 生成规则生成 没有UID生成规则的对象的UID为GUID
        //存在性判断
        IObject lobjROPGroup;
        if (SchemaUtility.hasValue(pcolExistObjects) && pcolExistObjects.contains(pstrROPGroupUID, domainInfo.CCMROPCONDIFS.toString())) {
            //已经存在,判断order是否一致   //order一致 不做任何操作
            lobjROPGroup = pcolExistObjects.item(pstrROPGroupUID, domainInfo.CCMROPCONDIFS.toString());
            if (!Objects.equals(pintROPGroupOrder, lobjROPGroup.toInterface(IROPRuleGroup.class).ROPGroupOrder())) {
                //order不一致,判断order 是否重复,在同样TargetCLassDefUID 的ROPGroup中
                pcolExistOrders = checkOrderRepeatability(pstrROPGroupClassDefinitionUID, pcolExistOrders, pintROPGroupOrder);
                lobjROPGroup.BeginUpdate();
                IROPRuleGroup iropRuleGroup = lobjROPGroup.toInterface(IROPRuleGroup.class);
                iropRuleGroup.setROPGroupOrder(pintROPGroupOrder);
                lobjROPGroup.FinishUpdate();
            }
        } else {
            //不存在,新建
            //判断Order重复性
            pcolExistOrders = checkOrderRepeatability(pstrROPGroupClassDefinitionUID, pcolExistOrders, pintROPGroupOrder);
            lobjROPGroup = SchemaUtility.newIObject(classDefinitionType.ROPRuleGroup.toString(), pstrROPGroupName, pstrROPGroupDesc, null, pstrROPGroupUID);
            if (lobjROPGroup == null) throw new Exception("创建ROPGroup失败!名称:" + pstrROPGroupName);
            lobjROPGroup.fillingInterfaces(ropGroupJSON);
            lobjROPGroup.fillingProperties(ropGroupJSON);
            lobjROPGroup.toInterface(IROPRuleGroup.class).setROPInitStatus(false);
            lobjROPGroup.ClassDefinition().FinishCreate(lobjROPGroup);
        }
        return lobjROPGroup;
    }

    /*
     * @Description: 检查顺序重复性
     * @param lstrROPGroupClassDefinitionUID
     * @param lcolExistOrders
     * @param lintROPGroupOrder
     * @Return: java.util.List<java.lang.Integer>
     * @Author: Chen Jing
     * @Date: 2022/5/25 09:41:26
     */
    @NotNull
    private List<Integer> checkOrderRepeatability(String lstrROPGroupClassDefinitionUID, List<Integer> lcolExistOrders, Integer lintROPGroupOrder) throws Exception {
        if (lcolExistOrders != null) {
            if (lcolExistOrders.contains(lintROPGroupOrder)) {
                //已经存在
                throw new Exception("序号:" + lintROPGroupOrder + "在ClassDefUID:" + lstrROPGroupClassDefinitionUID + "的ROP规则组已经存在!");
            } else {
                //不存在,更新order属性
                lcolExistOrders.add(lintROPGroupOrder);
            }
        } else {
            lcolExistOrders = new ArrayList<>();
            lcolExistOrders.add(lintROPGroupOrder);

        }
        return lcolExistOrders;
    }

    /*
     * @Descriptions : 校验ROP模板信息有效性
     * @Author: Chen Jing
     * @Date: 2022/4/24 13:47
     * @param ropTemplateInfo rop模板所有包含的对象信息
     * @Return:
     */
    private void validateROPTemplateInfoIsCorrectWithJSONObj(@NotNull JSONObject ropTemplateInfo) throws Exception {
        //获取所有对象
        JSONArray items = ropTemplateInfo.getJSONArray(ROPUtils.JSON_TEMPLATE_ITEMS);
        List<JSONObject> lcolObjs = CommonUtility.toJSONObjList(items);
        if (CommonUtility.hasValue(lcolObjs)) {
            //检验对象中所有的Schema信息
            validateSchemaInfo(lcolObjs);
            //获取ROP规则组对象集合
            List<JSONObject> lcolROPGroupJSONObjs = lcolObjs.stream().filter(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.ROPRuleGroup.toString())).collect(Collectors.toList());
            if (CommonUtility.hasValue(lcolROPGroupJSONObjs)) {
                //校验必要属性
                validateROPGroupRequiredProps(lcolROPGroupJSONObjs);
                //按照规则组的对象定义类型分组
                validateROPGroupOrderAndName(lcolROPGroupJSONObjs);
            } else {
                throw new Exception("未解析到规则组信息!");
            }
            List<JSONObject> lcolROPGroupItems = lcolObjs.stream().filter(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.ROPRuleGroupItem.toString())).collect(Collectors.toList());
            if (CommonUtility.hasValue(lcolROPGroupItems)) {
                validateROPGroupItemName(lcolROPGroupItems);
            } else {
                throw new Exception("未解析到规则组条目信息!");
            }
            List<JSONObject> lcolWorkSteps = lcolObjs.stream().filter(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.ROPWorkStep.toString())).collect(Collectors.toList());
            if (CommonUtility.hasValue(lcolWorkSteps)) {
                //校验放行步骤是否设置了材料下发方式
                validateStepConsumeMaterialIndMaterialIssueMode(lcolWorkSteps);
                //校验步骤的计算属性定义,是否设置的正确 , 可以为空  可以是属性定义 可以是数字
                validateCalculatePropDefIsCorrect(lcolWorkSteps);
                //校验步骤的枚举类型值是否在系统中已经定义
                validateEnumListTypeValueIsCorrect(lcolWorkSteps);
                //校验步骤顺序是否有值
                validateStepOrderHasValue(lcolWorkSteps);
                //校验步骤的生成模式
                validateWorkStepGenerateMode(lcolWorkSteps);
            }
            List<JSONObject> lcolRels = lcolObjs.stream().filter(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID).equalsIgnoreCase(classDefinitionType.Rel.toString())).collect(Collectors.toList());
            if (!CommonUtility.hasValue(lcolRels)) {
                throw new Exception("未解析到关联信息!");
            }
        }
    }

    private void validateWorkStepGenerateMode(List<JSONObject> pcolWorkSteps) throws Exception {
        if (pcolWorkSteps.stream().anyMatch(r -> StringUtils.isEmpty(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPWorkStepGenerateMode.toString())))) {
            throw new Exception("步骤的生成模式没有给出!");
        }
    }

    private void validateStepOrderHasValue(List<JSONObject> pcolWorkSteps) throws Exception {
        if (pcolWorkSteps.stream().anyMatch(r -> StringUtils.isEmpty(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPWorkStepOrderValue.toString()))))
            throw new Exception("有步骤顺序值为空的步骤存在,请检查!");
    }



    /*
     * @Descriptions : 验证对象中的Schema信息
     * @Author: Chen Jing
     * @Date: 2022/4/26 10:19
     * @param pcolObjs  对象集合 JSON格式
     * @Return:
     */

    private void validateSchemaInfo(List<JSONObject> pcolObjs) throws Exception {
        List<String[]> interfaces = pcolObjs.stream().map(SchemaUtility::parseInterfaces).collect(Collectors.toList());
        List<List<Map.Entry<String, Object>>> properties = pcolObjs.stream().map(SchemaUtility::parseProperties).collect(Collectors.toList());
        List<String> classDefs = pcolObjs.stream().map(r -> r.getString(CommonUtility.JSON_FORMAT_CLASS_DEFINITION_UID)).collect(Collectors.toList());
        List<String> lcolInterfaces = new ArrayList<>();
        for (String[] arr : interfaces) {
            lcolInterfaces.addAll(Arrays.asList(arr));
        }
        SchemaUtility.verifyInterfaces(lcolInterfaces.stream().distinct().collect(Collectors.toList()));
        List<String> lcolProperties = new ArrayList<>();
        for (List<Map.Entry<String, Object>> entries : properties) {
            lcolProperties.addAll(entries.stream().map(Map.Entry::getKey).collect(Collectors.toList()));
        }
        SchemaUtility.verifyProperties(lcolProperties.stream().distinct().collect(Collectors.toList()));
        SchemaUtility.verifyClassDefs(classDefs);
    }

    /*
     * @Descriptions : 验证规则组条目的有效性
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:20
     * @param pcolROPGroupItems
     * @Return:void
     */
    private void validateROPGroupItemName(List<JSONObject> pcolROPGroupItems) throws Exception {
        Map<String, List<JSONObject>> collect = pcolROPGroupItems.stream().collect(Collectors.groupingBy(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.Name.toString())));
        List<Map.Entry<String, List<JSONObject>>> entryList = collect.entrySet().stream().filter(r -> r.getValue().size() > 1).collect(Collectors.toList());
        if (CommonUtility.hasValue(entryList)) {
            throw new Exception("存在同名规则组条目:" + entryList.get(0).getKey());
        }
    }

    /*
     * @Descriptions : 验证规则组名称以及顺序
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:20
     * @param pcolROPGroupJSONObjs
     * @Return:
     */
    private void validateROPGroupOrderAndName(List<JSONObject> pcolROPGroupJSONObjs) throws Exception {
        Map<String, List<JSONObject>> groupByClassDefUID = pcolROPGroupJSONObjs.stream().collect(Collectors.groupingBy(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPGroupClassDefinitionUID.toString())));
        for (Map.Entry<String, List<JSONObject>> entry : groupByClassDefUID.entrySet()) {
            String lstrClassDefUID = entry.getKey();
            List<JSONObject> lcolROPGroups = entry.getValue();
            //一个组别中组别中的排序号有没有重
            Map<String, List<JSONObject>> groupByOrder = lcolROPGroups.stream().collect(Collectors.groupingBy(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPGroupOrder.toString())));
            Optional<List<JSONObject>> first = groupByOrder.values().stream().filter(r -> r.size() > 1).findFirst();
            if (first.isPresent()) {
                throw new Exception("指定的类型定义:" + lstrClassDefUID + "的规则组中,有重复的顺序号:" + SchemaUtility.getSpecialPropertyValue(first.get().get(0), propertyDefinitionType.ROPGroupOrder.toString()));
            }
            Map<String, List<JSONObject>> groupByName = lcolROPGroups.stream().collect(Collectors.groupingBy(r -> SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.Name.toString())));
            if (groupByName.values().stream().anyMatch(r -> r.size() > 1)) {
                throw new Exception("指定的类型定义:" + lstrClassDefUID + "的规则组中,有重复的名称!");
            }
        }
    }

    /*
     * @Descriptions : 验证规则组必要的属性信息
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:20
     * @param pcolROPGroupJSONObjs
     * @Return:
     */
    private void validateROPGroupRequiredProps(List<JSONObject> pcolROPGroupJSONObjs) throws Exception {
        if (pcolROPGroupJSONObjs.stream().anyMatch(r -> StringUtils.isEmpty(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPGroupClassDefinitionUID.toString())))) {
            throw new Exception("规则组属性:" + propertyDefinitionType.ROPGroupClassDefinitionUID + " 不能为空值!");
        }
        if (pcolROPGroupJSONObjs.stream().anyMatch(r -> StringUtils.isEmpty(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPGroupOrder.toString())))) {
            throw new Exception("规则组属性:" + propertyDefinitionType.ROPGroupOrder + " 不能为空值!");
        }
    }

    /*
     * @Descriptions : 验证枚举类型的值是否有效
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:21
     * @param pcolWorkSteps
     * @Return:
     */
    private void validateEnumListTypeValueIsCorrect(List<JSONObject> pcolWorkSteps) throws Exception {
        for (JSONObject workStep : pcolWorkSteps) {
            ROPUtils.validateWorkStepEnumListTypePropValue(workStep);
        }
    }

    /*
     * @Descriptions : 验证工步的计算属性是否有效
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:21
     * @param pcolWorkSteps
     * @Return:
     */
    private void validateCalculatePropDefIsCorrect(List<JSONObject> pcolWorkSteps) throws Exception {
        List<JSONObject> lcolROPWorkStepWeightCalculateProperty = pcolWorkSteps.stream().filter(r -> !StringUtils.isEmpty(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPWorkStepWeightCalculateProperty.toString()))).collect(Collectors.toList());
        if (CommonUtility.hasValue(lcolROPWorkStepWeightCalculateProperty)) {
            for (JSONObject jsonObject : lcolROPWorkStepWeightCalculateProperty) {
                ROPUtils.validateCalculatePropDef(jsonObject);
            }
        }
    }

    /*
     * @Descriptions : 验证材料下发方式的有效性
     * @Author: Chen Jing
     * @Date: 2022/4/29 9:21
     * @param pcolWorkSteps
     * @Return:
     */
    private void validateStepConsumeMaterialIndMaterialIssueMode(List<JSONObject> pcolWorkSteps) throws Exception {
        List<JSONObject> lcolStepConsumeMaterialInd = pcolWorkSteps.stream().filter(r -> ROPUtils.checkBooleanTypeProp(r, propertyDefinitionType.ROPWorkStepConsumeMaterialInd.toString())).collect(Collectors.toList());
        if (CommonUtility.hasValue(lcolStepConsumeMaterialInd)) {
            JSONObject nullMaterialStep = lcolStepConsumeMaterialInd.stream().filter(r -> StringUtils.isEmpty(SchemaUtility.getSpecialPropertyValue(r, propertyDefinitionType.ROPWorkStepMaterialIssue.toString()))).findFirst().orElse(null);
            if (nullMaterialStep != null) {
                throw new Exception("ROP步骤:" + SchemaUtility.getSpecialPropertyValue(nullMaterialStep, propertyDefinitionType.Name.toString()) + ",没有设置材料下发模式, 消耗材料的步骤必须要设置材料下发模式");
            }
        }
    }

    /*
     * @Descriptions : 创建或更新ROPGroupItem
     * @Author: Chen Jing
     * @Date: 2022/4/24 13:49
     * @param pstrProperties
     * @param pstrROPGroupOBID
     * @Return:ccm.server.dto.base.ObjectDTO
     */
    @Override
    public ObjectDTO createOUpdateROPRuleGroupItemWithObjectDTOStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception {
        IObject lobjROPGroupItem = this.createOUpdateROPRuleGroupItemWithIObjectStyle(pstrProperties, pstrROPGroupOBID);
        ROPCache.Instance.refreshROPGroupItemCacheWhenCreateOrUpdate(lobjROPGroupItem, pstrROPGroupOBID);
        return lobjROPGroupItem != null ? lobjROPGroupItem.toObjectDTO() : null;
    }

    /*
     * @Descriptions :  创建或更新ROPGroupItem
     * @Author: Chen Jing
     * @Date: 2022/4/24 13:49
     * @param pstrProperties
     * @param pstrROPGroupOBID
     * @Return:ccm.server.schema.interfaces.IObject
     */
    @Override
    public IObject createOUpdateROPRuleGroupItemWithIObjectStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception {
        if (StringUtils.isEmpty(pstrProperties) || StringUtils.isEmpty(pstrROPGroupOBID)) {
            throw new Exception("传入的参数不能为空");
        }
        List<ObjectItemDTO> lcolObjsProps = CommonUtility.converterPropertiesToItemDTOList(pstrProperties);
        if (!CommonUtility.hasValue(lcolObjsProps)) throw new Exception("未解析到对象的属性信息!");
        Object lstrOBID = SchemaUtility.getSpecialPropValue(lcolObjsProps, propertyDefinitionType.OBID.toString());
        SchemaUtility.beginTransaction();
        IObject lobjROPGroupItem = this.schemaBusinessService.createOrUpdateObjectByProperties(lcolObjsProps, false);
        IObject lobjROPGroup = SchemaUtility.getObjectByOBIDAndClassDef(pstrROPGroupOBID, classDefinitionType.ROPRuleGroup.toString());
        if (lobjROPGroup == null)
            throw new Exception("未在系统中找到OBID:" + pstrROPGroupOBID + ",classDef:" + classDefinitionType.ROPRuleGroup + "的对象信息!");
        IRelCollection lcolRels = lobjROPGroup.GetEnd1Relationships().GetRels(relDefinitionType.ROPRuleGroup2Item.toString());
        if ((SchemaUtility.hasValue(lcolRels) && !lcolRels.containsUid2(lobjROPGroupItem.UID())) || !SchemaUtility.hasValue(lcolRels)) {
            SchemaUtility.createRelationShip(relDefinitionType.ROPRuleGroup2Item.toString(), lobjROPGroup, lobjROPGroupItem, false);
        }
        IROPRuleGroup ruleGroup = lobjROPGroup.toInterface(IROPRuleGroup.class);
        ruleGroup.BeginUpdate();
        if (StringUtils.isEmpty(lstrOBID)) {
            //新建
            ruleGroup.setROPGroupItemRevState(SchemaUtility.hasValue(lcolRels) ? ropRevState.EN_Updated_ROPTemplate.toString() : ropRevState.EN_Created_ROPTemplate.toString());
            ruleGroup.setROPHasHandleChange(false);
        } else {
            ruleGroup.setROPGroupStatusByItemsChanged(true, false);
        }
        ruleGroup.FinishUpdate();
        SchemaUtility.commitTransaction();
        return lobjROPGroupItem;
    }

    @Override
    public ObjectDTO createOUpdateROPWorkStepObjectDTOStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception {
        IObject lobjROPWorkStep = this.createOUpdateROPWorkStepObjectWithIObjectStyle(pstrProperties, pstrROPGroupOBID);
        ROPCache.Instance.refreshROPWorkStepCacheWhenCreateOrUpdate(lobjROPWorkStep, pstrROPGroupOBID);
        return lobjROPWorkStep != null ? lobjROPWorkStep.toObjectDTO() : null;
    }

    @Override
    public IObject createOUpdateROPWorkStepObjectWithIObjectStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception {
        if (StringUtils.isEmpty(pstrProperties) || StringUtils.isEmpty(pstrROPGroupOBID)) {
            throw new Exception("传入的参数不能为空");
        }
        List<ObjectItemDTO> lcolObjsProps = CommonUtility.converterPropertiesToItemDTOList(pstrProperties);
        if (!CommonUtility.hasValue(lcolObjsProps)) throw new Exception("未解析到对象的属性信息!");
        String lstrValidResult = ROPUtils.validateCalculatePropDef(lcolObjsProps);
        if (!StringUtils.isEmpty(lstrValidResult)) throw new Exception(lstrValidResult);
        Object lstrOBID = SchemaUtility.getSpecialPropValue(lcolObjsProps, propertyDefinitionType.OBID.toString());
        SchemaUtility.beginTransaction();
        IObject lobjROPWorkStep = this.schemaBusinessService.createOrUpdateObjectByProperties(lcolObjsProps, false);
        IObject lobjROPGroup = SchemaUtility.getObjectByOBIDAndClassDef(pstrROPGroupOBID, classDefinitionType.ROPRuleGroup.toString());
        if (lobjROPGroup == null)
            throw new Exception("未在系统中找到OBID:" + pstrROPGroupOBID + ",classDef:" + classDefinitionType.ROPRuleGroup + "的对象信息!");
        IRelCollection lcolRels = lobjROPGroup.GetEnd1Relationships().GetRels(relDefinitionType.ROPRuleGroup2ROPWorkStep.toString());
        if ((SchemaUtility.hasValue(lcolRels) && !lcolRels.containsUid2(lobjROPWorkStep.UID())) || !SchemaUtility.hasValue(lcolRels)) {
            SchemaUtility.createRelationShip(relDefinitionType.ROPRuleGroup2ROPWorkStep.toString(), lobjROPGroup, lobjROPWorkStep, false);
        }
        IROPRuleGroup ruleGroup = lobjROPGroup.toInterface(IROPRuleGroup.class);
        ruleGroup.BeginUpdate();
        if (StringUtils.isEmpty(lstrOBID)) {
            //新建
            ruleGroup.setROPGroupWorkStepRevState(SchemaUtility.hasValue(lcolRels) ? ropRevState.EN_Updated_ROPTemplate.toString() : ropRevState.EN_Created_ROPTemplate.toString());
            ruleGroup.setROPHasHandleChange(false);
        } else {
            ruleGroup.setROPGroupStatusByWorkStepChanged(true, false);
        }
        ruleGroup.FinishUpdate();
        SchemaUtility.commitTransaction();
        return lobjROPWorkStep;
    }

    @Override
    public ObjectDTO createOUpdateROPRuleGroupWithDTOStyle(String pstrProperties) throws Exception {
        IObject lobjROPGroupItem = this.createOUpdateROPRuleGroupWithIObjectStyle(pstrProperties);
        ROPCache.Instance.refreshROPRuleGroupCacheWhenCreateOrUpdate(lobjROPGroupItem);
        return lobjROPGroupItem != null ? lobjROPGroupItem.toObjectDTO() : null;
    }

    private final static Object syncObject = new Object();

    @Override
    public IObject createOUpdateROPRuleGroupWithIObjectStyle(String pstrProperties) throws Exception {
        if (StringUtils.isEmpty(pstrProperties)) throw new Exception("传入的对象属性参数不能为空!");
        List<ObjectItemDTO> lcolObjsProps = CommonUtility.converterPropertiesToItemDTOList(pstrProperties);
        if (!CommonUtility.hasValue(lcolObjsProps)) throw new Exception("未解析到对象的属性信息!");
        synchronized (syncObject) {
            IObject lobjROPGroup;
            Object lstrClassDef = SchemaUtility.getSpecialPropValue(lcolObjsProps, propertyDefinitionType.ROPGroupClassDefinitionUID.toString());
            Object lintGroupOrder = SchemaUtility.getSpecialPropValue(lcolObjsProps, propertyDefinitionType.ROPGroupOrder.toString());
            Object lstrDesc = SchemaUtility.getSpecialPropValue(lcolObjsProps, propertyDefinitionType.Description.toString());
            if (lintGroupOrder == null) throw new Exception("ROP规则组顺序不能为空!");
            Object lstrName = SchemaUtility.getSpecialPropValue(lcolObjsProps, propertyDefinitionType.Name.toString());
            if (lstrName == null) throw new Exception("ROP规则组的组名不能为空!");
            if (StringUtils.isEmpty(lstrClassDef))
                throw new Exception("未找到ROP规则组的必要属性信息:" + propertyDefinitionType.ROPGroupClassDefinitionUID);
            IObjectCollection lcolROPGroups = ROPUtils.getROPGroupByROPGroupClassDefinitionUID(lstrClassDef.toString(), false);
            if (SchemaUtility.hasValue(lcolROPGroups)) {
                IROPRuleGroup lobjSameName = null;
                IROPRuleGroup lobjSameOrder = null;
                Iterator<IObject> e = lcolROPGroups.GetEnumerator();
                while (e.hasNext()) {
                    IROPRuleGroup ruleGroup = e.next().toInterface(IROPRuleGroup.class);
                    Integer lintOrder = ruleGroup.ROPGroupOrder();
                    if (lintOrder == Integer.parseInt(lintGroupOrder.toString())) {
                        lobjSameOrder = ruleGroup;
                    }
                    if (lstrName.toString().equalsIgnoreCase(ruleGroup.Name())) {
                        lobjSameName = ruleGroup;
                    }
                }
                //已经存在该对象
                if (lobjSameName != null) {
                    //判断顺序
                    //该顺序不存在,直接更新
                    if (lobjSameOrder == null) {
                        SchemaUtility.beginTransaction();
                        lobjSameName.BeginUpdate();
                        lobjSameName.fillingProperties(lcolObjsProps, true);
                        lobjSameName.FinishUpdate();
                        SchemaUtility.commitTransaction();
                        lobjROPGroup = lobjSameName;
                    } else {
                        //存在相同顺序
                        //判断相同顺序的对象是不是该对象本身
                        if (lobjSameOrder.Name().equalsIgnoreCase(lobjSameName.Name())) {
                            SchemaUtility.beginTransaction();
                            lobjSameName.BeginUpdate();
                            lobjSameName.fillingProperties(lcolObjsProps, true);
                            lobjSameName.FinishUpdate();
                            SchemaUtility.commitTransaction();
                            lobjROPGroup = lobjSameName;
                        } else {
                            //序号存在,但是不是本身,不允许创建
                            throw new Exception("序号:" + lintGroupOrder + "已经存在!不能重复创建!");
                        }
                    }
                } else {
                    //该名程对象不存在,需要判断序号对象存在性
                    //序号存在,提示错误
                    if (lobjSameOrder != null) {
                        throw new Exception("序号:" + lintGroupOrder + "已经存在!不能重复创建!");
                    } else {
                        //直接创建
                        lobjROPGroup = createNewROPGroup(lcolObjsProps, lstrDesc, lstrName);
                    }
                }
            } else {
                //系统中还没有该ClassDef的规则组,直接创建
                lobjROPGroup = createNewROPGroup(lcolObjsProps, lstrDesc, lstrName);
            }
            return lobjROPGroup;
        }
    }

    private IObject createNewROPGroup(List<ObjectItemDTO> pcolObjsProps, Object pstrDesc, Object pstrName) throws Exception {
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        Objects.requireNonNull(configurationItem, "未找到项目信息");
        SchemaUtility.beginTransaction();
        IObject lobjROPGroup = SchemaUtility.newIObject(classDefinitionType.ROPRuleGroup.toString(), pstrName.toString(), pstrDesc != null ? pstrDesc.toString() : "", null, "RG_" + pstrName + "_" + configurationItem.UID());
        if (lobjROPGroup == null) throw new Exception("创建ROP规则组失败!");
        lobjROPGroup.fillingProperties(pcolObjsProps, false);
        lobjROPGroup.toInterface(IROPRuleGroup.class).setROPInitStatus(false);
        lobjROPGroup.ClassDefinition().FinishCreate(lobjROPGroup);
        SchemaUtility.commitTransaction();
        return lobjROPGroup;
    }


    /*
     * @Descriptions : 刷新ROP缓存
     * @Author: Chen Jing
     * @Date: 2022/4/24 17:22
     * @param null
     * @Return:
     */
    @Override
    public void refreshROPCache() {
        try {
            this.processCache.reInitialize();
            ROPCache.Instance.reInitialize();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
