package ccm.server.zip.entity;

import lombok.Getter;

/**
 * 压缩包内置文件类型
 *
 * @author HuangTao
 * @version 1.0
 * @since 2023/5/14 13:55
 */
@Getter
public enum ZipInnerFileType {

    /**
     * 文件
     */
    FILE,
    /**
     * 文件夹
     */
    DIRECTORY;
}
