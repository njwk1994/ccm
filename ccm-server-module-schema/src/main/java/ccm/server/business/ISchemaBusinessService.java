package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.dto.base.ObjectXmlDTO;
import ccm.server.dto.base.OptionItemDTO;
import ccm.server.enums.relCollectionTypes;
import ccm.server.model.*;
import ccm.server.module.service.base.IInternalService;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ISchemaBusinessService extends IInternalService {
    LiteCriteria liteCriteria(String queryParam) throws Exception;

    void reloadCache() throws Exception;

    IClassDef createClassDef(String name, String description, String systemIDPattern, String uniqueKeyDef, boolean isConfigControlled) throws Exception;

    IInterfaceDef createInterfaceDef(String name, String description, String displayAs) throws Exception;

    IPropertyDef createPropertyDef(String name, String description, String exposedInterfaceDef, String scopedByPropertyType) throws Exception;

    IRelDef createRelDef(String name, String description, String uid1, String uid2, String role1, String role2, int min1, String min2, String max1, String max2) throws Exception;

    IEnumListType createEnumListType(String name, String description, int enumNumber) throws Exception;

    boolean setImpliesForInterfaceDefs(String parentInterfaceDef, String impliesInterfaceDef) throws Exception;

    boolean setRealizes(String classDef, String interfaceDef) throws Exception;

    boolean setContains(String parentEnumListType, String subEntry) throws Exception;

    void loadSchemaXml(MultipartFile file) throws Exception;

    IObject getSchemaObjectByUID(String pstrUID) throws Exception;

    IObjectCollection getClassDefs() throws Exception;

    IObjectCollection getInterfaceDefs() throws Exception;

    IObjectCollection getPropertyDefs() throws Exception;

    IObjectCollection getRelDefs() throws Exception;

    IObjectCollection getEnumListTypes() throws Exception;

    IObjectCollection getEnumEnums() throws Exception;

    IObjectCollection getClassDefs(String pstrNameCriteria, PageRequest pageRequest) throws Exception;

    IObjectCollection getInterfaceDefs(String pstrNameCriteria, PageRequest pageRequest) throws Exception;

    IObjectCollection getPropertyDefs(String pstrNameCriteria, PageRequest pageRequest) throws Exception;

    IObjectCollection getRelDefs(String pstrNameCriteria, PageRequest pageRequest) throws Exception;

    IObjectCollection getEnumListTypes(String pstrNameCriteria, PageRequest pageRequest) throws Exception;

    IObjectCollection getEnumEnums(String pstrNameCriteria, PageRequest pageRequest) throws Exception;

    boolean deleteObject(String obid, String classDefinitionUID, boolean needTransaction) throws Exception;

    void generateForms() throws Exception;

    ObjectDTO generateDefaultPopup(String classDefinitionUID) throws Exception;

    IObject generateForm(String classDefinitionUID) throws Exception;

    IObjectCollection getRelsForObject(String obid, String classDefinition, relCollectionTypes collectionTypes) throws Exception;

    IObjectCollection expandRelationships(String objUID, String classDefinitionUID, String relDef) throws Exception;

    ICIMForm getForm(String formPurpose, String classDefinitionUID) throws Exception;

    ICIMForm getFormByName(String formPurpose, String formName);

    IObjectCollection generalQuery(String classDefinitionUID, String name, String description, int pageIndex, int pageSize) throws Exception;

    void expansionPaths(IObjectCollection pcolItems, List<String> pcolExpansionPaths) throws Exception;

    IObjectCollection generalQuery(String classDefinitionUID, int pageIndex, int pageSize, List<OrderByWrapper> sortedDefUIDs, Map<String, String> pMapCriteria) throws Exception;

    IObjectCollection expandObjs(IObjectCollection startObjs, String relDefUid, String... classDefinitionUids) throws Exception;

    IObjectCollection liteQuery(String classDefinitionUID, int pageIndex, int pageSize, List<OrderByWrapper> sortedDefUIDs, Map<String, String> criterions, ObjectDTO form) throws Exception;

    IObjectCollection generalQuery(String classDefinitionUID, int pageIndex, int pageSize, List<OrderByWrapper> sortedDefUIDs, Map<String, String> pMapCriteria, List<String> expansionPaths) throws Exception;

    IObject generalUpdate(ObjectDTO objectDTO) throws Exception;

    IObjectCollection generalUpdate(List<ObjectDTO> objectDTOs) throws Exception;

    IObject generalCreate(ObjectDTO objectDTO) throws Exception;

    IObjectCollection getSchemaObjects(@NotNull String pstrSchemaType, String pstrNameCriteria, String pstrClassDefinitionUID, @NotNull PageRequest pageRequest) throws Exception;

    String getObjectsXmlInfo(JSONArray jsonArray) throws Exception;

    List<ObjectXmlDTO> getObjRelatedObjsAndRels(@NotNull JSONObject jsonObject) throws Exception;

    void generateXmlFile(@NotNull JSONArray pcolSelObject, HttpServletResponse response) throws Exception;

    LoaderReport loadObjectsByJSONObject(JSONObject jsonObject) throws Exception;

    IObjectCollection getRelsByRelDef(List<String> relDefUIDs) throws Exception;

    IObject createOrUpdateObjectByProperties(String pstrPropertiesArray, boolean pblnNeedTransaction) throws Exception;

    IObject createOrUpdateObjectByProperties(List<ObjectItemDTO> pcolProperties, boolean pblnNeedTransaction) throws Exception;

    boolean deleteRelationship(String relDefUID, String obid) throws Exception;

    boolean deleteObjects(String pstrOBIDs, String pstrClassDefinitionUID) throws Exception;

    ICIMUser createUser(String loginUserName) throws Exception;

    void cleanScope(String userName) throws Exception;

    boolean dropUser(String loginUserName) throws Exception;

    IObject createConfigurationItem(ObjectDTO objectDTO) throws Exception;

    IObject updateConfigurationItem(ObjectDTO objectDTO) throws Exception;

    boolean deleteConfigurationItem(ObjectDTO objectDTO) throws Exception;

    void ensureTables() throws Exception;

    List<KeyValuePair> getUserWithDefaultSettings(String userName) throws Exception;

    List<PropertyHierarchyVo> summaryProperty(List<String> classDefinitionUids, String... propertyDefinitionUids) throws Exception;

    /**
     * 根据关联关系一端和二端的uid删除关联关系
     *
     * @param relDef
     * @param uid1
     * @param uid2
     * @return
     */
    boolean deleteRelByUid(String relDef, String uid1, String uid2);

    /**
     * 通用根据Excel模板导入标准数据
     *
     * @param file
     * @return
     * @throws Exception
     */
    LoaderReport importDataByExcelTemplate(MultipartFile file, Boolean withSchemaUidRule) throws Exception;

    /**
     * 根据通用查询条件导出数据
     *
     * @param jsonObject
     * @param response
     */
    void exportExcelByForm(JSONObject jsonObject, HttpServletResponse response);

    /**
     * 修改用户信息
     *
     * @param userName
     * @param properties
     */
    void updateUserInfo(String userName, JSONArray properties) throws Exception;

    List<OptionItemDTO> getSearchRelationshipOptions(JSONObject param) throws Exception;
}
