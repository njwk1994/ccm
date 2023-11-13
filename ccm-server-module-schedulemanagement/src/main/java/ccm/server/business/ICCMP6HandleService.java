package ccm.server.business;

/**
 * P6业务处理服务
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/9 17:27
 */
public interface ICCMP6HandleService {

    /**
     * 同步P6计划
     *
     * @throws Exception
     */
    void syncSchedule() throws Exception;
}
