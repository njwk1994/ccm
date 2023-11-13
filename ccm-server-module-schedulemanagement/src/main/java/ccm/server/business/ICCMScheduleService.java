package ccm.server.business;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.model.LoaderReport;
import ccm.server.params.PageRequest;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/11 15:32
 */
public interface ICCMScheduleService {

    /**
     * 下载计划Excel模版
     *
     * @throws Exception
     */
    void downloadScheduleExcelTemplate(HttpServletResponse response) throws Exception;

    LoaderReport importScheduleByExcelTemplate(MultipartFile file) throws Exception;

    ObjectDTO getScheduleForm(String formPurpose) throws Exception;

    /**
     * 获取计划
     *
     * @return
     * @throws Exception
     */
    IObjectCollection getSchedulesByItems(ObjectDTO items, PageRequest pageRequest) throws Exception;

    IObject getScheduleByOBID(String obid) throws Exception;

    List<ObjectDTO> getAllSchedules() throws Exception;

    IObject createSchedule(ObjectDTO schedule) throws Exception;

    void updateSchedule(ObjectDTO schedule) throws Exception;

    void deleteSchedule(String scheduleOBID) throws Exception;

    IObjectCollection getPackagesUnderSchedules(String scheduleOBID, String relDef) throws Exception;
}
