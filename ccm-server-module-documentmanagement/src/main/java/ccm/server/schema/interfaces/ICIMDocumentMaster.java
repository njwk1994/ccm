package ccm.server.schema.interfaces;


import ccm.server.dto.base.ObjectDTO;
import ccm.server.schema.collections.IObjectCollection;
import com.alibaba.fastjson.JSONObject;

public interface ICIMDocumentMaster extends IObject {

    void setCIMDocCategory(String docCategory) throws Exception;

    String getCIMDocCategory();

    void setCIMDocState(String docState) throws Exception;

    String getCIMDocState();

    void setCIMDocType(String docType) throws Exception;

    String getCIMDocType();

    void setCIMDocSubType(String docSubType) throws Exception;

    String getCIMDocSubType();

    void setCIMDocTitle(String docTitle) throws Exception;

    String getCIMDocTitle();

    ICIMObjClass getPrimaryClassification() throws Exception;

    IObjectCollection getDocumentRevisions() throws Exception;

    IObjectCollection getLatestRevisions() throws Exception;

    ICIMDocumentRevision getNewestRevision() throws Exception;

    void setMasterPropertiesByJsonInfo(JSONObject docMasterInfo) throws Exception;

    boolean  isRevised() throws Exception;

    void clearRevisedStatus() throws Exception;

    IObjectCollection getDesignObjects() throws Exception;

    void fillPropForObjectDTO(ObjectDTO objectDTO) throws Exception;

}
