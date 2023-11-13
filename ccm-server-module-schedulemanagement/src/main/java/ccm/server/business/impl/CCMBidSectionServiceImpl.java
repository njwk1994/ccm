package ccm.server.business.impl;

import ccm.server.business.ICCMBidSectionService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.classDefinitionType;
import ccm.server.enums.operator;
import ccm.server.enums.relDefinitionType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.ICCMBidSection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.utils.ICCMBidSectionUtils;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/6/13 10:57
 */
@Slf4j
@Service
public class CCMBidSectionServiceImpl implements ICCMBidSectionService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /**
     * 获取条件过滤分页标段数据
     *
     * @param filtersParam
     * @param orderByParam
     * @param pageRequest
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getBidSections(FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception {
        return schemaBusinessService.generalQuery(ICCMBidSectionUtils.CCM_BID_SECTION, pageRequest.getPageIndex(), pageRequest.getPageSize(), orderByParam.getOrderByWrappers(), filtersParam.getFilters());
    }

    /**
     * 根据OBID获取标段
     *
     * @param obid
     * @return
     * @throws Exception
     */
    @Override
    public IObject getBidSectionByOBID(String obid) throws Exception {
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICCMBidSectionUtils.CCM_BID_SECTION);
        queryEngine.addOBIDForQuery(queryRequest, operator.equal, obid);
        IObject iObject = queryEngine.queryOne(queryRequest);
        if (null == iObject) {
            throw new Exception("根据OBID获取标段失败!");
        }
        return iObject;
    }

    /**
     * 创建标段
     *
     * @param objectDTO
     * @return
     * @throws Exception
     */
    @Override
    public IObject createBidSection(ObjectDTO objectDTO) throws Exception {
        return schemaBusinessService.generalCreate(objectDTO);
    }

    /**
     * 更新标段
     *
     * @param objectDTO
     * @return
     * @throws Exception
     */
    @Override
    public IObject updateBidSection(ObjectDTO objectDTO) throws Exception {
        return schemaBusinessService.generalUpdate(objectDTO);
    }

    /**
     * 删除标段
     *
     * @param bidSectionOBID
     * @return
     * @throws Exception
     */
    @Override
    public void deleteBidSection(String bidSectionOBID) throws Exception {
        schemaBusinessService.deleteObject(bidSectionOBID, ICCMBidSectionUtils.CCM_BID_SECTION, true);
    }

    /**
     * 建立标段和施工区域关联关系
     *
     * @return
     * @throws Exception
     */
    @Override
    public void genRelBidSection2CWA(String bsOBID, String cwaOBID) throws Exception {
        IObject bidSectionByOBID = getBidSectionByOBID(bsOBID);
        IQueryEngine cwaQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest cwaQueryRequest = cwaQueryEngine.start();
        cwaQueryEngine.addClassDefForQuery(cwaQueryRequest, classDefinitionType.EnumEnum.name());
        cwaQueryEngine.addOBIDForQuery(cwaQueryRequest, operator.in, cwaOBID);
        IObjectCollection cwas = cwaQueryEngine.query(cwaQueryRequest);
        if (null == cwas || !cwas.hasValue()) {
            throw new Exception("获取施工区域失败,未找到对应施工区域!");
        }
        Iterator<IObject> cwaIterator = cwas.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (cwaIterator.hasNext()) {
            IObject cwa = cwaIterator.next();
            IRel iRel = SchemaUtility.newRelationship(ICCMBidSectionUtils.REL_BID_SECTION_2_CWA_ENUM, bidSectionByOBID, cwa, true);
            iRel.ClassDefinition().FinishCreate(iRel);
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 删除标段和施工区域关联关系
     *
     * @param bsOBID
     * @param cwaOBID
     * @throws Exception
     */
    @Override
    public void deleteRelBidSection2CWA(String bsOBID, String cwaOBID) throws Exception {
        List<String> cwaOBIDs = Arrays.asList(cwaOBID.split(","));
        IObject bidSectionByOBID = getBidSectionByOBID(bsOBID);
        ICCMBidSection iccmBidSection = bidSectionByOBID.toInterface(ICCMBidSection.class);
        IRelCollection iRelCollection = iccmBidSection.GetEnd1Relationships().GetRels(ICCMBidSectionUtils.REL_BID_SECTION_2_CWA_ENUM);

        Iterator<IObject> relIter = iRelCollection.GetEnumerator();
        SchemaUtility.beginTransaction();
        while (relIter.hasNext()) {
            IObject relObj = relIter.next();
            IRel iRel = relObj.toInterface(IRel.class);
            if (cwaOBIDs.contains(iRel.OBID2())) {
                iRel.Delete();
            }
        }
        SchemaUtility.commitTransaction();
    }

    /**
     * 获取可选择添加的施工区域
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getSelectableCWA() throws Exception {
        IObject elt_cwa = CIMContext.Instance.ProcessCache().getObjectByClassDefCache("ELT_CWA", classDefinitionType.EnumListType.name());
        IObjectCollection cwas = elt_cwa.GetEnd1Relationships().GetRels(relDefinitionType.contains.name()).GetEnd2s();
        Iterator<IObject> cwasIter = cwas.GetEnumerator();
        IObjectCollection objectCollection = new ObjectCollection();
        while (cwasIter.hasNext()) {
            IObject cwa = cwasIter.next();
            IRel iRel = cwa.GetEnd2Relationships().GetRel(ICCMBidSectionUtils.REL_BID_SECTION_2_CWA_ENUM);
            if (iRel == null || iRel.GetEnd1() == null) {
                objectCollection.addRangeUniquely(cwa);
            }
        }
        return objectCollection;
    }

    /**
     * 新建施工区域
     *
     * @param objectDTO
     * @return
     * @throws Exception
     */
    @Override
    public IObject createCWA(ObjectDTO objectDTO) throws Exception {
        IObject elt_cwa = CIMContext.Instance.ProcessCache().getObjectByClassDefCache("ELT_CWA", classDefinitionType.EnumListType.name());
        SchemaUtility.beginTransaction();
        IObject cwaOBJ = schemaBusinessService.generalCreate(objectDTO);
        IRel iRel = SchemaUtility.newRelationship(relDefinitionType.contains.name(), elt_cwa, cwaOBJ, true);
        iRel.ClassDefinition().FinishCreate(iRel);
        SchemaUtility.commitTransaction();
        return cwaOBJ;
    }
}
