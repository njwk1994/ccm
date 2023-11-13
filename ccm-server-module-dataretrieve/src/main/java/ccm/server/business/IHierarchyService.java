package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/2/21 18:22
 */
public interface IHierarchyService {

    /* ******************************************************* 树配置 方法 Start ******************************************************* */

    /**
     * 根据表单生成属性集合
     * <p>用于给配置项设置TargetProperty</p>
     *
     * @param classDef 需要获取属性的ClassDef
     * @return
     * @throws Exception
     */
    List<ObjectDTO> getObjectFormPropertiesForConfigurationItem(String classDef) throws Exception;

    /**
     * 获取对应对象的树配置及配置项的表单
     *
     * @param formPurpose
     * @param targetClassDef
     * @return
     * @throws Exception
     */
    Map<String, Object> getObjectHierarchyConfigurationFormWithItem(String formPurpose, String targetClassDef) throws Exception;

    /**
     * 获取对应对象的树表单
     *
     * @param formPurpose
     * @param targetClassDef 目标对象
     * @return
     * @throws Exception
     */
    ObjectDTO getObjectHierarchyConfigurationForm(String formPurpose, String targetClassDef) throws Exception;

    /**
     * 获取当前用户树配置
     *
     * @param targetClassDef
     * @param pageRequest
     * @return
     * @throws Exception
     */
    IObjectCollection getMyHierarchyConfigurations(HttpServletRequest request, String targetClassDef, PageRequest pageRequest) throws Exception;

    IObject getHierarchyConfigurationByOBID(String obid) throws Exception;

    IObject getDefaultHierarchyConfiguration(String targetDef) throws Exception;

    ObjectDTO getHierarchyConfigurationByClassDefAndOBID(String obid, String classDef) throws Exception;

    IObject createHierarchyConfiguration(JSONObject requestBody, String targetDef) throws Exception;

    IObject createHierarchyConfigurationWithItems(JSONObject requestBody, String targetDef, boolean withTransaction) throws Exception;

    void deleteHierarchyConfiguration(String obid) throws Exception;

    void updateHierarchyConfiguration(ObjectDTO toUpdate) throws Exception;

    /* ******************************************************* 树配置 方法 End ******************************************************* */
    /* ******************************************************* 树配置项 方法 Start ******************************************************* */

    /**
     * 获取对应对象的树表单
     *
     * @param formPurpose
     * @return
     * @throws Exception
     */
    ObjectDTO getHierarchyConfigurationItemForm(String formPurpose) throws Exception;

    IObjectCollection getItemsByHierarchyConfigurationOBID(String hierarchyConfigurationOBID) throws Exception;

    IObject getHierarchyConfigurationItemByOBID(String obid) throws Exception;

    IObject createHierarchyConfigurationItemWithConfigurationOBID(String hierarchyConfigurationOBID, ObjectDTO itemDTO) throws Exception;

    IObject createHierarchyConfigurationItems(String configurationOBID, ObjectDTO toCreateHierarchyItem) throws Exception;

    void deleteHierarchyConfigurationItem(String itemOBID) throws Exception;

    void updateHierarchyConfigurationItem(ObjectDTO toUpdateItem) throws Exception;

    /**
     * 生成配置项
     *
     * @return
     * @throws Exception
     */
    void createHierarchyConfigurationItems(IObject hierarchyConfigurationByOBID, List<ObjectDTO> itemDTOs) throws Exception;
    /* ******************************************************* 树配置项 方法 End ******************************************************* */

    /**
     * 生成树
     *
     * @param hierarchyConfigurationOBID
     * @return
     */
    HierarchyObjectDTO generateHierarchy(String hierarchyConfigurationOBID, String classDef) throws Exception;

    /**
     * 根据前端传入生成目录树
     *
     * @return
     */
    HierarchyObjectDTO generateHierarchyWithoutConf(String classDefinitionUid, String[] propertyDefinitionUidsArray) throws Exception;
}
