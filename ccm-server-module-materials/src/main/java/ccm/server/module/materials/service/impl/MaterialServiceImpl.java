package ccm.server.module.materials.service.impl;

import ccm.server.module.materials.aspect.ProcedureCheck;
import ccm.server.module.materials.datasource.ProcedureDataSourceUtils;
import ccm.server.module.materials.entity.ParamInfo;
import ccm.server.module.materials.entity.ProcedureInfo;
import ccm.server.module.materials.entity.ProcedureResult;
import ccm.server.module.materials.enums.ProcedureParamType;
import ccm.server.module.materials.service.IMaterialService;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.OracleUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MaterialServiceImpl implements IMaterialService {

    /**
     * 调用 存储过程
     *
     * @param procedure 存储过程 例 doJob() doJob(?,?,?)
     * @return 执行结果
     */
    @ProcedureCheck
    @Override
    public Result<List<Object>> callProcedure(ProcedureInfo procedure) throws Exception {

        Result<List<Object>> result = new Result<>();
        result.setSuccess(false);
        if (null == procedure || StringUtils.isBlank(procedure.getProcedure())) {
            return result;
        }

        String call = "{ call " + procedure.getProcedure() + "}";

        boolean execute = false;
        List<Object> objects = new ArrayList<Object>();

        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement callableStatement = connection.prepareCall(call);) {

            // 输入参数
            List<ParamInfo> params = new ArrayList<>();
            // 输出参数
            List<ParamInfo> out = new ArrayList<>();
            if (procedure.withParam()) {
                params = procedure.getParams().stream()
                        .filter(p -> (ProcedureParamType.param == p.getParamType() || ProcedureParamType.all == p.getParamType()))
                        .collect(Collectors.toList());
                out = procedure.getParams().stream().filter(p -> (ProcedureParamType.out == p.getParamType() || ProcedureParamType.all == p.getParamType()))
                        .collect(Collectors.toList());
                // 设置输入参数
                paramInit(callableStatement, params);
                // 设置输出参数
                if (out.size() > 0) {
                    out.forEach(o -> {
                        try {
                            callableStatement.registerOutParameter(o.getParamNo(), o.getJdbcType());
                        } catch (SQLException ignored) {
                        }
                    });
                }

            }

            // 直接执行语句
            execute = callableStatement.execute();
            if (execute) {
                if (out.size() > 0) {
                    out.forEach(o -> {
                        try {
                            switchGetResult(callableStatement, o, objects);
                        } catch (SQLException ignored) {
                        }
                    });
                }
            }
            result.setResult(objects);
            result.setSuccess(execute);
        } catch (SQLException e) {
            throw new Exception("call procedure error,please check connection or procedure");
        }
        return result;
    }

    /**
     * 调用 存储过程
     *
     * @param procedure 存储过程(存储过程名+参数) 例 doJob() doJob(?,?,?)
     * @return 返回结果集
     */
    @ProcedureCheck
    @Override
    public ResultSet callProcedureForQuery(ProcedureInfo procedure) throws Exception {
        String call = "{ call " + procedure.getProcedure() + "}";
        ResultSet resultSet;
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement callableStatement = connection.prepareCall(call)) {

            // 输入参数
            List<ParamInfo> params = new ArrayList<>();
            if (procedure.withParam()) {
                params = procedure.getParams().stream()
                        .filter(p -> (ProcedureParamType.param == p.getParamType() || ProcedureParamType.all == p.getParamType()))
                        .collect(Collectors.toList());
                // 设置输入参数
                paramInit(callableStatement, params);
            }

            // 执行获取结果集
            resultSet = callableStatement.executeQuery();
        } catch (SQLException e) {
            throw new Exception("call procedure error,please check connection or procedure");
        }
        return resultSet;
    }

    /**
     * 参数填充
     *
     * @param cs
     * @param params
     */
    private void paramInit(CallableStatement cs, List<ParamInfo> params) throws SQLException {
        for (ParamInfo param : params) {
            switchInitParam(cs, param);
        }
    }

    private void switchInitParam(CallableStatement cs, ParamInfo param) throws SQLException {
        switch (param.getJdbcType()) {
            case CHAR:
            case NCHAR:
            case VARCHAR:
            case NVARCHAR:
            case LONGVARCHAR:
            case LONGNVARCHAR:
                cs.setString(param.getParamNo(), (String) param.getParam());
                break;
            case NUMERIC:
            case DECIMAL:
                cs.setBigDecimal(param.getParamNo(), (BigDecimal) param.getParam());
                break;
            case BIT:
                cs.setBoolean(param.getParamNo(), (boolean) param.getParam());
                break;
            case TINYINT:
                cs.setByte(param.getParamNo(), (byte) param.getParam());
                break;
            case SMALLINT:
                cs.setShort(param.getParamNo(), (short) param.getParam());
                break;
            case INTEGER:
                cs.setInt(param.getParamNo(), (int) param.getParam());
                break;
            case BIGINT:
                cs.setLong(param.getParamNo(), (long) param.getParam());
                break;
            case REAL:
                cs.setFloat(param.getParamNo(), (float) param.getParam());
                break;
            case FLOAT:
            case DOUBLE:
                cs.setDouble(param.getParamNo(), (double) param.getParam());
                break;
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
                cs.setBytes(param.getParamNo(), (byte[]) param.getParam());
                break;
            case DATE:
                cs.setDate(param.getParamNo(), new Date(((java.util.Date) param.getParam()).getTime()));
                break;
            case TIME:
                cs.setTime(param.getParamNo(), new Time(((java.util.Date) param.getParam()).getTime()));
                break;
            case TIMESTAMP:
                cs.setTimestamp(param.getParamNo(), new Timestamp(((java.util.Date) param.getParam()).getTime()));
                break;
            default:
                cs.setString(param.getParamNo(), (String) param.getParam());
                break;
        }
    }

    private void switchGetResult(CallableStatement cs, ParamInfo param, List<Object> results) throws SQLException {
        Object o = new Object();
        switch (param.getJdbcType()) {
            case CHAR:
            case NCHAR:
            case VARCHAR:
            case NVARCHAR:
            case LONGVARCHAR:
            case LONGNVARCHAR:
                o = cs.getString(param.getParamNo());
                break;
            case NUMERIC:
            case DECIMAL:
                o = cs.getBigDecimal(param.getParamNo());
                break;
            case BIT:
                o = cs.getBoolean(param.getParamNo());
                break;
            case TINYINT:
                o = cs.getByte(param.getParamNo());
                break;
            case SMALLINT:
                o = cs.getShort(param.getParamNo());
                break;
            case INTEGER:
                o = cs.getInt(param.getParamNo());
                break;
            case BIGINT:
                o = cs.getLong(param.getParamNo());
                break;
            case REAL:
                o = cs.getFloat(param.getParamNo());
                break;
            case FLOAT:
            case DOUBLE:
                o = cs.getDouble(param.getParamNo());
                break;
            case BINARY:
            case VARBINARY:
            case LONGVARBINARY:
                o = cs.getBytes(param.getParamNo());
                break;
            case DATE:
                o = cs.getDate(param.getParamNo());
                break;
            case TIME:
                o = cs.getTime(param.getParamNo());
                break;
            case TIMESTAMP:
                o = cs.getTimestamp(param.getParamNo());
                break;
            default:
                o = cs.getString(param.getParamNo());
                break;
        }
        results.add(o);
    }

    /**
     * 图纸是否存在
     *
     * @param projectId     项目ID
     * @param drawingNumber 图纸号
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public int doesDrawingExist(String projectId, String drawingNumber) throws Exception {
        int result;
        String call = "{ call M_API_SITE_SPC2.DoesDrawingExist(?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString(1, projectId);
            cs.setString(2, drawingNumber);
            cs.registerOutParameter(3, OracleTypes.NUMBER);

            cs.execute();

            result = cs.getInt(3);

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call DoesDrawingExist procedure error,please check connection or procedure");
        }
        return result;
    }

    /**
     * 创建预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合数组
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult createNewStatusRequest(String projectId,
                                                  String requestName,
                                                  String requestType,
                                                  List<String> drawingNumbers) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.CreateNewStatusRequest(?,?,?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection()) {
            // 生成 数组 参数
            OracleConnection unwrap = OracleUtils.unwrap(connection);
            String[] strings = drawingNumbers.toArray(new String[0]);
            Array oracleArray = unwrap.createOracleArray("M_API_SITE.LN_TAB_TYPE", strings);

            try (OracleCallableStatement cs = (OracleCallableStatement) unwrap.prepareCall(call);) {
                // 参数填充 并 执行
                cs.setString(1, projectId);
                cs.setString(2, requestName);
                cs.setString(3, requestType);
//                cs.setArray(4, oracleArray);
                cs.setObject(4, oracleArray, OracleTypes.ARRAY);
                cs.registerOutParameter(5, OracleTypes.NUMBER);
                cs.registerOutParameter(6, OracleTypes.VARCHAR);
                cs.registerOutParameter(7, OracleTypes.VARCHAR);

                cs.execute();

                int requestId = cs.getInt(5);
                String requestDate = cs.getString(6);
                String message = cs.getString(7);

                result.setRequestId(requestId);
                result.setRequestDate(requestDate);
                result.setMessage(message);
            } catch (SQLException e) {
                log.error(ExceptionUtil.getMessage(e), e);
                throw new Exception("execute procedure error! " +
                        "exception info:" + ExceptionUtil.getMessage(e));
            }
        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
        return result;
    }

    /**
     * 创建预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合数组
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult createNewStatusRequestDnStr(String projectId,
                                                       String requestName,
                                                       String requestType,
                                                       String drawingNumbers
    ) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.CreateNewStatusRequestDnStr(?,?,?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {

            // 参数填充 并 执行
            cs.setString(1, projectId);
            cs.setString(2, requestName);
            cs.setString(3, requestType);
            cs.setString(4, drawingNumbers);
            cs.registerOutParameter(5, OracleTypes.NUMBER);
            cs.registerOutParameter(6, OracleTypes.VARCHAR);
            cs.registerOutParameter(7, OracleTypes.VARCHAR);

            cs.execute();

            int requestId = cs.getInt(5);
            String requestDate = cs.getString(6);
            String message = cs.getString(7);

            result.setRequestId(requestId);
            result.setRequestDate(requestDate);
            result.setMessage(message);

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
        return result;
    }

    /**
     * 创建部分预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合数组
     * @param commodityCodes
     * @param size1s
     * @param size2s
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult createPartialStatusRequest(String projectId, String requestName, String requestType,
                                                      List<String> drawingNumbers, List<String> commodityCodes,
                                                      List<String> size1s, List<String> size2s) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.CreatePartialStatusRequest(?,?,?,?,?,?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString(1, projectId);
            cs.setString(2, requestName);
            cs.setString(3, requestType);

            // 生成 数组 参数
            OracleConnection drawingNumbersUnwrap = OracleUtils.unwrap(ProcedureDataSourceUtils.getConnection());
            Array drawing_numbers = drawingNumbersUnwrap.createOracleArray("M_API_SITE.LN_TAB_TYPE", drawingNumbers.toArray());
            cs.setArray(4, drawing_numbers);

            OracleConnection commodityCodesUnwrap = OracleUtils.unwrap(ProcedureDataSourceUtils.getConnection());
            Array commodity_codes = commodityCodesUnwrap.createOracleArray("M_API_SITE.LN_TAB_TYPE", commodityCodes.toArray());
            cs.setArray(5, commodity_codes);

            OracleConnection size1sUnwrap = OracleUtils.unwrap(ProcedureDataSourceUtils.getConnection());
            Array sizes_1 = size1sUnwrap.createOracleArray("M_API_SITE.LN_TAB_TYPE", size1s.toArray());
            cs.setArray(6, sizes_1);

            OracleConnection size2sUnwrap = OracleUtils.unwrap(ProcedureDataSourceUtils.getConnection());
            Array sizes_2 = size2sUnwrap.createOracleArray("M_API_SITE.LN_TAB_TYPE", size2s.toArray());
            cs.setArray(7, sizes_2);

            closeUnwraps(drawingNumbersUnwrap, commodityCodesUnwrap, size1sUnwrap, size2sUnwrap);

            cs.registerOutParameter(8, OracleTypes.NUMBER);
            cs.registerOutParameter(9, OracleTypes.VARCHAR);
            cs.registerOutParameter(10, OracleTypes.VARCHAR);
            cs.execute();

            int requestId = cs.getInt(8);
            String requestDate = cs.getString(9);
            String message = cs.getString(10);

            result.setRequestId(requestId);
            result.setRequestDate(requestDate);
            result.setMessage(message);

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
        return result;
    }

    /**
     * 创建部分预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合数组
     * @param commodityCodes
     * @param size1s
     * @param size2s
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult createPartialStatusRequestStr(String projectId, String requestName, String requestType,
                                                         String drawingNumbers, String commodityCodes,
                                                         String size1s, String size2s) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.CreatePartialStatusRequestStr(?,?,?,?,?,?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString(1, projectId);
            cs.setString(2, requestName);
            cs.setString(3, requestType);
            cs.setString(4, drawingNumbers);
            cs.setString(5, commodityCodes);
            cs.setString(6, size1s);
            cs.setString(7, size2s);

            cs.registerOutParameter(8, OracleTypes.NUMBER);
            cs.registerOutParameter(9, OracleTypes.VARCHAR);
            cs.registerOutParameter(10, OracleTypes.VARCHAR);
            cs.execute();

            int requestId = cs.getInt(8);
            String requestDate = cs.getString(9);
            String message = cs.getString(10);

            result.setRequestId(requestId);
            result.setRequestDate(requestDate);
            result.setMessage(message);

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
        return result;
    }

    /**
     * 创建部分预测预留33
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param warehouses     仓库
     * @param drawingNumbers 图纸号集合数组
     * @param identCode
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult CreatePartialStatusRequest33Str(String projectId, String requestName, String requestType,
                                                           String warehouses, String drawingNumbers, String identCode) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.CreatePartialStatusRequest33Str(?,?,?,?,?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString(1, projectId);
            cs.setString(2, requestName);
            cs.setString(3, requestType);
            cs.setString(4, warehouses);
            cs.setString(5, drawingNumbers);
            cs.setString(6, identCode);

            cs.registerOutParameter(7, OracleTypes.NUMBER);
            cs.registerOutParameter(8, OracleTypes.VARCHAR);
            cs.registerOutParameter(9, OracleTypes.VARCHAR);
            cs.execute();

            int requestId = cs.getInt(7);
            String requestDate = cs.getString(8);
            String message = cs.getString(9);

            result.setRequestId(requestId);
            result.setRequestDate(requestDate);
            result.setMessage(message);

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
        return result;
    }

    /**
     * 获取预测结果
     *
     * @param requestId
     * @param searchColumn
     * @param searchValue
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public JSONArray getMaterialStatusResults(int requestId, String searchColumn, String searchValue) throws Exception {
        JSONArray results = new JSONArray();
        String call = "{ call M_API_SITE_SPC2.GetMaterialStatusResults(?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setInt(1, requestId);
            cs.setString(2, searchColumn);
            cs.setString(3, searchValue);
            cs.registerOutParameter(4, OracleTypes.CURSOR);
            cs.execute();

            try (ResultSet rs = (ResultSet) cs.getObject(4);) {
                results = extractJSONArray(rs);
            } catch (SQLException e) {
                throw new Exception("get request_data_cursor error");
            }

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
        return results;
    }

    /**
     * 转换 ResultSet 为 JSON 数组
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public JSONArray extractJSONArray(ResultSet rs) throws SQLException, ParseException {
        ResultSetMetaData md = rs.getMetaData();
        int num = md.getColumnCount();
        JSONArray array = new JSONArray();
        while (rs.next()) {
            JSONObject mapOfColValues = new JSONObject();
            Object o = new Object();
            for (int i = 1; i <= num; i++) {
                if (md.getColumnName(i).equals("ETA_DATE") || md.getColumnName(i).equals("REQ_SITE_DATE")) {
                    if (null != rs.getObject(i)) {
                        String s = rs.getObject(i).toString();
                        java.util.Date date = DateUtils.parseDate(s, "yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        o = simpleDateFormat.format(date);
                    } else {
                        o = "";
                    }
                } else {
                    o = rs.getObject(i);
                }
                mapOfColValues.put(md.getColumnName(i), o);
            }
            array.add(mapOfColValues);
        }
        return array;
    }

    /**
     * 查找对应的任务包或工作包在SPM中有没用进行过预测预留
     *
     * @param projectId   项目ID
     * @param requestName TWP编号
     * @param requestType FR是预测，RR是预留
     *                    数据源
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult getLatestRequestInfo(String projectId, String requestName, String requestType) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.GetLastestRequestInfo(?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString("project_id", projectId);
            cs.setString("request_name", requestName);
            cs.setString("request_type", requestType);
            cs.registerOutParameter("request_id", OracleTypes.VARCHAR);
            cs.registerOutParameter("request_date", OracleTypes.VARCHAR);
            cs.execute();

            result.setRequestIdStr(cs.getString("request_id"));
            result.setRequestDate(cs.getString("request_date"));

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call GetLastestRequestInfo procedure error,please check connection or procedure");
        }
        return result;
    }

    /**
     * 取消预测预留
     *
     * @param projectId   项目ID
     * @param requestName TWP编号
     * @param requestType FR是预测，RR是预留
     *                    数据源
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult undoMatStatusRequests(String projectId, String requestName, String requestType) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC2.UndoMatStatusRequests(?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString("project_id", projectId);
            cs.setString("request_name", requestName);
            cs.setString("request_type", requestType);
            cs.registerOutParameter("r_result", OracleTypes.NUMBER);
            cs.registerOutParameter("r_message", OracleTypes.VARCHAR);
            cs.execute();

            result.setRequestId(cs.getInt("r_result"));
            result.setMessage(cs.getString("r_message"));

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call UndoMatStatusRequests procedure error,please check connection or procedure");
        }
        return result;
    }

    /**
     * 按阶段进行材料预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param lpAttrCode     SPM中阶段对于的属性字段
     * @param lpAttrValue    阶段值，从SPC的工作包里取
     * @param drawingNumbers 图纸号集合数组
     * @param commodityCodes 材料编码
     * @param size1s
     * @param size2s
     * @return
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult createFAWithExtraFilterStr(String projectId, String requestName, String requestType,
                                                      String lpAttrCode, String lpAttrValue,
                                                      String drawingNumbers, String commodityCodes,
                                                      String size1s, String size2s) throws Exception {
        ProcedureResult result = new ProcedureResult();
        String call = "{ call M_API_SITE_SPC3.CreateFAWithExtraFilterStr(?,?,?,?,?,?,?,?,?,?,?,?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            // 参数填充 并 执行
            cs.setString(1, projectId);
            cs.setString(2, requestName);
            cs.setString(3, requestType);
            cs.setString(4, lpAttrCode);
            cs.setString(5, lpAttrValue);
            cs.setString(6, drawingNumbers);
            cs.setString(7, commodityCodes);
            cs.setString(8, size1s);
            cs.setString(9, size2s);

            cs.registerOutParameter(10, OracleTypes.NUMBER);
            cs.registerOutParameter(11, OracleTypes.VARCHAR);
            cs.registerOutParameter(12, OracleTypes.VARCHAR);
            cs.execute();

            int requestId = cs.getInt(10);
            String requestDate = cs.getString(11);
            String message = cs.getString(12);

            result.setRequestId(requestId);
            result.setRequestDate(requestDate);
            result.setMessage(message);

        } catch (SQLException e) {
            log.error("call procedure CreateFAWithExtraFilterStr error,please check connection or procedure! exception info:{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("call procedure CreateFAWithExtraFilterStr error,please check connection or procedure! exception info:" + ExceptionUtil.getMessage(e));
        }
        return result;
    }

    /**
     * SPM 登录
     *
     * @param uid
     * @throws Exception
     */
    public void selectLogin(int uid) throws Exception {
        String call = "{ call MPCK_LOGIN.SELECT_LOGIN(?) }";
        try (DruidPooledConnection connection = ProcedureDataSourceUtils.getConnection();
             CallableStatement cs = connection.prepareCall(call)) {
            cs.setInt(1, uid);
            cs.execute();

        } catch (SQLException e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("call procedure error,please check connection or procedure! " +
                    "exception info:" + ExceptionUtil.getMessage(e));
        }
    }

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public Map<String, Object> existAndCreateNewStatusRequest(String projectId,
                                                              String requestName,
                                                              String requestType, String drawingNumbers,
                                                              String searchColumn, String searchValue) throws Exception {
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(requestName) || StringUtils.isBlank(requestType) || StringUtils.isBlank(drawingNumbers)) {
            throw new Exception("存在为空参数,请检查参数!项目号:" + projectId + " 任务包名称:" + requestName + " 预测/预留:" + requestType + " 图纸集合:" + drawingNumbers);
        }
        Map<String, Object> resultMap = new HashMap<>();
        List<ProcedureResult> procedureResults = new ArrayList<>();
        String[] split = drawingNumbers.split(",");
        try {
            // 检测图纸是否存在
            for (String drawingNumber : split) {
                ProcedureResult result = new ProcedureResult();
                result.setObjectName(drawingNumber);
                int i = doesDrawingExist(projectId, drawingNumber);
                result.setExist(i);
                if (!result.isExist()) {
                    result.setMessage("SPM不存在此图纸");
                }
                procedureResults.add(result);
            }
            resultMap.put("drawingInfo", procedureResults);
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("检测图纸是否存在时异常! 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取存在的图纸集合
        List<String> existedDrawingNumbers = procedureResults.stream()
                .filter(ProcedureResult::isExist)
                .map(ProcedureResult::getObjectName)
                .collect(Collectors.toList());
        String existedDrawingNumbersStr = list2String(existedDrawingNumbers);
        ProcedureResult newStatusRequest = null;
        try {
            // 创建预测预留
            if (StringUtils.isNotBlank(existedDrawingNumbersStr)) {
                ProcedureResult result = createNewStatusRequestDnStr(projectId, requestName, requestType, existedDrawingNumbersStr);
                newStatusRequest = result;
                procedureResults.forEach(p -> {
                    if (p.isExist()) {
                        p.setRequestId(result.getRequestId());
                        p.setRequestDate(result.getRequestDate());
                        p.setMessage(result.getMessage());
                    }
                });
                if (null != newStatusRequest.getRequestId() && 0 < newStatusRequest.getRequestId()) {
                    resultMap.put("requestId", newStatusRequest.getRequestId());
                    resultMap.put("requestDate", newStatusRequest.getRequestDate());
                } else {
                    throw new Exception("创建预测预留异常! 异常信息: requestId:" + newStatusRequest.getRequestId() + " requestDate:" + newStatusRequest.getRequestDate());
                }
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("创建预测预留异常! 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取预测结果
        JSONArray materialStatusResults = null;
        try {
            if (newStatusRequest != null) {
                materialStatusResults = getMaterialStatusResults(newStatusRequest.getRequestId(), searchColumn, searchValue);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getMessage(e), e);
            throw new Exception("获取预测结果异常! 异常信息:" + ExceptionUtil.getMessage(e));
        }
        resultMap.put("data", materialStatusResults);
        return resultMap;
    }

    /**
     * 检测图纸 并部分预测/预留 获取预测数据
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public Map<String, Object> existAndCreatePartialStatusRequest(String projectId,
                                                                  String requestName, String requestType, String drawingNumbers,
                                                                  String commodityCodes, String size1s, String size2s,
                                                                  String searchColumn, String searchValue) throws Exception {
        String params = "项目号:[" + projectId
                + "], 预测单号:[" + requestName
                + "], 预测/预留:[" + requestType
                + "], 存在材料消耗图纸集合:[" + drawingNumbers
                + "], 材料编码集合:[" + commodityCodes
                + "], PSize1集合:[" + size1s
                + "], PSize2集合:[" + size2s
                + "]";
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(requestName) || StringUtils.isBlank(requestType) || StringUtils.isBlank(drawingNumbers)) {
            throw new Exception("存在为空参数,请检查参数!" + params);
        }

        Map<String, Object> resultMap = new HashMap<>();

        List<ProcedureResult> procedureResults = new ArrayList<>();
        String[] split = drawingNumbers.split(",");
        try {
            // 检测图纸是否存在
            for (String drawingNumber : split) {
                ProcedureResult result = new ProcedureResult();
                result.setObjectName(drawingNumber);
                int i = doesDrawingExist(projectId, drawingNumber);
                result.setExist(i);
                if (!result.isExist()) {
                    result.setMessage("SPM不存在此图纸");
                }
                procedureResults.add(result);
            }
            resultMap.put("drawingInfo", procedureResults);
        } catch (Exception e) {
            log.error("检测图纸是否存在时异常!" + params + " 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("检测图纸是否存在时异常!" + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取存在的图纸集合
        List<String> existedDrawingNumbers = procedureResults.stream()
                .filter(ProcedureResult::isExist)
                .map(ProcedureResult::getObjectName)
                .collect(Collectors.toList());
        String existedDrawingNumbersStr = list2String(existedDrawingNumbers);
        ProcedureResult newStatusRequest = null;
        try {
            // 创建预测预留
            if (StringUtils.isNotBlank(existedDrawingNumbersStr)) {
                ProcedureResult result = createPartialStatusRequestStr(projectId,
                        requestName, requestType,
                        existedDrawingNumbersStr, commodityCodes,
                        size1s, size2s);
                newStatusRequest = result;
                procedureResults.forEach(p -> {
                    if (p.isExist()) {
                        p.setRequestId(result.getRequestId());
                        p.setRequestDate(result.getRequestDate());
                        p.setMessage(result.getMessage());
                    }
                });
                if (null != newStatusRequest.getRequestId() && 0 < newStatusRequest.getRequestId()) {
                    resultMap.put("requestId", newStatusRequest.getRequestId());
                    resultMap.put("requestDate", newStatusRequest.getRequestDate());
                } else {
                    throw new Exception("存储过程信息: requestId:" + newStatusRequest.getRequestId()
                            + " requestDate:" + newStatusRequest.getRequestDate()
                            + " requestMessage:" + newStatusRequest.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("创建部分预测预留异常!" + params + " 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("创建部分预测预留异常! " + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取预测结果
        JSONArray materialStatusResults = null;
        try {
            if (newStatusRequest != null) {
                materialStatusResults = getMaterialStatusResults(newStatusRequest.getRequestId(), searchColumn, searchValue);
            }
        } catch (Exception e) {
            log.error("获取预测结果异常! 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("获取预测结果异常!" + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        resultMap.put("data", materialStatusResults);
        resultMap.put("param", params);
        return resultMap;
    }

    /**
     * 检测图纸 并按阶段进行材料预测预留 获取预测数据
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public Map<String, Object> existAndCreateFAWithExtraFilter(String projectId, String requestName, String requestType,
                                                               String lpAttrCode, String lpAttrValue,
                                                               String drawingNumbers, String commodityCodes,
                                                               String size1s, String size2s,
                                                               String searchColumn, String searchValue) throws Exception {
        String params = "项目号:[" + projectId
                + "], 预测单号:[" + requestName
                + "], 预测/预留:[" + requestType
                + "], 存在材料消耗图纸集合:[" + drawingNumbers
                + "], 材料编码集合:[" + commodityCodes
                + "], PSize1集合:[" + size1s
                + "], PSize2集合:[" + size2s
                + "]";
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(requestName) || StringUtils.isBlank(requestType) || StringUtils.isBlank(drawingNumbers)) {
            throw new Exception("存在为空参数,请检查参数!" + params);
        }

        Map<String, Object> resultMap = new HashMap<>();

        List<ProcedureResult> procedureResults = new ArrayList<>();
        String[] split = drawingNumbers.split(",");
        try {
            // 检测图纸是否存在
            for (String drawingNumber : split) {
                ProcedureResult result = new ProcedureResult();
                result.setObjectName(drawingNumber);
                int i = doesDrawingExist(projectId, drawingNumber);
                result.setExist(i);
                if (!result.isExist()) {
                    result.setMessage("SPM不存在此图纸");
                }
                procedureResults.add(result);
            }
            resultMap.put("drawingInfo", procedureResults);
        } catch (Exception e) {
            log.error("检测图纸是否存在时异常!" + params + " 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("检测图纸是否存在时异常!" + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取存在的图纸集合
        List<String> existedDrawingNumbers = procedureResults.stream()
                .filter(ProcedureResult::isExist)
                .map(ProcedureResult::getObjectName)
                .collect(Collectors.toList());
        String existedDrawingNumbersStr = list2String(existedDrawingNumbers);
        ProcedureResult newStatusRequest = null;
        try {
            // 创建预测预留
            if (StringUtils.isNotBlank(existedDrawingNumbersStr)) {
                ProcedureResult result = createFAWithExtraFilterStr(projectId,
                        requestName, requestType,
                        lpAttrCode, lpAttrValue,
                        existedDrawingNumbersStr, commodityCodes,
                        size1s, size2s);
                newStatusRequest = result;
                procedureResults.forEach(p -> {
                    if (p.isExist()) {
                        p.setRequestId(result.getRequestId());
                        p.setRequestDate(result.getRequestDate());
                        p.setMessage(result.getMessage());
                    }
                });
                if (null != newStatusRequest.getRequestId() && 0 < newStatusRequest.getRequestId()) {
                    resultMap.put("requestId", newStatusRequest.getRequestId());
                    resultMap.put("requestDate", newStatusRequest.getRequestDate());
                } else {
                    throw new Exception("存储过程信息: requestId:" + newStatusRequest.getRequestId()
                            + " requestDate:" + newStatusRequest.getRequestDate()
                            + " requestMessage:" + newStatusRequest.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("按阶段进行创建预测预留异常!" + params + " 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("按阶段进行创建预测预留异常! " + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取预测结果
        JSONArray materialStatusResults = null;
        try {
            if (newStatusRequest != null) {
                materialStatusResults = getMaterialStatusResults(newStatusRequest.getRequestId(), searchColumn, searchValue);
            }
        } catch (Exception e) {
            log.error("获取预测结果异常! 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("获取预测结果异常!" + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        resultMap.put("data", materialStatusResults);
        resultMap.put("param", params);
        return resultMap;
    }

    /**
     * 检测图纸 并部分预测/预留 获取预测数据 33
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public Map<String, Object> existAndCreatePartialStatusRequest33(String projectId,
                                                                    String requestName, String requestType,
                                                                    String warehouses, String drawingNumbers, String identCode,
                                                                    String searchColumn, String searchValue) throws Exception {
        String params = "项目号:[" + projectId
                + "], 预测单号:[" + requestName
                + "], 预测/预留:[" + requestType
                + "], 存在材料消耗图纸集合:[" + drawingNumbers
                + "], identCode集合:[" + identCode
                + "], 仓库集合:[" + warehouses
                + "]";
        if (StringUtils.isBlank(projectId) || StringUtils.isBlank(requestName) || StringUtils.isBlank(requestType) || StringUtils.isBlank(drawingNumbers)) {
            throw new Exception("存在为空参数,请检查参数!" + params);
        }

        Map<String, Object> resultMap = new HashMap<>();

        List<ProcedureResult> procedureResults = new ArrayList<>();
        String[] split = drawingNumbers.split(",");
        try {
            // 检测图纸是否存在
            for (String drawingNumber : split) {
                ProcedureResult result = new ProcedureResult();
                result.setObjectName(drawingNumber);
                int i = doesDrawingExist(projectId, drawingNumber);
                result.setExist(i);
                if (!result.isExist()) {
                    result.setMessage("SPM不存在此图纸");
                }
                procedureResults.add(result);
            }
            resultMap.put("drawingInfo", procedureResults);
        } catch (Exception e) {
            log.error("检测图纸是否存在时异常!" + params + " 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("检测图纸是否存在时异常!" + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取存在的图纸集合
        List<String> existedDrawingNumbers = procedureResults.stream()
                .filter(ProcedureResult::isExist)
                .map(ProcedureResult::getObjectName)
                .collect(Collectors.toList());
        String existedDrawingNumbersStr = list2String(existedDrawingNumbers);
        ProcedureResult newStatusRequest = null;
        try {
            // 创建预测预留
            if (StringUtils.isNotBlank(existedDrawingNumbersStr)) {
                ProcedureResult result = CreatePartialStatusRequest33Str(projectId,
                        requestName, requestType, warehouses,
                        existedDrawingNumbersStr, identCode);
                newStatusRequest = result;
                procedureResults.forEach(p -> {
                    if (p.isExist()) {
                        p.setRequestId(result.getRequestId());
                        p.setRequestDate(result.getRequestDate());
                        p.setMessage(result.getMessage());
                    }
                });
                if (null != newStatusRequest.getRequestId() && 0 < newStatusRequest.getRequestId()) {
                    resultMap.put("requestId", newStatusRequest.getRequestId());
                    resultMap.put("requestDate", newStatusRequest.getRequestDate());
                } else {
                    throw new Exception("存储过程信息: requestId:" + newStatusRequest.getRequestId()
                            + " requestDate:" + newStatusRequest.getRequestDate()
                            + " requestMessage:" + newStatusRequest.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("创建部分预测预留异常!" + params + " 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("创建部分预测预留异常! " + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        // 获取预测结果
        JSONArray materialStatusResults = new JSONArray();
        try {
            if (newStatusRequest != null) {
                materialStatusResults = getMaterialStatusResults(newStatusRequest.getRequestId(), searchColumn, searchValue);
            }
        } catch (Exception e) {
            log.error("获取预测结果异常! 异常信息:{}{}", ExceptionUtil.getMessage(e), ExceptionUtil.getRootCause(e));
            throw new Exception("获取预测结果异常!" + params + " 异常信息:" + ExceptionUtil.getMessage(e));
        }
        resultMap.put("data", materialStatusResults);
        resultMap.put("param", params);
        return resultMap;
    }

    /**
     * 关闭 createOracleArray
     *
     * @param unwrap
     * @throws Exception
     */
    private void closeUnwraps(OracleConnection... unwrap) throws Exception {
        for (OracleConnection oracleConnection : unwrap) {
            try {
                log.info("正在关闭createOracleArray连接....");
                oracleConnection.close();
                log.info("关闭createOracleArray连接成功");
            } catch (SQLException e) {
                log.error(ExceptionUtil.getMessage(e), e);
                throw new Exception("关闭createOracleArray连接异常!异常信息:" + ExceptionUtil.getMessage(e));
            }
        }
    }

    /**
     * 将 List<String> 转换为不带空格的用","分隔的字符串
     *
     * @param params
     * @return
     */
    private String list2String(List<String> params) {
        StringBuffer sb = new StringBuffer();
        for (String param : params) {
            sb.append(param).append(",");
        }
        String result = sb.toString();
        result = result.endsWith(",") ? result.substring(0, result.length() - 1) : result;
        return result;
    }

    /**
     * 如果想新建或从新预测预留用这个，这个是基于整张图纸的预留预留。
     * 还有一个是基于图纸和材料编码的预测预留。相比这个预测预留就是将材料编码也作为参数传递给SPM并获取该图纸下该材料的预测预留结果
     *
     * @param projectId
     * @param drawingNumbers
     * @throws Exception
     */
    @ProcedureCheck
    @Override
    public ProcedureResult performStatusRequest(String projectId,
                                                String requestName, String requestType,
                                                List<String> drawingNumbers) throws Exception {
        ProcedureResult result = new ProcedureResult();
        boolean flag = false;
        // 检测是否存在
        for (String drawingNumber : drawingNumbers) {
            int i = doesDrawingExist(projectId, drawingNumber);
            if (i > 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new Exception("MATERIALS_PERFORM_MAT_STATUS_ERROR_SPMAT_NODE_NOT_EXIST");
        }

        // 创建预测预留
        String drawingNumbersStr = list2String(drawingNumbers);
        result = createNewStatusRequestDnStr(projectId, requestName, requestType, drawingNumbersStr);
        if (null == result.getRequestId() || 0 >= result.getRequestId()) {
            throw new Exception("MATERIALS_PERFORM_MAT_STATUS_ERROR_START_REQUEST");
        }
        return result;
    }

    @ProcedureCheck
    @Override
    public boolean aopTest() throws Exception {

        return true;
    }


}
