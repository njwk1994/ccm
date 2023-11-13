
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ResourceRateFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ResourceRateFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="EffectiveDate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="MaxUnitsPerTime"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="PricePerUnit"/&gt;
 *     &lt;enumeration value="PricePerUnit2"/&gt;
 *     &lt;enumeration value="PricePerUnit3"/&gt;
 *     &lt;enumeration value="PricePerUnit4"/&gt;
 *     &lt;enumeration value="PricePerUnit5"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="ShiftPeriodObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ResourceRateFieldType")
@XmlEnum
public enum ResourceRateFieldType {

    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("EffectiveDate")
    EFFECTIVE_DATE("EffectiveDate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("MaxUnitsPerTime")
    MAX_UNITS_PER_TIME("MaxUnitsPerTime"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("PricePerUnit")
    PRICE_PER_UNIT("PricePerUnit"),
    @XmlEnumValue("PricePerUnit2")
    PRICE_PER_UNIT_2("PricePerUnit2"),
    @XmlEnumValue("PricePerUnit3")
    PRICE_PER_UNIT_3("PricePerUnit3"),
    @XmlEnumValue("PricePerUnit4")
    PRICE_PER_UNIT_4("PricePerUnit4"),
    @XmlEnumValue("PricePerUnit5")
    PRICE_PER_UNIT_5("PricePerUnit5"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("ShiftPeriodObjectId")
    SHIFT_PERIOD_OBJECT_ID("ShiftPeriodObjectId");
    private final String value;

    ResourceRateFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceRateFieldType fromValue(String v) {
        for (ResourceRateFieldType c: ResourceRateFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
