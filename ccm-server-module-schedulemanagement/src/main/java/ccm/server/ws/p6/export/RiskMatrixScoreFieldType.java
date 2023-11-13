
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskMatrixScoreFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskMatrixScoreFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProbabilityThresholdLevel"/&gt;
 *     &lt;enumeration value="RiskMatrixName"/&gt;
 *     &lt;enumeration value="RiskMatrixObjectId"/&gt;
 *     &lt;enumeration value="Severity1"/&gt;
 *     &lt;enumeration value="Severity1Label"/&gt;
 *     &lt;enumeration value="Severity2"/&gt;
 *     &lt;enumeration value="Severity2Label"/&gt;
 *     &lt;enumeration value="Severity3"/&gt;
 *     &lt;enumeration value="Severity3Label"/&gt;
 *     &lt;enumeration value="Severity4"/&gt;
 *     &lt;enumeration value="Severity4Label"/&gt;
 *     &lt;enumeration value="Severity5"/&gt;
 *     &lt;enumeration value="Severity5Label"/&gt;
 *     &lt;enumeration value="Severity6"/&gt;
 *     &lt;enumeration value="Severity6Label"/&gt;
 *     &lt;enumeration value="Severity7"/&gt;
 *     &lt;enumeration value="Severity7Label"/&gt;
 *     &lt;enumeration value="Severity8"/&gt;
 *     &lt;enumeration value="Severity8Label"/&gt;
 *     &lt;enumeration value="Severity9"/&gt;
 *     &lt;enumeration value="Severity9Label"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskMatrixScoreFieldType")
@XmlEnum
public enum RiskMatrixScoreFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProbabilityThresholdLevel")
    PROBABILITY_THRESHOLD_LEVEL("ProbabilityThresholdLevel"),
    @XmlEnumValue("RiskMatrixName")
    RISK_MATRIX_NAME("RiskMatrixName"),
    @XmlEnumValue("RiskMatrixObjectId")
    RISK_MATRIX_OBJECT_ID("RiskMatrixObjectId"),
    @XmlEnumValue("Severity1")
    SEVERITY_1("Severity1"),
    @XmlEnumValue("Severity1Label")
    SEVERITY_1_LABEL("Severity1Label"),
    @XmlEnumValue("Severity2")
    SEVERITY_2("Severity2"),
    @XmlEnumValue("Severity2Label")
    SEVERITY_2_LABEL("Severity2Label"),
    @XmlEnumValue("Severity3")
    SEVERITY_3("Severity3"),
    @XmlEnumValue("Severity3Label")
    SEVERITY_3_LABEL("Severity3Label"),
    @XmlEnumValue("Severity4")
    SEVERITY_4("Severity4"),
    @XmlEnumValue("Severity4Label")
    SEVERITY_4_LABEL("Severity4Label"),
    @XmlEnumValue("Severity5")
    SEVERITY_5("Severity5"),
    @XmlEnumValue("Severity5Label")
    SEVERITY_5_LABEL("Severity5Label"),
    @XmlEnumValue("Severity6")
    SEVERITY_6("Severity6"),
    @XmlEnumValue("Severity6Label")
    SEVERITY_6_LABEL("Severity6Label"),
    @XmlEnumValue("Severity7")
    SEVERITY_7("Severity7"),
    @XmlEnumValue("Severity7Label")
    SEVERITY_7_LABEL("Severity7Label"),
    @XmlEnumValue("Severity8")
    SEVERITY_8("Severity8"),
    @XmlEnumValue("Severity8Label")
    SEVERITY_8_LABEL("Severity8Label"),
    @XmlEnumValue("Severity9")
    SEVERITY_9("Severity9"),
    @XmlEnumValue("Severity9Label")
    SEVERITY_9_LABEL("Severity9Label");
    private final String value;

    RiskMatrixScoreFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskMatrixScoreFieldType fromValue(String v) {
        for (RiskMatrixScoreFieldType c: RiskMatrixScoreFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
