package ccm.server.business.impl;

import ccm.server.business.ICCMConfigService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.engine.IQueryEngine;
import ccm.server.enums.operator;
import ccm.server.models.query.QueryRequest;
import ccm.server.schema.interfaces.IObject;
import ccm.server.utils.ICIMProjectConfigUtils;
import ccm.server.utils.SchemaUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/6 16:29
 */
@Service
public class ICCMConfigServiceImpl implements ICCMConfigService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /**
     * 获取系统级配置表单
     *
     * @param formPurpose
     * @return
     * @throws Exception
     */
    @Override
    public ObjectDTO getSystemConfigForm(String formPurpose) throws Exception {
        return null;
    }

    /**
     * 获取系统级配置
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObject getSystemConfig() throws Exception {
        return null;
    }

    /**
     * 新增/更新系统级配置
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObject createOrUpdateSystemConfig() throws Exception {
        return null;
    }

    /**
     * 获取项目级配置表单
     *
     * @param formPurpose
     * @return
     * @throws Exception
     */
    @Override
    public ObjectDTO getProjectConfigForm(String formPurpose) throws Exception {
        return null;
    }

    /**
     * 获取项目级配置
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObject getProjectConfig() throws Exception {
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
        return queryEngine.queryOne(queryRequest);
    }

    public IObject getProjectConfigByOBID(String obid) throws Exception {
        IQueryEngine queryEngine = CIMContext.Instance.QueryEngine();
        QueryRequest queryRequest = queryEngine.start();
        queryEngine.addClassDefForQuery(queryRequest, ICIMProjectConfigUtils.CIM_PROJECT_CONFIG);
        queryEngine.addOBIDForQuery(queryRequest, operator.equal, obid);
        return queryEngine.queryOne(queryRequest);
    }

    /**
     * 新增/更新系统级配置
     *
     * @return
     * @throws Exception
     */
    @Override
    public IObject createOrUpdateProjectConfig(ObjectDTO objectDTO) throws Exception {
        IObject projectConfig = getProjectConfig();
        IObject iObject = null;

        if (null == projectConfig) {
            // 不存在配置时新建
            iObject = schemaBusinessService.generalCreate(objectDTO);
        } else {
            IObject projectConfigByOBID = getProjectConfigByOBID(objectDTO.getObid());
            if (null == projectConfigByOBID) {
                throw new RuntimeException("需要更新的配置与已存在的配置OBID不匹配,请检查!");
            } else {
                SchemaUtility.beginTransaction();
                projectConfig.BeginUpdate();
                // 存在配置时更新
                List<ObjectItemDTO> items = objectDTO.getItems();
                for (ObjectItemDTO item : items) {
                    projectConfig.setValue(item.getDefUID(), item.getDisplayValue());
                }
                projectConfig.FinishUpdate();
                SchemaUtility.commitTransaction();
            }
        }
        return iObject;
    }


    /**
     * 新增/更新系统级配置
     *
     * @param spmdbHost
     * @param spmdbPort
     * @param spmDatabaseName
     * @param spmdbUsername
     * @param spmdbPassword
     * @param spmProject
     * @param procedureType
     * @return
     * @throws Exception
     */
    @Override
    public IObject createOrUpdateProjectConfigTest(String spmdbHost, String spmdbPort, String spmDatabaseName, String spmdbUsername, String spmdbPassword, String spmProject, String procedureType) throws Exception {
        IObject projectConfig = getProjectConfig();
        IObject iObject = null;
        SchemaUtility.beginTransaction();
        if (null == projectConfig) {
            // 不存在配置时新建
            iObject = SchemaUtility.newIObject(ICIMProjectConfigUtils.CIM_PROJECT_CONFIG, "项目配置", "项目配置", null, null);
            iObject.setValue(ICIMProjectConfigUtils.SPM_DB_HOST, spmdbHost);
            iObject.setValue(ICIMProjectConfigUtils.SPM_DB_PORT, spmdbPort);
            iObject.setValue(ICIMProjectConfigUtils.SPM_DATABASE_NAME, spmDatabaseName);
            iObject.setValue(ICIMProjectConfigUtils.SPM_DB_USERNAME, spmdbUsername);
            iObject.setValue(ICIMProjectConfigUtils.SPM_DB_PASSWORD, spmdbPassword);
            iObject.setValue(ICIMProjectConfigUtils.SPM_PROJECT, spmProject);
            iObject.setValue(ICIMProjectConfigUtils.PROCEDURE_TYPE, procedureType);
            iObject.ClassDefinition().FinishCreate(iObject);
        } else {
            SchemaUtility.beginTransaction();
            projectConfig.BeginUpdate();
            // 存在配置时更新
            projectConfig.setValue(ICIMProjectConfigUtils.SPM_DB_HOST, spmdbHost);
            projectConfig.setValue(ICIMProjectConfigUtils.SPM_DB_PORT, spmdbPort);
            projectConfig.setValue(ICIMProjectConfigUtils.SPM_DATABASE_NAME, spmDatabaseName);
            projectConfig.setValue(ICIMProjectConfigUtils.SPM_DB_USERNAME, spmdbUsername);
            projectConfig.setValue(ICIMProjectConfigUtils.SPM_DB_PASSWORD, spmdbPassword);
            projectConfig.setValue(ICIMProjectConfigUtils.SPM_PROJECT, spmProject);
            projectConfig.setValue(ICIMProjectConfigUtils.PROCEDURE_TYPE, procedureType);
            projectConfig.FinishUpdate();
            SchemaUtility.commitTransaction();
        }
        SchemaUtility.commitTransaction();
        return iObject;
    }
}
