package ccm.server.module.materials.entity;

import ccm.server.module.materials.enums.DataBaseType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 数据源信息类
 *
 * @author HuangTao
 * @version 1.0
 * @since 2021/10/14 13:22
 */
public class DataBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dataSourceName;

    private String url;

    private String username;

    private String password;

    private DataBaseType dataBaseType;

    private String dataBaseDriver;

    public DataBaseInfo() {
    }

    /**
     * 构建数据库信息
     *
     * @param host         服务器host
     * @param port         数据库端口
     * @param databaseName 数据库名称
     * @param username     用户名
     * @param password     密码
     * @param dataBaseType 数据库类型
     * @throws Exception 异常
     */
    public DataBaseInfo(String host, String port, String databaseName, String username, String password, DataBaseType dataBaseType) throws Exception {
        this.username = username;
        this.password = password;
        this.dataBaseType = dataBaseType;
        setUrl(host, port, databaseName);
        switchDriver(dataBaseType);
    }

    /**
     * 构建数据库信息
     *
     * @param dataSourceName 数据源名称
     * @param host           服务器host
     * @param port           数据库端口
     * @param databaseName   数据库名称
     * @param username       用户名
     * @param password       密码
     * @param dataBaseType   数据库类型
     * @throws Exception 异常
     */
    public DataBaseInfo(String dataSourceName, String host, String port, String databaseName, String username, String password, DataBaseType dataBaseType) throws Exception {
        this.dataSourceName = dataSourceName;
        this.username = username;
        this.password = password;
        this.dataBaseType = dataBaseType;
        setUrl(host, port, databaseName);
        switchDriver(dataBaseType);
    }

    /**
     * 构建数据库信息
     *
     * @param dataSourceName 数据源名称
     * @param host           服务器host
     * @param port           数据库端口
     * @param databaseName   数据库名称
     * @param username       用户名
     * @param password       密码
     * @param dataBaseDriver 数据库驱动
     * @throws Exception 异常
     */
    public DataBaseInfo(String dataSourceName, String host, String port, String databaseName, String username, String password, String dataBaseDriver) throws Exception {
        this.dataSourceName = dataSourceName;
        this.username = username;
        this.password = password;
        this.dataBaseDriver = dataBaseDriver;
        switchDataBaseType(dataBaseDriver);
        setUrl(host, port, databaseName);
    }

    /**
     * 构建数据库信息
     *
     * @param host           服务器host
     * @param port           数据库端口
     * @param databaseName   数据库名称
     * @param username       用户名
     * @param password       密码
     * @param dataBaseDriver 数据库驱动
     * @throws Exception 异常
     */
    public DataBaseInfo(String host, String port, String databaseName, String username, String password, String dataBaseDriver) throws Exception {
        this.username = username;
        this.password = password;
        this.dataBaseDriver = dataBaseDriver;
        switchDataBaseType(dataBaseDriver);
        setUrl(host, port, databaseName);
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String host, String port, String databaseName) throws Exception {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(port) || StringUtils.isBlank(databaseName)) {
            throw new Exception("数据库配置信息不全,请检查配置!地址:" + host + ",端口:" + port + ",数据库名称:" + databaseName);
        }
        StringBuilder builder = new StringBuilder();
        if (dataBaseType == DataBaseType.sqlServer) {
            builder.append("jdbc:sqlserver://")
                    .append(host).append(":").append(port)
                    .append(";DatabaseName=").append(databaseName);
        }
        if (dataBaseType == DataBaseType.oracle) {
            builder.append("jdbc:oracle:thin:@")
                    .append(host).append(":").append(port)
                    .append(":").append(databaseName);
        }
        if (dataBaseType == DataBaseType.mySQL) {
            builder.append("jdbc:mysql://")
                    .append(host).append(":")
                    .append(port).append("/")
                    .append(databaseName)
                    .append("?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai");
        }
        this.url = builder.toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(DataBaseType dataBaseType) {
        this.dataBaseType = dataBaseType;
        switchDriver(dataBaseType);
    }

    private void switchDriver(DataBaseType dataBaseType) {
        switch (dataBaseType) {
            case mySQL:
                this.setDataBaseDriver("com.cj.mysql.jdbc.Driver");
                break;
            case oracle:
                this.setDataBaseDriver("oracle.jdbc.OracleDriver");
                break;
            case sqlServer:
                this.setDataBaseDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                break;
        }
    }

    private void switchDataBaseType(String dataBaseDriver) throws Exception {
        if (dataBaseDriver.contains("mysql")) {
            this.setDataBaseType(DataBaseType.mySQL);
        } else if (dataBaseDriver.contains("oracle")) {
            this.setDataBaseType(DataBaseType.oracle);
        } else if (dataBaseDriver.contains("sqlserver")) {
            this.setDataBaseType(DataBaseType.sqlServer);
        } else {
            throw new Exception("未知数据库类型,请检查配置!");
        }
    }

    public String getDataBaseDriver() {
        return dataBaseDriver;
    }

    private void setDataBaseDriver(String dataBaseDriver) {
        this.dataBaseDriver = dataBaseDriver;
    }
}
