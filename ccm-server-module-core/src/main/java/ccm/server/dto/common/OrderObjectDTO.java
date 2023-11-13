package ccm.server.dto.common;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.entity.MetaDataObj;
import ccm.server.entity.MetaDataObjProperty;
import ccm.server.entity.MetaDataRel;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class OrderObjectDTO extends ObjectDTO implements Serializable {
    private static final long serializableId = 1L;
    private final List<String> sortProperties = new ArrayList<>();
    private int orderValue;

    public int getOrderValue() {
        return this.orderValue;
    }

    public void setOrderValue(int orderValue) {
        this.orderValue = orderValue;
    }

    public static List<OrderObjectDTO> sort(List<OrderObjectDTO> items, String defUID) {
        if (CommonUtility.hasValue(items)) {
            if (StringUtils.isEmpty(defUID))
                defUID = propertyDefinitionType.Name.toString();
            String finalDefUID = defUID;
            return items.stream().sorted(new Comparator<OrderObjectDTO>() {
                @Override
                public int compare(OrderObjectDTO o1, OrderObjectDTO o2) {
                    String value1 = o1.toGetValue(finalDefUID);
                    String value2 = o2.toGetValue(finalDefUID);
                    return value1.compareTo(value2);
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    public OrderObjectDTO() {
        super();
    }

    public OrderObjectDTO(MetaDataObj metaObj, List<? extends MetaDataObjProperty> objPrs) {
        super(metaObj, objPrs);
    }

    public OrderObjectDTO(MetaDataRel rel, List<? extends MetaDataObjProperty> objPrs) {
        super(rel, objPrs);
    }
}
