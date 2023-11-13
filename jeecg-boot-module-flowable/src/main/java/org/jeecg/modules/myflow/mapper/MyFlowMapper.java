package org.jeecg.modules.myflow.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.myflow.entity.MyFlow;

/**
 * @Description: 测试用户表
 * @Author: jeecg-boot
 * @Date: 2021-11-30
 * @Version: V1.0
 */
public interface MyFlowMapper extends BaseMapper<MyFlow> {

    IPage<MyFlow> getPageList(Page<MyFlow> page, @Param(Constants.WRAPPER) QueryWrapper<MyFlow> queryWrapper);
}
