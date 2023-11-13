package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;

/**
 * 标段管理
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/6/13 10:51
 */
public interface ICCMBidSectionService {

    /**
     * 获取条件过滤分页标段数据
     *
     * @param filtersParam
     * @param orderByParam
     * @param pageRequest
     * @return
     * @throws Exception
     */
    IObjectCollection getBidSections(FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception;

    /**
     * 根据OBID获取标段
     *
     * @param obid
     * @return
     * @throws Exception
     */
    IObject getBidSectionByOBID(String obid) throws Exception;

    /**
     * 创建标段
     *
     * @param objectDTO
     * @return
     * @throws Exception
     */
    IObject createBidSection(ObjectDTO objectDTO) throws Exception;

    /**
     * 更新标段
     *
     * @param objectDTO
     * @return
     * @throws Exception
     */
    IObject updateBidSection(ObjectDTO objectDTO) throws Exception;

    /**
     * 删除标段
     *
     * @param bidSectionOBID
     * @return
     * @throws Exception
     */
    void deleteBidSection(String bidSectionOBID) throws Exception;

    /**
     * 建立标段和施工区域关联关系
     *
     * @param bsOBID
     * @param cwaOBID
     * @return
     * @throws Exception
     */
    void genRelBidSection2CWA(String bsOBID, String cwaOBID) throws Exception;

    /**
     * 删除标段和施工区域关联关系
     *
     * @param bsOBID
     * @param cwaOBID
     * @throws Exception
     */
    void deleteRelBidSection2CWA(String bsOBID, String cwaOBID) throws Exception;

    /**
     * 获取可选择添加的施工区域
     *
     * @return
     * @throws Exception
     */
    IObjectCollection getSelectableCWA() throws Exception;

    /**
     * 新建施工区域
     *
     * @param objectDTO
     * @return
     * @throws Exception
     */
    IObject createCWA(ObjectDTO objectDTO) throws Exception;
}
