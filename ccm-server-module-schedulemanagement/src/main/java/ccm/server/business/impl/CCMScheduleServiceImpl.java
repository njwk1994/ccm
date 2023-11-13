package ccm.server.business.impl;

import ccm.server.business.ICCMScheduleService;
import ccm.server.business.ISchemaBusinessService;
import ccm.server.context.CIMContext;
import ccm.server.dto.base.ObjectDTO;
import ccm.server.dto.base.ObjectItemDTO;
import ccm.server.enums.operator;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.excel.util.ExcelUtility;
import ccm.server.helper.HardCodeHelper;
import ccm.server.model.LoaderReport;
import ccm.server.models.query.QueryRequest;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.interfaces.IObject;
import ccm.server.util.CommonUtility;
import ccm.server.utils.ObjectDTOUtility;
import ccm.server.utils.PageUtility;
import ccm.server.utils.ScheduleUtils;
import ccm.server.utils.SchemaUtility;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 15:32
 */
@Slf4j
@Service
public class CCMScheduleServiceImpl implements ICCMScheduleService {

    @Autowired
    private ISchemaBusinessService schemaBusinessService;

    /**
     * CWP表格名称
     */
    private static final String CWP_SHEET_NAME = "CWPs";

    /**
     * 属性过滤词
     */
    private static final List<String> FILTER_WORDS = Arrays.asList(HardCodeHelper.OBJ_QUERY_INDICATORS);


    /**
     * 下载计划Excel模版
     *
     * @param response
     * @throws Exception
     */
    @Override
    public void downloadScheduleExcelTemplate(HttpServletResponse response) throws Exception {
        ExcelUtility.downloadTemplate("计划导入模版", response);
    }

    @Override
    public LoaderReport importScheduleByExcelTemplate(MultipartFile file) throws Exception {
        Map<String, Object> result = new HashMap<>();
        JSONObject jsonObject = ExcelUtility.importTemplateExcel(file);
        JSONArray jsonArray = jsonObject.getJSONArray(ExcelUtility.ITEMS);
        ICIMConfigurationItem configurationItem = CIMContext.Instance.getMyConfigurationItem(null);
        if (configurationItem == null)
            throw new Exception("未获取到有效的项目信息!");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject toChangeUIDObj = jsonArray.getJSONObject(i);
            JSONObject properties = toChangeUIDObj.getJSONObject(CommonUtility.JSON_FORMAT_PROPERTIES);
            properties.put(propertyDefinitionType.UID.toString(), SchemaUtility.generateUIDByJSONObject(toChangeUIDObj, configurationItem.Name(),true));
        }

