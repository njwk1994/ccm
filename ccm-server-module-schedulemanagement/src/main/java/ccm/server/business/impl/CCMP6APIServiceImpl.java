package ccm.server.business.impl;

import ccm.server.business.ICCMConfigService;
import ccm.server.business.ICCMP6APIService;
import ccm.server.context.CIMContext;
import ccm.server.engine.IQueryEngine;
import ccm.server.entity.ThreadResult;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.interfaces.ICIMProjectConfig;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.ICIMProjectConfigUtils;
import ccm.server.ws.p6.SoapClientUtils;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.http.webservice.SoapClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * P6API调用服务
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/9/9 17:30
 */
@Slf4j
@Service
public class CCMP6APIServiceImpl implements ICCMP6APIService {

    @Autowired
    private ICCMConfigService configService;

    private static final String P6_PROJECT_SERVICE_PATH = "ProjectService?wsdl";
    private static final String P6_PROJECT_V2_NAMESPACE_URL = "http://xmlns.oracle.com/Primavera/P6/WS/Project/V2";
    private static final String P6_EXPORT_SERVICE_PATH = "ExportService?wsdl";
    private static final String P6_EXPORT_V2_NAMESPACE_URL = "http://xmlns.oracle.com/Primavera/P6/WS/WSExport/V2";


