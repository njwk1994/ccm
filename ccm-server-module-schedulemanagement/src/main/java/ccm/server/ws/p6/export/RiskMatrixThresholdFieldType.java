
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskMatrixThresholdFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskMatrixThresholdFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="RiskMatrixName"/&gt;
 *     &lt;enumeration value="RiskMatrixObjectId"/&gt;
 *     &lt;enumeration value="RiskThresholdName"/&gt;
 *     &lt;enumeration value="RiskThresholdObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskMatrixThresholdFieldType")
@XmlEnum
public enum RiskMatrixThresholdFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("RiskMatrixName")
    RISK_MATRIX_NAME("RiskMatrixName"),
    @XmlEnumValue("RiskMatrixObjectId")
    RISK_MATRIX_OBJECT_ID("RiskMatrixObjectId"),
    @XmlEnumValue("RiskThresholdName")
    RISK_THRESHOLD_NAME("RiskThresholdName"),
    @XmlEnumValue("RiskThresholdObjectId")
    RISK_THRESHOLD_OBJECT_ID("RiskThresholdObjectId");
    private final String value;

    RiskMatrixThresholdFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskMatrixThresholdFieldType fromValue(String v) {
        for (RiskMatrixThresholdFieldType c: RiskMatrixThresholdFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
