
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ActivityRiskFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ActivityRiskFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityId"/&gt;
 *     &lt;enumeration value="ActivityName"/&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectName"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="RiskId"/&gt;
 *     &lt;enumeration value="RiskName"/&gt;
 *     &lt;enumeration value="RiskObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ActivityRiskFieldType")
@XmlEnum
public enum ActivityRiskFieldType {

    @XmlEnumValue("ActivityId")
    ACTIVITY_ID("ActivityId"),
    @XmlEnumValue("ActivityName")
    ACTIVITY_NAME("ActivityName"),
    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectName")
    PROJECT_NAME("ProjectName"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("RiskId")
    RISK_ID("RiskId"),
    @XmlEnumValue("RiskName")
    RISK_NAME("RiskName"),
    @XmlEnumValue("RiskObjectId")
    RISK_OBJECT_ID("RiskObjectId");
    private final String value;

    ActivityRiskFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActivityRiskFieldType fromValue(String v) {
        for (ActivityRiskFieldType c: ActivityRiskFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
