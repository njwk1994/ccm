package ccm.server.module.materials.aspect;

import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.materials.datasource.ProcedureDataSourceUtils;
import ccm.server.module.materials.entity.DataBaseInfo;
import ccm.server.module.materials.enums.DataBaseType;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.ICIMProjectConfig;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.ICIMProjectConfigUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Component;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/6 14:47
 */
@Slf4j
@Aspect
@Component
public class ProcedureCheckAspect {

    @Pointcut("@annotation(ccm.server.module.materials.aspect.ProcedureCheck)")
    public void check() {
    }

    /**
     * 在方法执行前验证连接池是否存在,如果不存在则构建连接池对象到缓存
     *
     * @param joinPoint
     * @throws Exception
     */
    @Before("check()")
    public void beforeStart(JoinPoint joinPoint) throws Exception {
        String name = joinPoint.getSignature().getName();
        log.info("开始执行方法{}!", name);
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ICIMConfigurationItem configurationItem = null;
        try {
            configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());
        } catch (Exception e) {
            log.error("存储过程连接池检查获取项目信息失败!", ExceptionUtil.getRootCause(e));
            throw new RuntimeException("存储过程连接池检查获取项目信息失败!");
        }

        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
        IObject iObject = queryEngine.queryOne(queryRequest);
        if (null == iObject) {
            log.error("存储过程连接池检查获取项目配置失败!");
            throw new RuntimeException("获取项目配置失败!");
        }
        ICIMProjectConfig icimProjectConfig = iObject.toInterface(ICIMProjectConfig.class);

        String key = ProcedureDataSourceUtils.genKey(configurationItem.UID(),
                icimProjectConfig.getSPMDBHost(), icimProjectConfig.getSPMDBPort(), icimProjectConfig.getSPMDatabaseName(),
                icimProjectConfig.getSPMDBUsername(), icimProjectConfig.getSPMDBPassword());
        // 默认需要创建连接池
        boolean needCreate = true;
        if (ProcedureDataSourceUtils.hasDataSource(key)) {// 存在连接池时,检测连接池状态
            DruidDataSource dataSource = ProcedureDataSourceUtils.getDataSource(key);
            needCreate = dataSource.isClosed();
        }
        if (needCreate) {
            log.warn("存储过程连接池已被关闭或未创建,开始创建新的连接池!");
            DataBaseInfo dataBaseInfo;
            try {
                dataBaseInfo = new DataBaseInfo(key, icimProjectConfig.getSPMDBHost(), icimProjectConfig.getSPMDBPort(), icimProjectConfig.getSPMDatabaseName(),
                        icimProjectConfig.getSPMDBUsername(), icimProjectConfig.getSPMDBPassword(), DataBaseType.oracle);
            } catch (Exception e) {
                throw new RuntimeException("构建数据库URL信息失败!" + ExceptionUtil.getMessage(e));
            }
            ProcedureDataSourceUtils.putDataSource(key, dataBaseInfo);
        }
    }

    @After("check()")
    public void afterFinished(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        log.info("方法{}执行结束!", name);
    }
}
