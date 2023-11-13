package ccm.server.model;

import ccm.server.context.CIMContext;
import ccm.server.enums.*;
import ccm.server.schema.interfaces.IEdgeDef;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IPropertyDef;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Data
@Slf4j
public class displayItemWrapper {
    private String relOrEdgeDef = "";
    private String propertyDef = propertyDefinitionType.Name.toString();
    private relDirection relDirection = ccm.server.enums.relDirection._unknown;
    private final static String REGEX_CHAR_DOT = "\\.";
    private final static String CHAR_DOT = ".";
    private classDefinitionType itemType = null;
    private propertyValueType propertyValueType = ccm.server.enums.propertyValueType.StringType;
    private Object value;

    public String getSchemaDefinition() {
        if (!StringUtils.isEmpty(this.relOrEdgeDef))
            return this.relOrEdgeDef + "." + this.propertyDef;
        return this.propertyDef;
    }

    private void checkItemType(String itemType) {
        if (itemType.equalsIgnoreCase(classDefinitionType.PropertyDef.toString()))
            this.itemType = classDefinitionType.PropertyDef;
        else if (itemType.equalsIgnoreCase(classDefinitionType.InterfaceDef.toString()))
            this.itemType = classDefinitionType.InterfaceDef;
        else if (itemType.equalsIgnoreCase(classDefinitionType.EdgeDef.toString()))
            this.itemType = classDefinitionType.EdgeDef;
        else if (itemType.equalsIgnoreCase(classDefinitionType.Rel.toString())) {
            this.itemType = classDefinitionType.Rel;
            this.relDirection = CommonUtility.toRelDirection(itemType);
        } else {
            this.itemType = classDefinitionType.RelDef;
            this.relDirection = CommonUtility.toRelDirection(itemType);
        }
    }

    public void doInit(String formPurpose) throws Exception {
        if (!StringUtils.isEmpty(formPurpose)) {
            if (!(ccm.server.enums.formPurpose.Info.toString().equalsIgnoreCase(formPurpose) || ccm.server.enums.formPurpose.List.toString().equalsIgnoreCase(formPurpose))) {
                if (!StringUtils.isEmpty(this.relOrEdgeDef)) {
                    IObject schemaObj = CIMContext.Instance.ProcessCache().item(this.relOrEdgeDef, domainInfo.SCHEMA.toString(), false);
                    if (schemaObj != null) {
                        if (schemaObj.IsTypeOf(IRelDef.class.getSimpleName()))
                            this.propertyValueType = schemaObj.toInterface(IRelDef.class).checkPropertyValueType(this.relDirection);
                        else if (schemaObj.IsTypeOf(IEdgeDef.class.getSimpleName()))
                            this.propertyValueType = ccm.server.enums.propertyValueType.SearchRelationship;
                    }
                } else {
                    if (!StringUtils.isEmpty(this.propertyDef)) {
                        IPropertyDef propertyDef = CIMContext.Instance.ProcessCache().item(this.propertyDef, domainInfo.SCHEMA.toString(), false).toInterface(IPropertyDef.class);
                        if (propertyDef != null) {
                            this.propertyValueType = propertyDef.checkPropertyValueType();
                        }
                    }
                }
            } else {
                if (!StringUtils.isEmpty(this.propertyDef)) {
                    IPropertyDef propertyDef = CIMContext.Instance.ProcessCache().item(this.propertyDef, domainInfo.SCHEMA.toString(), false).toInterface(IPropertyDef.class);
                    if (propertyDef != null) {
                        this.propertyValueType = propertyDef.checkPropertyValueType();
                    }
                }
            }
        }
    }

    protected boolean relationshipItemOrNot() {
        switch (this.itemType) {
            case Rel:
            case RelDef:
            case EdgeDef:
            case ViewDef:
                return true;
        }
        return false;
    }

    public displayItemWrapper(String itemType, String schemeDefinition) throws Exception {
        if (!StringUtils.isEmpty(itemType))
            this.checkItemType(itemType);

        if (this.itemType == null)
            throw new Exception("invalid item type for display item configuration, progress will be terminated");

        if (StringUtils.isEmpty(schemeDefinition))
            throw new Exception("invalid schema definition as it is NULL for display item configuration, progress will be terminated");
        else {
            if (this.relDirection == ccm.server.enums.relDirection._unknown || this.relDirection == null)
                this.relDirection = CommonUtility.toRelDirection(schemeDefinition);

            String actualSchemaDefinition = CommonUtility.toActualDefinition(schemeDefinition);
            if (actualSchemaDefinition.contains(CHAR_DOT) && this.relationshipItemOrNot()) {
                if (this.relDirection == ccm.server.enums.relDirection._unknown || this.relDirection == null)
                    this.relDirection = ccm.server.enums.relDirection._1To2;
                if (this.itemType == classDefinitionType.EdgeDef)
                    this.relDirection = ccm.server.enums.relDirection._1To2;

                String[] strings = actualSchemaDefinition.split(REGEX_CHAR_DOT);
                this.relOrEdgeDef = strings[0];
                if (strings.length > 1)
                    this.propertyDef = strings[1];

                if (CIMContext.Instance.ProcessCache().item(this.relOrEdgeDef, domainInfo.SCHEMA.toString(), false) == null)
                    throw new Exception("invalid path definition " + this.relOrEdgeDef);
            } else {
                this.propertyDef = actualSchemaDefinition;
            }

            if (CIMContext.Instance.ProcessCache().item(this.propertyDef, domainInfo.SCHEMA.toString(), false) == null)
                throw new Exception("invalid property definition " + this.propertyDef);
        }
    }

    @Override
    public String toString() {
        return this.relOrEdgeDef + "," + this.propertyDef + "," + this.relDirection.toString();
    }
}
