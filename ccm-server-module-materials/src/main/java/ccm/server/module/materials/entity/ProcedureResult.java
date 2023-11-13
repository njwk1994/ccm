package ccm.server.module.materials.entity;

import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2021/10/18 17:44
 */
public class ProcedureResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectName;

    private Integer requestId = 0;
    private Integer result;
    private String requestIdStr;
    private String requestDate;
    private String message = "";
    private int exist = 0;
    private JSONArray requestDataCursor;

    public ProcedureResult() {
    }

    public boolean isExist() {
        return this.exist > 0;
    }

    public String getObjectName() {
        return objectName;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getRequestIdStr() {
        return requestIdStr;
    }

    public void setRequestIdStr(String requestIdStr) {
        this.requestIdStr = requestIdStr;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public JSONArray getRequestDataCursor() {
        return requestDataCursor;
    }

    public void setRequestDataCursor(JSONArray requestDataCursor) {
        this.requestDataCursor = requestDataCursor;
    }
}
