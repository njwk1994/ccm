package ccm.server.schema.interfaces;

import ccm.server.model.LoaderReport;
import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

public interface ICIMLoader extends IObject {

    LoaderReport loadData(JSONObject jsonObject) throws Exception;

    LoaderReport loadDataByXml(@NotNull MultipartFile file) throws Exception;
}
