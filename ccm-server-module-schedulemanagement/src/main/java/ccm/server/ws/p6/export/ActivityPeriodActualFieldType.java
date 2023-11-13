
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ActivityPeriodActualFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ActivityPeriodActualFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="ActualExpenseCost"/&gt;
 *     &lt;enumeration value="ActualLaborCost"/&gt;
 *     &lt;enumeration value="ActualLaborUnits"/&gt;
 *     &lt;enumeration value="ActualMaterialCost"/&gt;
 *     &lt;enumeration value="ActualNonLaborCost"/&gt;
 *     &lt;enumeration value="ActualNonLaborUnits"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="EarnedValueCost"/&gt;
 *     &lt;enumeration value="EarnedValueLaborUnits"/&gt;
 *     &lt;enumeration value="FinancialPeriodObjectId"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="PlannedValueCost"/&gt;
 *     &lt;enumeration value="PlannedValueLaborUnits"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ActivityPeriodActualFieldType")
@XmlEnum
public enum ActivityPeriodActualFieldType {

    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("ActualExpenseCost")
    ACTUAL_EXPENSE_COST("ActualExpenseCost"),
    @XmlEnumValue("ActualLaborCost")
    ACTUAL_LABOR_COST("ActualLaborCost"),
    @XmlEnumValue("ActualLaborUnits")
    ACTUAL_LABOR_UNITS("ActualLaborUnits"),
    @XmlEnumValue("ActualMaterialCost")
    ACTUAL_MATERIAL_COST("ActualMaterialCost"),
    @XmlEnumValue("ActualNonLaborCost")
    ACTUAL_NON_LABOR_COST("ActualNonLaborCost"),
    @XmlEnumValue("ActualNonLaborUnits")
    ACTUAL_NON_LABOR_UNITS("ActualNonLaborUnits"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("EarnedValueCost")
    EARNED_VALUE_COST("EarnedValueCost"),
    @XmlEnumValue("EarnedValueLaborUnits")
    EARNED_VALUE_LABOR_UNITS("EarnedValueLaborUnits"),
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
    @XmlEnumValue("PlannedValueCost")
    PLANNED_VALUE_COST("PlannedValueCost"),
    @XmlEnumValue("PlannedValueLaborUnits")
    PLANNED_VALUE_LABOR_UNITS("PlannedValueLaborUnits"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ActivityPeriodActualFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActivityPeriodActualFieldType fromValue(String v) {
        for (ActivityPeriodActualFieldType c: ActivityPeriodActualFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
