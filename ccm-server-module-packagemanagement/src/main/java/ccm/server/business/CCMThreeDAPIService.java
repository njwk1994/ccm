package ccm.server.business;

import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/12 11:10
 */
public interface CCMThreeDAPIService {

    /**
     * 根据时间范围获取对应工作包施工阶段与步骤已完成的设计数据
     *
     * @param wpPurpose       工作包施工阶段
     * @param ropWorkStepName 工作步骤
     * @param startDate       日期开始时间
     * @param endDate         日期结束时间
     * @return
     * @throws Exception
     */
    List<Map<String, String>> getFinishedDesignByDate(String wpPurpose, String ropWorkStepName, String startDate, String endDate) throws Exception;

    /**
     * 所有工作包中工作步骤的完成时间的 最大值 最小值
     *
     * @param wpPurpose       工作包施工阶段
     * @param ropWorkStepName 工作步骤
     * @return
     * @throws Exception
     */
    Map<String, Object> getMaxAndMinDate(String wpPurpose, String ropWorkStepName) throws Exception;

}
