package org.jeecg.modules.flowable.apithird.business.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.flowable.apithird.business.entity.FlowMyBusiness;
import org.jeecg.modules.flowable.apithird.business.mapper.FlowMyBusinessMapper;
import org.jeecg.modules.flowable.apithird.business.service.IFlowMyBusinessService;
import org.jeecg.modules.flowable.common.exception.CustomException;
import org.springframework.stereotype.Service;

/**
 * @Description: 流程业务扩展表
 * @Author: jeecg-boot
 * @Date: 2021-11-25
 * @Version: V1.0
 */
@Service
public class FlowMyBusinessServiceImpl extends ServiceImpl<FlowMyBusinessMapper, FlowMyBusiness> implements IFlowMyBusinessService {

    public FlowMyBusiness getByDataId(String dataId) {
        LambdaQueryWrapper<FlowMyBusiness> flowMyBusinessLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flowMyBusinessLambdaQueryWrapper.eq(FlowMyBusiness::getDataId, dataId);
        //如果保存数据前未调用必调的FlowCommonService.initActBusiness方法，就会有问题
        FlowMyBusiness business = this.getOne(flowMyBusinessLambdaQueryWrapper);
        return business;
    }

    public boolean createActBusiness(String title, String dataId, String serviceImplName, String processDefinitionKey, String processDefinitionId) {
        boolean hasBlank = StrUtil.hasBlank(title, dataId, serviceImplName, processDefinitionKey);
        if (hasBlank) throw new CustomException("流程关键参数未填完全！dataId, serviceImplName, processDefinitionKey");
        FlowMyBusiness flowMyBusiness = new FlowMyBusiness();
        flowMyBusiness.setId(IdUtil.fastSimpleUUID());
        if (StringUtils.isEmpty(processDefinitionId)) {
            processDefinitionId = "";
        }
        flowMyBusiness.setTitle(title).setDataId(dataId).setServiceImplName(serviceImplName).setProcessDefinitionKey(processDefinitionKey).setProcessDefinitionId(processDefinitionId);
        return this.save(flowMyBusiness);
    }

    public void deleteFlowMyBusiness(String Id) {
        this.baseMapper.deleteById(Id);
    }
}
