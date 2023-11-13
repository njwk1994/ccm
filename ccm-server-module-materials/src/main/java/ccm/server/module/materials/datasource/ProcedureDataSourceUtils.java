package ccm.server.module.materials.datasource;

import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.materials.entity.DataBaseInfo;
import ccm.server.module.materials.enums.DataBaseType;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.ICIMProjectConfig;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.ICIMProjectConfigUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/8/1 10:41
 */
@Slf4j
@Component
public class ProcedureDataSourceUtils {

    /**
     * 线程安全的数据库连接池缓存
     */
    private static final Map<String, DruidDataSource> druidDataSourceMap = new ConcurrentHashMap<>();

    @PreDestroy
    public void closeSPMDefaultDatabase() {
        log.info("============ 开始关闭所有存储过程构建的连接池! =============");
        AtomicInteger i = new AtomicInteger(1);
        druidDataSourceMap.keySet().forEach(k -> {
            log.info("开始关闭第[{}]个连接池,共[{}]个.", i, druidDataSourceMap.size());
            DruidDataSource druidDataSource = druidDataSourceMap.get(k);
            druidDataSource.close();
            i.getAndIncrement();
        });
        log.info("============ 关闭所有存储过程构建的连接池结束! =============");
    }

    /**
     * 获取该项目配置的数据库连接池,如果不存在则新建
     *
     * @return
     */
    public static DruidDataSource getDataSource() throws Exception {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());

        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
        IObject iObject = queryEngine.queryOne(queryRequest);
        if (null == iObject) {
            throw new RuntimeException("获取项目配置失败!");
        }
        ICIMProjectConfig icimProjectConfig = iObject.toInterface(ICIMProjectConfig.class);
        String key = ProcedureDataSourceUtils.genKey(configurationItem.UID(),
                icimProjectConfig.getSPMDBHost(), icimProjectConfig.getSPMDBPort(), icimProjectConfig.getSPMDatabaseName(),
                icimProjectConfig.getSPMDBUsername(), icimProjectConfig.getSPMDBPassword());
        if (!hasDataSource(key)) {
            putDataSource(key,
                    new DataBaseInfo(key, icimProjectConfig.getSPMDBHost(), icimProjectConfig.getSPMDBPort(), icimProjectConfig.getSPMDatabaseName(),
                            icimProjectConfig.getSPMDBUsername(), icimProjectConfig.getSPMDBPassword(),
                            DataBaseType.oracle));
        }
        return druidDataSourceMap.get(getKey());
    }

    /**
     * 根据key获取连接池
     *
     * @param key
     * @return
     */
    public static DruidDataSource getDataSource(String key) {
        return druidDataSourceMap.get(key);
    }

    public static DruidPooledConnection getConnection() throws Exception {
        DruidDataSource dataSource = getDataSource();
        if (dataSource == null) {
            throw new Exception("获取项目配置的数据库连接池!");
        }
        return dataSource.getConnection();
    }

    /**
     * 添加/更新连接池
     *
     * @param key
     * @param druidDataSource
     */
    public static void putDataSource(String key, DruidDataSource druidDataSource) {
        druidDataSourceMap.put(key, druidDataSource);
    }

    /**
     * 新建并添加/更新连接池
     *
     * @param key
     * @param dataBaseInfo
     * @return
     */
    public static DruidDataSource putDataSource(String key, @NotNull DataBaseInfo dataBaseInfo) {
        DruidDataSource dataSource = new DruidDataSource();
        // 数据库连接基本信息
        dataSource.setName(dataBaseInfo.getDataSourceName());
        dataSource.setDriverClassName(dataBaseInfo.getDataBaseDriver());
        dataSource.setUrl(dataBaseInfo.getUrl());
        dataSource.setUsername(dataBaseInfo.getUsername());
        dataSource.setPassword(dataBaseInfo.getPassword());
        if (DataBaseType.oracle.equals(dataBaseInfo.getDataBaseType())) {
            dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        } else {
            dataSource.setValidationQuery("SELECT 1");
        }
        // 申请连接时执行validationQuery检测连接是否有效,做了这个配置会降低性能
        dataSource.setTestOnBorrow(false);
        // 归还连接时执行validationQuery检测连接是否有效,做了这个配置会降低性能
        dataSource.setTestOnReturn(false);
        // 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis,执行validationQuery检测连接是否有效
        // 如果检测失败，则连接将被从池中去除
        dataSource.setTestWhileIdle(true);
        dataSource.setMaxWait(40000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        // 最大连接数
        dataSource.setMaxActive(100);
        // 初始连接数
        dataSource.setInitialSize(3);
        dataSource.setMinIdle(0);
        // open PSPSCache
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(200);

        // 强制关闭连接(生产环境中不建议开启)
        //dataSource.setRemoveAbandoned(true);
        //dataSource.setRemoveAbandonedTimeout(180);
        //dataSource.setLogAbandoned(true);

        // close reconnect after failed
        dataSource.setBreakAfterAcquireFailure(true);
        dataSource.setConnectionErrorRetryAttempts(0);
        try {
            dataSource.init();
            putDataSource(key, dataSource);
        } catch (SQLException e) {
            dataSource.close();
            throw new RuntimeException("存储过程连接池创建失败!");
        }
        return dataSource;
    }

    /**
     * 删除连接池
     *
     * @param key
     */
    public static void removeDataSource(String key) {
        DruidDataSource removedDruidDataSource = druidDataSourceMap.remove(key);
        removedDruidDataSource.close();
    }

    /**
     * 检查项目连接池是否存在
     *
     * @return
     */
    public static boolean hasDataSource() {
        return druidDataSourceMap.containsKey(getKey());
    }

    /**
     * 检查指定key的连接池是否存在
     *
     * @param key
     * @return
     */
    public static boolean hasDataSource(String key) {
        return druidDataSourceMap.containsKey(key);
    }

    /**
     * 获取项目对应的key
     *
     * @return
     */
    public static String getKey() {
        String key = "";
        try {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());

            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
            IObject iObject = queryEngine.queryOne(queryRequest);
            if (null == iObject) {
                throw new RuntimeException("获取项目配置失败!");
            }
            ICIMProjectConfig icimProjectConfig = iObject.toInterface(ICIMProjectConfig.class);
            key = configurationItem.UID() + "-" +
                    icimProjectConfig.getSPMDBHost() + ":" + icimProjectConfig.getSPMDBPort() + ":" + icimProjectConfig.getSPMDatabaseName() + "@" +
                    icimProjectConfig.getSPMDBUsername() + "/" + icimProjectConfig.getSPMDBPassword();
        } catch (Exception e) {
            log.error("获取连接池Key失败!", ExceptionUtil.getRootCause(e));
            throw new RuntimeException("获取连接池Key失败!");
        }
        return key;
    }

    /**
     * 生成key
     *
     * @param projectUID
     * @param dbHost
     * @param dbPort
     * @param dbName
     * @param dbUsername
     * @param dbPassword
     * @return
     */
    public static String genKey(String projectUID, String dbHost, String dbPort, String dbName, String dbUsername, String dbPassword) {
        String key = "";
        try {
            if (StringUtils.isBlank(projectUID)) {
                LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());
                projectUID = configurationItem.UID();
            }
            key = projectUID + "-" +
                    dbHost + ":" + dbPort + ":" + dbName + "@" +
                    dbUsername + "/" + dbPassword;
        } catch (Exception e) {
            log.error("获取连接池Key失败!", ExceptionUtil.getRootCause(e));
            throw new RuntimeException("获取连接池Key失败!");
        }
        return key;
    }
}
