package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 移交邮件管理
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/16 9:10
 */
public interface ICCMHandoverMailService {

    /**
     * 获取邮件箱类型
     * <p>
     * 草稿箱,发件箱,收件箱
     * </p>
     *
     * @return
     */
    IObjectCollection getMailBoxType() throws Exception;

    /**
     * 获取对应的邮件
     *
     * @param filtersParam
     * @param orderByParam
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws Exception
     */
    IObjectCollection getMails(FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception;

    /**
     * 新建邮件
     *
     * @param objectDTO
     * @throws Exception
     */
    IObject createMail(ObjectDTO objectDTO) throws Exception;

    /**
     * 批量删除邮件
     *
     * @param mailOBIDs
     * @throws Exception
     */
    void removeMail(String mailOBIDs) throws Exception;

    /**
     * 批量发送邮件
     *
     * @param mailOBIDs
     * @throws Exception
     */
    void sendMails(String mailOBIDs,String userIds,String roleIds) throws Exception;

    /**
     * 批量接收邮件
     *
     * @param mailOBIDs
     * @throws Exception
     */
    void receiveMails(String mailOBIDs) throws Exception;

    /**
     * 上传文件
     *
     * @param file
     * @param fileOBID
     * @throws Exception
     */
    void uploadFile(MultipartFile file, String fileOBID) throws Exception;

    /**
     * 下载文件
     *
     * @param fileOBID
     * @throws Exception
     */
    void downloadFile(String fileOBID, HttpServletResponse response) throws Exception;

    /**
     * 删除文件
     *
     * @param mailOBID
     * @param fileOBIDs
     * @throws Exception
     */
    void deleteFile(String mailOBID, String fileOBIDs) throws Exception;

    /**
     * 获取可打包的移交单
     *
     * @return
     * @throws Exception
     */
    IObjectCollection getToPackage() throws Exception;

    /**
     * 下载移交包
     *
     * @param mailOBID
     * @throws Exception
     */
    void packageToDownload(String mailOBID, HttpServletResponse response) throws Exception;

}
