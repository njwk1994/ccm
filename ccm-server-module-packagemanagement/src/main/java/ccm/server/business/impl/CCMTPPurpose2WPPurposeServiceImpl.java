package ccm.server.business.impl;

import ccm.server.business.ICCMTPPurpose2WPPurposeService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.operator;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.utils.PackagesUtils;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/8/16 9:19
 */
@Slf4j
@Service
public class CCMTPPurpose2WPPurposeServiceImpl implements ICCMTPPurpose2WPPurposeService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;


    /**
     * 获取任务包施工阶段
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getTPPurpose(FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception {
        return getEnumEnums("ELT_Purpose", filtersParam, orderByParam, pageIndex, pageSize);
    }

    /**
     * 获取工作包施工阶段
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getWPPurpose(FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception {
        IObjectCollection wpPurpose = getEnumEnums("ELT_WPPurpose", filtersParam, orderByParam, 0, 0);
        // 2022.08.18 HT 修改为返回所有工作包施工阶段 (前台需要)
        /*Iterator<IObject> iObjectIterator = wpPurpose.GetEnumerator();

        while (iObjectIterator.hasNext()) {
            IObject next = iObjectIterator.next();
            IRel iRel = next.GetEnd2Relationships().GetRel(PackagesUtils.REL_TP_PURPOSE_2_WP_PURPOSE);
            if (null != iRel && null != iRel.GetEnd1()) {
                wpPurpose.remove(next);
                wpPurpose.PageResult().setTotal(wpPurpose.PageResult().getTotal() - 1);
            }
        }*/
        return wpPurpose;
    }

    private IObjectCollection getEnumEnums(String enumTypeListUID, FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception {
        IObject tpPurposeEnumListType = CIMContext.Instance.ProcessCache().item(enumTypeListUID);
        Map<String, String> filters = filtersParam.getFilters();
        filters.put("-" + relDefinitionType.contains.name(), tpPurposeEnumListType.OBID());
        return schemaBusinessService.generalQuery(classDefinitionType.EnumEnum.name(), pageIndex, pageSize, orderByParam.getOrderByWrappers(), filtersParam.getFilters());
    }


    /**
     * 根据工作包施工阶段获取对应任务包施工阶段
     *
     * @param wpPurpose
     * @return
     * @throws Exception
     */
    @Override
    public String getTPPurposeByWPPurpose(String wpPurpose) throws Exception {
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest start = queryEngine.start();
        queryEngine.addClassDefForQuery(start, classDefinitionType.EnumEnum.name());
        queryEngine.addRelOrEdgeDefForQuery(start, "+" + PackagesUtils.REL_TP_PURPOSE_2_WP_PURPOSE,
                "", propertyDefinitionType.OBID.name(), operator.equal, wpPurpose);
        IObject iObject = queryEngine.queryOne(start);
        if (null != iObject) {
            return iObject.UID();
        }
        return null;
    }

    /**
     * 获取任务包阶段下的工作包施工阶段
     *
     * @param tpPurpose
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getWPPurposeInTPPurpose(String tpPurpose, FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception {
        Map<String, String> filters = filtersParam.getFilters();
        filters.put("-" + PackagesUtils.REL_TP_PURPOSE_2_WP_PURPOSE, tpPurpose);
        return schemaBusinessService.generalQuery(classDefinitionType.EnumEnum.name(), pageIndex, pageSize, orderByParam.getOrderByWrappers(), filters);
    }

    /**
     * 创建任务包施工阶段和工作包施工阶段关联关系
     *
     * @param tpPurpose
     * @param wpPurposes
     * @throws Exception
     */
    @Override
    public void createTPPurpose2WPPurposeRel(String tpPurpose, String wpPurposes) throws Exception {
        IObject tpPurposeObj = getEnumEnumByOBID(tpPurpose);
        if (null == tpPurposeObj) {
            throw new RuntimeException("未能根据任务包施工阶段OBID获取到对应任务包施工阶段!任务包施工阶段OBID：" + tpPurpose);
        }
        IObjectCollection wpPurposeObjs = getEnumEnumByOBIDs(wpPurposes);
        Iterator<IObject> iObjectIterator = wpPurposeObjs.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (iObjectIterator.hasNext()) {
            IObject wpPurposeObj = iObjectIterator.next();
            IRel iRel = wpPurposeObj.GetEnd2Relationships().GetRel(PackagesUtils.REL_TP_PURPOSE_2_WP_PURPOSE);
            if (null != iRel && null != iRel.GetEnd1()) {
                throw new RuntimeException("工作包施工阶段已经绑定过任务包施工阶段!工作包施工阶段OBID：" + wpPurposeObj.OBID());
            }
            SchemaUtility.createRelationShip(PackagesUtils.REL_TP_PURPOSE_2_WP_PURPOSE, tpPurposeObj, wpPurposeObj, false);
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 解除任务包施工阶段和工作包施工阶段关联关系
     *
     * @param tpPurpose
     * @param wpPurposes
     * @throws Exception
     */
    @Override
    public void removeTPPurpose2WPPurposeRel(String tpPurpose, String wpPurposes) throws Exception {
        IObject tpPurposeObj = getEnumEnumByOBID(tpPurpose);
        if (null == tpPurposeObj) {
            throw new RuntimeException("未能根据任务包施工阶段OBID获取到对应任务包施工阶段!任务包施工阶段OBID：" + tpPurpose);
        }
        // 根据OBID1和OBID2获取关联关系
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest start = iQueryEngine.start();
        iQueryEngine.addRelDefUidForQuery(start, operator.equal, PackagesUtils.REL_TP_PURPOSE_2_WP_PURPOSE);
        iQueryEngine.addRelOrEdgeDefForQuery(start, "", "", propertyDefinitionType.OBID1.name(), operator.equal, tpPurpose);
        iQueryEngine.addRelOrEdgeDefForQuery(start, "", "", propertyDefinitionType.OBID2.name(), operator.in, wpPurposes);
        iQueryEngine.setQueryForRelationship(start, true);
        IObjectCollection tpp2wppRelCollection = iQueryEngine.query(start);

        Iterator<IObject> iObjectIterator = tpp2wppRelCollection.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (iObjectIterator.hasNext()) {
            IObject tpp2wppRelObject = iObjectIterator.next();
            tpp2wppRelObject.Delete();
        }
        SchemaUtility.commitTransaction();
    }

    private IObject getEnumEnumByOBID(String obid) {
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest start = iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(start, classDefinitionType.EnumEnum.name());
        iQueryEngine.addOBIDForQuery(start, operator.equal, obid);
        return iQueryEngine.queryOne(start);
    }

    private IObjectCollection getEnumEnumByOBIDs(String obids) {
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest start = iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(start, classDefinitionType.EnumEnum.name());
        iQueryEngine.addOBIDForQuery(start, operator.in, obids);
        return iQueryEngine.query(start);
    }
}
