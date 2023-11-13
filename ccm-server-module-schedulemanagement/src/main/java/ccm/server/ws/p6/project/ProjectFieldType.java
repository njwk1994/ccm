
package ccm.server.ws.p6.project;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ProjectFieldType的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <pre>
 * &lt;simpleType name="ProjectFieldType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ActivityDefaultActivityType"/&gt;
 *     &lt;enumeration value="ActivityDefaultCalendarName"/&gt;
 *     &lt;enumeration value="ActivityDefaultCalendarObjectId"/&gt;
 *     &lt;enumeration value="ActivityDefaultCostAccountObjectId"/&gt;
 *     &lt;enumeration value="ActivityDefaultDurationType"/&gt;
 *     &lt;enumeration value="ActivityDefaultPercentCompleteType"/&gt;
 *     &lt;enumeration value="ActivityDefaultPricePerUnit"/&gt;
 *     &lt;enumeration value="ActivityDefaultReviewRequired"/&gt;
 *     &lt;enumeration value="ActivityIdBasedOnSelectedActivity"/&gt;
 *     &lt;enumeration value="ActivityIdIncrement"/&gt;
 *     &lt;enumeration value="ActivityIdPrefix"/&gt;
 *     &lt;enumeration value="ActivityIdSuffix"/&gt;
 *     &lt;enumeration value="ActivityPercentCompleteBasedOnActivitySteps"/&gt;
 *     &lt;enumeration value="AddActualToRemaining"/&gt;
 *     &lt;enumeration value="AddedBy"/&gt;
 *     &lt;enumeration value="AllowNegativeActualUnitsFlag"/&gt;
 *     &lt;enumeration value="AllowStatusReview"/&gt;
 *     &lt;enumeration value="AnnualDiscountRate"/&gt;
 *     &lt;enumeration value="AnticipatedFinishDate"/&gt;
 *     &lt;enumeration value="AnticipatedStartDate"/&gt;
 *     &lt;enumeration value="AssignmentDefaultDrivingFlag"/&gt;
 *     &lt;enumeration value="AssignmentDefaultRateType"/&gt;
 *     &lt;enumeration value="CalculateFloatBasedOnFinishDate"/&gt;
 *     &lt;enumeration value="CheckOutDate"/&gt;
 *     &lt;enumeration value="CheckOutStatus"/&gt;
 *     &lt;enumeration value="CheckOutUserObjectId"/&gt;
 *     &lt;enumeration value="ComputeTotalFloatType"/&gt;
 *     &lt;enumeration value="ContainsSummaryData"/&gt;
 *     &lt;enumeration value="ContractManagementGroupName"/&gt;
 *     &lt;enumeration value="ContractManagementProjectName"/&gt;
 *     &lt;enumeration value="CostQuantityRecalculateFlag"/&gt;
 *     &lt;enumeration value="CreateDate"/&gt;
 *     &lt;enumeration value="CreateUser"/&gt;
 *     &lt;enumeration value="CriticalActivityFloatLimit"/&gt;
 *     &lt;enumeration value="CriticalActivityFloatThreshold"/&gt;
 *     &lt;enumeration value="CriticalActivityPathType"/&gt;
 *     &lt;enumeration value="CriticalFloatThreshold"/&gt;
 *     &lt;enumeration value="CurrentBaselineProjectObjectId"/&gt;
 *     &lt;enumeration value="CurrentBudget"/&gt;
 *     &lt;enumeration value="CurrentVariance"/&gt;
 *     &lt;enumeration value="DataDate"/&gt;
 *     &lt;enumeration value="DateAdded"/&gt;
 *     &lt;enumeration value="DefaultPriceTimeUnits"/&gt;
 *     &lt;enumeration value="Description"/&gt;
 *     &lt;enumeration value="DiscountApplicationPeriod"/&gt;
 *     &lt;enumeration value="DistributedCurrentBudget"/&gt;
 *     &lt;enumeration value="EarnedValueComputeType"/&gt;
 *     &lt;enumeration value="EarnedValueETCComputeType"/&gt;
 *     &lt;enumeration value="EarnedValueETCUserValue"/&gt;
 *     &lt;enumeration value="EarnedValueUserPercent"/&gt;
 *     &lt;enumeration value="EnablePrimeSycFlag"/&gt;
 *     &lt;enumeration value="EnablePublication"/&gt;
 *     &lt;enumeration value="EnableSummarization"/&gt;
 *     &lt;enumeration value="EtlInterval"/&gt;
 *     &lt;enumeration value="FinishDate"/&gt;
 *     &lt;enumeration value="FiscalYearStartMonth"/&gt;
 *     &lt;enumeration value="ForecastFinishDate"/&gt;
 *     &lt;enumeration value="ForecastStartDate"/&gt;
 *     &lt;enumeration value="GUID"/&gt;
 *     &lt;enumeration value="HasFutureBucketData"/&gt;
 *     &lt;enumeration value="HistoryInterval"/&gt;
 *     &lt;enumeration value="HistoryLevel"/&gt;
 *     &lt;enumeration value="Id"/&gt;
 *     &lt;enumeration value="IgnoreOtherProjectRelationships"/&gt;
 *     &lt;enumeration value="IndependentETCLaborUnits"/&gt;
 *     &lt;enumeration value="IndependentETCTotalCost"/&gt;
 *     &lt;enumeration value="IntegratedType"/&gt;
 *     &lt;enumeration value="IsTemplate"/&gt;
 *     &lt;enumeration value="LastApplyActualsDate"/&gt;
 *     &lt;enumeration value="LastFinancialPeriodObjectId"/&gt;
 *     &lt;enumeration value="LastLevelDate"/&gt;
 *     &lt;enumeration value="LastPublishedOn"/&gt;
 *     &lt;enumeration value="LastScheduleDate"/&gt;
 *     &lt;enumeration value="LastSummarizedDate"/&gt;
 *     &lt;enumeration value="LastUpdateDate"/&gt;
 *     &lt;enumeration value="LastUpdateUser"/&gt;
 *     &lt;enumeration value="Latitude"/&gt;
 *     &lt;enumeration value="LevelAllResources"/&gt;
 *     &lt;enumeration value="LevelDateFlag"/&gt;
 *     &lt;enumeration value="LevelFloatThresholdCount"/&gt;
 *     &lt;enumeration value="LevelOuterAssign"/&gt;
 *     &lt;enumeration value="LevelOuterAssignPriority"/&gt;
 *     &lt;enumeration value="LevelOverAllocationPercent"/&gt;
 *     &lt;enumeration value="LevelPriorityList"/&gt;
 *     &lt;enumeration value="LevelResourceList"/&gt;
 *     &lt;enumeration value="LevelWithinFloat"/&gt;
 *     &lt;enumeration value="LevelingPriority"/&gt;
 *     &lt;enumeration value="LimitMultipleFloatPaths"/&gt;
 *     &lt;enumeration value="LinkActualToActualThisPeriod"/&gt;
 *     &lt;enumeration value="LinkPercentCompleteWithActual"/&gt;
 *     &lt;enumeration value="LinkPlannedAndAtCompletionFlag"/&gt;
 *     &lt;enumeration value="LocationName"/&gt;
 *     &lt;enumeration value="LocationObjectId"/&gt;
 *     &lt;enumeration value="Longitude"/&gt;
 *     &lt;enumeration value="MakeOpenEndedActivitiesCritical"/&gt;
 *     &lt;enumeration value="MaximumMultipleFloatPaths"/&gt;
 *     &lt;enumeration value="MultipleFloatPathsEnabled"/&gt;
 *     &lt;enumeration value="MultipleFloatPathsEndingActivityObjectId"/&gt;
 *     &lt;enumeration value="MultipleFloatPathsUseTotalFloat"/&gt;
 *     &lt;enumeration value="MustFinishByDate"/&gt;
 *     &lt;enumeration value="Name"/&gt;
 *     &lt;enumeration value="NetPresentValue"/&gt;
 *     &lt;enumeration value="OBSName"/&gt;
 *     &lt;enumeration value="OBSObjectId"/&gt;
 *     &lt;enumeration value="ObjectId"/&gt;
 *     &lt;enumeration value="OriginalBudget"/&gt;
 *     &lt;enumeration value="OutOfSequenceScheduleType"/&gt;
 *     &lt;enumeration value="OverallProjectScore"/&gt;
 *     &lt;enumeration value="OwnerResourceObjectId"/&gt;
 *     &lt;enumeration value="ParentEPSObjectId"/&gt;
 *     &lt;enumeration value="PaybackPeriod"/&gt;
 *     &lt;enumeration value="PlannedStartDate"/&gt;
 *     &lt;enumeration value="PrimaryResourcesCanMarkActivitiesAsCompleted"/&gt;
 *     &lt;enumeration value="ProjectForecastStartDate"/&gt;
 *     &lt;enumeration value="ProjectScheduleType"/&gt;
 *     &lt;enumeration value="PropertyType"/&gt;
 *     &lt;enumeration value="ProposedBudget"/&gt;
 *     &lt;enumeration value="PublicationPriority"/&gt;
 *     &lt;enumeration value="PublishLevel"/&gt;
 *     &lt;enumeration value="RelationshipLagCalendar"/&gt;
 *     &lt;enumeration value="ResetPlannedToRemainingFlag"/&gt;
 *     &lt;enumeration value="ResourceCanBeAssignedToSameActivityMoreThanOnce"/&gt;
 *     &lt;enumeration value="ResourcesCanAssignThemselvesToActivities"/&gt;
 *     &lt;enumeration value="ResourcesCanEditAssignmentPercentComplete"/&gt;
 *     &lt;enumeration value="ResourcesCanMarkAssignmentAsCompleted"/&gt;
 *     &lt;enumeration value="ResourcesCanViewInactiveActivities"/&gt;
 *     &lt;enumeration value="ReturnOnInvestment"/&gt;
 *     &lt;enumeration value="RiskExposure"/&gt;
 *     &lt;enumeration value="RiskLevel"/&gt;
 *     &lt;enumeration value="RiskMatrixName"/&gt;
 *     &lt;enumeration value="RiskMatrixObjectId"/&gt;
 *     &lt;enumeration value="RiskScore"/&gt;
 *     &lt;enumeration value="ScheduleWBSHierarchyType"/&gt;
 *     &lt;enumeration value="ScheduledFinishDate"/&gt;
 *     &lt;enumeration value="SourceProjectObjectId"/&gt;
 *     &lt;enumeration value="StartDate"/&gt;
 *     &lt;enumeration value="StartToStartLagCalculationType"/&gt;
 *     &lt;enumeration value="Status"/&gt;
 *     &lt;enumeration value="StatusReviewerName"/&gt;
 *     &lt;enumeration value="StatusReviewerObjectId"/&gt;
 *     &lt;enumeration value="StrategicPriority"/&gt;
 *     &lt;enumeration value="SummarizeResourcesRolesByWBS"/&gt;
 *     &lt;enumeration value="SummarizeToWBSLevel"/&gt;
 *     &lt;enumeration value="SummarizedDataDate"/&gt;
 *     &lt;enumeration value="SummaryAccountingVarianceByCost"/&gt;
 *     &lt;enumeration value="SummaryAccountingVarianceByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActivityCount"/&gt;
 *     &lt;enumeration value="SummaryActualDuration"/&gt;
 *     &lt;enumeration value="SummaryActualExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryActualFinishDate"/&gt;
 *     &lt;enumeration value="SummaryActualLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryActualNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualStartDate"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryActualThisPeriodNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryActualTotalCost"/&gt;
 *     &lt;enumeration value="SummaryActualValueByCost"/&gt;
 *     &lt;enumeration value="SummaryActualValueByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionDuration"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionLaborCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionTotalCost"/&gt;
 *     &lt;enumeration value="SummaryAtCompletionTotalCostVariance"/&gt;
 *     &lt;enumeration value="SummaryBaselineCompletedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryBaselineDuration"/&gt;
 *     &lt;enumeration value="SummaryBaselineExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineFinishDate"/&gt;
 *     &lt;enumeration value="SummaryBaselineInProgressActivityCount"/&gt;
 *     &lt;enumeration value="SummaryBaselineLaborCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryBaselineMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryBaselineNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryBaselineNotStartedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryBaselineStartDate"/&gt;
 *     &lt;enumeration value="SummaryBaselineTotalCost"/&gt;
 *     &lt;enumeration value="SummaryBudgetAtCompletionByCost"/&gt;
 *     &lt;enumeration value="SummaryBudgetAtCompletionByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryCompletedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryCostPercentOfPlanned"/&gt;
 *     &lt;enumeration value="SummaryCostPerformanceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryCostPerformanceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceByCost"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceIndex"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryCostVarianceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryDurationPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryDurationPercentOfPlanned"/&gt;
 *     &lt;enumeration value="SummaryDurationVariance"/&gt;
 *     &lt;enumeration value="SummaryEarnedValueByCost"/&gt;
 *     &lt;enumeration value="SummaryEarnedValueByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionByCost"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionHighPercentByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateAtCompletionLowPercentByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryEstimateToCompleteByCost"/&gt;
 *     &lt;enumeration value="SummaryEstimateToCompleteByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryExpenseCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryExpenseCostVariance"/&gt;
 *     &lt;enumeration value="SummaryFinishDateVariance"/&gt;
 *     &lt;enumeration value="SummaryInProgressActivityCount"/&gt;
 *     &lt;enumeration value="SummaryLaborCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryLaborCostVariance"/&gt;
 *     &lt;enumeration value="SummaryLaborUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryLaborUnitsVariance"/&gt;
 *     &lt;enumeration value="SummaryLevel"/&gt;
 *     &lt;enumeration value="SummaryMaterialCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryMaterialCostVariance"/&gt;
 *     &lt;enumeration value="SummaryNonLaborCostPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryNonLaborCostVariance"/&gt;
 *     &lt;enumeration value="SummaryNonLaborUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryNonLaborUnitsVariance"/&gt;
 *     &lt;enumeration value="SummaryNotStartedActivityCount"/&gt;
 *     &lt;enumeration value="SummaryPerformancePercentCompleteByCost"/&gt;
 *     &lt;enumeration value="SummaryPerformancePercentCompleteByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryPlannedCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedDuration"/&gt;
 *     &lt;enumeration value="SummaryPlannedExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedFinishDate"/&gt;
 *     &lt;enumeration value="SummaryPlannedLaborCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryPlannedMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryPlannedStartDate"/&gt;
 *     &lt;enumeration value="SummaryPlannedValueByCost"/&gt;
 *     &lt;enumeration value="SummaryPlannedValueByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryProgressFinishDate"/&gt;
 *     &lt;enumeration value="SummaryRemainingDuration"/&gt;
 *     &lt;enumeration value="SummaryRemainingExpenseCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingFinishDate"/&gt;
 *     &lt;enumeration value="SummaryRemainingLaborCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryRemainingMaterialCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingNonLaborCost"/&gt;
 *     &lt;enumeration value="SummaryRemainingNonLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryRemainingStartDate"/&gt;
 *     &lt;enumeration value="SummaryRemainingTotalCost"/&gt;
 *     &lt;enumeration value="SummarySchedulePercentComplete"/&gt;
 *     &lt;enumeration value="SummarySchedulePercentCompleteByLaborUnits"/&gt;
 *     &lt;enumeration value="SummarySchedulePerformanceIndexByCost"/&gt;
 *     &lt;enumeration value="SummarySchedulePerformanceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceByCost"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceIndex"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryScheduleVarianceIndexByLaborUnits"/&gt;
 *     &lt;enumeration value="SummaryStartDateVariance"/&gt;
 *     &lt;enumeration value="SummaryToCompletePerformanceIndexByCost"/&gt;
 *     &lt;enumeration value="SummaryTotalCostVariance"/&gt;
 *     &lt;enumeration value="SummaryTotalFloat"/&gt;
 *     &lt;enumeration value="SummaryUnitsPercentComplete"/&gt;
 *     &lt;enumeration value="SummaryVarianceAtCompletionByLaborUnits"/&gt;
 *     &lt;enumeration value="SyncWbsHierarchyFlag"/&gt;
 *     &lt;enumeration value="TeamMemberActivityFields"/&gt;
 *     &lt;enumeration value="TeamMemberAddNewActualUnits"/&gt;
 *     &lt;enumeration value="TeamMemberAssignmentOption"/&gt;
 *     &lt;enumeration value="TeamMemberCanStatusOtherResources"/&gt;
 *     &lt;enumeration value="TeamMemberCanUpdateNotebooks"/&gt;
 *     &lt;enumeration value="TeamMemberDisplayPlannedUnits"/&gt;
 *     &lt;enumeration value="TeamMemberIncludePrimaryResources"/&gt;
 *     &lt;enumeration value="TeamMemberResourceAssignmentFields"/&gt;
 *     &lt;enumeration value="TeamMemberStepsAddDeletable"/&gt;
 *     &lt;enumeration value="TeamMemberViewableFields"/&gt;
 *     &lt;enumeration value="TotalBenefitPlan"/&gt;
 *     &lt;enumeration value="TotalBenefitPlanTally"/&gt;
 *     &lt;enumeration value="TotalFunding"/&gt;
 *     &lt;enumeration value="TotalSpendingPlan"/&gt;
 *     &lt;enumeration value="TotalSpendingPlanTally"/&gt;
 *     &lt;enumeration value="UnallocatedBudget"/&gt;
 *     &lt;enumeration value="UndistributedCurrentVariance"/&gt;
 *     &lt;enumeration value="UnifierCBSTasksOnlyFlag"/&gt;
 *     &lt;enumeration value="UnifierDataMappingName"/&gt;
 *     &lt;enumeration value="UnifierDeleteActivitiesFlag"/&gt;
 *     &lt;enumeration value="UnifierEnabledFlag"/&gt;
 *     &lt;enumeration value="UnifierProjectName"/&gt;
 *     &lt;enumeration value="UnifierProjectNumber"/&gt;
 *     &lt;enumeration value="UnifierScheduleSheetName"/&gt;
 *     &lt;enumeration value="UseExpectedFinishDates"/&gt;
 *     &lt;enumeration value="UseProjectBaselineForEarnedValue"/&gt;
 *     &lt;enumeration value="WBSCodeSeparator"/&gt;
 *     &lt;enumeration value="WBSHierarchyLevels"/&gt;
 *     &lt;enumeration value="WBSMilestonePercentComplete"/&gt;
 *     &lt;enumeration value="WBSObjectId"/&gt;
 *     &lt;enumeration value="WebSiteRootDirectory"/&gt;
 *     &lt;enumeration value="WebSiteURL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "ProjectFieldType")
