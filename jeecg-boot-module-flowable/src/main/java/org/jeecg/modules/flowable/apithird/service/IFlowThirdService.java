package org.jeecg.modules.flowable.apithird.service;

import org.jeecg.modules.flowable.apithird.business.entity.FlowMyBusiness;
import org.jeecg.modules.flowable.apithird.entity.SysCategory;
import org.jeecg.modules.flowable.apithird.entity.SysDepart;
import org.jeecg.modules.flowable.apithird.entity.SysRole;
import org.jeecg.modules.flowable.apithird.entity.SysUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务层需实现的接口定义<br/>
 * 支撑工作流模块与业务的关联
 *
 * @author pmc
 */
public interface IFlowThirdService {

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户信息
     */
    public SysUser getLoginUser();

    /**
     * 所有用户
     *
     * @return
     */
    List<SysUser> getAllUser();

    /**
     * 通过角色id获取用户
     *
     * @return
     */
    List<SysUser> getUsersByRoleId(String roleId);

    /**
     * 根据用户username查询用户信息
     *
     * @param username
     * @return
     */
    SysUser getUserByUsername(String username);

    /**
     * 获取所有角色
     *
     * @return
     */
    public List<SysRole> getAllRole();

    /**
     * 获取所有流程分类
     *
     * @return
     */
    List<SysCategory> getAllCategory();

    /**
     * 获取所有部门 树形结构
     *
     * @return
     */
    List<SysDepart> getTreeDepart();

    /**
     * 通过用户账号查询部门 name
     *
     * @param username
     * @return 部门 name
     */
    List<String> getDepartNamesByUsername(String username);

    /**
     * 向审批人发送审批通知
     *
     * @param business
     */
    public void sendUserMessage(FlowMyBusiness business);

    public List<SysUser> getUsersByOrganizationId(String organizationId);
}
