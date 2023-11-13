
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskMatrixFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskMatrixFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="Description"/&gt;
 *     &lt;enumeration value="ImpactThresholdLevel"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProbabilityThresholdLevel"/&gt;
 *     &lt;enumeration value="RiskScoringMethod"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskMatrixFieldType")
@XmlEnum
public enum RiskMatrixFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("Description")
    DESCRIPTION("Description"),
    @XmlEnumValue("ImpactThresholdLevel")
    IMPACT_THRESHOLD_LEVEL("ImpactThresholdLevel"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProbabilityThresholdLevel")
    PROBABILITY_THRESHOLD_LEVEL("ProbabilityThresholdLevel"),
    @XmlEnumValue("RiskScoringMethod")
    RISK_SCORING_METHOD("RiskScoringMethod");
    private final String value;

    RiskMatrixFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskMatrixFieldType fromValue(String v) {
        for (RiskMatrixFieldType c: RiskMatrixFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
