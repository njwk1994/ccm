package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;

import java.util.List;

public interface ICIMSection extends IObject {

    ICIMForm getForm() throws Exception;

    List<ICIMDisplayItem> getOrderedDisplayItems() throws Exception;

    String LabelName();

    void setLabelName(String value) throws Exception;

    IObjectCollection getDisplayItems() throws Exception;
    
}
