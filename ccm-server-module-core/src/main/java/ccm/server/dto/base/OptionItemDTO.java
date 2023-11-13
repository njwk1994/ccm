package ccm.server.dto.base;

import ccm.server.entity.MetaDataObj;
import ccm.server.enums.operationPurpose;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relDirection;
import ccm.server.enums.relEndQuantity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@Slf4j
public class OptionItemDTO implements Serializable {
    private static final long serializableId = 1L;
    private String uid;
    private String name;
    private String description;
    private String displayAs;
    private Boolean selected;

    public OptionItemDTO copyTo() {
        OptionItemDTO result = new OptionItemDTO();
        result.setUid(this.uid);
        result.setName(this.name);
        result.setDescription(this.description);
        result.setDisplayAs(this.displayAs);
        result.setSelected(this.selected);
        return result;
    }

    public OptionItemDTO() {

    }

    public OptionItemDTO(String pstrId, String pstrName, String pstrDescription, String pstrDisplayAs) {
        this();
        this.setUid(pstrId);
        this.setName(pstrName);
        this.setDescription(pstrDescription);
        if (StringUtils.isEmpty(pstrDisplayAs))
            pstrDisplayAs = pstrName + (StringUtils.isEmpty(pstrDescription) ? "" : "," + pstrDescription);
        this.setDisplayAs(pstrDisplayAs);
    }

    public OptionItemDTO(String pstrId, String pstrName, String pstrDescription) {
        this(pstrId, pstrName, pstrDescription, "");
    }

    public OptionItemDTO(@NotNull MetaDataObj metaObj) {
        this(metaObj.getObid(), metaObj.getName(), metaObj.getDescription(), "");
    }

    public static List<OptionItemDTO> propertyValueTypeOptions() {
        List<OptionItemDTO> items = new ArrayList<>();
        items.add(new OptionItemDTO(propertyValueType.StringType.getUid(), propertyValueType.StringType.toString(), "字符串", ""));
        items.add(new OptionItemDTO(propertyValueType.BooleanType.getUid(), propertyValueType.BooleanType.toString(), "布尔型", ""));
        items.add(new OptionItemDTO(propertyValueType.DoubleType.getUid(), propertyValueType.DoubleType.toString(), "浮点型", ""));
        items.add(new OptionItemDTO(propertyValueType.IntegerType.getUid(), propertyValueType.IntegerType.toString(), "整型", ""));
        items.add(new OptionItemDTO(propertyValueType.DateTimeType.getUid(), propertyValueType.DateTimeType.toString(), "日期", ""));
        items.add(new OptionItemDTO(propertyValueType.YMDType.getUid(), propertyValueType.YMDType.toString(), "年月日", ""));
        return items;
    }

    public static List<OptionItemDTO> operationPurposeOptions() {
        List<OptionItemDTO> items = new ArrayList<>();
        items.add(new OptionItemDTO(operationPurpose.create.getUid(), operationPurpose.create.toString(), "创建", ""));
        items.add(new OptionItemDTO(operationPurpose.update.getUid(), operationPurpose.update.toString(), "更新", ""));
        items.add(new OptionItemDTO(operationPurpose.info.getUid(), operationPurpose.info.toString(), "信息", ""));
        items.add(new OptionItemDTO(operationPurpose.query.getUid(), operationPurpose.query.toString(), "查询", ""));
        items.add(new OptionItemDTO(operationPurpose.all.getUid(), operationPurpose.all.toString(), "全部", ""));
        return items;
    }

    //to generate option items with True and False
    //only for property value type of Boolean
    public static List<OptionItemDTO> booleanOptions() {
        List<OptionItemDTO> items = new ArrayList<>();
        items.add(new OptionItemDTO(Boolean.FALSE.toString(), "×", "否", ""));
        items.add(new OptionItemDTO(Boolean.TRUE.toString(), "√", "是", ""));
        return items;
    }

    public static List<OptionItemDTO> endQuantityOptions() {
        List<OptionItemDTO> items = new ArrayList<>();
        items.add(new OptionItemDTO(relEndQuantity._0.getUid(), "0", "不必须", ""));
        items.add(new OptionItemDTO(relEndQuantity._1.getUid(), "1", "至少存在一个", ""));
        items.add(new OptionItemDTO(relEndQuantity._M.getUid(), "*", "多个", ""));
        return items;
    }

    public static List<OptionItemDTO> relDirectionOptions() {
        List<OptionItemDTO> items = new ArrayList<>();
        items.add(new OptionItemDTO(relDirection._1To2.getUid(), relDirection._1To2.toString(), relDirection._1To2.getDisplayAs()));
        items.add(new OptionItemDTO(relDirection._2To1.getUid(), relDirection._2To1.toString(), relDirection._2To1.getDisplayAs()));
        return items;
    }

    @Override
    public String toString() {
        return "OptionItemDTO{" +
                   "id='" + uid + '\'' +
                   '}';
    }

    public static List<OptionItemDTO> toOptionItemsByOBJ(List<? extends MetaDataObj> objs) throws Exception {
        List<OptionItemDTO> items = new ArrayList<>();
        if (objs != null && objs.size() > 0) {
            for (MetaDataObj o : objs) {
                OptionItemDTO item = new OptionItemDTO(o);
                items.add(item);
            }
        }
        items.sort(new Comparator<OptionItemDTO>() {
            @Override
            public int compare(OptionItemDTO o1, OptionItemDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return items;
    }

    public static List<OptionItemDTO> toOptionItemsByObjectDTO(List<ObjectDTO> objs) throws Exception {
        List<OptionItemDTO> items = new ArrayList<>();
        if (objs != null && objs.size() > 0) {
            for (ObjectDTO o : objs) {
                OptionItemDTO item = new OptionItemDTO(o.getUid(), o.getName(), o.getDescription(), "");
                items.add(item);
            }
        }
        items.sort(new Comparator<OptionItemDTO>() {
            @Override
            public int compare(OptionItemDTO o1, OptionItemDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return items;
    }
}
