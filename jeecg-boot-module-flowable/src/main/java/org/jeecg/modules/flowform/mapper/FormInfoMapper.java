package org.jeecg.modules.flowform.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.flowform.entity.FormInfo;
import org.jeecg.modules.flowform.model.DicItemModel;
import org.jeecg.modules.flowform.model.FormInfoModel;

import java.util.List;

public interface FormInfoMapper extends BaseMapper<FormInfo> {

    IPage<FormInfoModel> queryPageList(Page<FormInfoMapper> page, @Param(Constants.WRAPPER) QueryWrapper<FormInfoMapper> queryWrapper);

    List<DicItemModel> queryDicItems(@Param(Constants.WRAPPER) QueryWrapper<DicItemModel> queryWrapper);
}
