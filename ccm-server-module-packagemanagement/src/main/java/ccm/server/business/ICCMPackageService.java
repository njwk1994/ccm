package ccm.server.business;

import ccm.server.enums.PackageTypeEnum;
import ccm.server.enums.ProcedureTypeEnum;
import ccm.server.schema.collections.IObjectCollection;

import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/7/8 7:48
 */
public interface ICCMPackageService {

    /**
     * 获取包对应的施工数据分类
     *
     * @param packageOBID
     * @return
     * @throws Exception
     */
    IObjectCollection getPackageConstructionTypes(String packageOBID, String packageClassDefinitionUID,boolean needConsumeMaterial) throws Exception;


    /**
     * 包预测预留
     *
     * @param packageType    包类型
     * @param packageId      包OBID
     * @param projectId      项目号
     * @param requestName    包名称
     * @param requestType    FR是预测,RR是预留
     * @param warehouses     仓库
     * @param drawingNumbers 图纸号
     * @param searchColumn   查询字段
     * @param searchValue    查询值
     * @param procedureType  存储过程类型
     * @return
     * @throws Exception
     */
    Map<String, Object> existAndCreateRequest(PackageTypeEnum packageType, String packageId, String projectId, String requestName, String requestType, String warehouses,
                                              String drawingNumbers, String searchColumn, String searchValue, ProcedureTypeEnum procedureType) throws Exception;

    /**
     * 确认升版
     *
     * @param packageOBID
     * @param packageType
     * @throws Exception
     */
    void confirmRevision(String packageOBID, PackageTypeEnum packageType) throws Exception;
}
