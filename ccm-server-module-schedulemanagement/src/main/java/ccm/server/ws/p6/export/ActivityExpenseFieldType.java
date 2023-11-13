
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ActivityExpenseFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ActivityExpenseFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AccrualType"/&gt;
 *     &lt;enumeration value="ActivityId"/&gt;
 *     &lt;enumeration value="ActivityName"/&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="ActualCost"/&gt;
 *     &lt;enumeration value="ActualUnits"/&gt;
 *     &lt;enumeration value="AtCompletionCost"/&gt;
 *     &lt;enumeration value="AtCompletionUnits"/&gt;
 *     &lt;enumeration value="AutoComputeActuals"/&gt;
 *     &lt;enumeration value="CBSCode"/&gt;
 *     &lt;enumeration value="CBSId"/&gt;
 *     &lt;enumeration value="CostAccountId"/&gt;
 *     &lt;enumeration value="CostAccountName"/&gt;
 *     &lt;enumeration value="CostAccountObjectId"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DocumentNumber"/&gt;
 *     &lt;enumeration value="ExpenseCategoryName"/&gt;
 *     &lt;enumeration value="ExpenseCategoryObjectId"/&gt;
 *     &lt;enumeration value="ExpenseDescription"/&gt;
 *     &lt;enumeration value="ExpenseItem"/&gt;
 *     &lt;enumeration value="ExpensePercentComplete"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="OverBudget"/&gt;
 *     &lt;enumeration value="PlannedCost"/&gt;
 *     &lt;enumeration value="PlannedUnits"/&gt;
 *     &lt;enumeration value="PricePerUnit"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="RemainingCost"/&gt;
 *     &lt;enumeration value="RemainingUnits"/&gt;
 *     &lt;enumeration value="UnitOfMeasure"/&gt;
 *     &lt;enumeration value="Vendor"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ActivityExpenseFieldType")
@XmlEnum
public enum ActivityExpenseFieldType {

    @XmlEnumValue("AccrualType")
    ACCRUAL_TYPE("AccrualType"),
    @XmlEnumValue("ActivityId")
    ACTIVITY_ID("ActivityId"),
    @XmlEnumValue("ActivityName")
    ACTIVITY_NAME("ActivityName"),
    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("ActualCost")
    ACTUAL_COST("ActualCost"),
    @XmlEnumValue("ActualUnits")
    ACTUAL_UNITS("ActualUnits"),
    @XmlEnumValue("AtCompletionCost")
    AT_COMPLETION_COST("AtCompletionCost"),
    @XmlEnumValue("AtCompletionUnits")
    AT_COMPLETION_UNITS("AtCompletionUnits"),
    @XmlEnumValue("AutoComputeActuals")
    AUTO_COMPUTE_ACTUALS("AutoComputeActuals"),
    @XmlEnumValue("CBSCode")
    CBS_CODE("CBSCode"),
    @XmlEnumValue("CBSId")
    CBS_ID("CBSId"),
    @XmlEnumValue("CostAccountId")
    COST_ACCOUNT_ID("CostAccountId"),
    @XmlEnumValue("CostAccountName")
    COST_ACCOUNT_NAME("CostAccountName"),
    @XmlEnumValue("CostAccountObjectId")
    COST_ACCOUNT_OBJECT_ID("CostAccountObjectId"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("DocumentNumber")
    DOCUMENT_NUMBER("DocumentNumber"),
    @XmlEnumValue("ExpenseCategoryName")
    EXPENSE_CATEGORY_NAME("ExpenseCategoryName"),
    @XmlEnumValue("ExpenseCategoryObjectId")
    EXPENSE_CATEGORY_OBJECT_ID("ExpenseCategoryObjectId"),
    @XmlEnumValue("ExpenseDescription")
    EXPENSE_DESCRIPTION("ExpenseDescription"),
    @XmlEnumValue("ExpenseItem")
    EXPENSE_ITEM("ExpenseItem"),
    @XmlEnumValue("ExpensePercentComplete")
    EXPENSE_PERCENT_COMPLETE("ExpensePercentComplete"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("OverBudget")
    OVER_BUDGET("OverBudget"),
    @XmlEnumValue("PlannedCost")
    PLANNED_COST("PlannedCost"),
    @XmlEnumValue("PlannedUnits")
    PLANNED_UNITS("PlannedUnits"),
    @XmlEnumValue("PricePerUnit")
    PRICE_PER_UNIT("PricePerUnit"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("RemainingCost")
    REMAINING_COST("RemainingCost"),
    @XmlEnumValue("RemainingUnits")
    REMAINING_UNITS("RemainingUnits"),
    @XmlEnumValue("UnitOfMeasure")
    UNIT_OF_MEASURE("UnitOfMeasure"),
    @XmlEnumValue("Vendor")
    VENDOR("Vendor"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ActivityExpenseFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ActivityExpenseFieldType fromValue(String v) {
        for (ActivityExpenseFieldType c: ActivityExpenseFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
