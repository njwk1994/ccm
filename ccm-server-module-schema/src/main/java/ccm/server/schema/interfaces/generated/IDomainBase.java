package ccm.server.schema.interfaces.generated;

import ccm.server.args.suppressibleArgs;
import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.enums.relDefinitionType;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.interfaces.IDomain;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.ValueConversionUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class IDomainBase extends InterfaceDefault implements IDomain {

    public IDomainBase(boolean instantiateRequiredProperties) {
        super("IDomain", instantiateRequiredProperties);
    }

    @Override
    public String TablePrefix() {
        IProperty property = this.getProperty("ITableInfo", "TablePrefix");
        String tablePrefix = ValueConversionUtility.toString(property);
        if (StringUtils.isEmpty(tablePrefix))
            tablePrefix = this.Name();
        return tablePrefix;
    }

    protected List<String> domainsNotScopeWise() {
        return new ArrayList<String>() {{
            this.add(domainInfo.ADMIN.toString());
            this.add(domainInfo.SCHEMA.toString());
            this.add(domainInfo.DATA.toString());
        }};
    }

    @Override
    public boolean ScopeWiseInd() {
        List<String> hardCodeDomains = this.domainsNotScopeWise();
        if (hardCodeDomains.stream().anyMatch(c -> c.equalsIgnoreCase(this.Name())))
            return false;
        IProperty property = this.getProperty("ITableInfo", "ScopeWiseInd");
        return ValueConversionUtility.toBoolean(property);
    }

    @Override
    public void setScopeWiseInd(boolean value) throws Exception {
        this.Interfaces().item("ITableInfo", true).Properties().item("ScopeWiseInd", true).setValue(value);
    }

    @Override
    public void setTablePrefix(String value) throws Exception {
        this.Interfaces().item("ITableInfo", true).Properties().item("TablePrefix", true).setValue(value);
    }

    @Override
    public IObjectCollection getIncludeClassDefs() throws Exception {
        IRelCollection relCollection = this.GetEnd1Relationships().GetRels(relDefinitionType.classDefDomainInfo.toString());
        if (relCollection != null && relCollection.hasValue())
            return relCollection.GetEnd2s();
        return null;
    }

    @Override
    public void OnCreated(suppressibleArgs e) throws Exception {
        super.OnCreated(e);
        CIMContext.Instance.ensureTables(this);
    }

    @Override
    public void OnUpdated(suppressibleArgs e) throws Exception {
        super.OnUpdated(e);
        CIMContext.Instance.ensureTables(this);
    }
}
