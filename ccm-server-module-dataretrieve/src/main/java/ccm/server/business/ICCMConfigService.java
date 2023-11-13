package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.schema.interfaces.IObject;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/6 16:11
 */
public interface ICCMConfigService {

    /**
     * 获取系统级配置表单
     *
     * @return
     * @throws Exception
     */
    ObjectDTO getSystemConfigForm(String formPurpose) throws Exception;

    /**
     * 获取系统级配置
     *
     * @return
     * @throws Exception
     */
    IObject getSystemConfig() throws Exception;

    /**
     * 新增/更新系统级配置
     *
     * @return
     * @throws Exception
     */
    IObject createOrUpdateSystemConfig() throws Exception;

    /**
     * 获取项目级配置表单
     *
     * @return
     * @throws Exception
     */
    ObjectDTO getProjectConfigForm(String formPurpose) throws Exception;

    /**
     * 获取项目级配置
     *
     * @return
     * @throws Exception
     */
    IObject getProjectConfig() throws Exception;

    /**
     * 新增/更新系统级配置
     *
     * @return
     * @throws Exception
     */
    IObject createOrUpdateProjectConfig(ObjectDTO objectDTO) throws Exception;

    /**
     * 新增/更新系统级配置
     *
     * @return
     * @throws Exception
     */
    IObject createOrUpdateProjectConfigTest(String spmdbHost, String spmdbPort, String spmDatabaseName, String spmdbUsername, String spmdbPassword, String spmProject, String procedureType) throws Exception;
}
