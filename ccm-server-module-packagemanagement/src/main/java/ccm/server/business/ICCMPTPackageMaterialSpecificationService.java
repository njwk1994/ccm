package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectDTOCollection;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ICCMPTPackageMaterialSpecificationService {
    ObjectDTO createOrUpdatePTPMaterialSpecification(String pstrPTPMS, String lstrPTPMSItems) throws Exception;

    IObject createOrUpdatePTPMaterialSpecificationWithIObjectStyle(String pstrPTPMS, String lstrPTPMSItems) throws Exception;

    ObjectDTO createOrUpdatePTPMaterialTemplate(String pstrProperties, String pstrPTPMSOBID) throws Exception;

    IObject createOrUpdatePTPMaterialTemplateWithIObjectStyle(String pstrProperties, String pstrPTPMSOBID) throws Exception;

    Boolean relatePTPMaterialsForPTPMS(String pstrPTPMaterialOBIDs, String pstrPTPMSOBID) throws Exception;

    void importPTPMSFormExcel(@NotNull MultipartFile file) throws Exception;

    IObjectCollection getPTPMaterialsTemplatesForDesignObjs(String pstrOBIDs, String pstrClassDefUID) throws Exception;

    ObjectDTOCollection getPTPMaterialsTemplatesForDesignObjsWithDTOStyle(String pstrOBIDs, String pstrClassDefUID, PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam) throws Exception;

    ObjectDTOCollection createPTPackageMaterials(String ptpackageOBID, String ptpMaterialTemplateOBIDs, String count) throws Exception;

    Boolean deletePTPMaterials(String ptpMaterialTemplateOBIDs) throws Exception;

    ObjectDTOCollection getPTPMaterialTemplatesByPTPackage(String pstrPTPackageOBID, PageRequest pageRequest, FiltersParam filtersParam, OrderByParam orderByParam) throws Exception;

    void generatePTPMSTemplateData(HttpServletResponse response, String pstrPTPMSOBID) throws Exception;
}
