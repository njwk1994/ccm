package org.jeecg.modules.flowform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.flowform.entity.FormInfo;
import org.jeecg.modules.flowform.model.DicItemModel;
import org.jeecg.modules.flowform.model.FormInfoModel;
import org.jeecg.modules.flowform.model.TreeModel;

import java.util.List;

public interface IFormInfoService extends IService<FormInfo> {

    IPage<FormInfoModel> queryPageList(Page page, @Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    List<DicItemModel> queryDicItems(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);

    List<TreeModel> queryDicTree(@Param(Constants.WRAPPER) QueryWrapper queryWrapper);
}
