package ccm.server.schema.interfaces.defaults;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.IInterfaceDef;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.generated.IClassDefBase;

import java.util.Iterator;

public class IClassDefDefault extends IClassDefBase {
    public IClassDefDefault(boolean instantiateRequiredProperties) {
        super(instantiateRequiredProperties);
    }

    @Override
    public IObjectCollection getAllPropertyDefs() throws Exception {
        IObjectCollection interfaceDefs = this.getRealizedInterfaceDefs();
        if (interfaceDefs != null && interfaceDefs.size() > 0) {
            IObjectCollection objectCollection = new ObjectCollection();
            Iterator<IObject> iterator = interfaceDefs.GetEnumerator();
            while (iterator.hasNext()) {
                IObject iObject = iterator.next();
                IInterfaceDef interfaceDef = iObject.toInterface(IInterfaceDef.class);
                if (interfaceDef != null) {
                    IObjectCollection propertyDefinitions = interfaceDef.getExposesPropertyDefinition();
                    if (propertyDefinitions != null && propertyDefinitions.size() > 0) {
                        objectCollection.addRangeUniquely(propertyDefinitions);
                    }
                }
            }
            return objectCollection;
        }
        return null;
    }
}
