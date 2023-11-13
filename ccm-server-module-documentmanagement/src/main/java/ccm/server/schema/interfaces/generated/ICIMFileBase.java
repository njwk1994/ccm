package ccm.server.schema.interfaces.generated;


import ccm.server.args.suppressibleArgs;
import ccm.server.enums.*;
import ccm.server.schema.interfaces.*;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.service.ICCMDocumentBusinessService;
import ccm.server.utils.SchemaUtility;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
public abstract class ICIMFileBase extends InterfaceDefault implements ICIMFile {

    public ICIMFileBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMFile.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setFileName(String fileName) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.FileName.toString(), fileName);
    }

    @Override
    public String getFileName() {
        return this.getProperty(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.FileName.toString()).Value().toString();
    }

    @Override
    public void setFileExt(String fileExt) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.FileExt.toString(), fileExt);
    }

    @Override
    public String getFileExt() {
        return this.getProperty(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.FileExt.toString()).Value().toString();
    }

    @Override
    public void setBucketName(String bucketName) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.BucketName.toString(), bucketName);
    }

    @Override
    public String getBucketName() {
        return this.getProperty(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.BucketName.toString()).Value().toString();
    }

    @Override
    public void setBucketObjName(String bucketObjName) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.BucketObjName.toString(), bucketObjName);
    }

    @Override
    public String getBucketObjName() {
        return this.getProperty(interfaceDefinitionType.ICIMFile.toString(), propertyDefinitionType.BucketObjName.toString()).Value().toString();
    }

    @Override
    public void OnCreated(suppressibleArgs e) throws Exception {
        super.OnCreated(e);
        String fileExt = this.getFileExt();
        if (!StringUtils.isEmpty(fileExt)) {
            IObject fileType = SchemaUtility.getObjectWithInterfaceDefAndCriteria(interfaceDefinitionType.ICIMFileType.toString(), fileExt);
            if (fileType == null) {
                fileType = SchemaUtility.newIObject(classDefinitionType.CIMFileType.toString(), fileExt, "type is " + fileExt, domainInfo.ADMIN.toString(), "FT_" + fileExt);
                if (fileType == null) throw new Exception("创建FileType失败!");
                ICIMFileType icimFileType = fileType.toInterface(ICIMFileType.class);
                if (icimFileType == null) throw new Exception("转换ICIMFileType失败!");
                icimFileType.setCIMFileEditable(false);
                icimFileType.setCIMFileViewable(true);
                icimFileType.setCIMFileExtension(fileExt);
                fileType.ClassDefinition().FinishCreate(fileType);
            }
            IRel rel = SchemaUtility.newRelationship(relDefinitionType.CIMFileFileType.toString(), this, fileType, true);
            rel.ClassDefinition().FinishCreate(rel);
        }
    }

    @Override
    public void OnDeleted(suppressibleArgs e) throws Exception {
        super.OnDeleted(e);
        ICCMDocumentBusinessService documentManagementService = SpringContextUtils.getBean(ICCMDocumentBusinessService.class);
        documentManagementService.deleteFileByCIMFile(this);
    }

    @Override
    public InputStream download() throws Exception {
        ICCMDocumentBusinessService documentManagementService = SpringContextUtils.getBean(ICCMDocumentBusinessService.class);
        return documentManagementService.downloadFile(this.getBucketObjName());
    }

    @Override
    public ICIMFileType getFileType() throws Exception {
        IObject object = this.GetEnd1Relationships().GetRel(relDefinitionType.CIMFileFileType.toString(), false).GetEnd2();
        return object != null ? object.toInterface(ICIMFileType.class) : null;
    }

    @Override
    public boolean upload(MultipartFile multipartFile) {
        if (multipartFile != null) {
            try {
                ICCMDocumentBusinessService documentManagementService = SpringContextUtils.getBean(ICCMDocumentBusinessService.class);
                String minioObjName = documentManagementService.uploadFileToMinio(multipartFile,null);
                if (!StringUtils.isEmpty(minioObjName) && !minioObjName.startsWith("error")) {
                    ICIMMinioConfig minioConfig = documentManagementService.getDefaultConfig();
                    SchemaUtility.beginTransaction();
                    this.BeginUpdate();
                    this.setBucketName(minioConfig.getCIMMinioBucket());
                    this.setBucketObjName(minioObjName);
                    this.FinishUpdate();
                    SchemaUtility.commitTransaction();
                    return true;
                }
            } catch (Exception ex) {
                log.error(ex.toString());
                return false;
            }
        }
        return true;
    }
}
