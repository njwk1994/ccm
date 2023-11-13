
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>SpreadPeriodType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="SpreadPeriodType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Hour"/&gt;
 *     &lt;enumeration value="Day"/&gt;
 *     &lt;enumeration value="Week"/&gt;
 *     &lt;enumeration value="Month"/&gt;
 *     &lt;enumeration value="Quarter"/&gt;
 *     &lt;enumeration value="Year"/&gt;
 *     &lt;enumeration value="FinancialPeriod"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "SpreadPeriodType")
@XmlEnum
public enum SpreadPeriodType {

    @XmlEnumValue("Hour")
    HOUR("Hour"),
    @XmlEnumValue("Day")
    DAY("Day"),
    @XmlEnumValue("Week")
    WEEK("Week"),
    @XmlEnumValue("Month")
    MONTH("Month"),
    @XmlEnumValue("Quarter")
    QUARTER("Quarter"),
    @XmlEnumValue("Year")
    YEAR("Year"),
    @XmlEnumValue("FinancialPeriod")
    FINANCIAL_PERIOD("FinancialPeriod");
    private final String value;

    SpreadPeriodType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SpreadPeriodType fromValue(String v) {
        for (SpreadPeriodType c: SpreadPeriodType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
