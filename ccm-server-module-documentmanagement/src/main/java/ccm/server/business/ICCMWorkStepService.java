package ccm.server.business;

import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/3/25 15:16
 */
public interface ICCMWorkStepService {



    /**
     * 根据施工阶段查询工作步骤的步骤
     *
     * @param purpose
     * @return
     * @throws Exception
     */
    IObjectCollection getWorkStepROPWorkStepNameByPurpose(String purpose, String classDefinitionUID) throws Exception;

    /**
     * 根据工作步骤的施工阶段和施工步骤查询已完成的设计数据对象
     *
     * @param purpose
     * @param ropWorkStepName
     * @return
     * @throws Exception
     */
    IObjectCollection getFinishedDesignObjByPurposeAndROPWorkStepName(String purpose, String ropWorkStepName, String classDefinitionUID, PageRequest pageRequest) throws Exception;
}
