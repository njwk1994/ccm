package ccm.server.business.impl;

import ccm.server.business.ICCMWorkStepService;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.domainInfo;
import ccm.server.enums.operator;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IWorkStep;
import ccm.server.utils.PackagesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/3/25 15:16
 */
@Service
public class CCMWorkStepServiceImpl implements ICCMWorkStepService {

    /**
     * 根据施工阶段查询工作步骤
     *
     * @param purpose
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getWorkStepROPWorkStepNameByPurpose(String purpose, String classDefinitionUID) throws Exception {
        if (StringUtils.isBlank(purpose)) {
            throw new Exception("施工阶段不可为空!");
        }
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(queryRequest, classDefinitionUID);
        iQueryEngine.addPropertyForQuery(queryRequest, "", "ROPWorkStepWPPhase", operator.equal, purpose);
        IObjectCollection workStepColl = iQueryEngine.query(queryRequest);
        Iterator<IObject> workStepIter = workStepColl.GetEnumerator();
        Set<String> ropWorkStepNameSet = new HashSet<>();
        while (workStepIter.hasNext()) {
            IObject workStepObj = workStepIter.next();
            String ropWorkStepName = workStepObj.getProperty("ROPWorkStepName").Value().toString();
            ropWorkStepNameSet.add(ropWorkStepName);
        }
        ObjectCollection objectCollection = new ObjectCollection();
        for (String ropWorkStepName : ropWorkStepNameSet) {
            IObject ropWorkStepNameObj = CIMContext.Instance.ProcessCache().item(ropWorkStepName, domainInfo.SCHEMA.toString());
            objectCollection.addRangeUniquely(ropWorkStepNameObj);
        }
        return objectCollection;
    }


    /**
     * 根据工作步骤的施工阶段和施工步骤查询已完成的设计数据对象
     *
     * @param purpose
     * @param classDefinitionUID
     * @param ropWorkStepName
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getFinishedDesignObjByPurposeAndROPWorkStepName(String purpose, String ropWorkStepName, String classDefinitionUID, PageRequest pageRequest) throws Exception {
        if (StringUtils.isBlank(purpose) || StringUtils.isBlank(ropWorkStepName)) {
            throw new Exception("施工阶段或施工步骤不可为空!");
        }
        IQueryEngine iQueryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = iQueryEngine.start();
        iQueryEngine.addClassDefForQuery(queryRequest, PackagesUtils.CCM_WORK_STEP);
        iQueryEngine.addPropertyForQuery(queryRequest, "", "ROPWorkStepWPPhase", operator.equal, purpose);
        iQueryEngine.addPropertyForQuery(queryRequest, "", "ROPWorkStepName", operator.equal, ropWorkStepName);
        IObjectCollection workStepColl = iQueryEngine.query(queryRequest);
        Iterator<IObject> workStepIter = workStepColl.GetEnumerator();

        ObjectCollection designOC = new ObjectCollection();
        int pageIndex = pageRequest.getPageIndex();
        int pageSize = pageRequest.getPageSize();
        designOC.PageResult().setCurrent(pageIndex);
        designOC.PageResult().setSize(pageSize);

        long total = 0L;

        while (workStepIter.hasNext()) {
            IObject workStepObj = workStepIter.next();
            IWorkStep workStep = workStepObj.toInterface(IWorkStep.class);
            // 当已完成时获取对应组件
            if (workStep.hasActualCompletedDate()) {
                IObject designData = workStep.GetEnd2Relationships().GetRel(PackagesUtils.REL_DESIGN_OBJ_2_WORK_STEP).GetEnd1();
                // 只获取对应类型的数据
                if (!designData.ClassDefinitionUID().equalsIgnoreCase(classDefinitionUID)) {
                    continue;
                }
                designOC.addRangeUniquely(designData);
                total++;
            }
        }
        designOC.PageResult().setTotal(total);
        return designOC;
    }
}
