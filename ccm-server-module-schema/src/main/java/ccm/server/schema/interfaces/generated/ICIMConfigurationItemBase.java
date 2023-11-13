package ccm.server.schema.interfaces.generated;

import ccm.server.args.suppressibleArgs;
import ccm.server.context.CIMContext;
import ccm.server.schema.interfaces.ICIMConfigurationItem;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ICIMConfigurationItemBase extends InterfaceDefault implements ICIMConfigurationItem {
    public ICIMConfigurationItemBase(boolean instantiateRequiredProperties) {
        super("ICIMConfigurationItem", instantiateRequiredProperties);
    }

    @Override
    public String generateIObjectConfig() {
        List<String> items = new ArrayList<>();
        items.add(this.UID());
        items.add(this.TablePrefix());
        return items.stream().filter(c -> !StringUtils.isEmpty(c)).distinct().collect(Collectors.joining("<<>>"));
    }

    @Override
    public List<String> getPropertiesThatCannotBeUpdated() {
        List<String> propertyDefsThatCannotBeUpdated = super.getPropertiesThatCannotBeUpdated();
        propertyDefsThatCannotBeUpdated.add("TablePrefix");
        return propertyDefsThatCannotBeUpdated;
    }

    public ICIMConfigurationItemBase(String interfaceDefinitionUid, boolean instantiateRequiredProperties) {
        super(interfaceDefinitionUid, instantiateRequiredProperties);
    }

    @Override
    public String TablePrefix() {
        IProperty property = this.getProperty("ITableInfo", "TablePrefix");
        return ValueConversionUtility.toString(property);
    }

    @Override
    public void setTablePrefix(String value) throws Exception {
        this.Interfaces().item("ITableInfo", true).Properties().item("TablePrefix", true).setValue(value);
    }

    @Override
    public void OnCreated(suppressibleArgs e) throws Exception {
        CIMContext.Instance.ensureTables(this);
        super.OnCreated(e);
    }

    @Override
    public void OnDeleted(suppressibleArgs e) throws Exception {
        CIMContext.Instance.dropTables(this);
        super.OnDeleted(e);
    }
}
