package ccm.server.dto.base;

import ccm.server.enums.orderMode;
import ccm.server.enums.propertyValueType;
import ccm.server.model.FiltersParam;
import ccm.server.model.OrderByWrapper;
import ccm.server.model.OrderByParam;
import ccm.server.model.ValueWithOperator;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

@Data
@Slf4j
public class ObjectDTOCollection implements Serializable {
    private static final long serializableId = 1L;

    protected int doCompare(ObjectItemDTO item1, ObjectItemDTO item2) {
        if (item1 != null && item2 != null) {
            int result = -1;
            propertyValueType propertyValueType = ccm.server.enums.propertyValueType.StringType;
            try {
                if (!StringUtils.isEmpty(item1.getPropertyValueType()))
                    propertyValueType = ccm.server.enums.propertyValueType.valueOf(item1.getPropertyValueType());
            } catch (IllegalArgumentException e) {
                log.error("comparator failed", e);
                propertyValueType = ccm.server.enums.propertyValueType.StringType;
            } finally {
                Object value1 = propertyValueType.parseValue(item1.getDisplayValue());
                Object value2 = propertyValueType.parseValue(item2.getDisplayValue());
                if (value1 != null && value2 != null) {
                    result = propertyValueType.compareTo(value1, value2);
                } else if (value1 == null && value2 == null)
                    result = 0;
                else
                    result = -1;
            }
            return result;
        } else if (item1 == null && item2 == null)
            return 0;
        return -1;
    }

    public void initOrderByParam(OrderByParam orderByParam) {
        if (orderByParam != null) {
            this.orderByWrappers.addAll(orderByParam.getOrderByWrappers());
        }
    }

    private final Map<String, String> filters = new HashMap<>();

    public void initFilterParam(FiltersParam filtersParam) {
        if (filtersParam != null) {
            this.filters.putAll(filtersParam.getFilters());
        }

    }

    protected Comparator<ObjectDTO> comparator(String defUID) {
        return new Comparator<ObjectDTO>() {
            @Override
            public int compare(ObjectDTO o1, ObjectDTO o2) {
                if (o1 != null && o2 != null) {
                    ObjectItemDTO item1 = o1.toGetItem(defUID);
                    ObjectItemDTO item2 = o2.toGetItem(defUID);
                    return doCompare(item1, item2);
                } else if (o1 == null && o2 == null)
                    return 0;
                return -1;
            }
        };
    }

    private final List<ObjectDTO> items = new ArrayList<>();
    private Integer current;
    private Integer size;
    private Long total;
    private String token;
    private final List<OrderByWrapper> orderByWrappers = new ArrayList<>();

    public void clearOrderBys() {
        this.orderByWrappers.clear();
    }

    public void addOrderBy(String defUID, orderMode orderMode) {
        if (!StringUtils.isEmpty(defUID)) {
            OrderByWrapper order = new OrderByWrapper(orderMode, defUID);
            if (!this.orderByWrappers.contains(order))
                this.orderByWrappers.add(order);
        }
    }

    public void addOrderBy(String defUID) {
        if (!StringUtils.isEmpty(defUID)) {
            OrderByWrapper order = new OrderByWrapper(null, defUID);
            if (!this.orderByWrappers.contains(order))
                this.orderByWrappers.add(order);
        }
    }

    public ObjectDTOCollection(Collection<ObjectDTO> item) {
        this.items.addAll(item);
        this.setCurrent(1);
        this.setSize(this.items.size());
        this.total = Long.parseLong(String.valueOf(this.items.size()));
    }

    public void setSize(Integer value) {
        if (value != null && value > 0) {
            this.size = value;
        } else
            this.size = this.items.size();
    }

    public void setCurrent(Integer value) {
        if (value != null && value > 0) {
            this.current = value;
        } else
            this.current = 1;
    }

    public void adjust() {
        this.onFilter();
        this.onSort();
        this.onFill();
    }

    private void onFill() {
        if (this.size > 0) {
            this.total = (long) this.items.size();
            List<List<ObjectDTO>> list = CommonUtility.createList(this.items, this.size);
            if (this.current > 0 && list.size() > 0) {
                if (this.current > list.size())
                    this.current = list.size();
                List<ObjectDTO> dtoList = list.get(this.current - 1);
                this.items.clear();
                this.items.addAll(dtoList);
            }
        }
    }

    private void onFilter() {
        if (this.filters.size() > 0) {
            List<ObjectDTO> result = new ArrayList<>();
            for (ObjectDTO item : this.items) {
                boolean flag = true;
                for (Map.Entry<String, String> entry : this.filters.entrySet()) {
                    String definitionInfo = entry.getKey();
                    ValueWithOperator valueWithOperator = new ValueWithOperator(entry.getValue());
                    ObjectItemDTO objectItemDTO = item.toGetItem(definitionInfo);
                    if (objectItemDTO != null) {
                        String value = CommonUtility.valueToString(objectItemDTO.getDisplayValue());
                        if (objectItemDTO.getOptions() != null) {
                            String finalValue = value;
                            OptionItemDTO optionItemDTO = objectItemDTO.getOptions().stream().filter(c -> c.getDisplayAs().equalsIgnoreCase(finalValue) || c.getName().equalsIgnoreCase(finalValue) || c.getDescription().equalsIgnoreCase(finalValue)).findFirst().orElse(null);
                            if (optionItemDTO != null)
                                value = optionItemDTO.getUid();
                        }
                        flag = valueWithOperator.inHint(value);
                        if (!flag)
                            break;
                    }
                }
                if (flag)
                    result.add(item);
            }
            this.items.clear();
            this.items.addAll(result);
        }
    }

    private void onSort() {
        if (this.orderByWrappers.size() > 0) {
            Comparator<ObjectDTO> comparator = null;
            for (OrderByWrapper orderByWrapper : this.orderByWrappers) {
                switch (orderByWrapper.getOrderMode()) {
                    case none:
                        break;
                    case asc:
                        if (comparator == null)
                            comparator = Comparator.comparing(c -> c, this.comparator(orderByWrapper.getIdentity()));
                        else
                            comparator = comparator.thenComparing(c -> c, this.comparator(orderByWrapper.getIdentity()));
                        break;
                    case desc:
                        if (comparator == null)
                            comparator = Comparator.comparing(c -> c, this.comparator(orderByWrapper.getIdentity()).reversed());
                        else
                            comparator = comparator.thenComparing(c -> c, this.comparator(orderByWrapper.getIdentity()).reversed());
                }
            }
            this.items.sort(comparator);
        }
    }

    public void initPageInfo(Integer pageIndex, Integer pageSize) {
        this.setCurrent(pageIndex);
        this.setSize(pageSize);
    }
}