    /**
     * 检查P6的webservice服务是否可以访问
     *
     * @return
     */
    @Override
    public ThreadResult<Boolean> isServiceAvailable() {
        IObject projectConfig;
        ThreadResult<Boolean> threadResult = new ThreadResult<>();
        try {
            projectConfig = configService.getProjectConfig();
            if (null == projectConfig) {
                throw new Exception("未能获取项目配置,获取到的项目配置为NULL,请检查配置!");
            }

            String p6WSUrl = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_WEBSERVICE_URL);
            String p6ProjectName = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_PROJECT_NAME);
            String p6ProjectLoginName = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_PROJECT_LOGIN_NAME);
            String p6ProjectPassword = projectConfig.getDisplayValue(ICIMProjectConfigUtils.P6_PROJECT_PASSWORD);

            // 测试填充
            /*p6WSUrl = "http://192.168.3.18:8206";
            p6ProjectName = "Test";
            p6ProjectLoginName = "admin";
            p6ProjectPassword = "admin";*/

            if (StringUtils.isBlank(p6WSUrl) || StringUtils.isBlank(p6ProjectName) || StringUtils.isBlank(p6ProjectLoginName) || StringUtils.isBlank(p6ProjectPassword)) {
                throw new Exception("未能获取项目配置中P6配置失败,P6地址:" + p6WSUrl +
                        ",P6项目名称:" + p6ProjectName +
                        ",P6项目登录用户名:" + p6ProjectLoginName +
                        ",P6项目登录密码:" + p6ProjectPassword +
                        ",请检查配置!");
            }
            try {
                HttpURLConnection connectionProject = (HttpURLConnection) new URL(SoapClientUtils.checkURLPath(p6WSUrl) + P6_PROJECT_SERVICE_PATH).openConnection();
                HttpURLConnection connectionExport = (HttpURLConnection) new URL(SoapClientUtils.checkURLPath(p6WSUrl) + P6_EXPORT_SERVICE_PATH).openConnection();
                if ((HttpURLConnection.HTTP_OK == connectionProject.getResponseCode()) && (HttpURLConnection.HTTP_OK == connectionExport.getResponseCode())) {
                    threadResult.successResult(true);
                } else {
                    throw new Exception("P6接口连接失败,获取服务:" + connectionProject.getResponseCode() + ",导出服务:" + connectionExport.getResponseCode() + ".");
                }
            } catch (Exception e) {
                throw new Exception("P6接口连接异常!" + ExceptionUtil.getSimpleMessage(e));
            }
        } catch (Exception e) {
            log.error("检查P6接口服务失败!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            threadResult.errorResult("检查P6接口服务失败!{}", ExceptionUtil.getSimpleMessage(e));
        }
        return threadResult;
    }

    /**
     * 检查P6的webservice服务是否可以访问
     *
     * @return
     */
    @Override
    public ThreadResult<Boolean> isServiceAvailable(String p6WSUrl, String p6ProjectName, String p6ProjectLoginName, String p6ProjectPassword) {
        ThreadResult<Boolean> threadResult = new ThreadResult<>();
        try {
            if (StringUtils.isBlank(p6WSUrl) || StringUtils.isBlank(p6ProjectName) || StringUtils.isBlank(p6ProjectLoginName) || StringUtils.isBlank(p6ProjectPassword)) {
                throw new Exception("未能获取项目配置中P6配置失败,P6地址:" + p6WSUrl +
                        ",P6项目名称:" + p6ProjectName +
                        ",P6项目登录用户名:" + p6ProjectLoginName +
                        ",P6项目登录密码:" + p6ProjectPassword +
                        ",请检查配置!");
            }
            HttpURLConnection connectionProject;
            HttpURLConnection connectionExport;
            try {
                connectionProject = (HttpURLConnection) new URL(SoapClientUtils.checkURLPath(p6WSUrl) + P6_PROJECT_SERVICE_PATH).openConnection();
                connectionExport = (HttpURLConnection) new URL(SoapClientUtils.checkURLPath(p6WSUrl) + P6_EXPORT_SERVICE_PATH).openConnection();
            } catch (Exception e) {
                throw new Exception("P6接口连接异常!" + ExceptionUtil.getSimpleMessage(e));
            }
            if ((HttpURLConnection.HTTP_OK == connectionProject.getResponseCode()) && (HttpURLConnection.HTTP_OK == connectionExport.getResponseCode())) {
                threadResult.successResult(true);
            } else {
                throw new Exception("P6接口连接失败,获取服务:" + connectionProject.getResponseCode() + ",导出服务:" + connectionExport.getResponseCode() + ".");
            }
        } catch (Exception e) {
            log.error("检查P6接口服务失败!{}", ExceptionUtil.getSimpleMessage(e), ExceptionUtil.getRootCause(e));
            threadResult.errorResult("检查P6接口服务失败!{}", ExceptionUtil.getSimpleMessage(e));
        }
        return threadResult;
    }

    @Override
    public String readProjects() throws Exception {
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
        IObject iObject = queryEngine.queryOne(queryRequest);
        if (null == iObject) {
            log.error("获取P6项目时获取项目配置失败!");
            throw new RuntimeException("获取P6项目时获取项目配置失败,请检查项目配置!");
        }
        ICIMProjectConfig icimProjectConfig = iObject.toInterface(ICIMProjectConfig.class);
        String p6WebservicePath = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_WEBSERVICE_URL);
        String p6ProjectName = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_PROJECT_NAME);
        String p6ProjectLoginName = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_PROJECT_LOGIN_NAME);
        String p6ProjectPassword = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_PROJECT_PASSWORD);
        if (org.springframework.util.StringUtils.isEmpty(p6WebservicePath) || org.springframework.util.StringUtils.isEmpty(p6ProjectName) || org.springframework.util.StringUtils.isEmpty(p6ProjectLoginName) || org.springframework.util.StringUtils.isEmpty(p6ProjectPassword)) {
            log.error("获取P6项目时获取P6项目配置失败!WS地址:{},项目名称:{},登录用户名:{},登录密码:{}", p6WebservicePath, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);
            throw new RuntimeException("获取P6项目时获取P6项目配置失败,请检查项目配置!WS地址:" + p6WebservicePath + ",项目名称:" + p6ProjectName + ",登录用户名:" + p6ProjectLoginName + ",登录密码:" + p6ProjectPassword);
        }
        return readProjects(p6WebservicePath, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);
    }

    @Override
    public String readProjects(String p6WebservicePath, String p6ProjectName, String p6ProjectLoginName, String p6ProjectPassword) throws Exception {
        String objectId;
        SoapClient clientWithWsseNoCache = SoapClientUtils.createClientWithWsseNoCache(SoapClientUtils.checkURLPath(p6WebservicePath) + P6_PROJECT_SERVICE_PATH, p6ProjectLoginName, p6ProjectPassword);
        clientWithWsseNoCache.setMethod("v2:ReadProjects", P6_PROJECT_V2_NAMESPACE_URL)
                .setParam("Field", "Name")
                .setParam("Filter", "Name='" + p6ProjectName + "'");
        String send = clientWithWsseNoCache.send();
        Document document = DocumentHelper.parseText(send);
        Element rootElement = document.getRootElement();
        Element envBody = rootElement.element("Body");
        Element readProjectsResponse = envBody.element("ReadProjectsResponse");
        Element project = readProjectsResponse.element("Project");
        if (org.springframework.util.StringUtils.isEmpty(project)) {
            throw new Exception("未从P6获取到对应项目名称为\"" + p6ProjectName + "\"的项目信息!");
        } else {
            objectId = project.elementText("ObjectId");
            if (org.springframework.util.StringUtils.isEmpty(objectId)) {
                throw new Exception("未从P6获取到对应项目名称为\"" + p6ProjectName + "\"的项目ID!");
            }
        }
        return objectId;
    }

    @Override
    public String exportProject(String objectId) throws Exception {
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
        IObject iObject = queryEngine.queryOne(queryRequest);
        if (null == iObject) {
            log.error("获取P6项目时获取项目配置失败!");
            throw new RuntimeException("获取P6项目时获取项目配置失败,请检查项目配置!");
        }
        ICIMProjectConfig icimProjectConfig = iObject.toInterface(ICIMProjectConfig.class);
        String p6WebservicePath = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_WEBSERVICE_URL);
        String p6ProjectName = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_PROJECT_NAME);
        String p6ProjectLoginName = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_PROJECT_LOGIN_NAME);
        String p6ProjectPassword = icimProjectConfig.getStringPropertyValue(ICIMProjectConfigUtils.P6_PROJECT_PASSWORD);
        if (org.springframework.util.StringUtils.isEmpty(p6WebservicePath) || org.springframework.util.StringUtils.isEmpty(p6ProjectName) || org.springframework.util.StringUtils.isEmpty(p6ProjectLoginName) || org.springframework.util.StringUtils.isEmpty(p6ProjectPassword)) {
            log.error("获取P6项目时获取P6项目配置失败!WS地址:{},项目名称:{},登录用户名:{},登录密码:{}", p6WebservicePath, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);
            throw new RuntimeException("获取P6项目时获取P6项目配置失败,请检查项目配置!WS地址:" + p6WebservicePath + ",项目名称:" + p6ProjectName + ",登录用户名:" + p6ProjectLoginName + ",登录密码:" + p6ProjectPassword);
        }

        return exportProject(objectId, p6WebservicePath, p6ProjectName, p6ProjectLoginName, p6ProjectPassword);
    }

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
    @Override
    public String exportProject(String objectId, String p6WebservicePath, String p6ProjectName, String p6ProjectLoginName, String p6ProjectPassword) throws Exception {
        SoapClient clientWithWsseNoCache = SoapClientUtils.createClientWithWsseNoCache(SoapClientUtils.checkURLPath(p6WebservicePath) + P6_EXPORT_SERVICE_PATH, p6ProjectLoginName, p6ProjectPassword);
        clientWithWsseNoCache.setMethod("v2:ExportProject", P6_EXPORT_V2_NAMESPACE_URL)
                .setParam("ProjectObjectId", objectId);
        String send = clientWithWsseNoCache.send();
        Document document = DocumentHelper.parseText(send);
        Element rootElement = document.getRootElement();
        Element envBody = rootElement.element("Body");
        Element exportProjectResponse = envBody.element("ExportProjectResponse");
        String projectData = exportProjectResponse.elementText("ProjectData");
        if (StringUtils.isEmpty(projectData)) {
            throw new Exception("未从P6获取到对应项目名称为\"" + p6ProjectName + "\"的项目数据!");
        }
        // 获取到的数据为base64加密数据,返回解密后的XML字符串
        return Base64.decodeStr(projectData);
    }
}
