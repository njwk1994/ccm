package org.jeecg.modules.flowable.apithird.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.flowable.apithird.business.entity.FlowMyBusiness;

/**
 * @Description: 流程业务扩展表
 * @Author: jeecg-boot
 * @Date: 2021-11-25
 * @Version: V1.0
 */
public interface IFlowMyBusinessService extends IService<FlowMyBusiness> {

    boolean createActBusiness(String title, String dataId, String serviceImplName, String processDefinitionKey, String processDefinitionId);

    void deleteFlowMyBusiness(String Id);
}
