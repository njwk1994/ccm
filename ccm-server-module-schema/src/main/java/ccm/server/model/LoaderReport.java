package ccm.server.model;


import ccm.server.context.CIMContext;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRel;
import ccm.server.util.CommonUtility;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class LoaderReport {
    private final List<IObject> objects = new ArrayList<>();
    private final List<IObject> relationships = new ArrayList<>();
    private final List<IObject> instructions = new ArrayList<>();

    public LoaderReport() {

    }

    public void addObject(IObject object) {
        if (object != null) {
            this.objects.add(object);
        }
    }

    public void addObjects(IObjectCollection objectCollection) {
        if (objectCollection.hasValue()) {
            Iterator<IObject> iterator = objectCollection.GetEnumerator();
            while (iterator.hasNext()) {
                IObject object = iterator.next();
                if (object instanceof IRel) {
                    this.addRelationships(object.toInterface(IRel.class));
                } else {
                    this.addObject(object);
                }
            }
        }
    }

    public void commit() throws Exception {
        if (CIMContext.Instance.Transaction().inTransaction()) {
            CIMContext.Instance.Transaction().commit();
        } else
            throw new Exception("invalid transaction as it was not started yet");
    }

    public IObject getObjectByName(String name) {
        if (!StringUtils.isEmpty(name)) {
            if (this.objects.size() > 0) {
                return this.objects.stream().filter(c -> c.Name().equalsIgnoreCase(name)).findFirst().orElse(null);
            }
        }
        return null;
    }

    public List<IObject> getObjectsByInterface(@NotNull String pstrInterfaceDef) {
        if (!StringUtils.isEmpty(pstrInterfaceDef) && CommonUtility.hasValue((this.objects))) {
            return this.objects.stream().filter(r -> r.Interfaces().hasInterface(pstrInterfaceDef)).collect(Collectors.toList());
        }
        return null;
    }

    public IObject getObjectByUID(String uid) {
        IObject result = null;
        if (!StringUtils.isEmpty(uid)) {
            if (this.objects.size() > 0) {
                result = this.objects.stream().filter(c -> c.UID().equalsIgnoreCase(uid)).findFirst().orElse(null);
            }
            if (result == null)
                result = CIMContext.Instance.ProcessCache().item(uid, null);
        }
        return result;
    }

    public void addRelationships(IRel rel) {
        if (rel != null) {
            this.relationships.add(rel);
        }
    }
}
