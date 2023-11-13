package ccm.server.business.impl;

import ccm.server.business.CCMThreeDAPIService;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.operator;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.tableSuffix;
import ccm.server.models.query.QueryRequest;
import ccm.server.processors.ThreadsProcessor;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.utils.BasicPlanPackageObjUtils;
import ccm.server.utils.ICCMWBSUtils;
import ccm.server.utils.PackagesUtils;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/12 11:10
 */
@Slf4j
@Service
public class CCMThreeDAPIServiceImpl implements CCMThreeDAPIService {

    /**
     * 注入数据源 实际实现为 DynamicRoutingDataSource 动态数据源
     */
    @Autowired
    private DataSource dataSource;

    /**
     * 根据时间范围获取对应工作包施工阶段与步骤已完成的设计数据
     *
     * @param wpPurpose       工作包施工阶段
     * @param ropWorkStepName 工作步骤
     * @param startDate       完成日期开始时间
     * @param endDate         完成日期结束时间
     * @return
     * @throws Exception
     */
    @Override
    public List<Map<String, String>> getFinishedDesignByDate(String wpPurpose, String ropWorkStepName, String startDate, String endDate) throws Exception {
        long s = System.currentTimeMillis();

        List<Callable<IRelCollection>> matchRelCallables = new ArrayList<>();
        Callable<IRelCollection> getPlannedStartMatchRelCollectionCallable = () -> getPlannedStartMatchRelCollection(wpPurpose, ropWorkStepName, startDate, endDate);
        Callable<IRelCollection> getPlannedEndMatchRelCollectionCallable = () -> getPlannedEndMatchRelCollection(wpPurpose, ropWorkStepName, startDate, endDate);
        Callable<IRelCollection> getActualStartMatchRelCollectionCallable = () -> getActualStartMatchRelCollection(wpPurpose, ropWorkStepName, startDate, endDate);
        Callable<IRelCollection> getActualEndMatchRelCollectionCallable = () -> getActualEndMatchRelCollection(wpPurpose, ropWorkStepName, startDate, endDate);
        // 绑定subject到任务上
        Subject subject = ThreadContext.getSubject();
        subject.associateWith(getPlannedStartMatchRelCollectionCallable);
        subject.associateWith(getPlannedEndMatchRelCollectionCallable);
        subject.associateWith(getActualStartMatchRelCollectionCallable);
        subject.associateWith(getActualEndMatchRelCollectionCallable);

        matchRelCallables.add(getPlannedStartMatchRelCollectionCallable);
        matchRelCallables.add(getPlannedEndMatchRelCollectionCallable);
        matchRelCallables.add(getActualStartMatchRelCollectionCallable);
        matchRelCallables.add(getActualEndMatchRelCollectionCallable);

        List<IRelCollection> relCollections = ThreadsProcessor.Instance.execute(matchRelCallables);
        long e1 = System.currentTimeMillis();
        log.info("查询相关设计数据耗时:{}ms", e1 - s);
        List<String> relOBIDs = Collections.synchronizedList(new ArrayList<>());

        List<Callable<List<Map<String, String>>>> designCallables = new ArrayList<>();
        for (IRelCollection design2wsRelCollection : relCollections) {
            Callable<List<Map<String, String>>> getDesignDataMapListCallable = () -> getDesignDataMapList(design2wsRelCollection, relOBIDs);
            // 绑定subject到任务上
            subject.associateWith(getDesignDataMapListCallable);

            designCallables.add(getDesignDataMapListCallable);
        }
        List<List<Map<String, String>>> designDataMaps = ThreadsProcessor.Instance.execute(designCallables);
        List<Map<String, String>> result = new ArrayList<>();
        for (List<Map<String, String>> designDataMap : designDataMaps) {
            result.addAll(designDataMap);
        }
        log.info("封装设计数据总耗时:{}ms", System.currentTimeMillis() - e1);
        return result;
    }

