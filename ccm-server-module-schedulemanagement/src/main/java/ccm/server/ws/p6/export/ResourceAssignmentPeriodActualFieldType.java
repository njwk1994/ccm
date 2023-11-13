
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ResourceAssignmentPeriodActualFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ResourceAssignmentPeriodActualFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="ActualCost"/&gt;
 *     &lt;enumeration value="ActualUnits"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="FinancialPeriodObjectId"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ResourceAssignmentObjectId"/&gt;
 *     &lt;enumeration value="ResourceType"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ResourceAssignmentPeriodActualFieldType")
@XmlEnum
public enum ResourceAssignmentPeriodActualFieldType {

    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("ActualCost")
    ACTUAL_COST("ActualCost"),
    @XmlEnumValue("ActualUnits")
    ACTUAL_UNITS("ActualUnits"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("FinancialPeriodObjectId")
    FINANCIAL_PERIOD_OBJECT_ID("FinancialPeriodObjectId"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ResourceAssignmentObjectId")
    RESOURCE_ASSIGNMENT_OBJECT_ID("ResourceAssignmentObjectId"),
    @XmlEnumValue("ResourceType")
    RESOURCE_TYPE("ResourceType"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ResourceAssignmentPeriodActualFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceAssignmentPeriodActualFieldType fromValue(String v) {
        for (ResourceAssignmentPeriodActualFieldType c: ResourceAssignmentPeriodActualFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
