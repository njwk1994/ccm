package ccm.server.business;

import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.schema.collections.IObjectCollection;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/8/16 9:15
 */
public interface ICCMTPPurpose2WPPurposeService {

    /**
     * 获取任务包施工阶段
     *
     * @return
     * @throws Exception
     */
    IObjectCollection getTPPurpose(FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception;

    /**
     * 获取工作包施工阶段
     *
     * @return
     * @throws Exception
     */
    IObjectCollection getWPPurpose(FiltersParam filtersParam, OrderByParam orderByParam,int pageIndex,int pageSize) throws Exception;

    /**
     * 根据工作包施工阶段获取对应任务包施工阶段
     * @param wpPurpose
     * @return
     * @throws Exception
     */
    String getTPPurposeByWPPurpose(String wpPurpose)throws Exception;

    /**
     * 获取任务包阶段下的工作包施工阶段
     *
     * @return
     * @throws Exception
     */
    IObjectCollection getWPPurposeInTPPurpose(String tpPurpose,FiltersParam filtersParam, OrderByParam orderByParam,int pageIndex,int pageSize) throws Exception;

    /**
     * 创建任务包施工阶段和工作包施工阶段关联关系
     *
     * @param tpPurpose
     * @param wpPurposes
     * @throws Exception
     */
    void createTPPurpose2WPPurposeRel(String tpPurpose, String wpPurposes) throws Exception;

    /**
     * 解除任务包施工阶段和工作包施工阶段关联关系
     *
     * @param tpPurpose
     * @param wpPurposes
     * @throws Exception
     */
    void removeTPPurpose2WPPurposeRel(String tpPurpose, String wpPurposes) throws Exception;

}
