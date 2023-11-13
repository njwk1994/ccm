package ccm.server.zip.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2023/3/16 10:55
 */
@Data
@Slf4j
public class ZipInnerFile implements Serializable {

    /**
     * 压缩文件名 xxxx
     */
    private String zipName;
    /**
     * 文件路径 xxx/xxx.xx
     */
    private String filePath;
    /**
     * 文件名 xxx.xx
     */
    private String fileName;
    /**
     * 带压缩文件名称的文件路径
     * zipName/filePath
     */
    private String fullFilePath;
    /**
     * 具体文件
     */
    private MultipartFile file;

    /**
     * 文件类型
     */
    private ZipInnerFileType fileType;
}
