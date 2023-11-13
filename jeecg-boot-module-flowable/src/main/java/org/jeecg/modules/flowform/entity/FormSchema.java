package org.jeecg.modules.flowform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 表单
 * @Author: HQ
 * @Date: 2022-04-01
 * @Version: V1.0
 */
@Data
@TableName("flow_form_schema")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "FormRelation对象", description = "表单关联表")
public class FormSchema implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "表单ID")
    private String infoId;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "表单JSON")
    private String scheme;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "创建人登录名称")
    private String createBy;
}
