
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>CurrencyFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="CurrencyFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DecimalPlaces"/&gt;
 *     &lt;enumeration value="DecimalSymbol"/&gt;
 *     &lt;enumeration value="DigitGroupingSymbol"/&gt;
 *     &lt;enumeration value="ExchangeRate"/&gt;
 *     &lt;enumeration value="Id"/&gt;
 *     &lt;enumeration value="IsBaseCurrency"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="NegativeSymbol"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="PositiveSymbol"/&gt;
 *     &lt;enumeration value="Symbol"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "CurrencyFieldType")
@XmlEnum
public enum CurrencyFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("DecimalPlaces")
    DECIMAL_PLACES("DecimalPlaces"),
    @XmlEnumValue("DecimalSymbol")
    DECIMAL_SYMBOL("DecimalSymbol"),
    @XmlEnumValue("DigitGroupingSymbol")
    DIGIT_GROUPING_SYMBOL("DigitGroupingSymbol"),
    @XmlEnumValue("ExchangeRate")
    EXCHANGE_RATE("ExchangeRate"),
    @XmlEnumValue("Id")
    ID("Id"),
    @XmlEnumValue("IsBaseCurrency")
    IS_BASE_CURRENCY("IsBaseCurrency"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("NegativeSymbol")
    NEGATIVE_SYMBOL("NegativeSymbol"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("PositiveSymbol")
    POSITIVE_SYMBOL("PositiveSymbol"),
    @XmlEnumValue("Symbol")
    SYMBOL("Symbol");
    private final String value;

    CurrencyFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CurrencyFieldType fromValue(String v) {
        for (CurrencyFieldType c: CurrencyFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
