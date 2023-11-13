package ccm.server.model;

import ccm.server.enums.ExpansionMode;
import ccm.server.enums.orderMode;
import ccm.server.util.CommonUtility;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class OrderByParam {
    private final static String NODE_NAME = "orderBy";
    private final static String NODE_DEFUIDS = "defUIDs";
    private final static String NODE_DEFUID = "defUID";
    private final static String NODE_ORDER = "orderBy";
    private final static String NODE_DEFTYPE = "defType";
    private final static String NODE_ACS = "asc";
    private final List<OrderByWrapper> orderByWrappers = new ArrayList<>();

    protected orderMode getOrderMode(Boolean ascOrNot) {
        if (ascOrNot == null) return orderMode.none;
        else if (ascOrNot) return orderMode.asc;
        else return orderMode.desc;
    }

    public void insertFirstly(String defUID, boolean ascOrNot) {
        if (!StringUtils.isEmpty(defUID)) {
            List<OrderByWrapper> current = new ArrayList<>();
            current.add(new OrderByWrapper(this.getOrderMode(ascOrNot), defUID));
            current.addAll(this.orderByWrappers);
            this.orderByWrappers.clear();
            this.orderByWrappers.addAll(current);
        }
    }

    public void clear() {
        this.orderByWrappers.clear();
    }

    public OrderByParam(JSONObject jsonObject) {
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.getJSONArray(NODE_NAME);
            this.parseOrderBy(jsonArray);
        }
    }

    protected void parseDefUIDs(String defUIDs) {
        if (!StringUtils.isEmpty(defUIDs)) {
            List<String> orderParams = Arrays.stream(defUIDs.split(",")).collect(Collectors.toList());
            if (CommonUtility.hasValue(orderParams)) {
                for (String orderParam : orderParams) {
                    if (orderParam.contains("<<>>")) {
                        this.getOrderByWrappers().add(new OrderByWrapper(orderParam));
                    } else {
                        this.getOrderByWrappers().add(new OrderByWrapper(orderMode.asc, orderParam));
                    }
                }
            }

        }
    }

    public OrderByParam(JSONArray jsonArray) {
        this.parseOrderBy(jsonArray);
    }

    protected void parseOrderBy(JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            if (jsonArray.size() == 1) {
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(jsonArray.get(0)));
                if (jsonObject != null) {
                    String defUIDs = jsonObject.getString(NODE_DEFUIDS);
                    this.parseDefUIDs(defUIDs);
                    String asc = jsonObject.getString(NODE_ACS);
                    if (!StringUtils.isEmpty(asc)) {
                        Boolean ascOrNot = Boolean.parseBoolean(asc);
                        if (this.getOrderByWrappers().size() > 0) {
                            this.getOrderByWrappers().forEach(c -> c.setOrderMode(ascOrNot));
                        }
                    }
                }
            } else {
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(o));
                    if (jsonObject != null) {
                        String defUID = jsonObject.getString(NODE_DEFUIDS);
                        String orderBy = jsonObject.getString(NODE_ACS);
                        String defType = jsonObject.getString(NODE_DEFTYPE);
                        if (StringUtils.isEmpty(defType))
                            defType = ExpansionMode.none.toString();
                        if (!StringUtils.isEmpty(orderBy) && orderBy.equalsIgnoreCase(Boolean.TRUE.toString()))
                            orderBy = orderMode.asc.toString();
                        else if (!StringUtils.isEmpty(orderBy) && orderBy.equalsIgnoreCase(Boolean.FALSE.toString()))
                            orderBy = orderMode.desc.toString();
                        if (!StringUtils.isEmpty(defUID)) {
                            if (defUID.contains("."))
                                defType = ExpansionMode.relatedObject.toString();
                            else if (defUID.startsWith("+") || defType.startsWith("-"))
                                defType = ExpansionMode.relatedObject.toString();
                        }
                        this.getOrderByWrappers().add(new OrderByWrapper(defType + "->>>" + defUID + "<<>>" + orderBy));
                    }
                }
            }
        }
    }
}
