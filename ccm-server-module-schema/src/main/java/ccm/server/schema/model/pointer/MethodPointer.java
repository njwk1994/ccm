package ccm.server.schema.model.pointer;

import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.IInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Data
@Slf4j
public class MethodPointer {
    private IObject mobjIObject;
    private String method;
    private String interfaceDef;
    private Object[] args;
    private boolean useToInterface;

    public IObject Object() {
        return this.mobjIObject;
    }

    public MethodPointer(IObject mobjIObject, String interfaceDef, String method, Object[] args) {
        this.mobjIObject = mobjIObject;
        this.method = method;
        this.interfaceDef = interfaceDef;
        this.args = args;
    }

    public Object invoke() throws Exception {
        boolean lblnEmptyArgs = false;
        Class<?>[] types = null;
        if (this.args != null && this.args.length > 0) {
            types = new Class<?>[this.args.length];
            int maxIndex = this.args.length - 1;
            for (int i = 0; i <= maxIndex; i++) {
                types[i] = this.args[i].getClass();
            }
        } else
            lblnEmptyArgs = true;
        IInterface lobjInterface = null;
        if (this.useToInterface)
            lobjInterface = this.mobjIObject.myNext(this.interfaceDef, new ArrayList<>());
        else
            lobjInterface = (IInterface) this.mobjIObject;

        Method method = null;
        if (lblnEmptyArgs)
            method = lobjInterface.getClass().getMethod(this.method);
        else
            method = lobjInterface.getClass().getMethod(this.method, types);
        log.trace("start invoke " + this.method + " on " + this.interfaceDef + " for " + this.mobjIObject.toErrorPop());
        return method.invoke(lobjInterface, this.args);
    }
}
