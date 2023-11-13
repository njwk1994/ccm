package ccm.server.schema.interfaces.generated;


import ccm.server.enums.interfaceDefinitionType;
import ccm.server.enums.propertyDefinitionType;
import ccm.server.enums.relDefinitionType;
import ccm.server.schema.interfaces.ICIMDocumentRevision;
import ccm.server.schema.interfaces.ICIMDocumentVersion;
import ccm.server.util.CommonUtility;
import ccm.server.schema.interfaces.IObject;
import ccm.server.schema.model.InterfaceDefault;
import ccm.server.utils.SchemaUtility;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public abstract class ICIMDocumentVersionBase extends InterfaceDefault implements ICIMDocumentVersion {
    public ICIMDocumentVersionBase(boolean instantiateRequiredProperties) {
        super(interfaceDefinitionType.ICIMDocumentVersion.toString(), instantiateRequiredProperties);
    }

    @Override
    public void setCIMVersionCheckInDate(Date date) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMVersionCheckInDate.toString(), CommonUtility.formatDateWithDateFormat(date));
    }

    @Override
    public Date getCIMVersionCheckInDate() throws ParseException {
        return CommonUtility.parseStrToDate(this.getProperty(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMVersionCheckInDate.toString()).Value().toString());
    }

    @Override
    public void setCIMVersionCheckInUser(String user) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMVersionCheckInUser.toString(), user);
    }

    @Override
    public String getCIMVersionCheckInUser() {
        return this.getProperty(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMVersionCheckInUser.toString()).Value().toString();
    }

    @Override
    public void setCIMDocVersion(Integer version) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMDocVersion.toString(), version);
    }

    @Override
    public Integer getCIMDocVersion() {
        return Integer.parseInt(this.getProperty(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMDocVersion.toString()).Value().toString());
    }

    @Override
    public void setCIMIsDocVersionCheckedOut(Boolean isDocVersionCheckedOut) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMIsDocVersionCheckedOut.toString(), isDocVersionCheckedOut);
    }

    @Override
    public Boolean getCIMIsDocVersionCheckedOut() {
        return Boolean.parseBoolean(this.getProperty(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMIsDocVersionCheckedOut.toString()).Value().toString());
    }

    @Override
    public void setCIMIsDocVersionSuperseded(Boolean isDocVersionSuperseded) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMIsDocVersionSuperseded.toString(), isDocVersionSuperseded);
    }

    @Override
    public Boolean getCIMIsDocVersionSuperseded() {
        return Boolean.parseBoolean(this.getProperty(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMIsDocVersionSuperseded.toString()).Value().toString());
    }

    @Override
    public void setCIMVersionSupersededDate(Date date) throws Exception {
        this.setPropertyValue(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMVersionSupersededDate.toString(), CommonUtility.formatDateWithDateFormat(date));
    }

    @Override
    public Date getCIMVersionSupersededDate() throws ParseException {
        return CommonUtility.parseStrToDate(this.getProperty(interfaceDefinitionType.ICIMDocumentVersion.toString(), propertyDefinitionType.CIMVersionSupersededDate.toString()).Value().toString());
    }

    @Override
    public ICIMDocumentRevision getDocumentRevision() throws Exception {
        IObject object = this.GetEnd2Relationships().GetRel(relDefinitionType.CIMDocumentRevisionVersions.toString(), false).GetEnd1();
        return object != null ? object.toInterface(ICIMDocumentRevision.class) : null;
    }

    @Override
    public void setVersionPropertyInfo(JSONObject versionPropertyInfo) throws Exception {
        if (versionPropertyInfo != null) {
            for (Map.Entry<String, Object> item : versionPropertyInfo.entrySet()) {
                SchemaUtility.setObjPropertyValue(this, interfaceDefinitionType.ICIMDocumentVersion.toString(), item.getKey(), item.getValue(), false, false);
            }
        }
    }
}