        return schemaBusinessService.loadObjectsByJSONObject(jsonObject);
    }

    private String caseCellTypeToStringCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        String cellValue = "";
        switch (cellType) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 日期类型
                    cellValue = cell.getDateCellValue().toString();
                } else {
                    // 数值类型
                    cellValue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            default:
                cellValue = cell.getStringCellValue();
                break;
        }
        return cellValue;
    }

    /**
     * 获取计划
     *
     * @param items
     * @param pageRequest
     * @return
     * @throws Exception
     */
    @Override
    public IObjectCollection getSchedulesByItems(ObjectDTO items, PageRequest pageRequest) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        if (items.getItems().size() > 0) {
            for (ObjectItemDTO item : items.getItems()) {
                CIMContext.Instance.QueryEngine().addPropertyForQuery(queryRequest,
                        "", item.getDefUID(), operator.equal, item.getDisplayValue().toString());
            }
        }
        // 分页
        if (PageUtility.verifyPage(pageRequest)) {
            CIMContext.Instance.QueryEngine().setPageRequest(queryRequest, pageRequest);
        }
        return CIMContext.Instance.QueryEngine().query(queryRequest);
    }

    @Override
    public ObjectDTO getScheduleForm(String formPurpose) throws Exception {
        IObject form = schemaBusinessService.generateForm(ScheduleUtils.CCM_SCHEDULE);
        return form.toObjectDTO();
    }

    @Override
    public IObject getScheduleByOBID(String obid) throws Exception {
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, obid);
        return CIMContext.Instance.QueryEngine().queryOne(queryRequest);
    }

    @Override
    public List<ObjectDTO> getAllSchedules() throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        IObjectCollection query = CIMContext.Instance.QueryEngine().query(queryRequest);
        return ObjectDTOUtility.convertToObjectDTOList(query);
    }


    @Override
    public IObject createSchedule(ObjectDTO toCreateSchedule) throws Exception {
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        IObject newSchedule = SchemaUtility.newIObject(ScheduleUtils.CCM_SCHEDULE,
                toCreateSchedule.getName(),
                toCreateSchedule.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newSchedule,
                ScheduleUtils.I_SCHEDULE);*/
        for (ObjectItemDTO item : toCreateSchedule.getItems()) {
            newSchedule.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newSchedule.ClassDefinition().FinishCreate(newSchedule);
        // 提交事务
        CIMContext.Instance.Transaction().commit();
        return newSchedule;
    }

    @Override
    public void updateSchedule(ObjectDTO toUpdateSchedule) throws Exception {
        if (StringUtils.isBlank(toUpdateSchedule.getObid())) {
            throw new Exception("计划OBID不可为空!");
        }
        // 获取已存在的图纸
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdateSchedule.getObid());
        IObject existDocument = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existDocument.BeginUpdate();
        for (ObjectItemDTO item : toUpdateSchedule.getItems()) {
            existDocument.Interfaces().item(ScheduleUtils.I_SCHEDULE, true)
                    .Properties().item(item.getDefUID(), true).setValue(item.toValue());
        }
        existDocument.FinishUpdate();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public void deleteSchedule(String scheduleOBID) throws Exception {
        if (StringUtils.isBlank(scheduleOBID)) {
            throw new Exception("计划OBID不可为空!");
        }
        // 获取已存在的设计数据
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, scheduleOBID);
        IObject existDocument = CIMContext.Instance.QueryEngine().queryOne(queryRequest);
        // 开启事务
        if (!CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().start();
        }
        existDocument.Delete();
        // 提交事务
        CIMContext.Instance.Transaction().commit();
    }

    @Override
    public IObjectCollection getPackagesUnderSchedules(String scheduleOBID, String relDef) throws Exception {
        IObject schedulesByOBID = getScheduleByOBID(scheduleOBID);
        return schedulesByOBID.GetEnd1Relationships().GetRels(relDef).GetEnd2s();
    }

    private void createScheduleWithOutTransaction(ObjectDTO toCreateSchedule) throws Exception {

        IObject newSchedule = SchemaUtility.newIObject(ScheduleUtils.CCM_SCHEDULE,
                toCreateSchedule.getName(),
                toCreateSchedule.getDescription(),
                "", "");
        /*InterfaceDefUtility.addInterface(newSchedule,ScheduleUtils.I_SCHEDULE);*/
        for (ObjectItemDTO item : toCreateSchedule.getItems()) {
            newSchedule.setValue(item.getDefUID(), item.getDisplayValue());
        }
        // 结束创建
        newSchedule.ClassDefinition().FinishCreate(newSchedule);
    }

    private void updateScheduleWithOutTransaction(ObjectDTO toUpdateSchedule) throws Exception {
        // 获取已存在的图纸
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, toUpdateSchedule.getObid());
        IObject existDocument = CIMContext.Instance.QueryEngine().queryOne(queryRequest);

        existDocument.BeginUpdate();
        for (ObjectItemDTO item : toUpdateSchedule.getItems()) {
            existDocument.setValue(item.getDefUID(), item.getDisplayValue());
        }
        existDocument.FinishUpdate();
    }

    public void deleteScheduleWithOutTransaction(String scheduleOBID) throws Exception {
        // 获取已存在的设计数据
        QueryRequest queryRequest = CIMContext.Instance.QueryEngine().start();
        CIMContext.Instance.QueryEngine().addInterfaceForQuery(queryRequest, ScheduleUtils.I_SCHEDULE);
        CIMContext.Instance.QueryEngine().addOBIDForQuery(queryRequest, operator.equal, scheduleOBID);
        IObject existDocument = CIMContext.Instance.QueryEngine().queryOne(queryRequest);

        existDocument.Delete();

    }
}
