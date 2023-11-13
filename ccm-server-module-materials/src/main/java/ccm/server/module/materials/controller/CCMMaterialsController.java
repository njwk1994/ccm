package ccm.server.module.materials.controller;

import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.models.query.QueryRequest;
import ccm.server.module.materials.entity.ProcedureResult;
import ccm.server.module.materials.service.IMaterialService;
import ccm.server.module.vo.ResultVo;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.ICIMProjectConfig;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.ICIMProjectConfigUtils;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储过程调用
 *
 * @author HuangTao
 * @version 1.0
 * @since 2021/10/18 16:00
 */
@RestController
@RequestMapping("/procedure")
@Api(tags = "预测预留接口")
@Slf4j
public class CCMMaterialsController {

    @Autowired
    private IMaterialService materialService;

    /*@ApiOperation(value = "构建数据源连接池", notes = "构建数据源连接池(频繁调用会导致性能问题)")
    @PostMapping("initDatabase")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "host", value = "数据库主机地址", example = "e-ws2016-db01.smart.ext", dataTypeClass = String.class),
            @DynamicParameter(name = "port", value = "数据库连接端口", example = "1522", dataTypeClass = String.class),
            @DynamicParameter(name = "databaseName", value = "数据库实例名称", example = "SMAT01", dataTypeClass = String.class),
            @DynamicParameter(name = "username", value = "用户名", example = "m_sys", dataTypeClass = String.class),
            @DynamicParameter(name = "password", value = "密码", example = "Oracle123", dataTypeClass = String.class),
            @DynamicParameter(name = "dataBaseType", value = "数据库类型(oracle mysql sqlserver)", example = "oracle", dataTypeClass = String.class)
    }))
    public String initDataBaseCon(@RequestBody JSONObject jsonObject) {

        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            String host = jsonObject.getString("host");
            String port = jsonObject.getString("port");
            String databaseName = jsonObject.getString("databaseName");
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            String dataBaseType = jsonObject.getString("dataBaseType");
            DataBaseInfo dataBaseInfo = new DataBaseInfo(host, port, databaseName, username, password, DataBaseType.getType(dataBaseType));
            materialService.initDataSource(dataBaseInfo);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }*/

    /*@ApiOperation(value = "获取数据源URL", notes = "数据源连接池是否关闭")
    @PostMapping("getDataSourceUrl")
    public String getDataSourceUrl() {

        Result<String> result = new Result<String>();
        boolean flag = false;
        String message = "";
        try {
            String url = materialService.getDataSource().getUrl();
            result.setResult(url);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }*/

    /*@ApiOperation(value = "数据源连接池是否关闭", notes = "数据源连接池是否关闭")
    @PostMapping("dataSourceCloseStatus")
    public String dataSourceCloseStatus() {

        Result<Boolean> result = new Result<Boolean>();
        boolean flag = false;
        String message = "";
        try {
            boolean closed = materialService.getDataSource().isClosed();
            result.setResult(closed);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "数据源连接池是否启用", notes = "数据源连接池是否启用")
    @PostMapping("dataSourceEnableStatus")
    public String dataSourceEnableStatus() {

        Result<Boolean> result = new Result<Boolean>();
        boolean flag = false;
        String message = "";
        try {
            boolean closed = materialService.getDataSource().isEnable();
            result.setResult(closed);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "关闭数据源连接池连接", notes = "关闭数据源连接池连接")
    @PostMapping("closeDataSource")
    public String closeDataSource() {
        Result result = new Result();
        boolean flag = false;
        String message = "";
        try {
            materialService.closeDataSource();
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }*/

