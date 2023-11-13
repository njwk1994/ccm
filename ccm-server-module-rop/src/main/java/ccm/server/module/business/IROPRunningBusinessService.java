package ccm.server.module.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.model.FiltersParam;
import ccm.server.model.LoaderReport;
import ccm.server.model.OrderByParam;
import ccm.server.module.service.base.IInternalService;
import ccm.server.module.task.ROPTemplateReviseTask;
import ccm.server.module.vo.ROPReviseTaskVo;
import ccm.server.module.vo.ResultVo;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import org.jeecg.common.api.vo.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface IROPRunningBusinessService extends IInternalService {

    IObjectCollection getComponentCategoriesByConstructionType(IObject constructionType) throws Exception;

    IObjectCollection getROPGroupsByConstructionType(IObject constructionType) throws Exception;

    IObjectCollection getROPGroupItemsByROPGroup(IObject pobjROPRuleGroup);

    IObjectCollection getROPWorkStepTemplatesByROPRuleGroup(IObject ropRuleGroup) throws Exception;

    IObjectCollection generateWorkStepByDocument(IObject document) throws Exception;

    IObjectCollection generateWorkStepByDocument(IObject document, boolean transactionOrNot) throws Exception;

    IObjectCollection generateWorkStepByDocument(String documentOBID, String classDefinitionUID) throws Exception;

    IObjectCollection generateWorkStepForDesignObject(IObject designObject, IObjectCollection pcolExistObjects,String configName) throws Exception;

    IObjectCollection getWorkStepsForByDocument(IObject document) throws Exception;

    IObjectCollection getWorkStepsForDesignObject(IObject designObject) throws Exception;

    void deleteAllROPTemplateInfo() throws Exception;

    void loadROPTemplateIntoSystem(@NotNull MultipartFile file) throws Exception;

    ObjectDTO createOUpdateROPRuleGroupItemWithObjectDTOStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception;

    IObject createOUpdateROPRuleGroupItemWithIObjectStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception;

    ObjectDTO createOUpdateROPWorkStepObjectDTOStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception;

    IObject createOUpdateROPWorkStepObjectWithIObjectStyle(String pstrProperties, String pstrROPGroupOBID) throws Exception;

    ObjectDTO createOUpdateROPRuleGroupWithDTOStyle(String lstrProperties) throws Exception;

    IObject createOUpdateROPRuleGroupWithIObjectStyle(String lstrProperties) throws Exception;

    void refreshROPCache();

    IObjectCollection generateWorkStepForDesignObjects(IObjectCollection pcolDesignObjects, IObjectCollection pcolExistObjects, String configItemName) throws Exception;

    List<ObjectDTO> getConstructionTypes() throws Exception;

    void generateROPTemplateData(HttpServletResponse response, String pstrROPGroupOBID) throws Exception;

    Boolean deleteROPTemplateInfo(String pstrOBID, String pstrClassDef, String pstrROPGroupOBID) throws Exception;

    List<ObjectDTO> getPropDefsForROPGroupItemByROPGroupOBID(String ropGroupOBID) throws Exception;

    void refreshObjStatusByROPChanged() throws Exception;

    ROPReviseTaskVo getROPProcessingTaskStatus(String taskUUID);

    void clearProcessingTasks();

    Map<String, IObjectCollection> getNotProcessRevisedROPRuleGroups();

    LoaderReport processROPTemplateRevise(ROPTemplateReviseTask callable) ;

    void getAllROPProcessingTasks(PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam, Result<List<ROPReviseTaskVo>> result) throws Exception;

    void getROPTemplateReviseHistoryTasks(PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam, ResultVo<List<ROPReviseTaskVo>> result) throws Exception;
}
