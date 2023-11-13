package ccm.server.schema.interfaces;

import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.util.Date;

public interface ICIMDocumentVersion extends IObject {

    void setCIMVersionCheckInDate(Date date) throws Exception;

    Date getCIMVersionCheckInDate() throws ParseException;

    void setCIMVersionCheckInUser(String user) throws Exception;

    String getCIMVersionCheckInUser();

    void setCIMDocVersion(Integer version) throws Exception;

    Integer getCIMDocVersion();

    void setCIMIsDocVersionCheckedOut(Boolean isDocVersionCheckedOut) throws Exception;

    Boolean getCIMIsDocVersionCheckedOut();

    void setCIMIsDocVersionSuperseded(Boolean isDocVersionSuperseded) throws Exception;

    Boolean getCIMIsDocVersionSuperseded();

    void setCIMVersionSupersededDate(Date date) throws Exception;

    Date getCIMVersionSupersededDate() throws ParseException;

    ICIMDocumentRevision getDocumentRevision() throws Exception;

    void setVersionPropertyInfo(JSONObject versionPropertyInfo) throws Exception;

}
