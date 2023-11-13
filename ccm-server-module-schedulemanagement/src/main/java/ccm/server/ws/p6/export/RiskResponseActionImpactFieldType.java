
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskResponseActionImpactFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskResponseActionImpactFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
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
 *     &lt;enumeration value="RiskObjectId"/&gt;
 *     &lt;enumeration value="RiskResponseActionId"/&gt;
 *     &lt;enumeration value="RiskResponseActionName"/&gt;
 *     &lt;enumeration value="RiskResponseActionObjectId"/&gt;
 *     &lt;enumeration value="RiskThresholdLevelCode"/&gt;
 *     &lt;enumeration value="RiskThresholdLevelName"/&gt;
 *     &lt;enumeration value="RiskThresholdLevelObjectId"/&gt;
 *     &lt;enumeration value="RiskThresholdName"/&gt;
 *     &lt;enumeration value="RiskThresholdObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskResponseActionImpactFieldType")
@XmlEnum
public enum RiskResponseActionImpactFieldType {

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
    @XmlEnumValue("RiskObjectId")
    RISK_OBJECT_ID("RiskObjectId"),
    @XmlEnumValue("RiskResponseActionId")
    RISK_RESPONSE_ACTION_ID("RiskResponseActionId"),
    @XmlEnumValue("RiskResponseActionName")
    RISK_RESPONSE_ACTION_NAME("RiskResponseActionName"),
    @XmlEnumValue("RiskResponseActionObjectId")
    RISK_RESPONSE_ACTION_OBJECT_ID("RiskResponseActionObjectId"),
    @XmlEnumValue("RiskThresholdLevelCode")
    RISK_THRESHOLD_LEVEL_CODE("RiskThresholdLevelCode"),
    @XmlEnumValue("RiskThresholdLevelName")
    RISK_THRESHOLD_LEVEL_NAME("RiskThresholdLevelName"),
    @XmlEnumValue("RiskThresholdLevelObjectId")
    RISK_THRESHOLD_LEVEL_OBJECT_ID("RiskThresholdLevelObjectId"),
    @XmlEnumValue("RiskThresholdName")
    RISK_THRESHOLD_NAME("RiskThresholdName"),
    @XmlEnumValue("RiskThresholdObjectId")
    RISK_THRESHOLD_OBJECT_ID("RiskThresholdObjectId");
    private final String value;

    RiskResponseActionImpactFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskResponseActionImpactFieldType fromValue(String v) {
        for (RiskResponseActionImpactFieldType c: RiskResponseActionImpactFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
