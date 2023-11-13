
package ccm.server.ws.p6.export;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ResourceAssignmentFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ResourceAssignmentFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityActualFinish"/&gt;
 *     &lt;enumeration value="ActivityId"/&gt;
 *     &lt;enumeration value="ActivityName"/&gt;
 *     &lt;enumeration value="ActivityObjectId"/&gt;
 *     &lt;enumeration value="ActivityType"/&gt;
 *     &lt;enumeration value="ActualCost"/&gt;
 *     &lt;enumeration value="ActualCurve"/&gt;
 *     &lt;enumeration value="ActualCurve"/&gt;
 *     &lt;enumeration value="ActualDuration"/&gt;
 *     &lt;enumeration value="ActualFinishDate"/&gt;
 *     &lt;enumeration value="ActualOvertimeCost"/&gt;
 *     &lt;enumeration value="ActualOvertimeUnits"/&gt;
 *     &lt;enumeration value="ActualRegularCost"/&gt;
 *     &lt;enumeration value="ActualRegularUnits"/&gt;
 *     &lt;enumeration value="ActualStartDate"/&gt;
 *     &lt;enumeration value="ActualThisPeriodCost"/&gt;
 *     &lt;enumeration value="ActualThisPeriodUnits"/&gt;
 *     &lt;enumeration value="ActualUnits"/&gt;
 *     &lt;enumeration value="AtCompletionCost"/&gt;
 *     &lt;enumeration value="AtCompletionDuration"/&gt;
 *     &lt;enumeration value="AtCompletionUnits"/&gt;
 *     &lt;enumeration value="AutoComputeActuals"/&gt;
 *     &lt;enumeration value="CBSCode"/&gt;
 *     &lt;enumeration value="CBSId"/&gt;
 *     &lt;enumeration value="CalendarName"/&gt;
 *     &lt;enumeration value="CalendarObjectId"/&gt;
 *     &lt;enumeration value="CostAccountId"/&gt;
 *     &lt;enumeration value="CostAccountName"/&gt;
 *     &lt;enumeration value="CostAccountObjectId"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="DrivingActivityDatesFlag"/&gt;
 *     &lt;enumeration value="FinishDate"/&gt;
 *     &lt;enumeration value="GUID"/&gt;
 *     &lt;enumeration value="HasFutureBucketData"/&gt;
 *     &lt;enumeration value="IsActive"/&gt;
 *     &lt;enumeration value="IsActivityFlagged"/&gt;
 *     &lt;enumeration value="IsBaseline"/&gt;
 *     &lt;enumeration value="IsCostUnitsLinked"/&gt;
 *     &lt;enumeration value="IsOvertimeAllowed"/&gt;
 *     &lt;enumeration value="IsPrimaryResource"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="OvertimeFactor"/&gt;
 *     &lt;enumeration value="PendingActualOvertimeUnits"/&gt;
 *     &lt;enumeration value="PendingActualRegularUnits"/&gt;
 *     &lt;enumeration value="PendingPercentComplete"/&gt;
 *     &lt;enumeration value="PendingRemainingUnits"/&gt;
 *     &lt;enumeration value="PlannedCost"/&gt;
 *     &lt;enumeration value="PlannedCurve"/&gt;
 *     &lt;enumeration value="PlannedCurve"/&gt;
 *     &lt;enumeration value="PlannedDuration"/&gt;
 *     &lt;enumeration value="PlannedFinishDate"/&gt;
 *     &lt;enumeration value="PlannedLag"/&gt;
 *     &lt;enumeration value="PlannedStartDate"/&gt;
 *     &lt;enumeration value="PlannedUnits"/&gt;
 *     &lt;enumeration value="PlannedUnitsPerTime"/&gt;
 *     &lt;enumeration value="PricePerUnit"/&gt;
 *     &lt;enumeration value="PriorActualOvertimeUnits"/&gt;
 *     &lt;enumeration value="PriorActualRegularUnits"/&gt;
 *     &lt;enumeration value="Proficiency"/&gt;
 *     &lt;enumeration value="ProjectFlag"/&gt;
 *     &lt;enumeration value="ProjectId"/&gt;
 *     &lt;enumeration value="ProjectName"/&gt;
 *     &lt;enumeration value="ProjectObjectId"/&gt;
 *     &lt;enumeration value="ProjectProjectFlag"/&gt;
 *     &lt;enumeration value="RateSource"/&gt;
 *     &lt;enumeration value="RateType"/&gt;
 *     &lt;enumeration value="RemainingCost"/&gt;
 *     &lt;enumeration value="RemainingCurve"/&gt;
 *     &lt;enumeration value="RemainingCurve"/&gt;
 *     &lt;enumeration value="RemainingDuration"/&gt;
 *     &lt;enumeration value="RemainingFinishDate"/&gt;
 *     &lt;enumeration value="RemainingLag"/&gt;
 *     &lt;enumeration value="RemainingLateFinishDate"/&gt;
 *     &lt;enumeration value="RemainingLateStartDate"/&gt;
 *     &lt;enumeration value="RemainingStartDate"/&gt;
 *     &lt;enumeration value="RemainingUnits"/&gt;
 *     &lt;enumeration value="RemainingUnitsPerTime"/&gt;
 *     &lt;enumeration value="ResourceCalendarName"/&gt;
 *     &lt;enumeration value="ResourceCurveName"/&gt;
 *     &lt;enumeration value="ResourceCurveObjectId"/&gt;
 *     &lt;enumeration value="ResourceId"/&gt;
 *     &lt;enumeration value="ResourceName"/&gt;
 *     &lt;enumeration value="ResourceObjectId"/&gt;
 *     &lt;enumeration value="ResourceRequest"/&gt;
 *     &lt;enumeration value="ResourceType"/&gt;
 *     &lt;enumeration value="ReviewRequired"/&gt;
 *     &lt;enumeration value="RoleId"/&gt;
 *     &lt;enumeration value="RoleName"/&gt;
 *     &lt;enumeration value="RoleObjectId"/&gt;
 *     &lt;enumeration value="RoleShortName"/&gt;
 *     &lt;enumeration value="StaffedRemainingCost"/&gt;
 *     &lt;enumeration value="StaffedRemainingUnits"/&gt;
 *     &lt;enumeration value="StartDate"/&gt;
 *     &lt;enumeration value="StatusCode"/&gt;
 *     &lt;enumeration value="UnitsPercentComplete"/&gt;
 *     &lt;enumeration value="UnreadCommentCount"/&gt;
 *     &lt;enumeration value="UnstaffedRemainingCost"/&gt;
 *     &lt;enumeration value="UnstaffedRemainingUnits"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ResourceAssignmentFieldType")
