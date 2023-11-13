
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>RiskResponseActionFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="RiskResponseActionFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityId"/&gt;
 *     &lt;enumeration value="ActivityName"/&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="ActualCost"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="FinishDate"/&gt;
 *     &lt;enumeration value="Id"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="PlannedCost"/&gt;
 *     &lt;enumeration value="PlannedFinishDate"/&gt;
 *     &lt;enumeration value="PlannedStartDate"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectName"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="RemainingCost"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="RiskId"/&gt;
 *     &lt;enumeration value="RiskObjectId"/&gt;
 *     &lt;enumeration value="RiskResponsePlanId"/&gt;
 *     &lt;enumeration value="RiskResponsePlanName"/&gt;
 *     &lt;enumeration value="RiskResponsePlanObjectId"/&gt;
 *     &lt;enumeration value="Score"/&gt;
 *     &lt;enumeration value="ScoreColor"/&gt;
 *     &lt;enumeration value="ScoreText"/&gt;
 *     &lt;enumeration value="StartDate"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "RiskResponseActionFieldType")
@XmlEnum
public enum RiskResponseActionFieldType {

    @XmlEnumValue("ActivityId")
    ACTIVITY_ID("ActivityId"),
    @XmlEnumValue("ActivityName")
    ACTIVITY_NAME("ActivityName"),
    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("ActualCost")
    ACTUAL_COST("ActualCost"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("FinishDate")
    FINISH_DATE("FinishDate"),
    @XmlEnumValue("Id")
    ID("Id"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
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
    @XmlEnumValue("PlannedCost")
    PLANNED_COST("PlannedCost"),
    @XmlEnumValue("PlannedFinishDate")
    PLANNED_FINISH_DATE("PlannedFinishDate"),
    @XmlEnumValue("PlannedStartDate")
    PLANNED_START_DATE("PlannedStartDate"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectName")
    PROJECT_NAME("ProjectName"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("RemainingCost")
    REMAINING_COST("RemainingCost"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("RiskId")
    RISK_ID("RiskId"),
    @XmlEnumValue("RiskObjectId")
    RISK_OBJECT_ID("RiskObjectId"),
    @XmlEnumValue("RiskResponsePlanId")
    RISK_RESPONSE_PLAN_ID("RiskResponsePlanId"),
    @XmlEnumValue("RiskResponsePlanName")
    RISK_RESPONSE_PLAN_NAME("RiskResponsePlanName"),
    @XmlEnumValue("RiskResponsePlanObjectId")
    RISK_RESPONSE_PLAN_OBJECT_ID("RiskResponsePlanObjectId"),
    @XmlEnumValue("Score")
    SCORE("Score"),
    @XmlEnumValue("ScoreColor")
    SCORE_COLOR("ScoreColor"),
    @XmlEnumValue("ScoreText")
    SCORE_TEXT("ScoreText"),
    @XmlEnumValue("StartDate")
    START_DATE("StartDate"),
    @XmlEnumValue("Status")
    STATUS("Status");
    private final String value;

    RiskResponseActionFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RiskResponseActionFieldType fromValue(String v) {
        for (RiskResponseActionFieldType c: RiskResponseActionFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
