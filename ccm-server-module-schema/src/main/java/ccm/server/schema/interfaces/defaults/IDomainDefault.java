package ccm.server.schema.interfaces.defaults;

import ccm.server.args.createArgs;
import ccm.server.schema.interfaces.generated.IDomainBase;
import org.springframework.util.StringUtils;

public class IDomainDefault extends IDomainBase {
    public IDomainDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    @Override
    public void OnCreate(createArgs e) throws Exception {
        String tablePrefix = this.TablePrefix();
        if (StringUtils.isEmpty(tablePrefix) || tablePrefix.length() > 5)
            throw new Exception("invalid table prefix as it is empty or length is more than 5");
        super.OnCreate(e);
    }
}