@XmlEnum
public enum ResourceAssignmentFieldType {

    @XmlEnumValue("ActivityActualFinish")
    ACTIVITY_ACTUAL_FINISH("ActivityActualFinish"),
    @XmlEnumValue("ActivityId")
    ACTIVITY_ID("ActivityId"),
    @XmlEnumValue("ActivityName")
    ACTIVITY_NAME("ActivityName"),
    @XmlEnumValue("ActivityObjectId")
    ACTIVITY_OBJECT_ID("ActivityObjectId"),
    @XmlEnumValue("ActivityType")
    ACTIVITY_TYPE("ActivityType"),
    @XmlEnumValue("ActualCost")
    ACTUAL_COST("ActualCost"),
    @XmlEnumValue("ActualCurve")
    ACTUAL_CURVE("ActualCurve"),
    @XmlEnumValue("ActualDuration")
    ACTUAL_DURATION("ActualDuration"),
    @XmlEnumValue("ActualFinishDate")
    ACTUAL_FINISH_DATE("ActualFinishDate"),
    @XmlEnumValue("ActualOvertimeCost")
    ACTUAL_OVERTIME_COST("ActualOvertimeCost"),
    @XmlEnumValue("ActualOvertimeUnits")
    ACTUAL_OVERTIME_UNITS("ActualOvertimeUnits"),
    @XmlEnumValue("ActualRegularCost")
    ACTUAL_REGULAR_COST("ActualRegularCost"),
    @XmlEnumValue("ActualRegularUnits")
    ACTUAL_REGULAR_UNITS("ActualRegularUnits"),
    @XmlEnumValue("ActualStartDate")
    ACTUAL_START_DATE("ActualStartDate"),
    @XmlEnumValue("ActualThisPeriodCost")
    ACTUAL_THIS_PERIOD_COST("ActualThisPeriodCost"),
    @XmlEnumValue("ActualThisPeriodUnits")
    ACTUAL_THIS_PERIOD_UNITS("ActualThisPeriodUnits"),
    @XmlEnumValue("ActualUnits")
    ACTUAL_UNITS("ActualUnits"),
    @XmlEnumValue("AtCompletionCost")
    AT_COMPLETION_COST("AtCompletionCost"),
    @XmlEnumValue("AtCompletionDuration")
    AT_COMPLETION_DURATION("AtCompletionDuration"),
    @XmlEnumValue("AtCompletionUnits")
    AT_COMPLETION_UNITS("AtCompletionUnits"),
    @XmlEnumValue("AutoComputeActuals")
    AUTO_COMPUTE_ACTUALS("AutoComputeActuals"),
    @XmlEnumValue("CBSCode")
    CBS_CODE("CBSCode"),
    @XmlEnumValue("CBSId")
    CBS_ID("CBSId"),
    @XmlEnumValue("CalendarName")
    CALENDAR_NAME("CalendarName"),
    @XmlEnumValue("CalendarObjectId")
    CALENDAR_OBJECT_ID("CalendarObjectId"),
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
    @XmlEnumValue("DrivingActivityDatesFlag")
    DRIVING_ACTIVITY_DATES_FLAG("DrivingActivityDatesFlag"),
    @XmlEnumValue("FinishDate")
    FINISH_DATE("FinishDate"),
    GUID("GUID"),
    @XmlEnumValue("HasFutureBucketData")
    HAS_FUTURE_BUCKET_DATA("HasFutureBucketData"),
    @XmlEnumValue("IsActive")
    IS_ACTIVE("IsActive"),
    @XmlEnumValue("IsActivityFlagged")
    IS_ACTIVITY_FLAGGED("IsActivityFlagged"),
    @XmlEnumValue("IsBaseline")
    IS_BASELINE("IsBaseline"),
    @XmlEnumValue("IsCostUnitsLinked")
    IS_COST_UNITS_LINKED("IsCostUnitsLinked"),
    @XmlEnumValue("IsOvertimeAllowed")
    IS_OVERTIME_ALLOWED("IsOvertimeAllowed"),
    @XmlEnumValue("IsPrimaryResource")
    IS_PRIMARY_RESOURCE("IsPrimaryResource"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("OvertimeFactor")
    OVERTIME_FACTOR("OvertimeFactor"),
    @XmlEnumValue("PendingActualOvertimeUnits")
    PENDING_ACTUAL_OVERTIME_UNITS("PendingActualOvertimeUnits"),
    @XmlEnumValue("PendingActualRegularUnits")
    PENDING_ACTUAL_REGULAR_UNITS("PendingActualRegularUnits"),
    @XmlEnumValue("PendingPercentComplete")
    PENDING_PERCENT_COMPLETE("PendingPercentComplete"),
    @XmlEnumValue("PendingRemainingUnits")
    PENDING_REMAINING_UNITS("PendingRemainingUnits"),
    @XmlEnumValue("PlannedCost")
    PLANNED_COST("PlannedCost"),
    @XmlEnumValue("PlannedCurve")
    PLANNED_CURVE("PlannedCurve"),
    @XmlEnumValue("PlannedDuration")
    PLANNED_DURATION("PlannedDuration"),
    @XmlEnumValue("PlannedFinishDate")
    PLANNED_FINISH_DATE("PlannedFinishDate"),
    @XmlEnumValue("PlannedLag")
    PLANNED_LAG("PlannedLag"),
    @XmlEnumValue("PlannedStartDate")
    PLANNED_START_DATE("PlannedStartDate"),
    @XmlEnumValue("PlannedUnits")
    PLANNED_UNITS("PlannedUnits"),
    @XmlEnumValue("PlannedUnitsPerTime")
    PLANNED_UNITS_PER_TIME("PlannedUnitsPerTime"),
    @XmlEnumValue("PricePerUnit")
    PRICE_PER_UNIT("PricePerUnit"),
    @XmlEnumValue("PriorActualOvertimeUnits")
    PRIOR_ACTUAL_OVERTIME_UNITS("PriorActualOvertimeUnits"),
    @XmlEnumValue("PriorActualRegularUnits")
    PRIOR_ACTUAL_REGULAR_UNITS("PriorActualRegularUnits"),
    @XmlEnumValue("Proficiency")
    PROFICIENCY("Proficiency"),
    @XmlEnumValue("ProjectFlag")
    PROJECT_FLAG("ProjectFlag"),
    @XmlEnumValue("ProjectId")
    PROJECT_ID("ProjectId"),
    @XmlEnumValue("ProjectName")
    PROJECT_NAME("ProjectName"),
    @XmlEnumValue("ProjectObjectId")
    PROJECT_OBJECT_ID("ProjectObjectId"),
    @XmlEnumValue("ProjectProjectFlag")
    PROJECT_PROJECT_FLAG("ProjectProjectFlag"),
    @XmlEnumValue("RateSource")
    RATE_SOURCE("RateSource"),
    @XmlEnumValue("RateType")
    RATE_TYPE("RateType"),
    @XmlEnumValue("RemainingCost")
    REMAINING_COST("RemainingCost"),
    @XmlEnumValue("RemainingCurve")
    REMAINING_CURVE("RemainingCurve"),
    @XmlEnumValue("RemainingDuration")
    REMAINING_DURATION("RemainingDuration"),
    @XmlEnumValue("RemainingFinishDate")
    REMAINING_FINISH_DATE("RemainingFinishDate"),
    @XmlEnumValue("RemainingLag")
    REMAINING_LAG("RemainingLag"),
    @XmlEnumValue("RemainingLateFinishDate")
    REMAINING_LATE_FINISH_DATE("RemainingLateFinishDate"),
    @XmlEnumValue("RemainingLateStartDate")
    REMAINING_LATE_START_DATE("RemainingLateStartDate"),
    @XmlEnumValue("RemainingStartDate")
    REMAINING_START_DATE("RemainingStartDate"),
    @XmlEnumValue("RemainingUnits")
    REMAINING_UNITS("RemainingUnits"),
    @XmlEnumValue("RemainingUnitsPerTime")
    REMAINING_UNITS_PER_TIME("RemainingUnitsPerTime"),
    @XmlEnumValue("ResourceCalendarName")
    RESOURCE_CALENDAR_NAME("ResourceCalendarName"),
    @XmlEnumValue("ResourceCurveName")
    RESOURCE_CURVE_NAME("ResourceCurveName"),
    @XmlEnumValue("ResourceCurveObjectId")
    RESOURCE_CURVE_OBJECT_ID("ResourceCurveObjectId"),
    @XmlEnumValue("ResourceId")
    RESOURCE_ID("ResourceId"),
    @XmlEnumValue("ResourceName")
    RESOURCE_NAME("ResourceName"),
    @XmlEnumValue("ResourceObjectId")
    RESOURCE_OBJECT_ID("ResourceObjectId"),
    @XmlEnumValue("ResourceRequest")
    RESOURCE_REQUEST("ResourceRequest"),
    @XmlEnumValue("ResourceType")
    RESOURCE_TYPE("ResourceType"),
    @XmlEnumValue("ReviewRequired")
    REVIEW_REQUIRED("ReviewRequired"),
    @XmlEnumValue("RoleId")
    ROLE_ID("RoleId"),
    @XmlEnumValue("RoleName")
    ROLE_NAME("RoleName"),
    @XmlEnumValue("RoleObjectId")
    ROLE_OBJECT_ID("RoleObjectId"),
    @XmlEnumValue("RoleShortName")
    ROLE_SHORT_NAME("RoleShortName"),
    @XmlEnumValue("StaffedRemainingCost")
    STAFFED_REMAINING_COST("StaffedRemainingCost"),
    @XmlEnumValue("StaffedRemainingUnits")
    STAFFED_REMAINING_UNITS("StaffedRemainingUnits"),
    @XmlEnumValue("StartDate")
    START_DATE("StartDate"),
    @XmlEnumValue("StatusCode")
    STATUS_CODE("StatusCode"),
    @XmlEnumValue("UnitsPercentComplete")
    UNITS_PERCENT_COMPLETE("UnitsPercentComplete"),
    @XmlEnumValue("UnreadCommentCount")
    UNREAD_COMMENT_COUNT("UnreadCommentCount"),
    @XmlEnumValue("UnstaffedRemainingCost")
    UNSTAFFED_REMAINING_COST("UnstaffedRemainingCost"),
    @XmlEnumValue("UnstaffedRemainingUnits")
    UNSTAFFED_REMAINING_UNITS("UnstaffedRemainingUnits"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId");
    private final String value;

    ResourceAssignmentFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceAssignmentFieldType fromValue(String v) {
        for (ResourceAssignmentFieldType c: ResourceAssignmentFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
