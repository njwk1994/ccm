package ccm.server.business.impl;

import ccm.server.business.ICCMConfigService;
import ccm.server.business.ICCMP6APIService;
import ccm.server.business.ICCMP6HandleService;
import ccm.server.context.CIMContext;
import ccm.server.entity.ThreadResult;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.BasicPlanPackageObjUtils;
import ccm.server.utils.ICIMProjectConfigUtils;
import ccm.server.utils.ScheduleUtils;
import ccm.server.utils.SchemaUtility;
import cn.hutool.core.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * P6业务处理服务
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/9 17:28
 */
@Slf4j
@Service
public class CCMP6HandleServiceImpl implements ICCMP6HandleService {

    @Autowired
    private ICCMConfigService configService;
    @Autowired
    private ICCMP6APIService p6APIService;

    private static final String TAG_PROJECT = "Project";
    private static final String TAG_WBS = "WBS";
    private static final String TAG_ACTIVITY = "Activity";

    /**
     * 同步P6计划
     *
     * @throws Exception
     */
    @Override
    public void syncSchedule() throws Exception {
        // 获取项目配置
        IObject projectConfig = configService.getProjectConfig();
        if (null == projectConfig) {
            throw new Exception("未能获取项目配置,获取到的项目配置为NULL,请检查配置!");
        }
        String p6WSUrl = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_WEBSERVICE_URL);
        String p6ProjectName = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_PROJECT_NAME);
        String p6ProjectLoginName = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_PROJECT_LOGIN_NAME);
        String p6ProjectPassword = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_PROJECT_PASSWORD);

        // 检查连接状态
        ThreadResult<Boolean> serviceAvailable = p6APIService.isServiceAvailable(p6WSUrl, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);
        if (!serviceAvailable.isSuccess()) {
            throw new Exception("P6服务无法访问!" + serviceAvailable.getMessage());
        }

        // 获取项目信息
        String objectId = p6APIService.readProjects(p6WSUrl, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);

        // 导出项目数据
        String xmlStr = p6APIService.exportProject(objectId, p6WSUrl, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);
        Map<String, Object> xmlMap = XmlUtil.xmlToMap(xmlStr);
        Map<String, Object> projectMap = (Map<String, Object>) xmlMap.get(TAG_PROJECT);
        // 映射WBS信息
        List<Map<String, Object>> wbsList = (List<Map<String, Object>>) projectMap.get(TAG_WBS);
        HashMap<String, Map<String, Object>> wbsNameMap = new HashMap<>();
        wbsList.forEach(m -> {
            wbsNameMap.put(m.get("ObjectId").toString(), m);
        });
        List<Map<String, Object>> activities = (List<Map<String, Object>>) projectMap.get(TAG_ACTIVITY);

        // 获取所有计划
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
        List<IObject> iObjects = query.toList();
        Map<String, IObject> existed = new HashMap<>();
        List<String> toDelete = new ArrayList<>();
        iObjects.forEach(i -> {
            existed.put(i.Name(), i);
            toDelete.add(i.Name());
        });

        SchemaUtility.beginTransaction();
        List<String> toChanges = new ArrayList<>();
        for (Map<String, Object> activity : activities) {// 新增及更新
            Object name = activity.get("Id");
            Object description = activity.get("Name");
            Object wbsPath = activity.get("WBSObjectId");
            Map<String, Object> wbsInfo = wbsNameMap.get(wbsPath.toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            IObject iObject = existed.get(name.toString());
            if (null == iObject) {// 不存在则创建
                iObject = SchemaUtility.newIObject(ScheduleUtils.CCM_SCHEDULE, name.toString(), description.toString(), null, null);
                iObject.ClassDefinition().BeginCreate(true);
            } else {// 存在则更新
                iObject.BeginUpdate();
            }
            iObject.setValue(propertyDefinitionType.Description.name(), name);
            iObject.setValue(ScheduleUtils.PROPERTY_WBS_PATH, wbsInfo.get("Name"));
            dataFormat(dateFormat, activity, iObject);
            if (null == iObject) {// 不存在则创建
                iObject.ClassDefinition().FinishCreate(iObject);
            } else {// 存在则更新
                iObject.FinishUpdate();
            }
            toChanges.add(name.toString());
        }
        // 处理删除数据
        toDelete.removeAll(toChanges);
        for (String s : toDelete) {
            IObject iObject = existed.get(s);
            iObject.Delete();
        }
        SchemaUtility.commitTransaction();
    }

    private static Map<String, String> DATA_STRING = new HashMap<String, String>();

    static {
        DATA_STRING.put("PlannedStartDate", BasicPlanPackageObjUtils.PROPERTY_PLANNED_START);
        DATA_STRING.put("PlannedFinishDate", BasicPlanPackageObjUtils.PROPERTY_PLANNED_END);
        DATA_STRING.put("ActualStartDate", BasicPlanPackageObjUtils.PROPERTY_ACTUAL_START);
        DATA_STRING.put("ActualFinishDate", BasicPlanPackageObjUtils.PROPERTY_ACTUAL_END);
        DATA_STRING.put("RemainingEarlyStartDate", ScheduleUtils.PROPERTY_EARLY_START);
        DATA_STRING.put("RemainingEarlyFinishDate", ScheduleUtils.PROPERTY_EARLY_END);
        DATA_STRING.put("RemainingLateStartDate", ScheduleUtils.PROPERTY_LATE_START);
        DATA_STRING.put("RemainingLateFinishDate", ScheduleUtils.PROPERTY_LATE_END);
    }

    private void dataFormat(SimpleDateFormat dateFormat, Map<String, Object> activity, IObject iObject) throws Exception {
        for (String s : DATA_STRING.keySet()) {
            Object o = activity.get(s);
            if (!StringUtils.isEmpty(o)) {
                Date parse = dateFormat.parse(activity.get(s).toString());
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = df.format(parse);
                iObject.setValue(DATA_STRING.get(s), format);
            } else {
                iObject.setValue(DATA_STRING.get(s), "");
            }
        }
    }
}
