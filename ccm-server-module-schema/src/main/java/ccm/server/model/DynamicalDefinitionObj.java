package ccm.server.model;

import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyValueType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class DynamicalDefinitionObj {
    private String uid;
    private String name;
    private String description;
    private String displayAs;
    private propertyValueType propertyValueType;
    private ccm.server.enums.classDefinitionType classDefinitionType;
}
