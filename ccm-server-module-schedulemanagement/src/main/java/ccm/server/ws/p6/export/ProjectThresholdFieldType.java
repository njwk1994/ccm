
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ProjectThresholdFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ProjectThresholdFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DetailToMonitor"/&gt;
 *     &lt;enumeration value="FromDate"/&gt;
 *     &lt;enumeration value="FromDateExpression"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="IssuePriority"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="LowerThreshold"/&gt;
 *     &lt;enumeration value="OBSObjectId"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *     &lt;enumeration value="ThresholdParameterObjectId"/&gt;
 *     &lt;enumeration value="ToDate"/&gt;
 *     &lt;enumeration value="ToDateExpression"/&gt;
 *     &lt;enumeration value="UpperThreshold"/&gt;
 *     &lt;enumeration value="WBSCode"/&gt;
 *     &lt;enumeration value="WBSName"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ProjectThresholdFieldType")
@XmlEnum
public enum ProjectThresholdFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("DetailToMonitor")
    DETAIL_TO_MONITOR("DetailToMonitor"),
    @XmlEnumValue("FromDate")
    FROM_DATE("FromDate"),
    @XmlEnumValue("FromDateExpression")
    FROM_DATE_EXPRESSION("FromDateExpression"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("IssuePriority")
    ISSUE_PRIORITY("IssuePriority"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("LowerThreshold")
    LOWER_THRESHOLD("LowerThreshold"),
    @XmlEnumValue("OBSObjectId")
    OBS_OBJECT_ID("OBSObjectId"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("ThresholdParameterObjectId")
    THRESHOLD_PARAMETER_OBJECT_ID("ThresholdParameterObjectId"),
    @XmlEnumValue("ToDate")
    TO_DATE("ToDate"),
    @XmlEnumValue("ToDateExpression")
    TO_DATE_EXPRESSION("ToDateExpression"),
    @XmlEnumValue("UpperThreshold")
    UPPER_THRESHOLD("UpperThreshold"),
    @XmlEnumValue("WBSCode")
    WBS_CODE("WBSCode"),
    @XmlEnumValue("WBSName")
    WBS_NAME("WBSName"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ProjectThresholdFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectThresholdFieldType fromValue(String v) {
        for (ProjectThresholdFieldType c: ProjectThresholdFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
