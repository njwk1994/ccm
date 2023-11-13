package ccm.server.model;

import ccm.server.enums.classDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relDirection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RenderColumn {
    public static RenderColumn OBIDRenderColumn = new RenderColumn(classDefinitionType.PropertyDef, propertyDefinitionType.OBID.toString(), null);
    public static RenderColumn UIDRenderColumn = new RenderColumn(classDefinitionType.PropertyDef, propertyDefinitionType.UID.toString(), null);
    private classDefinitionType schemaType;
    private String definition;
    private relDirection direction;
    private Integer groupIndex = null;
    private int columnIndex = 0;

    public RenderColumn(classDefinitionType classDefinitionType, String definition, relDirection direction) {
        this.schemaType = classDefinitionType;
        this.definition = definition;
        this.direction = direction;
    }

}
