package ccm.server.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PropertyScopedByInfo {
    private String propertyDef;
    private String scopedByUID;
    private String scopedByClassDefUID;
}
