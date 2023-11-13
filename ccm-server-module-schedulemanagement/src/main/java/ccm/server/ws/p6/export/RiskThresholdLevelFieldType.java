
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskThresholdLevelFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskThresholdLevelFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Code"/&gt;
 *     &lt;enumeration value="Color"/&gt;
 *     &lt;enumeration value="CostRange"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Level"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProbabilityRange"/&gt;
 *     &lt;enumeration value="Range"/&gt;
 *     &lt;enumeration value="RiskThresholdName"/&gt;
 *     &lt;enumeration value="RiskThresholdObjectId"/&gt;
 *     &lt;enumeration value="ScheduleRange"/&gt;
 *     &lt;enumeration value="ThresholdType"/&gt;
 *     &lt;enumeration value="ToleranceRange"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskThresholdLevelFieldType")
@XmlEnum
public enum RiskThresholdLevelFieldType {

    @XmlEnumValue("Code")
    CODE("Code"),
    @XmlEnumValue("Color")
    COLOR("Color"),
    @XmlEnumValue("CostRange")
    COST_RANGE("CostRange"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Level")
    LEVEL("Level"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProbabilityRange")
    PROBABILITY_RANGE("ProbabilityRange"),
    @XmlEnumValue("Range")
    RANGE("Range"),
    @XmlEnumValue("RiskThresholdName")
    RISK_THRESHOLD_NAME("RiskThresholdName"),
    @XmlEnumValue("RiskThresholdObjectId")
    RISK_THRESHOLD_OBJECT_ID("RiskThresholdObjectId"),
    @XmlEnumValue("ScheduleRange")
    SCHEDULE_RANGE("ScheduleRange"),
    @XmlEnumValue("ThresholdType")
    THRESHOLD_TYPE("ThresholdType"),
    @XmlEnumValue("ToleranceRange")
    TOLERANCE_RANGE("ToleranceRange");
    private final String value;

    RiskThresholdLevelFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskThresholdLevelFieldType fromValue(String v) {
        for (RiskThresholdLevelFieldType c: RiskThresholdLevelFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