    @ApiOperation(value = "检测图纸是否存在", notes = "检测图纸是否存在")
    @PostMapping("doesDrawingExist")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumber", value = "目前任务包图纸Name", dataTypeClass = String.class)
    }))
    public String doesDrawingExist(@RequestBody JSONObject jsonObject) {

        Result<Integer> result = new Result<Integer>();
        boolean flag = false;
        String message = "";
        try {
            String projectId = jsonObject.getString("projectId");
            String drawingNumber = jsonObject.getString("drawingNumber");
            int i = materialService.doesDrawingExist(projectId, drawingNumber);
            result.setResult(i);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "创建预测/预留", notes = "创建预测/预留")
    @PostMapping("createNewStatusRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "任务包的Name", dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "目前任务包图纸Name集合(从任务包取)", dataTypeClass = String.class)
    }))
    public String createNewStatusRequest(@RequestBody JSONObject jsonObject) {

        Result<ProcedureResult> result = new Result<ProcedureResult>();
        boolean flag = false;
        String message = "";
        try {
            String projectId = jsonObject.getString("projectId");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String drawingNumbers = jsonObject.getString("drawingNumbers");
//            List<String> drawingNumbers = jsonObject.getJSONArray("drawingNumbers").toJavaList(String.class);
//            ProcedureResult newStatusRequest = dataBaseConnectionService.createNewStatusRequest(projectId, requestName, requestType, drawingNumbers);
            ProcedureResult newStatusRequest = materialService.createNewStatusRequestDnStr(projectId, requestName, requestType, drawingNumbers);

            result.setResult(newStatusRequest);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "创建部分预测/预留", notes = "创建部分预测/预留")
    @PostMapping("createPartialStatusRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "任务包的Name", dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "目前任务包图纸Name集合(从任务包取)", dataTypeClass = List.class),
            @DynamicParameter(name = "commodityCodes", value = "材料的ID", dataTypeClass = List.class),
            @DynamicParameter(name = "sizes1", value = "材料的类型码尺寸信息1", dataTypeClass = List.class),
            @DynamicParameter(name = "sizes2", value = "材料的类型码尺寸信息2", dataTypeClass = List.class)
    }))
    public String createPartialStatusRequest(@RequestBody JSONObject jsonObject) {

        Result<ProcedureResult> result = new Result<ProcedureResult>();
        boolean flag = false;
        String message = "";
        try {
            String projectId = jsonObject.getString("projectId");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String drawingNumbers = jsonObject.getString("drawingNumbers");
            String commodityCodes = jsonObject.getString("commodityCodes");
            String sizes1 = jsonObject.getString("sizes1");
            String sizes2 = jsonObject.getString("sizes2");
            ProcedureResult newStatusRequest = materialService.createPartialStatusRequestStr(projectId, requestName, requestType,
                    drawingNumbers, commodityCodes,
                    sizes1, sizes2);
            result.setResult(newStatusRequest);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "获取预测结果", notes = "获取预测结果")
    @PostMapping("getMaterialStatusResults")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "requestId", dataTypeClass = Integer.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String getMaterialStatusResults(@RequestBody JSONObject jsonObject) {

        Result<JSONArray> result = new Result<JSONArray>();
        boolean flag = false;
        String message = "";
        try {
            int requestId = jsonObject.getIntValue("requestId");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            JSONArray materialStatusResults = materialService.getMaterialStatusResults(requestId, searchColumn, searchValue);
            result.setResult(materialStatusResults);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "基于整张图纸的预测/预留并获取预测结果", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("existAndCreateNewStatusRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "图纸Name集合", example = "DO-35009-01,DO-35009-04", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "任务包的Name", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "searchColumn", dataTypeClass = String.class),
            @DynamicParameter(name = "searchValue", dataTypeClass = String.class)
    }))
    public String existAndCreateNewStatusRequest(@RequestBody JSONObject jsonObject) {

        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        boolean flag = false;
        String message = "";
        try {
            String projectId = jsonObject.getString("projectId");
            String drawingNumbers = jsonObject.getString("drawingNumbers");
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            String searchColumn = jsonObject.getString("searchColumn");
            String searchValue = jsonObject.getString("searchValue");
            Map<String, Object> procedureResults = materialService
                    .existAndCreateNewStatusRequest(projectId,
                            requestName, requestType, drawingNumbers,
                            searchColumn, searchValue);
            result.setResult(procedureResults);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "查找对应的任务包或工作包在SPM中有没用进行过预测预留", notes = "查找对应的任务包或工作包在SPM中有没用进行过预测预留")
    @PostMapping("getLastestRequestInfo")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", dataTypeClass = String.class),
            @DynamicParameter(name = "requestName", value = "任务包的Name", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class)
    }))
    public String getLatestRequestInfo(@RequestBody JSONObject jsonObject) {

        Result<ProcedureResult> result = new Result<ProcedureResult>();
        boolean flag = false;
        String message = "";
        try {
            // 获取项目配置
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
            IObject projectConfig = queryEngine.queryOne(queryRequest);
            if (null == projectConfig) {
                throw new RuntimeException("获取项目配置失败!");
            }
            ICIMProjectConfig icimProjectConfig = projectConfig.toInterface(ICIMProjectConfig.class);

            String projectId = icimProjectConfig.getSPMProject();
            if (StringUtils.isEmpty(projectId)) {
                throw new RuntimeException("获取项目配置的项目号失败!");
            }
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            ProcedureResult requestInfo = materialService.getLatestRequestInfo(projectId, requestName, requestType);
            result.setResult(requestInfo);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "基于整张图纸的预测/预留", notes = "检测图纸是否存在,不存在则提示SPM不存在此图纸,存在的进行预测预留操作")
    @PostMapping("performStatusRequest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "projectId", value = "目前固定一个项目号 POC_A", example = "POC_A", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "drawingNumbers", value = "图纸Name集合", required = true, dataTypeClass = List.class),
            @DynamicParameter(name = "requestName", value = "任务包的Name", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class)
    }))
    public String performStatusRequest(@RequestBody JSONObject jsonObject) {

        Result<ProcedureResult> result = new Result<ProcedureResult>();
        boolean flag = false;
        String message = "";
        try {
            // 获取项目配置
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
            IObject projectConfig = queryEngine.queryOne(queryRequest);
            if (null == projectConfig) {
                throw new RuntimeException("获取项目配置失败!");
            }
            ICIMProjectConfig icimProjectConfig = projectConfig.toInterface(ICIMProjectConfig.class);

            String projectId = icimProjectConfig.getSPMProject();
            if (StringUtils.isEmpty(projectId)) {
                throw new RuntimeException("获取项目配置的项目号失败!");
            }
            List<String> drawingNumbers = jsonObject.getJSONArray("drawingNumbers").toJavaList(String.class);
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            ProcedureResult performStatusRequest = materialService
                    .performStatusRequest(projectId,
                            requestName, requestType, drawingNumbers);
            result.setResult(performStatusRequest);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "取消预测预留", notes = "取消预测预留")
    @PostMapping("undoMatStatusRequests")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {
            @DynamicParameter(name = "requestName", value = "任务包的Name", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "requestType", value = "FR是预测,RR是预留", required = true, dataTypeClass = String.class)
    }))
    public String undoMatStatusRequests(@RequestBody JSONObject jsonObject) {

        Result<ProcedureResult> result = new Result<ProcedureResult>();
        boolean flag = false;
        String message = "";
        try {
            // 获取项目配置
            IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
            QueryRequest queryRequest = queryEngine.start();
            queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
            IObject projectConfig = queryEngine.queryOne(queryRequest);
            if (null == projectConfig) {
                throw new RuntimeException("获取项目配置失败!");
            }
            ICIMProjectConfig icimProjectConfig = projectConfig.toInterface(ICIMProjectConfig.class);

            String projectId = icimProjectConfig.getSPMProject();
            if (StringUtils.isEmpty(projectId)) {
                throw new RuntimeException("获取项目配置的项目号失败!");
            }
            String requestName = jsonObject.getString("requestName");
            String requestType = jsonObject.getString("requestType");
            ProcedureResult requestInfo = materialService.undoMatStatusRequests(projectId, requestName, requestType);
            result.setResult(requestInfo);
            flag = true;
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            message = exception.getMessage();
        } finally {
            result.setSuccess(flag);
            result.setMessage(message);
            if (flag) {
                result.setCode(200);
            } else {
                result.setCode(500);
            }
        }
        return JSON.toJSONString(result);
    }

    @ApiOperation(value = "存储过程AOP测试", notes = "存储过程AOP测试")
    @PostMapping("aopTest")
    @ApiOperationSupport(params = @DynamicParameters(name = "JSONObject", properties = {

    }))
    public String aopTest(@RequestBody JSONObject jsonObject) {

        ResultVo<Boolean> result = new ResultVo<>();
        try {
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(loginUser.getUsername());
            //stringObjectHashMap.put(configurationItem.UID(), materialService.getDataSource());
            boolean b = materialService.aopTest();
            result.successResult(b);
        } catch (Exception exception) {
            result.errorResult(ExceptionUtil.getMessage(exception));
        }
        return JSON.toJSONString(result);
    }

}
