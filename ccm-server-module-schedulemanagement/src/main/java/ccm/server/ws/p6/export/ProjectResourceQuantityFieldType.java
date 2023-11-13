
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ProjectResourceQuantityFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ProjectResourceQuantityFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CommittedFlag"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="FinancialPeriod1ObjectId"/&gt;
 *     &lt;enumeration value="FinancialPeriod1Quantity"/&gt;
 *     &lt;enumeration value="FinancialPeriod2ObjectId"/&gt;
 *     &lt;enumeration value="FinancialPeriod2Quantity"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="MonthStartDate"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ProjectResourceObjectId"/&gt;
 *     &lt;enumeration value="Quantity"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="RoleObjectId"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *     &lt;enumeration value="WeekStartDate"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ProjectResourceQuantityFieldType")
@XmlEnum
public enum ProjectResourceQuantityFieldType {

    @XmlEnumValue("CommittedFlag")
    COMMITTED_FLAG("CommittedFlag"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("FinancialPeriod1ObjectId")
    FINANCIAL_PERIOD_1_OBJECT_ID("FinancialPeriod1ObjectId"),
    @XmlEnumValue("FinancialPeriod1Quantity")
    FINANCIAL_PERIOD_1_QUANTITY("FinancialPeriod1Quantity"),
    @XmlEnumValue("FinancialPeriod2ObjectId")
    FINANCIAL_PERIOD_2_OBJECT_ID("FinancialPeriod2ObjectId"),
    @XmlEnumValue("FinancialPeriod2Quantity")
    FINANCIAL_PERIOD_2_QUANTITY("FinancialPeriod2Quantity"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("MonthStartDate")
    MONTH_START_DATE("MonthStartDate"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ProjectResourceObjectId")
    PROJECT_RESOURCE_OBJECT_ID("ProjectResourceObjectId"),
    @XmlEnumValue("Quantity")
    QUANTITY("Quantity"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("RoleObjectId")
    ROLE_OBJECT_ID("RoleObjectId"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId"),
    @XmlEnumValue("WeekStartDate")
    WEEK_START_DATE("WeekStartDate");
    private final String value;

    ProjectResourceQuantityFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectResourceQuantityFieldType fromValue(String v) {
        for (ProjectResourceQuantityFieldType c: ProjectResourceQuantityFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
