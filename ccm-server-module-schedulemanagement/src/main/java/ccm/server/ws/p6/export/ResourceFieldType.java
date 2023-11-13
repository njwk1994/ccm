
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ResourceFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ResourceFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AutoComputeActuals"/&gt;
 *     &lt;enumeration value="CalculateCostFromUnits"/&gt;
 *     &lt;enumeration value="CalendarName"/&gt;
 *     &lt;enumeration value="CalendarObjectId"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="CurrencyId"/&gt;
 *     &lt;enumeration value="CurrencyName"/&gt;
 *     &lt;enumeration value="CurrencyObjectId"/&gt;
 *     &lt;enumeration value="DefaultUnitsPerTime"/&gt;
 *     &lt;enumeration value="EffectiveDate"/&gt;
 *     &lt;enumeration value="EmailAddress"/&gt;
 *     &lt;enumeration value="EmployeeId"/&gt;
 *     &lt;enumeration value="GUID"/&gt;
 *     &lt;enumeration value="Id"/&gt;
 *     &lt;enumeration value="IntegratedType"/&gt;
 *     &lt;enumeration value="IsActive"/&gt;
 *     &lt;enumeration value="IsOverTimeAllowed"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Latitude"/&gt;
 *     &lt;enumeration value="LocationName"/&gt;
 *     &lt;enumeration value="LocationObjectId"/&gt;
 *     &lt;enumeration value="Longitude"/&gt;
 *     &lt;enumeration value="MaxUnitsPerTime"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="OfficePhone"/&gt;
 *     &lt;enumeration value="OtherPhone"/&gt;
 *     &lt;enumeration value="OvertimeFactor"/&gt;
 *     &lt;enumeration value="ParentObjectId"/&gt;
 *     &lt;enumeration value="PricePerUnit"/&gt;
 *     &lt;enumeration value="PrimaryRoleId"/&gt;
 *     &lt;enumeration value="PrimaryRoleName"/&gt;
 *     &lt;enumeration value="PrimaryRoleObjectId"/&gt;
 *     &lt;enumeration value="ResourceNotes"/&gt;
 *     &lt;enumeration value="ResourceType"/&gt;
 *     &lt;enumeration value="SequenceNumber"/&gt;
 *     &lt;enumeration value="ShiftObjectId"/&gt;
 *     &lt;enumeration value="TimesheetApprovalManager"/&gt;
 *     &lt;enumeration value="TimesheetApprovalManagerObjectId"/&gt;
 *     &lt;enumeration value="Title"/&gt;
 *     &lt;enumeration value="UnitOfMeasureAbbreviation"/&gt;
 *     &lt;enumeration value="UnitOfMeasureName"/&gt;
 *     &lt;enumeration value="UnitOfMeasureObjectId"/&gt;
 *     &lt;enumeration value="UseTimesheets"/&gt;
 *     &lt;enumeration value="UserName"/&gt;
 *     &lt;enumeration value="UserObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ResourceFieldType")
@XmlEnum
public enum ResourceFieldType {

