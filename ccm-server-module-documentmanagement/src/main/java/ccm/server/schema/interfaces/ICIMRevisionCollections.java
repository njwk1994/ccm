package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;
import ccm.server.schema.collections.IRelCollection;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ICIMRevisionCollections extends IObject {

    void setCIMPipeUIDs(String... uids) throws Exception;

    String[] getCIMPipeUIDs();

    void setCIMPipeComponentUIDs(String... uids) throws Exception;

    String[] getCIMPipeComponentUIDs();

    void setCIMSupportUIDs(String... uids) throws Exception;

    String[] getCIMSupportUIDs();

    void setCIMWeldUIDs(String... uids) throws Exception;

    String[] getCIMWeldUIDs();

    void setCIMSpoolUIDs(String... uids) throws Exception;

    String[] getCIMSpoolUIDs();

    String[] getCCMPipeLineUIDs();

    void setCCMPipeLineUIDs(String... uids) throws Exception;

    String[] getCCMBoltUIDs();

    void setCCMBoltUIDs(String... uids) throws Exception;

    String[] getCCMGasketUIDs();

    void setCCMGasketUIDs(String... uids) throws Exception;

    String[] getCCMEquipUIDs();

    void setCCMEquipUIDs(String... uids) throws Exception;

    String[] getCCMSubEquipUIDs();

    void setCCMSubEquipUIDs(String... uids) throws Exception;

    String[] getCCMCableTrayUIDs();

    void setCCMCableTrayUIDs(String... uids) throws Exception;

    String[] getCCMCableTrayComponentUIDs();

    void setCCMCableTrayComponentUIDs(String... uids) throws Exception;

    String[] getCCMCableUIDs();

    void setCCMCableUIDs(String... uids) throws Exception;

    String[] getCCMInstrumentUIDs();

    void setCCMInstrumentUIDs(String... uids) throws Exception;

    String[] getCCMDuctLineUIDs();

    void setCCMDuctLineUIDs(String... uids) throws Exception;

    String[] getCCMDuctComponentUIDs();

    void setCCMDuctComponentUIDs(String... uids) throws Exception;

    String[] getCCMJunctionBoxUIDs();

    void setCCMJunctionBoxUIDs(String... uids) throws Exception;

    String[] getCCMSTPartUIDs();

    void setCCMSTPartUIDs(String... uids) throws Exception;

    String[] getCCMSTComponentUIDs();

    void setCCMSTComponentUIDs(String... uids) throws Exception;

    String[] getCCMSTBlockUIDs();

    void setCCMSTBlockUIDs(String... uids) throws Exception;

    IObjectCollection updateHasDeletedDesignObjStatusByNewDesignObjUIDs(List<JSONObject> designObjs, IRelCollection docMaterRelatedDesignObjs, boolean systemUpgradeDeleteData) throws Exception;

    void setDesignObjUIDs(List<JSONObject> docsDesignObjs) throws Exception;
}
