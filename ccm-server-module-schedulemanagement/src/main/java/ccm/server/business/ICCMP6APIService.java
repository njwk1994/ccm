package ccm.server.business;

import ccm.server.entity.ThreadResult;

/**
 * P6API调用服务
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/9 17:26
 */
public interface ICCMP6APIService {

    /**
     * 检查P6的webservice服务是否可以访问
     *
     * @return
     */
    ThreadResult<Boolean> isServiceAvailable();

    /**
     * 检查P6的webservice服务是否可以访问
     *
     * @param p6WSUrl
     * @param p6ProjectName
     * @param p6ProjectLoginName
     * @param p6ProjectPassword
     * @return
     */
    ThreadResult<Boolean> isServiceAvailable(String p6WSUrl, String p6ProjectName, String p6ProjectLoginName, String p6ProjectPassword);

    /**
     * 获取项目
     *
     * @return
     * @throws Exception
     */
    String readProjects() throws Exception;

    /**
     * 获取项目
     *
     * @param p6WebservicePath
     * @param p6ProjectName
     * @param p6ProjectLoginName
     * @param p6ProjectPassword
     * @return
     * @throws Exception
     */
    String readProjects(String p6WebservicePath, String p6ProjectName, String p6ProjectLoginName, String p6ProjectPassword) throws Exception;

    /**
     * 导出项目
     *
     * @param objectId
     * @return
     * @throws Exception
     */
    String exportProject(String objectId) throws Exception;

    /**
     * 导出项目
     *
     * @param objectId
     * @param p6WebservicePath
     * @param p6ProjectName
     * @param p6ProjectLoginName
     * @param p6ProjectPassword
     * @return XML 字符串
     * @throws Exception
     */
    String exportProject(String objectId, String p6WebservicePath, String p6ProjectName, String p6ProjectLoginName, String p6ProjectPassword) throws Exception;
}