    /**
     * 工作包计划开始时间匹配的工作步骤
     *
     * @param wpPurpose
     * @param ropWorkStepName
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private IRelCollection getPlannedStartMatchRelCollection(String wpPurpose, String ropWorkStepName, String startDate, String endDate) throws Exception {
        // PlannedStart 在范围时间内
        IQueryEngine plannedStartQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest plannedStartQueryRequest = plannedStartQueryEngine.start();
        plannedStartQueryEngine.addClassDefForQuery(plannedStartQueryRequest, PackagesUtils.CCM_WORK_STEP);
        plannedStartQueryEngine.addRelOrEdgeDefForQuery(plannedStartQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", ICCMWBSUtils.PROPERTY_WP_PURPOSE, operator.equal, wpPurpose);
        plannedStartQueryEngine.addRelOrEdgeDefForQuery(plannedStartQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", BasicPlanPackageObjUtils.PROPERTY_PLANNED_START, operator.largeOrEqualThan, startDate);
        plannedStartQueryEngine.addRelOrEdgeDefForQuery(plannedStartQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", BasicPlanPackageObjUtils.PROPERTY_PLANNED_START, operator.lessOrEqualThan, endDate);
        plannedStartQueryEngine.addPropertyForQuery(plannedStartQueryRequest, "", propertyDefinitionType.ROPWorkStepName.name(), operator.equal, ropWorkStepName);
        IObjectCollection plannedStartMatch = plannedStartQueryEngine.query(plannedStartQueryRequest);
        return plannedStartMatch.GetEnd2Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP);
    }

    /**
     * 工作包计划结束时间匹配的工作步骤
     *
     * @param wpPurpose
     * @param ropWorkStepName
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private IRelCollection getPlannedEndMatchRelCollection(String wpPurpose, String ropWorkStepName, String startDate, String endDate) throws Exception {
        // PlannedEnd 在范围时间内
        IQueryEngine plannedEndQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest plannedEndQueryRequest = plannedEndQueryEngine.start();
        plannedEndQueryEngine.addClassDefForQuery(plannedEndQueryRequest, PackagesUtils.CCM_WORK_STEP);
        plannedEndQueryEngine.addRelOrEdgeDefForQuery(plannedEndQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", ICCMWBSUtils.PROPERTY_WP_PURPOSE, operator.equal, wpPurpose);
        plannedEndQueryEngine.addRelOrEdgeDefForQuery(plannedEndQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", BasicPlanPackageObjUtils.PROPERTY_PLANNED_START, operator.largeOrEqualThan, startDate);
        plannedEndQueryEngine.addRelOrEdgeDefForQuery(plannedEndQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", BasicPlanPackageObjUtils.PROPERTY_PLANNED_START, operator.lessOrEqualThan, endDate);
        plannedEndQueryEngine.addPropertyForQuery(plannedEndQueryRequest, "", propertyDefinitionType.ROPWorkStepName.name(), operator.equal, ropWorkStepName);
        IObjectCollection plannedEndMatch = plannedEndQueryEngine.query(plannedEndQueryRequest);
        return plannedEndMatch.GetEnd2Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP);
    }

    /**
     * 实际开始时间匹配的工作步骤
     *
     * @param wpPurpose
     * @param ropWorkStepName
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private IRelCollection getActualStartMatchRelCollection(String wpPurpose, String ropWorkStepName, String startDate, String endDate) throws Exception {
        // ActualStart 在范围时间内
        IQueryEngine actualStartQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest actualStartQueryRequest = actualStartQueryEngine.start();
        actualStartQueryEngine.addClassDefForQuery(actualStartQueryRequest, PackagesUtils.CCM_WORK_STEP);
        actualStartQueryEngine.addRelOrEdgeDefForQuery(actualStartQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", ICCMWBSUtils.PROPERTY_WP_PURPOSE, operator.equal, wpPurpose);
        actualStartQueryEngine.addPropertyForQuery(actualStartQueryRequest, "", propertyDefinitionType.ROPWorkStepName.name(), operator.equal, ropWorkStepName);
        actualStartQueryEngine.addPropertyForQuery(actualStartQueryRequest, "", BasicPlanPackageObjUtils.PROPERTY_ACTUAL_START, operator.largeOrEqualThan, startDate);
        actualStartQueryEngine.addPropertyForQuery(actualStartQueryRequest, "", BasicPlanPackageObjUtils.PROPERTY_ACTUAL_START, operator.lessOrEqualThan, endDate);
        IObjectCollection actualStartMatch = actualStartQueryEngine.query(actualStartQueryRequest);
        return actualStartMatch.GetEnd2Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP);
    }

    /**
     * 实际结束时间匹配的工作步骤
     *
     * @param wpPurpose
     * @param ropWorkStepName
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    private IRelCollection getActualEndMatchRelCollection(String wpPurpose, String ropWorkStepName, String startDate, String endDate) throws Exception {
        // ActualEnd 在范围时间内
        IQueryEngine actualEndQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest actualEndQueryRequest = actualEndQueryEngine.start();
        actualEndQueryEngine.addClassDefForQuery(actualEndQueryRequest, PackagesUtils.CCM_WORK_STEP);
        actualEndQueryEngine.addRelOrEdgeDefForQuery(actualEndQueryRequest, "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, "", ICCMWBSUtils.PROPERTY_WP_PURPOSE, operator.equal, wpPurpose);
        actualEndQueryEngine.addPropertyForQuery(actualEndQueryRequest, "", propertyDefinitionType.ROPWorkStepName.name(), operator.equal, ropWorkStepName);
        actualEndQueryEngine.addPropertyForQuery(actualEndQueryRequest, "", BasicPlanPackageObjUtils.PROPERTY_ACTUAL_END, operator.largeOrEqualThan, startDate);
        actualEndQueryEngine.addPropertyForQuery(actualEndQueryRequest, "", BasicPlanPackageObjUtils.PROPERTY_ACTUAL_END, operator.lessOrEqualThan, endDate);
        IObjectCollection actualEndMatch = actualEndQueryEngine.query(actualEndQueryRequest);
        return actualEndMatch.GetEnd2Relationships().GetRels(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP);
    }

    /**
     * 封装单种时间类型匹配的设计数据
     *
     * @param design2wsRelCollection
     * @param relOBIDs
     * @return
     * @throws Exception
     */
    private List<Map<String, String>> getDesignDataMapList(IRelCollection design2wsRelCollection, List<String> relOBIDs) throws Exception {
        long rel1 = System.currentTimeMillis();
        List<Map<String, String>> objects = new ArrayList<>();
        IObjectCollection wsCollection = design2wsRelCollection.GetEnd2s();
        IRelCollection wp2wsRelCollection = wsCollection.GetEnd2Relationships().GetRels(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP);

        Iterator<IObject> relIter = design2wsRelCollection.GetEnumerator();
        while (relIter.hasNext()) {
            IObject next = relIter.next();
            IRel design2wsRel = next.toInterface(IRel.class);
            if (relOBIDs.contains(design2wsRel.OBID())) {
                continue;
            }
            relOBIDs.add(design2wsRel.OBID());
            Map<String, String> data = new HashMap<>();
            data.put("designName", design2wsRel.Name1());
            IObject wsOBJ = design2wsRel.GetEnd2();
            /*工作包属性*/
            IRel wp2wsRel = wp2wsRelCollection.GetRel(PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP, wsOBJ.UID());
            if (null == wp2wsRel) {
                throw new RuntimeException("根据时间范围获取对应工作包施工阶段与步骤已完成的设计数据未查询到对应工作包!");
            }
            log.debug("获取工作步骤{}对应的工作包", wsOBJ.UID());
            IObject wp = wp2wsRel.GetEnd1();
            data.put("plannedStart", wp.getDisplayValue(BasicPlanPackageObjUtils.PROPERTY_PLANNED_START));
            data.put("plannedEnd", wp.getDisplayValue(BasicPlanPackageObjUtils.PROPERTY_PLANNED_END));
            /*工作步骤属性*/
            data.put("actualStart", wsOBJ.getDisplayValue(BasicPlanPackageObjUtils.PROPERTY_ACTUAL_START));
            data.put("actualEnd", wsOBJ.getDisplayValue(BasicPlanPackageObjUtils.PROPERTY_ACTUAL_END));
            objects.add(data);
        }
        log.info("封装单种时间类型匹配的设计数据耗时:{}ms", System.currentTimeMillis() - rel1);
        return objects;
    }

