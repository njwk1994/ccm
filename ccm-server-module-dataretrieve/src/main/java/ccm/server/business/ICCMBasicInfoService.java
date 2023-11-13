package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2021/12/29 13:00
 */
public interface ICCMBasicInfoService {

    /* =========================================== 施工分类 start =========================================== */

    /**
     * 获取施工分类表单
     *
     * @param formPurpose 表单类型
     * @return
     * @throws Exception
     */
    ObjectDTO getConstructionTypeForm(String formPurpose) throws Exception;

    /**
     * 获取所有施工分类
     *
     * @param pageRequest pageIndex 当前页 pageSize  每页条数
     * @return 施工分类的分页数据
     * @throws Exception
     */
    IObjectCollection getConstructionTypes(PageRequest pageRequest) throws Exception;

    /**
     * 根据OBID获取施工分类
     *
     * @param obid pageIndex 当前页 pageSize  每页条数
     * @return 施工分类的分页数据
     * @throws Exception
     */
    IObject getConstructionTypeByOBID(String obid) throws Exception;

    /**
     * 新建施工分类
     *
     * @param toCreateConstructionTypeDTO 施工分类
     * @return 施工分类ID
     * @throws Exception
     */
    IObject createConstructionType(ObjectDTO toCreateConstructionTypeDTO) throws Exception;

    /**
     * 删除施工分类
     *
     * @param constructionTypeOBID 施工分类OBID
     * @throws Exception
     */
    void deleteConstructionType(String constructionTypeOBID) throws Exception;

    /**
     * 更新施工分类
     *
     * @param toUpdateConstructionType 施工分类
     * @throws Exception
     */
    void updateConstructionType(ObjectDTO toUpdateConstructionType) throws Exception;

    /**
     * 获取施工分类下的设计类型
     *
     * @param constructionTypeId 施工分类ID
     * @return 当前施工分类下的设计数据类型的分页数据
     * @throws Exception
     */
    IObjectCollection getDesignTypesUnderConstructionType(String constructionTypeId, PageRequest pageRequest) throws Exception;

    /**
     * 添加设计数据类型到施工分类
     *
     * @param constructionType 施工分类
     * @param designType       设计数据类型
     * @return 设计数据类型ID
     * @throws Exception
     */
    void addDesignTypeIntoConstructionType(String constructionType, ObjectDTO designType) throws Exception;
    /* =========================================== 施工分类 end =========================================== */
    /* =========================================== 设计数据类型 start =========================================== */

    /**
     * 获取设计类型表单
     *
     * @param formPurpose
     * @return
     * @throws Exception
     */
    ObjectDTO getDesignTypeForm(String formPurpose) throws Exception;

    /**
     * 新建设计类型
     *
     * @param designType
     * @return
     * @throws Exception
     */
    IObject createDesignType(ObjectDTO designType) throws Exception;

    /**
     * 删除设计类型(同时删除关联关系)
     *
     * @param designTypeId 设计类型ID
     * @throws Exception
     */
    void deleteDesignType(String designTypeId) throws Exception;

    /**
     * 更新设计类型
     *
     * @param designType
     * @throws Exception
     */
    void updateDesignType(ObjectDTO designType) throws Exception;

    /* =========================================== 设计数据类型 end =========================================== */
}
