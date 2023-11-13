package ccm.server.model;

import ccm.server.enums.ExpansionMode;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.propertyValueType;
import ccm.server.enums.relDirection;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class LiteCriteria {
    private String interfaceDefinitionUID;
    private String pathDefinition;
    private relDirection relDirection;
    private String propertyDefinition;
    private String propertyValueType;
    private ExpansionMode expansionMode = ExpansionMode.none;

    public LiteCriteria(String pathDefinition, relDirection relDirection, String interfaceDefinitionUID, String propertyDefinition, String propertyValueType, ExpansionMode expansionMode) {
        this.pathDefinition = pathDefinition;
        this.relDirection = relDirection;
        this.interfaceDefinitionUID = interfaceDefinitionUID;
        this.propertyDefinition = propertyDefinition;
        this.expansionMode = expansionMode;
        this.propertyValueType = propertyValueType;
    }

    public String getPropertyValueType() {
        if (this.propertyValueType == null || StringUtils.isEmpty(this.propertyValueType))
            this.propertyValueType = ccm.server.enums.propertyValueType.StringType.name();
        return this.propertyValueType;
    }

    public String expansionPath() {
        if (this.relDirection == null)
            this.relDirection = ccm.server.enums.relDirection._1To2;
        if (!StringUtils.isEmpty(this.pathDefinition))
            return this.relDirection.getPrefix() + this.pathDefinition;
        return "";
    }

    public String getPropertyDefinition() {
        if (this.propertyDefinition == null || StringUtils.isEmpty(this.propertyDefinition))
            this.propertyDefinition = propertyDefinitionType.Name.toString();
        return this.propertyDefinition;
    }

    public String expansionModePlusIdentity() {
        return this.expansionMode + "->>>" + this.identity();
    }

    public String identity() {
        String result = this.getPropertyDefinition();
        if (!StringUtils.isEmpty(this.pathDefinition)) {
            result = this.expansionPath() + "." + result;
        }
        return result;
    }
}