    private static final String BLANK = " ";
    private static final String _COMMA_ = " , ";
    private static final String _AS_ = " AS ";

    /**
     * 所有工作包中工作步骤的完成时间的 最大值 最小值
     *
     * @param wpPurpose       工作包施工阶段
     * @param ropWorkStepName 工作步骤
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> getMaxAndMinDate(String wpPurpose, String ropWorkStepName) throws Exception {
        Map<String, String> result = new HashMap<>();
        // 项目表名称
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        // 获取当前项目表前缀
        String projectTablePrefix = configurationItem.TablePrefix();

        // 工作包表名
        String wpTablePrefix = CIMContext.Instance.ProcessCache().getTablePrefixForClassDefinition(PackagesUtils.CCM_WORK_PACKAGE);
        String wpObjTable = BLANK + projectTablePrefix + wpTablePrefix + tableSuffix.OBJ.name() + BLANK;
        String wpObjPrTable = BLANK + projectTablePrefix + wpTablePrefix + tableSuffix.OBJPR.name() + BLANK;
        String wpObjRelTable = BLANK + projectTablePrefix + wpTablePrefix + tableSuffix.OBJREL.name() + BLANK;
        // 工作步骤表名
        String wsTablePrefix = CIMContext.Instance.ProcessCache().getTablePrefixForClassDefinition(PackagesUtils.CCM_WORK_STEP);
        String wsObjTable = BLANK + projectTablePrefix + wsTablePrefix + tableSuffix.OBJ.name() + BLANK;
        String wsObjPrTable = BLANK + projectTablePrefix + wsTablePrefix + tableSuffix.OBJPR.name() + BLANK;
        String wsObjRelTable = BLANK + projectTablePrefix + wsTablePrefix + tableSuffix.OBJREL.name() + BLANK;

        // 拼接sql
        String sql = " SELECT " +
                "MAX(wp_pr_ps.str_value) " + _AS_ + "\"PlannedStartMax\"" + _COMMA_ +
                "MIN(wp_pr_ps.str_value) " + _AS_ + "\"PlannedStartMin\"" + _COMMA_ +
                "MAX(wp_pr_pe.str_value) " + _AS_ + "\"PlannedEndMax\"" + _COMMA_ +
                "MIN(wp_pr_pe.str_value) " + _AS_ + "\"PlannedEndMin\"" + _COMMA_ +
                "MAX(ws_pr_as.str_value) " + _AS_ + "\"ActualStartMax\"" + _COMMA_ +
                "MIN(ws_pr_as.str_value) " + _AS_ + "\"ActualStartMin\"" + _COMMA_ +
                "MAX(ws_pr_ae.str_value) " + _AS_ + "\"ActualEndMax\"" + _COMMA_ +
                "MIN(ws_pr_ae.str_value) " + _AS_ + "\"ActualEndMin\"" +
                " FROM " +
                wsObjTable + _AS_ + "ws_obj" +
                " INNER JOIN " + wsObjRelTable + _AS_ + "ws_rel_01" + " ON " + "ws_obj.obid = ws_rel_01.obid2" +
                " AND " + "ws_rel_01.rel_def_uid = '" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP + "'" +
                // 工作包阶段条件
                " INNER JOIN " + wpObjPrTable + _AS_ + "wp_pr_purpose" + " ON " + "ws_rel_01.OBID1 = wp_pr_purpose.obj_obid" +
                " AND " + "wp_pr_purpose.property_def_uid = '" + ICCMWBSUtils.PROPERTY_WP_PURPOSE + "'" +
                " AND " + "wp_pr_purpose.str_value ='" + wpPurpose + "'" +
                " AND ( wp_pr_purpose.termination_user IS NULL OR wp_pr_purpose.termination_user = '' )" +
                // 工作包计划开始属性
                " INNER JOIN " + wpObjPrTable + _AS_ + "wp_pr_ps" + " ON " + "ws_rel_01.OBID1 = wp_pr_ps.obj_obid" +
                " AND " + "wp_pr_ps.property_def_uid = '" + BasicPlanPackageObjUtils.PROPERTY_PLANNED_START + "'" +
                " AND ( wp_pr_ps.termination_user IS NULL OR wp_pr_ps.termination_user = '' )" +
                // 工作包计划结束属性
                " INNER JOIN " + wpObjPrTable + _AS_ + "wp_pr_pe" + " ON " + "ws_rel_01.OBID1 = wp_pr_pe.obj_obid" +
                " AND " + "wp_pr_pe.property_def_uid = '" + BasicPlanPackageObjUtils.PROPERTY_PLANNED_END + "'" +
                " AND ( wp_pr_pe.termination_user IS NULL OR wp_pr_pe.termination_user = '' )" +
                // 工作步骤名称条件
                " INNER JOIN " + wsObjPrTable + _AS_ + "ws_pr_rop_workstep_name" + " ON " + "ws_obj.obid = ws_pr_rop_workstep_name.obj_obid" +
                " AND ws_pr_rop_workstep_name.property_def_uid = '" + propertyDefinitionType.ROPWorkStepName.name() + "'" +
                " AND ws_pr_rop_workstep_name.str_value = '" + ropWorkStepName + "'" +
                " AND ( ws_pr_rop_workstep_name.termination_user IS NULL OR ws_pr_rop_workstep_name.termination_user = '' )" +
                // 工作步骤实际开始时间属性
                " INNER JOIN " + wsObjPrTable + _AS_ + "ws_pr_as" + " ON " + "ws_obj.obid = ws_pr_as.obj_obid" +
                " AND ws_pr_as.property_def_uid = '" + BasicPlanPackageObjUtils.PROPERTY_ACTUAL_START + "'" +
                " AND ( ws_pr_as.termination_user IS NULL OR ws_pr_as.termination_user = '' )" +
                // 工作步骤实际结束时间属性
                " INNER JOIN " + wsObjPrTable + _AS_ + "ws_pr_ae" + " ON " + "ws_obj.obid = ws_pr_ae.obj_obid" +
                " AND ws_pr_ae.property_def_uid = '" + BasicPlanPackageObjUtils.PROPERTY_ACTUAL_END + "'" +
                " AND ( ws_pr_ae.termination_user IS NULL OR ws_pr_ae.termination_user = '' )" +

                " WHERE " +
                " ws_obj.termination_user IS NULL OR ws_obj.termination_user = '' ";

        log.debug("执行SQL语句:{}", sql);

        DynamicRoutingDataSource dyDataSource = (DynamicRoutingDataSource) dataSource;
        DataSource master = dyDataSource.getDataSource("master");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(master);

        // ActualEnd 在范围时间内
        /*IQueryEngine actualEndQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest actualEndQueryRequest = actualEndQueryEngine.start();
        actualEndQueryEngine.addClassDefForQuery(actualEndQueryRequest, PackagesUtils.CCM_WORK_STEP);
        actualEndQueryEngine.addRelOrEdgeDefForQuery(actualEndQueryRequest,
                "-" + PackagesUtils.REL_WORK_PACKAGE_2_WORK_STEP,
                "", ICCMWBSUtils.PROPERTY_WP_PURPOSE, operator.equal, wpPurpose);
        actualEndQueryEngine.addPropertyForQuery(actualEndQueryRequest, "", propertyDefinitionType.ROPWorkStepName.name(), operator.equal, ropWorkStepName);
        IObjectCollection actualEndMatch = actualEndQueryEngine.query(actualEndQueryRequest);*/

        return jdbcTemplate.queryForMap(sql);
    }
}
