package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;

import java.util.List;

/**
 * 计划策略管理
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/2 9:17
 */
public interface ICCMSchedulePolicyService {

    /**
     * 创建计划策略条件
     *
     * @param scheduleOBID
     * @param policyItem
     * @throws Exception
     */
    void createSchedulePolicyItem(String scheduleOBID, ObjectDTO policyItem) throws Exception;

    /**
     * 批量创建计划策略条件
     *
     * @param scheduleOBID
     * @param policyItems
     * @throws Exception
     */
    void createSchedulePolicyItems(String scheduleOBID, List<ObjectDTO> policyItems) throws Exception;

    /**
     * 删除计划策略条件
     *
     * @param scheduleOBID    计划OBID
     * @param policyItemOBIDs 策略OBID集合
     * @throws Exception
     */
    void deleteSchedulePolicyItems(String scheduleOBID, String policyItemOBIDs) throws Exception;

    /**
     * 根据计划策略条件归集数据
     * <p>
     * 根据计划策略条件查询对应设计数据并且和计划创建关联关系
     * </p>
     *
     * @param scheduleOBID
     * @throws Exception
     */
    void dataCollection(String scheduleOBID) throws Exception;

}

