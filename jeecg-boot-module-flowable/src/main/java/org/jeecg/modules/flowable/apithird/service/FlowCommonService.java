package org.jeecg.modules.flowable.apithird.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.flowable.apithird.business.entity.FlowMyBusiness;
import org.jeecg.modules.flowable.apithird.business.service.impl.FlowMyBusinessServiceImpl;
import org.jeecg.modules.flowable.common.exception.CustomException;
import org.jeecg.modules.flowable.service.impl.FlowInstanceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 业务模块调用API的集合
 *
 * @author PanMeiCheng
 * @version 1.0
 * @date 2021/11/22
 */
@Service
public class FlowCommonService {

    @Autowired
    FlowMyBusinessServiceImpl flowMyBusinessService;

    @Autowired
    FlowInstanceServiceImpl flowInstanceService;

    /**
     * 初始生成或修改业务与流程的关联信息<br/>
     * 当业务模块新增一条数据后调用，此时业务数据关联一个流程定义，以备后续流程使用
     *
     * @param title                必填。流程业务简要描述。例：2021年11月26日xxxxx申请
     * @param dataId               必填。业务数据Id，如果是一对多业务关系，传入主表的数据Id
     * @param serviceImplName      必填。业务service注入spring容器的名称。
     *                             例如：@Service("demoService")则传入 demoService
     * @param processDefinitionKey 必填。流程定义Key，传入此值，未来启动的会是该类流程的最新一个版本
     * @param processDefinitionId  选填。流程定义Id，传入此值，未来启动的为指定版本的流程
     * @return 是否成功
     */
    public boolean initActBusiness(String title, String dataId, String serviceImplName, String processDefinitionKey, String processDefinitionId) {
        boolean hasBlank = StrUtil.hasBlank(title, dataId, serviceImplName, processDefinitionKey);
        if (hasBlank) throw new CustomException("流程关键参数未填完全！dataId, serviceImplName, processDefinitionKey");
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessLambdaQueryWrapper.eq(FlowMyBusiness::getDataId, dataId);
        FlowMyBusiness flowMyBusiness = new FlowMyBusiness();
        FlowMyBusiness business = flowMyBusinessService.getOne(flowMyBusinessLambdaQueryWrapper);
        if (business != null) {
            flowMyBusiness = business;
        } else {
            flowMyBusiness.setId(IdUtil.fastSimpleUUID());
        }
        if (processDefinitionId == null) {
            // 以便更新流程
            processDefinitionId = "";
        }
        flowMyBusiness.setTitle(title).setDataId(dataId).setServiceImplName(serviceImplName).setProcessDefinitionKey(processDefinitionKey).setProcessDefinitionId(processDefinitionId);
        if (business != null) {
            return flowMyBusinessService.updateById(flowMyBusiness);
        } else {
            return flowMyBusinessService.save(flowMyBusiness);
        }
    }

    /**
     * 删除流程
     *
     * @param dataId
     * @return
     */
    public boolean delActBusiness(String dataId) {
        boolean hasBlank = StrUtil.hasBlank(dataId);
        if (hasBlank) throw new CustomException("流程关键参数未填完全！dataId");
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessQueryWrapper.eq(FlowMyBusiness::getDataId, dataId);
        FlowMyBusiness one = flowMyBusinessService.getOne(flowMyBusinessQueryWrapper);
        if (one.getProcessInstanceId() != null) {
            try {
                flowInstanceService.delete(one.getProcessInstanceId(), "删除流程");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flowMyBusinessService.remove(flowMyBusinessQueryWrapper);
    }
}
