package ccm.server.engine.impl;

import ccm.server.engine.IGraphExpansionEngine;
import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import ccm.server.schema.collections.impl.ObjectCollection;
import ccm.server.schema.interfaces.IInterfaceDef;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.interfaces.IRelDef;
import ccm.server.schema.model.IInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service("graphExpansionEngine")
public class GraphExpansionEngine implements IGraphExpansionEngine {
    @Override
    public void getRelatedInfoForObject(IObjectCollection items, IObjectCollection container) throws Exception {
        if (items != null && items.hasValue() && container != null) {
            Iterator<IObject> objectIterator = items.GetEnumerator();
            while (objectIterator.hasNext()) {
                IObject object = objectIterator.next();
                if (!container.contains(object)) {
                    Iterator<Map.Entry<String, IInterface>> entryIterator = object.Interfaces().GetEnumerator();
                    while (entryIterator.hasNext()) {
                        IInterface anInterface = entryIterator.next().getValue();
                        IInterfaceDef interfaceDefinition = anInterface.getInterfaceDefinition();
                        if (interfaceDefinition == null)
                            throw new Exception("invalid interface definition for " + anInterface.InterfaceDefinitionUID());
                        IObjectCollection end1RelDefs = interfaceDefinition.getEnd1RelDefs();
                        if (end1RelDefs != null && end1RelDefs.hasValue()) {
                            Iterator<IObject> iObjectIterator = end1RelDefs.GetEnumerator();
                            while (iObjectIterator.hasNext()) {
                                IRelDef relDef = iObjectIterator.next().toInterface(IRelDef.class);
                                IRelCollection relCollection = object.GetEnd1Relationships().GetRels(relDef.Name());
                                if (relCollection != null && relCollection.hasValue()) {
                                    container.addRangeUniquely(relCollection);
                                    if (relDef.Delete12()) {
                                        IObjectCollection objectCollection = relCollection.GetEnd2s();
                                        container.addRangeUniquely(objectCollection);
                                    }
                                }
                            }
                        }
                        IObjectCollection end2RelDefs = interfaceDefinition.getEnd2RelDefs();
                        if (end2RelDefs != null && end2RelDefs.hasValue()) {
                            Iterator<IObject> iObjectIterator = end2RelDefs.GetEnumerator();
                            while (iObjectIterator.hasNext()) {
                                IRelDef relDef = iObjectIterator.next().toInterface(IRelDef.class);
                                IRelCollection relCollection = object.GetEnd2Relationships().GetRels(relDef.Name());
                                if (relCollection != null && relCollection.hasValue()) {
                                    container.addRangeUniquely(relCollection);
                                    if (relDef.Delete21()) {
                                        IObjectCollection objectCollection = relCollection.GetEnd1s();
                                        container.addRangeUniquely(objectCollection);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public IObjectCollection getRelatedInfoForObject(IObject object) throws Exception {
        IObjectCollection result = new ObjectCollection();
        if (object != null) {
            IObjectCollection startObjs = new ObjectCollection();
            startObjs.append(object);
            this.getRelatedInfoForObject(startObjs, result);
        }
        return result;
    }
}

