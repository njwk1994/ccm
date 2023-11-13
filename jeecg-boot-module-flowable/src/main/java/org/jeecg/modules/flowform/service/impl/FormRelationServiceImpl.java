package org.jeecg.modules.flowform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.flowform.entity.FormRelation;
import org.jeecg.modules.flowform.mapper.FormRelationMapper;
import org.jeecg.modules.flowform.service.IFormRelationService;
import org.springframework.stereotype.Service;

@Service("formRelationService")
public class FormRelationServiceImpl extends ServiceImpl<FormRelationMapper, FormRelation> implements IFormRelationService {

}
