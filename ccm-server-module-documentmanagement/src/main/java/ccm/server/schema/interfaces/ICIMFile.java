package ccm.server.schema.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ICIMFile extends IObject {

    void setFileName(String fileName) throws Exception;

    String getFileName();

    void setFileExt(String fileExt) throws Exception;

    String getFileExt();

    void setBucketName(String bucketName) throws Exception;

    String getBucketName();

    void setBucketObjName(String bucketObjName) throws Exception;

    String getBucketObjName();

    ICIMFileType getFileType() throws Exception;

    InputStream download() throws Exception;

    boolean upload(MultipartFile multipartFile);
}