    @XmlEnumValue("AutoComputeActuals")
    AUTO_COMPUTE_ACTUALS("AutoComputeActuals"),
    @XmlEnumValue("CalculateCostFromUnits")
    CALCULATE_COST_FROM_UNITS("CalculateCostFromUnits"),
    @XmlEnumValue("CalendarName")
    CALENDAR_NAME("CalendarName"),
    @XmlEnumValue("CalendarObjectId")
    CALENDAR_OBJECT_ID("CalendarObjectId"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("CurrencyId")
    CURRENCY_ID("CurrencyId"),
    @XmlEnumValue("CurrencyName")
    CURRENCY_NAME("CurrencyName"),
    @XmlEnumValue("CurrencyObjectId")
    CURRENCY_OBJECT_ID("CurrencyObjectId"),
    @XmlEnumValue("DefaultUnitsPerTime")
    DEFAULT_UNITS_PER_TIME("DefaultUnitsPerTime"),
    @XmlEnumValue("EffectiveDate")
    EFFECTIVE_DATE("EffectiveDate"),
    @XmlEnumValue("EmailAddress")
    EMAIL_ADDRESS("EmailAddress"),
    @XmlEnumValue("EmployeeId")
    EMPLOYEE_ID("EmployeeId"),
    GUID("GUID"),
    @XmlEnumValue("Id")
    ID("Id"),
    @XmlEnumValue("IntegratedType")
    INTEGRATED_TYPE("IntegratedType"),
    @XmlEnumValue("IsActive")
    IS_ACTIVE("IsActive"),
    @XmlEnumValue("IsOverTimeAllowed")
    IS_OVER_TIME_ALLOWED("IsOverTimeAllowed"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Latitude")
    LATITUDE("Latitude"),
    @XmlEnumValue("LocationName")
    LOCATION_NAME("LocationName"),
    @XmlEnumValue("LocationObjectId")
    LOCATION_OBJECT_ID("LocationObjectId"),
    @XmlEnumValue("Longitude")
    LONGITUDE("Longitude"),
    @XmlEnumValue("MaxUnitsPerTime")
    MAX_UNITS_PER_TIME("MaxUnitsPerTime"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("OfficePhone")
    OFFICE_PHONE("OfficePhone"),
    @XmlEnumValue("OtherPhone")
    OTHER_PHONE("OtherPhone"),
    @XmlEnumValue("OvertimeFactor")
    OVERTIME_FACTOR("OvertimeFactor"),
    @XmlEnumValue("ParentObjectId")
    PARENT_OBJECT_ID("ParentObjectId"),
    @XmlEnumValue("PricePerUnit")
    PRICE_PER_UNIT("PricePerUnit"),
    @XmlEnumValue("PrimaryRoleId")
    PRIMARY_ROLE_ID("PrimaryRoleId"),
    @XmlEnumValue("PrimaryRoleName")
    PRIMARY_ROLE_NAME("PrimaryRoleName"),
    @XmlEnumValue("PrimaryRoleObjectId")
    PRIMARY_ROLE_OBJECT_ID("PrimaryRoleObjectId"),
    @XmlEnumValue("ResourceNotes")
    RESOURCE_NOTES("ResourceNotes"),
    @XmlEnumValue("ResourceType")
    RESOURCE_TYPE("ResourceType"),
    @XmlEnumValue("SequenceNumber")
    SEQUENCE_NUMBER("SequenceNumber"),
    @XmlEnumValue("ShiftObjectId")
    SHIFT_OBJECT_ID("ShiftObjectId"),
    @XmlEnumValue("TimesheetApprovalManager")
    TIMESHEET_APPROVAL_MANAGER("TimesheetApprovalManager"),
    @XmlEnumValue("TimesheetApprovalManagerObjectId")
    TIMESHEET_APPROVAL_MANAGER_OBJECT_ID("TimesheetApprovalManagerObjectId"),
    @XmlEnumValue("Title")
    TITLE("Title"),
    @XmlEnumValue("UnitOfMeasureAbbreviation")
    UNIT_OF_MEASURE_ABBREVIATION("UnitOfMeasureAbbreviation"),
    @XmlEnumValue("UnitOfMeasureName")
    UNIT_OF_MEASURE_NAME("UnitOfMeasureName"),
    @XmlEnumValue("UnitOfMeasureObjectId")
    UNIT_OF_MEASURE_OBJECT_ID("UnitOfMeasureObjectId"),
    @XmlEnumValue("UseTimesheets")
    USE_TIMESHEETS("UseTimesheets"),
    @XmlEnumValue("UserName")
    USER_NAME("UserName"),
    @XmlEnumValue("UserObjectId")
    USER_OBJECT_ID("UserObjectId");
    private final String value;

    ResourceFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceFieldType fromValue(String v) {
        for (ResourceFieldType c: ResourceFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
