package ccm.server.schema.interfaces.defaults;

import ccm.server.context.CIMContext;
import ccm.server.schema.interfaces.generated.IInterfaceDefBase;
import ccm.server.schema.model.IInterface;
import ccm.server.schema.model.InterfaceDefault;

public class IInterfaceDefDefault extends IInterfaceDefBase {
    public IInterfaceDefDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }
    @Override
    public Object Instantiate(boolean instantiateRequiredItems) throws Exception {
        return this.Instantiate(this.UID(), this.UID(), instantiateRequiredItems);
    }

    @Override
    public Object Instantiate(String pstrOBID, String pstrUID, boolean instantiateRequiredItems) throws Exception {
        IInterface anInterface = (IInterface) CIMContext.Instance.getSchemaActivator().newInstance(
            "ccm.server.schema.interfaces.defaults." + pstrUID + "Default",
            "ccm.server.schema.model.InterfaceDefault", new Object[]{instantiateRequiredItems});
        if (anInterface instanceof InterfaceDefault)
            ((InterfaceDefault) anInterface).setInterfaceDefinitionUID(pstrUID);
        return anInterface;
    }
}
