package ccm.server.schema.interfaces;

import ccm.server.schema.collections.IObjectCollection;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.util.Date;

public interface ICIMDocumentRevision extends IObject {

    void setCIMRevisionScheme(String revisionScheme) throws Exception;

    String getCIMRevisionScheme();

    void setCIMExternalRevision(String externalRevision) throws Exception;

    String getCIMExternalRevision();

    void setCIMMajorRevision(String majorRevision) throws Exception;

    String getCIMMajorRevision();

    void setCIMMinorRevision(String minorRevision) throws Exception;

    String getCIMMinorRevision();

    void setCIMRevState(String revState) throws Exception;

    String getCIMRevState();

    void setCIMSignOffComments(String signOffComments) throws Exception;

    String getCIMSignOffComments();

    void setCIMRevIssueDate(Date date) throws Exception;

    Date getCIMRevIssueDate() throws ParseException;

    IObjectCollection getDocumentVersions() throws Exception;

    ICIMDocumentVersion getNewestDocumentVersion() throws Exception;

    ICIMDocumentMaster getDocumentMaster() throws Exception;

    ICIMDocumentRevision getPreviousRevision() throws Exception;

    IObjectCollection getLatestVersions() throws Exception;

    ICIMRevisionScheme getRevisionScheme() throws Exception;

    void signOff() throws Exception;

    void signOff(String comments) throws Exception;

    void undoSignOff() throws Exception;

    void revise() throws Exception;

    void setRevisionPropertyInfo(JSONObject revisionPropertyInfo) throws Exception;

    void revise(boolean pblnMajorRevise) throws Exception;

    void superseded(boolean needTransaction) throws Exception;
}
