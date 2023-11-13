package ccm.server.schema.interfaces.generated;

import ccm.server.entity.MetaDataObj;
import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.objectUpdateState;
import ccm.server.enums.propertyValueUpdateState;
import ccm.server.schema.model.IProperty;
import ccm.server.schema.model.IPropertyValue;
import ccm.server.schema.model.InterfaceDefault;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Map;

public abstract class IObjectBase extends InterfaceDefault {
    public IObjectBase(boolean instantiateRequiredProperties) {
        super("IObject", instantiateRequiredProperties);
    }

    @Override
    public void selfCheck() throws Exception {
        if (StringUtils.isEmpty(this.OBID()))
            throw new Exception("OBID shall not by NULL");
        if (StringUtils.isEmpty(this.UID()))
            throw new Exception("UID shall not by NULL");
        if (StringUtils.isEmpty(this.ClassDefinitionUID()))
            throw new Exception("Class Definition UID shall not be NULL");
        if (StringUtils.isEmpty(this.DomainUID()))
            throw new Exception("Domain UID shall not be NULL");
    }

    @Override
    public MetaDataObj toMetaDataObject() throws Exception {
        MetaDataObj result = new MetaDataObj();
        result.setObid(this.OBID());
        result.setObjUid(this.UID());
        result.setDomainUid(this.DomainUID());
        result.setConfig(this.getConfigForMetaData());
        result.setName(this.Name());
        result.setDescription(this.Description());
        result.setUniqueKey(this.UniqueKey());
        result.setClassDefinitionUid(this.ClassDefinitionUID());
        result.setTerminationDate(this.TerminationDate());
        result.setTerminationUser(this.TerminationUser());
        result.setLastUpdateDate(this.LastUpdateDate());
        result.setLastUpdateUser(this.LastUpdateUser());
        result.setCreationDate(this.CreationDate());
        result.setCreationUser(this.CreationUser());
        result.setUpdateState(this.ObjectUpdateState());
        if (result.getUpdateState() != objectUpdateState.created && result.getUpdateState() != objectUpdateState.deleted && result.getUpdateState() != objectUpdateState.terminated) {
            Iterator<Map.Entry<String, IProperty>> iterator = this.Interfaces().item(interfaceDefinitionType.IObject.toString()).Properties().GetEnumerator();
            while (iterator.hasNext()) {
                IProperty property = iterator.next().getValue();
                IPropertyValue iPropertyValue = property.CurrentValue();
                if (iPropertyValue != null) {
                    if (property.CurrentValue().UpdateState() != propertyValueUpdateState.none) {
                        result.setUpdateState(objectUpdateState.updated);
                        break;
                    }
                }
            }
        }
        return result;
    }
}