@XmlEnum
public enum ProjectFieldType {

    @XmlEnumValue("ActivityDefaultActivityType")
    ACTIVITY_DEFAULT_ACTIVITY_TYPE("ActivityDefaultActivityType"),
    @XmlEnumValue("ActivityDefaultCalendarName")
    ACTIVITY_DEFAULT_CALENDAR_NAME("ActivityDefaultCalendarName"),
    @XmlEnumValue("ActivityDefaultCalendarObjectId")
    ACTIVITY_DEFAULT_CALENDAR_OBJECT_ID("ActivityDefaultCalendarObjectId"),
    @XmlEnumValue("ActivityDefaultCostAccountObjectId")
    ACTIVITY_DEFAULT_COST_ACCOUNT_OBJECT_ID("ActivityDefaultCostAccountObjectId"),
    @XmlEnumValue("ActivityDefaultDurationType")
    ACTIVITY_DEFAULT_DURATION_TYPE("ActivityDefaultDurationType"),
    @XmlEnumValue("ActivityDefaultPercentCompleteType")
    ACTIVITY_DEFAULT_PERCENT_COMPLETE_TYPE("ActivityDefaultPercentCompleteType"),
    @XmlEnumValue("ActivityDefaultPricePerUnit")
    ACTIVITY_DEFAULT_PRICE_PER_UNIT("ActivityDefaultPricePerUnit"),
    @XmlEnumValue("ActivityDefaultReviewRequired")
    ACTIVITY_DEFAULT_REVIEW_REQUIRED("ActivityDefaultReviewRequired"),
    @XmlEnumValue("ActivityIdBasedOnSelectedActivity")
    ACTIVITY_ID_BASED_ON_SELECTED_ACTIVITY("ActivityIdBasedOnSelectedActivity"),
    @XmlEnumValue("ActivityIdIncrement")
    ACTIVITY_ID_INCREMENT("ActivityIdIncrement"),
    @XmlEnumValue("ActivityIdPrefix")
    ACTIVITY_ID_PREFIX("ActivityIdPrefix"),
    @XmlEnumValue("ActivityIdSuffix")
    ACTIVITY_ID_SUFFIX("ActivityIdSuffix"),
    @XmlEnumValue("ActivityPercentCompleteBasedOnActivitySteps")
    ACTIVITY_PERCENT_COMPLETE_BASED_ON_ACTIVITY_STEPS("ActivityPercentCompleteBasedOnActivitySteps"),
    @XmlEnumValue("AddActualToRemaining")
    ADD_ACTUAL_TO_REMAINING("AddActualToRemaining"),
    @XmlEnumValue("AddedBy")
    ADDED_BY("AddedBy"),
    @XmlEnumValue("AllowNegativeActualUnitsFlag")
    ALLOW_NEGATIVE_ACTUAL_UNITS_FLAG("AllowNegativeActualUnitsFlag"),
    @XmlEnumValue("AllowStatusReview")
    ALLOW_STATUS_REVIEW("AllowStatusReview"),
    @XmlEnumValue("AnnualDiscountRate")
    ANNUAL_DISCOUNT_RATE("AnnualDiscountRate"),
    @XmlEnumValue("AnticipatedFinishDate")
    ANTICIPATED_FINISH_DATE("AnticipatedFinishDate"),
    @XmlEnumValue("AnticipatedStartDate")
    ANTICIPATED_START_DATE("AnticipatedStartDate"),
    @XmlEnumValue("AssignmentDefaultDrivingFlag")
    ASSIGNMENT_DEFAULT_DRIVING_FLAG("AssignmentDefaultDrivingFlag"),
    @XmlEnumValue("AssignmentDefaultRateType")
    ASSIGNMENT_DEFAULT_RATE_TYPE("AssignmentDefaultRateType"),
    @XmlEnumValue("CalculateFloatBasedOnFinishDate")
    CALCULATE_FLOAT_BASED_ON_FINISH_DATE("CalculateFloatBasedOnFinishDate"),
    @XmlEnumValue("CheckOutDate")
    CHECK_OUT_DATE("CheckOutDate"),
    @XmlEnumValue("CheckOutStatus")
    CHECK_OUT_STATUS("CheckOutStatus"),
    @XmlEnumValue("CheckOutUserObjectId")
    CHECK_OUT_USER_OBJECT_ID("CheckOutUserObjectId"),
    @XmlEnumValue("ComputeTotalFloatType")
    COMPUTE_TOTAL_FLOAT_TYPE("ComputeTotalFloatType"),
    @XmlEnumValue("ContainsSummaryData")
    CONTAINS_SUMMARY_DATA("ContainsSummaryData"),
    @XmlEnumValue("ContractManagementGroupName")
    CONTRACT_MANAGEMENT_GROUP_NAME("ContractManagementGroupName"),
    @XmlEnumValue("ContractManagementProjectName")
    CONTRACT_MANAGEMENT_PROJECT_NAME("ContractManagementProjectName"),
    @XmlEnumValue("CostQuantityRecalculateFlag")
    COST_QUANTITY_RECALCULATE_FLAG("CostQuantityRecalculateFlag"),
    @XmlEnumValue("CreateDate")
    CREATE_DATE("CreateDate"),
    @XmlEnumValue("CreateUser")
    CREATE_USER("CreateUser"),
    @XmlEnumValue("CriticalActivityFloatLimit")
    CRITICAL_ACTIVITY_FLOAT_LIMIT("CriticalActivityFloatLimit"),
    @XmlEnumValue("CriticalActivityFloatThreshold")
    CRITICAL_ACTIVITY_FLOAT_THRESHOLD("CriticalActivityFloatThreshold"),
    @XmlEnumValue("CriticalActivityPathType")
    CRITICAL_ACTIVITY_PATH_TYPE("CriticalActivityPathType"),
    @XmlEnumValue("CriticalFloatThreshold")
    CRITICAL_FLOAT_THRESHOLD("CriticalFloatThreshold"),
    @XmlEnumValue("CurrentBaselineProjectObjectId")
    CURRENT_BASELINE_PROJECT_OBJECT_ID("CurrentBaselineProjectObjectId"),
    @XmlEnumValue("CurrentBudget")
    CURRENT_BUDGET("CurrentBudget"),
    @XmlEnumValue("CurrentVariance")
    CURRENT_VARIANCE("CurrentVariance"),
    @XmlEnumValue("DataDate")
    DATA_DATE("DataDate"),
    @XmlEnumValue("DateAdded")
    DATE_ADDED("DateAdded"),
    @XmlEnumValue("DefaultPriceTimeUnits")
    DEFAULT_PRICE_TIME_UNITS("DefaultPriceTimeUnits"),
    @XmlEnumValue("Description")
    DESCRIPTION("Description"),
    @XmlEnumValue("DiscountApplicationPeriod")
    DISCOUNT_APPLICATION_PERIOD("DiscountApplicationPeriod"),
    @XmlEnumValue("DistributedCurrentBudget")
    DISTRIBUTED_CURRENT_BUDGET("DistributedCurrentBudget"),
    @XmlEnumValue("EarnedValueComputeType")
    EARNED_VALUE_COMPUTE_TYPE("EarnedValueComputeType"),
    @XmlEnumValue("EarnedValueETCComputeType")
    EARNED_VALUE_ETC_COMPUTE_TYPE("EarnedValueETCComputeType"),
    @XmlEnumValue("EarnedValueETCUserValue")
    EARNED_VALUE_ETC_USER_VALUE("EarnedValueETCUserValue"),
    @XmlEnumValue("EarnedValueUserPercent")
    EARNED_VALUE_USER_PERCENT("EarnedValueUserPercent"),
    @XmlEnumValue("EnablePrimeSycFlag")
    ENABLE_PRIME_SYC_FLAG("EnablePrimeSycFlag"),
    @XmlEnumValue("EnablePublication")
    ENABLE_PUBLICATION("EnablePublication"),
    @XmlEnumValue("EnableSummarization")
    ENABLE_SUMMARIZATION("EnableSummarization"),
    @XmlEnumValue("EtlInterval")
    ETL_INTERVAL("EtlInterval"),
    @XmlEnumValue("FinishDate")
    FINISH_DATE("FinishDate"),
    @XmlEnumValue("FiscalYearStartMonth")
    FISCAL_YEAR_START_MONTH("FiscalYearStartMonth"),
    @XmlEnumValue("ForecastFinishDate")
    FORECAST_FINISH_DATE("ForecastFinishDate"),
    @XmlEnumValue("ForecastStartDate")
    FORECAST_START_DATE("ForecastStartDate"),
    GUID("GUID"),
    @XmlEnumValue("HasFutureBucketData")
    HAS_FUTURE_BUCKET_DATA("HasFutureBucketData"),
    @XmlEnumValue("HistoryInterval")
    HISTORY_INTERVAL("HistoryInterval"),
    @XmlEnumValue("HistoryLevel")
    HISTORY_LEVEL("HistoryLevel"),
    @XmlEnumValue("Id")
    ID("Id"),
    @XmlEnumValue("IgnoreOtherProjectRelationships")
    IGNORE_OTHER_PROJECT_RELATIONSHIPS("IgnoreOtherProjectRelationships"),
    @XmlEnumValue("IndependentETCLaborUnits")
    INDEPENDENT_ETC_LABOR_UNITS("IndependentETCLaborUnits"),
    @XmlEnumValue("IndependentETCTotalCost")
    INDEPENDENT_ETC_TOTAL_COST("IndependentETCTotalCost"),
    @XmlEnumValue("IntegratedType")
    INTEGRATED_TYPE("IntegratedType"),
    @XmlEnumValue("IsTemplate")
    IS_TEMPLATE("IsTemplate"),
    @XmlEnumValue("LastApplyActualsDate")
    LAST_APPLY_ACTUALS_DATE("LastApplyActualsDate"),
    @XmlEnumValue("LastFinancialPeriodObjectId")
    LAST_FINANCIAL_PERIOD_OBJECT_ID("LastFinancialPeriodObjectId"),
    @XmlEnumValue("LastLevelDate")
    LAST_LEVEL_DATE("LastLevelDate"),
    @XmlEnumValue("LastPublishedOn")
    LAST_PUBLISHED_ON("LastPublishedOn"),
    @XmlEnumValue("LastScheduleDate")
    LAST_SCHEDULE_DATE("LastScheduleDate"),
    @XmlEnumValue("LastSummarizedDate")
    LAST_SUMMARIZED_DATE("LastSummarizedDate"),
    @XmlEnumValue("LastUpdateDate")
    LAST_UPDATE_DATE("LastUpdateDate"),
    @XmlEnumValue("LastUpdateUser")
    LAST_UPDATE_USER("LastUpdateUser"),
    @XmlEnumValue("Latitude")
    LATITUDE("Latitude"),
    @XmlEnumValue("LevelAllResources")
    LEVEL_ALL_RESOURCES("LevelAllResources"),
    @XmlEnumValue("LevelDateFlag")
    LEVEL_DATE_FLAG("LevelDateFlag"),
    @XmlEnumValue("LevelFloatThresholdCount")
    LEVEL_FLOAT_THRESHOLD_COUNT("LevelFloatThresholdCount"),
    @XmlEnumValue("LevelOuterAssign")
    LEVEL_OUTER_ASSIGN("LevelOuterAssign"),
    @XmlEnumValue("LevelOuterAssignPriority")
    LEVEL_OUTER_ASSIGN_PRIORITY("LevelOuterAssignPriority"),
    @XmlEnumValue("LevelOverAllocationPercent")
    LEVEL_OVER_ALLOCATION_PERCENT("LevelOverAllocationPercent"),
    @XmlEnumValue("LevelPriorityList")
    LEVEL_PRIORITY_LIST("LevelPriorityList"),
    @XmlEnumValue("LevelResourceList")
    LEVEL_RESOURCE_LIST("LevelResourceList"),
    @XmlEnumValue("LevelWithinFloat")
    LEVEL_WITHIN_FLOAT("LevelWithinFloat"),
    @XmlEnumValue("LevelingPriority")
    LEVELING_PRIORITY("LevelingPriority"),
    @XmlEnumValue("LimitMultipleFloatPaths")
    LIMIT_MULTIPLE_FLOAT_PATHS("LimitMultipleFloatPaths"),
    @XmlEnumValue("LinkActualToActualThisPeriod")
    LINK_ACTUAL_TO_ACTUAL_THIS_PERIOD("LinkActualToActualThisPeriod"),
    @XmlEnumValue("LinkPercentCompleteWithActual")
    LINK_PERCENT_COMPLETE_WITH_ACTUAL("LinkPercentCompleteWithActual"),
    @XmlEnumValue("LinkPlannedAndAtCompletionFlag")
    LINK_PLANNED_AND_AT_COMPLETION_FLAG("LinkPlannedAndAtCompletionFlag"),
    @XmlEnumValue("LocationName")
    LOCATION_NAME("LocationName"),
    @XmlEnumValue("LocationObjectId")
    LOCATION_OBJECT_ID("LocationObjectId"),
    @XmlEnumValue("Longitude")
    LONGITUDE("Longitude"),
    @XmlEnumValue("MakeOpenEndedActivitiesCritical")
    MAKE_OPEN_ENDED_ACTIVITIES_CRITICAL("MakeOpenEndedActivitiesCritical"),
    @XmlEnumValue("MaximumMultipleFloatPaths")
    MAXIMUM_MULTIPLE_FLOAT_PATHS("MaximumMultipleFloatPaths"),
    @XmlEnumValue("MultipleFloatPathsEnabled")
    MULTIPLE_FLOAT_PATHS_ENABLED("MultipleFloatPathsEnabled"),
    @XmlEnumValue("MultipleFloatPathsEndingActivityObjectId")
    MULTIPLE_FLOAT_PATHS_ENDING_ACTIVITY_OBJECT_ID("MultipleFloatPathsEndingActivityObjectId"),
    @XmlEnumValue("MultipleFloatPathsUseTotalFloat")
    MULTIPLE_FLOAT_PATHS_USE_TOTAL_FLOAT("MultipleFloatPathsUseTotalFloat"),
    @XmlEnumValue("MustFinishByDate")
    MUST_FINISH_BY_DATE("MustFinishByDate"),
    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("NetPresentValue")
    NET_PRESENT_VALUE("NetPresentValue"),
    @XmlEnumValue("OBSName")
    OBS_NAME("OBSName"),
    @XmlEnumValue("OBSObjectId")
    OBS_OBJECT_ID("OBSObjectId"),
    @XmlEnumValue("ObjectId")
    OBJECT_ID("ObjectId"),
    @XmlEnumValue("OriginalBudget")
    ORIGINAL_BUDGET("OriginalBudget"),
    @XmlEnumValue("OutOfSequenceScheduleType")
    OUT_OF_SEQUENCE_SCHEDULE_TYPE("OutOfSequenceScheduleType"),
    @XmlEnumValue("OverallProjectScore")
    OVERALL_PROJECT_SCORE("OverallProjectScore"),
    @XmlEnumValue("OwnerResourceObjectId")
    OWNER_RESOURCE_OBJECT_ID("OwnerResourceObjectId"),
    @XmlEnumValue("ParentEPSObjectId")
    PARENT_EPS_OBJECT_ID("ParentEPSObjectId"),
    @XmlEnumValue("PaybackPeriod")
    PAYBACK_PERIOD("PaybackPeriod"),
    @XmlEnumValue("PlannedStartDate")
    PLANNED_START_DATE("PlannedStartDate"),
    @XmlEnumValue("PrimaryResourcesCanMarkActivitiesAsCompleted")
    PRIMARY_RESOURCES_CAN_MARK_ACTIVITIES_AS_COMPLETED("PrimaryResourcesCanMarkActivitiesAsCompleted"),
    @XmlEnumValue("ProjectForecastStartDate")
    PROJECT_FORECAST_START_DATE("ProjectForecastStartDate"),
    @XmlEnumValue("ProjectScheduleType")
    PROJECT_SCHEDULE_TYPE("ProjectScheduleType"),
    @XmlEnumValue("PropertyType")
    PROPERTY_TYPE("PropertyType"),
    @XmlEnumValue("ProposedBudget")
    PROPOSED_BUDGET("ProposedBudget"),
    @XmlEnumValue("PublicationPriority")
    PUBLICATION_PRIORITY("PublicationPriority"),
    @XmlEnumValue("PublishLevel")
    PUBLISH_LEVEL("PublishLevel"),
    @XmlEnumValue("RelationshipLagCalendar")
    RELATIONSHIP_LAG_CALENDAR("RelationshipLagCalendar"),
    @XmlEnumValue("ResetPlannedToRemainingFlag")
    RESET_PLANNED_TO_REMAINING_FLAG("ResetPlannedToRemainingFlag"),
    @XmlEnumValue("ResourceCanBeAssignedToSameActivityMoreThanOnce")
    RESOURCE_CAN_BE_ASSIGNED_TO_SAME_ACTIVITY_MORE_THAN_ONCE("ResourceCanBeAssignedToSameActivityMoreThanOnce"),
    @XmlEnumValue("ResourcesCanAssignThemselvesToActivities")
    RESOURCES_CAN_ASSIGN_THEMSELVES_TO_ACTIVITIES("ResourcesCanAssignThemselvesToActivities"),
    @XmlEnumValue("ResourcesCanEditAssignmentPercentComplete")
    RESOURCES_CAN_EDIT_ASSIGNMENT_PERCENT_COMPLETE("ResourcesCanEditAssignmentPercentComplete"),
    @XmlEnumValue("ResourcesCanMarkAssignmentAsCompleted")
    RESOURCES_CAN_MARK_ASSIGNMENT_AS_COMPLETED("ResourcesCanMarkAssignmentAsCompleted"),
    @XmlEnumValue("ResourcesCanViewInactiveActivities")
    RESOURCES_CAN_VIEW_INACTIVE_ACTIVITIES("ResourcesCanViewInactiveActivities"),
    @XmlEnumValue("ReturnOnInvestment")
    RETURN_ON_INVESTMENT("ReturnOnInvestment"),
    @XmlEnumValue("RiskExposure")
    RISK_EXPOSURE("RiskExposure"),
    @XmlEnumValue("RiskLevel")
    RISK_LEVEL("RiskLevel"),
    @XmlEnumValue("RiskMatrixName")
    RISK_MATRIX_NAME("RiskMatrixName"),
    @XmlEnumValue("RiskMatrixObjectId")
    RISK_MATRIX_OBJECT_ID("RiskMatrixObjectId"),
    @XmlEnumValue("RiskScore")
    RISK_SCORE("RiskScore"),
    @XmlEnumValue("ScheduleWBSHierarchyType")
    SCHEDULE_WBS_HIERARCHY_TYPE("ScheduleWBSHierarchyType"),
    @XmlEnumValue("ScheduledFinishDate")
    SCHEDULED_FINISH_DATE("ScheduledFinishDate"),
    @XmlEnumValue("SourceProjectObjectId")
    SOURCE_PROJECT_OBJECT_ID("SourceProjectObjectId"),
    @XmlEnumValue("StartDate")
    START_DATE("StartDate"),
    @XmlEnumValue("StartToStartLagCalculationType")
    START_TO_START_LAG_CALCULATION_TYPE("StartToStartLagCalculationType"),
    @XmlEnumValue("Status")
    STATUS("Status"),
    @XmlEnumValue("StatusReviewerName")
    STATUS_REVIEWER_NAME("StatusReviewerName"),
    @XmlEnumValue("StatusReviewerObjectId")
    STATUS_REVIEWER_OBJECT_ID("StatusReviewerObjectId"),
    @XmlEnumValue("StrategicPriority")
    STRATEGIC_PRIORITY("StrategicPriority"),
    @XmlEnumValue("SummarizeResourcesRolesByWBS")
    SUMMARIZE_RESOURCES_ROLES_BY_WBS("SummarizeResourcesRolesByWBS"),
    @XmlEnumValue("SummarizeToWBSLevel")
    SUMMARIZE_TO_WBS_LEVEL("SummarizeToWBSLevel"),
    @XmlEnumValue("SummarizedDataDate")
    SUMMARIZED_DATA_DATE("SummarizedDataDate"),
    @XmlEnumValue("SummaryAccountingVarianceByCost")
    SUMMARY_ACCOUNTING_VARIANCE_BY_COST("SummaryAccountingVarianceByCost"),
    @XmlEnumValue("SummaryAccountingVarianceByLaborUnits")
    SUMMARY_ACCOUNTING_VARIANCE_BY_LABOR_UNITS("SummaryAccountingVarianceByLaborUnits"),
    @XmlEnumValue("SummaryActivityCount")
    SUMMARY_ACTIVITY_COUNT("SummaryActivityCount"),
    @XmlEnumValue("SummaryActualDuration")
    SUMMARY_ACTUAL_DURATION("SummaryActualDuration"),
    @XmlEnumValue("SummaryActualExpenseCost")
    SUMMARY_ACTUAL_EXPENSE_COST("SummaryActualExpenseCost"),
    @XmlEnumValue("SummaryActualFinishDate")
    SUMMARY_ACTUAL_FINISH_DATE("SummaryActualFinishDate"),
    @XmlEnumValue("SummaryActualLaborCost")
    SUMMARY_ACTUAL_LABOR_COST("SummaryActualLaborCost"),
    @XmlEnumValue("SummaryActualLaborUnits")
    SUMMARY_ACTUAL_LABOR_UNITS("SummaryActualLaborUnits"),
    @XmlEnumValue("SummaryActualMaterialCost")
    SUMMARY_ACTUAL_MATERIAL_COST("SummaryActualMaterialCost"),
    @XmlEnumValue("SummaryActualNonLaborCost")
    SUMMARY_ACTUAL_NON_LABOR_COST("SummaryActualNonLaborCost"),
    @XmlEnumValue("SummaryActualNonLaborUnits")
    SUMMARY_ACTUAL_NON_LABOR_UNITS("SummaryActualNonLaborUnits"),
    @XmlEnumValue("SummaryActualStartDate")
    SUMMARY_ACTUAL_START_DATE("SummaryActualStartDate"),
    @XmlEnumValue("SummaryActualThisPeriodCost")
    SUMMARY_ACTUAL_THIS_PERIOD_COST("SummaryActualThisPeriodCost"),
    @XmlEnumValue("SummaryActualThisPeriodLaborCost")
    SUMMARY_ACTUAL_THIS_PERIOD_LABOR_COST("SummaryActualThisPeriodLaborCost"),
    @XmlEnumValue("SummaryActualThisPeriodLaborUnits")
    SUMMARY_ACTUAL_THIS_PERIOD_LABOR_UNITS("SummaryActualThisPeriodLaborUnits"),
    @XmlEnumValue("SummaryActualThisPeriodMaterialCost")
    SUMMARY_ACTUAL_THIS_PERIOD_MATERIAL_COST("SummaryActualThisPeriodMaterialCost"),
    @XmlEnumValue("SummaryActualThisPeriodNonLaborCost")
    SUMMARY_ACTUAL_THIS_PERIOD_NON_LABOR_COST("SummaryActualThisPeriodNonLaborCost"),
    @XmlEnumValue("SummaryActualThisPeriodNonLaborUnits")
    SUMMARY_ACTUAL_THIS_PERIOD_NON_LABOR_UNITS("SummaryActualThisPeriodNonLaborUnits"),
    @XmlEnumValue("SummaryActualTotalCost")
    SUMMARY_ACTUAL_TOTAL_COST("SummaryActualTotalCost"),
    @XmlEnumValue("SummaryActualValueByCost")
    SUMMARY_ACTUAL_VALUE_BY_COST("SummaryActualValueByCost"),
    @XmlEnumValue("SummaryActualValueByLaborUnits")
    SUMMARY_ACTUAL_VALUE_BY_LABOR_UNITS("SummaryActualValueByLaborUnits"),
    @XmlEnumValue("SummaryAtCompletionDuration")
    SUMMARY_AT_COMPLETION_DURATION("SummaryAtCompletionDuration"),
    @XmlEnumValue("SummaryAtCompletionExpenseCost")
    SUMMARY_AT_COMPLETION_EXPENSE_COST("SummaryAtCompletionExpenseCost"),
    @XmlEnumValue("SummaryAtCompletionLaborCost")
    SUMMARY_AT_COMPLETION_LABOR_COST("SummaryAtCompletionLaborCost"),
    @XmlEnumValue("SummaryAtCompletionLaborUnits")
    SUMMARY_AT_COMPLETION_LABOR_UNITS("SummaryAtCompletionLaborUnits"),
    @XmlEnumValue("SummaryAtCompletionMaterialCost")
    SUMMARY_AT_COMPLETION_MATERIAL_COST("SummaryAtCompletionMaterialCost"),
    @XmlEnumValue("SummaryAtCompletionNonLaborCost")
    SUMMARY_AT_COMPLETION_NON_LABOR_COST("SummaryAtCompletionNonLaborCost"),
    @XmlEnumValue("SummaryAtCompletionNonLaborUnits")
    SUMMARY_AT_COMPLETION_NON_LABOR_UNITS("SummaryAtCompletionNonLaborUnits"),
    @XmlEnumValue("SummaryAtCompletionTotalCost")
    SUMMARY_AT_COMPLETION_TOTAL_COST("SummaryAtCompletionTotalCost"),
    @XmlEnumValue("SummaryAtCompletionTotalCostVariance")
    SUMMARY_AT_COMPLETION_TOTAL_COST_VARIANCE("SummaryAtCompletionTotalCostVariance"),
    @XmlEnumValue("SummaryBaselineCompletedActivityCount")
    SUMMARY_BASELINE_COMPLETED_ACTIVITY_COUNT("SummaryBaselineCompletedActivityCount"),
    @XmlEnumValue("SummaryBaselineDuration")
    SUMMARY_BASELINE_DURATION("SummaryBaselineDuration"),
    @XmlEnumValue("SummaryBaselineExpenseCost")
    SUMMARY_BASELINE_EXPENSE_COST("SummaryBaselineExpenseCost"),
    @XmlEnumValue("SummaryBaselineFinishDate")
    SUMMARY_BASELINE_FINISH_DATE("SummaryBaselineFinishDate"),
    @XmlEnumValue("SummaryBaselineInProgressActivityCount")
    SUMMARY_BASELINE_IN_PROGRESS_ACTIVITY_COUNT("SummaryBaselineInProgressActivityCount"),
    @XmlEnumValue("SummaryBaselineLaborCost")
    SUMMARY_BASELINE_LABOR_COST("SummaryBaselineLaborCost"),
    @XmlEnumValue("SummaryBaselineLaborUnits")
    SUMMARY_BASELINE_LABOR_UNITS("SummaryBaselineLaborUnits"),
    @XmlEnumValue("SummaryBaselineMaterialCost")
    SUMMARY_BASELINE_MATERIAL_COST("SummaryBaselineMaterialCost"),
    @XmlEnumValue("SummaryBaselineNonLaborCost")
    SUMMARY_BASELINE_NON_LABOR_COST("SummaryBaselineNonLaborCost"),
    @XmlEnumValue("SummaryBaselineNonLaborUnits")
    SUMMARY_BASELINE_NON_LABOR_UNITS("SummaryBaselineNonLaborUnits"),
    @XmlEnumValue("SummaryBaselineNotStartedActivityCount")
    SUMMARY_BASELINE_NOT_STARTED_ACTIVITY_COUNT("SummaryBaselineNotStartedActivityCount"),
    @XmlEnumValue("SummaryBaselineStartDate")
    SUMMARY_BASELINE_START_DATE("SummaryBaselineStartDate"),
    @XmlEnumValue("SummaryBaselineTotalCost")
    SUMMARY_BASELINE_TOTAL_COST("SummaryBaselineTotalCost"),
    @XmlEnumValue("SummaryBudgetAtCompletionByCost")
    SUMMARY_BUDGET_AT_COMPLETION_BY_COST("SummaryBudgetAtCompletionByCost"),
    @XmlEnumValue("SummaryBudgetAtCompletionByLaborUnits")
    SUMMARY_BUDGET_AT_COMPLETION_BY_LABOR_UNITS("SummaryBudgetAtCompletionByLaborUnits"),
    @XmlEnumValue("SummaryCompletedActivityCount")
    SUMMARY_COMPLETED_ACTIVITY_COUNT("SummaryCompletedActivityCount"),
    @XmlEnumValue("SummaryCostPercentComplete")
    SUMMARY_COST_PERCENT_COMPLETE("SummaryCostPercentComplete"),
    @XmlEnumValue("SummaryCostPercentOfPlanned")
    SUMMARY_COST_PERCENT_OF_PLANNED("SummaryCostPercentOfPlanned"),
    @XmlEnumValue("SummaryCostPerformanceIndexByCost")
    SUMMARY_COST_PERFORMANCE_INDEX_BY_COST("SummaryCostPerformanceIndexByCost"),
    @XmlEnumValue("SummaryCostPerformanceIndexByLaborUnits")
    SUMMARY_COST_PERFORMANCE_INDEX_BY_LABOR_UNITS("SummaryCostPerformanceIndexByLaborUnits"),
    @XmlEnumValue("SummaryCostVarianceByCost")
    SUMMARY_COST_VARIANCE_BY_COST("SummaryCostVarianceByCost"),
    @XmlEnumValue("SummaryCostVarianceByLaborUnits")
    SUMMARY_COST_VARIANCE_BY_LABOR_UNITS("SummaryCostVarianceByLaborUnits"),
    @XmlEnumValue("SummaryCostVarianceIndex")
    SUMMARY_COST_VARIANCE_INDEX("SummaryCostVarianceIndex"),
    @XmlEnumValue("SummaryCostVarianceIndexByCost")
    SUMMARY_COST_VARIANCE_INDEX_BY_COST("SummaryCostVarianceIndexByCost"),
    @XmlEnumValue("SummaryCostVarianceIndexByLaborUnits")
    SUMMARY_COST_VARIANCE_INDEX_BY_LABOR_UNITS("SummaryCostVarianceIndexByLaborUnits"),
    @XmlEnumValue("SummaryDurationPercentComplete")
    SUMMARY_DURATION_PERCENT_COMPLETE("SummaryDurationPercentComplete"),
    @XmlEnumValue("SummaryDurationPercentOfPlanned")
    SUMMARY_DURATION_PERCENT_OF_PLANNED("SummaryDurationPercentOfPlanned"),
    @XmlEnumValue("SummaryDurationVariance")
    SUMMARY_DURATION_VARIANCE("SummaryDurationVariance"),
    @XmlEnumValue("SummaryEarnedValueByCost")
    SUMMARY_EARNED_VALUE_BY_COST("SummaryEarnedValueByCost"),
    @XmlEnumValue("SummaryEarnedValueByLaborUnits")
    SUMMARY_EARNED_VALUE_BY_LABOR_UNITS("SummaryEarnedValueByLaborUnits"),
    @XmlEnumValue("SummaryEstimateAtCompletionByCost")
    SUMMARY_ESTIMATE_AT_COMPLETION_BY_COST("SummaryEstimateAtCompletionByCost"),
    @XmlEnumValue("SummaryEstimateAtCompletionByLaborUnits")
    SUMMARY_ESTIMATE_AT_COMPLETION_BY_LABOR_UNITS("SummaryEstimateAtCompletionByLaborUnits"),
    @XmlEnumValue("SummaryEstimateAtCompletionHighPercentByLaborUnits")
    SUMMARY_ESTIMATE_AT_COMPLETION_HIGH_PERCENT_BY_LABOR_UNITS("SummaryEstimateAtCompletionHighPercentByLaborUnits"),
    @XmlEnumValue("SummaryEstimateAtCompletionLowPercentByLaborUnits")
    SUMMARY_ESTIMATE_AT_COMPLETION_LOW_PERCENT_BY_LABOR_UNITS("SummaryEstimateAtCompletionLowPercentByLaborUnits"),
    @XmlEnumValue("SummaryEstimateToCompleteByCost")
    SUMMARY_ESTIMATE_TO_COMPLETE_BY_COST("SummaryEstimateToCompleteByCost"),
    @XmlEnumValue("SummaryEstimateToCompleteByLaborUnits")
    SUMMARY_ESTIMATE_TO_COMPLETE_BY_LABOR_UNITS("SummaryEstimateToCompleteByLaborUnits"),
    @XmlEnumValue("SummaryExpenseCostPercentComplete")
    SUMMARY_EXPENSE_COST_PERCENT_COMPLETE("SummaryExpenseCostPercentComplete"),
    @XmlEnumValue("SummaryExpenseCostVariance")
    SUMMARY_EXPENSE_COST_VARIANCE("SummaryExpenseCostVariance"),
    @XmlEnumValue("SummaryFinishDateVariance")
    SUMMARY_FINISH_DATE_VARIANCE("SummaryFinishDateVariance"),
    @XmlEnumValue("SummaryInProgressActivityCount")
    SUMMARY_IN_PROGRESS_ACTIVITY_COUNT("SummaryInProgressActivityCount"),
    @XmlEnumValue("SummaryLaborCostPercentComplete")
    SUMMARY_LABOR_COST_PERCENT_COMPLETE("SummaryLaborCostPercentComplete"),
    @XmlEnumValue("SummaryLaborCostVariance")
    SUMMARY_LABOR_COST_VARIANCE("SummaryLaborCostVariance"),
    @XmlEnumValue("SummaryLaborUnitsPercentComplete")
    SUMMARY_LABOR_UNITS_PERCENT_COMPLETE("SummaryLaborUnitsPercentComplete"),
    @XmlEnumValue("SummaryLaborUnitsVariance")
    SUMMARY_LABOR_UNITS_VARIANCE("SummaryLaborUnitsVariance"),
    @XmlEnumValue("SummaryLevel")
    SUMMARY_LEVEL("SummaryLevel"),
    @XmlEnumValue("SummaryMaterialCostPercentComplete")
    SUMMARY_MATERIAL_COST_PERCENT_COMPLETE("SummaryMaterialCostPercentComplete"),
    @XmlEnumValue("SummaryMaterialCostVariance")
    SUMMARY_MATERIAL_COST_VARIANCE("SummaryMaterialCostVariance"),
    @XmlEnumValue("SummaryNonLaborCostPercentComplete")
    SUMMARY_NON_LABOR_COST_PERCENT_COMPLETE("SummaryNonLaborCostPercentComplete"),
    @XmlEnumValue("SummaryNonLaborCostVariance")
    SUMMARY_NON_LABOR_COST_VARIANCE("SummaryNonLaborCostVariance"),
    @XmlEnumValue("SummaryNonLaborUnitsPercentComplete")
    SUMMARY_NON_LABOR_UNITS_PERCENT_COMPLETE("SummaryNonLaborUnitsPercentComplete"),
    @XmlEnumValue("SummaryNonLaborUnitsVariance")
    SUMMARY_NON_LABOR_UNITS_VARIANCE("SummaryNonLaborUnitsVariance"),
    @XmlEnumValue("SummaryNotStartedActivityCount")
    SUMMARY_NOT_STARTED_ACTIVITY_COUNT("SummaryNotStartedActivityCount"),
    @XmlEnumValue("SummaryPerformancePercentCompleteByCost")
    SUMMARY_PERFORMANCE_PERCENT_COMPLETE_BY_COST("SummaryPerformancePercentCompleteByCost"),
    @XmlEnumValue("SummaryPerformancePercentCompleteByLaborUnits")
    SUMMARY_PERFORMANCE_PERCENT_COMPLETE_BY_LABOR_UNITS("SummaryPerformancePercentCompleteByLaborUnits"),
    @XmlEnumValue("SummaryPlannedCost")
    SUMMARY_PLANNED_COST("SummaryPlannedCost"),
    @XmlEnumValue("SummaryPlannedDuration")
    SUMMARY_PLANNED_DURATION("SummaryPlannedDuration"),
    @XmlEnumValue("SummaryPlannedExpenseCost")
    SUMMARY_PLANNED_EXPENSE_COST("SummaryPlannedExpenseCost"),
    @XmlEnumValue("SummaryPlannedFinishDate")
    SUMMARY_PLANNED_FINISH_DATE("SummaryPlannedFinishDate"),
    @XmlEnumValue("SummaryPlannedLaborCost")
    SUMMARY_PLANNED_LABOR_COST("SummaryPlannedLaborCost"),
    @XmlEnumValue("SummaryPlannedLaborUnits")
    SUMMARY_PLANNED_LABOR_UNITS("SummaryPlannedLaborUnits"),
    @XmlEnumValue("SummaryPlannedMaterialCost")
    SUMMARY_PLANNED_MATERIAL_COST("SummaryPlannedMaterialCost"),
    @XmlEnumValue("SummaryPlannedNonLaborCost")
    SUMMARY_PLANNED_NON_LABOR_COST("SummaryPlannedNonLaborCost"),
    @XmlEnumValue("SummaryPlannedNonLaborUnits")
    SUMMARY_PLANNED_NON_LABOR_UNITS("SummaryPlannedNonLaborUnits"),
    @XmlEnumValue("SummaryPlannedStartDate")
    SUMMARY_PLANNED_START_DATE("SummaryPlannedStartDate"),
    @XmlEnumValue("SummaryPlannedValueByCost")
    SUMMARY_PLANNED_VALUE_BY_COST("SummaryPlannedValueByCost"),
    @XmlEnumValue("SummaryPlannedValueByLaborUnits")
    SUMMARY_PLANNED_VALUE_BY_LABOR_UNITS("SummaryPlannedValueByLaborUnits"),
    @XmlEnumValue("SummaryProgressFinishDate")
    SUMMARY_PROGRESS_FINISH_DATE("SummaryProgressFinishDate"),
    @XmlEnumValue("SummaryRemainingDuration")
    SUMMARY_REMAINING_DURATION("SummaryRemainingDuration"),
    @XmlEnumValue("SummaryRemainingExpenseCost")
    SUMMARY_REMAINING_EXPENSE_COST("SummaryRemainingExpenseCost"),
    @XmlEnumValue("SummaryRemainingFinishDate")
    SUMMARY_REMAINING_FINISH_DATE("SummaryRemainingFinishDate"),
    @XmlEnumValue("SummaryRemainingLaborCost")
    SUMMARY_REMAINING_LABOR_COST("SummaryRemainingLaborCost"),
    @XmlEnumValue("SummaryRemainingLaborUnits")
    SUMMARY_REMAINING_LABOR_UNITS("SummaryRemainingLaborUnits"),
    @XmlEnumValue("SummaryRemainingMaterialCost")
    SUMMARY_REMAINING_MATERIAL_COST("SummaryRemainingMaterialCost"),
    @XmlEnumValue("SummaryRemainingNonLaborCost")
    SUMMARY_REMAINING_NON_LABOR_COST("SummaryRemainingNonLaborCost"),
    @XmlEnumValue("SummaryRemainingNonLaborUnits")
    SUMMARY_REMAINING_NON_LABOR_UNITS("SummaryRemainingNonLaborUnits"),
    @XmlEnumValue("SummaryRemainingStartDate")
    SUMMARY_REMAINING_START_DATE("SummaryRemainingStartDate"),
    @XmlEnumValue("SummaryRemainingTotalCost")
    SUMMARY_REMAINING_TOTAL_COST("SummaryRemainingTotalCost"),
    @XmlEnumValue("SummarySchedulePercentComplete")
    SUMMARY_SCHEDULE_PERCENT_COMPLETE("SummarySchedulePercentComplete"),
    @XmlEnumValue("SummarySchedulePercentCompleteByLaborUnits")
    SUMMARY_SCHEDULE_PERCENT_COMPLETE_BY_LABOR_UNITS("SummarySchedulePercentCompleteByLaborUnits"),
    @XmlEnumValue("SummarySchedulePerformanceIndexByCost")
    SUMMARY_SCHEDULE_PERFORMANCE_INDEX_BY_COST("SummarySchedulePerformanceIndexByCost"),
    @XmlEnumValue("SummarySchedulePerformanceIndexByLaborUnits")
    SUMMARY_SCHEDULE_PERFORMANCE_INDEX_BY_LABOR_UNITS("SummarySchedulePerformanceIndexByLaborUnits"),
    @XmlEnumValue("SummaryScheduleVarianceByCost")
    SUMMARY_SCHEDULE_VARIANCE_BY_COST("SummaryScheduleVarianceByCost"),
    @XmlEnumValue("SummaryScheduleVarianceByLaborUnits")
    SUMMARY_SCHEDULE_VARIANCE_BY_LABOR_UNITS("SummaryScheduleVarianceByLaborUnits"),
    @XmlEnumValue("SummaryScheduleVarianceIndex")
    SUMMARY_SCHEDULE_VARIANCE_INDEX("SummaryScheduleVarianceIndex"),
    @XmlEnumValue("SummaryScheduleVarianceIndexByCost")
    SUMMARY_SCHEDULE_VARIANCE_INDEX_BY_COST("SummaryScheduleVarianceIndexByCost"),
    @XmlEnumValue("SummaryScheduleVarianceIndexByLaborUnits")
    SUMMARY_SCHEDULE_VARIANCE_INDEX_BY_LABOR_UNITS("SummaryScheduleVarianceIndexByLaborUnits"),
    @XmlEnumValue("SummaryStartDateVariance")
    SUMMARY_START_DATE_VARIANCE("SummaryStartDateVariance"),
    @XmlEnumValue("SummaryToCompletePerformanceIndexByCost")
    SUMMARY_TO_COMPLETE_PERFORMANCE_INDEX_BY_COST("SummaryToCompletePerformanceIndexByCost"),
    @XmlEnumValue("SummaryTotalCostVariance")
    SUMMARY_TOTAL_COST_VARIANCE("SummaryTotalCostVariance"),
    @XmlEnumValue("SummaryTotalFloat")
    SUMMARY_TOTAL_FLOAT("SummaryTotalFloat"),
    @XmlEnumValue("SummaryUnitsPercentComplete")
    SUMMARY_UNITS_PERCENT_COMPLETE("SummaryUnitsPercentComplete"),
    @XmlEnumValue("SummaryVarianceAtCompletionByLaborUnits")
    SUMMARY_VARIANCE_AT_COMPLETION_BY_LABOR_UNITS("SummaryVarianceAtCompletionByLaborUnits"),
    @XmlEnumValue("SyncWbsHierarchyFlag")
    SYNC_WBS_HIERARCHY_FLAG("SyncWbsHierarchyFlag"),
    @XmlEnumValue("TeamMemberActivityFields")
    TEAM_MEMBER_ACTIVITY_FIELDS("TeamMemberActivityFields"),
    @XmlEnumValue("TeamMemberAddNewActualUnits")
    TEAM_MEMBER_ADD_NEW_ACTUAL_UNITS("TeamMemberAddNewActualUnits"),
    @XmlEnumValue("TeamMemberAssignmentOption")
    TEAM_MEMBER_ASSIGNMENT_OPTION("TeamMemberAssignmentOption"),
    @XmlEnumValue("TeamMemberCanStatusOtherResources")
    TEAM_MEMBER_CAN_STATUS_OTHER_RESOURCES("TeamMemberCanStatusOtherResources"),
    @XmlEnumValue("TeamMemberCanUpdateNotebooks")
    TEAM_MEMBER_CAN_UPDATE_NOTEBOOKS("TeamMemberCanUpdateNotebooks"),
    @XmlEnumValue("TeamMemberDisplayPlannedUnits")
    TEAM_MEMBER_DISPLAY_PLANNED_UNITS("TeamMemberDisplayPlannedUnits"),
    @XmlEnumValue("TeamMemberIncludePrimaryResources")
    TEAM_MEMBER_INCLUDE_PRIMARY_RESOURCES("TeamMemberIncludePrimaryResources"),
    @XmlEnumValue("TeamMemberResourceAssignmentFields")
    TEAM_MEMBER_RESOURCE_ASSIGNMENT_FIELDS("TeamMemberResourceAssignmentFields"),
    @XmlEnumValue("TeamMemberStepsAddDeletable")
    TEAM_MEMBER_STEPS_ADD_DELETABLE("TeamMemberStepsAddDeletable"),
    @XmlEnumValue("TeamMemberViewableFields")
    TEAM_MEMBER_VIEWABLE_FIELDS("TeamMemberViewableFields"),
    @XmlEnumValue("TotalBenefitPlan")
    TOTAL_BENEFIT_PLAN("TotalBenefitPlan"),
    @XmlEnumValue("TotalBenefitPlanTally")
    TOTAL_BENEFIT_PLAN_TALLY("TotalBenefitPlanTally"),
    @XmlEnumValue("TotalFunding")
    TOTAL_FUNDING("TotalFunding"),
    @XmlEnumValue("TotalSpendingPlan")
    TOTAL_SPENDING_PLAN("TotalSpendingPlan"),
    @XmlEnumValue("TotalSpendingPlanTally")
    TOTAL_SPENDING_PLAN_TALLY("TotalSpendingPlanTally"),
    @XmlEnumValue("UnallocatedBudget")
    UNALLOCATED_BUDGET("UnallocatedBudget"),
    @XmlEnumValue("UndistributedCurrentVariance")
    UNDISTRIBUTED_CURRENT_VARIANCE("UndistributedCurrentVariance"),
    @XmlEnumValue("UnifierCBSTasksOnlyFlag")
    UNIFIER_CBS_TASKS_ONLY_FLAG("UnifierCBSTasksOnlyFlag"),
    @XmlEnumValue("UnifierDataMappingName")
    UNIFIER_DATA_MAPPING_NAME("UnifierDataMappingName"),
    @XmlEnumValue("UnifierDeleteActivitiesFlag")
    UNIFIER_DELETE_ACTIVITIES_FLAG("UnifierDeleteActivitiesFlag"),
    @XmlEnumValue("UnifierEnabledFlag")
    UNIFIER_ENABLED_FLAG("UnifierEnabledFlag"),
    @XmlEnumValue("UnifierProjectName")
    UNIFIER_PROJECT_NAME("UnifierProjectName"),
    @XmlEnumValue("UnifierProjectNumber")
    UNIFIER_PROJECT_NUMBER("UnifierProjectNumber"),
    @XmlEnumValue("UnifierScheduleSheetName")
    UNIFIER_SCHEDULE_SHEET_NAME("UnifierScheduleSheetName"),
    @XmlEnumValue("UseExpectedFinishDates")
    USE_EXPECTED_FINISH_DATES("UseExpectedFinishDates"),
    @XmlEnumValue("UseProjectBaselineForEarnedValue")
    USE_PROJECT_BASELINE_FOR_EARNED_VALUE("UseProjectBaselineForEarnedValue"),
    @XmlEnumValue("WBSCodeSeparator")
    WBS_CODE_SEPARATOR("WBSCodeSeparator"),
    @XmlEnumValue("WBSHierarchyLevels")
    WBS_HIERARCHY_LEVELS("WBSHierarchyLevels"),
    @XmlEnumValue("WBSMilestonePercentComplete")
    WBS_MILESTONE_PERCENT_COMPLETE("WBSMilestonePercentComplete"),
    @XmlEnumValue("WBSObjectId")
    WBS_OBJECT_ID("WBSObjectId"),
    @XmlEnumValue("WebSiteRootDirectory")
    WEB_SITE_ROOT_DIRECTORY("WebSiteRootDirectory"),
    @XmlEnumValue("WebSiteURL")
    WEB_SITE_URL("WebSiteURL");
    private final String value;

    ProjectFieldType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProjectFieldType fromValue(String v) {
        for (ProjectFieldType c: ProjectFieldType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
