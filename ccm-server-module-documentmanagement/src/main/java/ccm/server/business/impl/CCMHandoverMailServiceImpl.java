package ccm.server.business.impl;

import ccm.server.business.ICCMHandoverMailService;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByParam;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/21 9:10
 */
@Slf4j
@Service
public class CCMHandoverMailServiceImpl implements ICCMHandoverMailService {


    /**
     * 获取邮件箱类型
     * <p>
     * 草稿箱,发件箱,收件箱
     * </p>
     *
     * @return
     */
    @Override
    public IObjectCollection getMailBoxType() throws Exception {
        return null;
    }

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
    @Override
    public IObjectCollection getMails(FiltersParam filtersParam, OrderByParam orderByParam, int pageIndex, int pageSize) throws Exception {
        return null;
    }

    /**
     * 新建邮件
     *
     * @param objectDTO
     * @throws Exception
     */
    @Override
    public IObject createMail(ObjectDTO objectDTO) throws Exception {
        return null;
    }

    /**
     * 批量删除邮件
     *
     * @param mailOBIDs
     * @throws Exception
     */
    @Override
    public void removeMail(String mailOBIDs) throws Exception {

    }

    /**
     * 批量发送邮件
     *
     * @param mailOBIDs
     * @param userIds
     * @param roleIds
     * @throws Exception
     */
    @Override
    public void sendMails(String mailOBIDs, String userIds, String roleIds) throws Exception {

    }

    /**
     * 批量接收邮件
     *
     * @param mailOBIDs
     * @throws Exception
     */
    @Override
    public void receiveMails(String mailOBIDs) throws Exception {

    }

    /**
     * 上传文件
     *
     * @param file
     * @param fileOBID
     * @throws Exception
     */
    @Override
    public void uploadFile(MultipartFile file, String fileOBID) throws Exception {

    }

    /**
     * 下载文件
     *
     * @param fileOBID
     * @param response
     * @throws Exception
     */
    @Override
    public void downloadFile(String fileOBID, HttpServletResponse response) throws Exception {

    }

    /**
     * 删除文件
     *
     * @param mailOBID
     * @param fileOBIDs
     * @throws Exception
     */
    @Override
    public void deleteFile(String mailOBID, String fileOBIDs) throws Exception {

    }

    /**
     * 获取可打包的移交单
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getToPackage() throws Exception {
        return null;
    }

    /**
     * 下载移交包
     *
     * @param mailOBID
     * @param response
     * @throws Exception
     */
    @Override
    public void packageToDownload(String mailOBID, HttpServletResponse response) throws Exception {

    }
}
