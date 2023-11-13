package ccm.server.schema.interfaces;

import ccm.server.dto.base.ObjectDTO;
import ccm.server.schema.collections.IObjectCollection;

import java.util.List;

public interface ICIMForm extends IObject {

    IObjectCollection getSections() throws Exception;

    List<ICIMSection> getOrderedSections() throws Exception;

    IInterfaceDef getEffectInterfaceDef() throws Exception;

    String FormPurpose();

    void setFormPurpose(String value) throws Exception;

    IObjectCollection getEffectClassDefs() throws Exception;

    ObjectDTO generatePopup(String formPurpose) throws Exception;

    ObjectDTO generatePopup(String formPurpose, IObject o) throws Exception;

    void setInfoWithProvidedObject(ObjectDTO objectDTO, IObject o);
}
