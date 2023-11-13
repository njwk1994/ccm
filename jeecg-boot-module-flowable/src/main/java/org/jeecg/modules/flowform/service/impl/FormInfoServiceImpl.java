package org.jeecg.modules.flowform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.flowform.entity.FormInfo;
import org.jeecg.modules.flowform.mapper.FormInfoMapper;
import org.jeecg.modules.flowform.model.DicItemModel;
import org.jeecg.modules.flowform.model.FormInfoModel;
import org.jeecg.modules.flowform.model.TreeModel;
import org.jeecg.modules.flowform.service.IFormInfoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("formInfoService")
public class FormInfoServiceImpl extends ServiceImpl<FormInfoMapper, FormInfo> implements IFormInfoService {

    @Override
    public IPage<FormInfoModel> queryPageList(Page page, QueryWrapper queryWrapper) {
        return baseMapper.queryPageList(page, queryWrapper);
    }

    @Override
    public List<DicItemModel> queryDicItems(QueryWrapper queryWrapper) {
        return baseMapper.queryDicItems(queryWrapper);
    }

    @Override
    public List<TreeModel> queryDicTree(QueryWrapper queryWrapper) {
        List<TreeModel> result = new ArrayList<>();
        List<DicItemModel> dicItemList = this.queryDicItems(queryWrapper);
        if (dicItemList != null && dicItemList.size() > 0) {
            for (DicItemModel item : dicItemList) {
                TreeModel tree = new TreeModel();
                tree.setTitle(item.getItemText());
                tree.setKey(item.getItemValue());
                result.add(tree);
            }
        }
        return result;
    }
}
