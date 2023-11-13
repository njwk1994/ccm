
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>CalendarFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="CalendarFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="BaseCalendarObjectId"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="HoursPerDay"/&gt;
 *     &lt;enumeration value="HoursPerMonth"/&gt;
 *     &lt;enumeration value="HoursPerWeek"/&gt;
 *     &lt;enumeration value="HoursPerYear"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsDefault"/&gt;
 *     &lt;enumeration value="IsPersonal"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="Type"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "CalendarFieldType")
@XmlEnum
public enum CalendarFieldType {

    @XmlEnumValue("BaseCalendarObjectId")
    BASE_CALENDAR_OBJECT_ID("BaseCalendarObjectId"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("HoursPerDay")
    HOURS_PER_DAY("HoursPerDay"),
    @XmlEnumValue("HoursPerMonth")
    HOURS_PER_MONTH("HoursPerMonth"),
    @XmlEnumValue("HoursPerWeek")
    HOURS_PER_WEEK("HoursPerWeek"),
    @XmlEnumValue("HoursPerYear")
    HOURS_PER_YEAR("HoursPerYear"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsDefault")
    IS_DEFAULT("IsDefault"),
    @XmlEnumValue("IsPersonal")
    IS_PERSONAL("IsPersonal"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("Type")
    TYPE("Type");
    private final String value;

    CalendarFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CalendarFieldType fromValue(String v) {
        for (CalendarFieldType c: CalendarFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
