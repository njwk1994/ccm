package org.jeecg.modules.flowform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.flowform.entity.FormSchema;
import org.jeecg.modules.flowform.mapper.FormSchemaMapper;
import org.jeecg.modules.flowform.service.IFormSchemaService;
import org.springframework.stereotype.Service;

@Service("formSchemaService")
public class FormSchemaServiceImpl extends ServiceImpl<FormSchemaMapper, FormSchema> implements IFormSchemaService {

}
