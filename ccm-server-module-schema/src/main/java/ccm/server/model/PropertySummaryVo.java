package ccm.server.model;

import ccm.server.entity.MetaDataObjProperty;
import ccm.server.util.CommonUtility;
import ccm.server.util.PerformanceUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class PropertySummaryVo {
    private final int level;
    private final String propertyDefinitionUid;
    private final List<MetaDataObjProperty> dataProperties = new ArrayList<>();

    public PropertySummaryVo(String propertyDefinitionUid, int level) {
        this.propertyDefinitionUid = propertyDefinitionUid;
        this.level = level;
    }

    public List<PropertyHierarchyVo> generateItems() {
        List<PropertyHierarchyVo> result = new ArrayList<>();
        if (this.dataProperties.size() > 0) {
            StopWatch stopWatch = PerformanceUtility.start();
            Map<String, List<String>> listMap = new HashMap<>();
            for (MetaDataObjProperty dataProperty : dataProperties) {
                CommonUtility.doAddElementGeneral(listMap, dataProperty.getStrValue(), dataProperty.getObjObid());
            }
            log.info("generate item(s) and quantity :" + listMap.size() + PerformanceUtility.stop(stopWatch));
            if (listMap.size() > 0) {
                for (Map.Entry<String, List<String>> listEntry : listMap.entrySet()) {
                    PropertyHierarchyVo hierarchyItem = new PropertyHierarchyVo(this.propertyDefinitionUid, listEntry.getKey(), listEntry.getValue(), this.level);
                    result.add(hierarchyItem);
                }
            }
        }
        return result;
    }
}
