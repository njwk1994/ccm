package ccm.server.schema.interfaces.generated;


import ccm.server.context.CIMContext;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.ISchemaObject;
import ccm.server.schema.model.ClassBase;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;
import org.springframework.util.StringUtils;

public abstract class ISchemaObjectBase extends InterfaceDefault implements ISchemaObject {
    public ISchemaObjectBase(boolean instantiateRequiredProperties) {
        super("ISchemaObject", instantiateRequiredProperties);
    }

    public ISchemaObjectBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    @Override
    public boolean Cached() {
        IProperty property = this.getProperty("ISchemaObject", "Cached");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setCached(boolean value) throws Exception {
        this.setPropertyValue("ISchemaObject", "Cached", value);
    }

    @Override
    public String CachedLevel() {
        IProperty property = this.getProperty("ISchemaObject", "CachedLevel");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setCachedLevel(String value) throws Exception {
        this.setPropertyValue("ISchemaObject", "CachedLevel", value);
    }

    @Override
    public boolean ByCustom() throws Exception {
        IProperty property = this.getProperty("ISchemaObject", "ByCustom");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setByCustom(boolean byCustom) throws Exception {
        this.setPropertyValue("ISchemaObject", "ByCustom", byCustom);
    }

    @Override
    public String DisplayName() {
        IProperty property = this.getProperty("ISchemaObject", "DisplayName");
        String result = ValueConversionUtility.toString(property);
        if (StringUtils.isEmpty(result))
            result = this.generateDisplayAs();
        return result;
    }

    @Override
    public void setDisplayName(String displayName) throws Exception {
        this.setPropertyValue("ISchemaObject", "DisplayName", displayName);
    }

    @Override
    public int SchemaRev() {
        IProperty property = this.getProperty("ISchemaObject", "SchemaRev");
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setSchemaRev(int schemaRev) throws Exception {
        this.setPropertyValue("ISchemaObject", "SchemaRev", schemaRev);
    }

    @Override
    public int SchemaVer() {
        IProperty property = this.getProperty("ISchemaObject", "SchemaVer");
        return ValueConversionUtility.toInteger(property);
    }

    @Override
    public void setSchemaVer(int schemaVer) throws Exception {
        this.setPropertyValue("ISchemaObject", "SchemaVer", schemaVer);
    }

    @Override
    public String SchemaRevVer() {
        return String.valueOf(this.SchemaRev()) + String.valueOf(this.SchemaVer());
    }

    @Override
    public Object Instantiate(boolean instantiateRequiredItems) throws Exception {
        return this.Instantiate(null, null, instantiateRequiredItems);
    }

    @Override
    public Object Instantiate(String pstrOBID, String pstrUID, boolean instantiateRequiredItems) throws Exception {
        Object result = null;
        IObject iObject = ((ClassBase) CIMContext.Instance.getSchemaActivator().newInstance(
            "ccm.server.schema.classes." + this.UID() + "." + pstrUID,
            "ccm.server.schema.classes." + this.UID() + ".Default",
            "ccm.server.schema.model.ClassDefault", new Object[]{instantiateRequiredItems})).IObject();
        if (!StringUtils.isEmpty(pstrOBID)) iObject.setOBID(pstrOBID);
        if (!StringUtils.isEmpty(pstrUID)) iObject.setUID(pstrUID);
        iObject.setClassDefinitionUID(this.UID());
        result = iObject;
        return result;
    }
}
