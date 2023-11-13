package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.common.HierarchyObjectDTO;
import ccm.server.enums.PackageRevProcessingMode;
import ccm.server.enums.operationPurpose;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 14:36
 */
public interface ICCMPressureTestPackageService {

    /* *****************************************  树结构方法 start  ***************************************** */
    ObjectDTO getPressureTestPackageHierarchyConfigurationForm(String formPurpose) throws Exception;

    List<ObjectDTO> getPressureTestPackageFormPropertiesForConfigurationItem() throws Exception;

    Map<String, Object> getPressureTestPackageHierarchyConfigurationFormWithItem(String formPurpose) throws Exception;

    IObject createPressureTestPackageHierarchyConfigurationWithItems(JSONObject requestBody) throws Exception;

    void deletePressureTestPackageHierarchyConfiguration(String hierarchyConfigurationOBID) throws Exception;

    void updatePressureTestPackageHierarchyConfiguration(ObjectDTO hierarchyConfiguration) throws Exception;

    IObjectCollection getMyPressureTestPackageHierarchyConfigurations(HttpServletRequest request,PageRequest pageRequest) throws Exception;

    IObjectCollection getPressureTestPackageHierarchyConfigurationItems(String hierarchyConfigurationOBID) throws Exception;

    IObject createPressureTestPackageHierarchyConfigurationItem(String hierarchyConfigurationOBID, ObjectDTO
            hierarchyConfigurationItemDTO) throws Exception;

    HierarchyObjectDTO generateHierarchyByPressureTestPackagesAndConfiguration(String hierarchyConfigurationOBID) throws Exception;

    IObjectCollection getPressureTestPackagesFromHierarchyNode(HierarchyObjectDTO selectedNode, PageRequest pageRequest) throws Exception;
    /* *****************************************  树结构方法 end  ***************************************** */

    /* *****************************************  试压包方法 start  ***************************************** */
    ObjectDTO getPressureTestPackageForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception;

    IObjectCollection getPressureTestPackages(PageRequest pageRequest) throws Exception;

    IObjectCollection getPressureTestPackagesWithItems(ObjectDTO items, PageRequest pageRequest) throws Exception;

    IObject getPressureTestPackagesByOBID(String obid) throws Exception;

    IObject getPressureTestPackagesByUID(String uid) throws Exception;

    IObject createPressureTestPackage(ObjectDTO objectDTO) throws Exception;

    void updatePressureTestPackage(ObjectDTO objectDTO) throws Exception;

    void deletePressureTestPackage(String obid) throws Exception;

    /**
     * 更新试压包状态
     *
     * @param uid
     * @param tptpStatus
     * @throws Exception
     */
    void updateTPTPStatusByUID(String uid, String tptpStatus) throws Exception;
    /* *****************************************  试压包方法 end  ***************************************** */

    /* *****************************************  试压包图纸方法 start  ***************************************** */
    IObjectCollection getRelatedDocuments(String pressureTestPackageOBID) throws Exception;

    IObjectCollection getSelectableDocuments(String obid, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception;

    void assignDocumentsToPressureTestPackage(String pressureTestPackage, List<String> providedDocuments) throws Exception;

    Boolean removeDocumentsFromPressureTestPackage(String packageId, String documentIds) throws Exception;
    /* *****************************************  试压包图纸方法 end  ***************************************** */

    /* *****************************************  试压包材料方法 start  ***************************************** */
    ObjectDTO getComponentForm(operationPurpose formPurpose, ObjectDTO mainObject) throws Exception;

    IObjectCollection getRelatedComponents(String pressureTestPackageOBID) throws Exception;

    IObjectCollection getRelatedWorkSteps(String pressureTestPackageOBID) throws Exception;

    IObjectCollection getSelectableComponentsForPressureTestPackage(String pressureTestPackageOBID, String documentOBID, String classDefinitionUID, FiltersParam filtersParam, OrderByParam orderByParam, PageRequest pageRequest) throws Exception;

    /**
     * 获取最新版加设图纸中同名组件
     * @param componentNames
     * @return
     * @throws Exception
     */
    Map<String, List<String>> getLatestComponentOBIDsAndDocOBIDsByNamesInShopDesignDocument(String componentNames) throws Exception;

    void assignComponentsToPressureTestPackage(String pressureTestPackageOBID, List<String> providedComponents) throws Exception;

    Boolean removeComponentsFromPackage(String packageId, String componentIds) throws Exception;
    /* *****************************************  试压包材料方法 end  ***************************************** */

    /* *****************************************  试压包文件方法 start  ***************************************** */
    ObjectDTO toPressureTestPackageFile(String fileVersion, String fileCount, String fileNotes);

    IObject getPressureTestPackagesFileByOBID(String obid) throws Exception;

    IObjectCollection getPressureTestPackagesFilesByOBIDs(String obids) throws Exception;

    IObject createPressureTestPackageFile(ObjectDTO toCreatePressureTestPackageFile) throws Exception;

    String saveFile(MultipartFile file, String bizPath, String pressureTestPackageId, ObjectDTO pressureTestPackageFile) throws Exception;

    Boolean deleteFile(String pressureTestPackageFileIds) throws Exception;
    /* *****************************************  试压包文件方法 end  ***************************************** */

    /* *****************************************  试压包报告方法 start  ***************************************** */

    /* *****************************************  试压包报告方法 end  ***************************************** */

    /* *****************************************  试压包审批方法 start  ***************************************** */

    /* *****************************************  试压包审批方法 end  ***************************************** */
    /* ******************************************************* 试压包-升版方法 Start ******************************************************* */

    /**
     * 试压包升版处理
     *
     * @param pressureTestPackageOBID
     * @param mode
     * @throws Exception
     */
    void pressureTestPackageRevisionHandler(String pressureTestPackageOBID, PackageRevProcessingMode mode) throws Exception;
    /* ******************************************************* 试压包-升版方法 End ******************************************************* */
}
