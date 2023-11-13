package ccm.server.module.materials.service;

import ccm.server.module.materials.entity.ProcedureInfo;
import ccm.server.module.materials.entity.ProcedureResult;
import com.alibaba.fastjson.JSONArray;
import org.jeecg.common.api.vo.Result;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface IMaterialService {

    /**
     * 调用 存储过程
     *
     * @param procedure 存储过程 例 doJob() doJob(?,?,?)
     * @return 执行结果
     */
    Result<List<Object>> callProcedure(ProcedureInfo procedure) throws Exception;

    /**
     * 调用 存储过程
     *
     * @param procedure 存储过程(存储过程名+参数) 例 doJob() doJob(?,?,?)
     * @return 返回结果集
     */
    ResultSet callProcedureForQuery(ProcedureInfo procedure) throws Exception;

    /**
     * 图纸是否存在
     *
     * @param projectId     项目ID
     * @param drawingNumber 图纸号
     * @return
     * @throws Exception
     */
    int doesDrawingExist(String projectId, String drawingNumber) throws Exception;

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
    ProcedureResult createNewStatusRequest(String projectId, String requestName, String requestType, List<String> drawingNumbers) throws Exception;

    /**
     * 创建部分预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合数组
     * @param commodityCodes
     * @param sizes1
     * @param sizes2
     * @return
     * @throws Exception
     */
    ProcedureResult createPartialStatusRequest(String projectId, String requestName, String requestType,
                                               List<String> drawingNumbers, List<String> commodityCodes,
                                               List<String> sizes1, List<String> sizes2) throws Exception;

    /**
     * 获取预测结果
     *
     * @param requestId
     * @param searchColumn
     * @param searchValue
     * @return
     * @throws Exception
     */
    JSONArray getMaterialStatusResults(int requestId, String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    Map<String, Object> existAndCreateNewStatusRequest(String projectId,
                                                       String requestName, String requestType, String drawingNumbers,
                                                       String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并预测/预留 获取预测数据
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    Map<String, Object> existAndCreatePartialStatusRequest(String projectId,
                                                           String requestName, String requestType, String drawingNumbers,
                                                           String commodityCodes, String size1s, String size2s,
                                                           String searchColumn, String searchValue) throws Exception;

    /**
     * 检测图纸 并部分预测/预留 获取预测数据 33
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    Map<String, Object> existAndCreatePartialStatusRequest33(String projectId,
                                                             String requestName, String requestType,
                                                             String warehouses, String drawingNumbers, String identCode,
                                                             String searchColumn, String searchValue) throws Exception;


    /**
     * 检测图纸 并按阶段进行材料预测预留 获取预测数据
     *
     * @param projectId      项目号
     * @param requestName    任务包名称
     * @param requestType    FR是预测,RR是预留
     * @param drawingNumbers 图纸Name集合
     * @throws Exception
     */
    Map<String, Object> existAndCreateFAWithExtraFilter(String projectId, String requestName, String requestType,
                                                        String lpAttrCode, String lpAttrValue,
                                                        String drawingNumbers, String commodityCodes,
                                                        String size1s, String size2s,
                                                        String searchColumn, String searchValue) throws Exception;

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
    ProcedureResult createNewStatusRequestDnStr(String projectId,
                                                String requestName,
                                                String requestType,
                                                String drawingNumbers) throws Exception;

    /**
     * 创建部分预测预留
     *
     * @param projectId      项目ID
     * @param requestName    TWP编号
     * @param requestType    FR是预测，RR是预留
     * @param drawingNumbers 图纸号集合字符串(用","分割)
     * @param commodityCodes 材料编号集合字符串(用","分割)
     * @param sizes1         PSize1s(用","分割)
     * @param sizes2         PSize2s(用","分割)
     * @return
     * @throws Exception
     */
    ProcedureResult createPartialStatusRequestStr(String projectId, String requestName, String requestType,
                                                  String drawingNumbers, String commodityCodes,
                                                  String sizes1, String sizes2) throws Exception;

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
    ProcedureResult CreatePartialStatusRequest33Str(String projectId, String requestName, String requestType,
                                                    String warehouses, String drawingNumbers, String identCode) throws Exception;

    /**
     * 查找对应的任务包或工作包在SPM中有没用进行过预测预留
     *
     * @param projectId   项目ID
     * @param requestName TWP编号
     * @param requestType FR是预测，RR是预留
     * @return
     * @throws Exception
     */
    ProcedureResult getLatestRequestInfo(String projectId, String requestName, String requestType) throws Exception;

    /**
     * 如果想新建或从新预测预留用这个，这个是基于整张图纸的预留预留。
     * 还有一个是基于图纸和材料编码的预测预留。相比这个预测预留就是将材料编码也作为参数传递给SPM并获取该图纸下该材料的预测预留结果
     *
     * @param projectId
     * @param drawingNumbers
     * @throws Exception
     */
    ProcedureResult performStatusRequest(String projectId,
                                         String requestName, String requestType,
                                         List<String> drawingNumbers) throws Exception;

    /**
     * 取消预测预留
     *
     * @param projectId   项目ID
     * @param requestName TWP编号
     * @param requestType FR是预测，RR是预留
     * @return
     * @throws Exception
     */
    ProcedureResult undoMatStatusRequests(String projectId, String requestName, String requestType) throws Exception;

    ProcedureResult createFAWithExtraFilterStr(String projectId, String requestName, String requestType,
                                               String lpAttrCode, String lpAttrValue,
                                               String drawingNumbers, String commodityCodes,
                                               String sizes1, String sizes2) throws Exception;

    boolean aopTest() throws Exception;
}
