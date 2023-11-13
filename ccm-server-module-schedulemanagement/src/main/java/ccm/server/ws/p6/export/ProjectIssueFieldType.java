
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ProjectIssueFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ProjectIssueFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityId"/&gt;
 *     &lt;enumeration value="ActivityName"/&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="ActualValue"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DateIdentified"/&gt;
 *     &lt;enumeration value="IdentifiedBy"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="LowerThreshold"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="Notes"/&gt;
 *     &lt;enumeration value="OBSName"/&gt;
 *     &lt;enumeration value="OBSObjectId"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="Priority"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectName"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ProjectThresholdObjectId"/&gt;
 *     &lt;enumeration value="RawTextNote"/&gt;
 *     &lt;enumeration value="ResolutionDate"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *     &lt;enumeration value="ThresholdParameterObjectId"/&gt;
 *     &lt;enumeration value="UpperThreshold"/&gt;
 *     &lt;enumeration value="WBSCode"/&gt;
 *     &lt;enumeration value="WBSName"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ProjectIssueFieldType")
@XmlEnum
public enum ProjectIssueFieldType {

    @XmlEnumValue("ActivityId")
    ACTIVITY_ID("ActivityId"),
    @XmlEnumValue("ActivityName")
    ACTIVITY_NAME("ActivityName"),
    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("ActualValue")
    ACTUAL_VALUE("ActualValue"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("DateIdentified")
    DATE_IDENTIFIED("DateIdentified"),
    @XmlEnumValue("IdentifiedBy")
    IDENTIFIED_BY("IdentifiedBy"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("LowerThreshold")
    LOWER_THRESHOLD("LowerThreshold"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("Notes")
    NOTES("Notes"),
    @XmlEnumValue("OBSName")
    OBS_NAME("OBSName"),
    @XmlEnumValue("OBSObjectId")
    OBS_OBJECT_ID("OBSObjectId"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("Priority")
    PRIORITY("Priority"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectName")
    PROJECT_NAME("ProjectName"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ProjectThresholdObjectId")
    PROJECT_THRESHOLD_OBJECT_ID("ProjectThresholdObjectId"),
    @XmlEnumValue("RawTextNote")
    RAW_TEXT_NOTE("RawTextNote"),
    @XmlEnumValue("ResolutionDate")
    RESOLUTION_DATE("ResolutionDate"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("ThresholdParameterObjectId")
    THRESHOLD_PARAMETER_OBJECT_ID("ThresholdParameterObjectId"),
    @XmlEnumValue("UpperThreshold")
    UPPER_THRESHOLD("UpperThreshold"),
    @XmlEnumValue("WBSCode")
    WBS_CODE("WBSCode"),
    @XmlEnumValue("WBSName")
    WBS_NAME("WBSName"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ProjectIssueFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectIssueFieldType fromValue(String v) {
        for (ProjectIssueFieldType c: ProjectIssueFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
