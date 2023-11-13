package org.jeecg.modules.flowable.apithird.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门表
 * <p>
 *
 * @Author Steve
 * @Since 2019-01-22
 */
@Data
public class SysDepart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 父机构ID
     */
    private String parentId;

    /**
     * 机构/部门名称
     */
    private String departName;

    /**
     * 英文名
     */
    private String departNameEn;

    /**
     * 缩写
     */
    private String departNameAbbr;

    /**
     * 排序
     */
    private Integer departOrder;

    /**
     * 描述
     */
    private String description;

    /**
     * 机构类别 1公司，2组织机构，2岗位
     */
    private String orgCategory;

    /**
     * 机构类型
     */
    private String orgType;

    /**
     * 机构编码
     */
    private String orgCode;

    /**
     * 子级
     */
    private List<SysDepart> children = new ArrayList<>();
}
