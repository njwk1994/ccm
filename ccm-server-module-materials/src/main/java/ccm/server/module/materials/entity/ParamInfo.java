package ccm.server.module.materials.entity;

import ccm.server.module.materials.enums.ProcedureParamType;

import java.io.Serializable;
import java.sql.JDBCType;

/**
 * 存储过程参数类
 *
 * @author HuangTao
 * @version 1.0
 * @since 2021/10/14 16:25
 */
public class ParamInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数值
     */
    private Object param;
    /**
     * 参数位置 doProcedure(?,?,?) 对应参数属于第几个问号 例如 第一个问号 paramNo=1
     */
    private int paramNo;
    /**
     * 参数类型
     * 输入 or 输出 or 输入输出
     */
    private ProcedureParamType paramType;
    /**
     * 数据类型 java.sql.Types 例 Types.VARCHAR
     */
    private JDBCType jdbcType;
    /**
     * 数据类型 OracleTypes 例 oracle.jdbc.OracleTypes.CURSOR
     */
    private int oracleType;

    public ParamInfo() {
    }

    /**
     * 创建 普通参数条件
     *
     * @param paramNo   参数位置
     * @param param     参数值
     * @param paramType 参数类型
     * @param jdbcType  JDBC类型
     */
    public ParamInfo(Object param, int paramNo, ProcedureParamType paramType, JDBCType jdbcType) {
        this.param = param;
        this.paramNo = paramNo;
        this.paramType = paramType;
        this.jdbcType = jdbcType;
    }

    /**
     * 创建 Oracle参数条件
     *
     * @param paramNo    参数位置
     * @param param      参数值
     * @param paramType  参数类型
     * @param oracleType Oracle类型
     */
    public ParamInfo(Object param, int paramNo, ProcedureParamType paramType, int oracleType) {
        this.param = param;
        this.paramNo = paramNo;
        this.paramType = paramType;
        this.oracleType = oracleType;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public int getParamNo() {
        return paramNo;
    }

    public void setParamNo(int paramNo) {
        this.paramNo = paramNo;
    }

    public ProcedureParamType getParamType() {
        return paramType;
    }

    public void setProcedureParamType(ProcedureParamType paramType) {
        this.paramType = paramType;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public int getOracleType() {
        return oracleType;
    }

    public void setOracleType(int oracleType) {
        this.oracleType = oracleType;
    }
}
