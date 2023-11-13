package ccm.server.schema.interfaces.defaults;

import ccm.server.args.createArgs;
import ccm.server.context.CIMContext;
import ccm.server.enums.domainInfo;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.generated.IROPWorkStepBase;

public class IROPWorkStepDefault extends IROPWorkStepBase {
    public IROPWorkStepDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    public void OnCreate(createArgs e) throws Exception {
        super.OnCreate(e);
        String workStepName = this.ROPWorkStepName();
        IObject item = CIMContext.Instance.ProcessCache().item(workStepName, domainInfo.SCHEMA.toString());
        if (item != null)
            workStepName = item.Name();
        this.setName(workStepName);
    }
}
