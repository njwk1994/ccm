package ccm.server.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class FiltersParam {
    private final Map<String, String> filters = new HashMap<>();
    private final static String NODE_NAME = "filters";

    public FiltersParam(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(NODE_NAME);
            this.parseFilters(jsonArray);
        }
    }

    private void parseFilters(JSONArray jsonArray) {
        if (jsonArray != null) {
            for (Object o : jsonArray) {
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(o));
                if (jsonObject != null) {
                    for (Map.Entry<String, Object> objectEntry : jsonObject.entrySet()) {
                        String value = objectEntry.getValue() != null ? objectEntry.getValue().toString() : "";
                        filters.putIfAbsent(objectEntry.getKey(), value);
                    }
                }
            }
        }
    }

    public FiltersParam(JSONArray jsonArray) {
        this.parseFilters(jsonArray);
    }
}
